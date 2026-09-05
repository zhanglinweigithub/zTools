package com.zhanglinwei.zTools.yapi.utils;

import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.javadoc.PsiDocComment;
import com.zhanglinwei.zTools.annotation.lookup.Attr;
import com.zhanglinwei.zTools.annotation.model.AnnotationDefinition;
import com.zhanglinwei.zTools.annotation.model.AttributeDefinition;
import com.zhanglinwei.zTools.annotation.model.MethodDefinition;
import com.zhanglinwei.zTools.annotation.model.ParameterDefinition;
import com.zhanglinwei.zTools.common.util.CollectionUtils;
import com.zhanglinwei.zTools.common.util.StringUtils;

import java.util.List;

import static com.zhanglinwei.zTools.common.constant.StringPool.AT;
import static com.zhanglinwei.zTools.common.constant.StringPool.COMMA_SPACE;
import static com.zhanglinwei.zTools.common.constant.StringPool.EMPTY;
import static com.zhanglinwei.zTools.common.constant.StringPool.EQUAL;
import static com.zhanglinwei.zTools.common.constant.StringPool.LEFT_BRACE;
import static com.zhanglinwei.zTools.common.constant.StringPool.LEFT_BRACKET;
import static com.zhanglinwei.zTools.common.constant.StringPool.NEWLINE;
import static com.zhanglinwei.zTools.common.constant.StringPool.QUOTE;
import static com.zhanglinwei.zTools.common.constant.StringPool.RIGHT_BRACE;
import static com.zhanglinwei.zTools.common.constant.StringPool.RIGHT_BRACKET;
import static com.zhanglinwei.zTools.common.constant.StringPool.SEMICOLON;
import static com.zhanglinwei.zTools.common.constant.StringPool.SPACE;

/**
 * 从方法源码抽出签名（注解、修饰符、返回类型、参数、throws），不含 JavaDoc 与方法体。
 */
public final class YApiMethodSignature {

    private YApiMethodSignature() {
    }

    /**
     * YApi 接口 {@code desc}：优先用 PSI 源码签名；没有 {@code delegate} 时按定义回拼。
     *
     * @param method 方法定义
     * @return 方法签名；{@code method} 为 {@code null} 时为空串
     */
    public static String of(MethodDefinition method) {
        if (method == null) {
            return EMPTY;
        }
        PsiMethod psiMethod = method.delegate();
        if (psiMethod != null) {
            return of(psiMethod);
        }
        return reconstruct(method);
    }

    /**
     * 从 {@link PsiMethod} 取源码，去掉 JavaDoc 与 {@code {}} 方法体。
     *
     * @param method PSI 方法
     * @return 方法签名；{@code method} 为 {@code null} 时为空串
     */
    public static String of(PsiMethod method) {
        if (method == null) {
            return EMPTY;
        }
        PsiDocComment docComment = method.getDocComment();
        PsiCodeBlock body = method.getBody();
        return ofText(
                method.getText(),
                docComment == null ? null : docComment.getText(),
                body == null ? null : body.getText()
        );
    }

    /**
     * 从完整方法源码中去掉 JavaDoc 与方法体，保留签名。
     * 方法体按整段文本删除，因此注解里的 {@code { "/a" }} 不会被误伤。
     *
     * @param methodText            {@link PsiMethod#getText()}
     * @param docComment            JavaDoc 原文，没有则为 {@code null}
     * @param bodyIncludingBraces   方法体（含花括号），抽象/接口方法为 {@code null}
     * @return 去掉文档与方法体后的签名
     */
    public static String ofText(String methodText, String docComment, String bodyIncludingBraces) {
        if (StringUtils.isBlank(methodText)) {
            return EMPTY;
        }
        String text = methodText;
        if (StringUtils.isNotBlank(docComment)) {
            int index = text.indexOf(docComment);
            if (index >= 0) {
                text = text.substring(0, index) + text.substring(index + docComment.length());
            }
        }
        if (StringUtils.isNotBlank(bodyIncludingBraces)) {
            int index = text.lastIndexOf(bodyIncludingBraces);
            if (index >= 0) {
                text = text.substring(0, index);
            }
        }
        text = dedent(text);
        if (text.endsWith(SEMICOLON)) {
            text = dedent(text.substring(0, text.length() - 1));
        }
        return text;
    }

    /**
     * 去掉各行共同的前导空白，避免 {@code trim} 只削第一行导致注解和 {@code public} 不对齐。
     *
     * @param text 签名原文
     * @return 左对齐后的文本
     */
    private static String dedent(String text) {
        if (StringUtils.isBlank(text)) {
            return EMPTY;
        }
        String normalized = text.replace("\r\n", NEWLINE).replace("\r", NEWLINE);
        String[] lines = normalized.split("\n", -1);
        int start = 0;
        int end = lines.length - 1;
        while (start <= end && StringUtils.isBlank(lines[start])) {
            start++;
        }
        while (end >= start && StringUtils.isBlank(lines[end])) {
            end--;
        }
        if (start > end) {
            return EMPTY;
        }
        int minIndent = Integer.MAX_VALUE;
        for (int i = start; i <= end; i++) {
            if (StringUtils.isBlank(lines[i])) {
                continue;
            }
            int indent = leadingWhitespace(lines[i]);
            if (indent < minIndent) {
                minIndent = indent;
            }
        }
        StringBuilder aligned = new StringBuilder();
        for (int i = start; i <= end; i++) {
            if (i > start) {
                aligned.append(NEWLINE);
            }
            String line = lines[i];
            if (StringUtils.isBlank(line)) {
                continue;
            }
            String stripped = line.length() <= minIndent ? EMPTY : line.substring(minIndent);
            aligned.append(trimTrailing(stripped));
        }
        return aligned.toString();
    }

    /**
     * 行首连续空格 / 制表符个数。
     *
     * @param line 一行文本
     * @return 前导空白长度
     */
    private static int leadingWhitespace(String line) {
        int count = 0;
        for (int i = 0; i < line.length(); i++) {
            char current = line.charAt(i);
            if (current != ' ' && current != '\t') {
                break;
            }
            count++;
        }
        return count;
    }

    /**
     * 去掉行尾空白。
     *
     * @param line 一行文本
     * @return 去掉尾部空格 / 制表符后的文本
     */
    private static String trimTrailing(String line) {
        int end = line.length();
        while (end > 0) {
            char current = line.charAt(end - 1);
            if (current != ' ' && current != '\t') {
                break;
            }
            end--;
        }
        return end == line.length() ? line : line.substring(0, end);
    }

    /**
     * 没有 PSI 时按注解、返回类型、参数回拼签名。
     *
     * @param method 方法定义
     * @return 回拼的签名
     */
    private static String reconstruct(MethodDefinition method) {
        StringBuilder signature = new StringBuilder();
        List<AnnotationDefinition> annotations = method.annotations();
        if (!CollectionUtils.isEmpty(annotations)) {
            for (int i = 0; i < annotations.size(); i++) {
                if (i > 0) {
                    signature.append(NEWLINE);
                }
                signature.append(formatAnnotation(annotations.get(i)));
            }
            signature.append(NEWLINE);
        }
        ParameterDefinition returns = method.returns();
        if (returns != null && StringUtils.isNotBlank(returns.type())) {
            signature.append(returns.type()).append(SPACE);
        }
        signature.append(method.name()).append(LEFT_BRACKET);
        List<ParameterDefinition> parameters = method.parameters();
        if (!CollectionUtils.isEmpty(parameters)) {
            for (int i = 0; i < parameters.size(); i++) {
                if (i > 0) {
                    signature.append(COMMA_SPACE);
                }
                signature.append(formatParameter(parameters.get(i)));
            }
        }
        signature.append(RIGHT_BRACKET);
        return signature.toString();
    }

    /**
     * 参数：绑定注解 + 类型 + 名称。
     *
     * @param parameter 参数定义
     * @return 如 {@code @PathVariable Long id}
     */
    private static String formatParameter(ParameterDefinition parameter) {
        StringBuilder text = new StringBuilder();
        List<AnnotationDefinition> annotations = parameter.annotations();
        if (!CollectionUtils.isEmpty(annotations)) {
            for (int i = 0; i < annotations.size(); i++) {
                if (i > 0) {
                    text.append(SPACE);
                }
                text.append(formatAnnotation(annotations.get(i)));
            }
            text.append(SPACE);
        }
        if (StringUtils.isNotBlank(parameter.type())) {
            text.append(parameter.type());
        }
        if (StringUtils.isNotBlank(parameter.name())) {
            if (text.length() > 0 && text.charAt(text.length() - 1) != ' ') {
                text.append(SPACE);
            }
            text.append(parameter.name());
        }
        return text.toString();
    }

    /**
     * 源码风格注解：{@code value} 可简写，其它属性写成 {@code name = "v"}。
     *
     * @param annotation 注解定义
     * @return 如 {@code @GetMapping("/{id}")}
     */
    private static String formatAnnotation(AnnotationDefinition annotation) {
        StringBuilder text = new StringBuilder();
        text.append(AT).append(annotation.name());
        List<AttributeDefinition> attributes = annotation.attributes();
        if (CollectionUtils.isEmpty(attributes)) {
            return text.toString();
        }
        text.append(LEFT_BRACKET);
        if (attributes.size() == 1 && isValueAttribute(attributes.get(0))) {
            text.append(formatValues(attributes.get(0).values()));
        } else {
            for (int i = 0; i < attributes.size(); i++) {
                if (i > 0) {
                    text.append(COMMA_SPACE);
                }
                AttributeDefinition attribute = attributes.get(i);
                text.append(attribute.name()).append(SPACE).append(EQUAL).append(SPACE);
                text.append(formatValues(attribute.values()));
            }
        }
        text.append(RIGHT_BRACKET);
        return text.toString();
    }

    /**
     * {@code value} 或未写属性名时按规范视为 value。
     *
     * @param attribute 属性
     * @return 可简写则为 {@code true}
     */
    private static boolean isValueAttribute(AttributeDefinition attribute) {
        return attribute != null && (StringUtils.isBlank(attribute.name()) || Attr.VALUE.equals(attribute.name()));
    }

    /**
     * 单值加引号；多值写成 {@code { "a", "b" }}。
     *
     * @param values 属性值
     * @return 源码风格文本
     */
    private static String formatValues(List<String> values) {
        if (CollectionUtils.isEmpty(values)) {
            return QUOTE + QUOTE;
        }
        if (values.size() == 1) {
            return quote(values.get(0));
        }
        StringBuilder text = new StringBuilder();
        text.append(LEFT_BRACE);
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                text.append(COMMA_SPACE);
            }
            text.append(quote(values.get(i)));
        }
        text.append(RIGHT_BRACE);
        return text.toString();
    }

    /**
     * 给字符串值加上双引号。
     *
     * @param value 原始值
     * @return 带引号的值
     */
    private static String quote(String value) {
        return QUOTE + (value == null ? EMPTY : value) + QUOTE;
    }
}
