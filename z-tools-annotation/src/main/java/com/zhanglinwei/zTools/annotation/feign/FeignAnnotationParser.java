package com.zhanglinwei.zTools.annotation.feign;

import com.zhanglinwei.zTools.annotation.lookup.AnnotationLookup;
import com.zhanglinwei.zTools.annotation.lookup.AnnotationType;
import com.zhanglinwei.zTools.annotation.lookup.Attr;
import com.zhanglinwei.zTools.annotation.model.AnnotationDefinition;
import com.zhanglinwei.zTools.annotation.model.MethodDefinition;
import com.zhanglinwei.zTools.common.util.StringUtils;

import java.util.List;
import java.util.Optional;

/**
 * 解析 Feign {@code @RequestLine}。只读源码写出的 {@code value}，不补 default。
 */
public final class FeignAnnotationParser {

    /** {@code feign.RequestLine} */
    public static final AnnotationType REQUEST_LINE = AnnotationType.of("feign.RequestLine");

    /** 工具类，禁止实例化。 */
    private FeignAnnotationParser() {}

    /**
     * 解析方法上的 {@code @RequestLine}。
     *
     * @param method 方法定义
     * @return RequestLine 属性；没有则为 {@code null}
     */
    public static RequestLineAnnotation requestLine(MethodDefinition method) {
        return method == null ? null : requestLine(method.annotations());
    }

    /**
     * {@code @RequestLine("GET /users/{id}")} → method={@code GET}，path={@code /users/{id}}。
     * 没有该注解或未写 {@code value} 时返回 {@code null}。
     *
     * @param annotations 注解列表
     * @return RequestLine 属性
     */
    public static RequestLineAnnotation requestLine(List<AnnotationDefinition> annotations) {
        Optional<AnnotationDefinition> found = AnnotationLookup.find(annotations, REQUEST_LINE);
        if (!found.isPresent()) {
            return null;
        }
        String value = AnnotationLookup.string(found.get(), Attr.VALUE);
        if (StringUtils.isBlank(value)) {
            return null;
        }
        String trimmed = value.trim();
        // 第一个空白切开动词和 path；没有空白则整段既当 method 也当原始 value
        int space = indexOfWhitespace(trimmed);
        if (space < 0) {
            return new RequestLineAnnotation(trimmed, trimmed, null);
        }
        String httpMethod = trimmed.substring(0, space).trim();
        String path = trimmed.substring(space).trim();
        return new RequestLineAnnotation(
                trimmed,
                StringUtils.isBlank(httpMethod) ? null : httpMethod,
                StringUtils.isBlank(path) ? null : path
        );
    }

    /**
     * 第一个空白字符的下标。
     *
     * @param text 待扫描文本
     * @return 下标；没有空白则为 {@code -1}
     */
    private static int indexOfWhitespace(String text) {
        for (int i = 0; i < text.length(); i++) {
            if (Character.isWhitespace(text.charAt(i))) {
                return i;
            }
        }
        return -1;
    }
}
