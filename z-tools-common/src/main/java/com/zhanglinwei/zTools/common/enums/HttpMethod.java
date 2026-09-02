package com.zhanglinwei.zTools.common.enums;

public enum HttpMethod {
    GET, POST, PUT, DELETE, PATCH, NONE;

    public static HttpMethod of(String httpMethod) {
        if (httpMethod == null) {
            return null;
        }
        for (HttpMethod item : values()) {
            if (item.name().equalsIgnoreCase(httpMethod)) {
                return item;
            }
        }
        return null;
    }
}
