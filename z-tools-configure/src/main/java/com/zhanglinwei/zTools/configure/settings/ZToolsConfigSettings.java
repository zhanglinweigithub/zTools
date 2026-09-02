package com.zhanglinwei.zTools.configure.settings;

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
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.io.HttpRequests;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import com.zhanglinwei.zTools.configure.config.DocumentConfig;
import com.zhanglinwei.zTools.configure.config.JasyptCryptoConfig;
import com.zhanglinwei.zTools.configure.config.YApiConfig;
import com.zhanglinwei.zTools.common.enums.DocumentType;
import com.zhanglinwei.zTools.configure.enums.JasyptIV;
import com.zhanglinwei.zTools.configure.enums.JasyptOutputType;
import com.zhanglinwei.zTools.configure.enums.JasyptSalt;
import org.jasypt.registry.AlgorithmRegistry;
import org.jdesktop.swingx.JXTextField;
import org.jdesktop.swingx.prompt.PromptSupport;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.zhanglinwei.zTools.common.constant.StringPool.EMPTY;
import static com.zhanglinwei.zTools.common.constant.StringPool.SLASH;

/**
 * 插件设置页：文档、敏感数据、YApi 各自一个 Tab。
 * <p>
 * Apply 时把控件值写回 {@link DocumentConfig}、{@link JasyptCryptoConfig}、{@link YApiConfig}，
 * 由 IDEA 持久化到项目 {@code zTools.xml}。
 */
public class ZToolsConfigSettings implements Configurable {

    /** 从 YApi {@code /api/project/get} 响应里抠 {@code "_id"} */
    private static final Pattern YAPI_PROJECT_ID = Pattern.compile("\"_id\"\\s*:\\s*(\\d+)");

    private final Project project;
    private final DocumentConfig documentConfig;
    private final JasyptCryptoConfig jasyptCryptoConfig;
    private final YApiConfig yApiConfig;

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

    /**
     * 绑定当前项目的三份持久化配置。
     *
     * @param project 当前工程
     */
    public ZToolsConfigSettings(Project project) {
        this.project = project;
        documentConfig = DocumentConfig.getInstance(project);
        jasyptCryptoConfig = JasyptCryptoConfig.getInstance(project);
        yApiConfig = YApiConfig.getInstance(project);
    }

    /**
     * 设置页在列表中的显示名。
     *
     * @return {@code z-tools}
     */
    @Override
    public String getDisplayName() {
        return "z-tools";
    }

    /**
     * 构建三个 Tab：文档、Jasypt、YApi。
     *
     * @return 设置页根组件
     */
    @Override
    public JComponent createComponent() {
        JBTabbedPane tabbedPane = new JBTabbedPane();
        tabbedPane.addTab("API & DB Document", wrapTop(createDocumentPanel()));
        tabbedPane.addTab("Jasypt Crypto", wrapTop(createSensitivePanel()));
        tabbedPane.addTab("YApi", wrapTop(createYApiPanel()));
        return tabbedPane;
    }

    /**
     * 把表单顶对齐，避免内容被垂直拉伸。
     *
     * @param content 表单面板
     * @return 包了一层 BorderLayout 的面板
     */
    private static JPanel wrapTop(JPanel content) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(content, BorderLayout.NORTH);
        return wrapper;
    }

    /**
     * 文档 Tab：保存目录、类型、覆盖、API 排除字段。
     *
     * @return 文档表单
     */
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

        JPanel panel = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JLabel("Save Directory"), saveDirectory, 1, false)
                .setVerticalGap(10)
                .addLabeledComponent(new JLabel("Doc Type"), docTypeBox, 2, false)
                .setVerticalGap(10)
                .addLabeledComponent(new JLabel("Overwrite exists docs"), overwriteBox, 3, false)
                .setVerticalGap(10)
                .addLabeledComponent(new JLabel("Exclude Fields (a;b): "), excludeFields, 4, false)
                .getPanel();
        panel.setBorder(JBUI.Borders.empty(10));
        return panel;
    }

    /**
     * Jasypt Tab：密码、算法、迭代、输出编码、包裹格式、盐与 IV。
     *
     * @return 加解密表单
     */
    private JPanel createSensitivePanel() {
        passwordField = new JPasswordField(20);
        passwordField.setText(jasyptCryptoConfig.getPassword());
        passwordField.setEchoChar((char) 0);
        PromptSupport.setPrompt("Multiple passwords separated by ;", passwordField);
        PromptSupport.setForeground(JBColor.GRAY, passwordField);
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

        algorithmBox = new ComboBox<>(400);
        for (Object algo : AlgorithmRegistry.getAllPBEAlgorithms()) {
            if (algo instanceof String) {
                algorithmBox.addItem(algo.toString());
            }
        }
        algorithmBox.setSelectedItem(jasyptCryptoConfig.getCryptoAlgorithm());

        iterationsSpinner = new JSpinner(new SpinnerNumberModel(
                jasyptCryptoConfig.getKeyObtentionIterations(), 1, 100000, 100));

        outputTypeBox = new ComboBox<>();
        JasyptOutputType.OUTPUT_OPTIONS.forEach(output -> outputTypeBox.addItem(output));
        outputTypeBox.setSelectedItem(jasyptCryptoConfig.getOutputType());

        encWrapperField = new JTextField(jasyptCryptoConfig.getEncWrapper(), 15);

        saltGeneratorBox = new ComboBox<>();
        JasyptSalt.SALT_OPTIONS.forEach(salt -> saltGeneratorBox.addItem(salt));
        saltGeneratorBox.setSelectedItem(jasyptCryptoConfig.getSaltGenerator());

        ivGeneratorBox = new ComboBox<>();
        JasyptIV.IV_OPTIONS.forEach(iv -> ivGeneratorBox.addItem(iv));
        ivGeneratorBox.setSelectedItem(jasyptCryptoConfig.getIvGenerator());

        JPanel panel = FormBuilder.createFormBuilder()
                .setVerticalGap(10)
                .addLabeledComponent(new JLabel("Password (a;b):"), passwordPanel, 1, false)
                .addLabeledComponent(new JLabel("Algorithm:"), algorithmBox, 2, false)
                .addLabeledComponent(new JLabel("Iterations:"), iterationsSpinner, 3, false)
                .addLabeledComponent(new JLabel("Output Type:"), outputTypeBox, 4, false)
                .addLabeledComponent(new JLabel("Enc Wrapper:"), encWrapperField, 5, false)
                .addLabeledComponent(new JLabel("Salt Generator:"), saltGeneratorBox, 6, false)
                .addLabeledComponent(new JLabel("IV Generator:"), ivGeneratorBox, 7, false)
                .getPanel();
        panel.setBorder(JBUI.Borders.empty(10));
        return panel;
    }

    /**
     * YApi Tab：服务地址、Token、只读项目 ID 与 Resolve 按钮。
     *
     * @return YApi 表单
     */
    private JPanel createYApiPanel() {
        yapiServerUrlField = new JBTextField();
        yapiTokenField = new JBTextField();
        yapiProjectIdField = new JBTextField();
        yapiProjectIdField.setEnabled(false);
        yapiServerUrlField.setText(yApiConfig.getServerUrl());
        yapiTokenField.setText(yApiConfig.getToken());
        yapiProjectIdField.setText(yApiConfig.getProjectId());

        JButton resolveButton = new JButton("Resolve");
        resolveButton.addActionListener(e -> resolveYApiProjectId());

        JPanel idPanel = new JPanel(new BorderLayout(8, 0));
        idPanel.add(yapiProjectIdField, BorderLayout.CENTER);
        idPanel.add(resolveButton, BorderLayout.EAST);

        JPanel panel = FormBuilder.createFormBuilder()
                .setVerticalGap(10)
                .addLabeledComponent(new JBLabel("YApi Server URL"), yapiServerUrlField, 1, false)
                .addLabeledComponent(new JBLabel("Project Token"), yapiTokenField, 2, false)
                .addLabeledComponent(new JBLabel("Project ID"), idPanel, 3, false)
                .getPanel();
        panel.setBorder(JBUI.Borders.empty(10));
        return panel;
    }

    /**
     * 用 Server URL + Token 请求 YApi，把解析到的项目 ID 填进控件并立刻写回配置。
     */
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
                        yApiConfig.setProjectId(projectId);
                    });
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() ->
                            Messages.showErrorDialog(project, "解析项目 ID 失败: " + ex.getMessage(), "YApi"));
                }
            }
        });
    }

    /**
     * 调用 {@code /api/project/get}，从 JSON 中取出 {@code _id}。
     *
     * @param serverUrl YApi 根地址
     * @param token     项目 Token
     * @return 项目数字 ID；errcode 非 0 或解析失败则为 {@code null}
     * @throws Exception 网络或读取失败
     */
    private static String lookupYApiProjectId(String serverUrl, String token) throws Exception {
        String base = serverUrl.endsWith(SLASH) ? serverUrl.substring(0, serverUrl.length() - 1) : serverUrl;
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

    /**
     * 向 ComboBox 追加一项。
     *
     * @param box   下拉框
     * @param value 选项文本
     */
    private static void addItem(ComboBox<String> box, String value) {
        box.addItem(value);
    }

    /**
     * 弹出文件夹选择器，把选中路径填进保存目录。
     */
    private void chooseFolder() {
        FileChooserDescriptor fileChooserDescriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
        Project active = ProjectUtil.getActiveProject();
        VirtualFile selectedFile = FileChooser.chooseFile(fileChooserDescriptor, active, null);
        if (selectedFile != null) {
            saveDirectory.setText(selectedFile.getPath());
        }
    }

    /**
     * 控件值是否与持久化配置不同（决定 Apply 是否可点）。
     *
     * @return 有未保存修改则为 {@code true}
     */
    @Override
    public boolean isModified() {
        String password = new String(passwordField.getPassword());
        return !documentConfig.getApiDocConfig().getExcludeFields().equals(excludeFields.getText())
                || !documentConfig.getSaveDir().equals(saveDirectory.getText())
                || documentConfig.isOverwriteDoc() != overwriteBox.isSelected()
                || !documentConfig.getDocType().equals(docTypeBox.getSelectedItem())
                || !jasyptCryptoConfig.getPassword().equals(password)
                || !jasyptCryptoConfig.getCryptoAlgorithm().equals(algorithmBox.getSelectedItem())
                || jasyptCryptoConfig.getKeyObtentionIterations() != (Integer) iterationsSpinner.getValue()
                || !jasyptCryptoConfig.getOutputType().equals(outputTypeBox.getSelectedItem())
                || !jasyptCryptoConfig.getEncWrapper().equals(encWrapperField.getText())
                || !jasyptCryptoConfig.getSaltGenerator().equals(saltGeneratorBox.getSelectedItem())
                || !jasyptCryptoConfig.getIvGenerator().equals(ivGeneratorBox.getSelectedItem())
                || !equalsField(yapiServerUrlField, yApiConfig.getServerUrl())
                || !equalsField(yapiTokenField, yApiConfig.getToken());
    }

    /**
     * 把三个 Tab 的控件值写回对应 Config。
     */
    @Override
    public void apply() {
        documentConfig.getApiDocConfig().setExcludeFields(excludeFields.getText());
        documentConfig.setSaveDir(saveDirectory.getText());
        documentConfig.setOverwriteDoc(overwriteBox.isSelected());
        documentConfig.setDocType(String.valueOf(docTypeBox.getSelectedItem()));

        jasyptCryptoConfig.setPassword(new String(passwordField.getPassword()));
        jasyptCryptoConfig.setCryptoAlgorithm(String.valueOf(algorithmBox.getSelectedItem()));
        jasyptCryptoConfig.setKeyObtentionIterations((Integer) iterationsSpinner.getValue());
        jasyptCryptoConfig.setOutputType(String.valueOf(outputTypeBox.getSelectedItem()));
        jasyptCryptoConfig.setEncWrapper(encWrapperField.getText());
        jasyptCryptoConfig.setSaltGenerator(String.valueOf(saltGeneratorBox.getSelectedItem()));
        jasyptCryptoConfig.setIvGenerator(String.valueOf(ivGeneratorBox.getSelectedItem()));

        yApiConfig.setServerUrl(yapiServerUrlField.getText().trim());
        yApiConfig.setToken(yapiTokenField.getText().trim());
        yApiConfig.setProjectId(yapiProjectIdField.getText().trim());
    }

    /**
     * 用持久化配置重置三个 Tab 的控件。
     */
    @Override
    public void reset() {
        saveDirectory.setText(documentConfig.getSaveDir());
        docTypeBox.setSelectedItem(documentConfig.getDocType());
        overwriteBox.setSelected(documentConfig.isOverwriteDoc());
        excludeFields.setText(documentConfig.getApiDocConfig().getExcludeFields());

        passwordField.setText(jasyptCryptoConfig.getPassword());
        algorithmBox.setSelectedItem(jasyptCryptoConfig.getCryptoAlgorithm());
        iterationsSpinner.setValue(jasyptCryptoConfig.getKeyObtentionIterations());
        outputTypeBox.setSelectedItem(jasyptCryptoConfig.getOutputType());
        encWrapperField.setText(jasyptCryptoConfig.getEncWrapper());
        saltGeneratorBox.setSelectedItem(jasyptCryptoConfig.getSaltGenerator());
        ivGeneratorBox.setSelectedItem(jasyptCryptoConfig.getIvGenerator());

        yapiServerUrlField.setText(yApiConfig.getServerUrl());
        yapiTokenField.setText(yApiConfig.getToken());
        yapiProjectIdField.setText(yApiConfig.getProjectId());
    }

    /**
     * 比较文本框（trim 后）与配置值是否相同。
     *
     * @param field 文本框
     * @param value 配置中的值，{@code null} 视为空串
     * @return 相同则为 {@code true}
     */
    private static boolean equalsField(JTextField field, String value) {
        return field.getText().trim().equals(value != null ? value : EMPTY);
    }
}
