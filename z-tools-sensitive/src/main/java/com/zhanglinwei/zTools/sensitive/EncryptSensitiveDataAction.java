package com.zhanglinwei.zTools.sensitive;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.zhanglinwei.zTools.sensitive.facade.SensitiveFacade;
import com.zhanglinwei.zTools.sensitive.model.SensitivePair;
import com.zhanglinwei.zTools.sensitive.ui.ChoosePasswordDialog;
import com.zhanglinwei.zTools.util.StringUtils;

public class EncryptSensitiveDataAction extends AbstractSensitiveData {

    @Override
    protected void handleAction(Project project, Editor editor, Document document, Caret primaryCaret, String selectedText, int start, int end) throws Exception {
        // 先尝试直接加密（单密码时直接完成）
        SensitivePair sensitivePair = SensitiveFacade.encrypt(project, selectedText);

        if (sensitivePair == null) {
            // 多密码场景，弹窗让用户选择加密用的密码
            String[] passwords = SensitiveFacade.getPasswords(project);
            if (passwords.length == 0) {
                HintManager.getInstance().showErrorHint(editor, "Password is required. Please configure it in Settings > zzz-sensitive.");
                return;
            }
            String chosen = ChoosePasswordDialog.choose(project, passwords);
            if (chosen == null) {
                // 用户取消
                return;
            }
            sensitivePair = SensitiveFacade.encrypt(project, selectedText, chosen);
        }

        if (sensitivePair == null) {
            HintManager.getInstance().showErrorHint(editor, "Please select content to encrypt.");
            return;
        }
        String encryptedText = StringUtils.replace(selectedText, sensitivePair.getOldData(), sensitivePair.getNewData(), 1);
        WriteCommandAction.runWriteCommandAction(project, () -> document.replaceString(start, end, encryptedText));
    }
}
