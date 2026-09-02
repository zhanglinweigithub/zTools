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
import com.zhanglinwei.zTools.common.util.JsonUtil;
import com.zhanglinwei.zTools.common.util.StringUtils;

import java.awt.datatransfer.StringSelection;

/**
 * 测试用：把注解模块解析出的类 / 方法定义拷成 JSON。
 * 光标在方法上只解析该方法；否则解析当前类。
 */
public class CopyAnnotationJsonAction extends AnAction {

    /** 插件通知组 id。 */
    private static final String NOTIFY_GROUP = "com.zhanglinwei.zTools.NotificationGroup";

    /**
     * 解析光标处的类或方法，把定义对象序列化为 JSON 并写入剪贴板。
     *
     * @param actionEvent 动作事件
     */
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

    /**
     * 优先解析光标所在方法，否则解析所在类（不展开方法列表）。
     *
     * @param referenceAt 光标处 PSI 元素
     * @return 方法定义或类定义；都不在范围内则为 {@code null}
     */
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

    /**
     * 弹出通知。
     *
     * @param project 当前项目
     * @param message 通知内容
     * @param type    通知类型
     */
    private static void notify(Project project, String message, NotificationType type) {
        Notifications.Bus.notify(new Notification(NOTIFY_GROUP, "zTools", message, type), project);
    }
}
