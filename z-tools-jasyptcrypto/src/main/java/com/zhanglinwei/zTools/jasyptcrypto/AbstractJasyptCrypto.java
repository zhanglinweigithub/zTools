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

/**
 * Jasypt 加解密 Action 骨架：取选区 → 子类处理 → 默认用结果替换选区。
 * <p>
 * 子类实现 {@link #doAction}；解密到剪贴板、整文件解密可覆盖 {@link #afterAction}。
 */
public abstract class AbstractJasyptCrypto extends AnAction {

    /**
     * 对选中文本做加密或解密。
     *
     * @param editor       当前编辑器
     * @param project      当前项目
     * @param selectedText 选区原文
     * @return 处理后的文本；{@code null} 或空白表示不改编辑器
     */
    protected abstract String doAction(Editor editor, Project project, String selectedText);

    /**
     * 校验密码已配置后，对主光标选区执行加解密，最后取消选区。
     *
     * @param actionEvent IDEA 动作事件
     */
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

        // 设置里至少一个密码，否则提示去 Settings > z-tools
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

    /**
     * 默认把结果写回选区。子类可改为复制到剪贴板或替换整个文件。
     *
     * @param editor       当前编辑器
     * @param project      当前项目
     * @param actionResult {@link #doAction} 的返回值
     * @param selectedText 原选区
     * @param start        选区起始偏移
     * @param end          选区结束偏移
     */
    protected void afterAction(Editor editor, Project project, String actionResult, String selectedText, int start, int end) {
        if (StringUtils.isBlank(actionResult)) {
            return;
        }

        Document document = editor.getDocument();
        WriteCommandAction.runWriteCommandAction(project, () -> document.replaceString(start, end, actionResult));
    }
}
