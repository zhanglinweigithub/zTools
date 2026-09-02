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
import com.zhanglinwei.zTools.common.constant.*;
import com.zhanglinwei.zTools.common.util.RequestPathUtils;
import com.zhanglinwei.zTools.annotation.web.WebAnnotationParser;
import com.zhanglinwei.zTools.annotation.web.WebParameterAnnotation;
import com.zhanglinwei.zTools.common.enums.HttpMethod;
import com.zhanglinwei.zTools.common.enums.SpringConfigProperties;
import com.zhanglinwei.zTools.common.util.JsonUtil;
import com.zhanglinwei.zTools.common.util.NestedUtils;
import com.zhanglinwei.zTools.common.util.ProjectConfigs;
import com.zhanglinwei.zTools.common.util.StringUtils;
import com.zhanglinwei.zTools.common.util.TypeUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.zhanglinwei.zTools.common.constant.StringPool.COMMA_SPACE;
import static com.zhanglinwei.zTools.common.constant.StringPool.EMPTY;

/**
 * 根据类 / 方法定义生成 curl。注解未写的属性在本模块内给可执行的缺省：动词 GET、端口 80、示例走类型默认值。
 * <p>
 * 由 {@link CopyCurlAction} 调用：解析 Mapping 与参数绑定 → 本类拼命令 → 复制到剪贴板。
 * 路径由类 Mapping + 方法 Mapping 拼接，{@code \{id\}} 用 PathVariable 示例值替换。
 * <p>
 * 示例：类 {@code @RequestMapping("/users")}，方法 {@code @PostMapping("/{id}")}，
 * 参数 {@code @PathVariable Long id}、{@code @RequestBody User user}（含 {@code String name}）→
 * <pre>
 * curl --location --request POST 'http://127.0.0.1:8080/users/0' \
 * --header 'Content-Type: application/json' \
 * --data '{
 *   "name": "stringValue"
 * }'
 * </pre>
 */
public final class CurlGenerator {

    private static final String DEFAULT_PORT = "8080";
    private static final String DEFAULT_HTTP_METHOD = HttpMethod.GET.name();

    /** 工具类，禁止实例化。 */
    private CurlGenerator() {}

    /**
     * 生成完整 curl 文本。
     *
     * @param type    方法所在类（取类级 Mapping、全局前缀）
     * @param method  当前 Mapping 方法
     * @param project 当前工程（读 server.port、网关前缀）
     * @return 以 {@code \\} 换行的 curl
     */
    public static String toCurl(ClassDefinition type, MethodDefinition method, Project project) {
        MappingAnnotation classMapping = WebAnnotationParser.mapping(type);
        MappingAnnotation methodMapping = WebAnnotationParser.mapping(method);

        List<String> lines = new ArrayList<String>();
        // 首行：动词 + 填好 PathVariable / Query 的 URL
        lines.add("curl --location --request " + verb(classMapping, methodMapping) + " '" + url(classMapping, methodMapping, method, project) + "'");
        appendContentType(lines, classMapping, methodMapping, method);
        appendAccept(lines, classMapping, methodMapping);
        appendRequestHeaders(lines, method);
        appendPayload(lines, method);
        return joinLines(lines);
    }

    /**
     * 解析 HTTP 动词：方法 Mapping 优先于类 Mapping，都没有则 GET。
     *
     * @param classMapping  类上 Mapping，可为 {@code null}
     * @param methodMapping 方法上 Mapping，可为 {@code null}
     * @return 大写动词，如 {@code POST}
     */
    private static String verb(MappingAnnotation classMapping, MappingAnnotation methodMapping) {
        String methodVerb = first(methodMapping == null ? null : methodMapping.methods());
        if (methodVerb != null) {
            return methodVerb.toUpperCase(Locale.ROOT);
        }
        String classVerb = first(classMapping == null ? null : classMapping.methods());
        return classVerb == null ? DEFAULT_HTTP_METHOD : classVerb.toUpperCase(Locale.ROOT);
    }

    /**
     * 拼完整 URL：{@code http://127.0.0.1:端口} + 全局前缀 + 类/方法路径 + query。
     *
     * @param classMapping  类 Mapping
     * @param methodMapping 方法 Mapping
     * @param method        用于填 PathVariable 和 Query
     * @param project       读端口与全局前缀
     * @return 如 {@code http://127.0.0.1:8080/users/0?active=false}
     */
    private static String url(MappingAnnotation classMapping, MappingAnnotation methodMapping,
                              MethodDefinition method, Project project) {
        // 类路径 + 方法路径，取第一条组合
        List<String> paths = RequestPathUtils.combine(
                classMapping == null ? null : classMapping.paths(),
                methodMapping == null ? null : methodMapping.paths()
        );
        String path = fillPath(paths.get(0), method);
        String port = resolvePort(project);
        return "http://127.0.0.1:" + port
                + RequestPathUtils.join(ProjectConfigs.globalRequestPrefix(project), path)
                + queryString(method);
    }

    /**
     * 把路径里的 {@code \{name\}} 换成对应 PathVariable 的示例值。
     *
     * @param path   组合后的路径，如 {@code /users/{id}}
     * @param method 方法定义
     * @return 如 {@code /users/0}
     */
    private static String fillPath(String path, MethodDefinition method) {
        String filled = path;
        List<ParameterDefinition> parameters = method.parameters();
        for (ParameterDefinition parameter : parameters) {
            if (kind(parameter) != WebParameterAnnotation.Kind.PATH) {
                continue;
            }
            String name = parameterName(parameter);
            if (StringUtils.isBlank(name)) {
                continue;
            }
            filled = filled.replace(StringPool.LEFT_BRACE + name + StringPool.RIGHT_BRACE, scalar(parameter));
        }
        return filled;
    }

    /**
     * 把 QUERY 参数拼成 {@code ?a=1&amp;b=2}；没有则空串。
     *
     * @param method 方法定义
     * @return query 字符串，含前导 {@code ?}；无 QUERY 参数时为空串
     */
    private static String queryString(MethodDefinition method) {
        List<ParameterDefinition> query = new ArrayList<ParameterDefinition>();
        List<ParameterDefinition> parameters = method.parameters();
        for (ParameterDefinition parameter : parameters) {
            if (kind(parameter) == WebParameterAnnotation.Kind.QUERY && !skip(parameter)) {
                query.add(parameter);
            }
        }
        if (query.isEmpty()) {
            return EMPTY;
        }
        StringBuilder builder = new StringBuilder(StringPool.QUESTION_MARK);
        for (int i = 0; i < query.size(); i++) {
            if (i > 0) {
                builder.append(CharacterPool.AMPERSAND);
            }
            ParameterDefinition parameter = query.get(i);
            builder.append(parameterName(parameter)).append(CharacterPool.EQUALS).append(scalar(parameter));
        }
        return builder.toString();
    }

    /**
     * 追加 Content-Type。有表单参数时不写（改由 {@code --form} 表达）；
     * 否则用 consumes，没有 consumes 但有 JSON body 时默认 {@code application/json}。
     *
     * @param lines         curl 行列表
     * @param classMapping  类 Mapping
     * @param methodMapping 方法 Mapping
     * @param method        用于判断表单 / body
     */
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

    /**
     * 按 produces 追加 Accept 头；方法级优先于类级。
     *
     * @param lines         curl 行列表
     * @param classMapping  类 Mapping
     * @param methodMapping 方法 Mapping
     */
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
                accept.append(COMMA_SPACE);
            }
            accept.append(mediaType(produces.get(i)));
        }
        lines.add("--header '" + WebTypes.ACCEPT + ": " + accept + "'");
    }

    /**
     * 把 {@code @RequestHeader} 参数写成 {@code --header 'Name: value'}。
     *
     * @param lines  curl 行列表
     * @param method 方法定义
     */
    private static void appendRequestHeaders(List<String> lines, MethodDefinition method) {
        List<ParameterDefinition> parameters = method.parameters();
        for (ParameterDefinition parameter : parameters) {
            if (kind(parameter) != WebParameterAnnotation.Kind.HEADER || skip(parameter)) {
                continue;
            }
            lines.add("--header '" + parameterName(parameter) + ": " + scalar(parameter) + "'");
        }
    }

    /**
     * 追加请求体：有 multipart / {@code @RequestPart} 时写 {@code --form}；否则 JSON body 写 {@code --data}。
     *
     * @param lines  curl 行列表
     * @param method 方法定义
     */
    private static void appendPayload(List<String> lines, MethodDefinition method) {
        boolean form = hasForm(method);
        List<ParameterDefinition> parameters = method.parameters();
        for (ParameterDefinition parameter : parameters) {
            if (kind(parameter) != WebParameterAnnotation.Kind.PART || skip(parameter)) {
                continue;
            }
            lines.add("--form '" + parameterName(parameter) + "=" + scalar(parameter) + "'");
        }

        // 不按照规范拦截 form / body 完全按照用户定义的接口来生成
//        if (form) {
//            return;
//        }
        ParameterDefinition body = bodyParameter(method);
        if (body == null) {
            return;
        }
        // JSON body：pretty 后包进 --data，单引号转义以便 shell 可执行
        String json = JsonUtil.toJsonString(jsonValue(body), true);
        if (StringUtils.isBlank(json)) {
            return;
        }
        lines.add("--data '" + json.replace("'", "'\\''") + "'");
    }

    /**
     * 判断参数种类：有 Spring 绑定注解则用其 kind；否则 multipart → PART，
     * 带字段的对象不当 QUERY（返回 {@code null}），其余简单类型 → QUERY。
     *
     * @param parameter 方法参数
     * @return 绑定种类；跳过的框架类型或未标注的对象为 {@code null}
     */
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
            // 未写 @RequestBody 的对象不塞进 query
            return null;
        }
        return WebParameterAnnotation.Kind.QUERY;
    }

    /**
     * 找到第一个 {@code @RequestBody} 且未跳过的参数。
     *
     * @param method 方法定义
     * @return body 参数；没有则为 {@code null}
     */
    private static ParameterDefinition bodyParameter(MethodDefinition method) {
        List<ParameterDefinition> parameters = method.parameters();
        for (ParameterDefinition parameter : parameters) {
            if (kind(parameter) == WebParameterAnnotation.Kind.BODY && !skip(parameter)) {
                return parameter;
            }
        }
        return null;
    }

    /**
     * 是否存在未跳过的表单 / multipart 参数。
     *
     * @param method 方法定义
     * @return 有 PART 参数则为 {@code true}
     */
    private static boolean hasForm(MethodDefinition method) {
        List<ParameterDefinition> parameters = method.parameters();
        for (ParameterDefinition parameter : parameters) {
            if (kind(parameter) == WebParameterAnnotation.Kind.PART && !skip(parameter)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否应从 curl 中忽略：{@code null}、Swagger hidden、或 Servlet 等框架类型。
     * multipart 始终保留。
     *
     * @param parameter 方法参数
     * @return 应跳过则为 {@code true}
     */
    private static boolean skip(ParameterDefinition parameter) {
        if (parameter == null || hidden(parameter)) {
            return true;
        }
        if (isMultipart(parameter)) {
            return false;
        }
        return WebTypes.skipParameter(parameter.packageName(), parameter.type());
    }

    /**
     * OpenAPI {@code @Parameter(hidden)} 或 Swagger {@code @ApiParam(hidden)}。
     *
     * @param parameter 方法参数
     * @return 标明隐藏则为 {@code true}
     */
    private static boolean hidden(ParameterDefinition parameter) {
        DocParameterAnnotation oas = SwaggerAnnotationParser.parameter(parameter);
        if (oas != null && Boolean.TRUE.equals(oas.hidden())) {
            return true;
        }
        DocParameterAnnotation apiParam = SwaggerAnnotationParser.apiParam(parameter);
        return apiParam != null && Boolean.TRUE.equals(apiParam.hidden());
    }

    /**
     * 是否已展开出子字段（视为对象，而不是简单 QUERY）。
     *
     * @param parameter 方法参数
     * @return 有 properties 则为 {@code true}
     */
    private static boolean isObject(ParameterDefinition parameter) {
        return parameter.properties() != null && !parameter.properties().isEmpty();
    }

    /**
     * 是否为 {@code MultipartFile} 等上传类型。
     *
     * @param parameter 方法参数
     * @return 是上传类型则为 {@code true}
     */
    private static boolean isMultipart(ParameterDefinition parameter) {
        return TypeUtils.isMultipart(parameter.type());
    }

    /**
     * 解析对外参数名：Spring 绑定 {@code name} → OpenAPI / Swagger {@code name} → 源码名。
     *
     * @param parameter 方法参数
     * @return 非空名称
     */
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

    /**
     * 路径 / Query / Header / 表单用的标量示例：Spring defaultValue → Swagger example → 类型默认值。
     *
     * @param parameter 方法参数
     * @return 可直接写入 URL 或 header 的字符串
     */
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

    /**
     * 把 body 参数转成 JSON 值：优先 Swagger example，否则按字段树或类型默认值。
     *
     * @param parameter body 参数
     * @return Gson 可序列化的对象
     */
    private static Object jsonValue(ParameterDefinition parameter) {
        String example = swaggerExample(parameter.annotations(), parameter);
        if (example != null && (parameter.properties() == null || parameter.properties().isEmpty())) {
            return example;
        }
        if (parameter.properties() == null || parameter.properties().isEmpty()) {
            // 叶子：类型默认值，集合再按嵌套深度包 List
            Object value = typeExample(parameter.type());
            return wrapCollection(parameter.type(), value);
        }
        Map<String, Object> object = jsonObject(parameter.properties());
        return wrapCollection(parameter.type(), object);
    }

    /**
     * 按字段列表组成 JSON 对象。
     *
     * @param properties 对象字段
     * @return 保持插入顺序的 Map
     */
    private static Map<String, Object> jsonObject(List<PropertyDefinition> properties) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        for (PropertyDefinition property : properties) {
            map.put(property.name(), jsonValue(property));
        }
        return map;
    }

    /**
     * 字段转 JSON 值：叶子优先 Schema example，否则类型默认值。
     *
     * @param property 字段
     * @return 标量或嵌套 Map
     */
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

    /**
     * 已是 List 则不再包一层；否则按类型嵌套深度包 {@code List}。
     *
     * @param type  声明类型
     * @param value 已构造的值
     * @return 可能被 List 包裹的值
     */
    private static Object wrapCollection(String type, Object value) {
        if (value instanceof List) {
            return value;
        }
        return NestedUtils.wrapWithNesting(value, TypeUtils.nestDepth(type));
    }

    /**
     * 参数上的 OpenAPI / Swagger example。
     *
     * @param annotations 参数注解列表
     * @param parameter   参数（用于 {@code @ApiParam}）
     * @return 写出的 example；没有则为 {@code null}
     */
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

    /**
     * 字段上的 Schema / ApiModelProperty / Parameter example。
     *
     * @param property 字段
     * @return 写出的 example；没有则为 {@code null}
     */
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

    /**
     * 按 Java 类型取默认示例：{@link NormalType}，boolean 为 {@code false}，未知为空串。
     *
     * @param type 声明类型
     * @return 示例值
     */
    private static Object typeExample(String type) {
        Object example = NormalType.exampleOf(type);
        if (example != null) {
            return example;
        }
        if ("boolean".equalsIgnoreCase(TypeUtils.rawType(type))) {
            return Boolean.FALSE;
        }
        return EMPTY;
    }

    /**
     * 把 consumes / produces 原文转成标准 MediaType。
     *
     * @param raw 注解写出的值，如 {@code APPLICATION_JSON_VALUE} 或 {@code application/json}
     * @return 可写入 Header 的媒体类型
     */
    private static String mediaType(String raw) {
        return MediaType.getValue(raw, raw);
    }

    /**
     * 取列表第一项。
     *
     * @param values 可为 {@code null} 或空
     * @return 第一项；没有则为 {@code null}
     */
    private static String first(List<String> values) {
        return values == null || values.isEmpty() ? null : values.get(0);
    }

    /**
     * 优先列表的第一项，没有则回落到备选列表。
     *
     * @param preferred 优先
     * @param fallback  备选
     * @return 第一个非空项；都没有则为 {@code null}
     */
    private static String firstPresent(List<String> preferred, List<String> fallback) {
        String value = first(preferred);
        return value == null ? first(fallback) : value;
    }

    /**
     * 优先返回非空列表，否则备选列表（可能仍为空）。
     *
     * @param preferred 优先
     * @param fallback  备选
     * @return 选中的列表引用
     */
    private static List<String> firstList(List<String> preferred, List<String> fallback) {
        return preferred != null && !preferred.isEmpty() ? preferred : fallback;
    }

    /**
     * 读 {@code server.port}，未配置则 8080。
     *
     * @param project 当前工程
     * @return 端口字符串
     */
    private static String resolvePort(Project project) {
        String port = ProjectConfigs.spring(project, SpringConfigProperties.SERVER_PROT);
        return StringUtils.isBlank(port) ? DEFAULT_PORT : port;
    }

    /**
     * 用 {@code  \\} 换行拼接 curl 各段。
     *
     * @param lines 已按顺序排好的命令行
     * @return 完整 curl 文本
     */
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
