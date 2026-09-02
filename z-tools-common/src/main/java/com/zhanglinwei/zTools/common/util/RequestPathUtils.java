package com.zhanglinwei.zTools.common.util;

import com.zhanglinwei.zTools.common.constant.CharacterPool;
import com.zhanglinwei.zTools.common.constant.StringPool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 拼接类级别与方法级别的 Mapping path，规则对齐 Spring MVC：
 * 补前导 {@code /}、去掉多余尾 {@code /}、空 path 视为根 {@code /}。
 * 类 path 作为前缀，与方法 path 做笛卡尔积。
 */
public final class RequestPathUtils {

    private RequestPathUtils() {}

    /**
     * 类级别 path × 方法级别 path。任一侧为 {@code null} 或空列表时，当作 {@code [""]}（即根路径）。
     *
     * <pre>
     * 输入：type=["/api"], method=["/user"]
     * 输出：["/api/user"]
     *
     * 输入：type=["/api", "/admin"], method=["/user", "/role"]
     * 输出：["/api/user", "/api/role", "/admin/user", "/admin/role"]
     *
     * 输入：type=["/api/"], method=["user"]
     * 输出：["/api/user"]
     *
     * 输入：type=null, method=["/user"]
     * 输出：["/user"]
     *
     * 输入：type=["/api"], method=null
     * 输出：["/api"]
     *
     * 输入：type=null, method=null
     * 输出：["/"]
     * </pre>
     */
    public static List<String> combine(List<String> typeLevel, List<String> methodLevel) {
        List<String> prefixes = emptyToRoot(typeLevel);
        List<String> suffixes = emptyToRoot(methodLevel);
        List<String> combined = new ArrayList<String>(prefixes.size() * suffixes.size());
        for (String prefix : prefixes) {
            for (String suffix : suffixes) {
                combined.add(join(prefix, suffix));
            }
        }
        return combined;
    }

    /**
     * 按顺序拼接若干 path 片段。{@code null}、空串、单独的 {@code /} 不贡献新段；全部为空则返回 {@code "/"}。
     *
     * <pre>
     * 输入：join("/api", "/user")
     * 输出："/api/user"
     *
     * 输入：join("/api/", "user/")
     * 输出："/api/user"
     *
     * 输入：join("api", "user")
     * 输出："/api/user"
     *
     * 输入：join("/api", "", "/user")
     * 输出："/api/user"
     *
     * 输入：join(null, "/user")
     * 输出："/user"
     *
     * 输入：join() 或 join(null, "")
     * 输出："/"
     *
     * 输入：join("/api", "/user/{id}")
     * 输出："/api/user/{id}"
     * </pre>
     */
    public static String join(String... segments) {
        StringBuilder builder = new StringBuilder();
        if (segments != null) {
            for (String segment : segments) {
                append(builder, segment);
            }
        }
        return builder.length() == 0 ? StringPool.SLASH : builder.toString();
    }

    /** 把一段 path 接到已有结果后面。 */
    private static void append(StringBuilder builder, String segment) {
        String normalized = normalize(segment);
        if (normalized.isEmpty() || StringPool.SLASH.equals(normalized)) {
            if (builder.length() == 0) {
                builder.append(StringPool.SLASH);
            }
            return;
        }
        if (builder.length() == 0 || StringPool.SLASH.equals(builder.toString())) {
            builder.setLength(0);
            builder.append(normalized);
            return;
        }
        if (builder.charAt(builder.length() - 1) == CharacterPool.SLASH) {
            builder.append(normalized.startsWith(StringPool.SLASH) ? normalized.substring(1) : normalized);
        } else if (normalized.startsWith(StringPool.SLASH)) {
            builder.append(normalized);
        } else {
            builder.append(CharacterPool.SLASH).append(normalized);
        }
    }

    /**
     * 规范化单段 path：去空白、补前导 {@code /}、去掉末尾多余 {@code /}（根 {@code /} 除外）。
     *
     * <pre>
     * 输入："user"      输出："/user"
     * 输入："/user/"    输出："/user"
     * 输入："/"         输出："/"
     * 输入：null / ""   输出：""
     * </pre>
     */
    private static String normalize(String path) {
        if (path == null) {
            return StringPool.EMPTY;
        }
        String trimmed = path.trim();
        if (trimmed.isEmpty()) {
            return StringPool.EMPTY;
        }
        if (!trimmed.startsWith(StringPool.SLASH)) {
            trimmed = StringPool.SLASH + trimmed;
        }
        while (trimmed.length() > 1 && trimmed.endsWith(StringPool.SLASH)) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        return trimmed;
    }

    /** {@code null} 或空列表视为只含根路径的单元素列表，便于和另一侧做笛卡尔积。 */
    private static List<String> emptyToRoot(List<String> paths) {
        return paths == null || paths.isEmpty()
                ? Collections.singletonList(StringPool.EMPTY)
                : paths;
    }
}
