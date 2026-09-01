package com.zhanglinwei.zTools.annotation.lookup;

import com.intellij.psi.PsiAnnotation;
import com.zhanglinwei.zTools.annotation.model.AnnotationDefinition;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 一组互为别名的注解全限定名（例如 javax / jakarta，Fastjson 1 / 2）。
 * 匹配只看 {@link PsiAnnotation#getQualifiedName()}，不看源码文本。
 */
public final class AnnotationType {

    private final List<String> qualifiedNames;
    private final String shortName;

    private AnnotationType(String... qualifiedNames) {
        this.qualifiedNames = Collections.unmodifiableList(Arrays.asList(qualifiedNames));
        String first = qualifiedNames[0];
        int lastDot = first.lastIndexOf('.');
        this.shortName = lastDot < 0 ? first : first.substring(lastDot + 1);
    }

    /**
     * @param qualifiedNames 至少一个全限定名，顺序即查找优先级
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

    /** {@code annotation} 的全限定名是否落在本组内。 */
    public boolean matches(PsiAnnotation annotation) {
        return annotation != null && matches(annotation.getQualifiedName());
    }

    public boolean matches(AnnotationDefinition definition) {
        return definition != null && matches(definition.qualifiedName());
    }

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
