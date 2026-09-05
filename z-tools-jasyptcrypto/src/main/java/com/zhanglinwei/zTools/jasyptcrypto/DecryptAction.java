package com.zhanglinwei.zTools.jasyptcrypto;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.zhanglinwei.zTools.configure.config.JasyptCryptoConfig;
import com.zhanglinwei.zTools.jasyptcrypto.utils.JasyptUtils;

/**
 * 解密当前选区：{@code ENC(<密文>)} 或裸密文 → 明文，写回编辑器。
 * <p>
 * 多个密码、固定盐、固定 IV 按组合依次尝试，任一成功即停止；全部失败则提示并保留原文。
 */
public class DecryptAction extends AbstractJasyptCrypto {


    /**
     * 按配置中的密码 × 盐 × IV 组合尝试解密选区。
     *
     * @param editor       当前编辑器
     * @param project      当前项目
     * @param selectedText 选中的密文（可带 {@code ENC(...)}）
     * @return 明文；全部组合失败则为 {@code null}
     */
    @Override
    protected String doAction(Editor editor, Project project, String selectedText) {
        JasyptCryptoConfig config = JasyptCryptoConfig.getInstance(project);
        String decrypted = JasyptUtils.tryDecrypt(config, selectedText.trim());
        if (decrypted == null) {
            HintManager.getInstance().showErrorHint(editor, "Decryption failure.");
            return null;
        }
        return decrypted;
    }
}
