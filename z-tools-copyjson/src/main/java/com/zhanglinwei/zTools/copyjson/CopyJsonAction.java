package com.zhanglinwei.zTools.copyjson;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiTypesUtil;
import com.zhanglinwei.zTools.common.util.ClipboardUtils;
import com.zhanglinwei.zTools.common.util.NotificationUtil;

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

        try {
            String prettyJson = CopyJsonGenerator.of(PsiTypesUtil.getClassType(selectedClass));
            ClipboardUtils.copyToClipboard(prettyJson);
            NotificationUtil.infoNotify("Copy Json successfully!", project);
        } catch (Exception e) {
            NotificationUtil.errorNotify("Copy Json fail, Caused by: " + e.getMessage(), project);
        }
    }
}
