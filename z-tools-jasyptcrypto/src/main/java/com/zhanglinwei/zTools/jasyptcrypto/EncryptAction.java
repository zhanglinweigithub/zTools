package com.zhanglinwei.zTools.jasyptcrypto;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.zhanglinwei.zTools.configure.config.JasyptCryptoConfig;
import com.zhanglinwei.zTools.configure.enums.JasyptIV;
import com.zhanglinwei.zTools.configure.enums.JasyptSalt;
import com.zhanglinwei.zTools.jasyptcrypto.facade.JasyptCrypto;
import com.zhanglinwei.zTools.jasyptcrypto.ui.ChoosePasswordDialog;
import com.zhanglinwei.zTools.jasyptcrypto.ui.EncryptParams;
import com.zhanglinwei.zTools.jasyptcrypto.utils.JasyptUtils;

/**
 * 加密当前选区：明文 → {@code ENC(<密文>)}，写回编辑器。
 * <p>
 * 单组密码 / 盐 / IV 直接加密；任一项有多个则弹窗让用户各选一个。取消弹窗则不改文本。
 */
public class EncryptAction extends AbstractJasyptCrypto {

    /**
     * 用选定密码、固定盐、固定 IV 加密选区并加上密文包裹。
     *
     * @param editor       当前编辑器
     * @param project      当前项目
     * @param selectedText 选中的明文
     * @return 带包裹的密文；用户取消或缺少 Fixed 值时为 {@code null}
     */
    @Override
    protected String doAction(Editor editor, Project project, String selectedText) {
        JasyptCryptoConfig config = JasyptCryptoConfig.getInstance(project);
        String trimmedSelectedText = selectedText.trim();

        String[] passwords = JasyptUtils.getPasswords(project);
        boolean saltFixed = JasyptSalt.codeOf(config.getSaltGenerator()).requiresValue();
        boolean ivFixed = JasyptIV.codeOf(config.getIvGenerator()).requiresValue();
        String[] salts = saltFixed ? JasyptUtils.getSaltValues(config) : new String[0];
        String[] ivs = ivFixed ? JasyptUtils.getIvValues(config) : new String[0];

        if (saltFixed && salts.length == 0) {
            HintManager.getInstance().showErrorHint(editor, "Salt value is required. Please configure it in Settings > z-tools.");
            return null;
        }
        if (ivFixed && ivs.length == 0) {
            HintManager.getInstance().showErrorHint(editor, "IV value is required. Please configure it in Settings > z-tools.");
            return null;
        }

        EncryptParams params;
        if (passwords.length > 1 || salts.length > 1 || ivs.length > 1) {
            params = ChoosePasswordDialog.choose(project, passwords, salts, ivs);
            if (params == null) {
                return null;
            }
        } else {
            params = new EncryptParams(passwords[0], JasyptUtils.firstOrEmpty(salts), JasyptUtils.firstOrEmpty(ivs));
        }

        JasyptCrypto crypto = new JasyptCrypto(config, params.getPassword(), params.getSaltValue(), params.getIvValue());
        return crypto.encryptWithWrapper(trimmedSelectedText);
    }
}
