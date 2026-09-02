package com.zhanglinwei.zTools.yapi.enums;

public enum ReqBodyType {

    JSON("json"),
    FORM("form"),
    ;

    private final String code;

    ReqBodyType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
