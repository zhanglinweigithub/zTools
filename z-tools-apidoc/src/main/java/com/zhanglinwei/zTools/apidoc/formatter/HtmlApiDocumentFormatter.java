package com.zhanglinwei.zTools.apidoc.formatter;

import com.zhanglinwei.zTools.apidoc.decorator.ColoredJsonDecorator;
import com.zhanglinwei.zTools.apidoc.enums.JsonValueKind;
import com.zhanglinwei.zTools.common.enums.DocumentType;
import com.zhanglinwei.zTools.common.util.StringUtils;

import static com.zhanglinwei.zTools.common.constant.StringPool.COLON;
import static com.zhanglinwei.zTools.common.constant.StringPool.COMMA;
import static com.zhanglinwei.zTools.common.constant.StringPool.EMPTY;

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

    /**
     * @return {@link DocumentType#HTML}
     */
    @Override
    public DocumentType documentType() {
        return DocumentType.HTML;
    }

    /**
     * @return {@code api-doc-html.ftl}
     */
    @Override
    public String templateName() {
        return "api-doc-html.ftl";
    }

    /**
     * 形如 {@code "name": "zlw",} 的一行：key 红、值按类型着色、逗号和注释跟在后面。
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
        builder.append(span(RED, key)).append(COLON).append(span(valueColor(value), value));
        if (needsComma) {
            builder.append(COMMA);
        }
        if (StringUtils.isNotBlank(comments)) {
            builder.append(span(GREEN, spacedComment(comments)));
        }
    }

    /**
     * 没有冒号的行，例如 {@code {} }、{@code [}、纯值。
     *
     * @param builder 输出缓冲
     * @param line    整行文本
     */
    @Override
    protected void appendPlainLine(StringBuilder builder, String line) {
        builder.append(span(valueColor(line), line));
    }

    /**
     * HTML 里不用真实换行，用 {@code <br>} 折行。
     *
     * @param builder 输出缓冲
     */
    @Override
    protected void appendLineSeparator(StringBuilder builder) {
        builder.append("<br>");
    }

    /**
     * 单层 span，颜色写在 style 里，避免依赖外部 CSS。
     *
     * @param color   带 {@code #} 的色值
     * @param content 文本
     * @return {@code <span style='color:...'>...</span>}
     */
    private static String span(String color, String content) {
        return "<span style='color:" + color + "'>" + content + "</span>";
    }

    /**
     * 字符串/布尔蓝、数字绿、其余不加色（EMPTY 会得到 {@code color:} 空值，浏览器走默认色）。
     *
     * @param value 冒号右侧或整行文本
     * @return CSS 颜色；无法识别时为空串
     */
    private static String valueColor(String value) {
        return JsonValueKind.of(value).color(BLUE, GREEN, EMPTY);
    }
}
