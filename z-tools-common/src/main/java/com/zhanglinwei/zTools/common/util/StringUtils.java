package com.zhanglinwei.zTools.common.util;

/**
 * 字符串判空、截取与有限次替换。空白判定与 Apache Commons 类似：{@code null}、空串、全空白都算 blank。
 *
 * <pre>
 *   StringUtils.isBlank(null) → true
 *   StringUtils.isBlank("  ") → true
 *   StringUtils.substringBetween("a[b]c", "[", "]") → "b"
 * </pre>
 */
public final class StringUtils {

    /** 工具类，禁止实例化 */
    private StringUtils() {}

    /**
     * {@code null}、空串或全是空白字符则为 {@code true}。
     *
     * <pre>
     *   isBlank(null) → true
     *   isBlank("") → true
     *   isBlank("  ") → true
     *   isBlank("a") → false
     * </pre>
     *
     * @param str 待测字符串
     * @return 空白则为 {@code true}
     */
    public static boolean isBlank(String str) {
        if (str == null || str.isEmpty()) {
            return true;
        }
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * {@link #isBlank(String)} 的否定。
     *
     * @param str 待测字符串
     * @return 含非空白字符则为 {@code true}
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /**
     * {@code null} 或空串为 {@code true}（全空白不算 empty）。
     *
     * <pre>
     *   isEmpty(null) → true
     *   isEmpty("") → true
     *   isEmpty("  ") → false
     * </pre>
     *
     * @param str 待测字符串
     * @return 空则为 {@code true}
     */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * {@link #isEmpty(String)} 的否定。
     *
     * @param str 待测字符串
     * @return 非 {@code null} 且长度大于 0
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static String wrap(String str, String prefix, String suffix) {
        if (str == null) {
            return str;
        }

        if (prefix != null) {
            str = prefix + str;
        }

        if (suffix != null) {
            str = str + suffix;
        }

        return str;
    }

    /**
     * 取 {@code open} 与 {@code close} 之间的子串；任一找不到则为 {@code null}。
     *
     * <pre>
     *   substringBetween("a[b]c", "[", "]") → "b"
     *   substringBetween("abc", "[", "]") → null
     *   substringBetween(null, "[", "]") → null
     * </pre>
     *
     * @param origin 源串
     * @param open   起始标记
     * @param close  结束标记
     * @return 中间子串；找不到或任一参数为 {@code null} 则返回 {@code null}
     */
    public static String substringBetween(String origin, String open, String close) {
        if (origin == null || open == null || close == null) {
            return null;
        }
        int start = origin.indexOf(open);
        if (start < 0) {
            return null;
        }
        int from = start + open.length();
        int end = origin.indexOf(close, from);
        return end < 0 ? null : origin.substring(from, end);
    }

    /**
     * 将 {@code search} 替换为 {@code replacement}，最多 {@code max} 次；{@code max < 0} 表示不限制。
     *
     * <pre>
     *   replace("aaa", "a", "b", 2) → "bba"
     *   replace("aaa", "a", "b", -1) → "bbb"
     *   replace("aaa", "x", "b", 1) → "aaa"
     * </pre>
     *
     * @param origin      源串
     * @param search      被替换片段
     * @param replacement 替换成的内容
     * @param max         最大替换次数；小于 0 不限制，等于 0 不替换
     * @return 替换后的字符串；入参不满足替换条件时原样返回 {@code origin}
     */
    public static String replace(String origin, String search, String replacement, int max) {
        if (isBlank(origin) || isBlank(search) || replacement == null || max == 0) {
            return origin;
        }
        int start = 0;
        int end = origin.indexOf(search, start);
        if (end < 0) {
            return origin;
        }
        int searchLength = search.length();
        StringBuilder buf = new StringBuilder(origin.length());
        while (end >= 0) {
            buf.append(origin, start, end).append(replacement);
            start = end + searchLength;
            max--;
            if (max == 0) {
                break;
            }
            end = origin.indexOf(search, start);
        }
        buf.append(origin.substring(start));
        return buf.toString();
    }
}
