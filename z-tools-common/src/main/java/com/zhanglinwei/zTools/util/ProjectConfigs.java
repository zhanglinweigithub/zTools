package com.zhanglinwei.zTools.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.zhanglinwei.zTools.constant.CharacterPool;
import com.zhanglinwei.zTools.enums.SpringConfigProperties;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

import static com.zhanglinwei.zTools.constant.StringPool.COMMA;
import static com.zhanglinwei.zTools.constant.StringPool.CRLF;
import static com.zhanglinwei.zTools.constant.StringPool.DOT;
import static com.zhanglinwei.zTools.constant.StringPool.EMPTY;
import static com.zhanglinwei.zTools.constant.StringPool.EQUAL;
import static com.zhanglinwei.zTools.constant.StringPool.LEFT_SQ_BRACKET;
import static com.zhanglinwei.zTools.constant.StringPool.NEWLINE;
import static com.zhanglinwei.zTools.constant.StringPool.RIGHT_SQ_BRACKET;

/**
 * 读项目里的 Spring {@code application.*} 与插件 {@code zTools.*} 配置。
 */
public final class ProjectConfigs {

    private static final String SPRING_YAML = "application.yaml";
    private static final String SPRING_YML = "application.yml";
    private static final String SPRING_PROPERTIES = "application.properties";

    private static final String ZTOOLS_YAML = "zTools.yaml";
    private static final String ZTOOLS_YML = "zTools.yml";
    private static final String ZTOOLS_PROPERTIES = "zTools.properties";

    private ProjectConfigs() {}

    public static String spring(Project project, SpringConfigProperties key) {
        return asString(property(project, key, SPRING_YAML, SPRING_YML, SPRING_PROPERTIES));
    }

    public static String zTools(Project project, SpringConfigProperties key) {
        return asString(property(project, key, ZTOOLS_YAML, ZTOOLS_YML, ZTOOLS_PROPERTIES));
    }

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
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
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

    private static Collection<VirtualFile> filesByName(Project project, String name) {
        return new ArrayList<VirtualFile>(
                FilenameIndex.getVirtualFilesByName(name, false, GlobalSearchScope.projectScope(project))
        );
    }

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

    private static String read(VirtualFile file) {
        try {
            return new String(file.contentsToByteArray());
        } catch (IOException ignored) {
            return null;
        }
    }

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
