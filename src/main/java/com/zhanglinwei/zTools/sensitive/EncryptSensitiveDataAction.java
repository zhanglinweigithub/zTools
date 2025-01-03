package com.zhanglinwei.zTools.sensitive;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.zhanglinwei.zTools.sensitive.facade.SensitiveFacade;
import com.zhanglinwei.zTools.sensitive.model.SensitivePair;
import com.zhanglinwei.zTools.util.CommonUtils;

public class EncryptSensitiveDataAction extends AbstractSensitiveData {

    @Override
    protected void handleAction(Project project, Editor editor, Document document, Caret primaryCaret, String selectedText, int start, int end) throws Exception {
        SensitivePair sensitivePair = SensitiveFacade.encrypt(project, selectedText);
        if (sensitivePair == null) {
            HintManager.getInstance().showErrorHint(editor, "Please select an encrypted content.");
            return;
        }
        String encryptedText = CommonUtils.replace(selectedText, sensitivePair.getOldData(), sensitivePair.getNewData(), 1);
        WriteCommandAction.runWriteCommandAction(project, () -> document.replaceString(start, end, encryptedText));
    }

}
