package com.zhanglinwei.zTools.annotation.parse;

import com.intellij.psi.PsiArrayType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypeParameter;
import com.intellij.psi.util.PsiUtil;
import com.zhanglinwei.zTools.annotation.model.PropertyDefinition;
import com.zhanglinwei.zTools.common.constant.CharacterPool;
import com.zhanglinwei.zTools.common.util.StringUtils;
import com.zhanglinwei.zTools.common.util.TypeUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 从 {@link PsiType} 取出类型名、包、对象字段。不推断 required / example。
 */
public final class TypeParser {

    private static final String SERIAL_VERSION_UID = "serialVersionUID";

    private TypeParser() {}

    public static String name(PsiType type) {
        return type == null ? null : type.getPresentableText();
    }

    public static String packageName(PsiType type) {
        if (type == null) {
            return null;
        }
        PsiClass psiClass = PsiUtil.resolveClassInType(unwrap(type));
        if (psiClass == null) {
            return null;
        }
        return packageOf(psiClass.getQualifiedName());
    }

    public static List<PropertyDefinition> properties(PsiType type) {
        if (type == null) {
            return Collections.emptyList();
        }
        return properties(type, new LinkedHashSet<String>(), genericsOf(type));
    }

    private static List<PropertyDefinition> properties(PsiType type, Set<String> visiting, Map<String, PsiType> generics) {
        if (type == null || isLeaf(type)) {
            return Collections.emptyList();
        }
        PsiType resolved = resolveGeneric(type, generics);
        PsiType real = unwrap(resolved);
        if (isLeaf(real)) {
            return Collections.emptyList();
        }
        PsiClass psiClass = PsiUtil.resolveClassInType(real);
        if (psiClass == null || psiClass.isEnum() || psiClass.isAnnotationType()) {
            return Collections.emptyList();
        }
        String qualifiedName = psiClass.getQualifiedName();
        if (qualifiedName != null && !visiting.add(qualifiedName)) {
            return Collections.emptyList();
        }
        Map<String, PsiType> childGenerics = mergeGenerics(real, generics);
        List<PropertyDefinition> result = new ArrayList<PropertyDefinition>();
        for (PsiField field : psiClass.getAllFields()) {
            if (isStatic(field) || SERIAL_VERSION_UID.equals(field.getName())) {
                continue;
            }
            result.add(fromField(field, visiting, childGenerics));
        }
        if (qualifiedName != null) {
            visiting.remove(qualifiedName);
        }
        return result;
    }

    private static PropertyDefinition fromField(PsiField field, Set<String> visiting, Map<String, PsiType> generics) {
        PsiType fieldType = resolveGeneric(field.getType(), generics);
        boolean cycle = cyclic(fieldType, visiting);
        List<PropertyDefinition> children = cycle
                ? Collections.emptyList()
                : properties(fieldType, visiting, mergeGenerics(fieldType, generics));
        return new PropertyDefinition(
                field.getName(),
                name(fieldType),
                packageName(fieldType),
                AnnotationParser.of(field),
                Comments.text(field),
                children,
                cycle
        );
    }

    /** 解开集合 / 数组后的真实类型已在当前解析链上，视为循环引用。 */
    private static boolean cyclic(PsiType type, Set<String> visiting) {
        if (type == null || visiting == null || visiting.isEmpty()) {
            return false;
        }
        PsiClass psiClass = PsiUtil.resolveClassInType(unwrap(type));
        if (psiClass == null) {
            return false;
        }
        String qualifiedName = psiClass.getQualifiedName();
        return qualifiedName != null && visiting.contains(qualifiedName);
    }

    private static boolean isLeaf(PsiType type) {
        PsiType real = unwrap(type);
        return TypeUtils.isPrimitive(real)
                || TypeUtils.isNormalType(real)
                || TypeUtils.isEnum(real)
                || TypeUtils.isMapType(real)
                || TypeUtils.isMultipartType(real)
                || TypeUtils.isHttpType(real)
                || TypeUtils.isServletType(real)
                || TypeUtils.isIOType(real);
    }

    private static PsiType unwrap(PsiType type) {
        if (type == null) {
            return null;
        }
        TypeUtils.NestedInfo nested = TypeUtils.deepExtractIterableType(type);
        return nested.getRealType() == null ? type : nested.getRealType();
    }

    private static Map<String, PsiType> genericsOf(PsiType type) {
        return mergeGenerics(type, Collections.<String, PsiType>emptyMap());
    }

    private static Map<String, PsiType> mergeGenerics(PsiType type, Map<String, PsiType> parent) {
        Map<String, PsiType> merged = new HashMap<String, PsiType>(parent);
        PsiType resolved = type instanceof PsiArrayType ? ((PsiArrayType) type).getComponentType() : type;
        if (!(resolved instanceof PsiClassType)) {
            return merged;
        }
        PsiClassType classType = (PsiClassType) resolved;
        PsiClass psiClass = classType.resolve();
        if (psiClass == null) {
            return merged;
        }
        PsiTypeParameter[] parameters = psiClass.getTypeParameters();
        PsiType[] actuals = classType.getParameters();
        int len = Math.min(parameters.length, actuals.length);
        for (int i = 0; i < len; i++) {
            merged.put(parameters[i].getName(), resolveGeneric(actuals[i], parent));
        }
        return merged;
    }

    private static PsiType resolveGeneric(PsiType type, Map<String, PsiType> generics) {
        if (type == null || generics.isEmpty()) {
            return type;
        }
        PsiType mapped = generics.get(type.getPresentableText());
        return mapped == null ? type : mapped;
    }

    static String packageOf(String qualifiedName) {
        if (StringUtils.isBlank(qualifiedName)) {
            return null;
        }
        int dot = qualifiedName.lastIndexOf(CharacterPool.DOT);
        return dot < 0 ? null : qualifiedName.substring(0, dot);
    }

    private static boolean isStatic(PsiField field) {
        PsiModifierList modifiers = field.getModifierList();
        return modifiers != null && modifiers.hasModifierProperty(PsiModifier.STATIC);
    }
}
