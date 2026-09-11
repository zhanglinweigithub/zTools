package com.zhanglinwei.zTools.jasyptcrypto;


import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.zhanglinwei.zTools.configure.config.JasyptCryptoConfig;
import com.zhanglinwei.zTools.jasyptcrypto.utils.EncWrapper;
import com.zhanglinwei.zTools.jasyptcrypto.utils.JasyptUtils;
import com.zhanglinwei.zTools.common.util.NotificationUtil;
import com.zhanglinwei.zTools.common.util.StringUtils;

/**
 * 批量解密配置文件中所有带包裹的密文。
 * <p>
 * 无需手写 main 方法，直接在编辑器中一键解密整个文件。
 * Enc Wrapper 支持有前有后、有前无后、有后无前；两边都没有时无法扫描，文件保持原样。
 */
public class DecryptFileAction extends AbstractJasyptCrypto {

    /**
     * 扫描整个文档，把所有带包裹的密文还原为明文。
     *
     * @param editor       当前编辑器
     * @param project      当前项目
     * @param selectedText 选区（整文件解密时忽略）
     * @return 解密后的全文；未发现密文或失败则为 {@code null}
     */
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

    /**
     * 用解密后的全文替换整个文档。
     *
     * @param editor       当前编辑器
     * @param project      当前项目
     * @param actionResult 解密后的全文
     * @param selectedText 原选区（忽略）
     * @param start        选区起始（忽略）
     * @param end          选区结束（忽略）
     */
    @Override
    protected void afterAction(Editor editor, Project project, String actionResult, String selectedText, int start, int end) {
        if (StringUtils.isBlank(actionResult)) {
            return;
        }

        WriteCommandAction.runWriteCommandAction(project, () -> editor.getDocument().setText(actionResult));
        NotificationUtil.infoNotify("Config file decrypted successfully.", project);
    }

    /**
     * 扫描全文，按当前 Enc Wrapper 的前缀 / 后缀组合还原密文。
     *
     * @param project 当前项目
     * @param content 文件全文
     * @return 替换密文后的全文；没有密文时与输入相同
     * @throws Exception 预留，当前实现内部吞掉单段解密失败
     */
    public static String decryptAll(Project project, String content) throws Exception {
        JasyptCryptoConfig config = JasyptCryptoConfig.getInstance(project);
        EncWrapper wrapper = EncWrapper.from(config);
        return wrapper.replaceAll(content, ciphertext -> JasyptUtils.tryDecrypt(config, ciphertext));
    }

    /**
     * 有编辑器时才显示本 Action。
     *
     * @param e IDEA 动作事件
     */
    @Override
    public void update(AnActionEvent e) {
        Editor editor = e.getDataContext().getData(CommonDataKeys.EDITOR);
        e.getPresentation().setEnabledAndVisible(editor != null);
    }
}
