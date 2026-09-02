package com.zhanglinwei.zTools.annotation.parse;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaDocumentedElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import com.zhanglinwei.zTools.annotation.model.CommentDefinition;
import com.zhanglinwei.zTools.common.util.StringUtils;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 只读 JavaDoc，不与注解说明合并。
 */
public final class Comments {

    private Comments() {}

    public static CommentDefinition of(PsiJavaDocumentedElement element) {
        if (element == null || element.getDocComment() == null) {
            return null;
        }
        PsiDocComment doc = element.getDocComment();
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

    /** 方法 {@code @return} 标签；未写则为 {@code null}。 */
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

    /** 方法 {@code @param} 标签：参数名 → 注释。 */
    public static Map<String, String> params(PsiMethod method) {
        if (method == null || method.getDocComment() == null) {
            return Collections.emptyMap();
        }
        Map<String, String> params = new LinkedHashMap<String, String>();
        for (PsiDocTag tag : method.getDocComment().getTags()) {
            if (!"param".equals(tag.getName())) {
                continue;
            }
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

    /** 字段等成员的 JavaDoc 正文；没有则为 {@code null}。 */
    public static String text(PsiJavaDocumentedElement element) {
        CommentDefinition comment = of(element);
        return comment == null ? null : comment.text();
    }

    private static String descriptionBody(PsiDocComment doc) {
        StringBuilder builder = new StringBuilder();
        for (PsiElement element : doc.getDescriptionElements()) {
            builder.append(element.getText());
        }
        return clean(builder.toString());
    }

    private static String tagContent(PsiDocTag tag, String stripPrefix) {
        String text = tag.getText();
        if (text == null) {
            return null;
        }
        String cleaned = clean(text);
        if (cleaned == null) {
            return null;
        }
        cleaned = cleaned.replaceFirst(stripPrefix, "");
        return StringUtils.isBlank(cleaned) ? null : cleaned;
    }

    private static String clean(String raw) {
        if (raw == null) {
            return null;
        }
        String text = raw.replace("*", " ")
                .replace("/", " ")
                .replace("<br>", " ")
                .replace("<br/>", " ")
                .replace("<p>", " ")
                .replace("</p>", " ")
                .replaceAll("\\s+", " ")
                .trim();
        return StringUtils.isBlank(text) ? null : text;
    }
}
