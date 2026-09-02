package com.zhanglinwei.zTools.annotation.feign;

import com.zhanglinwei.zTools.annotation.lookup.AnnotationDefinitions;
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

    public static final AnnotationType REQUEST_LINE = AnnotationType.of("feign.RequestLine");

    private FeignAnnotationParser() {}

    public static RequestLineAnnotation requestLine(MethodDefinition method) {
        return method == null ? null : requestLine(method.annotations());
    }

    /**
     * {@code @RequestLine("GET /users/{id}")} → method={@code GET}，path={@code /users/{id}}。
     * 没有该注解或未写 {@code value} 时返回 {@code null}。
     */
    public static RequestLineAnnotation requestLine(List<AnnotationDefinition> annotations) {
        Optional<AnnotationDefinition> found = AnnotationDefinitions.find(annotations, REQUEST_LINE);
        if (!found.isPresent()) {
            return null;
        }
        String value = AnnotationDefinitions.string(found.get(), Attr.VALUE);
        if (StringUtils.isBlank(value)) {
            return null;
        }
        String trimmed = value.trim();
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

    private static int indexOfWhitespace(String text) {
        for (int i = 0; i < text.length(); i++) {
            if (Character.isWhitespace(text.charAt(i))) {
                return i;
            }
        }
        return -1;
    }
}
