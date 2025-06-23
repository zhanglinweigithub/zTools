package com.zhanglinwei.zTools.util;


import static com.zhanglinwei.zTools.common.constants.SpringPool.*;

/**
 * 通用工具类
 */
public final class CommonUtils {

    private CommonUtils(){}

    /**
     * 将字符串中指定的子串替换为新的字符串，支持控制最大替换次数
     * @param origin 原始字符串
     * @param searchString 需要被替换的子串
     * @param replacement 用于替换的新字符串
     * @param max 最大替换次数（负数表示不限制次数）
     * @return
     * replace("aabaa", "a", "z", 2)  // 返回 "zzbaa"
     * replace("hello", "l", "L", -1) // 返回 "heLLo"
     */
    public static String replace(String origin, String searchString, String replacement, int max) {
        if (AssertUtils.isNotBlank(origin) && AssertUtils.isNotBlank(searchString) && replacement != null && max != 0) {
            int start = 0;
            int end = origin.indexOf(searchString, start);
            if (end == -1) {
                return origin;
            } else {
                int replLength = searchString.length();
                int increase = replacement.length() - replLength;
                increase = Math.max(increase, 0);
                increase *= max < 0 ? 16 : (Math.min(max, 64));

                StringBuffer buf;
                for(buf = new StringBuffer(origin.length() + increase); end != -1; end = origin.indexOf(searchString, start)) {
                    buf.append(origin, start, end).append(replacement);
                    start = end + replLength;
                    --max;
                    if (max == 0) {
                        break;
                    }
                }

                buf.append(origin.substring(start));
                return buf.toString();
            }
        } else {
            return origin;
        }
    }

    /**
     * 提取字符串中两个标记之间的子串
     * @param origin 原始字符串
     * @param open 开始标记
     * @param close 结束标记
     * @return
     * substringBetween(" abc[def]ghi ", " [ ", " ] ") // 返回 "def"
     * substringBetween("abc", "[", "]")         // 返回 null
     */
    public static String substringBetween(String origin, String open, String close) {
        if (origin != null && open != null && close != null) {
            int start = origin.indexOf(open);
            if (start != -1) {
                int end = origin.indexOf(close, start + open.length());
                if (end != -1) {
                    return origin.substring(start + open.length(), end);
                }
            }

            return null;
        } else {
            return null;
        }
    }

    /**
     * 追加斜杠
     */
    public static String appendSlash(String path) {
        if (AssertUtils.isBlank(path)) {
            return "";
        }
        String p = path.replaceAll("/{2,}", SLASH);
        if (!path.startsWith(SLASH)) {
            p = SLASH + path;
        }
        if (path.endsWith(SLASH)) {
            p = p.substring(0, p.length() - 1);
        }
        return p;
    }

    /**
     * 构建请求路径
     */
    public static String buildPath(String classPath, String methodPath) {
        return buildPath(classPath) + buildPath(methodPath);
    }

    public static String buildPath(String path) {
        if (AssertUtils.isBlank(path) || AssertUtils.isBlank(path.replaceAll(SLASH, EMPTY))) {
            return EMPTY;
        }
        path = path.replaceAll("/+$", EMPTY);
        return path.startsWith(SLASH) ? path : SLASH + path;
    }

}
