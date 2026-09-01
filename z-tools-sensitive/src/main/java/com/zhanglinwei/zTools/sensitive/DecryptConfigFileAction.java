package com.zhanglinwei.zTools.sensitive;


import com.intellij.codeInsight.hint.HintManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.zhanglinwei.zTools.sensitive.facade.SensitiveFacade;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;

/**
 * 批量解密配置文件中所有 prefix...suffix 密文
 * 无需手写 main 方法，直接在编辑器中一键解密整个文件
 */
public class DecryptConfigFileAction extends AnAction {

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
        String content = document.getText();

        try {
            String decrypted = SensitiveFacade.decryptAll(project, content);
            if (decrypted.equals(content)) {
                HintManager.getInstance().showInformationHint(editor, "No encrypted ciphertext found.");
                return;
            }
            String finalDecrypted = decrypted;
            WriteCommandAction.runWriteCommandAction(project, () -> document.setText(finalDecrypted));
            HintManager.getInstance().showInformationHint(editor, "Config file decrypted successfully.");
        } catch (EncryptionOperationNotPossibleException ex) {
            HintManager.getInstance().showErrorHint(editor,
                    "Encryption operation failed. Possible causes: " +
                            "1) Password is incorrect; " +
                            "2) Algorithm requires JCE unlimited policy (AES-256 etc.); " +
                            "3) Salt/IV generator mismatch with the encrypted data.");
        } catch (IllegalArgumentException ex) {
            HintManager.getInstance().showErrorHint(editor, ex.getMessage());
        } catch (Exception ex) {
            HintManager.getInstance().showErrorHint(editor, "Decrypt failed: " + ex.getLocalizedMessage());
        }
    }

    @Override
    public void update(AnActionEvent e) {
        Editor editor = e.getDataContext().getData(CommonDataKeys.EDITOR);
        e.getPresentation().setEnabledAndVisible(editor != null);
    }
}
