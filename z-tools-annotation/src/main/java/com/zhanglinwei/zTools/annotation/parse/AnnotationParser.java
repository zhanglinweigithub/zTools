package com.zhanglinwei.zTools.annotation.parse;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiArrayInitializerMemberValue;
import com.intellij.psi.PsiClassObjectAccessExpression;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiLiteral;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiModifierListOwner;
import com.intellij.psi.PsiNameValuePair;
import com.intellij.psi.PsiPrefixExpression;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiType;
import com.zhanglinwei.zTools.annotation.model.AnnotationDefinition;
import com.zhanglinwei.zTools.annotation.model.AttributeDefinition;
import com.zhanglinwei.zTools.annotation.lookup.Attr;
import com.zhanglinwei.zTools.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 读取元素上的注解及源码写出的属性，不含注解 default。
 */
public final class AnnotationParser {

    private AnnotationParser() {}

    public static List<AnnotationDefinition> of(PsiModifierListOwner owner) {
        if (owner == null) {
            return Collections.emptyList();
        }
        PsiModifierList modifiers = owner.getModifierList();
        if (modifiers == null) {
            return Collections.emptyList();
        }
        return of(modifiers.getAnnotations());
    }

    /** 类型上的 TYPE_USE 注解，例如返回类型前的 {@code @NotNull}。 */
    public static List<AnnotationDefinition> of(PsiType type) {
        return type == null ? Collections.<AnnotationDefinition>emptyList() : of(type.getAnnotations());
    }

    public static List<AnnotationDefinition> of(PsiAnnotation[] annotations) {
        if (annotations == null || annotations.length == 0) {
            return Collections.emptyList();
        }
        List<AnnotationDefinition> result = new ArrayList<AnnotationDefinition>(annotations.length);
        for (PsiAnnotation annotation : annotations) {
            result.add(of(annotation));
        }
        return Collections.unmodifiableList(result);
    }

    public static AnnotationDefinition of(PsiAnnotation annotation) {
        String qualifiedName = annotation.getQualifiedName();
        return new AnnotationDefinition(simpleName(annotation, qualifiedName), qualifiedName, attributes(annotation));
    }

    private static List<AttributeDefinition> attributes(PsiAnnotation annotation) {
        PsiNameValuePair[] pairs = annotation.getParameterList().getAttributes();
        if (pairs.length == 0) {
            return Collections.emptyList();
        }
        List<AttributeDefinition> attributes = new ArrayList<AttributeDefinition>(pairs.length);
        for (PsiNameValuePair pair : pairs) {
            attributes.add(attribute(pair));
        }
        return attributes;
    }

    private static AttributeDefinition attribute(PsiNameValuePair pair) {
        String name = pair.getName();
        if (StringUtils.isBlank(name)) {
            name = Attr.VALUE;
        }
        List<String> values = new ArrayList<String>();
        List<AnnotationDefinition> nested = new ArrayList<AnnotationDefinition>();
        PsiAnnotationMemberValue value = pair.getValue();
        if (value != null) {
            for (PsiAnnotationMemberValue item : flatten(value)) {
                if (item instanceof PsiAnnotation) {
                    nested.add(of((PsiAnnotation) item));
                } else {
                    String text = stringify(item);
                    if (text != null) {
                        values.add(text);
                    }
                }
            }
        }
        return new AttributeDefinition(name, values, nested);
    }

    private static List<PsiAnnotationMemberValue> flatten(PsiAnnotationMemberValue value) {
        if (value instanceof PsiArrayInitializerMemberValue) {
            PsiAnnotationMemberValue[] initializers = ((PsiArrayInitializerMemberValue) value).getInitializers();
            List<PsiAnnotationMemberValue> items = new ArrayList<PsiAnnotationMemberValue>(initializers.length);
            Collections.addAll(items, initializers);
            return items;
        }
        return Collections.singletonList(value);
    }

    private static String stringify(PsiAnnotationMemberValue value) {
        Object literal = literalValue(value);
        if (literal != null) {
            return String.valueOf(literal);
        }
        if (value instanceof PsiClassObjectAccessExpression) {
            PsiType type = ((PsiClassObjectAccessExpression) value).getOperand().getType();
            return type == null ? value.getText() : type.getCanonicalText();
        }
        if (value instanceof PsiReferenceExpression) {
            String name = ((PsiReferenceExpression) value).getReferenceName();
            if (name != null) {
                return name;
            }
        }
        String text = value == null ? null : value.getText();
        return StringUtils.isBlank(text) ? null : unquote(text);
    }

    private static Object literalValue(PsiAnnotationMemberValue value) {
        if (value instanceof PsiLiteral) {
            return ((PsiLiteral) value).getValue();
        }
        if (value instanceof PsiLiteralExpression) {
            return ((PsiLiteralExpression) value).getValue();
        }
        if (value instanceof PsiPrefixExpression) {
            PsiExpression operand = ((PsiPrefixExpression) value).getOperand();
            if (operand instanceof PsiLiteral) {
                return ((PsiLiteral) operand).getValue();
            }
        }
        return null;
    }

    private static String simpleName(PsiAnnotation annotation, String qualifiedName) {
        if (qualifiedName != null) {
            int dot = qualifiedName.lastIndexOf('.');
            return dot < 0 ? qualifiedName : qualifiedName.substring(dot + 1);
        }
        PsiJavaCodeReferenceElement reference = annotation.getNameReferenceElement();
        return reference == null ? null : reference.getReferenceName();
    }

    private static String unquote(String text) {
        String trimmed = text.trim();
        if (trimmed.length() >= 2 && trimmed.charAt(0) == '"' && trimmed.charAt(trimmed.length() - 1) == '"') {
            return trimmed.substring(1, trimmed.length() - 1);
        }
        return trimmed;
    }
}
