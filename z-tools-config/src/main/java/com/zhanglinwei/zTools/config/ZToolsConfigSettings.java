package com.zhanglinwei.zTools.config;

import com.intellij.ide.impl.ProjectUtil;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.io.HttpRequests;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import com.zhanglinwei.zTools.doc.config.DocumentConfig;
import com.zhanglinwei.zTools.enums.DocumentType;
import com.zhanglinwei.zTools.sensitive.config.SensitiveDataConfig;
import com.zhanglinwei.zTools.sensitive.constants.SensitiveDataConstant;
import com.zhanglinwei.zTools.yapi.config.YApiSettings;
import org.jdesktop.swingx.JXTextField;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 插件设置页：文档、敏感数据、YApi 在同一页，后两块各自一个标题。
 */
public class ZToolsConfigSettings implements Configurable {

    private static final Pattern YAPI_PROJECT_ID = Pattern.compile("\"_id\"\\s*:\\s*(\\d+)");

    private final Project project;
    private final DocumentConfig documentConfig;
    private final SensitiveDataConfig sensitiveDataConfig;
    private final YApiSettings yapiSettings;

    private JXTextField excludeFields;
    private TextFieldWithBrowseButton saveDirectory;
    private JBCheckBox overwriteBox;
    private ComboBox<String> docTypeBox;

    private JPasswordField passwordField;
    private ComboBox<String> algorithmBox;
    private JSpinner iterationsSpinner;
    private ComboBox<String> outputTypeBox;
    private JTextField encWrapperField;
    private ComboBox<String> saltGeneratorBox;
    private ComboBox<String> ivGeneratorBox;

    private JBTextField yapiServerUrlField;
    private JBTextField yapiTokenField;
    private JBTextField yapiProjectIdField;

    public ZToolsConfigSettings(Project project) {
        this.project = project;
        documentConfig = DocumentConfig.getInstance(project);
        sensitiveDataConfig = SensitiveDataConfig.getInstance(project);
        yapiSettings = YApiSettings.getInstance(project);
    }

    @Override
    public String getDisplayName() {
        return "z-tools";
    }

    @Override
    public JComponent createComponent() {
        JPanel form = FormBuilder.createFormBuilder()
                .addComponent(titled("Document", createDocumentPanel()))
                .setVerticalGap(16)
                .addComponent(titled("Sensitive Data", createSensitivePanel()))
                .setVerticalGap(16)
                .addComponent(titled("YApi", createYApiPanel()))
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
        form.setBorder(JBUI.Borders.empty(10));
        return new JBScrollPane(form);
    }

    private JPanel createDocumentPanel() {
        saveDirectory = new TextFieldWithBrowseButton();
        saveDirectory.setText(documentConfig.getSaveDir());
        saveDirectory.addActionListener(e -> chooseFolder());

        docTypeBox = new ComboBox<String>();
        DocumentType[] docs = DocumentType.values();
        for (int i = 0; i < docs.length; i++) {
            docTypeBox.addItem(docs[i].getType());
        }
        docTypeBox.setSelectedItem(documentConfig.getDocType());

        overwriteBox = new JBCheckBox();
        overwriteBox.setSelected(documentConfig.isOverwriteDoc());

        excludeFields = new JXTextField();
        excludeFields.setPrompt("Patterns should be separated with \";\"");
        excludeFields.setPromptForeground(JBColor.GRAY);
        excludeFields.setText(documentConfig.getApiDocConfig().getExcludeFields());

        return FormBuilder.createFormBuilder()
                .addLabeledComponent(new JLabel("Save Directory"), saveDirectory, 1, false)
                .setVerticalGap(10)
                .addLabeledComponent(new JLabel("Doc Type"), docTypeBox, 2, false)
                .setVerticalGap(10)
                .addLabeledComponent(new JLabel("Overwrite exists docs"), overwriteBox, 3, false)
                .setVerticalGap(10)
                .addLabeledComponent(new JLabel("Exclude Fields (a;b): "), excludeFields, 4, false)
                .getPanel();
    }

    private JPanel createSensitivePanel() {
        passwordField = new JPasswordField(20);
        passwordField.setText(sensitiveDataConfig.getPassword());
        passwordField.setEchoChar((char) 0);
        JPanel passwordPanel = new JPanel(new BorderLayout(5, 0));
        passwordPanel.add(passwordField, BorderLayout.CENTER);
        JToggleButton togglePwdBtn = new JToggleButton("Hide");
        togglePwdBtn.addActionListener((ActionEvent e) -> {
            if (togglePwdBtn.isSelected()) {
                passwordField.setEchoChar('•');
                togglePwdBtn.setText("Show");
            } else {
                passwordField.setEchoChar((char) 0);
                togglePwdBtn.setText("Hide");
            }
        });
        passwordPanel.add(togglePwdBtn, BorderLayout.EAST);

        algorithmBox = new ComboBox<String>();
        AlgorithmRegistry

        addItem(algorithmBox, SensitiveDataConstant.PBE_WITH_MD5_AND_DES);
        addItem(algorithmBox, SensitiveDataConstant.PBE_WITH_MD5_AND_TRIPLE_DES);
        addItem(algorithmBox, SensitiveDataConstant.PBE_WITH_SHA1_AND_DESEDE);
        addItem(algorithmBox, SensitiveDataConstant.PBE_WITH_HMAC_SHA512_AND_AES_256);
        algorithmBox.setSelectedItem(sensitiveDataConfig.getCryptoAlgorithm());

        iterationsSpinner = new JSpinner(new SpinnerNumberModel(
                sensitiveDataConfig.getKeyObtentionIterations(), 1, 100000, 100));

        outputTypeBox = new ComboBox<String>();
        addItem(outputTypeBox, SensitiveDataConstant.OUTPUT_TYPE_BASE64);
        addItem(outputTypeBox, SensitiveDataConstant.OUTPUT_TYPE_HEXADECIMAL);
        outputTypeBox.setSelectedItem(sensitiveDataConfig.getOutputType());

        encWrapperField = new JTextField(sensitiveDataConfig.getEncWrapper(), 15);

        saltGeneratorBox = new ComboBox<String>();
        addItem(saltGeneratorBox, SensitiveDataConstant.SALT_RANDOM);
        addItem(saltGeneratorBox, SensitiveDataConstant.SALT_ZERO);
        saltGeneratorBox.setSelectedItem(sensitiveDataConfig.getSaltGenerator());

        ivGeneratorBox = new ComboBox<String>();
        addItem(ivGeneratorBox, SensitiveDataConstant.IV_NO);
        addItem(ivGeneratorBox, SensitiveDataConstant.IV_RANDOM);
        ivGeneratorBox.setSelectedItem(sensitiveDataConfig.getIvGenerator());

        return FormBuilder.createFormBuilder()
                .setVerticalGap(10)
                .addLabeledComponent(new JLabel("Password:"), passwordPanel, 1, false)
                .addLabeledComponent(new JLabel("Algorithm:"), algorithmBox, 2, false)
                .addLabeledComponent(new JLabel("Iterations:"), iterationsSpinner, 3, false)
                .addLabeledComponent(new JLabel("Output Type:"), outputTypeBox, 4, false)
                .addLabeledComponent(new JLabel("Enc Wrapper:"), encWrapperField, 5, false)
                .addLabeledComponent(new JLabel("Salt Generator:"), saltGeneratorBox, 6, false)
                .addLabeledComponent(new JLabel("IV Generator:"), ivGeneratorBox, 7, false)
                .getPanel();
    }

    private JPanel createYApiPanel() {
        yapiServerUrlField = new JBTextField();
        yapiTokenField = new JBTextField();
        yapiProjectIdField = new JBTextField();
        yapiProjectIdField.setEnabled(false);
        yapiServerUrlField.setText(yapiSettings.getServerUrl());
        yapiTokenField.setText(yapiSettings.getToken());
        yapiProjectIdField.setText(yapiSettings.getProjectId());

        JButton resolveButton = new JButton("Resolve");
        resolveButton.addActionListener(e -> resolveYApiProjectId());

        JPanel idPanel = new JPanel(new BorderLayout(8, 0));
        idPanel.add(yapiProjectIdField, BorderLayout.CENTER);
        idPanel.add(resolveButton, BorderLayout.EAST);

        return FormBuilder.createFormBuilder()
                .setVerticalGap(10)
                .addLabeledComponent(new JBLabel("YApi Server URL"), yapiServerUrlField, 1, false)
                .addLabeledComponent(new JBLabel("Project Token"), yapiTokenField, 2, false)
                .addLabeledComponent(new JBLabel("Project ID"), idPanel, 3, false)
                .getPanel();
    }

    private void resolveYApiProjectId() {
        String serverUrl = yapiServerUrlField.getText().trim();
        String token = yapiTokenField.getText().trim();
        if (serverUrl.isEmpty() || token.isEmpty()) {
            Messages.showWarningDialog(project, "请先填写 YApi Server URL 和 Project Token", "YApi");
            return;
        }
        ProgressManager.getInstance().run(new Task.Modal(project, "Resolving Project ID...", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setIndeterminate(true);
                try {
                    String projectId = lookupYApiProjectId(serverUrl, token);
                    SwingUtilities.invokeLater(() -> {
                        if (projectId == null) {
                            Messages.showWarningDialog(project, "无法解析项目 ID，请检查 Token 是否正确", "YApi");
                            return;
                        }
                        yapiProjectIdField.setText(projectId);
                        yapiSettings.setProjectId(projectId);
                    });
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() ->
                            Messages.showErrorDialog(project, "解析项目 ID 失败: " + ex.getMessage(), "YApi"));
                }
            }
        });
    }

    private static String lookupYApiProjectId(String serverUrl, String token) throws Exception {
        String base = serverUrl.endsWith("/") ? serverUrl.substring(0, serverUrl.length() - 1) : serverUrl;
        String response = HttpRequests.request(base + "/api/project/get?token=" + token)
                .tuner(connection -> connection.setRequestProperty("Accept", "application/json"))
                .readString();
        if (response != null && response.contains("\"errcode\":0")) {
            Matcher matcher = YAPI_PROJECT_ID.matcher(response);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        return null;
    }

    private static void addItem(ComboBox<String> box, String value) {
        box.addItem(value);
    }

    private static JPanel titled(String title, JPanel inner) {
        Border border = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
        inner.setBorder(BorderFactory.createTitledBorder(
                border,
                " " + title + " ",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Dialog", Font.BOLD, 14),
                JBColor.black
        ));
        return inner;
    }

    private void chooseFolder() {
        FileChooserDescriptor fileChooserDescriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
        Project active = ProjectUtil.getActiveProject();
        VirtualFile selectedFile = FileChooser.chooseFile(fileChooserDescriptor, active, null);
        if (selectedFile != null) {
            saveDirectory.setText(selectedFile.getPath());
        }
    }

    @Override
    public boolean isModified() {
        String password = new String(passwordField.getPassword());
        return !documentConfig.getApiDocConfig().getExcludeFields().equals(excludeFields.getText())
                || !documentConfig.getSaveDir().equals(saveDirectory.getText())
                || documentConfig.isOverwriteDoc() != overwriteBox.isSelected()
                || !documentConfig.getDocType().equals(docTypeBox.getSelectedItem())
                || !sensitiveDataConfig.getPassword().equals(password)
                || !sensitiveDataConfig.getCryptoAlgorithm().equals(algorithmBox.getSelectedItem())
                || sensitiveDataConfig.getKeyObtentionIterations() != (Integer) iterationsSpinner.getValue()
                || !sensitiveDataConfig.getOutputType().equals(outputTypeBox.getSelectedItem())
                || !sensitiveDataConfig.getEncWrapper().equals(encWrapperField.getText())
                || !sensitiveDataConfig.getSaltGenerator().equals(saltGeneratorBox.getSelectedItem())
                || !sensitiveDataConfig.getIvGenerator().equals(ivGeneratorBox.getSelectedItem())
                || !equalsField(yapiServerUrlField, yapiSettings.getServerUrl())
                || !equalsField(yapiTokenField, yapiSettings.getToken());
    }

    @Override
    public void apply() {
        documentConfig.getApiDocConfig().setExcludeFields(excludeFields.getText());
        documentConfig.setSaveDir(saveDirectory.getText());
        documentConfig.setOverwriteDoc(overwriteBox.isSelected());
        documentConfig.setDocType(String.valueOf(docTypeBox.getSelectedItem()));

        sensitiveDataConfig.setPassword(new String(passwordField.getPassword()));
        sensitiveDataConfig.setCryptoAlgorithm(String.valueOf(algorithmBox.getSelectedItem()));
        sensitiveDataConfig.setKeyObtentionIterations((Integer) iterationsSpinner.getValue());
        sensitiveDataConfig.setOutputType(String.valueOf(outputTypeBox.getSelectedItem()));
        sensitiveDataConfig.setEncWrapper(encWrapperField.getText());
        sensitiveDataConfig.setSaltGenerator(String.valueOf(saltGeneratorBox.getSelectedItem()));
        sensitiveDataConfig.setIvGenerator(String.valueOf(ivGeneratorBox.getSelectedItem()));

        yapiSettings.setServerUrl(yapiServerUrlField.getText().trim());
        yapiSettings.setToken(yapiTokenField.getText().trim());
        yapiSettings.setProjectId(yapiProjectIdField.getText().trim());
    }

    @Override
    public void reset() {
        saveDirectory.setText(documentConfig.getSaveDir());
        docTypeBox.setSelectedItem(documentConfig.getDocType());
        overwriteBox.setSelected(documentConfig.isOverwriteDoc());
        excludeFields.setText(documentConfig.getApiDocConfig().getExcludeFields());

        passwordField.setText(sensitiveDataConfig.getPassword());
        algorithmBox.setSelectedItem(sensitiveDataConfig.getCryptoAlgorithm());
        iterationsSpinner.setValue(sensitiveDataConfig.getKeyObtentionIterations());
        outputTypeBox.setSelectedItem(sensitiveDataConfig.getOutputType());
        encWrapperField.setText(sensitiveDataConfig.getEncWrapper());
        saltGeneratorBox.setSelectedItem(sensitiveDataConfig.getSaltGenerator());
        ivGeneratorBox.setSelectedItem(sensitiveDataConfig.getIvGenerator());

        yapiServerUrlField.setText(yapiSettings.getServerUrl());
        yapiTokenField.setText(yapiSettings.getToken());
        yapiProjectIdField.setText(yapiSettings.getProjectId());
    }

    private static boolean equalsField(JTextField field, String value) {
        return field.getText().trim().equals(value != null ? value : "");
    }
}
