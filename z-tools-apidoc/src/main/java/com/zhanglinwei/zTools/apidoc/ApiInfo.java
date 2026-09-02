package com.zhanglinwei.zTools.apidoc;

import com.intellij.ide.impl.ProjectUtil;
import com.intellij.openapi.project.Project;
import com.zhanglinwei.zTools.annotation.model.ClassDefinition;
import com.zhanglinwei.zTools.annotation.model.MethodDefinition;
import com.zhanglinwei.zTools.annotation.model.ParameterDefinition;
import com.zhanglinwei.zTools.annotation.model.PropertyDefinition;
import com.zhanglinwei.zTools.annotation.web.MappingAnnotation;
import com.zhanglinwei.zTools.common.util.RequestPathUtils;
import com.zhanglinwei.zTools.annotation.web.WebAnnotationParser;
import com.zhanglinwei.zTools.annotation.web.WebParameterAnnotation;
import com.zhanglinwei.zTools.common.constant.MediaType;
import com.zhanglinwei.zTools.common.constant.WebTypes;
import com.zhanglinwei.zTools.configure.config.DocumentConfig;
import com.zhanglinwei.zTools.common.util.CollectionUtils;
import com.zhanglinwei.zTools.common.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.zhanglinwei.zTools.common.constant.StringPool.COMMA_SPACE;
import static com.zhanglinwei.zTools.common.constant.StringPool.EMPTY;
import static com.zhanglinwei.zTools.common.constant.StringPool.FOLD;

/**
 * 一份接口文档的数据模型：标题、基本信息、请求、响应。
 * <p>
 * 由 {@link GenerateApiDocAction} 在解析出类/方法定义后调用 {@link #create}，
 * 再交给 {@link com.zhanglinwei.zTools.apidoc.generator.ApiDocumentGenerator} 填入 Freemarker。
 * 字段名、必填、说明、示例一律走 {@link ApiFields}；请求/响应 JSON 走 {@link ApiJson}。
 */
public class ApiInfo {

    private String title;
    private String description;
    private ApiBaseInfo baseInfo;
    private ApiRequestInfo requestInfo;
    private ApiResponseInfo responseInfo;

    /** 仅通过 {@link #create} 构造。 */
    private ApiInfo() {}

    /**
     * 为控制器上每个 Mapping 方法各建一份 {@link ApiInfo}。
     *
     * @param type 控制器类定义
     * @return 接口列表；{@code type} 为 {@code null} 时返回空列表
     */
    public static List<ApiInfo> create(ClassDefinition type) {
        if (type == null) {
            return Collections.emptyList();
        }

        return type.methods().stream()
                .map(method -> create(type, method))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 为单个 Mapping 方法建一份 {@link ApiInfo}。
     *
     * @param type   所在控制器
     * @param method 接口方法
     * @return 组装好的模型；不是 handler 方法或 {@code type} 为 {@code null} 时返回 {@code null}
     */
    public static ApiInfo create(ClassDefinition type, MethodDefinition method) {
        if (type == null || !WebAnnotationParser.isHandlerMethod(method)) {
            return null;
        }

        String title = ApiFields.titleOf(method);
        String description = ApiFields.descriptionOf(method);

        ApiInfo apiInfo = new ApiInfo();
        apiInfo.setTitle(title);
        apiInfo.setDescription(description);
        apiInfo.setBaseInfo(new ApiBaseInfo(type, method, description));
        apiInfo.setRequestInfo(new ApiRequestInfo(type, method));
        apiInfo.setResponseInfo(new ApiResponseInfo(method));
        return apiInfo;
    }

    /**
     * 接口基本信息：HTTP 动词、路径、描述。
     */
    public static class ApiBaseInfo {
        private String requestType;
        private String requestPath;
        private String description;

        /**
         * 从类 / 方法 Mapping 拼路径和动词。
         *
         * @param type        控制器
         * @param method      接口方法
         * @param description 方法描述，原样保存
         */
        public ApiBaseInfo(ClassDefinition type, MethodDefinition method, String description) {
            MappingAnnotation classMapping = WebAnnotationParser.mapping(type);
            MappingAnnotation methodMapping = WebAnnotationParser.mapping(method);
            List<String> paths = RequestPathUtils.combine(
                    classMapping == null ? null : classMapping.paths(),
                    methodMapping == null ? null : methodMapping.paths()
            );
            this.requestPath = String.join(COMMA_SPACE, paths);
            this.requestType = verbs(classMapping, methodMapping);
            this.description = description;
        }

        /**
         * 方法 Mapping 的动词优先于类 Mapping；都没有则空串。
         *
         * @param classMapping  类 Mapping
         * @param methodMapping 方法 Mapping
         * @return 大写动词，多个用逗号分隔
         */
        private static String verbs(MappingAnnotation classMapping, MappingAnnotation methodMapping) {
            List<String> names = methodMapping == null ? null : methodMapping.methods();
            if (names == null || names.isEmpty()) {
                names = classMapping == null ? null : classMapping.methods();
            }
            if (names == null || names.isEmpty()) {
                return EMPTY;
            }
            List<String> verbs = new ArrayList<>(names.size());
            for (String name : names) {
                if (StringUtils.isNotBlank(name)) {
                    verbs.add(name.toUpperCase(Locale.ROOT));
                }
            }
            return String.join(COMMA_SPACE, verbs);
        }

        /** @return HTTP 动词，如 {@code POST} */
        public String getRequestType() {
            return requestType;
        }

        /** @param requestType HTTP 动词 */
        public void setRequestType(String requestType) {
            this.requestType = requestType;
        }

        /** @return 组合后的请求路径，多路径用逗号分隔 */
        public String getRequestPath() {
            return requestPath;
        }

        /** @param requestPath 请求路径 */
        public void setRequestPath(String requestPath) {
            this.requestPath = requestPath;
        }

        /** @return 接口描述 */
        public String getDescription() {
            return description;
        }

        /** @param description 接口描述 */
        public void setDescription(String description) {
            this.description = description;
        }
    }

    /**
     * 请求/响应体表格与 JSON 的公共构造：按字段树铺成行，JSON 交给 {@link ApiJson}。
     */
    private abstract static class AbstractBody {
        /**
         * 递归把字段铺成表格行，子字段名前加配置前缀。
         *
         * @param prefix   当前层名称前缀
         * @param children 子字段
         * @return 表格行，无字段时为空列表
         */
        protected static List<TableRowInfo> createTableRow(String prefix, List<PropertyDefinition> children) {
            if (CollectionUtils.isEmpty(children)) {
                return Collections.emptyList();
            }
            String cfgPrefix = tablePrefix();
            List<TableRowInfo> rowList = new ArrayList<TableRowInfo>();
            for (int i = 0; i < children.size(); i++) {
                PropertyDefinition property = children.get(i);
                rowList.add(new TableRowInfo(
                        prefix + ApiFields.name(property),
                        property.type(),
                        ApiFields.required(property),
                        ApiFields.description(property),
                        ApiFields.example(property)
                ));
                rowList.addAll(createTableRow(prefix + cfgPrefix, property.properties()));
            }
            return rowList;
        }

        /**
         * 按 body 参数类型建表格：Map 给占位行，对象展开字段，叶子给单行。
         *
         * @param body 请求体或返回值
         * @return 表格；{@code body} 为 {@code null} 时返回 {@code null}
         */
        protected static ApiTableInfo createBody(ParameterDefinition body) {
            if (body == null) {
                return null;
            }
            List<TableRowInfo> rowList = new ArrayList<TableRowInfo>();
            if (ApiFields.isMap(body.type())) {
                rowList.add(new TableRowInfo("KEY", "VALUE", false, "这是一个 Map 参数", ""));
            } else if (!CollectionUtils.isEmpty(body.properties())) {
                rowList.addAll(createTableRow(EMPTY, body.properties()));
            } else if (ApiFields.isNormal(body.type()) || body.name() != null) {
                rowList.add(new TableRowInfo(
                        ApiFields.name(body),
                        body.type(),
                        ApiFields.required(body),
                        ApiFields.description(body),
                        ApiFields.example(body)
                ));
            }
            return new ApiTableInfo(rowList);
        }

        /**
         * 请求/响应示例 JSON（pretty + 行注释）。
         *
         * @param body 请求体或返回值
         * @return JSON 文本；{@code body} 为 {@code null} 时返回 {@code null}
         */
        protected static String createBodyJson(ParameterDefinition body) {
            return body == null ? null : ApiJson.prettyWithComments(body);
        }

        /**
         * 表格子字段名前缀，来自设置页；无工程时用默认折叠符。
         *
         * @return 如 {@code └ }
         */
        private static String tablePrefix() {
            Project project = ProjectUtil.getActiveProject();
            if (project == null) {
                return FOLD;
            }
            return DocumentConfig.getInstance(project).getApiDocConfig().getPrefix();
        }
    }

    /**
     * 请求侧：Header、Path、Query、Form、Body 表以及 Body JSON。
     */
    public static class ApiRequestInfo extends AbstractBody {
        private ApiTableInfo requestHeader;
        private ApiTableInfo pathVariable;
        private ApiTableInfo requestParam;
        private ApiTableInfo formParam;
        private ApiTableInfo requestBody;
        private String requestBodyJson;

        /**
         * 按参数 kind 拆到各表，并生成 body JSON。
         *
         * @param type   控制器（consumes / produces 写进 Header）
         * @param method 接口方法
         */
        public ApiRequestInfo(ClassDefinition type, MethodDefinition method) {
            this.requestHeader = createRequestHeader(type, method);
            this.pathVariable = rows(method, WebParameterAnnotation.Kind.PATH, false);
            this.requestParam = rows(method, WebParameterAnnotation.Kind.QUERY, true);
            this.formParam = createFormParam(method);
            ParameterDefinition body = firstOfKind(method, WebParameterAnnotation.Kind.BODY);
            this.requestBody = createBody(body);
            this.requestBodyJson = createBodyJson(body);
        }

        /**
         * 收集 {@code @RequestPart} / multipart 参数。上传类型示例固定为「文件」。
         *
         * @param method 接口方法
         * @return 表单参数表
         */
        private static ApiTableInfo createFormParam(MethodDefinition method) {
            List<ParameterDefinition> parameters = method.parameters();
            List<TableRowInfo> rowList = new ArrayList<TableRowInfo>();
            for (ParameterDefinition parameter : parameters) {
                if (ApiFields.kind(parameter) != WebParameterAnnotation.Kind.PART || ApiFields.skip(parameter)) {
                    continue;
                }
                Object example = ApiFields.isMultipart(parameter.type()) ? "文件" : ApiJson.flatten(parameter);
                rowList.add(new TableRowInfo(
                        ApiFields.name(parameter),
                        parameter.type(),
                        ApiFields.required(parameter),
                        ApiFields.description(parameter),
                        example
                ));
            }
            return new ApiTableInfo(rowList);
        }

        /**
         * 收集指定 kind 的参数行。
         *
         * @param method         接口方法
         * @param kind           目标种类
         * @param skipMultipart  为 {@code true} 时再过滤掉上传类型（Query 表用）
         * @return 参数表
         */
        private static ApiTableInfo rows(MethodDefinition method, WebParameterAnnotation.Kind kind, boolean skipMultipart) {
            List<ParameterDefinition> parameters = method.parameters();
            List<TableRowInfo> rowList = new ArrayList<TableRowInfo>();
            for (ParameterDefinition parameter : parameters) {
                if (ApiFields.kind(parameter) != kind || ApiFields.skip(parameter)) {
                    continue;
                }
                if (skipMultipart && ApiFields.isMultipart(parameter.type())) {
                    continue;
                }
                rowList.add(new TableRowInfo(
                        ApiFields.name(parameter),
                        parameter.type(),
                        ApiFields.required(parameter),
                        ApiFields.description(parameter),
                        ApiFields.example(parameter)
                ));
            }
            return new ApiTableInfo(rowList);
        }

        /**
         * 第一个指定 kind 且未跳过的参数。
         *
         * @param method 接口方法
         * @param kind   目标种类
         * @return 参数；没有则为 {@code null}
         */
        private static ParameterDefinition firstOfKind(MethodDefinition method, WebParameterAnnotation.Kind kind) {
            List<ParameterDefinition> parameters = method.parameters();
            for (ParameterDefinition parameter : parameters) {
                if (ApiFields.kind(parameter) == kind && !ApiFields.skip(parameter)) {
                    return parameter;
                }
            }
            return null;
        }

        /**
         * 组装 Header 表：consumes/produces、{@code @RequestHeader}，以及按 body/form 推断的 Content-Type。
         *
         * @param type   控制器
         * @param method 接口方法
         * @return 合并同名后的 Header 表
         */
        private static ApiTableInfo createRequestHeader(ClassDefinition type, MethodDefinition method) {
            MappingAnnotation classMapping = WebAnnotationParser.mapping(type);
            MappingAnnotation methodMapping = WebAnnotationParser.mapping(method);
            List<TableRowInfo> headerList = new ArrayList<>();
            headerList.addAll(consumesAndProduces(methodMapping));
            headerList.addAll(consumesAndProduces(classMapping));

            boolean hasBody = false;
            boolean hasForm = false;
            List<ParameterDefinition> parameters = method.parameters();
            for (ParameterDefinition parameter : parameters) {
                WebParameterAnnotation.Kind kind = ApiFields.kind(parameter);
                if (kind == WebParameterAnnotation.Kind.HEADER && !ApiFields.skip(parameter)) {
                    headerList.add(new TableRowInfo(
                            ApiFields.name(parameter),
                            "String",
                            ApiFields.required(parameter),
                            ApiFields.description(parameter),
                            ApiFields.example(parameter)
                    ));
                } else if (kind == WebParameterAnnotation.Kind.PART || ApiFields.isMultipart(parameter.type())) {
                    hasForm = true;
                } else if (kind == WebParameterAnnotation.Kind.BODY) {
                    hasBody = true;
                }
            }
            if (hasForm) {
                headerList.add(new TableRowInfo(WebTypes.CONTENT_TYPE, "String", true, "表单", MediaType.MULTIPART_FORM_DATA_VALUE()));
            }
            if (hasBody) {
                headerList.add(new TableRowInfo(WebTypes.CONTENT_TYPE, "String", true, "JSON", MediaType.APPLICATION_JSON_VALUE()));
            }
            return new ApiTableInfo(mergeHeader(headerList));
        }

        /**
         * 把 Mapping 的 consumes / produces 转成 Content-Type / Accept 行。
         *
         * @param mapping 类或方法 Mapping
         * @return Header 行；{@code mapping} 为 {@code null} 时为空列表
         */
        private static List<TableRowInfo> consumesAndProduces(MappingAnnotation mapping) {
            List<TableRowInfo> headers = new ArrayList<TableRowInfo>();
            if (mapping == null) {
                return headers;
            }
            List<String> consumes = mapping.consumes();
            if (consumes != null) {
                for (String item : consumes) {
                    headers.add(new TableRowInfo(WebTypes.CONTENT_TYPE, "String", true, EMPTY, MediaType.getValue(item, item)));
                }
            }
            List<String> produces = mapping.produces();
            if (produces != null) {
                for (String item : produces) {
                    headers.add(new TableRowInfo(WebTypes.ACCEPT, "String", true, EMPTY, MediaType.getValue(item, item)));
                }
            }
            return headers;
        }

        /**
         * 同名 Header 合并：单值保留原行，多值把 example 拼成逗号列表。
         *
         * @param headerList 未合并的 Header
         * @return 按名称去重后的列表
         */
        private static List<TableRowInfo> mergeHeader(List<TableRowInfo> headerList) {
            Map<String, List<TableRowInfo>> grouped = new LinkedHashMap<String, List<TableRowInfo>>();
            for (TableRowInfo header : headerList) {
                List<TableRowInfo> same = grouped.computeIfAbsent(header.getName(),
                        k -> new ArrayList<>());
                same.add(header);
            }
            List<TableRowInfo> merged = new ArrayList<>();
            for (Map.Entry<String, List<TableRowInfo>> entry : grouped.entrySet()) {
                List<TableRowInfo> headers = entry.getValue();
                List<String> values = new ArrayList<String>();
                for (TableRowInfo header : headers) {
                    String text = header.getExample() == null ? EMPTY : header.getExample();
                    if (!values.contains(text)) {
                        values.add(text);
                    }
                }
                if (values.size() == 1) {
                    merged.add(new TableRowInfo(
                            entry.getKey(),
                            "String",
                            headers.get(0).isRequired(),
                            headers.get(0).getDescription(),
                            headers.get(0).getExample()
                    ));
                } else {
                    merged.add(new TableRowInfo(
                            entry.getKey(),
                            "String",
                            headers.get(0).isRequired(),
                            EMPTY,
                            String.join(COMMA_SPACE, values)
                    ));
                }
            }
            return merged;
        }

        /** @return Header 表 */
        public ApiTableInfo getRequestHeader() {
            return requestHeader;
        }

        /** @param requestHeader Header 表 */
        public void setRequestHeader(ApiTableInfo requestHeader) {
            this.requestHeader = requestHeader;
        }

        /** @return PathVariable 表 */
        public ApiTableInfo getPathVariable() {
            return pathVariable;
        }

        /** @param pathVariable PathVariable 表 */
        public void setPathVariable(ApiTableInfo pathVariable) {
            this.pathVariable = pathVariable;
        }

        /** @return Query 参数表 */
        public ApiTableInfo getRequestParam() {
            return requestParam;
        }

        /** @param requestParam Query 参数表 */
        public void setRequestParam(ApiTableInfo requestParam) {
            this.requestParam = requestParam;
        }

        /** @return 表单 / multipart 参数表 */
        public ApiTableInfo getFormParam() {
            return formParam;
        }

        /** @param formParam 表单参数表 */
        public void setFormParam(ApiTableInfo formParam) {
            this.formParam = formParam;
        }

        /** @return 请求体字段表 */
        public ApiTableInfo getRequestBody() {
            return requestBody;
        }

        /** @param requestBody 请求体字段表 */
        public void setRequestBody(ApiTableInfo requestBody) {
            this.requestBody = requestBody;
        }

        /** @return 请求体 pretty JSON（可能已着色） */
        public String getRequestBodyJson() {
            return requestBodyJson;
        }

        /** @param requestBodyJson 请求体 JSON */
        public void setRequestBodyJson(String requestBodyJson) {
            this.requestBodyJson = requestBodyJson;
        }
    }

    /**
     * 响应侧：返回值字段表与示例 JSON。
     */
    public static class ApiResponseInfo extends AbstractBody {
        private ApiTableInfo responseBody;
        private String responseBodyJson;

        /**
         * 用方法返回值构造响应表和 JSON。
         *
         * @param method 接口方法
         */
        public ApiResponseInfo(MethodDefinition method) {
            ParameterDefinition returns = method.returns();
            this.responseBody = createBody(returns);
            this.responseBodyJson = createBodyJson(returns);
        }

        /** @return 响应体字段表 */
        public ApiTableInfo getResponseBody() {
            return responseBody;
        }

        /** @param responseBody 响应体字段表 */
        public void setResponseBody(ApiTableInfo responseBody) {
            this.responseBody = responseBody;
        }

        /** @return 响应体 pretty JSON（可能已着色） */
        public String getResponseBodyJson() {
            return responseBodyJson;
        }

        /** @param responseBodyJson 响应体 JSON */
        public void setResponseBodyJson(String responseBodyJson) {
            this.responseBodyJson = responseBodyJson;
        }
    }

    /**
     * 文档里的一张参数表。
     */
    public static class ApiTableInfo {
        private List<TableRowInfo> rowList;

        /**
         * @param rowList 行数据；{@code null} 时存空列表
         */
        public ApiTableInfo(List<TableRowInfo> rowList) {
            this.rowList = rowList != null ? rowList : new ArrayList<TableRowInfo>();
        }

        /** @return 表格行 */
        public List<TableRowInfo> getRowList() {
            return this.rowList;
        }

        /** @param rowList 表格行 */
        public void setRowList(List<TableRowInfo> rowList) {
            this.rowList = rowList;
        }
    }

    /**
     * 参数表的一行：名称、类型、必填、说明、示例。
     */
    public static class TableRowInfo {
        private String name;
        private String type;
        private boolean required;
        private String description;
        private String example;

        /**
         * @param name        字段 / 参数名
         * @param type        声明类型
         * @param required    是否必填
         * @param description 说明
         * @param example     示例，{@code null} 存空串
         */
        public TableRowInfo(String name, String type, boolean required, String description, Object example) {
            this.name = name;
            this.type = type;
            this.required = required;
            this.description = description;
            this.example = example == null ? EMPTY : String.valueOf(example);
        }

        /** @return 名称 */
        public String getName() {
            return name;
        }

        /** @param name 名称 */
        public void setName(String name) {
            this.name = name;
        }

        /** @return 类型 */
        public String getType() {
            return type;
        }

        /** @param type 类型 */
        public void setType(String type) {
            this.type = type;
        }

        /** @return 必填则为 {@code true} */
        public boolean isRequired() {
            return required;
        }

        /** @param required 是否必填 */
        public void setRequired(boolean required) {
            this.required = required;
        }

        /** @return 说明 */
        public String getDescription() {
            return description;
        }

        /** @param description 说明 */
        public void setDescription(String description) {
            this.description = description;
        }

        /** @return 示例文本 */
        public String getExample() {
            return example;
        }

        /** @param example 示例文本 */
        public void setExample(String example) {
            this.example = example;
        }
    }

    /** @return 接口标题 */
    public String getTitle() {
        return title;
    }

    /** @param title 接口标题 */
    public void setTitle(String title) {
        this.title = title;
    }

    /** @return 接口描述 */
    public String getDescription() {
        return description;
    }

    /** @param description 接口描述 */
    public void setDescription(String description) {
        this.description = description;
    }

    /** @return 基本信息 */
    public ApiBaseInfo getBaseInfo() {
        return baseInfo;
    }

    /** @param baseInfo 基本信息 */
    public void setBaseInfo(ApiBaseInfo baseInfo) {
        this.baseInfo = baseInfo;
    }

    /** @return 请求信息 */
    public ApiRequestInfo getRequestInfo() {
        return requestInfo;
    }

    /** @param requestInfo 请求信息 */
    public void setRequestInfo(ApiRequestInfo requestInfo) {
        this.requestInfo = requestInfo;
    }

    /** @return 响应信息 */
    public ApiResponseInfo getResponseInfo() {
        return responseInfo;
    }

    /** @param responseInfo 响应信息 */
    public void setResponseInfo(ApiResponseInfo responseInfo) {
        this.responseInfo = responseInfo;
    }
}
