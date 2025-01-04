package com.zhanglinwei.zTools.config;

import com.intellij.ide.impl.ProjectUtil;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.util.ui.FormBuilder;
import com.zhanglinwei.zTools.doc.config.DocConfig;
import com.zhanglinwei.zTools.doc.constant.DocType;
import com.zhanglinwei.zTools.sensitive.config.SensitiveDataConfig;
import com.zhanglinwei.zTools.sensitive.constant.SensitiveDataConstant;
import org.jdesktop.swingx.JXTextField;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class ZToolsConfigSettings implements Configurable {

    /**
     * ===================================================== Doc Setting =====================================================
     */

    private final DocConfig docConfig;
    private JXTextField excludeFields;
    private TextFieldWithBrowseButton saveDirectory;
    private JBCheckBox overwriteBox;
    private ComboBox<String> docTypeBox;

    /**
     * ===================================================== Sensitive Data Setting =====================================================
     */

    private final SensitiveDataConfig sensitiveDataConfig;
    private JXTextField secretKeyField;
    private JXTextField ivField;
    private ComboBox<String> cryptoTypeBox;


    public ZToolsConfigSettings(Project project) {
        docConfig = project.getService(DocConfig.class);
        sensitiveDataConfig = SensitiveDataConfig.getInstance(project);
    }

    @Override
    public String getDisplayName() {
        return "z-tools";
    }

    @Override
    public JComponent createComponent() {
        JBTabbedPane zToolsTabbedPane = new JBTabbedPane();

        zToolsTabbedPane.addTab("Doc Setting", createDocSettingPanel());
        zToolsTabbedPane.addTab("Sensitive Data", createSensitiveDataSettingPanel());

        return zToolsTabbedPane;
    }

    private JPanel createSensitiveDataSettingPanel() {
        secretKeyField = new JXTextField();
        secretKeyField.setPrompt("Secret Key");
        secretKeyField.setPromptForeground(JBColor.GRAY);
        secretKeyField.setText(sensitiveDataConfig.getSecretKey());

        ivField = new JXTextField();
        ivField.setPrompt("IV Offset");
        ivField.setPromptForeground(JBColor.GRAY);
        ivField.setText(sensitiveDataConfig.getIv());

        cryptoTypeBox = new ComboBox<>();
        cryptoTypeBox.addItem(SensitiveDataConstant.SM4_CBC);
        cryptoTypeBox.addItem(SensitiveDataConstant.SM4_ECB);
        cryptoTypeBox.addItem(SensitiveDataConstant.AES);
        cryptoTypeBox.setSelectedItem(sensitiveDataConfig.getCryptoAlgorithm());

        return FormBuilder.createFormBuilder()
                .addLabeledComponent(new JLabel("Secret Key"), secretKeyField, 1, false)
                .setVerticalGap(10) // 行间距 10px
                .addLabeledComponent(new JLabel("IV Offset"), ivField, 2, false)
                .setVerticalGap(10)
                .addLabeledComponent(new JLabel("Crypto Type"), cryptoTypeBox, 3, false)
                .setVerticalGap(10)
                .addComponentFillVertically(new JPanel(), 0) // 填充剩余空间
                .getPanel();
    }

    private JPanel createDocSettingPanel() {
        saveDirectory = new TextFieldWithBrowseButton();
        saveDirectory.setText(docConfig.getSaveDir());
        saveDirectory.addActionListener(e -> chooseFolder());

        docTypeBox = new ComboBox<>();
        for (DocType doc : DocType.values()) {
            docTypeBox.addItem(doc.getType());
        }
        docTypeBox.setSelectedItem(docConfig.getDocType());

        overwriteBox = new JBCheckBox();
        overwriteBox.setSelected(docConfig.isOverwriteDoc());

        return FormBuilder.createFormBuilder()
                .addLabeledComponent(new JLabel("Save Directory"), saveDirectory, 1, false)
                .setVerticalGap(10) // 行间距 10px
                .addLabeledComponent(new JLabel("Doc Type"), docTypeBox, 2, false)
                .setVerticalGap(10)
                .addLabeledComponent(new JLabel("Overwrite exists docs"), overwriteBox, 3, false)
                .setVerticalGap(20)
                .addComponent(createApiDocumentSettingsPanel(),4)
                .setVerticalGap(10)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    private JPanel createApiDocumentSettingsPanel() {
        excludeFields = new JXTextField();
        excludeFields.setPrompt("Patterns should be separated with \";\"");
        excludeFields.setPromptForeground(JBColor.GRAY);
        excludeFields.setText(docConfig.getApiDocConfig().getExcludeFields());

        JPanel docPanel = FormBuilder.createFormBuilder()
                .setVerticalGap(10) // 设置行间距为 10px
                .addLabeledComponent(new JLabel("Exclude Fields (a;b): "), excludeFields, 1, false)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();

        Border border = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
        docPanel.setBorder(BorderFactory.createTitledBorder(
                border,
                " Api Document Settings ",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Dialog", Font.BOLD, 14), // 标题字体
                JBColor.black // 标题文本颜色
        ));

        return docPanel;
    }

    private void chooseFolder() {
        FileChooserDescriptor fileChooserDescriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
        Project project = ProjectUtil.getActiveProject();
        VirtualFile selectedFile = FileChooser.chooseFile(fileChooserDescriptor, project, null);
        if (selectedFile != null) {
            // 更新 UI 组件以显示选择的文件路径
            saveDirectory.setText(selectedFile.getPath());
        }
    }

    /**
     * 是否被修改
     */
    @Override
    public boolean isModified() {
        return isDocModified() || isSensitiveDataModified();
        // todo 数据库文档
    }

    private boolean isSensitiveDataModified() {
        return !sensitiveDataConfig.getSecretKey().equals(secretKeyField.getText()) ||
                !sensitiveDataConfig.getIv().equals(ivField.getText()) ||
                !sensitiveDataConfig.getCryptoAlgorithm().equals(cryptoTypeBox.getSelectedItem());
    }

    private boolean isDocModified() {
        return !docConfig.getApiDocConfig().getExcludeFields().equals(excludeFields.getText()) ||
                !docConfig.getSaveDir().equals(saveDirectory.getText()) ||
                docConfig.isOverwriteDoc() != overwriteBox.isSelected() ||
                !docConfig.getDocType().equals(docTypeBox.getSelectedItem())
                ;
    }

    /**
     * 应用修改
     */
    @Override
    public void apply() {
        docApply();
        sensitiveDataApply();

        // todo 数据库文档
    }

    private void sensitiveDataApply() {
        sensitiveDataConfig.setSecretKey(secretKeyField.getText());
        sensitiveDataConfig.setIv(ivField.getText());
        sensitiveDataConfig.setCryptoAlgorithm(String.valueOf(cryptoTypeBox.getSelectedItem()));
    }

    private void docApply() {
        docConfig.getApiDocConfig().setExcludeFields(excludeFields.getText());
        docConfig.setSaveDir(saveDirectory.getText());
        docConfig.setOverwriteDoc(overwriteBox.isSelected());
        docConfig.setDocType(String.valueOf(docTypeBox.getSelectedItem()));
    }

}
