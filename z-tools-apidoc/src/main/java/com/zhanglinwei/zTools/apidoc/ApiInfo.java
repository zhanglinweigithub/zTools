package com.zhanglinwei.zTools.apidoc;

import com.intellij.ide.impl.ProjectUtil;
import com.intellij.openapi.project.Project;
import com.zhanglinwei.zTools.annotation.model.ClassDefinition;
import com.zhanglinwei.zTools.annotation.model.MethodDefinition;
import com.zhanglinwei.zTools.annotation.model.ParameterDefinition;
import com.zhanglinwei.zTools.annotation.model.PropertyDefinition;
import com.zhanglinwei.zTools.annotation.web.MappingAnnotation;
import com.zhanglinwei.zTools.annotation.web.RequestPaths;
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

public class ApiInfo {

    private String title;
    private String description;
    private ApiBaseInfo baseInfo;
    private ApiRequestInfo requestInfo;
    private ApiResponseInfo responseInfo;

    private ApiInfo() {}

    public static List<ApiInfo> create(ClassDefinition type) {
        if (type == null) {
            return Collections.emptyList();
        }

        return type.methods().stream()
                .map(method -> create(type, method))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

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

    public static class ApiBaseInfo {
        private String requestType;
        private String requestPath;
        private String description;

        public ApiBaseInfo(ClassDefinition type, MethodDefinition method, String description) {
            MappingAnnotation classMapping = WebAnnotationParser.mapping(type);
            MappingAnnotation methodMapping = WebAnnotationParser.mapping(method);
            List<String> paths = RequestPaths.combine(
                    classMapping == null ? null : classMapping.paths(),
                    methodMapping == null ? null : methodMapping.paths()
            );
            this.requestPath = String.join(COMMA_SPACE, paths);
            this.requestType = verbs(classMapping, methodMapping);
            this.description = description;
        }

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

        public String getRequestType() {
            return requestType;
        }

        public void setRequestType(String requestType) {
            this.requestType = requestType;
        }

        public String getRequestPath() {
            return requestPath;
        }

        public void setRequestPath(String requestPath) {
            this.requestPath = requestPath;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    private abstract static class AbstractBody {
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

        protected static String createBodyJson(ParameterDefinition body) {
            return body == null ? null : ApiJson.prettyWithComments(body);
        }

        private static String tablePrefix() {
            Project project = ProjectUtil.getActiveProject();
            if (project == null) {
                return FOLD;
            }
            return DocumentConfig.getInstance(project).getApiDocConfig().getPrefix();
        }
    }

    public static class ApiRequestInfo extends AbstractBody {
        private ApiTableInfo requestHeader;
        private ApiTableInfo pathVariable;
        private ApiTableInfo requestParam;
        private ApiTableInfo formParam;
        private ApiTableInfo requestBody;
        private String requestBodyJson;

        public ApiRequestInfo(ClassDefinition type, MethodDefinition method) {
            this.requestHeader = createRequestHeader(type, method);
            this.pathVariable = rows(method, WebParameterAnnotation.Kind.PATH, false);
            this.requestParam = rows(method, WebParameterAnnotation.Kind.QUERY, true);
            this.formParam = createFormParam(method);
            ParameterDefinition body = firstOfKind(method, WebParameterAnnotation.Kind.BODY);
            this.requestBody = createBody(body);
            this.requestBodyJson = createBodyJson(body);
        }

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

        private static ParameterDefinition firstOfKind(MethodDefinition method, WebParameterAnnotation.Kind kind) {
            List<ParameterDefinition> parameters = method.parameters();
            for (ParameterDefinition parameter : parameters) {
                if (ApiFields.kind(parameter) == kind && !ApiFields.skip(parameter)) {
                    return parameter;
                }
            }
            return null;
        }

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

        public ApiTableInfo getRequestHeader() {
            return requestHeader;
        }

        public void setRequestHeader(ApiTableInfo requestHeader) {
            this.requestHeader = requestHeader;
        }

        public ApiTableInfo getPathVariable() {
            return pathVariable;
        }

        public void setPathVariable(ApiTableInfo pathVariable) {
            this.pathVariable = pathVariable;
        }

        public ApiTableInfo getRequestParam() {
            return requestParam;
        }

        public void setRequestParam(ApiTableInfo requestParam) {
            this.requestParam = requestParam;
        }

        public ApiTableInfo getFormParam() {
            return formParam;
        }

        public void setFormParam(ApiTableInfo formParam) {
            this.formParam = formParam;
        }

        public ApiTableInfo getRequestBody() {
            return requestBody;
        }

        public void setRequestBody(ApiTableInfo requestBody) {
            this.requestBody = requestBody;
        }

        public String getRequestBodyJson() {
            return requestBodyJson;
        }

        public void setRequestBodyJson(String requestBodyJson) {
            this.requestBodyJson = requestBodyJson;
        }
    }

    public static class ApiResponseInfo extends AbstractBody {
        private ApiTableInfo responseBody;
        private String responseBodyJson;

        public ApiResponseInfo(MethodDefinition method) {
            ParameterDefinition returns = method.returns();
            this.responseBody = createBody(returns);
            this.responseBodyJson = createBodyJson(returns);
        }

        public ApiTableInfo getResponseBody() {
            return responseBody;
        }

        public void setResponseBody(ApiTableInfo responseBody) {
            this.responseBody = responseBody;
        }

        public String getResponseBodyJson() {
            return responseBodyJson;
        }

        public void setResponseBodyJson(String responseBodyJson) {
            this.responseBodyJson = responseBodyJson;
        }
    }

    public static class ApiTableInfo {
        private List<TableRowInfo> rowList;

        public ApiTableInfo(List<TableRowInfo> rowList) {
            this.rowList = rowList != null ? rowList : new ArrayList<TableRowInfo>();
        }

        public List<TableRowInfo> getRowList() {
            return this.rowList;
        }

        public void setRowList(List<TableRowInfo> rowList) {
            this.rowList = rowList;
        }
    }

    public static class TableRowInfo {
        private String name;
        private String type;
        private boolean required;
        private String description;
        private String example;

        public TableRowInfo(String name, String type, boolean required, String description, Object example) {
            this.name = name;
            this.type = type;
            this.required = required;
            this.description = description;
            this.example = example == null ? EMPTY : String.valueOf(example);
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public boolean isRequired() {
            return required;
        }

        public void setRequired(boolean required) {
            this.required = required;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getExample() {
            return example;
        }

        public void setExample(String example) {
            this.example = example;
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ApiBaseInfo getBaseInfo() {
        return baseInfo;
    }

    public void setBaseInfo(ApiBaseInfo baseInfo) {
        this.baseInfo = baseInfo;
    }

    public ApiRequestInfo getRequestInfo() {
        return requestInfo;
    }

    public void setRequestInfo(ApiRequestInfo requestInfo) {
        this.requestInfo = requestInfo;
    }

    public ApiResponseInfo getResponseInfo() {
        return responseInfo;
    }

    public void setResponseInfo(ApiResponseInfo responseInfo) {
        this.responseInfo = responseInfo;
    }
}
