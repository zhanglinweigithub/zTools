package com.zhanglinwei.zTools.annotation.model;

import java.util.List;

/**
 * 对象类型上的一个字段，结构与参数相同：类型名、包、子字段。
 * {@link #cycle()} 为 true 时表示类型已在外层出现，不再展开 {@link #properties()}。
 */
public final class PropertyDefinition {

    private final String name;
    private final String type;
    private final String packageName;
    private final List<AnnotationDefinition> annotations;
    private final String comment;
    private final List<PropertyDefinition> properties;
    private final boolean cycle;

    public PropertyDefinition(String name, String type, String packageName,
                              List<AnnotationDefinition> annotations, String comment,
                              List<PropertyDefinition> properties) {
        this(name, type, packageName, annotations, comment, properties, false);
    }

    public PropertyDefinition(String name, String type, String packageName,
                              List<AnnotationDefinition> annotations, String comment,
                              List<PropertyDefinition> properties, boolean cycle) {
        this.name = name;
        this.type = type;
        this.packageName = packageName;
        this.annotations = Lists.copy(annotations);
        this.comment = comment;
        this.properties = Lists.copy(properties);
        this.cycle = cycle;
    }

    public String name() {
        return name;
    }

    public String type() {
        return type;
    }

    public String packageName() {
        return packageName;
    }

    public List<AnnotationDefinition> annotations() {
        return annotations;
    }

    /** 字段 JavaDoc 正文；没有则为 {@code null}。 */
    public String comment() {
        return comment;
    }

    public List<PropertyDefinition> properties() {
        return properties;
    }

    /** 字段类型已在外层解析过，子字段不再展开。 */
    public boolean cycle() {
        return cycle;
    }

}
