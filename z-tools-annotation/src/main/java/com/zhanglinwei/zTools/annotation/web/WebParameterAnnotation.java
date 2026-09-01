package com.zhanglinwei.zTools.annotation.web;

/**
 * Spring Web 参数绑定注解。未写的属性为 {@code null}，不把 {@code required} 缺省成 {@code true}。
 */
public final class WebParameterAnnotation {

    public enum Kind {
        QUERY,
        PATH,
        HEADER,
        BODY,
        PART,
        COOKIE,
        ATTRIBUTE,
        MODEL
    }

    private final Kind kind;
    private final String name;
    private final Boolean required;
    private final String defaultValue;

    public WebParameterAnnotation(Kind kind, String name, Boolean required, String defaultValue) {
        this.kind = kind;
        this.name = name;
        this.required = required;
        this.defaultValue = defaultValue;
    }

    public Kind kind() {
        return kind;
    }

    /** {@code name} 或 {@code value}。 */
    public String name() {
        return name;
    }

    /** 源码写出的 {@code required}；未写则为 {@code null}。 */
    public Boolean required() {
        return required;
    }

    /** 源码写出的 {@code defaultValue}；未写或为 {@code DEFAULT_NONE} 则为 {@code null}。 */
    public String defaultValue() {
        return defaultValue;
    }
}
