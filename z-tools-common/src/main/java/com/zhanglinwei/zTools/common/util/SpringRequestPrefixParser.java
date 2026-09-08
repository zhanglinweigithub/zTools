package com.zhanglinwei.zTools.common.util;

import com.zhanglinwei.zTools.common.constant.CharacterPool;
import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.zhanglinwei.zTools.common.constant.StringPool.EMPTY;
import static com.zhanglinwei.zTools.common.constant.StringPool.EQUAL;
import static com.zhanglinwei.zTools.common.constant.StringPool.SLASH;

/**
 * 从 Spring 配置文件文本中提取全局请求前缀。
 * <p>
 * 优先 {@code server.servlet.context-path}，否则 {@code spring.mvc.servlet.path}。
 * 只处理 {@code .yaml} / {@code .yml} / {@code .properties}。
 */
public final class SpringRequestPrefixParser {

    private static final String YAML_SUFFIX = ".yaml";
    private static final String YML_SUFFIX = ".yml";
    private static final String PROPERTIES_SUFFIX = ".properties";
    private static final String CONTEXT_PATH = "server.servlet.context-path";
    private static final String SERVLET_PATH = "spring.mvc.servlet.path";

    /** 工具类，禁止实例化 */
    private SpringRequestPrefixParser() {
    }

    /**
     * 是否为需要扫描的配置文件。
     *
     * @param fileName 文件名
     * @return yaml / yml / properties 则为 {@code true}
     */
    public static boolean isConfigFile(String fileName) {
        if (fileName == null) {
            return false;
        }
        String lower = fileName.toLowerCase();
        return lower.endsWith(YAML_SUFFIX) || lower.endsWith(YML_SUFFIX) || lower.endsWith(PROPERTIES_SUFFIX);
    }

    /**
     * 提取文件中的第一个前缀。
     *
     * @param fileName 文件名，用于判断格式
     * @param content  文件内容
     * @return 规范化前缀；没有则为空串
     */
    public static String fromContent(String fileName, String content) {
        List<String> all = allFromContent(fileName, content);
        return all.isEmpty() ? EMPTY : all.get(0);
    }

    /**
     * 提取文件中的全部前缀（YAML 多文档会得到多项）。
     *
     * @param fileName 文件名
     * @param content  文件内容
     * @return 规范化前缀列表，可能为空
     */
    public static List<String> allFromContent(String fileName, String content) {
        if (!isConfigFile(fileName) || StringUtils.isBlank(content)) {
            return Collections.emptyList();
        }
        String lower = fileName.toLowerCase();
        if (lower.endsWith(PROPERTIES_SUFFIX)) {
            String prefix = normalize(fromProperties(content));
            return StringUtils.isBlank(prefix) ? Collections.emptyList() : Collections.singletonList(prefix);
        }
        return fromYamlDocuments(content);
    }

    /**
     * 去空白、统一斜杠形式，去掉重复，保持原有顺序。空值不进入结果。
     *
     * @param prefixes 原始前缀
     * @return 去重后的前缀
     */
    public static List<String> unique(List<String> prefixes) {
        if (prefixes == null || prefixes.isEmpty()) {
            return Collections.emptyList();
        }
        Set<String> seen = new LinkedHashSet<String>();
        List<String> result = new ArrayList<String>();
        for (String prefix : prefixes) {
            String normalized = normalize(prefix);
            if (StringUtils.isBlank(normalized)) {
                continue;
            }
            if (seen.add(normalized)) {
                result.add(normalized);
            }
        }
        return result;
    }

    /**
     * 规范化前缀：补 {@code /}、去掉末尾 {@code /}；空或根路径视为未配置。
     *
     * @param raw 原始值
     * @return 规范化前缀或空串
     */
    public static String normalize(String raw) {
        if (StringUtils.isBlank(raw)) {
            return EMPTY;
        }
        String joined = RequestPathUtils.join(raw.trim());
        return SLASH.equals(joined) ? EMPTY : joined;
    }

    /**
     * 解析 YAML 全部文档。
     *
     * @param content YAML 文本
     * @return 每份文档中读到的前缀
     */
    private static List<String> fromYamlDocuments(String content) {
        List<String> result = new ArrayList<String>();
        try {
            Yaml yaml = new Yaml();
            for (Object document : yaml.loadAll(content)) {
                String prefix = normalize(fromYamlNode(document));
                if (StringUtils.isNotBlank(prefix)) {
                    result.add(prefix);
                }
            }
        } catch (Exception ignored) {
            return Collections.emptyList();
        }
        return result;
    }

    /**
     * 从 YAML 根节点取前缀。
     *
     * @param node 根节点
     * @return 原始前缀字符串
     */
    private static String fromYamlNode(Object node) {
        Object contextPath = nested(node, "server", "servlet", "context-path");
        if (contextPath == null) {
            contextPath = nested(node, "server", "servlet", "contextPath");
        }
        String prefix = asString(contextPath);
        if (StringUtils.isNotBlank(prefix)) {
            return prefix;
        }
        Object servletPath = nested(node, "spring", "mvc", "servlet", "path");
        return asString(servletPath);
    }

    /**
     * 沿嵌套 Map 取值，key 忽略 kebab / camel 差异。
     *
     * @param node 当前节点
     * @param path 路径片段
     * @return 叶子值
     */
    private static Object nested(Object node, String... path) {
        Object current = node;
        for (String key : path) {
            if (!(current instanceof Map)) {
                return null;
            }
            current = mapValue((Map<?, ?>) current, key);
        }
        return current;
    }

    /**
     * 按 key 取值，兼容大小写与 kebab/camel。
     *
     * @param map 当前层
     * @param key 期望 key
     * @return 对应值
     */
    private static Object mapValue(Map<?, ?> map, String key) {
        if (map.containsKey(key)) {
            return map.get(key);
        }
        String kebab = toKebab(key);
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            String actual = String.valueOf(entry.getKey());
            if (key.equalsIgnoreCase(actual) || kebab.equals(toKebab(actual))) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * 从 properties 文本读取前缀。
     *
     * @param content properties 文本
     * @return 原始前缀
     */
    private static String fromProperties(String content) {
        String contextPath = null;
        String servletPath = null;
        String[] lines = content.split("\\r?\\n");
        for (String raw : lines) {
            String line = raw.trim();
            if (line.isEmpty() || line.startsWith("#") || line.startsWith("!")) {
                continue;
            }
            int eq = line.indexOf(EQUAL);
            if (eq <= 0) {
                continue;
            }
            String key = line.substring(0, eq).trim();
            String value = line.substring(eq + 1).trim();
            if (CONTEXT_PATH.equals(key) && contextPath == null) {
                contextPath = value;
            } else if (SERVLET_PATH.equals(key) && servletPath == null) {
                servletPath = value;
            }
        }
        return StringUtils.isNotBlank(contextPath) ? contextPath : servletPath;
    }

    /**
     * camelCase → kebab-case。
     *
     * @param camelCase 原 key
     * @return kebab-case
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
     * 配置值转字符串。
     *
     * @param value 原始值
     * @return 字符串；{@code null} 为空串
     */
    private static String asString(Object value) {
        return value == null ? EMPTY : value.toString().trim();
    }

}
