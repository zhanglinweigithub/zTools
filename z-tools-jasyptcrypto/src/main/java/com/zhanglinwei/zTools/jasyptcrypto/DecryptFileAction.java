package com.zhanglinwei.zTools.jasyptcrypto;


import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.zhanglinwei.zTools.configure.config.JasyptCryptoConfig;
import com.zhanglinwei.zTools.jasyptcrypto.facade.JasyptCrypto;
import com.zhanglinwei.zTools.jasyptcrypto.utils.JasyptUtils;
import com.zhanglinwei.zTools.common.util.NotificationUtil;
import com.zhanglinwei.zTools.common.util.StringUtils;

/**
 * 批量解密配置文件中所有 prefix...suffix 密文
 * 无需手写 main 方法，直接在编辑器中一键解密整个文件
 */
public class DecryptFileAction extends AbstractJasyptCrypto {

    @Override
    protected String doAction(Editor editor, Project project, String selectedText) {
        Document document = editor.getDocument();
        String content = document.getText();

        try {
            String decrypted = decryptAll(project, content);
            if (decrypted.equals(content)) {
                NotificationUtil.infoNotify("No encrypted ciphertext found.", project);
                return null;
            }

            return decrypted;
        } catch (Exception ex) {
            NotificationUtil.errorNotify("Decrypt failed: " + ex.getLocalizedMessage(), project);
        }

        return null;
    }

    @Override
    protected void afterAction(Editor editor, Project project, String actionResult, String selectedText, int start, int end) {
        if (StringUtils.isBlank(actionResult)) {
            return;
        }

        WriteCommandAction.runWriteCommandAction(project, () -> editor.getDocument().setText(actionResult));
        NotificationUtil.infoNotify("Config file decrypted successfully.", project);
    }

    public static String decryptAll(Project project, String content) throws Exception {
        JasyptCryptoConfig config = JasyptCryptoConfig.getInstance(project);
        String[] passwords = JasyptUtils.getPasswords(project);
        String prefix = config.getEncPrefix();
        String suffix = config.getEncSuffix();

        StringBuilder result = new StringBuilder();
        int i = 0;

        while (i < content.length()) {
            int encStart = content.indexOf(prefix, i);
            if (encStart == -1) {
                result.append(content.substring(i));
                break;
            }

            // 追加 prefix 之前的原文
            result.append(content.substring(i, encStart));

            int encEnd = content.indexOf(suffix, encStart + prefix.length());
            if (encEnd == -1) {
                result.append(content.substring(encStart));
                break;
            }

            // 提取密文，依次用每个密码尝试解密
            String ciphertext = content.substring(encStart + prefix.length(), encEnd);
            String plaintext = null;
            for (String pwd : passwords) {
                try {
                    JasyptCrypto crypto = new JasyptCrypto(config, pwd);
                    plaintext = crypto.decrypt(ciphertext);
                    break;
                } catch (Exception ignored) {
                    // 当前密码解密失败，尝试下一个
                }
            }

            if (plaintext != null) {
                result.append(plaintext);
            } else {
                // 所有密码都解密失败，保留原文
                result.append(prefix).append(ciphertext).append(suffix);
            }

            i = encEnd + suffix.length();
        }

        return result.toString();
    }

    @Override
    public void update(AnActionEvent e) {
        Editor editor = e.getDataContext().getData(CommonDataKeys.EDITOR);
        e.getPresentation().setEnabledAndVisible(editor != null);
    }
}
