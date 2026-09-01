package com.zhanglinwei.zTools.annotation.model;

import java.util.List;

/**
 * 源码上的一个注解：全限定名 + 写出的属性。
 */
public final class AnnotationDefinition {

    private final String name;
    private final String qualifiedName;
    private final List<AttributeDefinition> attributes;

    public AnnotationDefinition(String name, String qualifiedName, List<AttributeDefinition> attributes) {
        this.name = name;
        this.qualifiedName = qualifiedName;
        this.attributes = Lists.copy(attributes);
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
