package com.zhanglinwei.zTools.convert.components;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;

import java.util.List;

/**
 * 把生成的转换方法体写回 {@link PsiMethod}，并按项目代码风格格式化。
 */
public final class ConvertMethodFiller {

    private ConvertMethodFiller() {}

    /**
     * 用 setter 风格替换方法体。调用方须已在写操作中。
     *
     * @param project 当前项目
     * @param method  目标方法，须带方法体
     */
    public static void fill(Project project, PsiMethod method) {
        fill(project, method, false);
    }

    /**
     * 用 {@code Dest.builder()...build()} 替换方法体。调用方须已在写操作中。
     *
     * @param project 当前项目
     * @param method  目标方法，须带方法体
     */
    public static void fillBuilder(Project project, PsiMethod method) {
        fill(project, method, true);
    }

    private static void fill(Project project, PsiMethod method, boolean builder) {
        if (method == null || method.getBody() == null) {
            return;
        }
        PsiClass target = ConvertMethodSupport.targetClass(method);
        String sourceVar = ConvertMethodSupport.sourceVar(method);
        if (target == null || sourceVar == null) {
            return;
        }

        String targetType = target.getName();
        List<ConvertField> fields = ConvertMethodSupport.matchingFields(method);
        String blockText = builder
                ? ConvertMethodBodyGenerator.generateBuilder(targetType, sourceVar, fields)
                : ConvertMethodBodyGenerator.generate(
                        targetType,
                        ConvertMethodBodyGenerator.targetVarName(targetType, sourceVar),
                        sourceVar,
                        fields);

        PsiElementFactory factory = JavaPsiFacade.getElementFactory(project);
        PsiCodeBlock newBody = factory.createCodeBlockFromText(blockText, method);
        method.getBody().replace(newBody);
        JavaCodeStyleManager.getInstance(project).shortenClassReferences(method);
        CodeStyleManager.getInstance(project).reformat(method);
    }
}
