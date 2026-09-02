package com.zhanglinwei.zTools.yapi.ui;


import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.zhanglinwei.zTools.yapi.client.YApiClient;
import com.zhanglinwei.zTools.configure.config.YApiConfig;
import com.zhanglinwei.zTools.yapi.model.YApiProject;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * YApi 未配置时弹出的配置对话框
 */
public class YApiConfigDialog extends DialogWrapper {

    private final Project project;
    private JTextField serverUrlField;
    private JTextField tokenField;
    private JLabel statusLabel;

    public YApiConfigDialog(Project project) {
        super(project);
        this.project = project;
        setTitle("Configure YApi Connection");
        init();
    }

    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // 提示信息
        JLabel hintLabel = new JLabel("请配置 YApi 连接信息以上传接口:");
        panel.add(hintLabel);
        panel.add(Box.createVerticalStrut(15));

        // YApi Server URL
        JPanel urlPanel = new JPanel();
        urlPanel.setLayout(new BoxLayout(urlPanel, BoxLayout.X_AXIS));
        urlPanel.add(new JLabel("YApi Server URL:  "));
        serverUrlField = new JTextField(25);
        urlPanel.add(serverUrlField);
        panel.add(urlPanel);
        panel.add(Box.createVerticalStrut(10));

        // Project Token
        JPanel tokenPanel = new JPanel();
        tokenPanel.setLayout(new BoxLayout(tokenPanel, BoxLayout.X_AXIS));
        tokenPanel.add(new JLabel("Project Token:        "));
        tokenField = new JTextField(25);
        tokenPanel.add(tokenField);
        panel.add(tokenPanel);
        panel.add(Box.createVerticalStrut(10));

        // 状态标签
        statusLabel = new JLabel(" ");
        panel.add(statusLabel);

        return panel;
    }

    @Override
    protected void doOKAction() {
        String serverUrl = serverUrlField.getText().trim();
        String token = tokenField.getText().trim();

        if (serverUrl.isEmpty() || token.isEmpty()) {
            statusLabel.setText("请填写 YApi Server URL 和 Project Token");
            return;
        }

        // 禁用 OK 按钮，防止重复点击
        getOKAction().setEnabled(false);
        statusLabel.setText("正在解析项目信息...");

        ProgressManager.getInstance().run(new Task.Modal(project, "Resolving YApi Project...", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setIndeterminate(true);
                try {
                    YApiProject projectInfo = YApiClient.getProject(serverUrl, token);
                    if (projectInfo != null && projectInfo.get_id() != null) {
                        String resolvedId = String.valueOf(projectInfo.get_id());
                        // 保存设置
                        SwingUtilities.invokeLater(() -> {
                            YApiConfig settings = YApiConfig.getInstance(project);
                            settings.setServerUrl(serverUrl);
                            settings.setToken(token);
                            settings.setProjectId(resolvedId);
                            close(OK_EXIT_CODE);
                        });
                    } else {
                        SwingUtilities.invokeLater(() -> {
                            statusLabel.setText("无法解析项目 ID，请检查 Token 是否正确");
                            getOKAction().setEnabled(true);
                        });
                    }
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        statusLabel.setText("连接失败: " + ex.getMessage());
                        getOKAction().setEnabled(true);
                    });
                }
            }
        });
    }


    @Override
    protected Action @NotNull [] createActions() {
        return new Action[]{getOKAction(), getCancelAction()};
    }


    protected String getOKActionText() {
        return "Save & Continue";
    }
}
