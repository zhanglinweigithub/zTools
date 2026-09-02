package com.zhanglinwei.zTools.jasyptcrypto;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.zhanglinwei.zTools.configure.config.JasyptCryptoConfig;
import com.zhanglinwei.zTools.jasyptcrypto.facade.JasyptCrypto;
import com.zhanglinwei.zTools.jasyptcrypto.ui.ChoosePasswordDialog;
import com.zhanglinwei.zTools.jasyptcrypto.utils.JasyptUtils;

/**
 * 加密当前选区：明文 → {@code ENC(<密文>)}，写回编辑器。
 * <p>
 * 单密码直接加密；多密码弹窗让用户选一个。取消弹窗则不改文本。
 */
public class EncryptAction extends AbstractJasyptCrypto {

    /**
     * 用选定密码加密选区并加上密文包裹。
     *
     * @param editor       当前编辑器
     * @param project      当前项目
     * @param selectedText 选中的明文
     * @return 带包裹的密文；用户取消选密码时为 {@code null}
     */
    @Override
    protected String doAction(Editor editor, Project project, String selectedText) {
        JasyptCryptoConfig config = JasyptCryptoConfig.getInstance(project);

        JasyptCrypto crypto = null;
        String trimmedSelectedText = selectedText.trim();

        String[] passwords = JasyptUtils.getPasswords(project);
        if (passwords.length == 1) {
            // 单密码时直接加密
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
