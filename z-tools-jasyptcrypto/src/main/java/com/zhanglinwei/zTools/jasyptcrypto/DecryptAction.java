package com.zhanglinwei.zTools.jasyptcrypto;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.zhanglinwei.zTools.configure.config.JasyptCryptoConfig;
import com.zhanglinwei.zTools.jasyptcrypto.facade.JasyptCrypto;
import com.zhanglinwei.zTools.jasyptcrypto.utils.JasyptUtils;

/**
 * 解密当前选区：{@code ENC(<密文>)} 或裸密文 → 明文，写回编辑器。
 * <p>
 * 配置了多个密码时按顺序尝试，任一成功即停止；全部失败则提示并保留原文。
 */
public class DecryptAction extends AbstractJasyptCrypto {


    /**
     * 依次用配置中的密码尝试解密选区。
     *
     * @param editor       当前编辑器
     * @param project      当前项目
     * @param selectedText 选中的密文（可带 {@code ENC(...)}）
     * @return 明文；全部密码失败则为 {@code null}
     */
    @Override
    protected String doAction(Editor editor, Project project, String selectedText) {
        String[] passwords = JasyptUtils.getPasswords(project);
        JasyptCryptoConfig config = JasyptCryptoConfig.getInstance(project);
        String trimmedSelectedText = selectedText.trim();
        String decrypted = null;

        // 多个密码循环解密，成功一个即可
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
