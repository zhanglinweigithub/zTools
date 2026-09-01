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
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;

public abstract class AbstractSensitiveData extends AnAction {

    protected abstract void handleAction(Project project, Editor editor, Document document, Caret primaryCaret, String selectedText, int start, int end) throws Exception;

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
        Document document = editor.getDocument();

        Caret primaryCaret = editor.getCaretModel().getPrimaryCaret();
        int start = primaryCaret.getSelectionStart();
        int end = primaryCaret.getSelectionEnd();
        String selectedText = document.getText(TextRange.create(start, end));

        try {
            handleAction(project, editor, document, primaryCaret, selectedText, start, end);
        } catch (EncryptionOperationNotPossibleException ex) {
            HintManager.getInstance().showErrorHint(editor,
                    "Encryption operation failed. Possible causes: " +
                            "1) Password is incorrect; " +
                            "2) Algorithm requires JCE unlimited policy (AES-256 etc.); " +
                            "3) Salt/IV generator mismatch with the encrypted data.");
        } catch (IllegalArgumentException ex) {
            HintManager.getInstance().showErrorHint(editor, ex.getMessage());
        } catch (Exception ex) {
            HintManager.getInstance().showErrorHint(editor, ex.getLocalizedMessage());
        } finally {
            primaryCaret.removeSelection();
        }
    }
}
