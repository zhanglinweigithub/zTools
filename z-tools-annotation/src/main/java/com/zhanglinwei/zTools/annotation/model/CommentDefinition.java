package com.zhanglinwei.zTools.annotation.model;

import com.zhanglinwei.zTools.common.util.StringUtils;

/**
 * 源码里的 JavaDoc。没有注释时为 {@code null}，不把注解说明合并进来。
 * {@code @param} / {@code @return} 分别挂在参数和返回值上，不放在这里。
 */
public final class CommentDefinition {

    /** 注释正文（标签之前的文字）。 */
    private final String text;
    /** {@code @description} 标签内容；未写则为 {@code null}。 */
    private final String description;

    /**
     * @param text        注释正文
     * @param description {@code @description} 标签
     */
    public CommentDefinition(String text, String description) {
        this.text = blankToNull(text);
        this.description = blankToNull(description);
    }

    /**
     * 正文与 {@code @description} 是否都为空。
     *
     * @return 都为空则为 {@code true}
     */
    public boolean isEmpty() {
        return text == null && description == null;
    }

    /** 注释正文（标签之前的文字）。 */
    public String text() {
        return text;
    }

    /** {@code @description} 标签内容；未写则为 {@code null}。 */
    public String description() {
        return description;
    }

    /**
     * 空白字符串视为 {@code null}。
     *
     * @param value 原始字符串
     * @return 非空白文本；空白则为 {@code null}
     */
    private static String blankToNull(String value) {
        return StringUtils.isBlank(value) ? null : value.trim();
    }
}
