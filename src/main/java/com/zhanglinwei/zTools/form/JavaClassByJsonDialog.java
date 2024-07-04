package com.zhanglinwei.zTools.form;

import com.zhanglinwei.zTools.util.AssertUtils;
import com.zhanglinwei.zTools.util.JsonUtil;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;


public class JavaClassByJsonDialog extends JDialog {

    private Project currentProject = null;

    private static final String CLASS_NAME_REG = "^[a-zA-Z_][a-zA-Z0-9_]*$";

    public interface OnClickOkListener {
        public void onOk(String className, String jsonString, boolean innerClass);
    }

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField javaClassName;
    private JCheckBox useInnerClass;
    private JTextArea jsonString;
    private JButton formatButton;
    private OnClickOkListener okListener;

    public JavaClassByJsonDialog(Project project, OnClickOkListener okListener) {
        this.okListener = okListener;
        this.currentProject = project;

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        setMinimumSize(new Dimension(1090, 830));

        buttonOK.addActionListener(e -> onOK());
        buttonCancel.addActionListener(e -> onCancel());
        formatButton.addActionListener(e -> formatJson());
        // 当单击叉号时调用 onCancel()
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(
                e -> onCancel()
                , KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
        );

        // 默认禁用 OK 按钮
        buttonOK.setEnabled(false);
        javaClassName.getDocument().addDocumentListener(new InputTextListener());
        jsonString.getDocument().addDocumentListener(new InputTextListener());

    }

    public JavaClassByJsonDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // 当单击叉号时调用 onCancel()
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private class InputTextListener implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent e) {
            validate();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            validate();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            validate();
        }

        private void validate() {
            String rootClassNameText = javaClassName.getText();
            String jsonStringText = jsonString.getText();

            buttonOK.setEnabled(rootClassNameText.matches(CLASS_NAME_REG) && JsonUtil.validateJson(jsonStringText));

        }
    }

    private void onOK() {
        // add your code here
        okListener.onOk(
                javaClassName.getText(),
                jsonString.getText(),
                useInnerClass.isSelected()
        );
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    private void formatJson() {
        String formatJson = JsonUtil.formatJson(jsonString.getText(), currentProject);
        if (AssertUtils.isNotEmpty(formatJson)) {
            jsonString.setText(formatJson);
        }
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
