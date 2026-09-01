package com.zhanglinwei.zTools.apidoc.formatter;

import com.zhanglinwei.zTools.apidoc.decorator.ColoredJsonDecorator;
import com.zhanglinwei.zTools.apidoc.enums.JsonValueKind;
import com.zhanglinwei.zTools.enums.DocumentType;
import com.zhanglinwei.zTools.util.StringUtils;

import static com.zhanglinwei.zTools.constant.StringPool.COLON;
import static com.zhanglinwei.zTools.constant.StringPool.COMMA;
import static com.zhanglinwei.zTools.constant.StringPool.EMPTY;

/**
 * HTML 导出：模板 {@code api-doc-html.ftl}。
 * <p>
 * 父类按行拆 JSON，本类把 key / value / 注释写成带颜色的 {@code <span>}，
 * 行与行之间用 {@code <br>}。颜色带 {@code #}，与 CSS 一致。
 */
public class HtmlApiDocumentFormatter extends ColoredJsonDecorator {

    /** 字段名（JSON key）。 */
    private static final String RED = "#a72020";
    /** 字符串、布尔值。 */
    private static final String BLUE = "#0451a5";
    /** 数字，以及行尾 {@code // 注释}。 */
    private static final String GREEN = "#0a850a";

    @Override
    public DocumentType documentType() {
        return DocumentType.HTML;
    }

    @Override
    public String templateName() {
        return "api-doc-html.ftl";
    }

    /** 形如 {@code "name": "zlw",} 的一行：key 红、值按类型着色、逗号和注释跟在后面。 */
    @Override
    protected void appendKeyedLine(StringBuilder builder, String key, String value,
                                   boolean needsComma, String comments) {
        builder.append(span(RED, key)).append(COLON).append(span(valueColor(value), value));
        if (needsComma) {
            builder.append(COMMA);
        }
        if (StringUtils.isNotBlank(comments)) {
            builder.append(span(GREEN, spacedComment(comments)));
        }
    }

    /** 没有冒号的行，例如 {@code {} }、{@code [}、纯值。 */
    @Override
    protected void appendPlainLine(StringBuilder builder, String line) {
        builder.append(span(valueColor(line), line));
    }

    /** HTML 里不用真实换行，用 {@code <br>} 折行。 */
    @Override
    protected void appendLineSeparator(StringBuilder builder) {
        builder.append("<br>");
    }

    /** 单层 span，颜色写在 style 里，避免依赖外部 CSS。 */
    private static String span(String color, String content) {
        return "<span style='color:" + color + "'>" + content + "</span>";
    }

    /** 字符串/布尔蓝、数字绿、其余不加色（EMPTY 会得到 {@code color:} 空值，浏览器走默认色）。 */
    private static String valueColor(String value) {
        return JsonValueKind.of(value).color(BLUE, GREEN, EMPTY);
    }
}
