package com.zhanglinwei.zTools.annotation.model;

import com.intellij.psi.PsiAnnotation;
import com.zhanglinwei.zTools.common.util.CollectionUtils;

import java.util.List;

/**
 * 源码上的一个注解：全限定名 + 写出的属性。
 */
public final class AnnotationDefinition {

    /** 简单名，如 {@code GetMapping}。 */
    private final String name;
    /** 全限定名；解析不到时为 {@code null}。 */
    private final String qualifiedName;
    /** 源码里写出的属性；未写则为空列表。 */
    private final List<AttributeDefinition> attributes;
    /** 原始 {@link PsiAnnotation}；手工构造或无法对应 PSI 时为 {@code null}。 */
    private final PsiAnnotation delegate;

    /**
     * @param name          简单名
     * @param qualifiedName 全限定名
     * @param attributes    源码写出的属性
     */
    public AnnotationDefinition(String name, String qualifiedName, List<AttributeDefinition> attributes) {
        this(name, qualifiedName, attributes, null);
    }

    /**
     * @param name          简单名
     * @param qualifiedName 全限定名
     * @param attributes    源码写出的属性
     * @param delegate      原始 PSI 注解
     */
    public AnnotationDefinition(String name, String qualifiedName, List<AttributeDefinition> attributes,
                                PsiAnnotation delegate) {
        this.name = name;
        this.qualifiedName = qualifiedName;
        this.attributes = CollectionUtils.unmodifiableList(attributes);
        this.delegate = delegate;
    }

    /** 简单名，如 {@code GetMapping}。 */
    public String name() {
        return name;
    }

    /** 全限定名；解析不到时为 {@code null}。 */
    public String qualifiedName() {
        return qualifiedName;
    }

    /** 源码里写出的属性；未写则为空列表。 */
    public List<AttributeDefinition> attributes() {
        return attributes;
    }

    /** 原始 {@link PsiAnnotation}；手工构造或无法对应 PSI 时为 {@code null}。 */
    public PsiAnnotation delegate() {
        return delegate;
    }
}
