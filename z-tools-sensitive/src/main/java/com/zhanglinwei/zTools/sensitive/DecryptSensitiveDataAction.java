package com.zhanglinwei.zTools.sensitive;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.zhanglinwei.zTools.sensitive.facade.SensitiveFacade;
import com.zhanglinwei.zTools.sensitive.model.SensitivePair;
import com.zhanglinwei.zTools.util.StringUtils;
import org.jasypt.registry.AlgorithmRegistry;

import java.util.Set;

public class DecryptSensitiveDataAction extends AbstractSensitiveData {

    @Override
    protected void handleAction(Project project, Editor editor, Document document, Caret primaryCaret, String selectedText, int start, int end) throws Exception {
        Set allPBEAlgorithms = AlgorithmRegistry.getAllPBEAlgorithms();

        SensitivePair sensitivePair = SensitiveFacade.decrypt(project, selectedText);
        if (sensitivePair == null) {
            HintManager.getInstance().showErrorHint(editor, "Please select an decrypted content.");
            return;
        }
        String decryptedText = StringUtils.replace(selectedText, sensitivePair.getOldData(), sensitivePair.getNewData(), 1);
        WriteCommandAction.runWriteCommandAction(project, () -> document.replaceString(start, end, decryptedText));
    }

}
