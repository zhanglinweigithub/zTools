package com.zhanglinwei.zTools.yapi.utils;

import com.zhanglinwei.zTools.annotation.model.*;
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
 * YApi 侧字段解析工具。基于 annotation 模块的解析结果，
 * 统一解析参数/字段的名称、说明、必填、示例、类型。
 *
 * <p>解析优先级与 apidoc 模块的 ApiFields 一致：
 * OpenAPI → Swagger → Spring → 校验 → 注释 → 默认值。
 *
 * <p>类型如何变成 YApi 字段：
 * <pre>
 * {@code @RequestParam Long id}           → Query  name=id, example=0
 * {@code @PathVariable String userId}     → Path   name=userId, example=""
 * {@code @RequestHeader String token}     → Header name=token
 * {@code @RequestBody User user}          → JSON Body（再交给 {@link YApiJson}）
 * {@code @RequestPart MultipartFile file} → Form   type=file
 * {@code @NotNull String name}            → required=true
 * </pre>
 */
public final class YApiFields {

    private static final String REQUIRED_MODE = "REQUIRED";
    private static final String NOT_REQUIRED_MODE = "NOT_REQUIRED";

    /** 工具类，禁止实例化。 */
    private YApiFields() {}

    /**
     * 方法标题：@Operation(summary) → @ApiOperation(value) → 注释 → 方法名
     *
     * @param method 方法定义
     * @return 接口标题；方法为 {@code null} 时返回 {@code null}
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
     * 方法描述：@Operation(description) → @ApiOperation(notes) → 注释
     *
     * @param method 方法定义
     * @return 接口描述；方法为 {@code null} 时返回 {@code null}
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
     * 参数种类：Spring 绑定注解优先；否则 multipart → PART，其余 → QUERY
     *
     * @param parameter 方法参数
     * @return 绑定种类；应跳过的参数返回 {@code null}
     */
    public static WebParameterAnnotation.Kind kind(ParameterDefinition parameter) {
        WebParameterAnnotation binding = WebAnnotationParser.parameter(parameter);
        if (binding != null) {
            return binding.kind();
        }
        if (skip(parameter)) {
            return null;
        }
        // 无绑定注解时：文件走 Form，其余默认 Query
        if (isMultipart(parameter.type())) {
            return WebParameterAnnotation.Kind.PART;
        }
        return WebParameterAnnotation.Kind.QUERY;
    }

    /**
     * 是否跳过该参数（框架注入类型如 HttpServletRequest；MultipartFile 不跳过）。
     *
     * @param parameter 方法参数
     * @return 应忽略则为 {@code true}
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
     * 参数名：Spring 绑定 name/value → 源码名
     *
     * @param parameter 方法参数
     * @return 展示名称
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
     * 字段名：Spring 绑定 name/value → 源码名
     *
     * @param property 字段定义
     * @return 展示名称
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
     * 参数说明：OpenAPI @Parameter/@Schema → Swagger @ApiParam → 注释
     *
     * @param parameter 方法参数
     * @return 说明文本
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
     * 字段说明：OpenAPI @Schema/@Parameter → Swagger @ApiModelProperty → 注释
     *
     * @param property 字段定义
     * @return 说明文本
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
     * 参数必填：OpenAPI / Swagger / Spring / 校验任一标明必填即为必填
     *
     * @param parameter 方法参数
     * @return 必填则为 {@code true}
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
     * 字段必填：OpenAPI / Swagger / Spring / 校验任一标明必填即为必填
     *
     * @param property 字段定义
     * @return 必填则为 {@code true}
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
     * 参数示例：OpenAPI example → Swagger example → Spring defaultValue → 类型默认值
     *
     * @param parameter 方法参数
     * @return 示例值，如 {@code Long} → {@code 0}
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
            return annotated;
        }
        return typeExample(parameter.type());
    }

    /**
     * 字段示例：OpenAPI example → Swagger example → 类型默认值
     *
     * @param property 字段定义
     * @return 示例值
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
            return annotated;
        }
        return typeExample(property.type());
    }

    /**
     * 是否为 Multipart 文件类型。
     *
     * @param type 类型名
     * @return 是文件类型则为 {@code true}
     */
    public static boolean isMultipart(String type) {
        return TypeUtils.isMultipart(type);
    }

    /**
     * 是否为 Map 类型。
     *
     * @param type 类型名
     * @return 是 Map 则为 {@code true}
     */
    public static boolean isMap(String type) {
        return TypeUtils.isMap(type);
    }

    /**
     * 返回第一个非空白字符串。
     *
     * @param values 候选值
     * @return 首个有效值；全空则为 {@code null}
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
     * 是否带 {@code @NotNull}/{@code @NotBlank}/{@code @NotEmpty}。
     *
     * @param annotations 注解列表
     * @return 标明必填则为 {@code true}
     */
    private static boolean validated(List<AnnotationDefinition> annotations) {
        ValidationConstraints constraints = ValidationAnnotationParser.parse(annotations);
        return constraints.notNull() || constraints.notBlank() || constraints.notEmpty();
    }

    /**
     * 取出 {@code @Parameter(schema=...)} 内嵌 Schema。
     *
     * @param oas OpenAPI Parameter 注解
     * @return 内嵌 Schema，没有则为 {@code null}
     */
    private static SchemaAnnotation nestedSchema(DocParameterAnnotation oas) {
        return oas == null ? null : oas.schema();
    }

    /**
     * 内嵌 Schema 上的 example。
     *
     * @param oas OpenAPI Parameter 注解
     * @return 示例文本
     */
    private static String nestedSchemaExample(DocParameterAnnotation oas) {
        SchemaAnnotation schema = nestedSchema(oas);
        return schema == null ? null : schema.example();
    }

    /**
     * OpenAPI Schema 说明：description 优先，否则 title。
     *
     * @param schema Schema 注解
     * @return 说明文本
     */
    private static String openApiSchemaDescription(SchemaAnnotation schema) {
        return schema == null ? null : first(schema.description(), schema.title());
    }

    /**
     * Swagger {@code @ApiModelProperty} 说明：title 优先，否则 description。
     *
     * @param modelProperty ApiModelProperty / Schema
     * @return 说明文本
     */
    private static String swaggerModelPropertyDescription(SchemaAnnotation modelProperty) {
        return modelProperty == null ? null : first(modelProperty.title(), modelProperty.description());
    }

    /**
     * {@code @Parameter(required=true)} 是否标明必填。
     *
     * @param annotation Parameter 注解
     * @return 必填则为 {@code true}
     */
    private static boolean markedRequired(DocParameterAnnotation annotation) {
        return annotation != null && Boolean.TRUE.equals(annotation.required());
    }

    /**
     * Spring 绑定注解 {@code required=true} 是否标明必填。
     *
     * @param binding Spring 参数绑定
     * @return 必填则为 {@code true}
     */
    private static boolean markedRequired(WebParameterAnnotation binding) {
        return binding != null && Boolean.TRUE.equals(binding.required());
    }

    /**
     * Schema 的 requiredMode / required 是否标明必填。
     *
     * @param schema Schema 注解
     * @return 必填则为 {@code true}
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
     * 按 Java 类型给出默认示例：MultipartFile → {@code 文件}，Long → {@code 0}，boolean → {@code false}。
     *
     * @param type 类型名
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
     * Javadoc 正文。
     *
     * @param comment 注释定义
     * @return 正文；没有则为 {@code null}
     */
    private static String commentText(CommentDefinition comment) {
        return comment == null ? null : comment.text();
    }

    /**
     * Javadoc {@code @description} 等描述段。
     *
     * @param comment 注释定义
     * @return 描述；没有则为 {@code null}
     */
    private static String commentDescription(CommentDefinition comment) {
        return comment == null ? null : comment.description();
    }
}
