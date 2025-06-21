package com.zhanglinwei.zTools.util;

import com.intellij.notification.*;
import com.intellij.openapi.project.Project;

/**
 * 通知工具类
 */
public final class NotificationUtil {

    private NotificationUtil(){}

    private static final NotificationGroup notificationGroup =  NotificationGroupManager.getInstance().getNotificationGroup("com.zhanglinwei.zTools.NotificationGroup");

    public static void warnNotify(String message, Project project) {
        notify(message, NotificationType.WARNING, project);
    }

    public static void infoNotify(String message, Project project) {
        notify(message, NotificationType.INFORMATION, project);
    }

    public static void errorNotify(String message, Project project) {
        notify(message, NotificationType.ERROR, project);
    }

    private static void notify(String content, NotificationType type, Project project) {
        NotificationGroupManager.getInstance()
                .getNotificationGroup("com.zhanglinwei.zTools.NotificationGroup")
                .createNotification(content, type)
                .notify(project);
    }

}
