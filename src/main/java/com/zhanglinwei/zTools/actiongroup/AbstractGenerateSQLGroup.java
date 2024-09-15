package com.zhanglinwei.zTools.actiongroup;

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
import com.zhanglinwei.zTools.builder.SQLBuilder;
import com.zhanglinwei.zTools.model.DbTableInfo;
import com.zhanglinwei.zTools.normal.SQLBuilderFactory;
import com.zhanglinwei.zTools.util.ConfigUtils;
import com.zhanglinwei.zTools.util.NotificationUtil;

public abstract class AbstractGenerateSQLGroup extends AnAction {

    private Editor editor;

    private PsiFile psiFile;

    private Project project;

    private PsiClass selectedClass;

    private final String DEFAULT_MESSAGE = "";


    protected boolean init(AnActionEvent actionEvent) {
        Editor editor = actionEvent.getDataContext().getData(CommonDataKeys.EDITOR);
        if (editor == null) {
            return false;
        }
        PsiFile psiFile = actionEvent.getData(CommonDataKeys.PSI_FILE);
        if (psiFile == null) {
            return false;
        }
        Project project = editor.getProject();
        if (project == null) {
            return false;
        }

        ConfigUtils.init(project);
        PsiElement referenceAt = psiFile.findElementAt(editor.getCaretModel().getOffset());
        PsiClass selectedClass = PsiTreeUtil.getContextOfType(referenceAt, PsiClass.class);
        if (selectedClass == null) {
            NotificationUtil.errorNotify(MessageConstants.ONLY_CLASS, project);
            return false;
        }

        this.editor = editor;
        this.project = project;
        this.psiFile = psiFile;
        this.selectedClass = selectedClass;

        return true;
    }

    protected String generateSQL(SQLTypeEnum sqlTypeEnum) {
        try {
            PsiField[] allFields = selectedClass.getAllFields();
            if (allFields.length == 0) {
                NotificationUtil.errorNotify(MessageConstants.NO_FIELDS, project);
                return null;
            }
            SQLBuilder sqlBuilder = SQLBuilderFactory.getSQLbuilder(ConfigUtils.getSQLDataBaseType());
            if (sqlBuilder == null) {
                NotificationUtil.errorNotify("This database is not supported!!!", project);
                return null;
            }

            return doGenerateSQL(sqlBuilder, sqlTypeEnum);
        } catch (Exception e) {
            NotificationUtil.errorNotify("Generate SQL fail, Caused by: " + e.getMessage(), project);
        }

        return null;
    }

    private String doGenerateSQL(SQLBuilder sqlBuilder, SQLTypeEnum sqlTypeEnum) {
        DbTableInfo dbTableInfo = new DbTableInfo(selectedClass.getAllFields(), selectedClass);

        switch (sqlTypeEnum) {
            case DDL: return sqlBuilder.generateDDL(dbTableInfo);
            case SELECT: return sqlBuilder.generateSelect(dbTableInfo);
            case INSERT: return sqlBuilder.generateInsert(dbTableInfo);
            case UPDATE: return sqlBuilder.generateUpdate(dbTableInfo);
            case DELETE: return sqlBuilder.generateDelete(dbTableInfo);
            default: return null;
        }
    }

    public Editor getEditor() {
        return editor;
    }

    public PsiFile getPsiFile() {
        return psiFile;
    }

    public Project getProject() {
        return project;
    }

    public PsiClass getSelectedClass() {
        return selectedClass;
    }


    protected enum SQLTypeEnum {
        DDL,
        SELECT,
        INSERT,
        UPDATE,
        DELETE
    }

}
