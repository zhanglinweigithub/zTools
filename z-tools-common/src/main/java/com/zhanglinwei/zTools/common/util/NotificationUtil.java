package com.zhanglinwei.zTools.common.util;

import com.intellij.notification.*;
import com.intellij.openapi.project.Project;

/**
 * IntelliJ 气泡通知。走插件注册的 {@code NotificationGroup}，用于成功/警告/失败提示。
 */
public final class NotificationUtil {

    /** 工具类，禁止实例化 */
    private NotificationUtil(){}

    /**
     * 警告通知。
     *
     * @param message 文案
     * @param project 当前项目
     */
    public static void warnNotify(String message, Project project) {
        notify(message, NotificationType.WARNING, project);
    }

    /**
     * 信息通知。
     *
     * @param message 文案
     * @param project 当前项目
     */
    public static void infoNotify(String message, Project project) {
        notify(message, NotificationType.INFORMATION, project);
    }

    /**
     * 错误通知。
     *
     * @param message 文案
     * @param project 当前项目
     */
    public static void errorNotify(String message, Project project) {
        notify(message, NotificationType.ERROR, project);
    }

    /**
     * 通过插件 NotificationGroup 弹出指定级别的通知。
     *
     * @param content 文案
     * @param type    通知级别
     * @param project 当前项目
     */
    private static void notify(String content, NotificationType type, Project project) {
        NotificationGroupManager.getInstance()
                .getNotificationGroup("com.zhanglinwei.zTools.NotificationGroup")
                .createNotification(content, type)
                .notify(project);
    }

}
