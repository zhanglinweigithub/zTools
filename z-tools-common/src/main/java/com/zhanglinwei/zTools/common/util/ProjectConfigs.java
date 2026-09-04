package com.zhanglinwei.zTools.common.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.zhanglinwei.zTools.common.constant.CharacterPool;
import com.zhanglinwei.zTools.common.enums.SpringConfigProperties;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

import static com.zhanglinwei.zTools.common.constant.StringPool.COMMA;
import static com.zhanglinwei.zTools.common.constant.StringPool.CRLF;
import static com.zhanglinwei.zTools.common.constant.StringPool.DOT;
import static com.zhanglinwei.zTools.common.constant.StringPool.EMPTY;
import static com.zhanglinwei.zTools.common.constant.StringPool.EQUAL;
import static com.zhanglinwei.zTools.common.constant.StringPool.LEFT_SQ_BRACKET;
import static com.zhanglinwei.zTools.common.constant.StringPool.NEWLINE;
import static com.zhanglinwei.zTools.common.constant.StringPool.RIGHT_SQ_BRACKET;

/**
 * 读项目里的 Spring {@code application.*} 与插件 {@code zTools.*} 配置。
 * <p>
 * 查找顺序：yaml → yml → properties；同名文件优先取 {@code src/main/resources} 下的那份。
 * YAML 嵌套 key 会被拍平为 kebab-case 点分路径，以便和 {@link SpringConfigProperties} 对齐。
 */
public final class ProjectConfigs {

    private static final String SPRING_YAML = "application.yaml";
    private static final String SPRING_YML = "application.yml";
    private static final String SPRING_PROPERTIES = "application.properties";

    private static final String ZTOOLS_YAML = "zTools.yaml";
    private static final String ZTOOLS_YML = "zTools.yml";
    private static final String ZTOOLS_PROPERTIES = "zTools.properties";

    /** 工具类，禁止实例化 */
    private ProjectConfigs() {}

    /**
     * 读取 Spring {@code application.*} 中的配置项，转成字符串。
     *
     * @param project 当前项目
     * @param key     配置 key
     * @return 配置值字符串；找不到则为空串
     */
    public static String spring(Project project, SpringConfigProperties key) {
        return asString(property(project, key, SPRING_YAML, SPRING_YML, SPRING_PROPERTIES));
    }

    /**
     * 读取插件 {@code zTools.*} 中的配置项，转成字符串。
     *
     * @param project 当前项目
     * @param key     配置 key
     * @return 配置值字符串；找不到则为空串
     */
    public static String zTools(Project project, SpringConfigProperties key) {
        return asString(property(project, key, ZTOOLS_YAML, ZTOOLS_YML, ZTOOLS_PROPERTIES));
    }

    /**
     * 全局请求前缀：优先 {@code server.servlet.context-path}，否则 {@code spring.mvc.servlet.path}。
     * 用于把 Controller Mapping 拼成完整 URL。
     *
     * @param project 当前项目
     * @return 前缀；项目为空或两项都未配置时为空串
     */
    public static String globalRequestPrefix(Project project) {
        if (project == null) {
            return EMPTY;
        }
        String prefix = spring(project, SpringConfigProperties.SERVER_SERVLET_CONTEXT_PATH);
        if (StringUtils.isBlank(prefix)) {
            prefix = spring(project, SpringConfigProperties.SPRING_MVC_SERVLET_PATH);
        }
        return prefix == null ? EMPTY : prefix;
    }

    /**
     * 先查 yaml/yml，再查 properties。任一环节异常则当作未配置。
     *
     * @param project        当前项目
     * @param key            配置 key
     * @param yamlName       {@code *.yaml} 文件名
     * @param ymlName        {@code *.yml} 文件名
     * @param propertiesName {@code *.properties} 文件名
     * @return 原始配置值，可能是标量或集合
     */
    private static Object property(Project project, SpringConfigProperties key,
                                   String yamlName, String ymlName, String propertiesName) {
        if (project == null || key == null) {
            return null;
        }
        try {
            Object value = flattenYaml(project, yamlName, ymlName).get(key.getValue());
            if (value == null) {
                value = flattenProperties(project, propertiesName).get(key.getValue());
            }
            return value;
        } catch (Exception ignored) {
            return null;
        }
    }

    /**
     * 加载并拍平 YAML：优先 {@code yamlName}，没有再找 {@code ymlName}。
     *
     * @param project  当前项目
     * @param yamlName {@code application.yaml} 一类文件名
     * @param ymlName  {@code application.yml} 一类文件名
     * @return kebab-case 点分 key 的扁平 Map；无文件则为空 Map
     */
    private static Map<String, Object> flattenYaml(Project project, String yamlName, String ymlName) {
        Collection<VirtualFile> files = filesByName(project, yamlName);
        if (CollectionUtils.isEmpty(files)) {
            files = filesByName(project, ymlName);
        }
        VirtualFile file = firstInResources(files);
        if (file == null) {
            return Collections.emptyMap();
        }
        String content = read(file);
        if (StringUtils.isBlank(content)) {
            return Collections.emptyMap();
        }
        Map<String, Object> yamlMap = new Yaml().load(content);
        Map<String, Object> flat = new LinkedHashMap<String, Object>();
        flatten(EMPTY, yamlMap, flat);
        return flat;
    }

    /**
     * 解析 properties：按行拆 {@code key=value}，重复 key 保留第一次出现。
     *
     * @param project        当前项目
     * @param propertiesName 文件名
     * @return 扁平 Map；无文件则为空 Map
     */
    private static Map<String, Object> flattenProperties(Project project, String propertiesName) {
        VirtualFile file = firstInResources(filesByName(project, propertiesName));
        if (file == null) {
            return Collections.emptyMap();
        }
        String content = read(file);
        if (StringUtils.isBlank(content)) {
            return Collections.emptyMap();
        }
        String linebreak = content.contains(CRLF) ? CRLF : NEWLINE;
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        String[] lines = content.split(linebreak);
        for (String line : lines) {
            if (StringUtils.isBlank(line) || !line.contains(EQUAL)) {
                continue;
            }
            String[] pair = line.split(EQUAL, 2);
            if (!result.containsKey(pair[0])) {
                result.put(pair[0], pair[1]);
            }
        }
        return result;
    }

    /**
     * 递归把嵌套 Map 拍平为 {@code a.b.c} 形式。非 Map 叶子写入 {@code result}。
     *
     * @param prefix 已拼好的上级 key
     * @param source 当前节点
     * @param result 扁平结果
     */
    private static void flatten(String prefix, Object source, Map<String, Object> result) {
        if (!(source instanceof Map)) {
            if (source != null && StringUtils.isNotBlank(prefix)) {
                result.put(prefix, source);
            }
            return;
        }
        Map<?, ?> map = (Map<?, ?>) source;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            String kebabKey = toKebab(String.valueOf(entry.getKey()));
            String next = prefix.isEmpty() ? kebabKey : prefix + DOT + kebabKey;
            flatten(next, entry.getValue(), result);
        }
    }

    /**
     * camelCase → kebab-case，例如 {@code contextPath} → {@code context-path}。
     *
     * @param camelCase 原 key
     * @return kebab-case；空白则原样返回
     */
    private static String toKebab(String camelCase) {
        if (StringUtils.isBlank(camelCase)) {
            return camelCase;
        }
        StringBuilder result = new StringBuilder();
        result.append(Character.toLowerCase(camelCase.charAt(0)));
        for (int i = 1; i < camelCase.length(); i++) {
            char current = camelCase.charAt(i);
            if (Character.isUpperCase(current)) {
                result.append(CharacterPool.DASH);
                result.append(Character.toLowerCase(current));
            } else {
                result.append(current);
            }
        }
        return result.toString();
    }

    /**
     * 按文件名在项目范围内查找。
     *
     * @param project 当前项目
     * @param name    文件名
     * @return 匹配到的虚拟文件
     */
    private static Collection<VirtualFile> filesByName(Project project, String name) {
        return new ArrayList<VirtualFile>(
                FilenameIndex.getVirtualFilesByName(name, false, GlobalSearchScope.projectScope(project))
        );
    }

    /**
     * 优先返回 {@code src/main/resources} 下的文件，避免误用 test 资源。
     *
     * @param files 候选文件
     * @return 首选文件；都没有 resources 路径则为 {@code null}
     */
    private static VirtualFile firstInResources(Collection<VirtualFile> files) {
        if (CollectionUtils.isEmpty(files)) {
            return null;
        }
        for (VirtualFile file : files) {
            if (file.getPath().contains("src/main/resources")) {
                return file;
            }
        }
        return null;
    }

    /**
     * 读取虚拟文件内容；IO 失败返回 {@code null}。
     *
     * @param file 虚拟文件
     * @return 文本内容
     */
    private static String read(VirtualFile file) {
        try {
            return new String(file.contentsToByteArray());
        } catch (IOException ignored) {
            return null;
        }
    }

    /**
     * 配置值转字符串。Iterable 会格式化成 {@code [a,b,c]}。
     *
     * @param value 原始值
     * @return 字符串；{@code null} 为空串
     */
    private static String asString(Object value) {
        if (value == null) {
            return EMPTY;
        }
        if (value instanceof Iterable) {
            StringJoiner joiner = new StringJoiner(COMMA, LEFT_SQ_BRACKET, RIGHT_SQ_BRACKET);
            for (Object item : (Iterable<?>) value) {
                joiner.add(String.valueOf(item));
            }
            return joiner.toString();
        }
        return value.toString();
    }
}
