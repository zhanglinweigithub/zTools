package com.zhanglinwei.zTools.yapi;


import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.zhanglinwei.zTools.annotation.model.ClassDefinition;
import com.zhanglinwei.zTools.annotation.model.MethodDefinition;
import com.zhanglinwei.zTools.annotation.model.ParameterDefinition;
import com.zhanglinwei.zTools.annotation.parse.SourceParser;
import com.zhanglinwei.zTools.annotation.web.MappingAnnotation;
import com.zhanglinwei.zTools.common.util.RequestPathUtils;
import com.zhanglinwei.zTools.annotation.web.WebAnnotationParser;
import com.zhanglinwei.zTools.annotation.web.WebParameterAnnotation;
import com.zhanglinwei.zTools.common.constant.MediaType;
import com.zhanglinwei.zTools.common.constant.WebTypes;
import com.zhanglinwei.zTools.common.enums.Boolean;
import com.zhanglinwei.zTools.common.enums.HttpMethod;
import com.zhanglinwei.zTools.common.util.CollectionUtils;
import com.zhanglinwei.zTools.common.util.NotificationUtil;
import com.zhanglinwei.zTools.common.util.ProjectConfigs;
import com.zhanglinwei.zTools.configure.config.YApiConfig;
import com.zhanglinwei.zTools.yapi.client.YApiClient;
import com.zhanglinwei.zTools.yapi.enums.ParameterType;
import com.zhanglinwei.zTools.yapi.enums.ReqBodyType;
import com.zhanglinwei.zTools.yapi.model.*;
import com.zhanglinwei.zTools.yapi.ui.YApiConfigDialog;
import com.zhanglinwei.zTools.yapi.utils.YApiFields;
import com.zhanglinwei.zTools.yapi.utils.YApiJson;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

import static com.zhanglinwei.zTools.common.constant.StringPool.COMMA_SPACE;
import static com.zhanglinwei.zTools.common.constant.StringPool.EMPTY;

/**
 * 右键上传接口至 YApi 的 Action
 * <p>
 * 支持在以下 PSI 元素上触发：
 * <ul>
 *     <li>方法（PsiMethod）—— 仅上传当前方法对应的接口</li>
 *     <li>类（PsiClass）—— 上传该类中所有 Web 处理方法</li>
 * </ul>
 * <p>
 */
public class UploadToYApiAction extends AnAction {

    /**
     * 控制 Action 在何种 PSI 上下文中可用
     * <p>
     * 仅当光标位于方法、类上时启用，其他位置隐藏
     *
     * @param e 当前 Action 事件
     */
    @Override
    public void update(AnActionEvent e) {
        Project project = e.getProject();
        PsiElement psiElement = e.getData(CommonDataKeys.PSI_ELEMENT);

        boolean enabled = false;
        if (project != null && psiElement != null) {
            if (psiElement instanceof PsiMethod) {
                enabled = true;
            }
            if (psiElement instanceof PsiClass) {
                enabled = true;
            }
        }
        e.getPresentation().setEnabledAndVisible(enabled);
    }

    /**
     * Action 执行入口
     *
     * @param e 当前 Action 事件
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }

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

        PsiClass targetClass = null;
        List<MethodDefinition> targetMethods = new ArrayList<>();

        if (psiElement instanceof PsiMethod) {
            // 单个方法：仅上传该方法的接口
            PsiMethod selectedMethod = (PsiMethod) psiElement;
            targetClass = selectedMethod.getContainingClass();

            // 判断是否 Web 方法
            MethodDefinition methodDefinition = SourceParser.parseMethod(selectedMethod);
            if (!WebAnnotationParser.isHandlerMethod(methodDefinition)) {
                NotificationUtil.warnNotify("Only web methods are supported!", project);
                return;
            }

            targetMethods.add(methodDefinition);
        } else if (psiElement instanceof PsiClass) {
            // 整个类：上传该类中所有 web 方法
            targetClass = (PsiClass) psiElement;

            // 过滤出 Web 方法
            List<MethodDefinition> webMethodDefinitionList = SourceParser.parseMethod(targetClass).stream()
                    .filter(WebAnnotationParser::isHandlerMethod)
                    .collect(Collectors.toList());
            if (webMethodDefinitionList.isEmpty()) {
                NotificationUtil.warnNotify("The Web method was not found in the class!", project);
                return;
            }

            targetMethods.addAll(webMethodDefinitionList);
        }

        if (targetMethods.isEmpty()) {
            NotificationUtil.warnNotify("No Web method was found!", project);
            return;
        }

        // 3. 保存配置快照（避免后台任务期间设置变化）
        final String serverUrl = settings.getServerUrl();
        final String token = settings.getToken();
        final Number projectId = parseNumber(settings.getProjectId());
        final ClassDefinition classDef = SourceParser.parseClass(targetClass, false);
        final String className = classDef != null ? classDef.name() : "Default";
        final List<MethodDefinition> methods = new ArrayList<>(targetMethods);

        // 4. 后台执行上传
        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Upload To YApi...", true) {
            /**
             * 后台创建分类并逐个上传接口。
             *
             * @param indicator 进度指示器
             */
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                // 表示进度条设为不确定模式——进度条会来回滚动（类似加载动画），而不是从 0% 走到 100%
                indicator.setIndeterminate(true);

                try {
                    // 获取已有分类列表

                    // 设置的是进度条下方显示的文字提示
                    indicator.setText("Load category list...");
                    List<YApiInterfaceCat> existingCats = YApiClient.getCatMenu(serverUrl, projectId, token);

                    // 以类名作为分类名称，查找已有分类或创建新分类
                    Number catId = null;
                    for (YApiInterfaceCat cat : existingCats) {
                        if (className.equals(cat.getName())) {
                            catId = cat.get_id();
                            break;
                        }
                    }

                    // 分类不存在则新增
                    if (catId == null) {
                        indicator.setText("Create category: " + className);
                        YApiInterfaceCatAddRequest catRequest = new YApiInterfaceCatAddRequest(className, projectId);
                        YApiInterfaceCat newCat = YApiClient.addCat(serverUrl, catRequest, token);
                        catId = newCat.get_id();
                    }

                    // 逐个上传接口 同名同分类自动覆盖 否则新增
                    for (int i = 0; i < methods.size(); i++) {
                        MethodDefinition methodDef = methods.get(i);
                        indicator.setText("Upload interface: " + methodDef.name());
                        // 设置进度条具体进度
                        indicator.setFraction((double) (i + 1) / methods.size());

                        try {
                            YApiInterfaceAddRequest request = buildInterface(project, classDef, methodDef, projectId, catId);
                            YApiClient.saveInterface(serverUrl, request, token);
                        } catch (Exception ignore) {
                            NotificationUtil.errorNotify("Fail to upload: " + methodDef.name() + ", " + ignore.getMessage(), project);
                        }
                    }
                } catch (Exception ex) {
                    NotificationUtil.errorNotify("Fail to upload: " + ex.getMessage(), project);
                }
            }
        });
    }
    
    /**
     * 把方法定义组装成 YApi 保存接口请求。
     * <p>
     * 路径拼接：全局前缀 × 类 Mapping × 方法 Mapping，
     * 例 {@code /api} + {@code /user/{id}} → {@code /api/user/{id}}。
     *
     * @param project          当前工程
     * @param classDefinition  所在类定义
     * @param methodDefinition 处理方法定义
     * @param projectId        YApi 项目 ID
     * @param catId            分类 ID
     * @return 可提交的保存请求
     */
    private YApiInterfaceAddRequest buildInterface(Project project, ClassDefinition classDefinition, MethodDefinition methodDefinition,
                                                   Number projectId, Number catId) {
        YApiInterfaceAddRequest request = new YApiInterfaceAddRequest();
        request.setProject_id(projectId);
        request.setCatid(catId);

        // 标题与描述
        // 优先级：OpenAPI @Operation → Swagger @ApiOperation → 方法注释
        request.setTitle(YApiFields.titleOf(methodDefinition));
        request.setDesc(YApiFields.descriptionOf(methodDefinition));
        request.setStatus("undone");

        // HTTP 请求方式
        // @GetMapping 等组合注解自带动词；@RequestMapping 未写 method 时默认 GET
        MappingAnnotation methodMapping = WebAnnotationParser.mapping(methodDefinition);
        request.setMethod(resolveHttpMethod(methodMapping));

        // 请求路径（全局 × 类级别 × 方法级别）
        // 例：/api + /user/{id} → /api/user/{id}
        MappingAnnotation classMapping = WebAnnotationParser.mapping(classDefinition);
        String requestPath = RequestPathUtils.join(
                ProjectConfigs.globalRequestPrefix(project),
                classMapping == null ? null : classMapping.firstPath(),
                methodMapping == null ? null : methodMapping.firstPath()
        );
        request.setPath(requestPath);

        // 遍历方法参数，通过 YApiFields.kind() 判断绑定类型，分别收集
        List<YApiQueryParam> queryList = new ArrayList<>();   // @RequestParam → Query 参数
        List<YApiPathParam> pathList = new ArrayList<>();     // @PathVariable → Path 参数
        List<YApiHeader> headerList = new ArrayList<>();      // @RequestHeader → Header
        List<YApiFormParam> formList = new ArrayList<>();     // @RequestPart → Form 参数
        ParameterDefinition bodyParam = null;                 // @RequestBody → JSON Body
        boolean hasForm = false;

        for (ParameterDefinition parameter : methodDefinition.parameters()) {
            WebParameterAnnotation.Kind kind = YApiFields.kind(parameter);
            // 跳过无绑定注解的参数（如 HttpServletRequest 等框架注入类型）和需要忽略的参数
            if (kind == null || YApiFields.skip(parameter)) {
                continue;
            }

            switch (kind) {
                case QUERY:
                    queryList.add(toQueryParam(parameter));
                    break;
                case PATH:
                    pathList.add(toPathParam(parameter));
                    break;
                case HEADER:
                    headerList.add(toHeader(parameter));
                    break;
                case BODY:
                    bodyParam = parameter;
                    break;
                case PART:
                    formList.add(toFormParam(parameter));
                    hasForm = true;
                    break;
                default:
                    // COOKIE, ATTRIBUTE, MODEL 等类型暂不处理
                    break;
            }
        }

        // 请求头：consumes/produces + @RequestHeader + Content-Type
        // 1. 从类级别和方法级别的 @RequestMapping(consumes/produces) 提取 Content-Type / Accept
        addConsumesProduces(classMapping, headerList);
        addConsumesProduces(methodMapping, headerList);
        // 2. 根据 body/form 类型自动推断 Content-Type
        if (bodyParam != null) {
            headerList.add(contentTypeHeader(MediaType.APPLICATION_JSON_VALUE()));
        }
        if (hasForm) {
            headerList.add(contentTypeHeader(MediaType.MULTIPART_FORM_DATA_VALUE()));
        }
        // 3. 合并同名请求头（去重，值不同时用逗号拼接）
        headerList = mergeHeaders(headerList);

        request.setReq_headers(headerList.isEmpty() ? null : headerList);
        request.setReq_query(queryList.isEmpty() ? null : queryList);
        request.setReq_params(pathList.isEmpty() ? null : pathList);

        // 请求体：json body 和 form 互斥
        // YApi 约束：设置 req_body_type="json" 时不能同时设置 req_body_form，否则 form 不展示
        if (bodyParam != null) {
            request.setReq_body_type(ReqBodyType.JSON.getCode());
            request.setReq_body_is_json_schema(false);
            request.setReq_body_other(YApiJson.prettyWithComments(bodyParam));
        } else if (!formList.isEmpty()) {
            request.setReq_body_type(ReqBodyType.FORM.getCode());
            request.setReq_body_form(formList);
        }

        // 响应体
        // 通过 YApiJson 生成带字段注释的 pretty JSON，方便在 YApi 上阅读
        ParameterDefinition returns = methodDefinition.returns();
        if (returns != null) {
            request.setRes_body_type(ReqBodyType.JSON.getCode());
            request.setRes_body_is_json_schema(false);
            request.setRes_body(YApiJson.prettyWithComments(returns));
        }

        return request;
    }

    /**
     * 将方法参数映射为 YApi Query 参数
     * <p>
     * 对应 Spring 的 {@code @RequestParam} 绑定
     *
     * @param parameter 方法参数定义
     * @return YApi Query 参数对象
     */
    private YApiQueryParam toQueryParam(ParameterDefinition parameter) {
        YApiQueryParam query = new YApiQueryParam();
        query.setName(YApiFields.name(parameter));
        query.setRequired(YApiFields.required(parameter) ? String.valueOf(Boolean.TRUE.getNumberValue()) : String.valueOf(Boolean.FALSE.getNumberValue()));
        query.setDesc(YApiFields.description(parameter));
        Object example = YApiFields.example(parameter);
        query.setExample(example == null ? EMPTY : String.valueOf(example));
        return query;
    }

    /**
     * 将方法参数映射为 YApi Path 参数
     * <p>
     * 对应 Spring 的 {@code @PathVariable} 绑定
     *
     * @param parameter 方法参数定义
     * @return YApi Path 参数对象
     */
    private YApiPathParam toPathParam(ParameterDefinition parameter) {
        YApiPathParam path = new YApiPathParam();
        path.setName(YApiFields.name(parameter));
        path.setDesc(YApiFields.description(parameter));
        Object example = YApiFields.example(parameter);
        path.setExample(example == null ? EMPTY : String.valueOf(example));
        return path;
    }

    /**
     * 将方法参数映射为 YApi 请求头
     * <p>
     * 对应 Spring 的 {@code @RequestHeader} 绑定
     *
     * @param parameter 方法参数定义
     * @return YApi 请求头对象
     */
    private YApiHeader toHeader(ParameterDefinition parameter) {
        YApiHeader header = new YApiHeader();
        header.setName(YApiFields.name(parameter));
        header.setRequired(YApiFields.required(parameter) ? String.valueOf(Boolean.TRUE.getNumberValue()) : String.valueOf(Boolean.FALSE.getNumberValue()));
        header.setDesc(YApiFields.description(parameter));
        Object example = YApiFields.example(parameter);
        header.setExample(example == null ? EMPTY : String.valueOf(example));
        header.setValue(EMPTY);
        return header;
    }

    /**
     * 将方法参数映射为 YApi Form 参数
     * <p>
     * 对应 Spring 的 {@code @RequestPart} 绑定
     * MultipartFile 类型映射为 "file"，其余映射为 "text"
     *
     * @param parameter 方法参数定义
     * @return YApi Form 参数对象
     */
    private YApiFormParam toFormParam(ParameterDefinition parameter) {
        YApiFormParam form = new YApiFormParam();
        form.setName(YApiFields.name(parameter));
        // MultipartFile 类型在 YApi 中显示为文件上传控件
        form.setType(YApiFields.isMultipart(parameter.type()) ? ParameterType.FILE.getCode() : ParameterType.TEXT.getCode());
        form.setRequired(YApiFields.required(parameter) ? String.valueOf(Boolean.TRUE.getNumberValue()) : String.valueOf(Boolean.FALSE.getNumberValue()));
        form.setDesc(YApiFields.description(parameter));
        // 文件类型不需要示例值
        Object example = YApiFields.isMultipart(parameter.type()) ? EMPTY : YApiFields.example(parameter);
        form.setExample(example == null ? EMPTY : String.valueOf(example));
        return form;
    }

    /**
     * 从 MappingAnnotation 的 consumes/produces 属性中提取请求头
     * <p>
     * consumes → Content-Type 请求头，produces → Accept 请求头
     *
     * @param mapping     类级别或方法级别的映射注解，为 null 时跳过
     * @param headerList  请求头收集列表，结果追加到此列表
     */
    private void addConsumesProduces(MappingAnnotation mapping, List<YApiHeader> headerList) {
        if (mapping == null) {
            return;
        }
        List<String> consumes = mapping.consumes();
        if (consumes != null) {
            for (String item : consumes) {
                YApiHeader header = new YApiHeader();
                header.setName(WebTypes.CONTENT_TYPE);
                header.setRequired(String.valueOf(Boolean.TRUE.getNumberValue()));
                header.setValue(MediaType.getValue(item, item));
                header.setExample(MediaType.getValue(item, item));
                headerList.add(header);
            }
        }
        List<String> produces = mapping.produces();
        if (produces != null) {
            for (String item : produces) {
                YApiHeader header = new YApiHeader();
                header.setName(WebTypes.ACCEPT);
                header.setRequired(String.valueOf(Boolean.TRUE.getNumberValue()));
                header.setValue(MediaType.getValue(item, item));
                header.setExample(MediaType.getValue(item, item));
                headerList.add(header);
            }
        }
    }

    /**
     * 构造 Content-Type 请求头。
     *
     * @param value Media Type 值，如 "application/json" 或 "multipart/form-data"
     * @return YApi 请求头对象
     */
    private YApiHeader contentTypeHeader(String value) {
        YApiHeader header = new YApiHeader();
        header.setName(WebTypes.CONTENT_TYPE);
        header.setRequired(String.valueOf(Boolean.TRUE.getNumberValue()));
        header.setValue(value);
        header.setExample(value);
        // 描述字段：JSON 类型标记 "JSON"，其余标记 "表单"
        header.setDesc(value.contains("json") ? "JSON" : "表单");
        return header;
    }

    /**
     * 合并同名请求头，去重并合并值。
     * <p>
     * 规则：
     * <ul>
     *     <li>同名请求头只有一个 → 直接保留</li>
     *     <li>同名请求头有多个且值相同 → 保留第一个</li>
     *     <li>同名请求头有多个且值不同 → 值用逗号拼接，描述清空</li>
     * </ul>
     * 例如 Content-Type 同时出现在 consumes 和 body 推断中，值相同则去重，
     * 值不同（如同时声明了 application/json 和 multipart/form-data）则逗号拼接。
     *
     * @param headerList 待合并的请求头列表
     * @return 合并后的请求头列表
     */
    private List<YApiHeader> mergeHeaders(List<YApiHeader> headerList) {
        if (headerList.isEmpty()) {
            return headerList;
        }
        // 按请求头名称分组，保持插入顺序
        Map<String, List<YApiHeader>> grouped = new LinkedHashMap<>();
        for (YApiHeader header : headerList) {
            grouped.computeIfAbsent(header.getName(), k -> new ArrayList<>()).add(header);
        }
        List<YApiHeader> merged = new ArrayList<>();
        for (Map.Entry<String, List<YApiHeader>> entry : grouped.entrySet()) {
            List<YApiHeader> headers = entry.getValue();
            if (headers.size() == 1) {
                merged.add(headers.get(0));
            } else {
                // 去重值（保持顺序）
                Set<String> values = new LinkedHashSet<>();
                for (YApiHeader h : headers) {
                    String v = h.getExample() == null ? EMPTY : h.getExample();
                    values.add(v);
                }
                YApiHeader first = headers.get(0);
                if (values.size() == 1) {
                    // 值相同，只保留一个
                    merged.add(first);
                } else {
                    // 值不同，逗号拼接，描述清空（无法确定单一含义）
                    first.setExample(String.join(COMMA_SPACE, values));
                    first.setValue(String.join(COMMA_SPACE, values));
                    first.setDesc(EMPTY);
                    merged.add(first);
                }
            }
        }
        return merged;
    }
    
    /**
     * 解析 HTTP 方法：Mapping 未写 method 时默认 GET。
     *
     * @param mapping 方法级 Mapping 注解
     * @return 大写 HTTP 方法名
     */
    private String resolveHttpMethod(MappingAnnotation mapping) {
        if (mapping == null || CollectionUtils.isEmpty(mapping.methods())) {
            return HttpMethod.GET.name();
        }
        
        return mapping.methods().get(0).toUpperCase();
    }

    /**
     * 将字符串安全地解析为数字
     * <p>
     * 依次尝试解析为 Integer → Long → 解析失败返回 0
     *
     * @param value 待解析的字符串，可能为 null 或空
     * @return 解析后的数字，解析失败返回 0
     */
    private Number parseNumber(String value) {
        if (value == null || value.isEmpty()) {
            return 0;
        }
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

}
