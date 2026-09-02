package com.zhanglinwei.zTools.jasyptcrypto;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.zhanglinwei.zTools.common.util.ClipboardUtils;

public class Decrypt2ClipboardAction extends DecryptAction {

    @Override
    protected void afterAction(Editor editor, Project project, String actionResult, String selectedText, int start, int end) {
        if (actionResult == null) {
            return;
        }

        ClipboardUtils.copyToClipboard(actionResult);
        HintManager.getInstance().showInformationHint(editor, "Copied to clipboard.");
    }

}
