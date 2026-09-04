package com.zhanglinwei.zTools.yapi;


import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.zhanglinwei.zTools.annotation.model.ClassDefinition;
import com.zhanglinwei.zTools.annotation.model.MethodDefinition;
import com.zhanglinwei.zTools.annotation.parse.SourceParser;
import com.zhanglinwei.zTools.common.util.CollectionUtils;
import com.zhanglinwei.zTools.common.util.NotificationUtil;
import com.zhanglinwei.zTools.common.util.ProjectConfigs;
import com.zhanglinwei.zTools.common.util.StringUtils;
import com.zhanglinwei.zTools.configure.config.YApiConfig;
import com.zhanglinwei.zTools.yapi.utils.YApiFields;
import com.zhanglinwei.zTools.yapi.client.YApiClient;
import com.zhanglinwei.zTools.yapi.model.YApiInterfaceAddRequest;
import com.zhanglinwei.zTools.yapi.model.YApiInterfaceCat;
import com.zhanglinwei.zTools.yapi.model.YApiInterfaceCatAddRequest;
import com.zhanglinwei.zTools.yapi.ui.YApiConfigDialog;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 右键上传接口至 YApi。
 * <p>
 * 支持在方法或类上触发：方法只上传当前接口，类上传其中全部 Spring Mapping / Feign {@code @RequestLine} 方法。
 * 组包逻辑在 {@link YApiInterfaceBuilder}，本类只负责可用性、配置检查与后台上传。
 */
public class UploadToYApiAction extends AnAction {

    /**
     * 仅当光标位于方法或类上时显示。
     *
     * @param e 当前 Action 事件
     */
    @Override
    public void update(AnActionEvent e) {
        Project project = e.getProject();
        PsiElement psiElement = e.getData(CommonDataKeys.PSI_ELEMENT);
        boolean enabled = project != null && (psiElement instanceof PsiMethod || psiElement instanceof PsiClass);
        e.getPresentation().setEnabledAndVisible(enabled);
    }

    /**
     * 检查配置、解析选中方法，并在后台上传。
     *
     * @param e 当前 Action 事件
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }

        YApiConfig settings = YApiConfig.getInstance(project);
        if (!settings.isConfigured()) {
            YApiConfigDialog dialog = new YApiConfigDialog(project);
            dialog.show();
            if (!dialog.isOK()) {
                return;
            }
            settings = YApiConfig.getInstance(project);
            if (!settings.isConfigured()) {
                NotificationUtil.warnNotify("YApi 未配置完整（需要 Server URL、Token 和项目 ID）", project);
                return;
            }
        }

        Number projectId = parseProjectId(settings.getProjectId());
        if (projectId == null) {
            NotificationUtil.warnNotify("YApi 项目 ID 无效，请在设置中重新解析", project);
            return;
        }

        PsiElement psiElement = e.getData(CommonDataKeys.PSI_ELEMENT);
        PsiClass targetClass = null;
        List<MethodDefinition> targetMethods = new ArrayList<MethodDefinition>();

        if (psiElement instanceof PsiMethod) {
            PsiMethod selectedMethod = (PsiMethod) psiElement;
            targetClass = selectedMethod.getContainingClass();
            MethodDefinition methodDefinition = SourceParser.parseMethod(selectedMethod);
            if (!YApiInterfaceBuilder.isUploadable(methodDefinition)) {
                NotificationUtil.warnNotify("Only web or Feign methods are supported!", project);
                return;
            }
            targetMethods.add(methodDefinition);
        } else if (psiElement instanceof PsiClass) {
            targetClass = (PsiClass) psiElement;
            List<MethodDefinition> parsed = SourceParser.parseMethod(targetClass);
            for (MethodDefinition methodDefinition : parsed) {
                if (YApiInterfaceBuilder.isUploadable(methodDefinition)) {
                    targetMethods.add(methodDefinition);
                }
            }
            if (targetMethods.isEmpty()) {
                NotificationUtil.warnNotify("The Web method was not found in the class!", project);
                return;
            }
        }

        if (targetClass == null || CollectionUtils.isEmpty(targetMethods)) {
            NotificationUtil.warnNotify("No Web method was found!", project);
            return;
        }

        final String serverUrl = settings.getServerUrl();
        final String token = settings.getToken();
        final Number resolvedProjectId = projectId;
        final ClassDefinition classDef = SourceParser.parseClass(targetClass, false);
        final String categoryName = YApiFields.categoryOf(classDef);
        final List<MethodDefinition> methods = new ArrayList<MethodDefinition>(targetMethods);
        String globalRequestPrefix = ProjectConfigs.globalRequestPrefix(project);

        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Upload To YApi...", true) {
            /**
             * 后台创建分类并逐个上传接口。
             *
             * @param indicator 进度指示器
             */
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setIndeterminate(true);
                try {
                    indicator.setText("Load category list...");
                    List<YApiInterfaceCat> existingCats = YApiClient.getCatMenu(serverUrl, resolvedProjectId, token);

                    Number catId = findCatId(existingCats, categoryName);
                    if (catId == null) {
                        indicator.setText("Create category: " + categoryName);
                        YApiInterfaceCatAddRequest catRequest = new YApiInterfaceCatAddRequest(categoryName, resolvedProjectId);
                        YApiInterfaceCat newCat = YApiClient.addCat(serverUrl, catRequest, token);
                        catId = newCat.get_id();
                    }

                    indicator.setIndeterminate(false);
                    int success = 0;
                    for (int i = 0; i < methods.size(); i++) {
                        indicator.checkCanceled();
                        MethodDefinition methodDef = methods.get(i);
                        indicator.setText("Upload interface: " + methodDef.name());
                        indicator.setFraction((double) (i + 1) / methods.size());
                        try {
                            YApiInterfaceAddRequest request = YApiInterfaceBuilder.build(
                                    globalRequestPrefix, classDef, methodDef, resolvedProjectId, catId);
                            YApiClient.saveInterface(serverUrl, request, token);
                            success++;
                        } catch (Exception uploadError) {
                            notifyError(project, "Fail to upload: " + methodDef.name() + ", " + uploadError.getMessage());
                        }
                    }
                    if (success == methods.size()) {
                        notifyInfo(project, "Uploaded " + success + " interfaces to YApi");
                    }
                } catch (ProcessCanceledException cancel) {
                    throw cancel;
                } catch (Exception ex) {
                    notifyError(project, "Fail to upload: " + ex.getMessage());
                }
            }
        });
    }

    /**
     * 按分类名查找已有分类 ID。
     *
     * @param cats         已有分类
     * @param categoryName 分类名
     * @return 分类 ID；没有则为 {@code null}
     */
    private static Number findCatId(List<YApiInterfaceCat> cats, String categoryName) {
        for (YApiInterfaceCat cat : cats) {
            if (cat != null && categoryName.equals(cat.getName())) {
                return cat.get_id();
            }
        }
        return null;
    }

    /**
     * 解析项目 ID；空串或非数字返回 {@code null}，避免上传 {@code project_id=0}。
     *
     * @param value 配置中的项目 ID
     * @return 数字 ID；无法解析则为 {@code null}
     */
    private static Number parseProjectId(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        String trimmed = value.trim();
        try {
            return Integer.valueOf(trimmed);
        } catch (NumberFormatException ignored) {
            try {
                return Long.valueOf(trimmed);
            } catch (NumberFormatException ignoredAgain) {
                return null;
            }
        }
    }

    /**
     * 在 EDT 弹出错误通知。
     *
     * @param project 当前工程
     * @param message 文案
     */
    private static void notifyError(final Project project, final String message) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                NotificationUtil.errorNotify(message, project);
            }
        });
    }

    /**
     * 在 EDT 弹出信息通知。
     *
     * @param project 当前工程
     * @param message 文案
     */
    private static void notifyInfo(final Project project, final String message) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                NotificationUtil.infoNotify(message, project);
            }
        });
    }
}
