package com.zhanglinwei.zTools.annotation.parse;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaDocumentedElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import com.zhanglinwei.zTools.annotation.model.CommentDefinition;
import com.zhanglinwei.zTools.common.constant.StringPool;
import com.zhanglinwei.zTools.common.util.StringUtils;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 只读 JavaDoc，不与注解说明合并。
 * {@code @param} / {@code @return} 分别挂到参数和返回值上，不放进 {@link CommentDefinition}。
 */
public final class CommentParser {

    /** 工具类，禁止实例化。 */
    private CommentParser() {}

    /**
     * 读取元素 JavaDoc 的正文与 {@code @description} 标签。
     *
     * @param element 带 JavaDoc 的 PSI 元素
     * @return 注释定义；没有 JavaDoc 或内容为空时为 {@code null}
     */
    public static CommentDefinition of(PsiJavaDocumentedElement element) {
        if (element == null || element.getDocComment() == null) {
            return null;
        }
        PsiDocComment doc = element.getDocComment();
        // 正文与 @description 分开存；@param / @return 不在这里处理
        String descriptionTag = null;
        for (PsiDocTag tag : doc.getTags()) {
            if ("description".equalsIgnoreCase(tag.getName())) {
                descriptionTag = tagContent(tag, "(?i)^@description\\s*");
                break;
            }
        }
        CommentDefinition comment = new CommentDefinition(descriptionBody(doc), descriptionTag);
        return comment.isEmpty() ? null : comment;
    }

    /**
     * 方法 {@code @return} 标签；未写则为 {@code null}。
     *
     * @param method 方法 PSI
     * @return {@code @return} 正文
     */
    public static String returnComment(PsiMethod method) {
        if (method == null || method.getDocComment() == null) {
            return null;
        }
        for (PsiDocTag tag : method.getDocComment().getTags()) {
            if ("return".equalsIgnoreCase(tag.getName())) {
                return tagContent(tag, "(?i)^@return\\s*");
            }
        }
        return null;
    }

    /**
     * 方法 {@code @param} 标签：参数名 → 注释。
     * 例如 {@code @param userId 用户编号} 写入 map 的 {@code userId → 用户编号}。
     *
     * @param method 方法 PSI
     * @return 参数名到注释的映射，保持标签出现顺序；没有 JavaDoc 时为空 Map
     */
    public static Map<String, String> params(PsiMethod method) {
        if (method == null || method.getDocComment() == null) {
            return Collections.emptyMap();
        }
        Map<String, String> params = new LinkedHashMap<String, String>();
        for (PsiDocTag tag : method.getDocComment().getTags()) {
            if (!"param".equals(tag.getName())) {
                continue;
            }
            // 标签值元素是参数名，去掉 "@param name " 前缀后剩下注释正文
            PsiElement value = tag.getValueElement();
            if (value == null || StringUtils.isBlank(value.getText())) {
                continue;
            }
            String content = tagContent(tag, "(?i)^@param\\s+\\S+\\s*");
            if (content != null) {
                params.put(value.getText().trim(), content);
            }
        }
        return params;
    }

    /**
     * 字段等成员的 JavaDoc 正文；没有则为 {@code null}。
     *
     * @param element 带 JavaDoc 的 PSI 元素
     * @return 注释正文
     */
    public static String text(PsiJavaDocumentedElement element) {
        CommentDefinition comment = of(element);
        return comment == null ? null : comment.text();
    }

    /**
     * 拼接 JavaDoc 标签之前的正文。
     *
     * @param doc JavaDoc
     * @return 清洗后的正文；空白则为 {@code null}
     */
    private static String descriptionBody(PsiDocComment doc) {
        StringBuilder builder = new StringBuilder();
        for (PsiElement element : doc.getDescriptionElements()) {
            builder.append(element.getText());
        }
        return clean(builder.toString());
    }

    /**
     * 取出标签全文并去掉前缀（如 {@code @param userId }）。
     *
     * @param tag         JavaDoc 标签
     * @param stripPrefix 要剥掉的前缀正则
     * @return 标签正文；空白则为 {@code null}
     */
    private static String tagContent(PsiDocTag tag, String stripPrefix) {
        String text = tag.getText();
        if (text == null) {
            return null;
        }
        String cleaned = clean(text);
        if (cleaned == null) {
            return null;
        }
        cleaned = cleaned.replaceFirst(stripPrefix, StringPool.EMPTY);
        return StringUtils.isBlank(cleaned) ? null : cleaned;
    }

    /**
     * 去掉星号、斜杠和常见 HTML 换行，压缩空白。
     *
     * @param raw 原始文本
     * @return 清洗后的文本；空白则为 {@code null}
     */
    private static String clean(String raw) {
        if (raw == null) {
            return null;
        }
        String text = raw.replace(StringPool.STAR, StringPool.SPACE)
                .replace(StringPool.SLASH, StringPool.SPACE)
                .replace("<br>", StringPool.SPACE)
                .replace("<br/>", StringPool.SPACE)
                .replace("<p>", StringPool.SPACE)
                .replace("</p>", StringPool.SPACE)
                .replaceAll("\\s+", StringPool.SPACE)
                .trim();
        return StringUtils.isBlank(text) ? null : text;
    }
}
