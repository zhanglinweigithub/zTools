package com.zhanglinwei.zTools.annotation;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import com.zhanglinwei.zTools.annotation.parse.SourceParser;
import com.zhanglinwei.zTools.util.JsonUtil;
import com.zhanglinwei.zTools.util.StringUtils;

import java.awt.datatransfer.StringSelection;

/**
 * 测试用：把注解模块解析出的类 / 方法定义拷成 JSON。
 * 光标在方法上只解析该方法；否则解析当前类。
 */
public class CopyAnnotationJsonAction extends AnAction {

    private static final String NOTIFY_GROUP = "com.zhanglinwei.zTools.NotificationGroup";

    @Override
    public void actionPerformed(AnActionEvent actionEvent) {
        Editor editor = actionEvent.getDataContext().getData(CommonDataKeys.EDITOR);
        if (editor == null) {
            return;
        }
        PsiFile psiFile = actionEvent.getData(CommonDataKeys.PSI_FILE);
        if (psiFile == null) {
            return;
        }
        Project project = editor.getProject();
        if (project == null) {
            return;
        }

        try {
            PsiElement referenceAt = psiFile.findElementAt(editor.getCaretModel().getOffset());
            Object definition = resolve(referenceAt);
            if (definition == null) {
                notify(project, "Place the caret on a class or method.", NotificationType.ERROR);
                return;
            }
            String json = JsonUtil.toJsonString(definition, true);
            if (StringUtils.isBlank(json)) {
                notify(project, "Copy Annotation Json failed: empty definition.", NotificationType.ERROR);
                return;
            }
            CopyPasteManager.getInstance().setContents(new StringSelection(json));
            notify(project, "Copy Annotation Json successfully!", NotificationType.INFORMATION);
        } catch (Exception ex) {
            notify(project, "Copy Annotation Json failed, Caused by: " + ex.getMessage(), NotificationType.ERROR);
        }
    }

    private static Object resolve(PsiElement referenceAt) {
        PsiMethod method = PsiTreeUtil.getContextOfType(referenceAt, PsiMethod.class);
        if (method != null) {
            return SourceParser.parseMethod(method);
        }
        PsiClass psiClass = PsiTreeUtil.getContextOfType(referenceAt, PsiClass.class);
        if (psiClass != null) {
            return SourceParser.parseClass(psiClass, false);
        }
        return null;
    }

    private static void notify(Project project, String message, NotificationType type) {
        Notifications.Bus.notify(new Notification(NOTIFY_GROUP, "zTools", message, type), project);
    }
}
