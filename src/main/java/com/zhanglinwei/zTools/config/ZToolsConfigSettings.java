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
import com.intellij.util.ui.JBInsets;
import com.zhanglinwei.zTools.constant.DocType;
import org.jdesktop.swingx.JXTextField;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ZToolsConfigSettings implements Configurable {

    private ZToolsConfig oldState;

    private JPanel globalPanel;

    private JXTextField excludeFields;
    private TextFieldWithBrowseButton saveDirectory;
    private JBCheckBox overwriteBox;
    private ComboBox<String> docTypeBox;

    public ZToolsConfigSettings(Project project) {
        oldState = project.getService(ZToolsConfig.class);
    }

    @Override
    public String getDisplayName() {
        return "Easy Doc";
    }

    @Override
    public JComponent createComponent() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        globalPanel = new JPanel(gridBagLayout);
        GridBagConstraints globalConstraints = new GridBagConstraints();
        // 设置全局约束的默认填充和权重
        globalConstraints.fill = GridBagConstraints.HORIZONTAL; // 水平填充
        globalConstraints.weightx = 1.0; // 列权重, 占满整列
        globalConstraints.weighty = 0;
        globalConstraints.anchor = GridBagConstraints.NORTH; // 位置
        globalConstraints.insets = new JBInsets(10, 0, 10, 0); // 间距

//        globalConstraints.gridwidth = GridBagConstraints.REMAINDER;
        globalConstraints.gridheight = 1;
        globalConstraints.gridy = 0; // 行
        JPanel commonPanel = createCommonSettingPanel();
        globalPanel.add(commonPanel, globalConstraints);

        globalConstraints.anchor = GridBagConstraints.NORTH;
        globalConstraints.gridy = 1; // 行
        globalConstraints.weighty = 1.0;
//        globalConstraints.gridheight = GridBagConstraints.REMAINDER;
        JPanel docPanel = createApiDocumentSettingsPanel(); // Api Doc Setting
        globalPanel.add(docPanel, globalConstraints);

        // todo 数据库文档

        return globalPanel;
    }

    private JPanel createCommonSettingPanel() {
        JPanel commonPanel = new JPanel(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new JBInsets(5, 3, 5, 3);
        constraints.anchor = GridBagConstraints.WEST;

        JLabel saveDirectoryLabel = new JLabel("Save Directory");
        constraints.gridy = 0;
        constraints.gridx = 0;
        commonPanel.add(saveDirectoryLabel, constraints);


        saveDirectory = new TextFieldWithBrowseButton();
        saveDirectory.setText(oldState.getSaveDir());
        saveDirectory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooseFolder();
            }
        });
        constraints.gridx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1.0;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        commonPanel.add(saveDirectory, constraints);

        constraints.fill = GridBagConstraints.NONE;
        constraints.gridy = 1;
        JLabel docTypeLabel = new JLabel("Doc Type");
        constraints.gridx = 0;
        constraints.weightx = 0;
        constraints.gridwidth = 1;
        commonPanel.add(docTypeLabel, constraints);

        docTypeBox = new ComboBox<>();
        for (DocType doc : DocType.values()) {
            docTypeBox.addItem(doc.getType());
        }
        docTypeBox.setSelectedItem(oldState.getDocType());

        constraints.gridx = 1;
        constraints.weightx = 0;
        constraints.gridwidth = 1;
        commonPanel.add(docTypeBox, constraints);


        constraints.gridx = 2;
        constraints.gridwidth = 1;
        constraints.weightx = 0;
        JLabel overwriteLabel = new JLabel("Overwrite exists docs");
        commonPanel.add(overwriteLabel, constraints);

        constraints.gridx = 3;
        constraints.weightx = 0;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        overwriteBox = new JBCheckBox("Yes", oldState.isOverwriteDoc());
        commonPanel.add(overwriteBox, constraints);

        return commonPanel;
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

    private JPanel createApiDocumentSettingsPanel() {
        Border border = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
        JPanel docPanel = new JPanel(new GridBagLayout());
        docPanel.setBorder(BorderFactory.createTitledBorder(
                border,
                " Api Document Settings ",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Dialog", Font.BOLD, 12), // 标题字体
                JBColor.black // 标题文本颜色
        ));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new JBInsets(5, 3, 5, 3);
        constraints.anchor = GridBagConstraints.WEST; // 设置标签靠左对齐

        JLabel excludeFieldsLabel = new JLabel("Exclude Fields (a;b)");
        constraints.gridx = 0;
        constraints.gridy = 0;
        docPanel.add(excludeFieldsLabel, constraints);

        excludeFields = new JXTextField();
        excludeFields.setPrompt("Patterns should be separated with \";\"");
        excludeFields.setPromptForeground(JBColor.GRAY);
        excludeFields.setText(oldState.getExcludeFields());
        constraints.gridx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1.0;
//        constraints.gridwidth = GridBagConstraints.REMAINDER;
        docPanel.add(excludeFields, constraints);

        return docPanel;
    }


    /**
     * 是否被修改
     */
    @Override
    public boolean isModified() {
        return !oldState.getExcludeFields().equals(excludeFields.getText()) ||
                !oldState.getSaveDir().equals(saveDirectory.getText()) ||
                oldState.isOverwriteDoc() != overwriteBox.isSelected() ||
                !oldState.getDocType().equals(docTypeBox.getSelectedItem())
                ;
        // todo 数据库文档
    }

    /**
     * 应用修改
     */
    @Override
    public void apply() {
        oldState.setExcludeFields(excludeFields.getText());
        oldState.setSaveDir(saveDirectory.getText());
        oldState.setOverwriteDoc(overwriteBox.isSelected());
        oldState.setDocType(String.valueOf(docTypeBox.getSelectedItem()));
        // todo 数据库文档
    }

}
