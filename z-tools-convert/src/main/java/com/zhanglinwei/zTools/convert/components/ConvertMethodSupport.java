package com.zhanglinwei.zTools.convert.components;

import com.intellij.psi.PsiArrayType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PropertyUtil;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtil;
import com.intellij.psi.util.TypeConversionUtil;
import com.zhanglinwei.zTools.common.util.TypeUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 判断当前方法能否填充转换体，并收集同名、类型兼容的 getter/setter。
 */
public final class ConvertMethodSupport {

    private ConvertMethodSupport() {}

    /**
     * 光标所在方法（含方法体内、签名上）。
     *
     * @param element 当前 PSI 元素
     * @return 所属方法；不在方法内则为 {@code null}
     */
    public static PsiMethod methodAt(PsiElement element) {
        return PsiTreeUtil.getParentOfType(element, PsiMethod.class, false);
    }

    /**
     * 是否适合生成：具体方法、单个 JavaBean 入参、JavaBean 返回类型（非 void/集合/接口/枚举/record）。
     *
     * @param method 待判断方法
     * @return 可生成则为 {@code true}
     */
    public static boolean isSupported(PsiMethod method) {
        return targetClass(method) != null && sourceClass(method) != null;
    }

    /**
     * 返回类型对应的具体类。
     *
     * @param method 转换方法
     * @return 目标类；不支持则为 {@code null}
     */
    public static PsiClass targetClass(PsiMethod method) {
        if (method == null || method.isConstructor() || method.getBody() == null) {
            return null;
        }
        return beanClass(method.getReturnType());
    }

    /**
     * 唯一入参对应的具体类。
     *
     * @param method 转换方法
     * @return 源类；不支持则为 {@code null}
     */
    public static PsiClass sourceClass(PsiMethod method) {
        if (method == null) {
            return null;
        }
        PsiParameter[] parameters = method.getParameterList().getParameters();
        if (parameters.length != 1) {
            return null;
        }
        return beanClass(parameters[0].getType());
    }

    /**
     * 入参名。
     *
     * @param method 转换方法
     * @return 参数名；无法解析则为 {@code null}
     */
    public static String sourceVar(PsiMethod method) {
        if (method == null) {
            return null;
        }
        PsiParameter[] parameters = method.getParameterList().getParameters();
        if (parameters.length != 1) {
            return null;
        }
        return parameters[0].getName();
    }

    /**
     * 从目标类往父类扫描 setter，源类上找同名 getter，类型可赋值才拷贝。
     * 源上没有对应属性或类型不兼容时记为 {@link ConvertField.Kind#UNMATCHED}，生成无参 setter 占位。
     * 子类同名 setter 优先。
     *
     * @param method 转换方法
     * @return 字段列表，可能为空列表
     */
    public static List<ConvertField> matchingFields(PsiMethod method) {
        List<ConvertField> fields = new ArrayList<ConvertField>();
        PsiClass target = targetClass(method);
        PsiClass source = sourceClass(method);
        if (target == null || source == null) {
            return fields;
        }

        Set<String> seen = new LinkedHashSet<String>();
        for (PsiClass current = target; current != null && !isJavaLangObject(current); current = current.getSuperClass()) {
            for (PsiMethod setter : current.getMethods()) {
                if (!isInstanceSetter(setter)) {
                    continue;
                }
                String propertyName = propertyNameFromSetter(setter.getName());
                if (propertyName == null || !seen.add(propertyName)) {
                    continue;
                }
                PsiMethod getter = PropertyUtil.findPropertyGetter(source, propertyName, false, true);
                if (getter == null || getter.hasModifierProperty(PsiModifier.STATIC)) {
                    fields.add(new ConvertField(null, setter.getName(), ConvertField.Kind.UNMATCHED));
                    continue;
                }
                PsiType getterType = getter.getReturnType();
                PsiType setterType = setter.getParameterList().getParameters()[0].getType();
                if (getterType == null || setterType == null || !TypeConversionUtil.isAssignable(setterType, getterType)) {
                    fields.add(new ConvertField(null, setter.getName(), ConvertField.Kind.UNMATCHED));
                    continue;
                }
                fields.add(new ConvertField(getter.getName(), setter.getName(), kindOf(getterType)));
            }
        }
        return fields;
    }

    private static PsiClass beanClass(PsiType type) {
        if (type == null || TypeUtils.isVoidType(type) || TypeUtils.isPrimitive(type)) {
            return null;
        }
        if (TypeUtils.isCollectionType(type) || TypeUtils.isMapType(type) || type instanceof PsiArrayType) {
            return null;
        }
        PsiClass psiClass = PsiUtil.resolveClassInClassTypeOnly(type);
        if (psiClass == null || psiClass.isInterface() || psiClass.isEnum() || psiClass.isAnnotationType()) {
            return null;
        }
        if (psiClass.getRecordComponents().length > 0) {
            return null;
        }
        String qualifiedName = psiClass.getQualifiedName();
        if (qualifiedName == null || isJdkType(qualifiedName)) {
            return null;
        }
        return psiClass;
    }

    private static boolean isJavaLangObject(PsiClass psiClass) {
        return psiClass != null && "java.lang.Object".equals(psiClass.getQualifiedName());
    }

    private static boolean isJdkType(String qualifiedName) {
        return qualifiedName.startsWith("java.")
                || qualifiedName.startsWith("javax.")
                || qualifiedName.startsWith("jakarta.");
    }

    private static boolean isInstanceSetter(PsiMethod method) {
        if (method.hasModifierProperty(PsiModifier.STATIC) || method.isConstructor()) {
            return false;
        }
        String name = method.getName();
        if (name.length() < 4 || !name.startsWith("set")) {
            return false;
        }
        return method.getParameterList().getParametersCount() == 1;
    }

    /**
     * {@code setName} → {@code name}；{@code setURL} → {@code URL}（JavaBeans）。
     */
    static String propertyNameFromSetter(String setterName) {
        String suffix = setterName.substring(3);
        if (suffix.isEmpty()) {
            return null;
        }
        if (suffix.length() > 1 && Character.isUpperCase(suffix.charAt(0)) && Character.isUpperCase(suffix.charAt(1))) {
            return suffix;
        }
        return Character.toLowerCase(suffix.charAt(0)) + suffix.substring(1);
    }

    private static ConvertField.Kind kindOf(PsiType type) {
        if (TypeUtils.isPrimitive(type)) {
            return ConvertField.Kind.PRIMITIVE;
        }
        if (type instanceof PsiArrayType) {
            return ConvertField.Kind.ARRAY;
        }
        if (TypeUtils.isCollectionType(type) || TypeUtils.isMapType(type)) {
            return ConvertField.Kind.COLLECTION;
        }
        return ConvertField.Kind.OBJECT;
    }
}
