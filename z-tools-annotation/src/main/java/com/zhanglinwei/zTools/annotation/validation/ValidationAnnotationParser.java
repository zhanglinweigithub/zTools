package com.zhanglinwei.zTools.annotation.validation;

import com.zhanglinwei.zTools.annotation.lookup.AnnotationLookup;
import com.zhanglinwei.zTools.annotation.lookup.AnnotationType;
import com.zhanglinwei.zTools.annotation.lookup.Attr;
import com.zhanglinwei.zTools.annotation.model.AnnotationDefinition;
import com.zhanglinwei.zTools.annotation.model.ParameterDefinition;
import com.zhanglinwei.zTools.annotation.model.PropertyDefinition;

import java.util.List;
import java.util.Optional;

/**
 * 解析 Bean Validation / Hibernate Validator / Spring {@code @Validated}。
 * 只认注解是否存在以及源码写出的属性，不把 {@code @Size} 的 min/max 缺省成 0 / MAX。
 */
public final class ValidationAnnotationParser {

    public static final AnnotationType NOT_NULL = AnnotationType.of(
            "javax.validation.constraints.NotNull",
            "jakarta.validation.constraints.NotNull");
    public static final AnnotationType NOT_BLANK = AnnotationType.of(
            "javax.validation.constraints.NotBlank",
            "jakarta.validation.constraints.NotBlank",
            "org.hibernate.validator.constraints.NotBlank");
    public static final AnnotationType NOT_EMPTY = AnnotationType.of(
            "javax.validation.constraints.NotEmpty",
            "jakarta.validation.constraints.NotEmpty",
            "org.hibernate.validator.constraints.NotEmpty");
    public static final AnnotationType SIZE = AnnotationType.of(
            "javax.validation.constraints.Size",
            "jakarta.validation.constraints.Size");
    public static final AnnotationType MIN = AnnotationType.of(
            "javax.validation.constraints.Min",
            "jakarta.validation.constraints.Min");
    public static final AnnotationType MAX = AnnotationType.of(
            "javax.validation.constraints.Max",
            "jakarta.validation.constraints.Max");
    public static final AnnotationType PATTERN = AnnotationType.of(
            "javax.validation.constraints.Pattern",
            "jakarta.validation.constraints.Pattern");
    public static final AnnotationType VALID = AnnotationType.of(
            "javax.validation.Valid",
            "jakarta.validation.Valid");
    public static final AnnotationType VALIDATED = AnnotationType.of(
            "org.springframework.validation.annotation.Validated");

    /** 工具类，禁止实例化。 */
    private ValidationAnnotationParser() {}

    /**
     * 解析参数上的校验注解。
     *
     * @param parameter 参数定义
     * @return 校验约束；{@code parameter} 为 {@code null} 时返回全 false / null 的空结果
     */
    public static ValidationConstraints parse(ParameterDefinition parameter) {
        return parameter == null ? empty() : parse(parameter.annotations());
    }

    /**
     * 解析字段上的校验注解。
     *
     * @param property 字段定义
     * @return 校验约束；{@code property} 为 {@code null} 时返回全 false / null 的空结果
     */
    public static ValidationConstraints parse(PropertyDefinition property) {
        return property == null ? empty() : parse(property.annotations());
    }

    /**
     * 从注解列表抽出校验约束。未写出的数值 / 正则保持 {@code null}。
     *
     * @param annotations 注解列表
     * @return 校验约束
     */
    public static ValidationConstraints parse(List<AnnotationDefinition> annotations) {
        Optional<AnnotationDefinition> size = AnnotationLookup.find(annotations, SIZE);
        Optional<AnnotationDefinition> min = AnnotationLookup.find(annotations, MIN);
        Optional<AnnotationDefinition> max = AnnotationLookup.find(annotations, MAX);
        Optional<AnnotationDefinition> pattern = AnnotationLookup.find(annotations, PATTERN);
        AnnotationDefinition sizeAnn = size.isPresent() ? size.get() : null;
        // 布尔表示注解是否存在；min/max/regexp 只读源码写出的值，不补注解 default
        return new ValidationConstraints(
                AnnotationLookup.present(annotations, NOT_NULL),
                AnnotationLookup.present(annotations, NOT_BLANK),
                AnnotationLookup.present(annotations, NOT_EMPTY),
                AnnotationLookup.anyPresent(annotations, VALID, VALIDATED),
                sizeAnn != null,
                sizeAnn == null ? null : AnnotationLookup.integer(sizeAnn, "min"),
                sizeAnn == null ? null : AnnotationLookup.integer(sizeAnn, "max"),
                min.isPresent() ? AnnotationLookup.longValue(min.get(), Attr.VALUE) : null,
                max.isPresent() ? AnnotationLookup.longValue(max.get(), Attr.VALUE) : null,
                pattern.isPresent() ? AnnotationLookup.string(pattern.get(), "regexp") : null
        );
    }

    /**
     * 无注解时的空约束。
     *
     * @return 全 false、数值 / 正则为 {@code null} 的结果
     */
    private static ValidationConstraints empty() {
        return new ValidationConstraints(false, false, false, false, false, null, null, null, null, null);
    }
}
