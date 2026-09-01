package com.zhanglinwei.zTools.apidoc.formatter;

import com.zhanglinwei.zTools.apidoc.decorator.ColoredJsonDecorator;
import com.zhanglinwei.zTools.apidoc.enums.JsonValueKind;
import com.zhanglinwei.zTools.enums.DocumentType;
import com.zhanglinwei.zTools.util.StringUtils;

import static com.zhanglinwei.zTools.constant.StringPool.COLON;
import static com.zhanglinwei.zTools.constant.StringPool.COMMA;

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

    @Override
    public DocumentType documentType() {
        return DocumentType.WORD;
    }

    @Override
    public String templateName() {
        return "api-doc-word.ftl";
    }

    /** 一整行包在一个段落里：key、冒号、值、可选逗号、可选注释各一个 run。 */
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

    /** 无冒号的行：单独一段，整行一种颜色。 */
    @Override
    protected void appendPlainLine(StringBuilder builder, String line) {
        builder.append(WordXmlHelper.paragraph(valueColor(line), line));
    }

    /**
     * Word 每行已经是独立 {@code <w:p>}，不再插入换行标记。
     */
    @Override
    protected void appendLineSeparator(StringBuilder builder) {
        // 每行已是独立 <w:p>
    }

    /** 字符串/布尔蓝、数字绿、其余黑。 */
    private static String valueColor(String value) {
        return JsonValueKind.of(value).color(BLUE, GREEN, BLACK);
    }
}
