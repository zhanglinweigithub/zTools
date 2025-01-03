package com.zhanglinwei.zTools.sensitive;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;

public abstract class AbstractSensitiveData extends AnAction {

    protected abstract void handleAction(Project project, Editor editor, Document document, Caret primaryCaret, String selectedText, int start, int end) throws Exception;

    @Override
    public void actionPerformed(AnActionEvent actionEvent) {
        Editor editor = actionEvent.getRequiredData(CommonDataKeys.EDITOR);
        Project project = actionEvent.getRequiredData(CommonDataKeys.PROJECT);
        Document document = editor.getDocument();

        Caret primaryCaret = editor.getCaretModel().getPrimaryCaret();
        int start = primaryCaret.getSelectionStart();
        int end = primaryCaret.getSelectionEnd();
        String selectedText = document.getText(TextRange.create(start, end));

        try {
            handleAction(project, editor, document, primaryCaret, selectedText, start, end);
        } catch (Exception ex) {
            HintManager.getInstance().showErrorHint(editor, ex.getLocalizedMessage());
        } finally {
            primaryCaret.removeSelection();
        }
    }

}
