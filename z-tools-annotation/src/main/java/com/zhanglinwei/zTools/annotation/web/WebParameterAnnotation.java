package com.zhanglinwei.zTools.annotation.web;

/**
 * Spring Web 参数绑定注解。未写的属性为 {@code null}，不把 {@code required} 缺省成 {@code true}。
 */
public final class WebParameterAnnotation {

    /**
     * 参数绑定种类，与源码注解对应；未出现的种类不会被推断出来。
     */
    public enum Kind {
        /** {@code @RequestParam} */
        QUERY,
        /** {@code @PathVariable} */
        PATH,
        /** {@code @RequestHeader} */
        HEADER,
        /** {@code @RequestBody} */
        BODY,
        /** {@code @RequestPart} */
        PART,
        /** {@code @CookieValue} */
        COOKIE,
        /** {@code @RequestAttribute} */
        ATTRIBUTE,
        /** {@code @ModelAttribute} */
        MODEL
    }

    /** 绑定种类。 */
    private final Kind kind;
    /** {@code name} 或 {@code value}。 */
    private final String name;
    /** 源码写出的 {@code required}；未写则为 {@code null}。 */
    private final Boolean required;
    /** 源码写出的 {@code defaultValue}；未写或为 {@code DEFAULT_NONE} 则为 {@code null}。 */
    private final String defaultValue;

    /**
     * @param kind         绑定种类
     * @param name         name 或 value
     * @param required     源码写出的 required
     * @param defaultValue 源码写出的 defaultValue
     */
    public WebParameterAnnotation(Kind kind, String name, Boolean required, String defaultValue) {
        this.kind = kind;
        this.name = name;
        this.required = required;
        this.defaultValue = defaultValue;
    }

    /** 绑定种类。 */
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
