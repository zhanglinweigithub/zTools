package com.zhanglinwei.zTools.copyjson;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiTypesUtil;
import com.zhanglinwei.zTools.doc.apidoc.model.JavaProperty;
import com.zhanglinwei.zTools.util.AssertUtils;
import com.zhanglinwei.zTools.util.ClipboardUtils;
import com.zhanglinwei.zTools.util.JsonUtil;
import com.zhanglinwei.zTools.util.NotificationUtil;


public class CopyJsonAction extends AnAction {

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

        PsiElement referenceAt = psiFile.findElementAt(editor.getCaretModel().getOffset());
        PsiClass selectedClass = PsiTreeUtil.getContextOfType(referenceAt, PsiClass.class);
        if (selectedClass == null) {
            NotificationUtil.errorNotify("This operation only supports Java Class files!", project);
            return;
        }

        if (generateJson(selectedClass)) {
            NotificationUtil.infoNotify("Copy Json successfully!", project);
        }
    }

    private boolean generateJson(PsiClass selectedClass) {
        Project project = selectedClass.getProject();

        try {
            PsiField[] allFields = selectedClass.getAllFields();
            if (AssertUtils.isEmpty(allFields)) {
                ClipboardUtils.copyToClipboard("{}");
                return true;
            }

            PsiType classType = PsiTypesUtil.getClassType(selectedClass);
            String prettyJson = JsonUtil.prettyJsonWithComment(JavaProperty.createSimple(classType, project, null));
            ClipboardUtils.copyToClipboard(prettyJson);
            return true;
        } catch (Exception e) {
            NotificationUtil.errorNotify("Copy Json fail, Caused by: " + e.getMessage(), project);
        }
        return false;
    }

}
