package com.zhanglinwei.zTools.apidoc.formatter;

import com.zhanglinwei.zTools.apidoc.decorator.ColoredJsonDecorator;
import com.zhanglinwei.zTools.apidoc.enums.JsonValueKind;
import com.zhanglinwei.zTools.common.enums.DocumentType;
import com.zhanglinwei.zTools.common.util.StringUtils;

import static com.zhanglinwei.zTools.common.constant.StringPool.COLON;
import static com.zhanglinwei.zTools.common.constant.StringPool.COMMA;

/**
 * Word 导出：模板 {@code api-doc-word.ftl}（OOXML）。
 * <p>
 * 父类按行拆 JSON，本类把每一行写成独立 {@code <w:p>}，行内用 {@link WordXmlHelper#run} 分色。
 * Word 的 {@code w:color} 不带 {@code #}。
 */
public class WordApiDocumentFormatter extends ColoredJsonDecorator {

    /** 字段名。 */
    private static final String RED = "a72020";
    /** 字符串、布尔。 */
    private static final String BLUE = "0451a5";
    /** 数字和行尾注释。 */
    private static final String GREEN = "0a850a";
    /** 冒号、逗号，以及无法识别的值。 */
    private static final String BLACK = "000000";

    /**
     * @return {@link DocumentType#WORD}
     */
    @Override
    public DocumentType documentType() {
        return DocumentType.WORD;
    }

    /**
     * @return {@code api-doc-word.ftl}
     */
    @Override
    public String templateName() {
        return "api-doc-word.ftl";
    }

    /**
     * 一整行包在一个段落里：key、冒号、值、可选逗号、可选注释各一个 run。
     *
     * @param builder    输出缓冲
     * @param key        冒号左侧
     * @param value      冒号右侧，已去掉末尾逗号
     * @param needsComma 原行值后面是否有逗号
     * @param comments   行尾 {@code // ...}，没有则为空串
     */
    @Override
    protected void appendKeyedLine(StringBuilder builder, String key, String value,
                                   boolean needsComma, String comments) {
        builder.append(WordXmlHelper.PARAGRAPH_OPEN);
        builder.append(WordXmlHelper.run(RED, key));
        builder.append(WordXmlHelper.run(BLACK, COLON));
        builder.append(WordXmlHelper.run(valueColor(value), value));
        if (needsComma) {
            builder.append(WordXmlHelper.run(BLACK, COMMA));
        }
        if (StringUtils.isNotBlank(comments)) {
            builder.append(WordXmlHelper.run(GREEN, spacedComment(comments)));
        }
        builder.append(WordXmlHelper.PARAGRAPH_CLOSE);
    }

    /**
     * 无冒号的行：单独一段，整行一种颜色。
     *
     * @param builder 输出缓冲
     * @param line    整行文本
     */
    @Override
    protected void appendPlainLine(StringBuilder builder, String line) {
        builder.append(WordXmlHelper.paragraph(valueColor(line), line));
    }

    /**
     * Word 每行已经是独立 {@code <w:p>}，不再插入换行标记。
     *
     * @param builder 输出缓冲（本实现不写入）
     */
    @Override
    protected void appendLineSeparator(StringBuilder builder) {
        // 每行已是独立 <w:p>
    }

    /**
     * 字符串/布尔蓝、数字绿、其余黑。
     *
     * @param value 冒号右侧或整行文本
     * @return 不带 {@code #} 的 hex
     */
    private static String valueColor(String value) {
        return JsonValueKind.of(value).color(BLUE, GREEN, BLACK);
    }
}
