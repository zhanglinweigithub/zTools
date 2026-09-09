package com.zhanglinwei.zTools.common.util;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileVisitor;
import com.zhanglinwei.zTools.common.constant.CharacterPool;
import com.zhanglinwei.zTools.common.enums.SpringConfigProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.model.java.JavaResourceRootType;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
 * 只扫描各模块生产资源目录（{@code src/main/resources}）下的文件，避免对整个工程做文件名索引。
 * 查找顺序：yaml → yml → properties。YAML 嵌套 key 会被拍平为 kebab-case 点分路径，
 * 以便和 {@link SpringConfigProperties} 对齐。
 */
public final class ProjectConfigs {

    private static final String SPRING_YAML = "application.yaml";
    private static final String SPRING_YML = "application.yml";
    private static final String SPRING_PROPERTIES = "application.properties";

    private static final String ZTOOLS_YAML = "zTools.yaml";
    private static final String ZTOOLS_YML = "zTools.yml";
    private static final String ZTOOLS_PROPERTIES = "zTools.properties";

    private static final String SRC_MAIN_RESOURCES = "src/main/resources";
    private static final int RESOURCE_WALK_MAX_DEPTH = 8;
    private static final Set<String> SKIP_DIR_NAMES;

    static {
        Set<String> skip = new HashSet<String>();
        skip.add(".git");
        skip.add(".idea");
        skip.add(".svn");
        skip.add(".hg");
        skip.add(".gradle");
        skip.add("target");
        skip.add("build");
        skip.add("out");
        skip.add("node_modules");
        skip.add("dist");
        skip.add("vendor");
        SKIP_DIR_NAMES = Collections.unmodifiableSet(skip);
    }

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
     * 用于把 Controller Mapping 拼成完整 URL。读取 {@code application.yaml/yml/properties} 中的第一份。
     *
     * @param project 当前项目
     * @return 前缀；项目为空或两项都未配置时为空串
     */
    public static String globalRequestPrefix(Project project) {
        if (project == null) {
            return EMPTY;
        }
        String prefix = SpringRequestPrefixParser.normalize(
                spring(project, SpringConfigProperties.SERVER_SERVLET_CONTEXT_PATH));
        if (StringUtils.isBlank(prefix)) {
            prefix = SpringRequestPrefixParser.normalize(
                    spring(project, SpringConfigProperties.SPRING_MVC_SERVLET_PATH));
        }
        return prefix == null ? EMPTY : prefix;
    }

    /**
     * 扫描打开工程内全部生产 {@code resources} 下的 yaml / yml / properties，收集去重后的请求前缀。
     * <p>
     * 适合一个 IDEA 窗口里放了多个子项目、各有不同 {@code context-path} 的场景。
     * 真实前缀在前，根路径（空串，界面显示 {@code /}）始终在最后。
     *
     * @param project 当前项目
     * @return 去重后的前缀，至少含根路径一项
     */
    public static List<String> globalRequestPrefixes(Project project) {
        if (project == null) {
            return Collections.singletonList(EMPTY);
        }
        List<String> found = new ArrayList<String>();
        for (VirtualFile file : resourceConfigFiles(project)) {
            found.addAll(SpringRequestPrefixParser.allFromContent(file.getName(), read(file)));
        }
        return SpringRequestPrefixParser.ensureRootLast(found);
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
     * 在生产 resources 目录中按文件名查找，不再做全工程 FilenameIndex。
     *
     * @param project 当前项目
     * @param name    文件名
     * @return 匹配到的虚拟文件
     */
    private static Collection<VirtualFile> filesByName(Project project, String name) {
        List<VirtualFile> match = new ArrayList<VirtualFile>();
        for (VirtualFile dir : productionResourceDirectories(project)) {
            findNamed(dir, name, match);
        }
        return match;
    }

    /**
     * 在目录树中查找指定文件名。
     *
     * @param dir  当前目录
     * @param name 文件名
     * @param out  结果
     */
    private static void findNamed(final VirtualFile dir, final String name, final List<VirtualFile> out) {
        if (dir == null || !dir.isValid() || !dir.isDirectory()) {
            return;
        }
        VfsUtilCore.visitChildrenRecursively(dir, new VirtualFileVisitor<Void>() {
            @Override
            public @NotNull Result visitFileEx(@NotNull VirtualFile file) {
                if (skipWalkDirectory(file, dir)) {
                    return SKIP_CHILDREN;
                }
                if (!file.isDirectory() && name.equals(file.getName())) {
                    out.add(file);
                }
                return CONTINUE;
            }
        });
    }

    /**
     * 收集生产 resources 下全部 yaml / yml / properties。
     *
     * @param project 当前项目
     * @return 配置文件列表
     */
    private static List<VirtualFile> resourceConfigFiles(Project project) {
        List<VirtualFile> files = new ArrayList<VirtualFile>();
        for (VirtualFile dir : productionResourceDirectories(project)) {
            collectConfigFiles(dir, files);
        }
        return files;
    }

    /**
     * 收集配置文件。
     *
     * @param dir 当前目录
     * @param out 结果
     */
    private static void collectConfigFiles(final VirtualFile dir, final List<VirtualFile> out) {
        if (dir == null || !dir.isValid() || !dir.isDirectory()) {
            return;
        }
        VfsUtilCore.visitChildrenRecursively(dir, new VirtualFileVisitor<Void>() {
            @Override
            public @NotNull Result visitFileEx(@NotNull VirtualFile file) {
                if (skipWalkDirectory(file, dir)) {
                    return SKIP_CHILDREN;
                }
                if (!file.isDirectory() && SpringRequestPrefixParser.isConfigFile(file.getName())) {
                    out.add(file);
                }
                return CONTINUE;
            }
        });
    }

    /**
     * 生产资源目录：Java Resource Root，以及内容根下的 {@code src/main/resources}（含未导入的子项目）。
     *
     * @param project 当前项目
     * @return 去重后的资源目录
     */
    private static List<VirtualFile> productionResourceDirectories(Project project) {
        Map<String, VirtualFile> dirs = new LinkedHashMap<String, VirtualFile>();
        for (Module module : ModuleManager.getInstance(project).getModules()) {
            ModuleRootManager rootManager = ModuleRootManager.getInstance(module);
            for (VirtualFile resourceRoot : rootManager.getSourceRoots(JavaResourceRootType.RESOURCE)) {
                putDir(dirs, resourceRoot);
            }
            for (VirtualFile contentRoot : rootManager.getContentRoots()) {
                findSrcMainResources(contentRoot, dirs);
            }
        }
        return new ArrayList<VirtualFile>(dirs.values());
    }

    /**
     * 有限深度查找 {@code src/main/resources}，跳过构建产物与 VCS 目录。
     *
     * @param dir  当前目录
     * @param dirs 结果
     */
    private static void findSrcMainResources(final VirtualFile dir, final Map<String, VirtualFile> dirs) {
        if (dir == null || !dir.isValid() || !dir.isDirectory()) {
            return;
        }
        VfsUtilCore.visitChildrenRecursively(dir, new VirtualFileVisitor<Void>(VirtualFileVisitor.limit(RESOURCE_WALK_MAX_DEPTH)) {
            @Override
            public @NotNull Result visitFileEx(@NotNull VirtualFile file) {
                if (!file.isDirectory()) {
                    return CONTINUE;
                }
                if (skipWalkDirectory(file, dir)) {
                    return SKIP_CHILDREN;
                }
                String path = file.getPath().replace('\\', '/');
                if (path.endsWith(SRC_MAIN_RESOURCES)) {
                    putDir(dirs, file);
                    return SKIP_CHILDREN;
                }
                return CONTINUE;
            }
        });
    }

    /**
     * 跳过构建产物、VCS 等目录，起点目录本身仍会扫描。
     *
     * @param file     当前文件或目录
     * @param walkRoot 本次遍历的起点
     * @return 应跳过该目录及其子项则为 {@code true}
     */
    private static boolean skipWalkDirectory(VirtualFile file, VirtualFile walkRoot) {
        return file.isDirectory() && !walkRoot.equals(file) && SKIP_DIR_NAMES.contains(file.getName());
    }

    /**
     * 按路径去重放入资源目录表。
     *
     * @param dirs 结果
     * @param dir  目录
     */
    private static void putDir(Map<String, VirtualFile> dirs, VirtualFile dir) {
        if (dir != null && dir.isValid()) {
            dirs.put(dir.getPath(), dir);
        }
    }

    /**
     * 返回列表中的第一份文件（候选已限制在 resources 内）。
     *
     * @param files 候选文件
     * @return 首选文件
     */
    private static VirtualFile firstInResources(Collection<VirtualFile> files) {
        if (CollectionUtils.isEmpty(files)) {
            return null;
        }
        for (VirtualFile file : files) {
            if (file.getPath().replace('\\', '/').contains(SRC_MAIN_RESOURCES)) {
                return file;
            }
        }
        return files.iterator().next();
    }

    /**
     * 读取虚拟文件内容；IO 失败返回 {@code null}。
     *
     * @param file 虚拟文件
     * @return 文本内容
     */
    private static String read(VirtualFile file) {
        if (file == null) {
            return null;
        }
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
