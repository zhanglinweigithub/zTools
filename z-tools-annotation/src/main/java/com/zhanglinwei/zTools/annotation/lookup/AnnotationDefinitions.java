package com.zhanglinwei.zTools.annotation.lookup;

import com.zhanglinwei.zTools.annotation.model.AnnotationDefinition;
import com.zhanglinwei.zTools.annotation.model.AttributeDefinition;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 在已解析的 {@link AnnotationDefinition} 上按 FQN 查找，并读取源码写出的属性。
 * 属性未写时返回 {@code null}，不补注解 default。
 */
public final class AnnotationDefinitions {

    private AnnotationDefinitions() {}

    public static Optional<AnnotationDefinition> find(List<AnnotationDefinition> annotations, AnnotationType type) {
        if (annotations == null || type == null) {
            return Optional.empty();
        }
        for (int i = 0; i < annotations.size(); i++) {
            AnnotationDefinition annotation = annotations.get(i);
            if (type.matches(annotation)) {
                return Optional.of(annotation);
            }
        }
        return Optional.empty();
    }

    public static Optional<AnnotationDefinition> first(List<AnnotationDefinition> annotations, AnnotationType... types) {
        if (types == null) {
            return Optional.empty();
        }
        for (int i = 0; i < types.length; i++) {
            Optional<AnnotationDefinition> found = find(annotations, types[i]);
            if (found.isPresent()) {
                return found;
            }
        }
        return Optional.empty();
    }

    public static boolean present(List<AnnotationDefinition> annotations, AnnotationType type) {
        return find(annotations, type).isPresent();
    }

    public static boolean anyPresent(List<AnnotationDefinition> annotations, AnnotationType... types) {
        return first(annotations, types).isPresent();
    }

    /** 按名字顺序取第一个已写出的属性的全部值；未写则为 {@code null}。 */
    public static List<String> strings(AnnotationDefinition annotation, String... names) {
        AttributeDefinition attribute = attribute(annotation, names);
        return attribute == null ? null : attribute.values();
    }

    /** 按名字顺序取第一个已写出的属性的第一个值；未写则为 {@code null}。 */
    public static String string(AnnotationDefinition annotation, String... names) {
        List<String> values = strings(annotation, names);
        return values == null || values.isEmpty() ? null : values.get(0);
    }

    /** 布尔属性；未写或无法识别则为 {@code null}。 */
    public static Boolean bool(AnnotationDefinition annotation, String name) {
        String value = string(annotation, name);
        if ("true".equalsIgnoreCase(value)) {
            return Boolean.TRUE;
        }
        if ("false".equalsIgnoreCase(value)) {
            return Boolean.FALSE;
        }
        return null;
    }

    public static Integer integer(AnnotationDefinition annotation, String name) {
        String value = string(annotation, name);
        if (value == null) {
            return null;
        }
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    public static Long longValue(AnnotationDefinition annotation, String name) {
        String value = string(annotation, name);
        if (value == null) {
            return null;
        }
        try {
            return Long.valueOf(value);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    public static List<AnnotationDefinition> nested(AnnotationDefinition annotation, String... names) {
        AttributeDefinition attribute = attribute(annotation, names);
        if (attribute == null) {
            return null;
        }
        List<AnnotationDefinition> nested = attribute.annotations();
        return nested == null ? Collections.<AnnotationDefinition>emptyList() : nested;
    }

    private static AttributeDefinition attribute(AnnotationDefinition annotation, String... names) {
        if (annotation == null || names == null) {
            return null;
        }
        List<AttributeDefinition> attributes = annotation.attributes();
        for (int i = 0; i < names.length; i++) {
            String name = names[i];
            for (int j = 0; j < attributes.size(); j++) {
                AttributeDefinition attribute = attributes.get(j);
                if (name.equals(attribute.name())) {
                    return attribute;
                }
            }
        }
        return null;
    }
}
