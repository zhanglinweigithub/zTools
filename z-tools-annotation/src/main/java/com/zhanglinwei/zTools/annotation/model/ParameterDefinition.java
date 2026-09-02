package com.zhanglinwei.zTools.annotation.model;

import com.zhanglinwei.zTools.common.util.CollectionUtils;

import java.util.List;

/**
 * 方法参数或返回值：类型名、包、注解、注释；对象类型则带 {@link #properties()}。
 * 返回值没有参数名，{@link #name()} 为 {@code null}。
 */
public final class ParameterDefinition {

    /** 参数名；返回值没有参数名，为 {@code null}。 */
    private final String name;
    /** 展示类型名，如 {@code String}、{@code List<User>}。 */
    private final String type;
    /** 类型所在包；基本类型为 {@code null}。 */
    private final String packageName;
    /** 参数或返回类型上源码写出的注解。 */
    private final List<AnnotationDefinition> annotations;
    /** {@code @param} 或 {@code @return} 注释；没有则为 {@code null}。 */
    private final String comment;
    /** 对象类型的字段；叶子类型为空列表。 */
    private final List<PropertyDefinition> properties;

    /**
     * @param name        参数名；返回值为 {@code null}
     * @param type        展示类型名
     * @param packageName 类型所在包
     * @param annotations 注解
     * @param comment     {@code @param} 或 {@code @return} 注释
     * @param properties  对象字段
     */
    public ParameterDefinition(String name, String type, String packageName,
                               List<AnnotationDefinition> annotations, String comment,
                               List<PropertyDefinition> properties) {
        this.name = name;
        this.type = type;
        this.packageName = packageName;
        this.annotations = CollectionUtils.unmodifiableList(annotations);
        this.comment = comment;
        this.properties = CollectionUtils.unmodifiableList(properties);
    }

    /** 参数名；返回值为 {@code null}。 */
    public String name() {
        return name;
    }

    /** 展示类型名，如 {@code String}、{@code List<User>}。 */
    public String type() {
        return type;
    }

    /** 类型所在包；基本类型为 {@code null}。 */
    public String packageName() {
        return packageName;
    }

    /** 参数或返回类型上源码写出的注解。 */
    public List<AnnotationDefinition> annotations() {
        return annotations;
    }

    /** {@code @param} 或 {@code @return} 注释；没有则为 {@code null}。 */
    public String comment() {
        return comment;
    }

    /** 对象类型的字段；叶子类型为空列表。 */
    public List<PropertyDefinition> properties() {
        return properties;
    }

}
