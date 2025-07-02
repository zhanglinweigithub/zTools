package com.zhanglinwei.zTools.doc.apidoc.model;

import com.intellij.psi.*;
import com.zhanglinwei.zTools.common.constants.MediaType;
import com.zhanglinwei.zTools.common.constants.WebAnnotation;
import com.zhanglinwei.zTools.doc.config.DocConfig;
import com.zhanglinwei.zTools.util.*;

import java.util.*;
import java.util.stream.Collectors;

import static com.zhanglinwei.zTools.common.constants.SpringPool.*;

public class ApiInfo {

    private String title;
    private String description;
    private ApiBaseInfo baseInfo;
    private ApiRequestInfo requestInfo;
    private ApiResponseInfo responseInfo;

    private boolean empty;

    private ApiInfo(){}

    public static List<ApiInfo> create(PsiClass psiClass) {
        return psiClass == null ? Collections.emptyList() : Arrays.stream(psiClass.getAllMethods()).map(ApiInfo::create).filter(api -> !api.isEmpty()).collect(Collectors.toList());
    }

    public static ApiInfo create(PsiMethod psiMethod) {
        ApiInfo apiInfo = new ApiInfo();

        // 忽略未标注xxxMapping注解的方法
        if (!AnnotationUtil.hasMappingAnnotation(psiMethod)) {
            apiInfo.setEmpty(true);
            return apiInfo;
        }
        // 方法所在类
        PsiClass psiClass = psiMethod.getContainingClass();
        if (psiClass == null) {
            apiInfo.setEmpty(true);
            return apiInfo;
        }

        // 方法标题: 优先使用方法注释
        String methodDescription = CommentsUtil.extractComments(psiMethod, psiMethod.getName());
        apiInfo.setTitle(methodDescription);

        // 方法注释
        apiInfo.setDescription(methodDescription);

        // 方法基本信息
        apiInfo.setBaseInfo(new ApiBaseInfo(psiMethod, psiClass));

        // 方法请求信息
        apiInfo.setRequestInfo(new ApiRequestInfo(psiMethod, psiClass));

        // 方法响应信息
        apiInfo.setResponseInfo(new ApiResponseInfo(psiMethod));

        return apiInfo;
    }

    public static class ApiBaseInfo {
        private String requestType;
        private String requestPath;

        public ApiBaseInfo(PsiMethod psiMethod, PsiClass psiClass) {
            PsiAnnotation classMappingAnnotation = AnnotationUtil.findXxxMappingAnnotation(psiClass.getAnnotations());
            PsiAnnotation methodMappingAnnotation = AnnotationUtil.findXxxMappingAnnotation(psiMethod.getAnnotations());

            String classPath = AnnotationUtil.extractPathFromAnnotation(classMappingAnnotation);
            String methodPath = AnnotationUtil.extractPathFromAnnotation(methodMappingAnnotation);

            this.requestPath = CommonUtils.buildPath(classPath, methodPath);
            this.requestType = AnnotationUtil.extractRequestTypeFromAnnotation(classMappingAnnotation, methodMappingAnnotation);
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
    }

    private abstract static class AbstractBody {
        protected static List<TableRowInfo> createTableRow(String prefix, List<JavaProperty> children) {
            if (AssertUtils.isEmpty(children)) {
                return Collections.emptyList();
            }
            String cfgPrefix = DocConfig.getInstance(children.get(0).getProject()).getApiDocConfig().getPrefix();
            List<TableRowInfo> rowList = new ArrayList<>();

            children.forEach(property -> {
                TableRowInfo rowInfo = new TableRowInfo(prefix + property.getOriginName(), property.getTypeName(), property.isRequired(), property.getComment(), property.getExample());
                rowList.add(rowInfo);
                rowList.addAll(createTableRow(prefix + cfgPrefix, property.getChildren()));
            });
            return rowList;
        }

        protected static ApiTableInfo createBody(JavaProperty body) {
            if (body != null) {
                JavaProperty realBody = body;
                if (TypeUtils.isIterableType(realBody.getPsiType())) {
                    PsiType realType = TypeUtils.deepExtractIterableType(realBody.getPsiType()).getRealType();
                    realBody = JavaProperty.createSimple(realType, realBody.getProject(), realBody.getParent());
                }

                PsiType realType = realBody.getPsiType();
                List<TableRowInfo> rowList = new ArrayList<>();

                if (TypeUtils.isMapType(realType)) {
                    rowList.add(new TableRowInfo("KEY", "VALUE", false, "这是一个 Map 参数", ""));
                }
                else if (TypeUtils.isNormalType(realType)) {
                    TableRowInfo rowInfo = new TableRowInfo(body.getOriginName(), body.getTypeName(), body.isRequired(), body.getComment(), body.getExample());
                    rowList.add(rowInfo);
                }
                else {
                    List<TableRowInfo> infoList = createTableRow(EMPTY, realBody.getChildren());
                    rowList.addAll(infoList);
                }
                return new ApiTableInfo(rowList);
            }

            return null;
        }
        protected static String createBodyJson(JavaProperty body) {
            return body == null ? null : JsonUtil.prettyJsonWithComment(body);
        }
    }

    public static class ApiRequestInfo extends AbstractBody {
        private ApiTableInfo requestHeader;
        private ApiTableInfo pathVariable;
        private ApiTableInfo requestParam;
        private ApiTableInfo formParam;
        private ApiTableInfo requestBody;
        private String requestBodyJson;

        private JavaProperty originRequestBody;

        public ApiRequestInfo(PsiMethod psiMethod, PsiClass psiClass) {
            List<JavaProperty> requestList = JavaProperty.create(psiMethod);
            this.requestHeader = createRequestHeader(psiMethod, psiClass);
            this.pathVariable = createPathVariable(psiMethod, requestList);
            this.requestParam = createRequestParam(psiMethod, requestList);
            this.formParam = createFormParam(psiMethod, requestList);

            JavaProperty requestBody = requestList.stream().filter(property -> property.hasAnnotation(WebAnnotation.RequestBody)).findFirst().orElse(null);
            this.originRequestBody = requestBody;
            this.requestBody = createBody(requestBody);
            this.requestBodyJson = createBodyJson(requestBody);
        }

        private ApiTableInfo createFormParam(PsiMethod psiMethod, List<JavaProperty> requestList) {
            if (AssertUtils.isEmpty(requestList)) {
                return null;
            }
            List<TableRowInfo> rowList = requestList.stream()
                    .filter(property -> property.hasAnnotation(WebAnnotation.RequestPart))
                    .map(property -> new TableRowInfo(property.getName(), property.getTypeName(), property.isRequired(), property.getComment(), JsonUtil.flattenJsonString(property)))
                    .collect(Collectors.toList());
            return new ApiTableInfo(rowList);
        }

        private ApiTableInfo createRequestParam(PsiMethod psiMethod, List<JavaProperty> requestList) {
            if (AssertUtils.isEmpty(requestList)) {
                return null;
            }
            List<TableRowInfo> rowList = requestList.stream()
                    .filter(property -> property.hasAnnotation(WebAnnotation.RequestParam))
                    .map(property -> new TableRowInfo(property.getName(), property.getTypeName(), property.isRequired(), property.getComment(), property.getExample()))
                    .collect(Collectors.toList());
            return new ApiTableInfo(rowList);
        }

        private ApiTableInfo createPathVariable(PsiMethod psiMethod, List<JavaProperty> requestList) {
            if (AssertUtils.isEmpty(requestList)) {
                return null;
            }
            List<TableRowInfo> rowList = requestList.stream()
                    .filter(property -> property.hasAnnotation(WebAnnotation.PathVariable))
                    .map(property -> new TableRowInfo(property.getName(), property.getTypeName(), property.isRequired(), property.getComment(), property.getExample()))
                    .collect(Collectors.toList());
            return new ApiTableInfo(rowList);
        }

        private ApiTableInfo createRequestHeader(PsiMethod psiMethod, PsiClass psiClass) {
            PsiAnnotation classMappingAnnotation = AnnotationUtil.findXxxMappingAnnotation(psiClass.getAnnotations());
            PsiAnnotation methodMappingAnnotation = AnnotationUtil.findXxxMappingAnnotation(psiMethod.getAnnotations());

            PsiParameterList parameterList = psiMethod.getParameterList();

            List<TableRowInfo> headerList = new ArrayList<>();
            // 解析 Mapping 注解的 consumes、produces 属性
            headerList.addAll(resolveConsumesAndProducesByMappingAnnotation(methodMappingAnnotation));
            headerList.addAll(resolveConsumesAndProducesByMappingAnnotation(classMappingAnnotation));

            // 解析 RequestHeader、RequestPart、RequestBody 注解、MultipartFile
            Map<String, String> paramDescMap = CommentsUtil.extractParamCommentsMap(psiMethod.getDocComment());
            for (PsiParameter parameter : parameterList.getParameters()) {
                String typeName = parameter.getType().getPresentableText();
                String parameterName = parameter.getName();

                // 处理 MultipartFile
                if (typeName.contains("MultipartFile")) {
                    headerList.add(new TableRowInfo("Content-Type", "String", true, "文件", MediaType.MULTIPART_FORM_DATA_VALUE()));
                }

                PsiAnnotation[] parameterAnnotations = parameter.getAnnotations();
                for (PsiAnnotation annotation : parameterAnnotations) {
                    String annotationText = annotation.getText();
                    // 处理 RequestHeader
                    if (annotationText.contains(WebAnnotation.RequestHeader)) {
                        String headerName = parameterName;
                        boolean required = AnnotationUtil.isRequired(annotation);
                        String description = paramDescMap.get(parameterName);

                        PsiNameValuePair[] attributes = annotation.getParameterList().getAttributes();
                        for (PsiNameValuePair attribute : attributes) {
                            if ("name".equals(attribute.getAttributeName()) || "value".equals(attribute.getAttributeName())) {
                                headerName = attribute.getLiteralValue();
                            }
                        }

                        headerList.add(new TableRowInfo(headerName, "String", required, description, "stringValue"));
                    }
                    // 处理 RequestPart
                    else if (annotationText.contains(WebAnnotation.RequestPart)) {
                        headerList.add(new TableRowInfo("Content-Type", "String", true, "表单", MediaType.MULTIPART_FORM_DATA_VALUE()));
                    }
                    // 处理 RequestBody
                    else if (annotationText.contains(WebAnnotation.RequestBody)) {
                        headerList.add(new TableRowInfo("Content-Type", "String", true, "JSON", MediaType.APPLICATION_JSON_VALUE()));
                    }
                }
            }

            // 合并返回
            return new ApiTableInfo(mergeHeader(headerList));
        }

        private List<TableRowInfo> mergeHeader(List<TableRowInfo> headerList) {
            List<TableRowInfo> mergedList = new ArrayList<>();

            Map<String, List<TableRowInfo>> headerNameMap = headerList.stream().collect(Collectors.groupingBy(TableRowInfo::getName));
            for (String key : headerNameMap.keySet()) {
                List<TableRowInfo> headers = headerNameMap.get(key);
                List<String> valueList = headers.stream().map(TableRowInfo::getExample).map(String::valueOf).distinct().collect(Collectors.toList());

                if (valueList.size() == 1) {
                    mergedList.add(new TableRowInfo(key, "String", headers.get(0).isRequired(), headers.get(0).getDescription(), headers.get(0).getExample()));
                } else {
                    mergedList.add(new TableRowInfo(key, "String", headers.get(0).isRequired(), EMPTY, String.join(COMMA_SPACE, valueList)));
                }
            }
            return mergedList;
        }

        public List<TableRowInfo> resolveConsumesAndProducesByMappingAnnotation(PsiAnnotation mappingAnnotation) {
            List<TableRowInfo> headers = new ArrayList<>();
            if (mappingAnnotation != null) {
                PsiNameValuePair[] attributes = mappingAnnotation.getParameterList().getAttributes();
                for (PsiNameValuePair pair : attributes) {
                    if ("consumes".equals(pair.getAttributeName())) {
                        headers.addAll(resolveConsumes(pair));
                    } else if ("produces".equals(pair.getAttributeName())) {
                        headers.addAll(resolveProduces(pair));
                    }
                }
            }
            return headers;
        }

        private List<TableRowInfo> resolveConsumes(PsiNameValuePair pair) {
            if (pair == null || pair.getValue() == null) {
                return new ArrayList<>();
            }
            String text = pair.getValue().getText();
            if (AssertUtils.isBlank(text)) {
                return new ArrayList<>();
            }
            List<TableRowInfo> consumesList = new ArrayList<>();
            text = text.replace(LEFT_BRACE, EMPTY).replace(RIGHT_BRACE, EMPTY).replace(QUOTE, EMPTY);
            if (text.contains(COMMA)) {
                String[] split = text.split(COMMA);
                for (String item : split) {
                    String trimmed = item.trim();
                    TableRowInfo accept = new TableRowInfo("Content-Type", "String", true, EMPTY, MediaType.getValue(trimmed, trimmed));
                    consumesList.add(accept);
                }
            } else {
                TableRowInfo accept = new TableRowInfo("Content-Type", "String", true, EMPTY, MediaType.getValue(text, text));
                consumesList.add(accept);
            }
            return consumesList;
        }

        private List<TableRowInfo> resolveProduces(PsiNameValuePair pair) {
            if (pair == null || pair.getValue() == null) {
                return new ArrayList<>();
            }
            String text = pair.getValue().getText();
            if (AssertUtils.isBlank(text)) {
                return new ArrayList<>();
            }
            List<TableRowInfo> producesList = new ArrayList<>();
            text = text.replace("{\"", EMPTY).replace("\"}", EMPTY).replace(QUOTE, EMPTY);
            if (text.contains(COMMA)) {
                String[] split = text.split(COMMA);
                for (String item : split) {
                    String trimmed = item.trim();
                    TableRowInfo accept = new TableRowInfo("Accept", "String", true, EMPTY, MediaType.getValue(trimmed, trimmed));
                    producesList.add(accept);
                }
            } else {
                TableRowInfo accept = new TableRowInfo("Accept", "String", true, EMPTY, MediaType.getValue(text, text));
                producesList.add(accept);
            }
            return producesList;
        }

        public JavaProperty getOriginRequestBody() {
            return originRequestBody;
        }

        public void setOriginRequestBody(JavaProperty originRequestBody) {
            this.originRequestBody = originRequestBody;
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

        public ApiResponseInfo(PsiMethod psiMethod) {
            PsiType returnType = psiMethod.getReturnType();
            JavaProperty responseBody = JavaProperty.createSimple(returnType, psiMethod.getProject(), null);
            this.responseBody = createBody(responseBody);
            this.responseBodyJson = createBodyJson(responseBody);
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
            this.rowList = rowList != null ? rowList : new ArrayList<>();
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
        private Object example;

        public TableRowInfo(String name, String type, boolean required, String description, Object example) {
            this.name = name;
            this.type = type;
            this.required = required;
            this.description = description;
            this.example = example;
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

        public Object getExample() {
            return example;
        }

        public void setExample(Object example) {
            this.example = example;
        }
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
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
