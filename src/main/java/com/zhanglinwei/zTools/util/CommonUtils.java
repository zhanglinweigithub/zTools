package com.zhanglinwei.zTools.util;

import org.apache.commons.lang.StringUtils;

/**
 * 通用工具类
 */
public class CommonUtils {

    private CommonUtils(){}

    public static final String SLASH = "/";
    public static final String BACK_SLASH = "\\";
    public static final String COMMA = "、";
    public static final String YES = "Y";
    public static final String NO = "N";
    public static final String NULL = "N/A";
    public static final String FOLD = "└";
    public static final String VERTICAL = "|";
    public static final String HORIZONTAL4 = "----";
    public static final String SEMICOLON = ";";
    public static final String STAR = "*";
    public static final String COLON = ":";

    private static final String[] UNITS = {"", "十", "百", "千"};
    private static final String[] NUMBERS = {"", "一", "二", "三", "四", "五", "六", "七", "八", "九"};

    /**
     * 追加斜杠
     */
    public static String appendSlash(String path) {
        if (StringUtils.isEmpty(path)) {
            return "";
        }
        String p = path.replaceAll("/{2,}", "/");
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
        return required ? YES : NO;
    }

    public static String getRange(String range) {
        return AssertUtils.isEmpty(range) ? NULL : range;
    }

    /**
     * 构建请求路径
     */
    public static String buildPath(String classPath, String methodPath) {
        return buildPath(classPath) + buildPath(methodPath);
    }

    public static String buildPath(String path) {
        if (AssertUtils.isBlank(path) || AssertUtils.isBlank(path.replaceAll(SLASH, ""))) {
            return "";
        }
        path = path.replaceAll("/+$", "");
        return path.startsWith(SLASH) ? path : SLASH + path;
    }

    public static String getPrefix() {
        return FOLD;
    }

}
