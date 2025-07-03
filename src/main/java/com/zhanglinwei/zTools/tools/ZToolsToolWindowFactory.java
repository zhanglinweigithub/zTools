package com.zhanglinwei.zTools.tools;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.AnimatedIcon;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.util.ui.components.BorderLayoutPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class ZToolsToolWindowFactory implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        BorderLayoutPanel loaderPanel = new BorderLayoutPanel();
        loaderPanel.addToCenter(new JLabel(AnimatedIcon.Default.INSTANCE));

        ContentFactory contentFactory = ApplicationManager.getApplication().getService(ContentFactory.class);
        Content loadingContent = contentFactory.createContent(loaderPanel, "", false);
        toolWindow.getContentManager().addContent(loadingContent);

        // 2. 后台线程初始化设置（避免阻塞UI）
        ApplicationManager.getApplication().executeOnPooledThread(() -> {

        });
    }

    @Override
    public void init(@NotNull ToolWindow toolWindow) {
        // 设置工具窗口在工具栏上的显示标题
        toolWindow.setStripeTitle("ZTools");
    }
}
