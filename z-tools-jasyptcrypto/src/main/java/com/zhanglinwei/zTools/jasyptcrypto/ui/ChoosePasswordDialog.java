package com.zhanglinwei.zTools.jasyptcrypto.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import com.zhanglinwei.zTools.jasyptcrypto.utils.JasyptUtils;

import javax.swing.*;
import java.awt.*;

/**
 * 加密参数选择弹窗。配置了多个密码、固定盐或固定 IV 时弹出，让用户各选一个。
 */
public class ChoosePasswordDialog extends DialogWrapper {

    private final String[] passwords;
    private final String[] salts;
    private final String[] ivs;
    private final ComboBox<String> passwordBox;
    private final ComboBox<String> saltBox;
    private final ComboBox<String> ivBox;

    /**
     * 用已配置的密码 / 盐 / IV 列表初始化下拉框，默认选中第一项。
     * 某一项数量不超过 1 时不展示对应下拉。
     *
     * @param project   当前项目
     * @param passwords 可供选择的密码
     * @param salts     可供选择的固定盐；非 Fixed 传空数组
     * @param ivs       可供选择的固定 IV；非 Fixed 传空数组
     */
    public ChoosePasswordDialog(Project project, String[] passwords, String[] salts, String[] ivs) {
        super(project);
        this.passwords = passwords;
        this.salts = salts;
        this.ivs = ivs;
        setTitle("Select Encryption Parameters");
        setOKButtonText("Encrypt");

        passwordBox = comboIfMultiple(passwords);
        saltBox = comboIfMultiple(salts);
        ivBox = comboIfMultiple(ivs);

        init();
    }

    /**
     * 多于一项时才创建下拉框。
     *
     * @param values 候选项
     * @return 下拉框；只有 0 或 1 项时为 {@code null}
     */
    private static ComboBox<String> comboIfMultiple(String[] values) {
        if (values == null || values.length <= 1) {
            return null;
        }
        ComboBox<String> box = new ComboBox<>(values, 300);
        box.setSelectedIndex(0);
        return box;
    }

    /**
     * 构建密码 / 盐 / IV 下拉表单，只展示需要选择的项。
     *
     * @return 对话框中心面板
     */
    @Override
    protected JComponent createCenterPanel() {
        FormBuilder builder = FormBuilder.createFormBuilder()
                .addComponent(new JLabel("Multiple values detected. Choose parameters for encryption:"))
                .addVerticalGap(10);
        if (passwordBox != null) {
            builder.addLabeledComponent(new JLabel("Password:"), passwordBox, 1, false);
        }
        if (saltBox != null) {
            builder.addLabeledComponent(new JLabel("Salt:"), saltBox, 1, false);
        }
        if (ivBox != null) {
            builder.addLabeledComponent(new JLabel("IV:"), ivBox, 1, false);
        }
        JPanel panel = builder.getPanel();
        panel.setBorder(JBUI.Borders.empty(5, 0));
        panel.setPreferredSize(new Dimension(350, panel.getPreferredSize().height));
        return panel;
    }

    /**
     * 读取用户选择；未展示的下拉回退为列表第一项或空串。
     *
     * @return 加密参数
     */
    public EncryptParams getParams() {
        return new EncryptParams(
                selectedOrFirst(passwordBox, passwords),
                selectedOrFirst(saltBox, salts),
                selectedOrFirst(ivBox, ivs)
        );
    }

    /**
     * 有下拉则取选中项，否则取列表第一项。
     *
     * @param box    可能为 {@code null} 的下拉
     * @param values 原始列表
     * @return 选定值
     */
    private static String selectedOrFirst(ComboBox<String> box, String[] values) {
        if (box != null) {
            Object selected = box.getSelectedItem();
            return selected == null ? JasyptUtils.firstOrEmpty(values) : String.valueOf(selected);
        }
        return JasyptUtils.firstOrEmpty(values);
    }

    /**
     * 弹出对话框让用户选择加密密码（兼容仅多密码的旧调用）。
     *
     * @param project   当前项目
     * @param passwords 可供选择的密码
     * @return 用户选择的密码；点取消则为 {@code null}
     */
    public static String choose(Project project, String[] passwords) {
        EncryptParams params = choose(project, passwords, new String[0], new String[0]);
        return params == null ? null : params.getPassword();
    }

    /**
     * 弹出对话框让用户选择加密用的密码、固定盐、固定 IV。
     *
     * @param project   当前项目
     * @param passwords 可供选择的密码
     * @param salts     可供选择的固定盐
     * @param ivs       可供选择的固定 IV
     * @return 用户选择的参数；点取消则为 {@code null}
     */
    public static EncryptParams choose(Project project, String[] passwords, String[] salts, String[] ivs) {
        ChoosePasswordDialog dialog = new ChoosePasswordDialog(project, passwords, salts, ivs);
        if (!dialog.showAndGet()) {
            return null;
        }
        return dialog.getParams();
    }
}
