package com.zhanglinwei.zTools.yapi;


import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.zhanglinwei.zTools.yapi.client.YApiClient;
import com.zhanglinwei.zTools.configure.config.YApiConfig;
import com.zhanglinwei.zTools.yapi.model.*;
import com.zhanglinwei.zTools.yapi.ui.YApiConfigDialog;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * 右键上传接口至 YApi 的 Action
 */
public class UploadToYApiAction extends AnAction {

    /** Spring Web 请求映射注解的全限定名 */
    private static final Set<String> MAPPING_ANNOTATIONS = new HashSet<>();

    static {
        MAPPING_ANNOTATIONS.add("org.springframework.web.bind.annotation.GetMapping");
        MAPPING_ANNOTATIONS.add("org.springframework.web.bind.annotation.PostMapping");
        MAPPING_ANNOTATIONS.add("org.springframework.web.bind.annotation.PutMapping");
        MAPPING_ANNOTATIONS.add("org.springframework.web.bind.annotation.DeleteMapping");
        MAPPING_ANNOTATIONS.add("org.springframework.web.bind.annotation.RequestMapping");
        MAPPING_ANNOTATIONS.add("org.springframework.web.bind.annotation.PatchMapping");
    }

    @Override
    public void update(AnActionEvent e) {
        Project project = e.getProject();
        PsiElement psiElement = e.getData(CommonDataKeys.PSI_ELEMENT);
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);

        boolean enabled = false;
        if (project != null && (psiElement != null || psiFile != null)) {
            if (psiElement instanceof PsiMethod) {
                enabled = true;
            } else if (psiElement instanceof PsiClass) {
                enabled = true;
            } else if (psiFile != null) {
                enabled = psiFile instanceof PsiJavaFile;
            }
        }
        e.getPresentation().setEnabledAndVisible(enabled);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) return;

        // 1. 检查 YApi 是否已配置
        YApiConfig settings = YApiConfig.getInstance(project);
        if (!settings.isConfigured()) {
            YApiConfigDialog dialog = new YApiConfigDialog(project);
            dialog.show();
            if (!dialog.isOK()) {
                return;
            }
            settings = YApiConfig.getInstance(project);
            if (!settings.isConfigured()) {
                return;
            }
        }

        // 2. 确定选中的元素
        PsiElement psiElement = e.getData(CommonDataKeys.PSI_ELEMENT);
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);

        PsiClass targetClass = null;
        List<PsiMethod> targetMethods = new ArrayList<>();

        if (psiElement instanceof PsiMethod) {
            PsiMethod method = (PsiMethod) psiElement;
            targetMethods.add(method);
            targetClass = method.getContainingClass();
        } else if (psiElement instanceof PsiClass) {
            targetClass = (PsiClass) psiElement;
            targetMethods = collectWebMethods(targetClass);
        } else if (psiFile instanceof PsiJavaFile) {
            PsiJavaFile javaFile = (PsiJavaFile) psiFile;
            PsiClass[] classes = javaFile.getClasses();
            if (classes.length > 0) {
                targetClass = classes[0];
                targetMethods = collectWebMethods(targetClass);
            }
        }

        if (targetMethods.isEmpty()) {
            showNotify(project, NotificationType.WARNING, "未找到可上传的 Web 方法");
            return;
        }

        // 3. 保存配置快照（避免后台任务期间设置变化）
        final String serverUrl = settings.getServerUrl();
        final String token = settings.getToken();
        final Number projectId = parseNumber(settings.getProjectId());
        final String className = targetClass != null ? targetClass.getName() : "Default";
        final List<PsiMethod> methods = new ArrayList<>(targetMethods);

        // 4. 后台执行上传
        ProgressManager.getInstance().run(new Task.Backgroundable(project, "上传至 YApi...", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setIndeterminate(true);
                try {
                    // 获取已有分类
                    indicator.setText("获取分类列表...");
                    List<YApiInterfaceCat> existingCats = YApiClient.getCatMenu(serverUrl, projectId, token);

                    // 查找或创建分类
                    Number catId = null;
                    for (YApiInterfaceCat cat : existingCats) {
                        if (className.equals(cat.getName())) {
                            catId = cat.get_id();
                            break;
                        }
                    }

                    if (catId == null) {
                        indicator.setText("创建分类: " + className);
                        YApiInterfaceCatAddRequest catRequest = new YApiInterfaceCatAddRequest(className, projectId);
                        YApiInterfaceCat newCat = YApiClient.addCat(serverUrl, catRequest, token);
                        catId = newCat.get_id();
                    }

                    // 上传接口（save 接口：同名同分类自动覆盖，否则新增）
                    int successCount = 0;
                    int failCount = 0;
                    for (int i = 0; i < methods.size(); i++) {
                        PsiMethod method = methods.get(i);
                        indicator.setText("上传接口: " + method.getName() + " (" + (i + 1) + "/" + methods.size() + ")");
                        indicator.setFraction((double) (i + 1) / methods.size());

                        try {
                            YApiInterfaceAddRequest request = buildMockInterface(method, projectId, catId, className);
                            YApiClient.saveInterface(serverUrl, request, token);
                            successCount++;
                        } catch (Exception ex) {
                            failCount++;
                        }
                    }

                    // 通知结果
                    String msg;
                    if (failCount == 0) {
                        msg = "成功上传 " + successCount + " 个接口至分类 \"" + className + "\"";
                        showNotify(project, NotificationType.INFORMATION, msg);
                    } else {
                        msg = "上传完成: 成功 " + successCount + " 个, 失败 " + failCount + " 个";
                        showNotify(project, NotificationType.WARNING, msg);
                    }
                } catch (Exception ex) {
                    showNotify(project, NotificationType.ERROR, "上传失败: " + ex.getMessage());
                }
            }
        });
    }

    /**
     * 收集类中所有带有 Web 映射注解的方法
     */
    private List<PsiMethod> collectWebMethods(PsiClass psiClass) {
        List<PsiMethod> webMethods = new ArrayList<>();
        for (PsiMethod method : psiClass.getMethods()) {
            if (hasMappingAnnotation(method)) {
                webMethods.add(method);
            }
        }
        return webMethods;
    }

    /**
     * 检查方法是否有 Web 映射注解
     */
    private boolean hasMappingAnnotation(PsiMethod method) {
        for (PsiAnnotation annotation : method.getModifierList().getAnnotations()) {
            String qualifiedName = annotation.getQualifiedName();
            if (qualifiedName != null && MAPPING_ANNOTATIONS.contains(qualifiedName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 构建 Mock 接口数据（包含完整的请求头、Query、Path、Body 信息）
     * <p>
     * 注意：YApi 中 req_body_type="json" 和 req_body_form 互斥，
     * 设置 json body 时不能同时设置 form 参数，否则 form 不展示。
     */
    private YApiInterfaceAddRequest buildMockInterface(PsiMethod method, Number projectId, Number catId, String className) {
        YApiInterfaceAddRequest request = new YApiInterfaceAddRequest();
        request.setProject_id(projectId);
        request.setCatid(catId);
        request.setTitle(method.getName());
        request.setPath("/" + camelToHyphen(className) + "/" + camelToHyphen(method.getName()));
        request.setMethod(resolveHttpMethod(method));
        request.setMarkdown("<pre>\n" +
                "/**\n" +
                " * xxxx\n" +
                " * @param form xxx\n" +
                " * @return wwww\n" +
                " */\n" +
                "@PostMapping(\"/xxxx\")\n" +
                "public PageResult<xxx> queryPage(@RequestBody xxxx form) {\n" +
                "   // something.....\n" +
                "   return xxxx;\n" +
                "}\n" +
                "</pre>");
        request.setStatus("undone");

        // ---- 请求头 ----
        YApiHeader contentTypeHeader = new YApiHeader();
        contentTypeHeader.setName("Content-Type");
        contentTypeHeader.setValue("application/json");
        contentTypeHeader.setRequired("1");
        contentTypeHeader.setDesc("请求体内容类型");
        contentTypeHeader.setExample("application/json");

        YApiHeader authHeader = new YApiHeader();
        authHeader.setName("Authorization");
        authHeader.setValue("Bearer {{token}}");
        authHeader.setRequired("1");
        authHeader.setDesc("用户认证Token");
        authHeader.setExample("Bearer eyJhbGciOiJIUzI1NiJ9.xxx");

        YApiHeader traceHeader = new YApiHeader();
        traceHeader.setName("X-Request-Id");
        traceHeader.setValue("");
        traceHeader.setRequired("0");
        traceHeader.setDesc("请求链路追踪ID");
        traceHeader.setExample("a1b2c3d4-e5f6-7890");

        request.setReq_headers(Arrays.asList(contentTypeHeader, authHeader, traceHeader));

        // ---- Query 参数 ----
        YApiQueryParam pageQuery = new YApiQueryParam();
        pageQuery.setName("page");
        pageQuery.setValue("1");
        pageQuery.setRequired("0");
        pageQuery.setDesc("页码");
        pageQuery.setExample("1");

        YApiQueryParam sizeQuery = new YApiQueryParam();
        sizeQuery.setName("pageSize");
        sizeQuery.setValue("20");
        sizeQuery.setRequired("0");
        sizeQuery.setDesc("每页条数");
        sizeQuery.setExample("20");

        YApiQueryParam keywordQuery = new YApiQueryParam();
        keywordQuery.setName("keyword");
        keywordQuery.setValue("");
        keywordQuery.setRequired("0");
        keywordQuery.setDesc("搜索关键词");
        keywordQuery.setExample("张三");

        request.setReq_query(Arrays.asList(pageQuery, sizeQuery, keywordQuery));

        // ---- Path 参数 ----
        YApiPathParam idPath = new YApiPathParam();
        idPath.setName("id");
        idPath.setDesc("资源唯一标识");
        idPath.setExample("100001");

        request.setReq_params(Collections.singletonList(idPath));

        // ---- 请求体（JSON 格式，is_json_schema=false 时 YApi 以原始 JSON 展示） ----
        // 注意：req_body_type="json" 与 req_body_form 互斥，不能同时设置
//        request.setReq_body_type("json");
//        request.setReq_body_is_json_schema(false);
//        request.setReq_body_other("{\n" +
//                "  \"name\": \"张三\",           // 用户名\n" +
//                "  \"age\": 28,                 // 年龄\n" +
//                "  \"email\": \"zhangsan@example.com\",  // 邮箱\n" +
//                "  \"phone\": \"13800138000\",   // 手机号\n" +
//                "  \"enabled\": true,            // 是否启用\n" +
//                "  \"department\": {             // 部门信息\n" +
//                "    \"id\": 1,\n" +
//                "    \"name\": \"研发部\"\n" +
//                "  },\n" +
//                "  /* 角色列表 */\n" +
//                "  \"roles\": [\n" +
//                "    \"admin\",\n" +
//                "    \"developer\"\n" +
//                "  ]\n" +
//                "}");

        // ---- Form 表单参数（与 req_body_type="json" 互斥，同时设置时 form 不展示） ----
        request.setReq_body_type("form");

        YApiFormParam fileField = new YApiFormParam();
        fileField.setName("file");
        fileField.setType("file");
        fileField.setRequired("1");
        fileField.setDesc("上传的文件");
        fileField.setExample("");

        YApiFormParam typeField = new YApiFormParam();
        typeField.setName("fileType");
        typeField.setType("text");
        typeField.setRequired("0");
        typeField.setDesc("文件类型");
        typeField.setExample("image");

        YApiFormParam nameField = new YApiFormParam();
        nameField.setName("name");
        nameField.setType("text");
        nameField.setRequired("1");
        nameField.setDesc("名称");
        nameField.setExample("测试文件");

        request.setReq_body_form(Arrays.asList(fileField, typeField, nameField));

        // ---- 响应体（JSON 格式，is_json_schema=false 时 YApi 以原始 JSON 展示） ----
        request.setRes_body_type("json");
        request.setRes_body_is_json_schema(false);
        request.setRes_body("{\n" +
                "  \"errcode\": 0,              // 返回码，0表示成功\n" +
                "  \"errmsg\": \"success\",       // 返回消息\n" +
                "  \"server_timestamp\": 1709105208,  // 服务器时间戳\n" +
                "  \"data\": {                   // 返回数据\n" +
                "    \"id\": 100001,            // 记录ID\n" +
                "    \"name\": \"张三\",          // 用户名\n" +
                "    \"age\": 28,               // 年龄\n" +
                "    \"email\": \"zhangsan@example.com\",  // 邮箱\n" +
                "    \"phone\": \"13800138000\", // 手机号\n" +
                "    \"enabled\": true,         // 是否启用\n" +
                "    \"department\": {          // 部门信息\n" +
                "      \"id\": 1,\n" +
                "      \"name\": \"研发部\"\n" +
                "    },\n" +
                "    /* 角色列表 */\n" +
                "    \"roles\": [\n" +
                "      \"admin\",\n" +
                "      \"developer\"\n" +
                "    ],\n" +
                "    \"createTime\": \"2024-06-01 10:30:00\",  // 创建时间\n" +
                "    \"updateTime\": \"2024-06-15 14:20:00\"   // 更新时间\n" +
                "  }\n" +
                "}");

        return request;
    }

    /**
     * 根据方法上的注解推断 HTTP 方法
     */
    private String resolveHttpMethod(PsiMethod method) {
        for (PsiAnnotation annotation : method.getModifierList().getAnnotations()) {
            String qualifiedName = annotation.getQualifiedName();
            if (qualifiedName == null) continue;

            if (qualifiedName.endsWith("GetMapping")) return "GET";
            if (qualifiedName.endsWith("PostMapping")) return "POST";
            if (qualifiedName.endsWith("PutMapping")) return "PUT";
            if (qualifiedName.endsWith("DeleteMapping")) return "DELETE";
            if (qualifiedName.endsWith("PatchMapping")) return "PATCH";
            if (qualifiedName.endsWith("RequestMapping")) {
                PsiAnnotationMemberValue methodAttr = annotation.findAttributeValue("method");
                if (methodAttr != null) {
                    String methodStr = methodAttr.getText();
                    if (methodAttr instanceof PsiReference) {
                        methodStr = ((PsiReference) methodAttr).resolve() != null
                                ? ((PsiReference) methodAttr).resolve().getText() : methodStr;
                    }
                    if (methodStr.contains("POST")) return "POST";
                    if (methodStr.contains("PUT")) return "PUT";
                    if (methodStr.contains("DELETE")) return "DELETE";
                    if (methodStr.contains("PATCH")) return "PATCH";
                }
                return "GET";
            }
        }
        return "GET";
    }

    /**
     * camelCase 转 hyphen-case
     */
    private String camelToHyphen(String name) {
        if (name == null || name.isEmpty()) return name;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0) sb.append('-');
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 解析数字（兼容 String 和 Number）
     */
    private Number parseNumber(String value) {
        if (value == null || value.isEmpty()) return 0;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException e2) {
                return 0;
            }
        }
    }

    /**
     * 发送通知
     */
    private void showNotify(Project project, NotificationType type, String message) {
        ApplicationManager.getApplication().invokeLater(() -> {
            Notification notification = NotificationGroupManager.getInstance()
                    .getNotificationGroup("com.wwww.yapi.utils.NotificationGroup")
                    .createNotification("YApi Tools", message, type);
            Notifications.Bus.notify(notification, project);
        });
    }
}
