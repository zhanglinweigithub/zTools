package com.zhanglinwei.zTools.util;


import static com.zhanglinwei.zTools.common.constants.SpringPool.*;

/**
 * 通用工具类
 */
public class CommonUtils {

    private CommonUtils(){}

    public static String replace(String text, String searchString, String replacement, int max) {
        if (AssertUtils.isNotBlank(text) && AssertUtils.isNotBlank(searchString) && replacement != null && max != 0) {
            int start = 0;
            int end = text.indexOf(searchString, start);
            if (end == -1) {
                return text;
            } else {
                int replLength = searchString.length();
                int increase = replacement.length() - replLength;
                increase = Math.max(increase, 0);
                increase *= max < 0 ? 16 : (Math.min(max, 64));

                StringBuffer buf;
                for(buf = new StringBuffer(text.length() + increase); end != -1; end = text.indexOf(searchString, start)) {
                    buf.append(text, start, end).append(replacement);
                    start = end + replLength;
                    --max;
                    if (max == 0) {
                        break;
                    }
                }

                buf.append(text.substring(start));
                return buf.toString();
            }
        } else {
            return text;
        }
    }

    public static String substringBetween(String str, String open, String close) {
        if (str != null && open != null && close != null) {
            int start = str.indexOf(open);
            if (start != -1) {
                int end = str.indexOf(close, start + open.length());
                if (end != -1) {
                    return str.substring(start + open.length(), end);
                }
            }

            return null;
        } else {
            return null;
        }
    }

    public static String convertCamelToSnake(String camelCase) {
        String snakeCase = camelCase.replaceAll("([a-z])([A-Z])", "$1_$2");
        return snakeCase.toLowerCase();
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
     * 转换必填
     */
    public static String convertRequired(boolean required) {
        return required ? Y : N;
    }

    public static String getRange(String range) {
        return AssertUtils.isEmpty(range) ? NA : range;
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

    public static String getPrefix() {
        return FOLD;
    }

}
