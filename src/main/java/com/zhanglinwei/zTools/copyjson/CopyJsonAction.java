package com.zhanglinwei.zTools.copyjson;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiTypesUtil;
import com.zhanglinwei.zTools.doc.apidoc.normal.FieldFactory;
import com.zhanglinwei.zTools.util.ClipboardUtils;
import com.zhanglinwei.zTools.util.JsonUtil;
import com.zhanglinwei.zTools.util.NotificationUtil;


/**
 * 不带注释的JSON
 */
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
        boolean generateSuccess = generateJson(project, selectedClass);
        if (generateSuccess) {
            NotificationUtil.infoNotify("Copy Json successfully!", project);
        }
    }

    private boolean generateJson(Project project, PsiClass selectedClass) {
        try {
            PsiField[] allFields = selectedClass.getAllFields();
            if (allFields.length == 0) {
                NotificationUtil.errorNotify("This class file has no fields!", project);
                return false;
            }
            PsiType classType = PsiTypesUtil.getClassType(selectedClass);

            String jsonStr = JsonUtil.buildJson5(FieldFactory.buildByPsiType(classType, null, project));
            ClipboardUtils.copyToClipboard(jsonStr);
            return true;
        } catch (Exception e) {
            NotificationUtil.errorNotify("Copy Json fail, Caused by: " + e.getMessage(), project);
        }
        return false;
    }

}
