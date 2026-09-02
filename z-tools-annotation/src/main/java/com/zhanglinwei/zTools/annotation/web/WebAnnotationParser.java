package com.zhanglinwei.zTools.annotation.web;

import com.zhanglinwei.zTools.annotation.lookup.AnnotationLookup;
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

    /** {@code org.springframework.stereotype.Controller} */
    public static final AnnotationType CONTROLLER = AnnotationType.of(
            "org.springframework.stereotype.Controller");
    /** {@code org.springframework.web.bind.annotation.RestController} */
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

    /** Spring 未写 defaultValue 时的占位常量名，解析结果视为没有默认值。 */
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

    /** 工具类，禁止实例化。 */
    private WebAnnotationParser() {}

    /**
     * 类上是否存在 {@code @RestController} 或 {@code @Controller}。
     *
     * @param type 类定义
     * @return 是控制器则为 {@code true}
     */
    public static boolean isController(ClassDefinition type) {
        return type != null && isController(type.annotations());
    }

    /**
     * 注解列表中是否存在 {@code @RestController} 或 {@code @Controller}。
     *
     * @param annotations 注解列表
     * @return 存在任一则为 {@code true}
     */
    public static boolean isController(List<AnnotationDefinition> annotations) {
        return AnnotationLookup.anyPresent(annotations, REST_CONTROLLER, CONTROLLER);
    }

    /**
     * 方法上是否存在 Mapping 注解。
     *
     * @param method 方法定义
     * @return 是处理器方法则为 {@code true}
     */
    public static boolean isHandlerMethod(MethodDefinition method) {
        return method != null && mapping(method.annotations()) != null;
    }

    /**
     * 解析类上的 Mapping；没有则为 {@code null}。
     *
     * @param type 类定义
     * @return Mapping 属性
     */
    public static MappingAnnotation mapping(ClassDefinition type) {
        return type == null ? null : mapping(type.annotations());
    }

    /**
     * 解析方法上的 Mapping；没有则为 {@code null}。
     *
     * @param method 方法定义
     * @return Mapping 属性
     */
    public static MappingAnnotation mapping(MethodDefinition method) {
        return method == null ? null : mapping(method.annotations());
    }

    /**
     * 解析当前注解列表上的 Mapping；没有则为 {@code null}。不与另一处 Mapping 合并。
     *
     * @param annotations 注解列表
     * @return Mapping 属性
     */
    public static MappingAnnotation mapping(List<AnnotationDefinition> annotations) {
        for (int i = 0; i < MAPPINGS.length; i++) {
            MappingKind kind = MAPPINGS[i];
            Optional<AnnotationDefinition> found = AnnotationLookup.find(annotations, kind.type);
            if (found.isPresent()) {
                return toMapping(found.get(), kind.impliedMethod);
            }
        }
        return null;
    }

    /**
     * 解析参数上的绑定注解；没有则为 {@code null}。
     *
     * @param parameter 参数定义
     * @return 参数绑定属性
     */
    public static WebParameterAnnotation parameter(ParameterDefinition parameter) {
        return parameter == null ? null : parameter(parameter.annotations());
    }

    /**
     * 解析参数上的绑定注解；没有则为 {@code null}。不把简单类型推断成 RequestParam。
     *
     * @param annotations 注解列表
     * @return 参数绑定属性
     */
    public static WebParameterAnnotation parameter(List<AnnotationDefinition> annotations) {
        for (int i = 0; i < PARAMETERS.length; i++) {
            ParameterKind kind = PARAMETERS[i];
            Optional<AnnotationDefinition> found = AnnotationLookup.find(annotations, kind.type);
            if (found.isPresent()) {
                return toParameter(found.get(), kind.kind);
            }
        }
        return null;
    }

    /**
     * 把 Mapping 注解转为 {@link MappingAnnotation}。
     * {@code @GetMapping} 等组合注解未写 {@code method} 时带上对应动词；{@code @RequestMapping} 未写则为 {@code null}。
     *
     * @param annotation    命中的 Mapping 注解
     * @param impliedMethod 组合注解隐含的 HTTP 动词，{@code @RequestMapping} 为 {@code null}
     * @return Mapping 属性
     */
    private static MappingAnnotation toMapping(AnnotationDefinition annotation, String impliedMethod) {
        List<String> methods = AnnotationLookup.strings(annotation, "method");
        // 组合注解（GetMapping 等）源码未写 method 时，用注解本身隐含的动词
        if (methods == null && impliedMethod != null) {
            methods = Collections.singletonList(impliedMethod);
        }
        return new MappingAnnotation(
                AnnotationLookup.string(annotation, Attr.NAME),
                AnnotationLookup.strings(annotation, Attr.PATH, Attr.VALUE, "url"),
                methods,
                AnnotationLookup.strings(annotation, "consumes", "contentType"),
                AnnotationLookup.strings(annotation, "produces", "accept"),
                AnnotationLookup.strings(annotation, "params"),
                AnnotationLookup.strings(annotation, "headers")
        );
    }

    /**
     * 把参数绑定注解转为 {@link WebParameterAnnotation}。未写的属性保持 {@code null}。
     *
     * @param annotation 命中的绑定注解
     * @param kind       绑定种类
     * @return 参数绑定属性
     */
    private static WebParameterAnnotation toParameter(AnnotationDefinition annotation, WebParameterAnnotation.Kind kind) {
        return new WebParameterAnnotation(
                kind,
                AnnotationLookup.string(annotation, Attr.NAME, Attr.VALUE),
                AnnotationLookup.bool(annotation, Attr.REQUIRED),
                noneToNull(AnnotationLookup.string(annotation, "defaultValue"))
        );
    }

    /**
     * Spring 的 {@code DEFAULT_NONE} 表示未设置默认值，结果视为 {@code null}。
     *
     * @param defaultValue 源码写出的 defaultValue
     * @return 真实默认值；占位常量则为 {@code null}
     */
    private static String noneToNull(String defaultValue) {
        return DEFAULT_NONE.equals(defaultValue) ? null : defaultValue;
    }

    /** Mapping 注解种类及其隐含 HTTP 动词。 */
    private static final class MappingKind {
        private final AnnotationType type;
        private final String impliedMethod;

        /**
         * @param type          Mapping 注解类型
         * @param impliedMethod 组合注解隐含动词；{@code @RequestMapping} 为 {@code null}
         */
        private MappingKind(AnnotationType type, String impliedMethod) {
            this.type = type;
            this.impliedMethod = impliedMethod;
        }
    }

    /** 参数绑定注解种类。 */
    private static final class ParameterKind {
        private final AnnotationType type;
        private final WebParameterAnnotation.Kind kind;

        /**
         * @param type 绑定注解类型
         * @param kind 绑定种类
         */
        private ParameterKind(AnnotationType type, WebParameterAnnotation.Kind kind) {
            this.type = type;
            this.kind = kind;
        }
    }
}
