package com.zhanglinwei.zTools.annotation.model;

import com.zhanglinwei.zTools.common.util.CollectionUtils;

import java.util.List;

/**
 * 方法参数或返回值：类型名、包、注解、注释；对象类型则带 {@link #properties()}。
 * 返回值没有参数名，{@link #name()} 为 {@code null}。
 */
public final class ParameterDefinition {

    private final String name;
    private final String type;
    private final String packageName;
    private final List<AnnotationDefinition> annotations;
    private final String comment;
    private final List<PropertyDefinition> properties;

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
