package com.zhanglinwei.zTools.yapi;

import com.intellij.openapi.project.Project;
import com.zhanglinwei.zTools.annotation.feign.FeignAnnotationParser;
import com.zhanglinwei.zTools.annotation.feign.RequestLineAnnotation;
import com.zhanglinwei.zTools.annotation.model.ClassDefinition;
import com.zhanglinwei.zTools.annotation.model.MethodDefinition;
import com.zhanglinwei.zTools.annotation.model.ParameterDefinition;
import com.zhanglinwei.zTools.annotation.web.MappingAnnotation;
import com.zhanglinwei.zTools.annotation.web.WebAnnotationParser;
import com.zhanglinwei.zTools.annotation.web.WebParameterAnnotation;
import com.zhanglinwei.zTools.common.constant.MediaType;
import com.zhanglinwei.zTools.common.constant.WebTypes;
import com.zhanglinwei.zTools.common.enums.Boolean;
import com.zhanglinwei.zTools.common.enums.HttpMethod;
import com.zhanglinwei.zTools.common.util.CollectionUtils;
import com.zhanglinwei.zTools.common.util.ProjectConfigs;
import com.zhanglinwei.zTools.common.util.RequestPathUtils;
import com.zhanglinwei.zTools.common.util.StringUtils;
import com.zhanglinwei.zTools.common.util.TypeUtils;
import com.zhanglinwei.zTools.yapi.enums.ParameterType;
import com.zhanglinwei.zTools.yapi.enums.ReqBodyType;
import com.zhanglinwei.zTools.yapi.model.YApiFormParam;
import com.zhanglinwei.zTools.yapi.model.YApiHeader;
import com.zhanglinwei.zTools.yapi.model.YApiInterfaceAddRequest;
import com.zhanglinwei.zTools.yapi.model.YApiPathParam;
import com.zhanglinwei.zTools.yapi.model.YApiQueryParam;
import com.zhanglinwei.zTools.yapi.utils.YApiFields;
import com.zhanglinwei.zTools.yapi.utils.YApiJson;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.zhanglinwei.zTools.common.constant.StringPool.COMMA_SPACE;
import static com.zhanglinwei.zTools.common.constant.StringPool.EMPTY;

/**
 * 把 annotation 解析出的类/方法定义组装成 YApi 保存请求。
 * 只依赖 common、annotation，不读其它功能模块。
 */
public final class YApiInterfaceBuilder {

    private YApiInterfaceBuilder() {}

    /**
     * 是否可上传：Spring Mapping 或 Feign {@code @RequestLine}。
     *
     * @param method 方法定义
     * @return 是 Web/Feign 接口方法则为 {@code true}
     */
    public static boolean isUploadable(MethodDefinition method) {
        return WebAnnotationParser.isHandlerMethod(method)
                || FeignAnnotationParser.requestLine(method) != null;
    }

    /**
     * 组装一条 YApi 保存请求。
     *
     * @param project          当前工程（读全局前缀）
     * @param classDefinition  所在类
     * @param methodDefinition 接口方法
     * @param projectId        YApi 项目 ID
     * @param catId            分类 ID
     * @return 保存请求
     */
    public static YApiInterfaceAddRequest build(Project project, ClassDefinition classDefinition,
                                                MethodDefinition methodDefinition, Number projectId, Number catId) {
        MappingAnnotation classMapping = WebAnnotationParser.mapping(classDefinition);
        MappingAnnotation methodMapping = WebAnnotationParser.mapping(methodDefinition);
        RequestLineAnnotation requestLine = FeignAnnotationParser.requestLine(methodDefinition);

        YApiInterfaceAddRequest request = new YApiInterfaceAddRequest();
        request.setProject_id(projectId);
        request.setCatid(catId);
        request.setTitle(YApiFields.titleOf(methodDefinition));
        request.setDesc(YApiFields.descriptionOf(methodDefinition));
        request.setStatus("undone");
        request.setMethod(resolveHttpMethod(methodMapping, requestLine));
        request.setPath(RequestPathUtils.join(
                ProjectConfigs.globalRequestPrefix(project),
                classMapping == null ? null : classMapping.firstPath(),
                methodPath(methodMapping, requestLine)
        ));

        List<YApiQueryParam> queryList = new ArrayList<YApiQueryParam>();
        List<YApiPathParam> pathList = new ArrayList<YApiPathParam>();
        List<YApiHeader> headerList = new ArrayList<YApiHeader>();
        List<YApiFormParam> formList = new ArrayList<YApiFormParam>();
        ParameterDefinition bodyParam = null;

        for (ParameterDefinition parameter : methodDefinition.parameters()) {
            WebParameterAnnotation.Kind kind = YApiFields.kind(parameter);
            if (kind == null) {
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
                    break;
                default:
                    break;
            }
        }

        addConsumesProduces(classMapping, headerList);
        addConsumesProduces(methodMapping, headerList);
        // json body 与 form 互斥：有 body 只出 JSON Content-Type，避免拼出非法组合头
        if (bodyParam != null) {
            headerList.add(contentTypeHeader(MediaType.APPLICATION_JSON_VALUE()));
            request.setReq_body_type(ReqBodyType.JSON.getCode());
            request.setReq_body_is_json_schema(false);
            request.setReq_body_other(YApiJson.prettyWithComments(bodyParam));
        } else if (!formList.isEmpty()) {
            headerList.add(contentTypeHeader(MediaType.MULTIPART_FORM_DATA_VALUE()));
            request.setReq_body_type(ReqBodyType.FORM.getCode());
            request.setReq_body_form(formList);
        }
        headerList = mergeHeaders(headerList);

        request.setReq_headers(headerList.isEmpty() ? null : headerList);
        request.setReq_query(queryList.isEmpty() ? null : queryList);
        request.setReq_params(pathList.isEmpty() ? null : pathList);

        ParameterDefinition returns = methodDefinition.returns();
        if (returns != null) {
            request.setRes_body_type(ReqBodyType.JSON.getCode());
            request.setRes_body_is_json_schema(false);
            request.setRes_body(YApiJson.prettyWithComments(returns));
        }
        return request;
    }

    private static String methodPath(MappingAnnotation methodMapping, RequestLineAnnotation requestLine) {
        if (methodMapping != null && StringUtils.isNotBlank(methodMapping.firstPath())) {
            return methodMapping.firstPath();
        }
        return requestLine == null ? null : requestLine.path();
    }

    private static String resolveHttpMethod(MappingAnnotation mapping, RequestLineAnnotation requestLine) {
        if (mapping != null && CollectionUtils.isNotEmpty(mapping.methods())) {
            return mapping.methods().get(0).toUpperCase();
        }
        if (requestLine != null && StringUtils.isNotBlank(requestLine.httpMethod())) {
            return requestLine.httpMethod().toUpperCase();
        }
        return HttpMethod.GET.name();
    }

    private static String requiredFlag(boolean required) {
        return String.valueOf(required ? Boolean.TRUE.getNumberValue() : Boolean.FALSE.getNumberValue());
    }

    private static String exampleText(Object example) {
        return example == null ? EMPTY : String.valueOf(example);
    }

    private static YApiQueryParam toQueryParam(ParameterDefinition parameter) {
        YApiQueryParam query = new YApiQueryParam();
        query.setName(YApiFields.name(parameter));
        query.setRequired(requiredFlag(YApiFields.required(parameter)));
        query.setDesc(YApiFields.description(parameter));
        query.setExample(exampleText(YApiFields.example(parameter)));
        return query;
    }

    private static YApiPathParam toPathParam(ParameterDefinition parameter) {
        YApiPathParam path = new YApiPathParam();
        path.setName(YApiFields.name(parameter));
        path.setDesc(YApiFields.description(parameter));
        path.setExample(exampleText(YApiFields.example(parameter)));
        return path;
    }

    private static YApiHeader toHeader(ParameterDefinition parameter) {
        YApiHeader header = new YApiHeader();
        header.setName(YApiFields.name(parameter));
        header.setRequired(requiredFlag(YApiFields.required(parameter)));
        header.setDesc(YApiFields.description(parameter));
        header.setExample(exampleText(YApiFields.example(parameter)));
        header.setValue(EMPTY);
        return header;
    }

    private static YApiFormParam toFormParam(ParameterDefinition parameter) {
        boolean file = TypeUtils.isMultipart(parameter.type())
                || TypeUtils.isStream(parameter.packageName(), parameter.type());
        YApiFormParam form = new YApiFormParam();
        form.setName(YApiFields.name(parameter));
        form.setType(file ? ParameterType.FILE.getCode() : ParameterType.TEXT.getCode());
        form.setRequired(requiredFlag(YApiFields.required(parameter)));
        form.setDesc(YApiFields.description(parameter));
        form.setExample(file ? EMPTY : exampleText(YApiFields.example(parameter)));
        return form;
    }

    private static void addConsumesProduces(MappingAnnotation mapping, List<YApiHeader> headerList) {
        if (mapping == null) {
            return;
        }
        addMediaHeaders(mapping.consumes(), WebTypes.CONTENT_TYPE, headerList);
        addMediaHeaders(mapping.produces(), WebTypes.ACCEPT, headerList);
    }

    private static void addMediaHeaders(List<String> items, String headerName, List<YApiHeader> headerList) {
        if (items == null) {
            return;
        }
        for (String item : items) {
            String value = MediaType.getValue(item, item);
            YApiHeader header = new YApiHeader();
            header.setName(headerName);
            header.setRequired(requiredFlag(true));
            header.setValue(value);
            header.setExample(value);
            headerList.add(header);
        }
    }

    private static YApiHeader contentTypeHeader(String value) {
        YApiHeader header = new YApiHeader();
        header.setName(WebTypes.CONTENT_TYPE);
        header.setRequired(requiredFlag(true));
        header.setValue(value);
        header.setExample(value);
        header.setDesc(value.contains("json") ? "JSON" : "表单");
        return header;
    }

    private static List<YApiHeader> mergeHeaders(List<YApiHeader> headerList) {
        if (headerList.isEmpty()) {
            return headerList;
        }
        Map<String, List<YApiHeader>> grouped = new LinkedHashMap<String, List<YApiHeader>>();
        for (YApiHeader header : headerList) {
            List<YApiHeader> sameName = grouped.get(header.getName());
            if (sameName == null) {
                sameName = new ArrayList<YApiHeader>();
                grouped.put(header.getName(), sameName);
            }
            sameName.add(header);
        }
        List<YApiHeader> merged = new ArrayList<YApiHeader>();
        for (Map.Entry<String, List<YApiHeader>> entry : grouped.entrySet()) {
            List<YApiHeader> headers = entry.getValue();
            if (headers.size() == 1) {
                merged.add(headers.get(0));
                continue;
            }
            Set<String> values = new LinkedHashSet<String>();
            for (YApiHeader header : headers) {
                values.add(header.getExample() == null ? EMPTY : header.getExample());
            }
            YApiHeader first = headers.get(0);
            if (values.size() > 1) {
                String joined = String.join(COMMA_SPACE, values);
                first.setExample(joined);
                first.setValue(joined);
                first.setDesc(EMPTY);
            }
            merged.add(first);
        }
        return merged;
    }
}
