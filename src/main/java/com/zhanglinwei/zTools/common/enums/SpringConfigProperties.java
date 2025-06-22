package com.zhanglinwei.zTools.common.enums;

public enum SpringConfigProperties {

    SERVER_PROT("server.port"),
    SERVER_SERVLET_CONTEXT_PATH("server.servlet.context-path"),
    SPRING_MVC_SERVLET_PATH("spring.mvc.servlet.path"),
    ACTIVE_PROFILES("spring.profiles.active"),
    ;

    private final String value;

    SpringConfigProperties(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
