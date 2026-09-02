package com.zhanglinwei.zTools.annotation.web;

import com.zhanglinwei.zTools.annotation.lookup.AnnotationDefinitions;
import com.zhanglinwei.zTools.annotation.lookup.AnnotationType;
import com.zhanglinwei.zTools.annotation.lookup.Attr;
import com.zhanglinwei.zTools.annotation.model.AnnotationDefinition;
import com.zhanglinwei.zTools.annotation.model.ClassDefinition;
import com.zhanglinwei.zTools.annotation.model.MethodDefinition;
import com.zhanglinwei.zTools.annotation.model.ParameterDefinition;
import com.zhanglinwei.zTools.common.enums.HttpMethod;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 解析 Spring Web 注解：Controller、Mapping、参数绑定。
 * 只读源码写出的属性，不合并类/方法 Mapping，不补 {@code required} 缺省值。
 */
public final class WebAnnotationParser {

    public static final AnnotationType CONTROLLER = AnnotationType.of(
            "org.springframework.stereotype.Controller");
    public static final AnnotationType REST_CONTROLLER = AnnotationType.of(
            "org.springframework.web.bind.annotation.RestController");

    public static final AnnotationType GET_MAPPING = AnnotationType.of(
            "org.springframework.web.bind.annotation.GetMapping");
    public static final AnnotationType POST_MAPPING = AnnotationType.of(
            "org.springframework.web.bind.annotation.PostMapping");
    public static final AnnotationType PUT_MAPPING = AnnotationType.of(
            "org.springframework.web.bind.annotation.PutMapping");
    public static final AnnotationType DELETE_MAPPING = AnnotationType.of(
            "org.springframework.web.bind.annotation.DeleteMapping");
    public static final AnnotationType PATCH_MAPPING = AnnotationType.of(
            "org.springframework.web.bind.annotation.PatchMapping");
    public static final AnnotationType REQUEST_MAPPING = AnnotationType.of(
            "org.springframework.web.bind.annotation.RequestMapping");

    public static final AnnotationType REQUEST_PARAM = AnnotationType.of(
            "org.springframework.web.bind.annotation.RequestParam");
    public static final AnnotationType PATH_VARIABLE = AnnotationType.of(
            "org.springframework.web.bind.annotation.PathVariable");
    public static final AnnotationType REQUEST_HEADER = AnnotationType.of(
            "org.springframework.web.bind.annotation.RequestHeader");
    public static final AnnotationType REQUEST_BODY = AnnotationType.of(
            "org.springframework.web.bind.annotation.RequestBody");
    public static final AnnotationType REQUEST_PART = AnnotationType.of(
            "org.springframework.web.bind.annotation.RequestPart");

    private static final String DEFAULT_NONE = "DEFAULT_NONE";

    private static final MappingKind[] MAPPINGS = {
            new MappingKind(GET_MAPPING, HttpMethod.GET.name()),
            new MappingKind(POST_MAPPING, HttpMethod.POST.name()),
            new MappingKind(PUT_MAPPING, HttpMethod.PUT.name()),
            new MappingKind(DELETE_MAPPING, HttpMethod.DELETE.name()),
            new MappingKind(PATCH_MAPPING, HttpMethod.PATCH.name()),
            new MappingKind(REQUEST_MAPPING, null)
    };

    private static final ParameterKind[] PARAMETERS = {
            new ParameterKind(REQUEST_PARAM, WebParameterAnnotation.Kind.QUERY),
            new ParameterKind(PATH_VARIABLE, WebParameterAnnotation.Kind.PATH),
            new ParameterKind(REQUEST_HEADER, WebParameterAnnotation.Kind.HEADER),
            new ParameterKind(REQUEST_BODY, WebParameterAnnotation.Kind.BODY),
            new ParameterKind(REQUEST_PART, WebParameterAnnotation.Kind.PART),
    };

    private WebAnnotationParser() {}

    public static boolean isController(ClassDefinition type) {
        return type != null && isController(type.annotations());
    }

    public static boolean isController(List<AnnotationDefinition> annotations) {
        return AnnotationDefinitions.anyPresent(annotations, REST_CONTROLLER, CONTROLLER);
    }

    public static boolean isHandlerMethod(MethodDefinition method) {
        return method != null && mapping(method.annotations()) != null;
    }

    public static MappingAnnotation mapping(ClassDefinition type) {
        return type == null ? null : mapping(type.annotations());
    }

    public static MappingAnnotation mapping(MethodDefinition method) {
        return method == null ? null : mapping(method.annotations());
    }

    /** 解析当前注解列表上的 Mapping；没有则为 {@code null}。不与另一处 Mapping 合并。 */
    public static MappingAnnotation mapping(List<AnnotationDefinition> annotations) {
        for (int i = 0; i < MAPPINGS.length; i++) {
            MappingKind kind = MAPPINGS[i];
            Optional<AnnotationDefinition> found = AnnotationDefinitions.find(annotations, kind.type);
            if (found.isPresent()) {
                return toMapping(found.get(), kind.impliedMethod);
            }
        }
        return null;
    }

    public static WebParameterAnnotation parameter(ParameterDefinition parameter) {
        return parameter == null ? null : parameter(parameter.annotations());
    }

    /** 解析参数上的绑定注解；没有则为 {@code null}。不把简单类型推断成 RequestParam。 */
    public static WebParameterAnnotation parameter(List<AnnotationDefinition> annotations) {
        for (int i = 0; i < PARAMETERS.length; i++) {
            ParameterKind kind = PARAMETERS[i];
            Optional<AnnotationDefinition> found = AnnotationDefinitions.find(annotations, kind.type);
            if (found.isPresent()) {
                return toParameter(found.get(), kind.kind);
            }
        }
        return null;
    }

    private static MappingAnnotation toMapping(AnnotationDefinition annotation, String impliedMethod) {
        List<String> methods = AnnotationDefinitions.strings(annotation, "method");
        if (methods == null && impliedMethod != null) {
            methods = Collections.singletonList(impliedMethod);
        }
        return new MappingAnnotation(
                AnnotationDefinitions.string(annotation, Attr.NAME),
                AnnotationDefinitions.strings(annotation, Attr.PATH, Attr.VALUE, "url"),
                methods,
                AnnotationDefinitions.strings(annotation, "consumes", "contentType"),
                AnnotationDefinitions.strings(annotation, "produces", "accept"),
                AnnotationDefinitions.strings(annotation, "params"),
                AnnotationDefinitions.strings(annotation, "headers")
        );
    }

    private static WebParameterAnnotation toParameter(AnnotationDefinition annotation, WebParameterAnnotation.Kind kind) {
        return new WebParameterAnnotation(
                kind,
                AnnotationDefinitions.string(annotation, Attr.NAME, Attr.VALUE),
                AnnotationDefinitions.bool(annotation, Attr.REQUIRED),
                noneToNull(AnnotationDefinitions.string(annotation, "defaultValue"))
        );
    }

    private static String noneToNull(String defaultValue) {
        return DEFAULT_NONE.equals(defaultValue) ? null : defaultValue;
    }

    private static final class MappingKind {
        private final AnnotationType type;
        private final String impliedMethod;

        private MappingKind(AnnotationType type, String impliedMethod) {
            this.type = type;
            this.impliedMethod = impliedMethod;
        }
    }

    private static final class ParameterKind {
        private final AnnotationType type;
        private final WebParameterAnnotation.Kind kind;

        private ParameterKind(AnnotationType type, WebParameterAnnotation.Kind kind) {
            this.type = type;
            this.kind = kind;
        }
    }
}
