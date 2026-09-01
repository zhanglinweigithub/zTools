package com.zhanglinwei.zTools.annotation.swagger;

import com.zhanglinwei.zTools.annotation.lookup.AnnotationDefinitions;
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

    private SwaggerAnnotationParser() {}

    public static OperationAnnotation operation(MethodDefinition method) {
        return method == null ? null : operation(method.annotations());
    }

    /** OpenAPI 3 {@code @Operation}。 */
    public static OperationAnnotation operation(List<AnnotationDefinition> annotations) {
        AnnotationDefinition annotation = find(annotations, OPERATION);
        if (annotation == null) {
            return null;
        }
        return new OperationAnnotation(
                AnnotationDefinitions.string(annotation, "summary"),
                AnnotationDefinitions.string(annotation, Attr.DESCRIPTION),
                AnnotationDefinitions.string(annotation, "method"),
                AnnotationDefinitions.string(annotation, "operationId"),
                AnnotationDefinitions.strings(annotation, "tags"),
                AnnotationDefinitions.bool(annotation, Attr.HIDDEN)
        );
    }

    public static OperationAnnotation apiOperation(MethodDefinition method) {
        return method == null ? null : apiOperation(method.annotations());
    }

    /** Swagger 2 {@code @ApiOperation}。 */
    public static OperationAnnotation apiOperation(List<AnnotationDefinition> annotations) {
        AnnotationDefinition annotation = find(annotations, API_OPERATION);
        if (annotation == null) {
            return null;
        }
        return new OperationAnnotation(
                AnnotationDefinitions.string(annotation, Attr.VALUE),
                AnnotationDefinitions.string(annotation, "notes"),
                AnnotationDefinitions.string(annotation, "httpMethod"),
                AnnotationDefinitions.string(annotation, "nickname"),
                AnnotationDefinitions.strings(annotation, "tags"),
                AnnotationDefinitions.bool(annotation, Attr.HIDDEN)
        );
    }

    public static DocParameterAnnotation parameter(ParameterDefinition parameter) {
        return parameter == null ? null : parameter(parameter.annotations());
    }

    public static DocParameterAnnotation parameter(PropertyDefinition property) {
        return property == null ? null : parameter(property.annotations());
    }

    /** OpenAPI 3 {@code @Parameter}。 */
    public static DocParameterAnnotation parameter(List<AnnotationDefinition> annotations) {
        AnnotationDefinition annotation = find(annotations, PARAMETER);
        if (annotation == null) {
            return null;
        }
        return new DocParameterAnnotation(
                AnnotationDefinitions.string(annotation, Attr.NAME),
                AnnotationDefinitions.string(annotation, Attr.DESCRIPTION),
                AnnotationDefinitions.bool(annotation, Attr.REQUIRED),
                AnnotationDefinitions.string(annotation, Attr.EXAMPLE),
                AnnotationDefinitions.string(annotation, "in"),
                AnnotationDefinitions.bool(annotation, Attr.HIDDEN),
                nestedSchema(annotation)
        );
    }

    public static DocParameterAnnotation apiParam(ParameterDefinition parameter) {
        return parameter == null ? null : apiParam(parameter.annotations());
    }

    public static DocParameterAnnotation apiParam(PropertyDefinition property) {
        return property == null ? null : apiParam(property.annotations());
    }

    /** Swagger 2 {@code @ApiParam}。 */
    public static DocParameterAnnotation apiParam(List<AnnotationDefinition> annotations) {
        AnnotationDefinition annotation = find(annotations, API_PARAM);
        if (annotation == null) {
            return null;
        }
        return new DocParameterAnnotation(
                AnnotationDefinitions.string(annotation, Attr.NAME),
                AnnotationDefinitions.string(annotation, Attr.VALUE),
                AnnotationDefinitions.bool(annotation, Attr.REQUIRED),
                AnnotationDefinitions.string(annotation, Attr.EXAMPLE),
                null,
                AnnotationDefinitions.bool(annotation, Attr.HIDDEN),
                null
        );
    }

    public static SchemaAnnotation schema(ParameterDefinition parameter) {
        return parameter == null ? null : schema(parameter.annotations());
    }

    public static SchemaAnnotation schema(PropertyDefinition property) {
        return property == null ? null : schema(property.annotations());
    }

    public static SchemaAnnotation schema(ClassDefinition type) {
        return type == null ? null : schema(type.annotations());
    }

    /** OpenAPI 3 {@code @Schema}。 */
    public static SchemaAnnotation schema(List<AnnotationDefinition> annotations) {
        AnnotationDefinition annotation = find(annotations, SCHEMA);
        return annotation == null ? null : toSchema(annotation);
    }

    public static SchemaAnnotation apiModelProperty(PropertyDefinition property) {
        return property == null ? null : apiModelProperty(property.annotations());
    }

    /** Swagger 2 {@code @ApiModelProperty}。 */
    public static SchemaAnnotation apiModelProperty(List<AnnotationDefinition> annotations) {
        AnnotationDefinition annotation = find(annotations, API_MODEL_PROPERTY);
        if (annotation == null) {
            return null;
        }
        return new SchemaAnnotation(
                AnnotationDefinitions.string(annotation, Attr.NAME),
                AnnotationDefinitions.string(annotation, Attr.VALUE),
                AnnotationDefinitions.string(annotation, "notes"),
                AnnotationDefinitions.string(annotation, Attr.EXAMPLE),
                null,
                null,
                null,
                AnnotationDefinitions.bool(annotation, Attr.REQUIRED),
                AnnotationDefinitions.bool(annotation, Attr.HIDDEN)
        );
    }

    public static SchemaAnnotation apiModel(ClassDefinition type) {
        return type == null ? null : apiModel(type.annotations());
    }

    /** Swagger 2 {@code @ApiModel}。 */
    public static SchemaAnnotation apiModel(List<AnnotationDefinition> annotations) {
        AnnotationDefinition annotation = find(annotations, API_MODEL);
        if (annotation == null) {
            return null;
        }
        return new SchemaAnnotation(
                AnnotationDefinitions.string(annotation, Attr.VALUE),
                AnnotationDefinitions.string(annotation, Attr.VALUE),
                AnnotationDefinitions.string(annotation, Attr.DESCRIPTION),
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    public static String tagName(ClassDefinition type) {
        return type == null ? null : tagName(type.annotations());
    }

    public static String tagName(List<AnnotationDefinition> annotations) {
        AnnotationDefinition annotation = find(annotations, TAG);
        return annotation == null ? null : AnnotationDefinitions.string(annotation, Attr.NAME);
    }

    public static String tagDescription(ClassDefinition type) {
        return type == null ? null : tagDescription(type.annotations());
    }

    public static String tagDescription(List<AnnotationDefinition> annotations) {
        AnnotationDefinition annotation = find(annotations, TAG);
        return annotation == null ? null : AnnotationDefinitions.string(annotation, Attr.DESCRIPTION);
    }

    private static SchemaAnnotation nestedSchema(AnnotationDefinition parameter) {
        List<AnnotationDefinition> nested = AnnotationDefinitions.nested(parameter, "schema");
        if (nested == null || nested.isEmpty()) {
            return null;
        }
        return toSchema(nested.get(0));
    }

    private static SchemaAnnotation toSchema(AnnotationDefinition annotation) {
        return new SchemaAnnotation(
                AnnotationDefinitions.string(annotation, Attr.NAME),
                AnnotationDefinitions.string(annotation, "title"),
                AnnotationDefinitions.string(annotation, Attr.DESCRIPTION),
                AnnotationDefinitions.string(annotation, Attr.EXAMPLE),
                AnnotationDefinitions.string(annotation, "type"),
                AnnotationDefinitions.string(annotation, "format"),
                AnnotationDefinitions.string(annotation, "requiredMode"),
                AnnotationDefinitions.bool(annotation, Attr.REQUIRED),
                AnnotationDefinitions.bool(annotation, Attr.HIDDEN)
        );
    }

    private static AnnotationDefinition find(List<AnnotationDefinition> annotations, AnnotationType type) {
        return AnnotationDefinitions.find(annotations, type).orElse(null);
    }
}
