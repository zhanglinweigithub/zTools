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
 * 只读源码：不推断 required、不补 default、不生成示例。
 */
public final class SourceParser {

    /** 工具类，禁止实例化。 */
    private SourceParser() {}

    /**
     * 将 {@link PsiClass} 解析为 {@link ClassDefinition}。
     *
     * @param psiClass    待解析的 PSI 类，为 {@code null} 时返回 {@code null}
     * @param parseMethod 为 {@code true} 时解析本类声明的方法（不含构造器、不含继承方法）
     * @return 类定义；{@code psiClass} 为 {@code null} 时返回 {@code null}
     */
    public static ClassDefinition parseClass(PsiClass psiClass, boolean parseMethod) {
        if (psiClass == null) {
            return null;
        }
        // 仅本类声明的方法：跳过构造器，以及 getMethods() 带来的父类方法
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
                CommentParser.of(psiClass),
                methods
        );
    }

    /**
     * 将 {@link PsiMethod} 解析为 {@link MethodDefinition}。
     *
     * @param method 待解析的 PSI 方法，为 {@code null} 时返回 {@code null}
     * @return 方法定义
     */
    public static MethodDefinition parseMethod(PsiMethod method) {
        if (method == null) {
            return null;
        }
        PsiClass containing = method.getContainingClass();
        String packageName = packageName(containing);
        // JavaDoc @param 按参数名挂到对应 ParameterDefinition.comment
        Map<String, String> paramComments = CommentParser.params(method);
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
                CommentParser.of(method)
        );
    }

    /**
     * 解析类上的全部方法（含构造器、含继承方法）。
     *
     * @param psiClass PSI 类，为 {@code null} 时返回 {@code null}
     * @return 方法定义列表
     */
    public static List<MethodDefinition> parseMethod(PsiClass psiClass) {
        if (psiClass == null) {
            return null;
        }

        return Arrays.stream(psiClass.getMethods())
                .map(SourceParser::parseMethod)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 解析方法返回值。构造器没有返回类型时为 {@code null}。
     *
     * @param method PSI 方法
     * @return 返回值定义；无返回类型时为 {@code null}
     */
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
                CommentParser.returnComment(method),
                TypeParser.properties(returnType)
        );
    }

    /**
     * 将 {@link PsiParameter} 解析为 {@link ParameterDefinition}。
     *
     * @param parameter PSI 参数，为 {@code null} 时返回 {@code null}
     * @param comment   对应 {@code @param} 注释，空白视为没有
     * @return 参数定义
     */
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

    /**
     * 抽出方法所在类的轻量引用，避免嵌套完整类定义。
     *
     * @param psiClass 所在类
     * @return 类引用；{@code psiClass} 为 {@code null} 时返回 {@code null}
     */
    private static ClassRef classRef(PsiClass psiClass) {
        if (psiClass == null) {
            return null;
        }
        return new ClassRef(psiClass.getName(), psiClass.getQualifiedName(), packageName(psiClass));
    }

    /**
     * 解析类所在包：优先从全限定名截取，否则读 Java 文件的 package 声明。
     *
     * @param psiClass PSI 类
     * @return 包名；缺省包或解析不到时为 {@code null}
     */
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
