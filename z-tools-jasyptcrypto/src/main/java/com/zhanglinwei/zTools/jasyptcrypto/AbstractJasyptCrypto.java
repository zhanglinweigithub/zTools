package com.zhanglinwei.zTools.jasyptcrypto;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.zhanglinwei.zTools.jasyptcrypto.utils.JasyptUtils;
import com.zhanglinwei.zTools.common.util.StringUtils;

public abstract class AbstractJasyptCrypto extends AnAction {

    protected abstract String doAction(Editor editor, Project project, String selectedText);

    @Override
    public void actionPerformed(AnActionEvent actionEvent) {
        Editor editor = actionEvent.getDataContext().getData(CommonDataKeys.EDITOR);
        if (editor == null) {
            return;
        }

        Project project = editor.getProject();
        if (project == null) {
            return;
        }

        String[] passwords = JasyptUtils.getPasswords(project);
        if (passwords.length == 0) {
            HintManager.getInstance().showErrorHint(editor, "Password is required. Please configure it in Settings > z-tools.");
            return;
        }

        Document document = editor.getDocument();
        Caret primaryCaret = editor.getCaretModel().getPrimaryCaret();
        int start = primaryCaret.getSelectionStart();
        int end = primaryCaret.getSelectionEnd();

        String selectedText = document.getText(TextRange.create(start, end));

        try {
            String actionResult = doAction(editor, project, selectedText);
            afterAction(editor, project, actionResult, selectedText, start, end);
        } catch (Exception ex) {
            HintManager.getInstance().showErrorHint(editor, "Execution failure.");
        } finally {
            primaryCaret.removeSelection();
        }
    }

    protected void afterAction(Editor editor, Project project, String actionResult, String selectedText, int start, int end) {
        if (StringUtils.isBlank(actionResult)) {
            return;
        }

        Document document = editor.getDocument();
        WriteCommandAction.runWriteCommandAction(project, () -> document.replaceString(start, end, actionResult));
    }
}
