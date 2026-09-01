package com.zhanglinwei.zTools.copycurl;

import com.intellij.openapi.project.Project;
import com.zhanglinwei.zTools.annotation.model.AnnotationDefinition;
import com.zhanglinwei.zTools.annotation.model.ClassDefinition;
import com.zhanglinwei.zTools.annotation.model.MethodDefinition;
import com.zhanglinwei.zTools.annotation.model.ParameterDefinition;
import com.zhanglinwei.zTools.annotation.model.PropertyDefinition;
import com.zhanglinwei.zTools.annotation.swagger.DocParameterAnnotation;
import com.zhanglinwei.zTools.annotation.swagger.SchemaAnnotation;
import com.zhanglinwei.zTools.annotation.swagger.SwaggerAnnotationParser;
import com.zhanglinwei.zTools.annotation.web.MappingAnnotation;
import com.zhanglinwei.zTools.annotation.web.RequestPaths;
import com.zhanglinwei.zTools.annotation.web.WebAnnotationParser;
import com.zhanglinwei.zTools.annotation.web.WebParameterAnnotation;
import com.zhanglinwei.zTools.constant.MediaType;
import com.zhanglinwei.zTools.constant.NormalType;
import com.zhanglinwei.zTools.constant.WebTypes;
import com.zhanglinwei.zTools.enums.HttpMethod;
import com.zhanglinwei.zTools.enums.SpringConfigProperties;
import com.zhanglinwei.zTools.util.JsonUtil;
import com.zhanglinwei.zTools.util.NestedUtils;
import com.zhanglinwei.zTools.util.ProjectConfigs;
import com.zhanglinwei.zTools.util.StringUtils;
import com.zhanglinwei.zTools.util.TypeUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 根据类 / 方法定义生成 curl。注解未写的属性在本模块内给可执行的缺省：动词 GET、端口 80、示例走类型默认值。
 */
public final class CurlGenerator {

    private static final String DEFAULT_PORT = "8080";
    private static final String DEFAULT_HTTP_METHOD = HttpMethod.GET.name();

    private CurlGenerator() {}

    public static String toCurl(ClassDefinition type, MethodDefinition method, Project project) {
        MappingAnnotation classMapping = WebAnnotationParser.mapping(type);
        MappingAnnotation methodMapping = WebAnnotationParser.mapping(method);

        List<String> lines = new ArrayList<String>();
        lines.add("curl --location --request " + verb(classMapping, methodMapping) + " '" + url(classMapping, methodMapping, method, project) + "'");
        appendContentType(lines, classMapping, methodMapping, method);
        appendAccept(lines, classMapping, methodMapping);
        appendRequestHeaders(lines, method);
        appendPayload(lines, method);
        return joinLines(lines);
    }

    private static String verb(MappingAnnotation classMapping, MappingAnnotation methodMapping) {
        String methodVerb = first(methodMapping == null ? null : methodMapping.methods());
        if (methodVerb != null) {
            return methodVerb.toUpperCase(Locale.ROOT);
        }
        String classVerb = first(classMapping == null ? null : classMapping.methods());
        return classVerb == null ? DEFAULT_HTTP_METHOD : classVerb.toUpperCase(Locale.ROOT);
    }

    private static String url(MappingAnnotation classMapping, MappingAnnotation methodMapping,
                              MethodDefinition method, Project project) {
        List<String> paths = RequestPaths.combine(
                classMapping == null ? null : classMapping.paths(),
                methodMapping == null ? null : methodMapping.paths()
        );
        String path = fillPath(paths.get(0), method);
        String port = resolvePort(project);
        return "http://127.0.0.1:" + port
                + RequestPaths.join(ProjectConfigs.globalRequestPrefix(project), path)
                + queryString(method);
    }

    private static String fillPath(String path, MethodDefinition method) {
        String filled = path;
        List<ParameterDefinition> parameters = method.parameters();
        for (int i = 0; i < parameters.size(); i++) {
            ParameterDefinition parameter = parameters.get(i);
            if (kind(parameter) != WebParameterAnnotation.Kind.PATH) {
                continue;
            }
            String name = parameterName(parameter);
            if (StringUtils.isBlank(name)) {
                continue;
            }
            filled = filled.replace("{" + name + "}", scalar(parameter));
        }
        return filled;
    }

    private static String queryString(MethodDefinition method) {
        List<ParameterDefinition> query = new ArrayList<ParameterDefinition>();
        List<ParameterDefinition> parameters = method.parameters();
        for (int i = 0; i < parameters.size(); i++) {
            ParameterDefinition parameter = parameters.get(i);
            if (kind(parameter) == WebParameterAnnotation.Kind.QUERY && !skip(parameter)) {
                query.add(parameter);
            }
        }
        if (query.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder("?");
        for (int i = 0; i < query.size(); i++) {
            if (i > 0) {
                builder.append('&');
            }
            ParameterDefinition parameter = query.get(i);
            builder.append(parameterName(parameter)).append('=').append(scalar(parameter));
        }
        return builder.toString();
    }

    private static void appendContentType(List<String> lines, MappingAnnotation classMapping,
                                          MappingAnnotation methodMapping, MethodDefinition method) {
        if (hasForm(method)) {
            return;
        }
        String consumes = firstPresent(
                methodMapping == null ? null : methodMapping.consumes(),
                classMapping == null ? null : classMapping.consumes()
        );
        if (consumes != null) {
            lines.add("--header '" + WebTypes.CONTENT_TYPE + ": " + mediaType(consumes) + "'");
            return;
        }
        if (bodyParameter(method) != null) {
            lines.add("--header '" + WebTypes.CONTENT_TYPE + ": " + MediaType.APPLICATION_JSON_VALUE() + "'");
        }
    }

    private static void appendAccept(List<String> lines, MappingAnnotation classMapping, MappingAnnotation methodMapping) {
        List<String> produces = firstList(
                methodMapping == null ? null : methodMapping.produces(),
                classMapping == null ? null : classMapping.produces()
        );
        if (produces == null || produces.isEmpty()) {
            return;
        }
        StringBuilder accept = new StringBuilder();
        for (int i = 0; i < produces.size(); i++) {
            if (i > 0) {
                accept.append(", ");
            }
            accept.append(mediaType(produces.get(i)));
        }
        lines.add("--header '" + WebTypes.ACCEPT + ": " + accept + "'");
    }

    private static void appendRequestHeaders(List<String> lines, MethodDefinition method) {
        List<ParameterDefinition> parameters = method.parameters();
        for (int i = 0; i < parameters.size(); i++) {
            ParameterDefinition parameter = parameters.get(i);
            if (kind(parameter) != WebParameterAnnotation.Kind.HEADER || skip(parameter)) {
                continue;
            }
            lines.add("--header '" + parameterName(parameter) + ": " + scalar(parameter) + "'");
        }
    }

    private static void appendPayload(List<String> lines, MethodDefinition method) {
        boolean form = hasForm(method);
        List<ParameterDefinition> parameters = method.parameters();
        for (int i = 0; i < parameters.size(); i++) {
            ParameterDefinition parameter = parameters.get(i);
            if (kind(parameter) != WebParameterAnnotation.Kind.PART || skip(parameter)) {
                continue;
            }
            lines.add("--form '" + parameterName(parameter) + "=" + scalar(parameter) + "'");
        }
        if (form) {
            return;
        }
        ParameterDefinition body = bodyParameter(method);
        if (body == null) {
            return;
        }
        String json = JsonUtil.toJsonString(jsonValue(body), true);
        if (StringUtils.isBlank(json)) {
            return;
        }
        lines.add("--data '" + json.replace("'", "'\\''") + "'");
    }

    private static WebParameterAnnotation.Kind kind(ParameterDefinition parameter) {
        WebParameterAnnotation binding = WebAnnotationParser.parameter(parameter);
        if (binding != null) {
            return binding.kind();
        }
        if (skip(parameter)) {
            return null;
        }
        if (isMultipart(parameter)) {
            return WebParameterAnnotation.Kind.PART;
        }
        if (isObject(parameter)) {
            return null;
        }
        return WebParameterAnnotation.Kind.QUERY;
    }

    private static ParameterDefinition bodyParameter(MethodDefinition method) {
        List<ParameterDefinition> parameters = method.parameters();
        for (int i = 0; i < parameters.size(); i++) {
            ParameterDefinition parameter = parameters.get(i);
            if (kind(parameter) == WebParameterAnnotation.Kind.BODY && !skip(parameter)) {
                return parameter;
            }
        }
        return null;
    }

    private static boolean hasForm(MethodDefinition method) {
        List<ParameterDefinition> parameters = method.parameters();
        for (int i = 0; i < parameters.size(); i++) {
            if (kind(parameters.get(i)) == WebParameterAnnotation.Kind.PART && !skip(parameters.get(i))) {
                return true;
            }
        }
        return false;
    }

    private static boolean skip(ParameterDefinition parameter) {
        if (parameter == null || hidden(parameter)) {
            return true;
        }
        if (isMultipart(parameter)) {
            return false;
        }
        return WebTypes.skipParameter(parameter.packageName(), parameter.type());
    }

    private static boolean hidden(ParameterDefinition parameter) {
        DocParameterAnnotation oas = SwaggerAnnotationParser.parameter(parameter);
        if (oas != null && Boolean.TRUE.equals(oas.hidden())) {
            return true;
        }
        DocParameterAnnotation apiParam = SwaggerAnnotationParser.apiParam(parameter);
        return apiParam != null && Boolean.TRUE.equals(apiParam.hidden());
    }

    private static boolean isObject(ParameterDefinition parameter) {
        return parameter.properties() != null && !parameter.properties().isEmpty();
    }

    private static boolean isMultipart(ParameterDefinition parameter) {
        return TypeUtils.isMultipart(parameter.type());
    }

    private static String parameterName(ParameterDefinition parameter) {
        WebParameterAnnotation binding = WebAnnotationParser.parameter(parameter);
        if (binding != null && StringUtils.isNotBlank(binding.name())) {
            return binding.name();
        }
        DocParameterAnnotation oas = SwaggerAnnotationParser.parameter(parameter);
        if (oas != null && StringUtils.isNotBlank(oas.name())) {
            return oas.name();
        }
        DocParameterAnnotation apiParam = SwaggerAnnotationParser.apiParam(parameter);
        if (apiParam != null && StringUtils.isNotBlank(apiParam.name())) {
            return apiParam.name();
        }
        return parameter.name();
    }

    private static String scalar(ParameterDefinition parameter) {
        WebParameterAnnotation binding = WebAnnotationParser.parameter(parameter);
        if (binding != null && StringUtils.isNotBlank(binding.defaultValue())) {
            return binding.defaultValue();
        }
        String example = swaggerExample(parameter.annotations(), parameter);
        if (example != null) {
            return example;
        }
        return String.valueOf(typeExample(parameter.type()));
    }

    private static Object jsonValue(ParameterDefinition parameter) {
        String example = swaggerExample(parameter.annotations(), parameter);
        if (example != null && (parameter.properties() == null || parameter.properties().isEmpty())) {
            return example;
        }
        if (parameter.properties() == null || parameter.properties().isEmpty()) {
            Object value = typeExample(parameter.type());
            return wrapCollection(parameter.type(), value);
        }
        Map<String, Object> object = jsonObject(parameter.properties());
        return wrapCollection(parameter.type(), object);
    }

    private static Map<String, Object> jsonObject(List<PropertyDefinition> properties) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        for (int i = 0; i < properties.size(); i++) {
            PropertyDefinition property = properties.get(i);
            map.put(property.name(), jsonValue(property));
        }
        return map;
    }

    private static Object jsonValue(PropertyDefinition property) {
        String example = propertyExample(property);
        if (example != null && (property.properties() == null || property.properties().isEmpty())) {
            return example;
        }
        if (property.properties() == null || property.properties().isEmpty()) {
            return wrapCollection(property.type(), typeExample(property.type()));
        }
        return wrapCollection(property.type(), jsonObject(property.properties()));
    }

    private static Object wrapCollection(String type, Object value) {
        if (value instanceof List) {
            return value;
        }
        return NestedUtils.wrapWithNesting(value, TypeUtils.nestDepth(type));
    }

    private static String swaggerExample(List<AnnotationDefinition> annotations, ParameterDefinition parameter) {
        DocParameterAnnotation oas = SwaggerAnnotationParser.parameter(annotations);
        if (oas != null && StringUtils.isNotBlank(oas.example())) {
            return oas.example();
        }
        if (oas != null && oas.schema() != null && StringUtils.isNotBlank(oas.schema().example())) {
            return oas.schema().example();
        }
        DocParameterAnnotation apiParam = SwaggerAnnotationParser.apiParam(parameter);
        if (apiParam != null && StringUtils.isNotBlank(apiParam.example())) {
            return apiParam.example();
        }
        SchemaAnnotation schema = SwaggerAnnotationParser.schema(annotations);
        return schema == null ? null : schema.example();
    }

    private static String propertyExample(PropertyDefinition property) {
        SchemaAnnotation schema = SwaggerAnnotationParser.schema(property);
        if (schema != null && StringUtils.isNotBlank(schema.example())) {
            return schema.example();
        }
        SchemaAnnotation modelProperty = SwaggerAnnotationParser.apiModelProperty(property);
        if (modelProperty != null && StringUtils.isNotBlank(modelProperty.example())) {
            return modelProperty.example();
        }
        DocParameterAnnotation oas = SwaggerAnnotationParser.parameter(property);
        return oas == null ? null : oas.example();
    }

    private static Object typeExample(String type) {
        String raw = TypeUtils.rawType(type);
        Object example = NormalType.get(raw);
        if (example != null) {
            return example;
        }
        if ("boolean".equalsIgnoreCase(raw)) {
            return Boolean.FALSE;
        }
        return "";
    }

    private static String mediaType(String raw) {
        return MediaType.getValue(raw, raw);
    }

    private static String first(List<String> values) {
        return values == null || values.isEmpty() ? null : values.get(0);
    }

    private static String firstPresent(List<String> preferred, List<String> fallback) {
        String value = first(preferred);
        return value == null ? first(fallback) : value;
    }

    private static List<String> firstList(List<String> preferred, List<String> fallback) {
        return preferred != null && !preferred.isEmpty() ? preferred : fallback;
    }

    private static String resolvePort(Project project) {
        String port = ProjectConfigs.spring(project, SpringConfigProperties.SERVER_PROT);
        return StringUtils.isBlank(port) ? DEFAULT_PORT : port;
    }

    private static String joinLines(List<String> lines) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < lines.size(); i++) {
            if (i > 0) {
                builder.append(" \\\n");
            }
            builder.append(lines.get(i));
        }
        return builder.toString();
    }
}
