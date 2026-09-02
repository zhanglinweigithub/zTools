package com.zhanglinwei.zTools.annotation.model;

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

    /**
     * @param name          简单名
     * @param qualifiedName 全限定名
     * @param attributes    源码写出的属性
     */
    public AnnotationDefinition(String name, String qualifiedName, List<AttributeDefinition> attributes) {
        this.name = name;
        this.qualifiedName = qualifiedName;
        this.attributes = CollectionUtils.unmodifiableList(attributes);
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
}
