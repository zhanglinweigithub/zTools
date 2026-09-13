package com.zhanglinwei.zTools.convert;

import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.util.IncorrectOperationException;
import com.zhanglinwei.zTools.convert.components.ConvertMethodFiller;
import com.zhanglinwei.zTools.convert.components.ConvertMethodSupport;
import org.jetbrains.annotations.NotNull;

/**
 * Alt+Enter：按方法返回类型与唯一入参，填充 JavaBean 转换方法体。
 */
public class FillConvertMethodIntention extends PsiElementBaseIntentionAction {

    private static final String FAMILY = "Convert";

    /**
     * 灯泡菜单文案。{@link #isAvailable} 成功后带上源/目标类型名。
     */
    private String text = FAMILY;

    @NotNull
    @Override
    public String getText() {
        return text;
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return FAMILY;
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        PsiMethod method = ConvertMethodSupport.methodAt(element);
        if (!ConvertMethodSupport.isSupported(method)) {
            text = FAMILY;
            return false;
        }
        PsiClass source = ConvertMethodSupport.sourceClass(method);
        PsiClass target = ConvertMethodSupport.targetClass(method);
        text = FAMILY + " " + source.getName() + " to " + target.getName();
        return true;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        PsiMethod method = ConvertMethodSupport.methodAt(element);
        if (!ConvertMethodSupport.isSupported(method)) {
            return;
        }
        ConvertMethodFiller.fill(project, method);
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
