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
 */
public final class YApiFields {

    private static final String REQUIRED_MODE = "REQUIRED";
    private static final String NOT_REQUIRED_MODE = "NOT_REQUIRED";

    private YApiFields() {}

    /** 方法标题：@Operation(summary) → @ApiOperation(value) → 注释 → 方法名 */
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

    /** 方法描述：@Operation(description) → @ApiOperation(notes) → 注释 */
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

    /** 参数种类：Spring 绑定注解优先；否则 multipart → PART，其余 → QUERY */
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

    /** 参数名：Spring 绑定 name/value → 源码名 */
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

    /** 字段名：Spring 绑定 name/value → 源码名 */
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

    /** 参数说明：OpenAPI @Parameter/@Schema → Swagger @ApiParam → 注释 */
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

    /** 字段说明：OpenAPI @Schema/@Parameter → Swagger @ApiModelProperty → 注释 */
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

    /** 参数必填：OpenAPI / Swagger / Spring / 校验任一标明必填即为必填 */
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

    /** 字段必填：OpenAPI / Swagger / Spring / 校验任一标明必填即为必填 */
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

    /** 参数示例：OpenAPI example → Swagger example → Spring defaultValue → 类型默认值 */
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

    /** 字段示例：OpenAPI example → Swagger example → 类型默认值 */
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

    private static String swaggerModelPropertyDescription(SchemaAnnotation modelProperty) {
        return modelProperty == null ? null : first(modelProperty.title(), modelProperty.description());
    }

    private static boolean markedRequired(DocParameterAnnotation annotation) {
        return annotation != null && Boolean.TRUE.equals(annotation.required());
    }

    private static boolean markedRequired(WebParameterAnnotation binding) {
        return binding != null && Boolean.TRUE.equals(binding.required());
    }

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

    private static String commentText(CommentDefinition comment) {
        return comment == null ? null : comment.text();
    }

    private static String commentDescription(CommentDefinition comment) {
        return comment == null ? null : comment.description();
    }
}
