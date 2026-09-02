package com.zhanglinwei.zTools.annotation.parse;

import com.intellij.psi.*;
import com.zhanglinwei.zTools.annotation.model.ClassDefinition;
import com.zhanglinwei.zTools.annotation.model.ClassRef;
import com.zhanglinwei.zTools.annotation.model.MethodDefinition;
import com.zhanglinwei.zTools.annotation.model.ParameterDefinition;
import com.zhanglinwei.zTools.common.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 从 PSI 抽出类 / 方法定义，不做接口文档合成。
 */
public final class SourceParser {

    private SourceParser() {}

    public static ClassDefinition parseClass(PsiClass psiClass, boolean parseMethod) {
        if (psiClass == null) {
            return null;
        }
        List<MethodDefinition> methods = !parseMethod ? new ArrayList<>() : Arrays.stream(psiClass.getMethods())
                .filter(method -> !method.isConstructor())
                .filter(method -> psiClass == method.getContainingClass())
                .map(SourceParser::parseMethod)
                .collect(Collectors.toList());

        return new ClassDefinition(
                psiClass.getName(),
                psiClass.getQualifiedName(),
                packageName(psiClass),
                AnnotationParser.of(psiClass),
                Comments.of(psiClass),
                methods
        );
    }

    public static MethodDefinition parseMethod(PsiMethod method) {
        if (method == null) {
            return null;
        }
        PsiClass containing = method.getContainingClass();
        String packageName = packageName(containing);
        Map<String, String> paramComments = Comments.params(method);
        List<ParameterDefinition> parameters = new ArrayList<ParameterDefinition>();
        for (PsiParameter parameter : method.getParameterList().getParameters()) {
            parameters.add(parseParameter(parameter, paramComments.get(parameter.getName())));
        }
        return new MethodDefinition(
                method.getName(),
                packageName,
                classRef(containing),
                AnnotationParser.of(method),
                parameters,
                parseReturn(method),
                Comments.of(method)
        );
    }

    private static ParameterDefinition parseReturn(PsiMethod method) {
        PsiType returnType = method.getReturnType();
        if (returnType == null) {
            return null;
        }
        return new ParameterDefinition(
                null,
                TypeParser.name(returnType),
                TypeParser.packageName(returnType),
                AnnotationParser.of(returnType),
                Comments.returnComment(method),
                TypeParser.properties(returnType)
        );
    }

    public static ParameterDefinition parseParameter(PsiParameter parameter, String comment) {
        if (parameter == null) {
            return null;
        }
        PsiType type = parameter.getType();
        return new ParameterDefinition(
                parameter.getName(),
                TypeParser.name(type),
                TypeParser.packageName(type),
                AnnotationParser.of(parameter),
                StringUtils.isBlank(comment) ? null : comment,
                TypeParser.properties(type)
        );
    }

    private static ClassRef classRef(PsiClass psiClass) {
        if (psiClass == null) {
            return null;
        }
        return new ClassRef(psiClass.getName(), psiClass.getQualifiedName(), packageName(psiClass));
    }

    private static String packageName(PsiClass psiClass) {
        if (psiClass == null) {
            return null;
        }
        String fromQualified = TypeParser.packageOf(psiClass.getQualifiedName());
        if (fromQualified != null) {
            return fromQualified;
        }
        PsiFile file = psiClass.getContainingFile();
        if (file instanceof PsiJavaFile) {
            String pkg = ((PsiJavaFile) file).getPackageName();
            return StringUtils.isBlank(pkg) ? null : pkg;
        }
        return null;
    }
}
