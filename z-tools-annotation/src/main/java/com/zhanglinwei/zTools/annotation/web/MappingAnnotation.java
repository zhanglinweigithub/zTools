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

    private final String name;
    private final List<String> paths;
    private final List<String> methods;
    private final List<String> consumes;
    private final List<String> produces;
    private final List<String> params;
    private final List<String> headers;

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

    public List<String> params() {
        return params;
    }

    public List<String> headers() {
        return headers;
    }

    private static List<String> copy(List<String> items) {
        return items == null ? null : Collections.unmodifiableList(new ArrayList<String>(items));
    }
}
