package com.zhanglinwei.zTools.jasyptcrypto.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;

/**
 * 加密密码选择弹窗，当配置了多个密码时弹出让用户选择。
 */
public class ChoosePasswordDialog extends DialogWrapper {

    private final ComboBox<String> passwordBox;

    /**
     * 用已配置的密码列表初始化下拉框，默认选中第一项。
     *
     * @param project   当前项目
     * @param passwords 可供选择的密码
     */
    public ChoosePasswordDialog(Project project, String[] passwords) {
        super(project);
        setTitle("Select Encryption Password");
        setOKButtonText("Encrypt");

        passwordBox = new ComboBox<>(passwords);
        passwordBox.setSelectedIndex(0);

        init();
    }

    /**
     * 构建密码下拉表单。
     *
     * @return 对话框中心面板
     */
    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = FormBuilder.createFormBuilder()
                .addComponent(new JLabel("Multiple passwords detected. Choose one for encryption:"))
                .addVerticalGap(10)
                .addLabeledComponent(new JLabel("Password:"), passwordBox, 1, false)
                .getPanel();
        panel.setBorder(JBUI.Borders.empty(5, 0));
        panel.setPreferredSize(new Dimension(350, panel.getPreferredSize().height));
        return panel;
    }

    /**
     * 弹出对话框让用户选择加密密码。
     *
     * @param project   当前项目
     * @param passwords 可供选择的密码
     * @return 用户选择的密码；点取消则为 {@code null}
     */
    public static String choose(Project project, String[] passwords) {
        ChoosePasswordDialog dialog = new ChoosePasswordDialog(project, passwords);
        if (!dialog.showAndGet()) {
            return null;
        }
        return (String) dialog.passwordBox.getSelectedItem();
    }
}
