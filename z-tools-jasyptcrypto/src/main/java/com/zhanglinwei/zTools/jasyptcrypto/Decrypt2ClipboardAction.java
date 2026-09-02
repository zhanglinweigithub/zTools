package com.zhanglinwei.zTools.jasyptcrypto;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.zhanglinwei.zTools.common.util.ClipboardUtils;

/**
 * 解密选区后复制到系统剪贴板，不改编辑器内容。
 */
public class Decrypt2ClipboardAction extends DecryptAction {

    /**
     * 把解密结果写入剪贴板并给出 Hint。
     *
     * @param editor       当前编辑器
     * @param project      当前项目
     * @param actionResult 明文；解密失败时为 {@code null}
     * @param selectedText 原选区
     * @param start        选区起始（本实现不用）
     * @param end          选区结束（本实现不用）
     */
    @Override
    protected void afterAction(Editor editor, Project project, String actionResult, String selectedText, int start, int end) {
        if (actionResult == null) {
            return;
        }

        ClipboardUtils.copyToClipboard(actionResult);
        HintManager.getInstance().showInformationHint(editor, "Copied to clipboard.");
    }

}
