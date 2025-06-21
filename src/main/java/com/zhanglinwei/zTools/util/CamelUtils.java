package com.zhanglinwei.zTools.util;

public final class CamelUtils {

    private CamelUtils(){}

    /**
     * 驼峰转中划线
     * userList ==> user-list
     */
    public static String camelToKebabCase(String camelCase) {
        if (AssertUtils.isBlank(camelCase)) {
            return camelCase;
        }

        StringBuilder result = new StringBuilder();
        result.append(Character.toLowerCase(camelCase.charAt(0)));

        for (int i = 1; i < camelCase.length(); i++) {
            char currentChar = camelCase.charAt(i);
            if (Character.isUpperCase(currentChar)) {
                result.append('-');
                result.append(Character.toLowerCase(currentChar));
            } else {
                result.append(currentChar);
            }
        }

        return result.toString();
    }

}
