package com.zhanglinwei.zTools.annotation.model;

import com.zhanglinwei.zTools.common.util.CollectionUtils;

import java.util.List;

/**
 * 方法定义。不合并 Mapping、不推断 required、不生成示例。
 */
public final class MethodDefinition {

    /** 方法名。 */
    private final String name;
    /** 所在类的包名。 */
    private final String packageName;
    /** 所在类的轻量引用。 */
    private final ClassRef containingClass;
    /** 方法上源码写出的注解。 */
    private final List<AnnotationDefinition> annotations;
    /** 方法参数。 */
    private final List<ParameterDefinition> parameters;
    /** 返回值；构造器没有返回类型时为 {@code null}。 */
    private final ParameterDefinition returns;
    /** 方法 JavaDoc；没有则为 {@code null}。 */
    private final CommentDefinition comment;

    /**
     * @param name            方法名
     * @param packageName     所在类的包名
     * @param containingClass 所在类引用
     * @param annotations     方法注解
     * @param parameters      方法参数
     * @param returns         返回值
     * @param comment         方法 JavaDoc
     */
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

    /** 方法名。 */
    public String name() {
        return name;
    }

    /** 所在类的包名。 */
    public String packageName() {
        return packageName;
    }

    /** 所在类的轻量引用。 */
    public ClassRef containingClass() {
        return containingClass;
    }

    /** 方法上源码写出的注解。 */
    public List<AnnotationDefinition> annotations() {
        return annotations;
    }

    /** 方法参数。 */
    public List<ParameterDefinition> parameters() {
        return parameters;
    }

    /** 返回值；构造器没有返回类型时为 {@code null}。 */
    public ParameterDefinition returns() {
        return returns;
    }

    /** 方法 JavaDoc；没有则为 {@code null}。 */
    public CommentDefinition comment() {
        return comment;
    }

}
