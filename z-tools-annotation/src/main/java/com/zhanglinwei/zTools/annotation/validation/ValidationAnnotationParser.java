package com.zhanglinwei.zTools.annotation.validation;

import com.zhanglinwei.zTools.annotation.lookup.AnnotationDefinitions;
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

    private ValidationAnnotationParser() {}

    public static ValidationConstraints parse(ParameterDefinition parameter) {
        return parameter == null ? empty() : parse(parameter.annotations());
    }

    public static ValidationConstraints parse(PropertyDefinition property) {
        return property == null ? empty() : parse(property.annotations());
    }

    public static ValidationConstraints parse(List<AnnotationDefinition> annotations) {
        Optional<AnnotationDefinition> size = AnnotationDefinitions.find(annotations, SIZE);
        Optional<AnnotationDefinition> min = AnnotationDefinitions.find(annotations, MIN);
        Optional<AnnotationDefinition> max = AnnotationDefinitions.find(annotations, MAX);
        Optional<AnnotationDefinition> pattern = AnnotationDefinitions.find(annotations, PATTERN);
        AnnotationDefinition sizeAnn = size.isPresent() ? size.get() : null;
        return new ValidationConstraints(
                AnnotationDefinitions.present(annotations, NOT_NULL),
                AnnotationDefinitions.present(annotations, NOT_BLANK),
                AnnotationDefinitions.present(annotations, NOT_EMPTY),
                AnnotationDefinitions.anyPresent(annotations, VALID, VALIDATED),
                sizeAnn != null,
                sizeAnn == null ? null : AnnotationDefinitions.integer(sizeAnn, "min"),
                sizeAnn == null ? null : AnnotationDefinitions.integer(sizeAnn, "max"),
                min.isPresent() ? AnnotationDefinitions.longValue(min.get(), Attr.VALUE) : null,
                max.isPresent() ? AnnotationDefinitions.longValue(max.get(), Attr.VALUE) : null,
                pattern.isPresent() ? AnnotationDefinitions.string(pattern.get(), "regexp") : null
        );
    }

    private static ValidationConstraints empty() {
        return new ValidationConstraints(false, false, false, false, false, null, null, null, null, null);
    }
}
