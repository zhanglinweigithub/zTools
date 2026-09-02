package com.zhanglinwei.zTools.annotation.lookup;

import com.intellij.psi.PsiAnnotation;
import com.zhanglinwei.zTools.annotation.model.AnnotationDefinition;
import com.zhanglinwei.zTools.common.constant.CharacterPool;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 一组互为别名的注解全限定名（例如 javax / jakarta，Fastjson 1 / 2）。
 * 匹配只看 {@link PsiAnnotation#getQualifiedName()}，不看源码文本。
 */
public final class AnnotationType {

    /** 本组全部全限定名，不可变。 */
    private final List<String> qualifiedNames;
    /** 简单名，取自第一个全限定名。 */
    private final String shortName;

    /**
     * @param qualifiedNames 至少一个全限定名，顺序即查找优先级
     */
    private AnnotationType(String... qualifiedNames) {
        this.qualifiedNames = Collections.unmodifiableList(Arrays.asList(qualifiedNames));
        String first = qualifiedNames[0];
        int lastDot = first.lastIndexOf(CharacterPool.DOT);
        this.shortName = lastDot < 0 ? first : first.substring(lastDot + 1);
    }

    /**
     * 按全限定名创建注解类型。
     *
     * @param qualifiedNames 至少一个全限定名，顺序即查找优先级
     * @return 注解类型
     */
    public static AnnotationType of(String... qualifiedNames) {
        if (qualifiedNames == null || qualifiedNames.length == 0) {
            throw new IllegalArgumentException("at least one qualified name is required");
        }
        return new AnnotationType(qualifiedNames);
    }

    /** 本组全部全限定名，不可变。 */
    public List<String> qualifiedNames() {
        return qualifiedNames;
    }

    /** 简单名，如 {@code GetMapping}。取自第一个全限定名。 */
    public String shortName() {
        return shortName;
    }

    /**
     * {@code annotation} 的全限定名是否落在本组内。
     *
     * @param annotation PSI 注解
     * @return 匹配则为 {@code true}
     */
    public boolean matches(PsiAnnotation annotation) {
        return annotation != null && matches(annotation.getQualifiedName());
    }

    /**
     * 定义对象的全限定名是否落在本组内。
     *
     * @param definition 注解定义
     * @return 匹配则为 {@code true}
     */
    public boolean matches(AnnotationDefinition definition) {
        return definition != null && matches(definition.qualifiedName());
    }

    /**
     * 全限定名是否落在本组内。只做字符串相等比较。
     *
     * @param qualifiedName 全限定名
     * @return 匹配则为 {@code true}
     */
    public boolean matches(String qualifiedName) {
        if (qualifiedName == null) {
            return false;
        }
        for (String name : qualifiedNames) {
            if (qualifiedName.equals(name)) {
                return true;
            }
        }
        return false;
    }
}
