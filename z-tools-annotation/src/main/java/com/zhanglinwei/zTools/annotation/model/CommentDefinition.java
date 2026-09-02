package com.zhanglinwei.zTools.annotation.model;

import com.zhanglinwei.zTools.common.util.StringUtils;

/**
 * 源码里的 JavaDoc。没有注释时为 {@code null}，不把注解说明合并进来。
 * {@code @param} / {@code @return} 分别挂在参数和返回值上，不放在这里。
 */
public final class CommentDefinition {

    private final String text;
    private final String description;

    public CommentDefinition(String text, String description) {
        this.text = blankToNull(text);
        this.description = blankToNull(description);
    }

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

    private static String blankToNull(String value) {
        return StringUtils.isBlank(value) ? null : value.trim();
    }
}
