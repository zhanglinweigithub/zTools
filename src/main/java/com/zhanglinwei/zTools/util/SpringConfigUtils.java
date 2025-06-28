package com.zhanglinwei.zTools.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.zhanglinwei.zTools.common.enums.SpringConfigProperties;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.zhanglinwei.zTools.common.constants.SpringPool.*;

public final class SpringConfigUtils {

    private SpringConfigUtils() {}

    private static Map<String, Object> findYamlToFlattenMap(Project project) {
        Collection<VirtualFile> configFileList = new ArrayList<>();

        Collection<VirtualFile> yamlCfg = FilenameIndex.getVirtualFilesByName("application.yaml", false, GlobalSearchScope.projectScope(project));
        if (AssertUtils.isNotEmpty(yamlCfg)) {
            configFileList.addAll(yamlCfg);
        } else {
            Collection<VirtualFile> ymlCfg = FilenameIndex.getVirtualFilesByName("application.yml", false, GlobalSearchScope.projectScope(project));
            if (AssertUtils.isNotEmpty(ymlCfg)) {
                configFileList.addAll(ymlCfg);
            }
        }

        if (!configFileList.isEmpty()) {
            VirtualFile configFile = configFileList.stream()
                    .filter(file -> file.getPath().contains("src/main/resources"))
                    .findFirst()
                    .orElse(null);

            if (configFile != null) {
                String yamlContent = null;
                try {
                    yamlContent = new String(configFile.contentsToByteArray());
                } catch (IOException ignore) {
                    // ignore exception
                }

                if (AssertUtils.isNotBlank(yamlContent)) {
                    return yamlToFlattenMap(yamlContent);
                }
            }
        }

        return Collections.emptyMap();
    }

    private static Map<String, Object> propertiesToFlattenMap(String propertiesContent) {
        return AssertUtils.isBlank(propertiesContent) ? Collections.emptyMap() : Arrays.stream(propertiesContent.split(propertiesContent.contains(CRLF) ? CRLF : NEWLINE))
                .filter(AssertUtils::isNotBlank)
                .collect(Collectors.toMap(
                        item -> item.split(EQUAL, 2)[0],
                        item -> item.split(EQUAL, 2)[1],
                        (oldVal, newVal) -> oldVal,
                        LinkedHashMap::new
                ));
    }

    private static Map<String, Object> yamlToFlattenMap(String yamlContent) {
        if (AssertUtils.isBlank(yamlContent)) {
            return Collections.emptyMap();
        }

        Map<String, Object> yamlMap = new Yaml().load(yamlContent);
        return yamlToFlattenMap(yamlMap);
    }

    private static Map<String, Object> yamlToFlattenMap(Map<String, Object> sourceMap) {
        Map<String, Object> flatMap = new LinkedHashMap<>();
        flattenMapRecursive(EMPTY, sourceMap, flatMap);
        return flatMap;
    }

    /**
     * 递归辅助方法
     * @param prefix 当前键前缀
     * @param source 当前处理的Map或值
     * @param result 结果Map
     */
    private static void flattenMapRecursive(String prefix, Object source, Map<String, Object> result) {
        // 处理Map类型
        if (source instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) source;
            map.forEach((key, value) -> {
                String kebabKey = CamelUtils.camelToKebabCase(key.toString());
                String newPrefix = prefix.isEmpty() ? kebabKey : prefix + DOT + kebabKey;
                flattenMapRecursive(newPrefix, value, result);
            });
        }
        // 处理列表/数组类型
//        else if (source instanceof Iterable) {
//            int index = 0;
//            for (Object item : (Iterable<?>) source) {
//                String newPrefix = prefix.isEmpty() ? String.valueOf(index) : prefix + "[" + index + "]";
//                flattenMapRecursive(newPrefix, item, result);
//                index++;
//            }
//        }
        // 基本类型，直接放入结果
        else {
            if (source != null) {
                result.put(prefix, source);
            }
        }
    }

    public static Object property(Project project, SpringConfigProperties configProperties) {
        try {
            Map<String, Object> yamlMap = findYamlToFlattenMap(project);
            Object value = yamlMap.get(configProperties.getValue());
            if (value == null) {
                Map<String, Object> propertiesMap = findPropertiesToFlattenMap(project);
                value = propertiesMap.get(configProperties.getValue());
            }

            return value;
        } catch (Exception e) {
        }
        return null;
    }

    private static Map<String, Object> findPropertiesToFlattenMap(Project project) {
        Collection<VirtualFile> configFileList = new ArrayList<>(FilenameIndex.getVirtualFilesByName("application.properties", false, GlobalSearchScope.projectScope(project)));

        VirtualFile configFile = configFileList.stream()
                .filter(file -> file.getPath().contains("src/main/resources"))
                .findFirst()
                .orElse(null);

        if (configFile != null) {
            String propertiesContent = null;
            try {
                propertiesContent = new String(configFile.contentsToByteArray());
            } catch (IOException ignore) {
                // ignore exception
            }

            if (AssertUtils.isNotBlank(propertiesContent)) {
                return propertiesToFlattenMap(propertiesContent);
            }
        }

        return Collections.emptyMap();
    }

    public static String propertyAsString(Project project, SpringConfigProperties configProperties) {
        Object object = property(project, configProperties);
        if (object != null) {
            if (object instanceof Iterable) {
                StringJoiner joiner = new StringJoiner(COMMA, LEFT_SQ_BRACKET, RIGHT_SQ_BRACKET);
                for (Object item : ((Iterable<?>) object)) {
                    joiner.add(item.toString());
                }
                return joiner.toString();
            }

            return object.toString();
        }
        return EMPTY;
    }

    public static boolean propertyAsBoolean(Project project, SpringConfigProperties configProperties) {
        String value = propertyAsString(project, configProperties);
        return TRUE.equalsIgnoreCase(value);
    }

    public static Long propertyAsLong(Project project, SpringConfigProperties configProperties) {
        try {
            String value = propertyAsString(project, configProperties);
            return AssertUtils.isBlank(value) ? null : Long.parseLong(value);
        } catch (NumberFormatException ignore) {
            // ignore exception
        }

        return null;
    }

    public static Integer propertyAsInteger(Project project, SpringConfigProperties configProperties) {
        try {
            String value = propertyAsString(project, configProperties);
            return AssertUtils.isBlank(value) ? null : Integer.parseInt(value);
        } catch (NumberFormatException ignore) {
            // ignore exception
        }

        return null;
    }
}
