package com.zhanglinwei.zTools.annotation.model;

import com.zhanglinwei.zTools.common.util.CollectionUtils;

import java.util.List;

/**
 * 类定义。方法列表为本类声明的方法，不含构造器、不含继承而来的方法。
 */
public final class ClassDefinition {

    /** 简单类名。 */
    private final String name;
    /** 全限定名。 */
    private final String qualifiedName;
    /** 所在包；缺省包为 {@code null}。 */
    private final String packageName;
    /** 类上源码写出的注解。 */
    private final List<AnnotationDefinition> annotations;
    /** 类 JavaDoc；没有则为 {@code null}。 */
    private final CommentDefinition comment;
    /** 本类声明的方法。 */
    private final List<MethodDefinition> methods;

    /**
     * @param name          简单类名
     * @param qualifiedName 全限定名
     * @param packageName   所在包
     * @param annotations   类注解
     * @param comment       类 JavaDoc
     * @param methods       本类声明的方法
     */
    public ClassDefinition(String name, String qualifiedName, String packageName,
                           List<AnnotationDefinition> annotations, CommentDefinition comment,
                           List<MethodDefinition> methods) {
        this.name = name;
        this.qualifiedName = qualifiedName;
        this.packageName = packageName;
        this.annotations = CollectionUtils.unmodifiableList(annotations);
        this.comment = comment;
        this.methods = CollectionUtils.unmodifiableList(methods);
    }

    /** 简单类名。 */
    public String name() {
        return name;
    }

    /** 全限定名。 */
    public String qualifiedName() {
        return qualifiedName;
    }

    /** 所在包；缺省包为 {@code null}。 */
    public String packageName() {
        return packageName;
    }

    /** 类上源码写出的注解。 */
    public List<AnnotationDefinition> annotations() {
        return annotations;
    }

    /** 类 JavaDoc；没有则为 {@code null}。 */
    public CommentDefinition comment() {
        return comment;
    }

    /** 本类声明的方法。 */
    public List<MethodDefinition> methods() {
        return methods;
    }
}
