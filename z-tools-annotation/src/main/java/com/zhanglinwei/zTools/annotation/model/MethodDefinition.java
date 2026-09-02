package com.zhanglinwei.zTools.annotation.model;

import com.zhanglinwei.zTools.common.util.CollectionUtils;

import java.util.List;

/**
 * 方法定义。不合并 Mapping、不推断 required、不生成示例。
 */
public final class MethodDefinition {

    private final String name;
    private final String packageName;
    private final ClassRef containingClass;
    private final List<AnnotationDefinition> annotations;
    private final List<ParameterDefinition> parameters;
    private final ParameterDefinition returns;
    private final CommentDefinition comment;

    public MethodDefinition(String name, String packageName, ClassRef containingClass,
                            List<AnnotationDefinition> annotations, List<ParameterDefinition> parameters,
                            ParameterDefinition returns, CommentDefinition comment) {
        this.name = name;
        this.packageName = packageName;
        this.containingClass = containingClass;
        this.annotations = CollectionUtils.unmodifiableList(annotations);
        this.parameters = CollectionUtils.unmodifiableList(parameters);
        this.returns = returns;
        this.comment = comment;
    }

    public String name() {
        return name;
    }

    public String packageName() {
        return packageName;
    }

    public ClassRef containingClass() {
        return containingClass;
    }

    public List<AnnotationDefinition> annotations() {
        return annotations;
    }

    public List<ParameterDefinition> parameters() {
        return parameters;
    }

    /** 返回值；构造器没有返回类型时为 {@code null}。 */
    public ParameterDefinition returns() {
        return returns;
    }

    public CommentDefinition comment() {
        return comment;
    }

}
