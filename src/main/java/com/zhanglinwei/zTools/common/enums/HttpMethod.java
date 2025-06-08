package com.zhanglinwei.zTools.common.enums;

import java.util.StringJoiner;

public enum HttpMethod {
    GET,POST,PUT,DELETE,PATCH,NONE;


    public static String getAll(){
        StringJoiner joiner = new StringJoiner("、");
        for (HttpMethod methodEnum : HttpMethod.values()) {
            joiner.add(methodEnum.name());
        }

        return joiner.toString();
    }

    public static HttpMethod of(String httpMethod) {
        for (HttpMethod item : HttpMethod.values()) {
            if (item.name().equalsIgnoreCase(httpMethod)) {
                return item;
            }
        }

        return null;
    }
}
