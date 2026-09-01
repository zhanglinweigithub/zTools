package com.zhanglinwei.zTools.copycurl;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import com.zhanglinwei.zTools.annotation.model.ClassDefinition;
import com.zhanglinwei.zTools.annotation.model.MethodDefinition;
import com.zhanglinwei.zTools.annotation.parse.SourceParser;
import com.zhanglinwei.zTools.annotation.web.WebAnnotationParser;
import com.zhanglinwei.zTools.util.ClipboardUtils;
import com.zhanglinwei.zTools.util.NotificationUtil;

/**
 * 把当前 Mapping 方法复制为 curl。路径、动词、入参来自注解模块的定义，由本模块决定缺省值。
 */
public class CopyCurlAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent actionEvent) {
        Editor editor = actionEvent.getDataContext().getData(CommonDataKeys.EDITOR);
        if (editor == null) {
            return;
        }
        PsiFile psiFile = actionEvent.getData(CommonDataKeys.PSI_FILE);
        if (psiFile == null) {
            return;
        }
        Project project = editor.getProject();
        if (project == null) {
            return;
        }

        try {
            PsiElement referenceAt = psiFile.findElementAt(editor.getCaretModel().getOffset());
            PsiMethod selectedMethod = PsiTreeUtil.getContextOfType(referenceAt, PsiMethod.class);
            if (selectedMethod == null) {
                NotificationUtil.errorNotify("Please choose a method!", project);
                return;
            }

            MethodDefinition methodDefinition = SourceParser.parseMethod(selectedMethod);
            if (!WebAnnotationParser.isHandlerMethod(methodDefinition)) {
                NotificationUtil.errorNotify("The method is not a RestApi!", project);
                return;
            }

            PsiClass psiClass = selectedMethod.getContainingClass();
            ClassDefinition classDefinition = SourceParser.parseClass(psiClass, false);

            String curl = CurlGenerator.toCurl(classDefinition, methodDefinition, project);
            ClipboardUtils.copyToClipboard(curl);

            NotificationUtil.infoNotify("Copy CURL successfully!", project);
        } catch (Exception ex) {
            NotificationUtil.errorNotify("Copy CURL failed, Caused by: " + ex.getMessage(), project);
        }
    }

}
