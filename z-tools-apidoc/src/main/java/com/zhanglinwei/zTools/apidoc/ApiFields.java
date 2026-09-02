package com.zhanglinwei.zTools.apidoc;

import com.zhanglinwei.zTools.annotation.lookup.AnnotationDefinitions;
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
 * Spring：绑定注解写出 {@code required=true}。
 * 校验：存在 {@code @NotNull} / {@code @NotBlank} / {@code @NotEmpty}。
 *
 * <p>参数 / 字段示例（先到先得）：OpenAPI {@code example} → Swagger {@code example}
 * → Spring {@code defaultValue}（仅参数）→ 类型默认值。
 *
 * <p>参数种类：有 Spring 绑定注解则用其 kind；否则 multipart 为 PART，其余非跳过参数为 QUERY。
 */
public final class ApiFields {

    private static final String REQUIRED_MODE = "REQUIRED";
    private static final String NOT_REQUIRED_MODE = "NOT_REQUIRED";

    private ApiFields() {}

    /** 类标题：OpenAPI {@code @Schema(title)} → {@code @Tag} → Swagger {@code @Api}/{@code @ApiModel} → 注释 → 类名。 */
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

    /** 方法标题：{@code @Operation(summary)} → {@code @ApiOperation(value)} → 注释 → 方法名。 */
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

    /** 类描述：OpenAPI {@code @Schema}/{@code @Tag} → Swagger {@code @Api}/{@code @ApiModel} → 注释。 */
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

    /** 方法描述：{@code @Operation(description)} → {@code @ApiOperation(notes)} → 注释。 */
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

    /** 参数种类：Spring 绑定注解优先；否则 multipart → PART，其余 → QUERY。 */
    public static WebParameterAnnotation.Kind kind(ParameterDefinition parameter) {
        WebParameterAnnotation binding = WebAnnotationParser.parameter(parameter);
        if (binding != null) {
            return binding.kind();
        }
        if (skip(parameter)) {
            return null;
        }
        if (isMultipart(parameter.type())) {
            return WebParameterAnnotation.Kind.PART;
        }
        return WebParameterAnnotation.Kind.QUERY;
    }

    public static boolean skip(ParameterDefinition parameter) {
        if (parameter == null) {
            return true;
        }
        if (isMultipart(parameter.type())) {
            return false;
        }
        return WebTypes.skipParameter(parameter.packageName(), parameter.type());
    }

    /** 参数名：Spring 绑定 {@code name}/{@code value} → 源码名。不读 OpenAPI / Swagger。 */
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

    /** 字段名：Spring 绑定 {@code name}/{@code value} → 源码名。不读 OpenAPI / Swagger。 */
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

    /** 参数说明：OpenAPI {@code @Parameter}/{@code @Schema} → Swagger {@code @ApiParam} → 注释。 */
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

    /** 字段说明：OpenAPI {@code @Schema}/{@code @Parameter} → Swagger {@code @ApiModelProperty} → 注释。 */
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
                || validated(parameter.annotations());
    }

    /**
     * 字段必填：OpenAPI / Swagger / Spring / 校验任一标明必填即为必填。
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

    /** 参数示例：OpenAPI {@code example} → Swagger {@code example} → Spring {@code defaultValue} → 类型默认值。 */
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
            return annotated;
        }
        return typeExample(parameter.type());
    }

    /** 字段示例：OpenAPI {@code example} → Swagger {@code example} → 类型默认值。 */
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
            return annotated;
        }
        return typeExample(property.type());
    }

    public static boolean isMultipart(String type) {
        return TypeUtils.isMultipart(type);
    }

    public static boolean isMap(String type) {
        return TypeUtils.isMap(type);
    }

    public static boolean isNormal(String type) {
        return NormalType.containsKey(TypeUtils.rawType(type));
    }

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

    private static boolean validated(List<AnnotationDefinition> annotations) {
        ValidationConstraints constraints = ValidationAnnotationParser.parse(annotations);
        return constraints.notNull() || constraints.notBlank() || constraints.notEmpty();
    }

    private static SchemaAnnotation nestedSchema(DocParameterAnnotation oas) {
        return oas == null ? null : oas.schema();
    }

    private static String nestedSchemaExample(DocParameterAnnotation oas) {
        SchemaAnnotation schema = nestedSchema(oas);
        return schema == null ? null : schema.example();
    }

    private static String openApiSchemaDescription(SchemaAnnotation schema) {
        return schema == null ? null : first(schema.description(), schema.title());
    }

    /** {@code @ApiModelProperty(value)} 才是说明，{@code notes} 是补充。 */
    private static String swaggerModelPropertyDescription(SchemaAnnotation modelProperty) {
        return modelProperty == null ? null : first(modelProperty.title(), modelProperty.description());
    }

    private static boolean markedRequired(DocParameterAnnotation annotation) {
        return annotation != null && Boolean.TRUE.equals(annotation.required());
    }

    private static boolean markedRequired(WebParameterAnnotation binding) {
        return binding != null && Boolean.TRUE.equals(binding.required());
    }

    /**
     * OpenAPI / Swagger Schema：{@code requiredMode=REQUIRED} 为必填；
     * {@code NOT_REQUIRED} 不贡献必填；未写或 {@code AUTO} 时看 {@code required=true}。
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

    private static Object typeExample(String type) {
        if (isMultipart(type)) {
            return "文件";
        }
        Object example = NormalType.get(TypeUtils.rawType(type));
        if (example != null) {
            return example;
        }
        if ("boolean".equalsIgnoreCase(TypeUtils.rawType(type))) {
            return Boolean.FALSE;
        }
        return EMPTY;
    }

    private static String swaggerApiValue(List<AnnotationDefinition> annotations) {
        return AnnotationDefinitions.string(findApi(annotations), Attr.VALUE, "tags");
    }

    private static String swaggerApiDescription(List<AnnotationDefinition> annotations) {
        return AnnotationDefinitions.string(findApi(annotations), Attr.DESCRIPTION);
    }

    private static AnnotationDefinition findApi(List<AnnotationDefinition> annotations) {
        return AnnotationDefinitions.find(annotations, SwaggerAnnotationParser.API).orElse(null);
    }

    private static String commentText(CommentDefinition comment) {
        return comment == null ? null : comment.text();
    }

    private static String commentDescription(CommentDefinition comment) {
        return comment == null ? null : comment.description();
    }
}
