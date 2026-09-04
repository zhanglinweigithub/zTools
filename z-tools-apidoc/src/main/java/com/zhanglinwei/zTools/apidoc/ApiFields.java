package com.zhanglinwei.zTools.apidoc;

import com.zhanglinwei.zTools.annotation.lookup.AnnotationLookup;
import com.zhanglinwei.zTools.annotation.lookup.Attr;
import com.zhanglinwei.zTools.annotation.model.AnnotationDefinition;
import com.zhanglinwei.zTools.annotation.model.ClassDefinition;
import com.zhanglinwei.zTools.annotation.model.CommentDefinition;
import com.zhanglinwei.zTools.annotation.model.MethodDefinition;
import com.zhanglinwei.zTools.annotation.model.ParameterDefinition;
import com.zhanglinwei.zTools.annotation.model.PropertyDefinition;
import com.zhanglinwei.zTools.annotation.swagger.DocParameterAnnotation;
import com.zhanglinwei.zTools.annotation.swagger.OperationAnnotation;
import com.zhanglinwei.zTools.annotation.swagger.SchemaAnnotation;
import com.zhanglinwei.zTools.annotation.swagger.SwaggerAnnotationParser;
import com.zhanglinwei.zTools.annotation.validation.ValidationAnnotationParser;
import com.zhanglinwei.zTools.annotation.validation.ValidationConstraints;
import com.zhanglinwei.zTools.annotation.web.WebAnnotationParser;
import com.zhanglinwei.zTools.annotation.web.WebParameterAnnotation;
import com.zhanglinwei.zTools.common.constant.NormalType;
import com.zhanglinwei.zTools.common.constant.WebTypes;
import com.zhanglinwei.zTools.common.util.StringUtils;
import com.zhanglinwei.zTools.common.util.TypeUtils;

import java.util.List;

import static com.zhanglinwei.zTools.common.constant.StringPool.EMPTY;

/**
 * 文档侧补全。只读源码写出的注解属性，不补注解 default。
 * <p>
 * 由 {@link ApiInfo} 在组装标题、参数表、示例 JSON 时调用；位于注解模块解析之后、模板写出之前。
 *
 * <p>类标题（先到先得）：{@code @Schema(title)} → {@code @Tag(name)} → {@code @Api(value/tags)}
 * → {@code @ApiModel(value)} → 注释摘要 → 注释说明 → 类名。
 *
 * <p>类描述（先到先得）：{@code @Schema(description)} → {@code @Tag(description)}
 * → {@code @Api(description)} → {@code @ApiModel(description)} → 注释说明 → 注释摘要。
 *
 * <p>方法标题（先到先得）：{@code @Operation(summary)} → {@code @ApiOperation(value)}
 * → 注释摘要 → 注释说明 → 方法名。
 *
 * <p>方法描述（先到先得）：{@code @Operation(description)} → {@code @ApiOperation(notes)}
 * → 注释说明 → 注释摘要。标题不回落到描述。
 *
 * <p>参数 / 字段名称（先到先得）：Spring 绑定的 {@code name}/{@code value}
 *（{@code @RequestParam}、{@code @PathVariable}、{@code @RequestHeader}、
 * {@code @RequestPart} 等）→ 源码名。不读 OpenAPI / Swagger 的 {@code name}。
 *
 * <p>参数 / 字段说明（先到先得）：OpenAPI {@code @Parameter(description)}、
 * {@code @Parameter(schema=@Schema(description/title))}、{@code @Schema(description/title)}
 * → Swagger {@code @ApiParam(value)}、{@code @ApiModelProperty(value)}（{@code notes} 作补充）
 * → 注释。
 *
     * <p>参数 / 字段必填：OpenAPI、Swagger、Spring、校验注解任一标明必填即为必填，不按优先级覆盖。
     * OpenAPI：{@code @Parameter(required=true)}，或 {@code @Schema}/{@code schema} 的
     * {@code requiredMode=REQUIRED}，或 {@code requiredMode} 未写 / {@code AUTO} 时 {@code required=true}。
     * {@code requiredMode=NOT_REQUIRED} 不贡献必填。
     * Swagger：{@code @ApiParam(required=true)}、{@code @ApiModelProperty(required=true)}。
     * Spring：绑定注解写出 {@code required=true}；{@code @PathVariable}、{@code @RequestParam}
     * 未写 {@code required}（且无 {@code defaultValue}）以及未写绑定的隐式 query，按 Spring 缺省视为必填。
     * 写出 {@code required=false} 则为非必填。校验：存在 {@code @NotNull} / {@code @NotBlank} / {@code @NotEmpty}。
 *
     * <p>参数 / 字段示例（先到先得）：OpenAPI {@code example} → Swagger {@code example}
     * → Spring {@code defaultValue}（仅参数）→ 枚举第一个常量 → 类型默认值。
     * 注解示例会按 Java 声明类型把数字字符串转成数值，避免 JSON 里带引号。
 *
 * <p>参数种类：有 Spring 绑定注解则用其 kind；否则 multipart 为 PART，其余非跳过参数为 QUERY。
 */
public final class ApiFields {

    private static final String REQUIRED_MODE = "REQUIRED";
    private static final String NOT_REQUIRED_MODE = "NOT_REQUIRED";

    /** 工具类，禁止实例化。 */
    private ApiFields() {}

    /**
     * 类标题：OpenAPI {@code @Schema(title)} → {@code @Tag} → Swagger {@code @Api}/{@code @ApiModel} → 注释 → 类名。
     *
     * @param type 控制器类定义
     * @return 文档用标题；{@code type} 为 {@code null} 时返回 {@code null}
     */
    public static String titleOf(ClassDefinition type) {
        if (type == null) {
            return null;
        }
        SchemaAnnotation schema = SwaggerAnnotationParser.schema(type);
        SchemaAnnotation apiModel = SwaggerAnnotationParser.apiModel(type);
        return first(
                schema == null ? null : schema.title(),
                SwaggerAnnotationParser.tagName(type),
                swaggerApiValue(type.annotations()),
                apiModel == null ? null : apiModel.title(),
                commentText(type.comment()),
                commentDescription(type.comment()),
                type.name()
        );
    }

    /**
     * 方法标题：{@code @Operation(summary)} → {@code @ApiOperation(value)} → 注释 → 方法名。
     *
     * @param method 接口方法定义
     * @return 文档用标题；{@code method} 为 {@code null} 时返回 {@code null}
     */
    public static String titleOf(MethodDefinition method) {
        if (method == null) {
            return null;
        }
        OperationAnnotation operation = SwaggerAnnotationParser.operation(method);
        OperationAnnotation apiOperation = SwaggerAnnotationParser.apiOperation(method);
        return first(
                operation == null ? null : operation.summary(),
                apiOperation == null ? null : apiOperation.summary(),
                commentText(method.comment()),
                commentDescription(method.comment()),
                method.name()
        );
    }

    /**
     * 类描述：OpenAPI {@code @Schema}/{@code @Tag} → Swagger {@code @Api}/{@code @ApiModel} → 注释。
     *
     * @param type 控制器类定义
     * @return 文档用描述；没有则为 {@code null}
     */
    public static String descriptionOf(ClassDefinition type) {
        if (type == null) {
            return null;
        }
        SchemaAnnotation schema = SwaggerAnnotationParser.schema(type);
        SchemaAnnotation apiModel = SwaggerAnnotationParser.apiModel(type);
        return first(
                schema == null ? null : schema.description(),
                SwaggerAnnotationParser.tagDescription(type),
                swaggerApiDescription(type.annotations()),
                apiModel == null ? null : apiModel.description(),
                commentDescription(type.comment()),
                commentText(type.comment())
        );
    }

    /**
     * 方法描述：{@code @Operation(description)} → {@code @ApiOperation(notes)} → 注释。标题不回落到描述。
     *
     * @param method 接口方法定义
     * @return 文档用描述；没有则为 {@code null}
     */
    public static String descriptionOf(MethodDefinition method) {
        if (method == null) {
            return null;
        }
        OperationAnnotation operation = SwaggerAnnotationParser.operation(method);
        OperationAnnotation apiOperation = SwaggerAnnotationParser.apiOperation(method);
        return first(
                operation == null ? null : operation.description(),
                apiOperation == null ? null : apiOperation.description(),
                commentDescription(method.comment()),
                commentText(method.comment())
        );
    }

    /**
     * 参数种类：Spring 绑定注解优先；否则 multipart → PART，其余非跳过参数 → QUERY。
     *
     * @param parameter 方法参数
     * @return 绑定种类；跳过的框架类型返回 {@code null}
     */
    public static WebParameterAnnotation.Kind kind(ParameterDefinition parameter) {
        WebParameterAnnotation binding = WebAnnotationParser.parameter(parameter);
        if (binding != null) {
            // 写出了 @RequestParam / @PathVariable / @RequestHeader / @RequestBody / @RequestPart
            return binding.kind();
        }
        if (skip(parameter)) {
            return null;
        }
        if (isMultipart(parameter.type())) {
            return WebParameterAnnotation.Kind.PART;
        }
        // 未写绑定注解的简单类型按 Spring 习惯当作 query
        return WebParameterAnnotation.Kind.QUERY;
    }

    /**
     * 是否从文档参数表中忽略：{@code null}、或 Servlet 等框架类型。multipart 始终保留。
     *
     * @param parameter 方法参数
     * @return 应跳过则为 {@code true}
     */
    public static boolean skip(ParameterDefinition parameter) {
        if (parameter == null) {
            return true;
        }
        if (isMultipart(parameter.type())) {
            return false;
        }
        return WebTypes.skipParameter(parameter.packageName(), parameter.type());
    }

    /**
     * 参数名：Spring 绑定 {@code name}/{@code value} → 源码名。不读 OpenAPI / Swagger。
     *
     * @param parameter 方法参数
     * @return 对外名称；{@code parameter} 为 {@code null} 时返回 {@code null}
     */
    public static String name(ParameterDefinition parameter) {
        if (parameter == null) {
            return null;
        }
        WebParameterAnnotation binding = WebAnnotationParser.parameter(parameter);
        return first(
                binding == null ? null : binding.name(),
                parameter.name()
        );
    }

    /**
     * 字段名：Spring 绑定 {@code name}/{@code value} → 源码名。不读 OpenAPI / Swagger。
     *
     * @param property 对象字段
     * @return 对外名称；{@code property} 为 {@code null} 时返回 {@code null}
     */
    public static String name(PropertyDefinition property) {
        if (property == null) {
            return null;
        }
        WebParameterAnnotation binding = WebAnnotationParser.parameter(property.annotations());
        return first(
                binding == null ? null : binding.name(),
                property.name()
        );
    }

    /**
     * 参数说明：OpenAPI {@code @Parameter}/{@code @Schema} → Swagger {@code @ApiParam} → 注释。
     *
     * @param parameter 方法参数
     * @return 说明文本；没有则为 {@code null}
     */
    public static String description(ParameterDefinition parameter) {
        if (parameter == null) {
            return null;
        }
        DocParameterAnnotation oas = SwaggerAnnotationParser.parameter(parameter);
        SchemaAnnotation schema = SwaggerAnnotationParser.schema(parameter.annotations());
        DocParameterAnnotation apiParam = SwaggerAnnotationParser.apiParam(parameter);
        return first(
                oas == null ? null : oas.description(),
                openApiSchemaDescription(nestedSchema(oas)),
                openApiSchemaDescription(schema),
                apiParam == null ? null : apiParam.description(),
                parameter.comment()
        );
    }

    /**
     * 字段说明：OpenAPI {@code @Schema}/{@code @Parameter} → Swagger {@code @ApiModelProperty} → 注释。
     *
     * @param property 对象字段
     * @return 说明文本；没有则为 {@code null}
     */
    public static String description(PropertyDefinition property) {
        if (property == null) {
            return null;
        }
        SchemaAnnotation schema = SwaggerAnnotationParser.schema(property);
        DocParameterAnnotation oas = SwaggerAnnotationParser.parameter(property);
        SchemaAnnotation modelProperty = SwaggerAnnotationParser.apiModelProperty(property);
        return first(
                openApiSchemaDescription(schema),
                oas == null ? null : oas.description(),
                openApiSchemaDescription(nestedSchema(oas)),
                swaggerModelPropertyDescription(modelProperty),
                property.comment()
        );
    }

    /**
     * 参数必填：OpenAPI / Swagger / Spring / 校验任一标明必填即为必填。
     * {@code @PathVariable}、{@code @RequestParam} 未写 {@code required}、隐式 query 按 Spring 缺省为必填。
     *
     * @param parameter 方法参数
     * @return 任一路径标明必填则为 {@code true}
     */
    public static boolean required(ParameterDefinition parameter) {
        if (parameter == null) {
            return false;
        }
        DocParameterAnnotation oas = SwaggerAnnotationParser.parameter(parameter);
        DocParameterAnnotation apiParam = SwaggerAnnotationParser.apiParam(parameter);
        WebParameterAnnotation binding = WebAnnotationParser.parameter(parameter);
        return markedRequired(oas)
                || schemaMarkedRequired(nestedSchema(oas))
                || schemaMarkedRequired(SwaggerAnnotationParser.schema(parameter.annotations()))
                || markedRequired(apiParam)
                || markedRequired(binding)
                || validated(parameter.annotations())
                || springDefaultRequired(parameter, binding);
    }

    /**
     * 字段必填：OpenAPI / Swagger / Spring / 校验任一标明必填即为必填。
     *
     * @param property 对象字段
     * @return 任一路径标明必填则为 {@code true}
     */
    public static boolean required(PropertyDefinition property) {
        if (property == null) {
            return false;
        }
        DocParameterAnnotation oas = SwaggerAnnotationParser.parameter(property);
        SchemaAnnotation modelProperty = SwaggerAnnotationParser.apiModelProperty(property);
        WebParameterAnnotation binding = WebAnnotationParser.parameter(property.annotations());
        return schemaMarkedRequired(SwaggerAnnotationParser.schema(property))
                || markedRequired(oas)
                || schemaMarkedRequired(nestedSchema(oas))
                || schemaMarkedRequired(modelProperty)
                || markedRequired(binding)
                || validated(property.annotations());
    }

    /**
     * 参数示例：OpenAPI {@code example} → Swagger {@code example} → Spring {@code defaultValue} → 类型默认值。
     *
     * @param parameter 方法参数
     * @return 示例值；参数为 {@code null} 时返回空串
     */
    public static Object example(ParameterDefinition parameter) {
        if (parameter == null) {
            return EMPTY;
        }
        DocParameterAnnotation oas = SwaggerAnnotationParser.parameter(parameter);
        SchemaAnnotation schema = SwaggerAnnotationParser.schema(parameter.annotations());
        DocParameterAnnotation apiParam = SwaggerAnnotationParser.apiParam(parameter);
        WebParameterAnnotation binding = WebAnnotationParser.parameter(parameter);
        String annotated = first(
                oas == null ? null : oas.example(),
                nestedSchemaExample(oas),
                schema == null ? null : schema.example(),
                apiParam == null ? null : apiParam.example(),
                binding == null ? null : binding.defaultValue()
        );
        if (annotated != null) {
            return coerceExample(annotated, parameter.type());
        }
        return typeExample(parameter.type());
    }

    /**
     * 字段示例：OpenAPI {@code example} → Swagger {@code example} → 枚举第一个常量 → 类型默认值。
     *
     * @param property 对象字段
     * @return 示例值；字段为 {@code null} 时返回空串
     */
    public static Object example(PropertyDefinition property) {
        if (property == null) {
            return EMPTY;
        }
        SchemaAnnotation schema = SwaggerAnnotationParser.schema(property);
        DocParameterAnnotation oas = SwaggerAnnotationParser.parameter(property);
        SchemaAnnotation modelProperty = SwaggerAnnotationParser.apiModelProperty(property);
        String annotated = first(
                schema == null ? null : schema.example(),
                oas == null ? null : oas.example(),
                nestedSchemaExample(oas),
                modelProperty == null ? null : modelProperty.example()
        );
        if (annotated != null) {
            return coerceExample(annotated, property.type());
        }
        if (!property.enumConstants().isEmpty()) {
            return property.enumConstants().get(0);
        }
        return typeExample(property.type());
    }

    /**
     * 是否为 {@code MultipartFile} 等上传类型。
     *
     * @param type 声明类型
     * @return 是上传类型则为 {@code true}
     */
    public static boolean isMultipart(String type) {
        return TypeUtils.isMultipart(type);
    }

    /**
     * 是否为 Map 类型。
     *
     * @param type 声明类型
     * @return 是 Map 则为 {@code true}
     */
    public static boolean isMap(String type) {
        return TypeUtils.isMap(type);
    }

    /**
     * 是否为 {@link NormalType} 收录的普通类型。
     *
     * @param type 声明类型
     * @return 有类型默认值则为 {@code true}
     */
    public static boolean isNormal(String type) {
        return NormalType.containsKey(TypeUtils.rawType(type));
    }

    /**
     * 返回第一个非空白字符串（trim 后）。
     *
     * @param values 候选，可为 {@code null}
     * @return 第一项非空白值；都没有则为 {@code null}
     */
    private static String first(String... values) {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            if (StringUtils.isNotBlank(value)) {
                return value.trim();
            }
        }
        return null;
    }

    /**
     * 是否存在 {@code @NotNull} / {@code @NotBlank} / {@code @NotEmpty}。
     *
     * @param annotations 注解列表
     * @return 任一校验注解存在则为 {@code true}
     */
    private static boolean validated(List<AnnotationDefinition> annotations) {
        ValidationConstraints constraints = ValidationAnnotationParser.parse(annotations);
        return constraints.notNull() || constraints.notBlank() || constraints.notEmpty();
    }

    /**
     * {@code @Parameter(schema=@Schema(...))} 里的嵌套 Schema。
     *
     * @param oas OpenAPI 参数注解
     * @return 嵌套 schema；没有则为 {@code null}
     */
    private static SchemaAnnotation nestedSchema(DocParameterAnnotation oas) {
        return oas == null ? null : oas.schema();
    }

    /**
     * 嵌套 Schema 上的 example。
     *
     * @param oas OpenAPI 参数注解
     * @return example；没有则为 {@code null}
     */
    private static String nestedSchemaExample(DocParameterAnnotation oas) {
        SchemaAnnotation schema = nestedSchema(oas);
        return schema == null ? null : schema.example();
    }

    /**
     * OpenAPI Schema 的说明：{@code description} 优先于 {@code title}。
     *
     * @param schema Schema 注解
     * @return 说明；没有则为 {@code null}
     */
    private static String openApiSchemaDescription(SchemaAnnotation schema) {
        return schema == null ? null : first(schema.description(), schema.title());
    }

    /**
     * {@code @ApiModelProperty(value)} 才是说明，{@code notes} 是补充。
     *
     * @param modelProperty Swagger 字段注解
     * @return 说明；没有则为 {@code null}
     */
    private static String swaggerModelPropertyDescription(SchemaAnnotation modelProperty) {
        return modelProperty == null ? null : first(modelProperty.title(), modelProperty.description());
    }

    /**
     * OpenAPI / Swagger 参数是否写出 {@code required=true}。
     *
     * @param annotation {@code @Parameter} 或 {@code @ApiParam}
     * @return 标明必填则为 {@code true}
     */
    private static boolean markedRequired(DocParameterAnnotation annotation) {
        return annotation != null && Boolean.TRUE.equals(annotation.required());
    }

    /**
     * Spring 绑定注解是否写出 {@code required=true}。未写不算必填。
     *
     * @param binding 绑定注解
     * @return 标明必填则为 {@code true}
     */
    private static boolean markedRequired(WebParameterAnnotation binding) {
        return binding != null && Boolean.TRUE.equals(binding.required());
    }

    /**
     * Spring 缺省必填：{@code @PathVariable}、{@code @RequestParam} 未写 {@code required=false}
     * 且没有 {@code defaultValue}；未写绑定的隐式 query 同样必填。
     *
     * @param parameter 方法参数
     * @param binding   绑定注解；隐式 query 为 {@code null}
     * @return 按 Spring 缺省必填则为 {@code true}
     */
    private static boolean springDefaultRequired(ParameterDefinition parameter, WebParameterAnnotation binding) {
        WebParameterAnnotation.Kind kind = kind(parameter);
        if (kind == WebParameterAnnotation.Kind.PATH) {
            return binding == null || !Boolean.FALSE.equals(binding.required());
        }
        if (kind != WebParameterAnnotation.Kind.QUERY) {
            return false;
        }
        if (binding == null) {
            return true;
        }
        if (Boolean.FALSE.equals(binding.required())) {
            return false;
        }
        return StringUtils.isBlank(binding.defaultValue());
    }

    /**
     * 把注解写出的示例按 Java 类型转成数值等，无法转换则保留原字符串。
     *
     * @param example 注解示例
     * @param type    声明类型
     * @return 转换后的示例
     */
    private static Object coerceExample(String example, String type) {
        if (example == null) {
            return null;
        }
        Object number = parseNumber(example.trim(), TypeUtils.rawType(type));
        return number != null ? number : example;
    }

    /**
     * 按目标类型解析数字字符串。
     *
     * @param value 示例文本
     * @param raw   简单类型名
     * @return 数值；无法解析或非数字类型为 {@code null}
     */
    private static Object parseNumber(String value, String raw) {
        if (StringUtils.isBlank(value) || StringUtils.isBlank(raw)) {
            return null;
        }
        try {
            if ("int".equals(raw) || "Integer".equals(raw) || "AtomicInteger".equals(raw) || "Number".equals(raw)) {
                return Integer.valueOf(value);
            }
            if ("long".equals(raw) || "Long".equals(raw) || "AtomicLong".equals(raw)) {
                return Long.valueOf(value);
            }
            if ("short".equals(raw) || "Short".equals(raw)) {
                return Short.valueOf(value);
            }
            if ("byte".equals(raw) || "Byte".equals(raw)) {
                return Byte.valueOf(value);
            }
            if ("float".equals(raw) || "Float".equals(raw)) {
                return Float.valueOf(value);
            }
            if ("double".equals(raw) || "Double".equals(raw)) {
                return Double.valueOf(value);
            }
            if ("BigDecimal".equals(raw)) {
                return new java.math.BigDecimal(value);
            }
            if ("BigInteger".equals(raw)) {
                return new java.math.BigInteger(value);
            }
        } catch (NumberFormatException ignored) {
            return null;
        }
        return null;
    }

    /**
     * OpenAPI / Swagger Schema：{@code requiredMode=REQUIRED} 为必填；
     * {@code NOT_REQUIRED} 不贡献必填；未写或 {@code AUTO} 时看 {@code required=true}。
     *
     * @param schema Schema 注解
     * @return 本条 Schema 贡献必填则为 {@code true}
     */
    private static boolean schemaMarkedRequired(SchemaAnnotation schema) {
        if (schema == null) {
            return false;
        }
        if (REQUIRED_MODE.equalsIgnoreCase(schema.requiredMode())) {
            return true;
        }
        if (NOT_REQUIRED_MODE.equalsIgnoreCase(schema.requiredMode())) {
            return false;
        }
        return Boolean.TRUE.equals(schema.required());
    }

    /**
     * 按 Java 类型取默认示例。
     * <pre>
     * MultipartFile          → 文件
     * String                 → stringValue
     * int / byte / short ... → 1
     * Integer / Long ...     → 0
     * boolean / Boolean      → false
     * 未知类型               → 空串
     * </pre>
     *
     * @param type 声明类型
     * @return 示例值
     */
    private static Object typeExample(String type) {
        if (isMultipart(type)) {
            return "文件";
        }
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
     * Swagger {@code @Api(value)} 或 {@code tags}。
     *
     * @param annotations 类注解
     * @return 标题候选；没有则为 {@code null}
     */
    private static String swaggerApiValue(List<AnnotationDefinition> annotations) {
        return AnnotationLookup.string(findApi(annotations), Attr.VALUE, "tags");
    }

    /**
     * Swagger {@code @Api(description)}。
     *
     * @param annotations 类注解
     * @return 描述候选；没有则为 {@code null}
     */
    private static String swaggerApiDescription(List<AnnotationDefinition> annotations) {
        return AnnotationLookup.string(findApi(annotations), Attr.DESCRIPTION);
    }

    /**
     * 查找 {@code @Api} 注解。
     *
     * @param annotations 类注解
     * @return 找到的定义；没有则为 {@code null}
     */
    private static AnnotationDefinition findApi(List<AnnotationDefinition> annotations) {
        return AnnotationLookup.find(annotations, SwaggerAnnotationParser.API).orElse(null);
    }

    /**
     * JavaDoc 摘要（{@code comment.text}）。
     *
     * @param comment 注释定义
     * @return 摘要；没有则为 {@code null}
     */
    private static String commentText(CommentDefinition comment) {
        return comment == null ? null : comment.text();
    }

    /**
     * JavaDoc 说明（{@code comment.description}）。
     *
     * @param comment 注释定义
     * @return 说明；没有则为 {@code null}
     */
    private static String commentDescription(CommentDefinition comment) {
        return comment == null ? null : comment.description();
    }
}
