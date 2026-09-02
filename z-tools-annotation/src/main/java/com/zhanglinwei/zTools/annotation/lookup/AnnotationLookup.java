package com.zhanglinwei.zTools.annotation.lookup;

import com.zhanglinwei.zTools.annotation.model.AnnotationDefinition;
import com.zhanglinwei.zTools.annotation.model.AttributeDefinition;
import com.zhanglinwei.zTools.common.constant.StringPool;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 在已解析的 {@link AnnotationDefinition} 上按 FQN 查找，并读取源码写出的属性。
 * 属性未写时返回 {@code null}，不补注解 default。
 */
public final class AnnotationLookup {

    /** 工具类，禁止实例化。 */
    private AnnotationLookup() {}

    /**
     * 按全限定名查找第一个匹配的注解。
     *
     * @param annotations 注解列表
     * @param type        目标注解类型
     * @return 命中的注解；没有则为 empty
     */
    public static Optional<AnnotationDefinition> find(List<AnnotationDefinition> annotations, AnnotationType type) {
        if (annotations == null || type == null) {
            return Optional.empty();
        }
        for (AnnotationDefinition annotation : annotations) {
            if (type.matches(annotation)) {
                return Optional.of(annotation);
            }
        }
        return Optional.empty();
    }

    /**
     * 按类型数组顺序查找第一个命中的注解。
     *
     * @param annotations 注解列表
     * @param types       候选注解类型，靠前的优先
     * @return 命中的注解；没有则为 empty
     */
    public static Optional<AnnotationDefinition> first(List<AnnotationDefinition> annotations, AnnotationType... types) {
        if (types == null) {
            return Optional.empty();
        }
        for (AnnotationType type : types) {
            Optional<AnnotationDefinition> found = find(annotations, type);
            if (found.isPresent()) {
                return found;
            }
        }
        return Optional.empty();
    }

    /**
     * 是否存在指定类型的注解。
     *
     * @param annotations 注解列表
     * @param type        目标注解类型
     * @return 存在则为 {@code true}
     */
    public static boolean present(List<AnnotationDefinition> annotations, AnnotationType type) {
        return find(annotations, type).isPresent();
    }

    /**
     * 是否存在任一指定类型的注解。
     *
     * @param annotations 注解列表
     * @param types       候选注解类型
     * @return 存在任一则为 {@code true}
     */
    public static boolean anyPresent(List<AnnotationDefinition> annotations, AnnotationType... types) {
        return first(annotations, types).isPresent();
    }

    /**
     * 按名字顺序取第一个已写出的属性的全部值；未写则为 {@code null}。
     *
     * @param annotation 注解定义
     * @param names      候选属性名，靠前的优先
     * @return 属性值列表
     */
    public static List<String> strings(AnnotationDefinition annotation, String... names) {
        AttributeDefinition attribute = attribute(annotation, names);
        return attribute == null ? null : attribute.values();
    }

    /**
     * 按名字顺序取第一个已写出的属性的第一个值；未写则为 {@code null}。
     *
     * @param annotation 注解定义
     * @param names      候选属性名，靠前的优先
     * @return 第一个属性值
     */
    public static String string(AnnotationDefinition annotation, String... names) {
        List<String> values = strings(annotation, names);
        return values == null || values.isEmpty() ? null : values.get(0);
    }

    /**
     * 布尔属性；未写或无法识别则为 {@code null}。
     *
     * @param annotation 注解定义
     * @param name       属性名
     * @return {@code true} / {@code false}；无法识别则为 {@code null}
     */
    public static Boolean bool(AnnotationDefinition annotation, String name) {
        String value = string(annotation, name);
        if (StringPool.TRUE.equalsIgnoreCase(value)) {
            return Boolean.TRUE;
        }
        if (StringPool.FALSE.equalsIgnoreCase(value)) {
            return Boolean.FALSE;
        }
        return null;
    }

    /**
     * 整型属性；未写或无法解析则为 {@code null}。
     *
     * @param annotation 注解定义
     * @param name       属性名
     * @return 整数值
     */
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

    /**
     * 长整型属性；未写或无法解析则为 {@code null}。
     *
     * @param annotation 注解定义
     * @param name       属性名
     * @return 长整数值
     */
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

    /**
     * 嵌套注解属性，例如 {@code schema = @Schema(...)}。
     *
     * @param annotation 外层注解
     * @param names      候选属性名
     * @return 嵌套注解列表；属性未写则为 {@code null}
     */
    public static List<AnnotationDefinition> nested(AnnotationDefinition annotation, String... names) {
        AttributeDefinition attribute = attribute(annotation, names);
        if (attribute == null) {
            return null;
        }
        List<AnnotationDefinition> nested = attribute.annotations();
        return nested == null ? Collections.<AnnotationDefinition>emptyList() : nested;
    }

    /**
     * 按名字顺序取第一个已写出的属性。
     *
     * @param annotation 注解定义
     * @param names      候选属性名
     * @return 属性定义；未写则为 {@code null}
     */
    private static AttributeDefinition attribute(AnnotationDefinition annotation, String... names) {
        if (annotation == null || names == null) {
            return null;
        }
        List<AttributeDefinition> attributes = annotation.attributes();
        for (String name : names) {
            for (AttributeDefinition attribute : attributes) {
                if (name.equals(attribute.name())) {
                    return attribute;
                }
            }
        }
        return null;
    }
}
