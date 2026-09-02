package com.zhanglinwei.zTools.common.util;

public final class StringUtils {

    private StringUtils() {}

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

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /** 取 {@code open} 与 {@code close} 之间的子串；任一找不到则为 {@code null}。 */
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

    /** 将 {@code search} 替换为 {@code replacement}，最多 {@code max} 次；{@code max < 0} 表示不限制。 */
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
