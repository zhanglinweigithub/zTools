package com.zhanglinwei.zTools.annotation.model;

import com.zhanglinwei.zTools.common.util.CollectionUtils;

import java.util.List;

/**
 * 注解上源码写出的一个属性。未写出的 default 不会出现。
 */
public final class AttributeDefinition {

    /** 属性名；简写 {@code @GetMapping("/x")} 对应 {@code value}。 */
    private final String name;
    /** 标量 / 枚举 / Class / 数组展开后的值。 */
    private final List<String> values;
    /** 嵌套注解，例如 {@code schema = @Schema(...)}。 */
    private final List<AnnotationDefinition> annotations;

    /**
     * @param name        属性名
     * @param values      标量值
     * @param annotations 嵌套注解
     */
    public AttributeDefinition(String name, List<String> values, List<AnnotationDefinition> annotations) {
        this.name = name;
        this.values = CollectionUtils.unmodifiableList(values);
        this.annotations = CollectionUtils.unmodifiableList(annotations);
    }

    /** 属性名；简写 {@code @GetMapping("/x")} 对应 {@code value}。 */
    public String name() {
        return name;
    }

    /** 标量 / 枚举 / Class / 数组展开后的值。 */
    public List<String> values() {
        return values;
    }

    /** 嵌套注解，例如 {@code schema = @Schema(...)}。 */
    public List<AnnotationDefinition> annotations() {
        return annotations;
    }

}
