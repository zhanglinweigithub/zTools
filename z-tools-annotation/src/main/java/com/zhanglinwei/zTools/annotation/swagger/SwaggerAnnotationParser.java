package com.zhanglinwei.zTools.annotation.swagger;

import com.zhanglinwei.zTools.annotation.lookup.AnnotationLookup;
import com.zhanglinwei.zTools.annotation.lookup.AnnotationType;
import com.zhanglinwei.zTools.annotation.lookup.Attr;
import com.zhanglinwei.zTools.annotation.model.AnnotationDefinition;
import com.zhanglinwei.zTools.annotation.model.ClassDefinition;
import com.zhanglinwei.zTools.annotation.model.MethodDefinition;
import com.zhanglinwei.zTools.annotation.model.ParameterDefinition;
import com.zhanglinwei.zTools.annotation.model.PropertyDefinition;

import java.util.List;

/**
 * 解析 Swagger 2 与 OpenAPI 3 注解。
 * 两类注解分开读取，不互相覆盖；未写的属性为 {@code null}。
 */
public final class SwaggerAnnotationParser {

    public static final AnnotationType OPERATION = AnnotationType.of(
            "io.swagger.v3.oas.annotations.Operation");
    public static final AnnotationType API_OPERATION = AnnotationType.of(
            "io.swagger.annotations.ApiOperation");
    public static final AnnotationType PARAMETER = AnnotationType.of(
            "io.swagger.v3.oas.annotations.Parameter");
    public static final AnnotationType API_PARAM = AnnotationType.of(
            "io.swagger.annotations.ApiParam");
    public static final AnnotationType SCHEMA = AnnotationType.of(
            "io.swagger.v3.oas.annotations.media.Schema");
    public static final AnnotationType API_MODEL_PROPERTY = AnnotationType.of(
            "io.swagger.annotations.ApiModelProperty");
    public static final AnnotationType API_MODEL = AnnotationType.of(
            "io.swagger.annotations.ApiModel");
    public static final AnnotationType API = AnnotationType.of(
            "io.swagger.annotations.Api");
    public static final AnnotationType TAG = AnnotationType.of(
            "io.swagger.v3.oas.annotations.tags.Tag");

    /** 工具类，禁止实例化。 */
    private SwaggerAnnotationParser() {}

    /**
     * 解析方法上的 OpenAPI 3 {@code @Operation}。
     *
     * @param method 方法定义
     * @return 操作注解；没有则为 {@code null}
     */
    public static OperationAnnotation operation(MethodDefinition method) {
        return method == null ? null : operation(method.annotations());
    }

    /**
     * OpenAPI 3 {@code @Operation}。
     *
     * @param annotations 注解列表
     * @return 操作注解；没有则为 {@code null}
     */
    public static OperationAnnotation operation(List<AnnotationDefinition> annotations) {
        AnnotationDefinition annotation = find(annotations, OPERATION);
        if (annotation == null) {
            return null;
        }
        return new OperationAnnotation(
                AnnotationLookup.string(annotation, "summary"),
                AnnotationLookup.string(annotation, Attr.DESCRIPTION),
                AnnotationLookup.string(annotation, "method"),
                AnnotationLookup.string(annotation, "operationId"),
                AnnotationLookup.strings(annotation, "tags"),
                AnnotationLookup.bool(annotation, Attr.HIDDEN)
        );
    }

    /**
     * 解析方法上的 Swagger 2 {@code @ApiOperation}。
     *
     * @param method 方法定义
     * @return 操作注解；没有则为 {@code null}
     */
    public static OperationAnnotation apiOperation(MethodDefinition method) {
        return method == null ? null : apiOperation(method.annotations());
    }

    /**
     * Swagger 2 {@code @ApiOperation}。
     *
     * @param annotations 注解列表
     * @return 操作注解；没有则为 {@code null}
     */
    public static OperationAnnotation apiOperation(List<AnnotationDefinition> annotations) {
        AnnotationDefinition annotation = find(annotations, API_OPERATION);
        if (annotation == null) {
            return null;
        }
        return new OperationAnnotation(
                AnnotationLookup.string(annotation, Attr.VALUE),
                AnnotationLookup.string(annotation, "notes"),
                AnnotationLookup.string(annotation, "httpMethod"),
                AnnotationLookup.string(annotation, "nickname"),
                AnnotationLookup.strings(annotation, "tags"),
                AnnotationLookup.bool(annotation, Attr.HIDDEN)
        );
    }

    /**
     * 解析参数上的 OpenAPI 3 {@code @Parameter}。
     *
     * @param parameter 参数定义
     * @return 文档参数注解；没有则为 {@code null}
     */
    public static DocParameterAnnotation parameter(ParameterDefinition parameter) {
        return parameter == null ? null : parameter(parameter.annotations());
    }

    /**
     * 解析字段上的 OpenAPI 3 {@code @Parameter}。
     *
     * @param property 字段定义
     * @return 文档参数注解；没有则为 {@code null}
     */
    public static DocParameterAnnotation parameter(PropertyDefinition property) {
        return property == null ? null : parameter(property.annotations());
    }

    /**
     * OpenAPI 3 {@code @Parameter}。
     *
     * @param annotations 注解列表
     * @return 文档参数注解；没有则为 {@code null}
     */
    public static DocParameterAnnotation parameter(List<AnnotationDefinition> annotations) {
        AnnotationDefinition annotation = find(annotations, PARAMETER);
        if (annotation == null) {
            return null;
        }
        return new DocParameterAnnotation(
                AnnotationLookup.string(annotation, Attr.NAME),
                AnnotationLookup.string(annotation, Attr.DESCRIPTION),
                AnnotationLookup.bool(annotation, Attr.REQUIRED),
                AnnotationLookup.string(annotation, Attr.EXAMPLE),
                AnnotationLookup.string(annotation, "in"),
                AnnotationLookup.bool(annotation, Attr.HIDDEN),
                nestedSchema(annotation)
        );
    }

    /**
     * 解析参数上的 Swagger 2 {@code @ApiParam}。
     *
     * @param parameter 参数定义
     * @return 文档参数注解；没有则为 {@code null}
     */
    public static DocParameterAnnotation apiParam(ParameterDefinition parameter) {
        return parameter == null ? null : apiParam(parameter.annotations());
    }

    /**
     * 解析字段上的 Swagger 2 {@code @ApiParam}。
     *
     * @param property 字段定义
     * @return 文档参数注解；没有则为 {@code null}
     */
    public static DocParameterAnnotation apiParam(PropertyDefinition property) {
        return property == null ? null : apiParam(property.annotations());
    }

    /**
     * Swagger 2 {@code @ApiParam}。
     *
     * @param annotations 注解列表
     * @return 文档参数注解；没有则为 {@code null}
     */
    public static DocParameterAnnotation apiParam(List<AnnotationDefinition> annotations) {
        AnnotationDefinition annotation = find(annotations, API_PARAM);
        if (annotation == null) {
            return null;
        }
        return new DocParameterAnnotation(
                AnnotationLookup.string(annotation, Attr.NAME),
                AnnotationLookup.string(annotation, Attr.VALUE),
                AnnotationLookup.bool(annotation, Attr.REQUIRED),
                AnnotationLookup.string(annotation, Attr.EXAMPLE),
                null,
                AnnotationLookup.bool(annotation, Attr.HIDDEN),
                null
        );
    }

    /**
     * 解析参数上的 OpenAPI 3 {@code @Schema}。
     *
     * @param parameter 参数定义
     * @return Schema 注解；没有则为 {@code null}
     */
    public static SchemaAnnotation schema(ParameterDefinition parameter) {
        return parameter == null ? null : schema(parameter.annotations());
    }

    /**
     * 解析字段上的 OpenAPI 3 {@code @Schema}。
     *
     * @param property 字段定义
     * @return Schema 注解；没有则为 {@code null}
     */
    public static SchemaAnnotation schema(PropertyDefinition property) {
        return property == null ? null : schema(property.annotations());
    }

    /**
     * 解析类上的 OpenAPI 3 {@code @Schema}。
     *
     * @param type 类定义
     * @return Schema 注解；没有则为 {@code null}
     */
    public static SchemaAnnotation schema(ClassDefinition type) {
        return type == null ? null : schema(type.annotations());
    }

    /**
     * OpenAPI 3 {@code @Schema}。
     *
     * @param annotations 注解列表
     * @return Schema 注解；没有则为 {@code null}
     */
    public static SchemaAnnotation schema(List<AnnotationDefinition> annotations) {
        AnnotationDefinition annotation = find(annotations, SCHEMA);
        return annotation == null ? null : toSchema(annotation);
    }

    /**
     * 解析字段上的 Swagger 2 {@code @ApiModelProperty}。
     *
     * @param property 字段定义
     * @return Schema 注解；没有则为 {@code null}
     */
    public static SchemaAnnotation apiModelProperty(PropertyDefinition property) {
        return property == null ? null : apiModelProperty(property.annotations());
    }

    /**
     * Swagger 2 {@code @ApiModelProperty}。
     *
     * @param annotations 注解列表
     * @return Schema 注解；没有则为 {@code null}
     */
    public static SchemaAnnotation apiModelProperty(List<AnnotationDefinition> annotations) {
        AnnotationDefinition annotation = find(annotations, API_MODEL_PROPERTY);
        if (annotation == null) {
            return null;
        }
        return new SchemaAnnotation(
                AnnotationLookup.string(annotation, Attr.NAME),
                AnnotationLookup.string(annotation, Attr.VALUE),
                AnnotationLookup.string(annotation, "notes"),
                AnnotationLookup.string(annotation, Attr.EXAMPLE),
                null,
                null,
                null,
                AnnotationLookup.bool(annotation, Attr.REQUIRED),
                AnnotationLookup.bool(annotation, Attr.HIDDEN)
        );
    }

    /**
     * 解析类上的 Swagger 2 {@code @ApiModel}。
     *
     * @param type 类定义
     * @return Schema 注解；没有则为 {@code null}
     */
    public static SchemaAnnotation apiModel(ClassDefinition type) {
        return type == null ? null : apiModel(type.annotations());
    }

    /**
     * Swagger 2 {@code @ApiModel}。
     *
     * @param annotations 注解列表
     * @return Schema 注解；没有则为 {@code null}
     */
    public static SchemaAnnotation apiModel(List<AnnotationDefinition> annotations) {
        AnnotationDefinition annotation = find(annotations, API_MODEL);
        if (annotation == null) {
            return null;
        }
        return new SchemaAnnotation(
                AnnotationLookup.string(annotation, Attr.VALUE),
                AnnotationLookup.string(annotation, Attr.VALUE),
                AnnotationLookup.string(annotation, Attr.DESCRIPTION),
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    /**
     * 解析类上 {@code @Tag} 的 {@code name}。
     *
     * @param type 类定义
     * @return tag 名称；没有则为 {@code null}
     */
    public static String tagName(ClassDefinition type) {
        return type == null ? null : tagName(type.annotations());
    }

    /**
     * 解析 {@code @Tag} 的 {@code name}。
     *
     * @param annotations 注解列表
     * @return tag 名称；没有则为 {@code null}
     */
    public static String tagName(List<AnnotationDefinition> annotations) {
        AnnotationDefinition annotation = find(annotations, TAG);
        return annotation == null ? null : AnnotationLookup.string(annotation, Attr.NAME);
    }

    /**
     * 解析类上 {@code @Tag} 的 {@code description}。
     *
     * @param type 类定义
     * @return tag 说明；没有则为 {@code null}
     */
    public static String tagDescription(ClassDefinition type) {
        return type == null ? null : tagDescription(type.annotations());
    }

    /**
     * 解析 {@code @Tag} 的 {@code description}。
     *
     * @param annotations 注解列表
     * @return tag 说明；没有则为 {@code null}
     */
    public static String tagDescription(List<AnnotationDefinition> annotations) {
        AnnotationDefinition annotation = find(annotations, TAG);
        return annotation == null ? null : AnnotationLookup.string(annotation, Attr.DESCRIPTION);
    }

    /**
     * 读取 {@code @Parameter(schema = @Schema(...))} 的嵌套 Schema。
     *
     * @param parameter {@code @Parameter} 注解
     * @return 嵌套 Schema；未写则为 {@code null}
     */
    private static SchemaAnnotation nestedSchema(AnnotationDefinition parameter) {
        List<AnnotationDefinition> nested = AnnotationLookup.nested(parameter, "schema");
        if (nested == null || nested.isEmpty()) {
            return null;
        }
        return toSchema(nested.get(0));
    }

    /**
     * 把 {@code @Schema} 注解转为 {@link SchemaAnnotation}。
     *
     * @param annotation Schema 注解
     * @return Schema 属性
     */
    private static SchemaAnnotation toSchema(AnnotationDefinition annotation) {
        return new SchemaAnnotation(
                AnnotationLookup.string(annotation, Attr.NAME),
                AnnotationLookup.string(annotation, "title"),
                AnnotationLookup.string(annotation, Attr.DESCRIPTION),
                AnnotationLookup.string(annotation, Attr.EXAMPLE),
                AnnotationLookup.string(annotation, "type"),
                AnnotationLookup.string(annotation, "format"),
                AnnotationLookup.string(annotation, "requiredMode"),
                AnnotationLookup.bool(annotation, Attr.REQUIRED),
                AnnotationLookup.bool(annotation, Attr.HIDDEN)
        );
    }

    /**
     * 按全限定名查找注解。
     *
     * @param annotations 注解列表
     * @param type        目标注解类型
     * @return 命中的注解；没有则为 {@code null}
     */
    private static AnnotationDefinition find(List<AnnotationDefinition> annotations, AnnotationType type) {
        return AnnotationLookup.find(annotations, type).orElse(null);
    }
}
