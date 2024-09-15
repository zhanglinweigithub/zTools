package com.zhanglinwei.zTools;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.zhanglinwei.zTools.constant.MessageConstants;
import com.zhanglinwei.zTools.ddlbuilder.DDLBuilder;
import com.zhanglinwei.zTools.model.DDLInfo;
import com.zhanglinwei.zTools.normal.DDLBuilderFactory;
import com.zhanglinwei.zTools.util.ClipboardUtils;
import com.zhanglinwei.zTools.util.ConfigUtils;
import com.zhanglinwei.zTools.util.NotificationUtil;

public class CopyDDLAction extends AnAction {

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
        ConfigUtils.init(project);
        PsiElement referenceAt = psiFile.findElementAt(editor.getCaretModel().getOffset());
        PsiClass selectedClass = PsiTreeUtil.getContextOfType(referenceAt, PsiClass.class);
        if (selectedClass == null) {
            NotificationUtil.errorNotify(MessageConstants.ONLY_CLASS, project);
            return;
        }
        boolean generateSuccess = generateDDLToClipboard(project, selectedClass);
        if (generateSuccess) {
            NotificationUtil.infoNotify(MessageConstants.DDL_SUCCESS, project);
        }
    }

    private boolean generateDDLToClipboard(Project project, PsiClass selectedClass) {
        try {
            PsiField[] allFields = selectedClass.getAllFields();
            if (allFields.length == 0) {
                NotificationUtil.errorNotify(MessageConstants.NO_FIELDS, project);
                return false;
            }

            DDLBuilder ddLbuilder = DDLBuilderFactory.getDDLbuilder(ConfigUtils.getDDLDataBaseType());
            if (ddLbuilder == null) {
                NotificationUtil.errorNotify("This database is not supported!!!", project);
                return false;
            }

            DDLInfo ddlInfo = new DDLInfo(allFields, selectedClass);
            String ddlString = ddLbuilder.generateDDL(ddlInfo);
            ClipboardUtils.copyToClipboard(ddlString);
            return true;
        } catch (Exception e) {
            NotificationUtil.errorNotify("Copy DDL fail, Caused by: " + e.getMessage(), project);
        }
        return false;
    }

}
