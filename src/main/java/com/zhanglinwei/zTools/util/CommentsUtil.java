package com.zhanglinwei.zTools.util;

import com.google.common.base.Strings;
import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import com.zhanglinwei.zTools.common.constants.CharacterPool;
import com.zhanglinwei.zTools.doc.apidoc.constant.SwaggerAnnotation;

import java.util.HashMap;
import java.util.Map;

import static com.zhanglinwei.zTools.common.constants.SpringPool.*;

/**
 * 注释工具类
 */
public final class CommentsUtil {

    private CommentsUtil(){}

    /**
     * 获得方法注释中的字段描述
     */
    public static Map<String, String> extractParamCommentsMap(PsiDocComment docComment) {
        Map<String, String> paramDescMap = new HashMap<>();
        if (docComment == null) {
            return paramDescMap;
        }
        for (PsiDocTag docTag : docComment.getTags()) {
            String tagValue = docTag.getValueElement() == null ? EMPTY : docTag.getValueElement().getText();
            if ("param".equals(docTag.getName()) && AssertUtils.isNotBlank(tagValue)) {
                paramDescMap.put(tagValue, extractParamComments(docTag.getText()));
            }
        }
        return paramDescMap;
    }

    private static String extractParamComments(String tagText) {
        String[] strings = tagText.replace(STAR, EMPTY).replaceAll(" {2,}", SPACE).trim().split(SPACE);
        if (strings.length >= 3) {
            String desc = strings[2];
            return desc.replace(NEWLINE, EMPTY);
        }
        return EMPTY;
    }

    /**
     * 去除字符串首尾出现的某个字符.
     *
     * @param source  源字符串.
     * @param element 需要去除的字符.
     * @return String.
     */
    private static String trimFirstAndLastChar(String source, char element) {
        boolean beginIndexFlag;
        boolean endIndexFlag;
        do {
            if (Strings.isNullOrEmpty(source.trim()) || source.equals(String.valueOf(element))) {
                source = EMPTY;
                break;
            }
            int beginIndex = source.indexOf(element) == 0 ? 1 : 0;
            int endIndex = source.lastIndexOf(element) + 1 == source.length() ? source.lastIndexOf(element) : source.length();
            source = source.substring(beginIndex, endIndex);
            beginIndexFlag = (source.indexOf(element) == 0);
            endIndexFlag = (source.lastIndexOf(element) + 1 == source.length());
        } while (beginIndexFlag || endIndexFlag);
        return source;
    }

    public static String extractComments(PsiClass psiClass) {
        String description = extractComments(psiClass.getDocComment(), psiClass.getAnnotations());
        return AssertUtils.isBlank(description) ? psiClass.getName() : description;
    }

    public static String extractComments(PsiMethod psiMethod) {
        String description = extractComments(psiMethod.getDocComment(), psiMethod.getAnnotations());
        return AssertUtils.isBlank(description) ? psiMethod.getName() : description;
    }

    public static String extractComments(PsiField psiField) {
        String description = extractComments(psiField.getDocComment(), psiField.getAnnotations());
        return AssertUtils.isBlank(description) ? psiField.getName() : description;
    }

    private static String extractComments(PsiDocComment docComment, PsiAnnotation[] annotations) {
        String desc = extractComments(docComment);
        if(AssertUtils.isBlank(desc)) {
            for (PsiAnnotation annotation : annotations) {
                String annotationText = annotation.getText();
                if(
                        annotationText.contains(SwaggerAnnotation.ApiOperation) ||
                        annotationText.contains(SwaggerAnnotation.ApiModelProperty)
                ) {
                    PsiNameValuePair[] attributes = annotation.getParameterList().getAttributes();
                    if(attributes.length == 1 && attributes[0].getName() == null) {
                        return attributes[0].getLiteralValue();
                    }
                    if(attributes.length >= 1) {
                        for (PsiNameValuePair attribute : attributes) {
                            if("value".equals(attribute.getName())) {
                                return attribute.getLiteralValue();
                            }
                        }
                    }
                }
            }
        }
        return desc;
    }

    public static String extractComments(PsiDocComment psiDocComment) {
        if (psiDocComment != null) {
            PsiDocTag[] psiDocTags = psiDocComment.getTags();
            for (PsiDocTag psiDocTag : psiDocTags) {
                String docTag = psiDocTag.getText().toLowerCase();
                if (docTag.contains("description")) {
                    return trimFirstAndLastChar(docTag
                            .replace("@description", EMPTY)
                            .replace("@Description", EMPTY)
                            .replace("Description", EMPTY)
                            .replace("<br>", EMPTY)
                            .replace(COLON, EMPTY)
                            .replace(STAR, EMPTY)
                            .replace(NEWLINE, SPACE), CharacterPool.SPACE);
                }
            }
            // 匹配 "todo"（忽略大小写）及其后面的所有内容，直到换行符或字符串结尾
            String text = psiDocComment.getText().replaceAll("(?i)TODO.*", EMPTY);
            return trimFirstAndLastChar(
                    text.split(AT)[0]
                            .replace("@description", EMPTY)
                            .replace("@Description", EMPTY)
                            .replace("Description", EMPTY)
                            .replace("<br>", NEWLINE)
                            .replace(":", EMPTY)
                            .replace("*", EMPTY)
                            .replace("/", EMPTY)
                            .replace("\n", SPACE)
                            .replace("<p>", NEWLINE)
                            .replace("</p>", NEWLINE)
                            .replace("<li>", NEWLINE)
                            .replace("</li>", NEWLINE)
                            .replace("{", EMPTY), CharacterPool.SPACE
            );
        }

        return null;
    }

}