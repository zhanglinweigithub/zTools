package com.zhanglinwei.zTools.model;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import com.zhanglinwei.zTools.constant.RequestMethodEnum;
import com.zhanglinwei.zTools.constant.RestConstant;
import com.zhanglinwei.zTools.constant.WebAnnotation;
import com.zhanglinwei.zTools.normal.FieldFactory;
import com.zhanglinwei.zTools.util.*;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import static com.zhanglinwei.zTools.util.CommonUtils.NULL;
import static com.zhanglinwei.zTools.util.CommonUtils.STAR;


/**
 * 方法对象
 */
public class MethodInfo implements Serializable {
    private static final long serialVersionUID = 834308673891064746L;

    /**
     * 名称
     */
    private String name;

    /**
     * 注释
     */
    private String desc;

    /**
     * 请求路径
     */
    private String requestPath = "";

    /**
     * 返回类型
     */
    private String requestType;

    /**
     * mapping注解
     */
    private PsiAnnotation xxxRequestMappingAnnotation;

    /**
     * ResponseBody注解
     */
    private PsiAnnotation responseBodyAnnotation;

    /**
     * 请求头参数
     */
    private List<RequestHeader> requestHeaders = new ArrayList<>();

    /**
     * query参数
     */
    private List<FieldInfo> requestParams = new ArrayList<>();

    /**
     * form参数
     */
    private List<FieldInfo> formParams = new ArrayList<>();

    /**
     * 路径参数
     */
    private List<FieldInfo> pathVariables = new ArrayList<>();

    /**
     * 请求体参数
     */
    private FieldInfo requestBody;

    /**
     * 请求体类型 JSON FILE FORM RAW
     */
    private String requestBodyType;

    /**
     * 请求体JSON
     */
    private String requestBodyJson = "";

    /**
     * 响应体
     */
    private FieldInfo responseBody;

    /**
     * 响应类型 JSON RAW
     */
    private String responseBodyType;

    /**
     * 响应JSON
     */
    private String responseBodyJson = "";

    private boolean empty;

    /**
     * 排除的类型
     */
    private final List<String> excludeParamTypes = Arrays.asList("RedirectAttributes", "HttpServletRequest", "HttpServletResponse");

    public MethodInfo(PsiMethod psiMethod) {
        // 忽略不带Mapping注解的方法
        if (!AnnotationUtil.hasMappingAnnotation(psiMethod)) {
            this.empty = true;
            return;
        }

        Project project = psiMethod.getProject();
        PsiClass containingClass = psiMethod.getContainingClass();
        ClassInfo classInfo = new ClassInfo(containingClass, false);
        Map<String, String> paramNameDescMap = getParamDescMap(psiMethod.getDocComment());
        PsiParameterList parameterList = psiMethod.getParameterList();
        List<FieldInfo> requestFieldList = FieldFactory.buildFieldList(parameterList, paramNameDescMap, project);
        this.name = psiMethod.getName();
        String description = DesUtil.getDescription(psiMethod.getDocComment(), psiMethod.getAnnotations());
        this.desc = AssertUtils.isBlank(description) ? this.name : description;

        // 设置query、path、form、requestHeader、请求体参数
        List<FieldInfo> requestParamList = new ArrayList<>();
        List<FieldInfo> pathVariableList = new ArrayList<>();
        List<FieldInfo> formList = new ArrayList<>();
        List<RequestHeader> requestHeaderList = new ArrayList<>();
        for (FieldInfo field : requestFieldList) {
            PsiType psiType = field.getPsiType();
            String psiTypeText = psiType.getPresentableText();
            if (excludeParamTypes.contains(psiTypeText)) {
                continue;
            }
            List<PsiAnnotation> annotationList = field.getAnnotations();
            for (PsiAnnotation annotation : annotationList) {
                String annotationText = annotation.getText();
                if (psiTypeText.contains("MultipartFile") || annotationText.contains(WebAnnotation.RequestPart)) {
                    requestHeaderList.add(RequestHeader.file());
                    formList.addAll(field.cutFirstLayer());
                } else if (annotationText.contains(WebAnnotation.RequestParam)) {
                    requestParamList.addAll(field.cutFirstLayer());
                } else if (annotationText.contains(WebAnnotation.RequestBody)) {
                    this.requestBody = field;
                } else if (annotationText.contains(WebAnnotation.PathVariable)) {
                    pathVariableList.addAll(field.cutFirstLayer());
                } else if (annotationText.contains(WebAnnotation.RequestHeader)) {
                    requestHeaderList.add(AnnotationUtil.buildRequestHeaderByFieldInfo(field));
                }
            }
        }
        this.requestParams = requestParamList;
        this.pathVariables = pathVariableList;
        this.requestHeaders = requestHeaderList;
        this.formParams = formList;

        // 设置方法注解
        PsiAnnotation[] methodAnnotations = psiMethod.getAnnotations();
        for (PsiAnnotation annotation : methodAnnotations) {
            String text = annotation.getText();
            if (text.contains("Mapping")) {
                this.xxxRequestMappingAnnotation = annotation;
            } else if (text.contains(WebAnnotation.ResponseBody)) {
                this.responseBodyAnnotation = annotation;
            }
        }

        // 设置请求类型、路径、请求头
        if (this.xxxRequestMappingAnnotation != null) {
            this.requestHeaders.addAll(AnnotationUtil.buildRequestHeaderByMappingAnnotation(xxxRequestMappingAnnotation, classInfo.getXxxRequestMappingAnnotation()));
            this.requestType = AnnotationUtil.getRequestTypeFromAnnotation(classInfo.getXxxRequestMappingAnnotation(), this.xxxRequestMappingAnnotation);
            this.requestPath = AnnotationUtil.getPathFromAnnotation(this.xxxRequestMappingAnnotation);
        }

        // 设置请求体、请求头信息
        String paramStr = parameterList.getText();
        if (paramStr.contains(WebAnnotation.RequestBody)) {
            this.requestHeaders.add(RequestHeader.json());
            this.requestBodyType = RestConstant.JSON;
            this.requestBodyJson = JsonUtil.buildPrettyJson(this.requestBody);
        }

        List<FieldInfo> noAnnotationField = noAnnotationField(requestFieldList, true);
        if (AssertUtils.isNotBlank(this.requestType) && !this.requestType.contains("、") && AssertUtils.isNotEmpty(noAnnotationField)) {
            if (RequestMethodEnum.GET.name().equals(this.requestType) || RequestMethodEnum.DELETE.name().equals(this.requestType)) {
                // 如果是GET、DELETE请求, 默认所有无注解的参数为query参数(排除文件)
                this.requestParams.addAll(noAnnotationField);
            }
            else if (RequestMethodEnum.POST.name().equals(this.requestType) || RequestMethodEnum.PUT.name().equals(this.requestType)){
                // 如果是POST、PATCH请求, 默认所有无注解的参数为form参数(排除文件)
                this.requestBodyType = RestConstant.FORM;
                this.requestHeaders.add(RequestHeader.form());
                this.formParams.addAll(noAnnotationField);
            }
            // 其它请求类型忽略无注解参数
        }

        // 设置响应信息
        if (hasResponseBodyAnnotation() || classInfo.hasRestController()) {
            this.responseBody = FieldFactory.buildByPsiType(psiMethod.getReturnType(), null, project);
            this.responseBodyJson = JsonUtil.buildPrettyJson(this.responseBody);
            this.responseBodyType = RestConstant.JSON;
        } else {
            this.responseBodyType = RestConstant.RAW;
        }

        // 合并请求头
        mergeRequestHeader(this.requestHeaders);
        if (AssertUtils.isBlank(this.requestType)) {
            this.requestType = RequestMethodEnum.getAll();
        }
    }

    private List<FieldInfo> noAnnotationField(List<FieldInfo> requestFieldList, boolean firstLayer) {
        if (AssertUtils.isEmpty(requestFieldList)) {
            return Collections.emptyList();
        }
        List<FieldInfo> noAnnotationList = requestFieldList.stream().filter(field -> AssertUtils.isEmpty(field.getAnnotations())).collect(Collectors.toList());
        List<FieldInfo> resultList = new ArrayList<>();
        for (FieldInfo fieldInfo : noAnnotationList) {
            if (firstLayer) {
                resultList.addAll(fieldInfo.cutFirstLayer());
            } else {
                resultList.add(fieldInfo);
            }
        }
        return resultList;
    }

    /**
     * 合并请求头
     */
    private void mergeRequestHeader(List<RequestHeader> requestHeaders) {
        List<RequestHeader> mergedList = new ArrayList<>();

        Map<String, List<RequestHeader>> headerNameMap = requestHeaders.stream().collect(Collectors.groupingBy(RequestHeader::getName));
        for (String key : headerNameMap.keySet()) {
            List<RequestHeader> headers = headerNameMap.get(key);
            List<String> valueList = headers.stream().map(item -> item.getValue().trim()).distinct().collect(Collectors.toList());
            mergedList.add(new RequestHeader(key, String.join(", ", valueList), headers.get(0).getRequire(), headers.get(0).getDesc()));
        }

        this.requestHeaders = mergedList;
    }


    private String generateDesc(FieldInfo fieldInfo) {
        if (AssertUtils.isEmpty(fieldInfo.getRange()) || NULL.equals(fieldInfo.getRange())) {
            return fieldInfo.getDesc();
        }
        if (AssertUtils.isEmpty(fieldInfo.getDesc())) {
            return "值域：" + fieldInfo.getRange();
        }
        return fieldInfo.getDesc() + "，值域：" + fieldInfo.getRange();
    }

    /**
     * 获得方法注释中的字段描述
     */
    private Map<String, String> getParamDescMap(PsiDocComment docComment) {
        Map<String, String> paramDescMap = new HashMap<>();
        if (docComment == null) {
            return paramDescMap;
        }
        for (PsiDocTag docTag : docComment.getTags()) {
            String tagValue = docTag.getValueElement() == null ? "" : docTag.getValueElement().getText();
            if ("param".equals(docTag.getName()) && StringUtils.isNotEmpty(tagValue)) {
                paramDescMap.put(tagValue, getParamDesc(docTag.getText()));
            }
        }
        return paramDescMap;
    }

    private String getParamDesc(String tagText) {
        String[] strings = tagText.replace(STAR, "").replaceAll(" {2,}", " ").trim().split(" ");
        if (strings.length >= 3) {
            String desc = strings[2];
            return desc.replace("\n", "");
        }
        return "";
    }

    public boolean isEmpty() {
        return this.empty;
    }

    public boolean hasResponseBodyAnnotation() {
        return this.responseBodyAnnotation != null;
    }

    public String getTitle() {
        return AssertUtils.isNotEmpty(this.desc) ? this.desc : this.name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = AssertUtils.isBlank(desc) ? "暂无" : desc;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public void setRequestPath(String requestPath) {
        this.requestPath = requestPath;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public PsiAnnotation getXxxRequestMappingAnnotation() {
        return xxxRequestMappingAnnotation;
    }

    public void setXxxRequestMappingAnnotation(PsiAnnotation xxxRequestMappingAnnotation) {
        this.xxxRequestMappingAnnotation = xxxRequestMappingAnnotation;
    }

    public PsiAnnotation getResponseBodyAnnotation() {
        return responseBodyAnnotation;
    }

    public void setResponseBodyAnnotation(PsiAnnotation responseBodyAnnotation) {
        this.responseBodyAnnotation = responseBodyAnnotation;
    }

    public List<RequestHeader> getRequestHeaders() {
        return requestHeaders;
    }

    public void setRequestHeaders(List<RequestHeader> requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public List<FieldInfo> getRequestParams() {
        return requestParams;
    }

    public void setRequestParams(List<FieldInfo> requestParams) {
        this.requestParams = requestParams;
    }

    public List<FieldInfo> getFormParams() {
        return formParams;
    }

    public void setFormParams(List<FieldInfo> formParams) {
        this.formParams = formParams;
    }

    public List<FieldInfo> getPathVariables() {
        return pathVariables;
    }

    public void setPathVariables(List<FieldInfo> pathVariables) {
        this.pathVariables = pathVariables;
    }

    public FieldInfo getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(FieldInfo requestBody) {
        this.requestBody = requestBody;
    }

    public String getRequestBodyType() {
        return requestBodyType;
    }

    public void setRequestBodyType(String requestBodyType) {
        this.requestBodyType = requestBodyType;
    }

    public String getRequestBodyJson() {
        return requestBodyJson;
    }

    public void setRequestBodyJson(String requestBodyJson) {
        this.requestBodyJson = requestBodyJson;
    }

    public FieldInfo getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(FieldInfo responseBody) {
        this.responseBody = responseBody;
    }

    public String getResponseBodyType() {
        return responseBodyType;
    }

    public void setResponseBodyType(String responseBodyType) {
        this.responseBodyType = responseBodyType;
    }

    public String getResponseBodyJson() {
        return responseBodyJson;
    }

    public void setResponseBodyJson(String responseBodyJson) {
        this.responseBodyJson = responseBodyJson;
    }

    public List<String> getExcludeParamTypes() {
        return excludeParamTypes;
    }

}
