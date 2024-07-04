package com.zhanglinwei.zTools.constant;

import java.util.StringJoiner;

public enum RequestMethodEnum {
    GET,POST,PUT,DELETE,PATCH;


    public static String getAll(){
        StringJoiner joiner = new StringJoiner("、");
        for (RequestMethodEnum methodEnum : RequestMethodEnum.values()) {
            joiner.add(methodEnum.name());
        }

        return joiner.toString();
    }
}
