package com.zhanglinwei.zTools.annotation.feign;

/**
 * {@code feign.RequestLine} 源码写出的 {@code value}，拆成 HTTP 动词和 path。
 * 未写 {@code value} 时整段为 {@code null}。
 */
public final class RequestLineAnnotation {

    private final String value;
    private final String httpMethod;
    private final String path;

    public RequestLineAnnotation(String value, String httpMethod, String path) {
        this.value = value;
        this.httpMethod = httpMethod;
        this.path = path;
    }

    /** 原始 {@code value}，如 {@code GET /users/{id}}。 */
    public String value() {
        return value;
    }

    /** 第一个空格前的动词，如 {@code GET}；没有空格则为整段。 */
    public String httpMethod() {
        return httpMethod;
    }

    /** 第一个空格后的 path；未写 path 则为 {@code null}。 */
    public String path() {
        return path;
    }
}
