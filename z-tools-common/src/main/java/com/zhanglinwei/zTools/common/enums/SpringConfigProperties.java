package com.zhanglinwei.zTools.common.enums;

public enum SpringConfigProperties {

    SERVER_PROT("server.port"),
    SERVER_SERVLET_CONTEXT_PATH("server.servlet.context-path"),
    SPRING_MVC_SERVLET_PATH("spring.mvc.servlet.path"),
    ACTIVE_PROFILES("spring.profiles.active"),

    DATASOURCE_DRIVER("spring.datasource.driver-class-name"),
    DATASOURCE_URL("spring.datasource.url"),
    DATASOURCE_PASSWORD("spring.datasource.password"),
    DATASOURCE_USERNAME("spring.datasource.username"),
    ;

    private final String value;

    SpringConfigProperties(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
