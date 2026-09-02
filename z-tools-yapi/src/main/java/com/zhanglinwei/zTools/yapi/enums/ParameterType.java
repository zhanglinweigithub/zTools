package com.zhanglinwei.zTools.yapi.enums;

public enum ParameterType {

    FILE("file"),
    TEXT("text"),
    ;

    private final String code;

    ParameterType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
