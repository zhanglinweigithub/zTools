package com.zhanglinwei.zTools.sensitive;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.zhanglinwei.zTools.sensitive.facade.SensitiveFacade;
import com.zhanglinwei.zTools.sensitive.model.SensitivePair;
import com.zhanglinwei.zTools.util.ClipboardUtils;

public class DecryptSensitiveData2ClipboardAction extends AbstractSensitiveData {

    @Override
    protected void handleAction(Project project, Editor editor, Document document, Caret primaryCaret, String selectedText, int start, int end) throws Exception {
        SensitivePair sensitivePair = SensitiveFacade.decrypt(project, selectedText);
        if (sensitivePair == null) {
            HintManager.getInstance().showErrorHint(editor, "Please select an decrypted content.");
            return;
        }

        ClipboardUtils.copyToClipboard(sensitivePair.getNewData());
        HintManager.getInstance().showInformationHint(editor, "Copied to clipboard.");
    }

}
