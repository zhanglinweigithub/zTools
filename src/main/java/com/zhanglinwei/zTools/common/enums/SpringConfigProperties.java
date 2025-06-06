package com.zhanglinwei.zTools.common.enums;

public enum SpringConfigProperties {

    SERVER_PROT("server.port"),
    CONTEXT_PATH("server.servlet.context-path"),
    ACTIVE_PROFILES("spring.profiles.active"),
    ;

    private String value;

    SpringConfigProperties(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
