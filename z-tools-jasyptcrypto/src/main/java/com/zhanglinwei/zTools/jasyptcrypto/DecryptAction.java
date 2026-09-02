package com.zhanglinwei.zTools.jasyptcrypto;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.zhanglinwei.zTools.configure.config.JasyptCryptoConfig;
import com.zhanglinwei.zTools.jasyptcrypto.facade.JasyptCrypto;
import com.zhanglinwei.zTools.jasyptcrypto.utils.JasyptUtils;

public class DecryptAction extends AbstractJasyptCrypto {


    @Override
    protected String doAction(Editor editor, Project project, String selectedText) {
        String[] passwords = JasyptUtils.getPasswords(project);
        JasyptCryptoConfig config = JasyptCryptoConfig.getInstance(project);
        String trimmedSelectedText = selectedText.trim();
        String decrypted = null;

        // 多个密码循环解密
        for (String password : passwords) {
            JasyptCrypto crypto = new JasyptCrypto(config, password);

            try {
                decrypted = crypto.decrypt(trimmedSelectedText);
                break;
            } catch (Exception ex) {
                // 当前密码解密失败，尝试下一个
            }
        }

        // 所有密码都解密失败，保留原文
        if (decrypted == null) {
            HintManager.getInstance().showErrorHint(editor, "Decryption failure.");
            return null;
        }

        return decrypted;
    }
}
