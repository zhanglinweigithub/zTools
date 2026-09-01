package com.zhanglinwei.zTools.sensitive.config;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.TitledSeparator;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import com.zhanglinwei.zTools.sensitive.constants.SensitiveDataConstant;
import org.jasypt.registry.AlgorithmRegistry;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class JasyptConfigSettings implements Configurable {

    private final SensitiveDataConfig sensitiveDataConfig;

    // 基础字段
    private JPasswordField passwordField;
    private ComboBox<String> algorithmBox;
    private JSpinner iterationsSpinner;
    private ComboBox<String> outputTypeBox;

    // 包裹标签字段
    private JTextField encWrapperField;

    // 高级字段
    private ComboBox<String> saltGeneratorBox;
    private ComboBox<String> ivGeneratorBox;

    public JasyptConfigSettings(Project project) {
        sensitiveDataConfig = SensitiveDataConfig.getInstance(project);
    }

    @Override
    public String getDisplayName() {
        return "zzz-sensitive";
    }

    @Override
    public JComponent createComponent() {
        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBorder(JBUI.Borders.empty(10));

        // ── 标题 ──
        JLabel titleLabel = new JLabel("Jasypt Encrypt / Decrypt");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 16f));
        titleLabel.setBorder(JBUI.Borders.emptyBottom(10));

        // ── 基础面板 ──
        FormBuilder basicBuilder = FormBuilder.createFormBuilder();
        basicBuilder.setVerticalGap(10);

        // Password*（必填，默认明文显示 + 显示/隐藏切换按钮）
        passwordField = new JPasswordField(20);
        passwordField.setText(sensitiveDataConfig.getPassword());
        passwordField.setEchoChar((char) 0); // 默认明文显示
        JPanel passwordPanel = new JPanel(new BorderLayout(5, 0));
        passwordPanel.add(passwordField, BorderLayout.CENTER);
        JToggleButton togglePwdBtn = new JToggleButton("Hide");
        togglePwdBtn.setSelected(false); // 未选中 = 当前明文状态
        togglePwdBtn.addActionListener((ActionEvent e) -> {
            if (togglePwdBtn.isSelected()) {
                // 切换为密码隐藏样式
                passwordField.setEchoChar('•');
                togglePwdBtn.setText("Show");
            } else {
                // 切换为明文显示
                passwordField.setEchoChar((char) 0);
                togglePwdBtn.setText("Hide");
            }
        });
        passwordPanel.add(togglePwdBtn, BorderLayout.EAST);
        basicBuilder.addLabeledComponent(new JLabel("Password:"), passwordPanel, 1, false);

        // Algorithm（下拉框填满可用宽度）
        algorithmBox = new ComboBox<>();
        AlgorithmRegistry.getAllPBEAlgorithms().forEach(algo -> {
            algorithmBox.addItem(algo.toString());
        });
        algorithmBox.setSelectedItem(sensitiveDataConfig.getCryptoAlgorithm());
        // 让下拉框横向填满可用空间
        algorithmBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, algorithmBox.getPreferredSize().height));
        algorithmBox.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        JPanel algorithmPanel = new JPanel();
        algorithmPanel.setLayout(new BoxLayout(algorithmPanel, BoxLayout.LINE_AXIS));
        algorithmPanel.add(algorithmBox);
        basicBuilder.addLabeledComponent(new JLabel("Algorithm:"), algorithmPanel, 2, false);

        // Iterations
        iterationsSpinner = new JSpinner(new SpinnerNumberModel(
                sensitiveDataConfig.getKeyObtentionIterations(), 1, 100000, 100));
        basicBuilder.addLabeledComponent(new JLabel("Iterations:"), iterationsSpinner, 3, false);

        // Output Type
        outputTypeBox = new ComboBox<>();
        outputTypeBox.addItem(SensitiveDataConstant.OUTPUT_TYPE_BASE64);
        outputTypeBox.addItem(SensitiveDataConstant.OUTPUT_TYPE_HEXADECIMAL);
        outputTypeBox.setSelectedItem(sensitiveDataConfig.getOutputType());
        basicBuilder.addLabeledComponent(new JLabel("Output Type:"), outputTypeBox, 4, false);

        // ── 包裹标签（合并为一个字段） ──
        encWrapperField = new JTextField(sensitiveDataConfig.getEncWrapper(), 15);
        basicBuilder.addLabeledComponent(new JLabel("Enc Wrapper:"), encWrapperField, 5, false);

        // ── Advanced 分割线 ──
        TitledSeparator advancedSeparator = new TitledSeparator("Advanced");

        // ── 高级面板（始终可见，与上方对齐，无缩进） ──
        FormBuilder advancedBuilder = FormBuilder.createFormBuilder();
        advancedBuilder.setVerticalGap(10);

        // Salt Generator
        saltGeneratorBox = new ComboBox<>();
        saltGeneratorBox.addItem(SensitiveDataConstant.SALT_RANDOM);
        saltGeneratorBox.addItem(SensitiveDataConstant.SALT_ZERO);
        saltGeneratorBox.setSelectedItem(sensitiveDataConfig.getSaltGenerator());
        advancedBuilder.addLabeledComponent(new JLabel("Salt Generator:"), saltGeneratorBox, 1, false);

        // IV Generator
        ivGeneratorBox = new ComboBox<>();
        ivGeneratorBox.addItem(SensitiveDataConstant.IV_NO);
        ivGeneratorBox.addItem(SensitiveDataConstant.IV_RANDOM);
        ivGeneratorBox.setSelectedItem(sensitiveDataConfig.getIvGenerator());
        advancedBuilder.addLabeledComponent(new JLabel("IV Generator:"), ivGeneratorBox, 2, false);

        // ── 组装 ──
        FormBuilder formBuilder = FormBuilder.createFormBuilder();
        formBuilder.addComponent(titleLabel);
        formBuilder.setVerticalGap(10);
        formBuilder.addComponent(basicBuilder.getPanel());
        formBuilder.setVerticalGap(15);
        formBuilder.addComponent(advancedSeparator);
        formBuilder.addComponent(advancedBuilder.getPanel());

        rootPanel.add(formBuilder.getPanel(), BorderLayout.NORTH);

        return rootPanel;
    }

    @Override
    public boolean isModified() {
        return isSensitiveDataModified();
    }

    private boolean isSensitiveDataModified() {
        String currentPassword = new String(passwordField.getPassword());
        return !sensitiveDataConfig.getPassword().equals(currentPassword) ||
                !sensitiveDataConfig.getCryptoAlgorithm().equals(algorithmBox.getSelectedItem()) ||
                sensitiveDataConfig.getKeyObtentionIterations() != (Integer) iterationsSpinner.getValue() ||
                !sensitiveDataConfig.getOutputType().equals(outputTypeBox.getSelectedItem()) ||
                !sensitiveDataConfig.getEncWrapper().equals(encWrapperField.getText()) ||
                !sensitiveDataConfig.getSaltGenerator().equals(saltGeneratorBox.getSelectedItem()) ||
                !sensitiveDataConfig.getIvGenerator().equals(ivGeneratorBox.getSelectedItem());
    }

    @Override
    public void apply() {
        sensitiveDataApply();
    }

    private void sensitiveDataApply() {
        sensitiveDataConfig.setPassword(new String(passwordField.getPassword()));
        sensitiveDataConfig.setCryptoAlgorithm(String.valueOf(algorithmBox.getSelectedItem()));
        sensitiveDataConfig.setKeyObtentionIterations((Integer) iterationsSpinner.getValue());
        sensitiveDataConfig.setOutputType(String.valueOf(outputTypeBox.getSelectedItem()));
        sensitiveDataConfig.setEncWrapper(encWrapperField.getText());
        sensitiveDataConfig.setSaltGenerator(String.valueOf(saltGeneratorBox.getSelectedItem()));
        sensitiveDataConfig.setIvGenerator(String.valueOf(ivGeneratorBox.getSelectedItem()));
    }

    @Override
    public void reset() {
        passwordField.setText(sensitiveDataConfig.getPassword());
        algorithmBox.setSelectedItem(sensitiveDataConfig.getCryptoAlgorithm());
        iterationsSpinner.setValue(sensitiveDataConfig.getKeyObtentionIterations());
        outputTypeBox.setSelectedItem(sensitiveDataConfig.getOutputType());
        encWrapperField.setText(sensitiveDataConfig.getEncWrapper());
        saltGeneratorBox.setSelectedItem(sensitiveDataConfig.getSaltGenerator());
        ivGeneratorBox.setSelectedItem(sensitiveDataConfig.getIvGenerator());
    }
}
