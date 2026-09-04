package com.zhanglinwei.zTools.annotation.model;

import com.zhanglinwei.zTools.common.util.CollectionUtils;

import java.util.List;

/**
 * 对象类型上的一个字段，结构与参数相同：类型名、包、子字段。
 * {@link #cycle()} 为 true 时表示类型已在外层出现，不再展开 {@link #properties()}。
 */
public final class PropertyDefinition {

    /** 字段名。 */
    private final String name;
    /** 展示类型名。 */
    private final String type;
    /** 类型所在包；基本类型为 {@code null}。 */
    private final String packageName;
    /** 字段上源码写出的注解。 */
    private final List<AnnotationDefinition> annotations;
    /** 字段 JavaDoc 正文；没有则为 {@code null}。 */
    private final String comment;
    /** 子字段；循环引用或叶子类型为空列表。 */
    private final List<PropertyDefinition> properties;
    /** 字段类型已在外层解析过，子字段不再展开。 */
    private final boolean cycle;
    /** 字段类型为枚举时的常量名；非枚举为空列表。 */
    private final List<String> enumConstants;

    /**
     * 非循环引用的字段。
     *
     * @param name        字段名
     * @param type        展示类型名
     * @param packageName 类型所在包
     * @param annotations 字段注解
     * @param comment     字段 JavaDoc
     * @param properties  子字段
     */
    public PropertyDefinition(String name, String type, String packageName,
                              List<AnnotationDefinition> annotations, String comment,
                              List<PropertyDefinition> properties) {
        this(name, type, packageName, annotations, comment, properties, false);
    }

    /**
     * @param name        字段名
     * @param type        展示类型名
     * @param packageName 类型所在包
     * @param annotations 字段注解
     * @param comment     字段 JavaDoc
     * @param properties  子字段
     * @param cycle       是否循环引用
     */
    public PropertyDefinition(String name, String type, String packageName,
                              List<AnnotationDefinition> annotations, String comment,
                              List<PropertyDefinition> properties, boolean cycle) {
        this(name, type, packageName, annotations, comment, properties, cycle, null);
    }

    /**
     * @param name          字段名
     * @param type          展示类型名
     * @param packageName   类型所在包
     * @param annotations   字段注解
     * @param comment       字段 JavaDoc
     * @param properties    子字段
     * @param cycle         是否循环引用
     * @param enumConstants 枚举常量名；非枚举为 {@code null} 或空
     */
    public PropertyDefinition(String name, String type, String packageName,
                              List<AnnotationDefinition> annotations, String comment,
                              List<PropertyDefinition> properties, boolean cycle,
                              List<String> enumConstants) {
        this.name = name;
        this.type = type;
        this.packageName = packageName;
        this.annotations = CollectionUtils.unmodifiableList(annotations);
        this.comment = comment;
        this.properties = CollectionUtils.unmodifiableList(properties);
        this.cycle = cycle;
        this.enumConstants = CollectionUtils.unmodifiableList(enumConstants);
    }

    /** 字段名。 */
    public String name() {
        return name;
    }

    /** 展示类型名。 */
    public String type() {
        return type;
    }

    /** 类型所在包；基本类型为 {@code null}。 */
    public String packageName() {
        return packageName;
    }

    /** 字段上源码写出的注解。 */
    public List<AnnotationDefinition> annotations() {
        return annotations;
    }

    /** 字段 JavaDoc 正文；没有则为 {@code null}。 */
    public String comment() {
        return comment;
    }

    /** 子字段；循环引用或叶子类型为空列表。 */
    public List<PropertyDefinition> properties() {
        return properties;
    }

    /** 字段类型已在外层解析过，子字段不再展开。 */
    public boolean cycle() {
        return cycle;
    }

    /** 字段类型为枚举时的常量名，声明顺序；非枚举为空列表。 */
    public List<String> enumConstants() {
        return enumConstants;
    }

}
