package com.zhanglinwei.zTools.annotation.parse;

import com.intellij.lang.ASTNode;
import com.intellij.psi.JavaTokenType;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaDocumentedElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.TokenType;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import com.zhanglinwei.zTools.annotation.model.CommentDefinition;
import com.zhanglinwei.zTools.common.constant.StringPool;
import com.zhanglinwei.zTools.common.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 读取 JavaDoc、行注释 {@code //}、块注释 {@code /* *}{@code /}。
 * 库依赖字段先跳到源码 PSI（{@code getNavigationElement}），否则 class 文件上没有注释。
 * 不与注解说明合并。{@code @param} / {@code @return} 分别挂到参数和返回值上。
 */
public final class CommentParser {

    /** 工具类，禁止实例化。 */
    private CommentParser() {}

    /**
     * 读取元素注释：JavaDoc 正文与 {@code @description}，没有 JavaDoc 时再用 {@code //} / {@code /* *}{@code /}。
     * 库类型会先解析到源码元素再读。
     *
     * @param element 带文档的 PSI 元素
     * @return 注释定义；没有任何注释时为 {@code null}
     */
    public static CommentDefinition of(PsiJavaDocumentedElement element) {
        if (element == null) {
            return null;
        }
        PsiJavaDocumentedElement source = sourceOf(element);
        CommentDefinition fromDoc = fromJavaDoc(source);
        if (fromDoc != null) {
            return fromDoc;
        }
        String fromSiblings = siblingComment(source);
        if (StringUtils.isBlank(fromSiblings)) {
            return null;
        }
        return new CommentDefinition(fromSiblings, null);
    }

    /**
     * 方法 {@code @return} 标签；未写则为 {@code null}。
     *
     * @param method 方法 PSI
     * @return {@code @return} 正文
     */
    public static String returnComment(PsiMethod method) {
        if (method == null) {
            return null;
        }
        PsiMethod source = sourceMethod(method);
        if (source.getDocComment() == null) {
            return null;
        }
        for (PsiDocTag tag : source.getDocComment().getTags()) {
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
        if (method == null) {
            return Collections.emptyMap();
        }
        PsiMethod source = sourceMethod(method);
        if (source.getDocComment() == null) {
            return Collections.emptyMap();
        }
        Map<String, String> params = new LinkedHashMap<String, String>();
        for (PsiDocTag tag : source.getDocComment().getTags()) {
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

    /**
     * 字段等成员用于展示的注释：JavaDoc 正文 → {@code @description} → 行/块注释。
     *
     * @param element 带文档的 PSI 元素
     * @return 注释文本；没有则为 {@code null}
     */
    public static String text(PsiJavaDocumentedElement element) {
        CommentDefinition comment = of(element);
        if (comment == null) {
            return null;
        }
        if (StringUtils.isNotBlank(comment.text())) {
            return comment.text();
        }
        return comment.description();
    }

    /**
     * 有源码时跳到源码 PSI，才能读到依赖 jar 里字段的注释。
     * compiled 元素和源码元素实现类不同，只能按接口判断，不能用 {@code getClass().isInstance}。
     *
     * @param element 原始 PSI（可能是 class 文件里的 compiled 元素）
     * @return 源码元素；没有源码时仍返回原元素
     */
    private static PsiJavaDocumentedElement sourceOf(PsiJavaDocumentedElement element) {
        PsiElement navigation = element.getNavigationElement();
        return navigation instanceof PsiJavaDocumentedElement
                ? (PsiJavaDocumentedElement) navigation
                : element;
    }

    /**
     * 方法跳到源码 PSI，以便读取依赖里的 {@code @param} / {@code @return}。
     *
     * @param method 原始方法（可能是 compiled）
     * @return 源码方法；没有源码时仍返回原方法
     */
    private static PsiMethod sourceMethod(PsiMethod method) {
        PsiElement navigation = method.getNavigationElement();
        return navigation instanceof PsiMethod ? (PsiMethod) navigation : method;
    }

    /**
     * 只读 JavaDoc；没有或内容为空则为 {@code null}。
     *
     * @param element 源码或 compiled 元素
     * @return JavaDoc 注释定义
     */
    private static CommentDefinition fromJavaDoc(PsiJavaDocumentedElement element) {
        PsiDocComment doc = element.getDocComment();
        if (doc == null) {
            return null;
        }
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
     * 字段上方的 {@code //} / {@code /* *}{@code /}，以及同一行末尾的行注释。
     * <p>
     * 必须走 AST（{@code getNode()}），不能用 stub 上的 {@code getPrevSibling()}：
     * Java 文件默认是 stub PSI，注释不在 stub 里，兄弟节点会直接跳到上一个字段。
     *
     * @param element 字段 / 方法 / 类
     * @return 拼接后的注释；没有则为 {@code null}
     */
    private static String siblingComment(PsiElement element) {
        ASTNode node = element.getNode();
        List<String> preceding = new ArrayList<String>();
        String trailing;
        if (node != null) {
            preceding.addAll(precedingFromTree(node));
            collectInnerPreceding(node, preceding);
            trailing = trailingFromChildren(node);
            if (trailing == null) {
                trailing = trailingFromTree(node);
            }
        } else {
            preceding.addAll(precedingFromPsi(element));
            trailing = trailingFromPsi(element);
        }
        if (preceding.isEmpty() && trailing == null) {
            return null;
        }
        List<String> parts = new ArrayList<String>(preceding);
        if (trailing != null) {
            parts.add(trailing);
        }
        return String.join(StringPool.SPACE, parts);
    }

    /**
     * AST 上、元素前方的行/块注释（类体里字段的上一个兄弟）。
     *
     * @param node 字段 / 方法 AST
     * @return 从上到下的注释
     */
    private static List<String> precedingFromTree(ASTNode node) {
        List<String> comments = new ArrayList<String>();
        ASTNode current = skipWhitespaceBackward(node.getTreePrev());
        while (current != null && isLineOrBlockComment(current)) {
            if (belongsToPreviousMember(current)) {
                break;
            }
            String text = commentText(current);
            if (text != null) {
                comments.add(text);
            }
            current = skipWhitespaceBackward(current.getTreePrev());
        }
        Collections.reverse(comments);
        return comments;
    }

    /**
     * 字段节点内部开头、以及修饰符列表开头的行/块注释。
     * {@code //} 经常挂在 FIELD / MODIFIER_LIST 的子节点上，而不是类体兄弟。
     *
     * @param node      字段 AST
     * @param preceding 追加到此列表
     */
    private static void collectInnerPreceding(ASTNode node, List<String> preceding) {
        for (ASTNode child = node.getFirstChildNode(); child != null; child = child.getTreeNext()) {
            if (isWhitespace(child)) {
                continue;
            }
            if (child.getPsi() instanceof PsiModifierList) {
                collectLeadingComments(child, preceding);
                return;
            }
            if (isLineOrBlockComment(child)) {
                String text = commentText(child);
                if (text != null) {
                    preceding.add(text);
                }
                continue;
            }
            return;
        }
    }

    /**
     * 修饰符列表里、第一个注解或关键字之前的注释。
     *
     * @param modifierList 修饰符列表 AST
     * @param preceding    追加到此列表
     */
    private static void collectLeadingComments(ASTNode modifierList, List<String> preceding) {
        for (ASTNode child = modifierList.getFirstChildNode(); child != null; child = child.getTreeNext()) {
            if (isWhitespace(child)) {
                continue;
            }
            if (isLineOrBlockComment(child)) {
                String text = commentText(child);
                if (text != null) {
                    preceding.add(text);
                }
                continue;
            }
            return;
        }
    }

    /**
     * 分号之后、同一行上的行/块注释（作为字段子节点）。
     *
     * @param node 字段 AST
     * @return 行尾注释
     */
    private static String trailingFromChildren(ASTNode node) {
        boolean afterSemicolon = false;
        for (ASTNode child = node.getFirstChildNode(); child != null; child = child.getTreeNext()) {
            if (!afterSemicolon) {
                if (child.getElementType() == JavaTokenType.SEMICOLON) {
                    afterSemicolon = true;
                }
                continue;
            }
            if (isWhitespace(child)) {
                if (child.getText().indexOf('\n') >= 0) {
                    return null;
                }
                continue;
            }
            return isLineOrBlockComment(child) ? commentText(child) : null;
        }
        return null;
    }

    /**
     * 同一行、AST 上元素后方的行/块注释。
     *
     * @param node 字段 AST
     * @return 行尾注释
     */
    private static String trailingFromTree(ASTNode node) {
        ASTNode current = node.getTreeNext();
        while (current != null && isWhitespace(current)) {
            if (current.getText().indexOf('\n') >= 0) {
                return null;
            }
            current = current.getTreeNext();
        }
        return current != null && isLineOrBlockComment(current) ? commentText(current) : null;
    }

    /**
     * stub 不可用时的 PSI 回退：元素前方的行/块注释。
     *
     * @param element 当前成员
     * @return 从上到下的注释
     */
    private static List<String> precedingFromPsi(PsiElement element) {
        List<String> comments = new ArrayList<String>();
        PsiElement current = element.getPrevSibling();
        while (current instanceof PsiWhiteSpace) {
            current = current.getPrevSibling();
        }
        while (current instanceof PsiComment) {
            PsiComment comment = (PsiComment) current;
            if (comment instanceof PsiDocComment) {
                break;
            }
            String text = cleanLineOrBlock(comment.getText());
            if (text != null) {
                comments.add(text);
            }
            current = current.getPrevSibling();
            while (current instanceof PsiWhiteSpace) {
                current = current.getPrevSibling();
            }
        }
        Collections.reverse(comments);
        return comments;
    }

    /**
     * stub 不可用时的 PSI 回退：同一行末尾注释。
     *
     * @param element 当前成员
     * @return 行尾注释
     */
    private static String trailingFromPsi(PsiElement element) {
        PsiElement current = element.getNextSibling();
        while (current instanceof PsiWhiteSpace) {
            if (current.getText().indexOf('\n') >= 0) {
                return null;
            }
            current = current.getNextSibling();
        }
        if (!(current instanceof PsiComment) || current instanceof PsiDocComment) {
            return null;
        }
        return cleanLineOrBlock(current.getText());
    }

    /**
     * 注释是否是上一成员的行尾注释（中间没有换行）。
     *
     * @param comment 待判定 AST 注释
     * @return 属于上一成员则为 {@code true}
     */
    private static boolean belongsToPreviousMember(ASTNode comment) {
        ASTNode prev = comment.getTreePrev();
        while (prev != null && isWhitespace(prev)) {
            if (prev.getText().indexOf('\n') >= 0) {
                return false;
            }
            prev = prev.getTreePrev();
        }
        return prev != null;
    }

    /**
     * 向前跳过空白。
     *
     * @param node 起点
     * @return 第一个非空白节点；没有则为 {@code null}
     */
    private static ASTNode skipWhitespaceBackward(ASTNode node) {
        ASTNode current = node;
        while (current != null && isWhitespace(current)) {
            current = current.getTreePrev();
        }
        return current;
    }

    /**
     * 是否空白节点。
     *
     * @param node AST 节点
     * @return 是空白则为 {@code true}
     */
    private static boolean isWhitespace(ASTNode node) {
        return node != null && (node.getElementType() == TokenType.WHITE_SPACE
                || node.getPsi() instanceof PsiWhiteSpace);
    }

    /**
     * 是否 {@code //} 或 {@code /* *}{@code /}（不含 JavaDoc）。
     *
     * @param node AST 节点
     * @return 是行/块注释则为 {@code true}
     */
    private static boolean isLineOrBlockComment(ASTNode node) {
        if (node == null || node.getPsi() instanceof PsiDocComment) {
            return false;
        }
        return node.getElementType() == JavaTokenType.END_OF_LINE_COMMENT
                || node.getElementType() == JavaTokenType.C_STYLE_COMMENT
                || node.getPsi() instanceof PsiComment;
    }

    /**
     * 从 AST 注释节点取出清洗后的正文。
     *
     * @param node 注释节点
     * @return 正文；空白则为 {@code null}
     */
    private static String commentText(ASTNode node) {
        return cleanLineOrBlock(node.getText());
    }

    /**
     * 清洗 {@code //} 或 {@code /* *}{@code /}，去掉注释标记和行首星号。
     *
     * @param raw 原始注释文本
     * @return 正文；空白则为 {@code null}
     */
    private static String cleanLineOrBlock(String raw) {
        if (raw == null) {
            return null;
        }
        String text = raw.trim();
        if (text.startsWith("//")) {
            text = text.substring(2);
        } else if (text.startsWith("/*") && text.endsWith("*/") && text.length() >= 4) {
            text = text.substring(2, text.length() - 2);
        }
        text = text.replaceAll("(?m)^\\s*\\*\\s?", StringPool.EMPTY);
        text = text.replaceAll("\\s+", StringPool.SPACE).trim();
        return StringUtils.isBlank(text) ? null : text;
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
