package com.zhanglinwei.zTools.jasyptcrypto;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.zhanglinwei.zTools.configure.config.JasyptCryptoConfig;
import com.zhanglinwei.zTools.jasyptcrypto.facade.JasyptCrypto;
import com.zhanglinwei.zTools.jasyptcrypto.ui.ChoosePasswordDialog;
import com.zhanglinwei.zTools.jasyptcrypto.utils.JasyptUtils;

public class EncryptAction extends AbstractJasyptCrypto {

    @Override
    protected String doAction(Editor editor, Project project, String selectedText) {
        JasyptCryptoConfig config = JasyptCryptoConfig.getInstance(project);

        JasyptCrypto crypto = null;
        String trimmedSelectedText = selectedText.trim();

        String[] passwords = JasyptUtils.getPasswords(project);
        if (passwords.length == 1) {
            // 单密码时直接完成
            crypto = new JasyptCrypto(config, passwords[0]);
        } else {
            // 多密码场景，弹窗让用户选择加密用的密码
            String chosen = ChoosePasswordDialog.choose(project, passwords);
            if (chosen == null) {
                // 用户取消
                return null;
            }
            crypto = new JasyptCrypto(config, chosen);
        }

        return crypto.encryptWithWrapper(trimmedSelectedText);
    }
}
