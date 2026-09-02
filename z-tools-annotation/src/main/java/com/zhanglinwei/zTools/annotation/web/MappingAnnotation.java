package com.zhanglinwei.zTools.annotation.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Web Mapping 注解上源码写出的属性。
 * {@link #methods()} 对 {@code @GetMapping} 等组合注解会带上对应动词；
 * {@code @RequestMapping} 未写 {@code method} 时为 {@code null}。
 */
public final class MappingAnnotation {

    /** {@code name} 属性。 */
    private final String name;
    /** {@code path} / {@code value} / {@code url}。 */
    private final List<String> paths;
    /** HTTP 动词；未写且非组合注解时为 {@code null}。 */
    private final List<String> methods;
    /** {@code consumes} / {@code contentType}。 */
    private final List<String> consumes;
    /** {@code produces} / {@code accept}。 */
    private final List<String> produces;
    /** {@code params}。 */
    private final List<String> params;
    /** {@code headers}。 */
    private final List<String> headers;

    /**
     * @param name     name 属性
     * @param paths    path / value / url
     * @param methods  HTTP 动词
     * @param consumes consumes / contentType
     * @param produces produces / accept
     * @param params   params
     * @param headers  headers
     */
    public MappingAnnotation(String name, List<String> paths, List<String> methods,
                             List<String> consumes, List<String> produces,
                             List<String> params, List<String> headers) {
        this.name = name;
        this.paths = copy(paths);
        this.methods = copy(methods);
        this.consumes = copy(consumes);
        this.produces = copy(produces);
        this.params = copy(params);
        this.headers = copy(headers);
    }

    /** {@code name} 属性。 */
    public String name() {
        return name;
    }

    /** {@code path} / {@code value} / {@code url}。 */
    public List<String> paths() {
        return paths;
    }

    /** 第一条 path；没有则为 {@code null}。 */
    public String firstPath() {
        if (paths == null || paths.isEmpty()) {
            return null;
        }

        return paths.get(0);
    }

    /** HTTP 动词；未写且非组合注解时为 {@code null}。 */
    public List<String> methods() {
        return methods;
    }

    /** {@code consumes} / {@code contentType}。 */
    public List<String> consumes() {
        return consumes;
    }

    /** {@code produces} / {@code accept}。 */
    public List<String> produces() {
        return produces;
    }

    /** {@code params}。 */
    public List<String> params() {
        return params;
    }

    /** {@code headers}。 */
    public List<String> headers() {
        return headers;
    }

    /**
     * 复制为不可变列表；入参为 {@code null} 时保持 {@code null}。
     *
     * @param items 原始列表
     * @return 不可变副本
     */
    private static List<String> copy(List<String> items) {
        return items == null ? null : Collections.unmodifiableList(new ArrayList<String>(items));
    }
}
