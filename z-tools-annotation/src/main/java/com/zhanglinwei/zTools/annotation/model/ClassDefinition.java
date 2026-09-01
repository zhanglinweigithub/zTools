package com.zhanglinwei.zTools.annotation.model;

import java.util.List;

/**
 * 类定义。方法列表为本类声明的方法，不含构造器、不含继承而来的方法。
 */
public final class ClassDefinition {

    private final String name;
    private final String qualifiedName;
    private final String packageName;
    private final List<AnnotationDefinition> annotations;
    private final CommentDefinition comment;
    private final List<MethodDefinition> methods;

    public ClassDefinition(String name, String qualifiedName, String packageName,
                           List<AnnotationDefinition> annotations, CommentDefinition comment,
                           List<MethodDefinition> methods) {
        this.name = name;
        this.qualifiedName = qualifiedName;
        this.packageName = packageName;
        this.annotations = Lists.copy(annotations);
        this.comment = comment;
        this.methods = Lists.copy(methods);
    }

    public String name() {
        return name;
    }

    public String qualifiedName() {
        return qualifiedName;
    }

    public String packageName() {
        return packageName;
    }

    public List<AnnotationDefinition> annotations() {
        return annotations;
    }

    public CommentDefinition comment() {
        return comment;
    }

    public List<MethodDefinition> methods() {
        return methods;
    }
}
