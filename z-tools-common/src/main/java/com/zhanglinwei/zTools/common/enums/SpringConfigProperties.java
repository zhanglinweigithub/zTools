package com.zhanglinwei.zTools.common.enums;

/**
 * 常用 Spring {@code application.*} 配置项的 key。
 * <p>
 * 配合 {@code ProjectConfigs} 从 yaml / properties 中读取端口、context-path、数据源等。
 */
public enum SpringConfigProperties {

    /** {@code server.port}（枚举名沿用历史拼写 PROT） */
    SERVER_PROT("server.port"),
    SERVER_SERVLET_CONTEXT_PATH("server.servlet.context-path"),
    SPRING_MVC_SERVLET_PATH("spring.mvc.servlet.path"),
    ACTIVE_PROFILES("spring.profiles.active"),

    DATASOURCE_DRIVER("spring.datasource.driver-class-name"),
    DATASOURCE_URL("spring.datasource.url"),
    DATASOURCE_PASSWORD("spring.datasource.password"),
    DATASOURCE_USERNAME("spring.datasource.username"),
    ;

    /** 配置文件中的完整 key */
    private final String value;

    /**
     * @param value Spring 配置 key
     */
    SpringConfigProperties(String value) {
        this.value = value;
    }

    /** Spring 配置 key，如 {@code server.port}。 */
    public String getValue() {
        return value;
    }
}
