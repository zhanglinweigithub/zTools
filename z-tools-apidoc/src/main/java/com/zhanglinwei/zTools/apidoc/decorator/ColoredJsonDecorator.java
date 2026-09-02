package com.zhanglinwei.zTools.apidoc.decorator;

import com.zhanglinwei.zTools.apidoc.formatter.ApiDocumentFormatter;

import static com.zhanglinwei.zTools.common.constant.StringPool.COLON;
import static com.zhanglinwei.zTools.common.constant.StringPool.COMMA;
import static com.zhanglinwei.zTools.common.constant.StringPool.DOUBLE_SLASH;
import static com.zhanglinwei.zTools.common.constant.StringPool.EMPTY;
import static com.zhanglinwei.zTools.common.constant.StringPool.NEWLINE;
import static com.zhanglinwei.zTools.common.constant.StringPool.SPACE;

/**
 * HTML / Word 共用的 JSON 拆行逻辑。
 * <p>
 * 输入是带缩进、且字段旁可能有 {@code // 注释} 的 pretty JSON。
 * 本类只负责：按换行切开、剥掉行尾注释、按有没有冒号分成「键值行」和「普通行」，
 * 再回调子类去写具体标记。子类不要再 split JSON。
 */
public abstract class ColoredJsonDecorator implements ApiDocumentFormatter {

    /**
     * 逐行处理：先取最后一个 {@code //} 作为注释，再按第一个冒号拆 key/value。
     * 值末尾的逗号会拆出来单独交给子类，方便分色。
     */
    @Override
    public final String decorateJson(String prettyJson) {
        StringBuilder builder = new StringBuilder();
        String[] lines = prettyJson.split(NEWLINE);

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            String comments = EMPTY;
            if (line.contains(DOUBLE_SLASH)) {
                int commentAt = line.lastIndexOf(DOUBLE_SLASH);
                comments = line.substring(commentAt);
                line = line.substring(0, commentAt - 1);
            }

            if (line.contains(COLON)) {
                String[] parts = line.split(COLON, 2);
                String value = parts[1];
                boolean needsComma = value.endsWith(COMMA);
                if (needsComma) {
                    value = value.substring(0, value.length() - 1);
                }
                appendKeyedLine(builder, parts[0], value, needsComma, comments);
            } else {
                appendPlainLine(builder, line);
            }

            if (i < lines.length - 1) {
                appendLineSeparator(builder);
            }
        }
        return builder.toString();
    }

    /**
     * 含冒号的一行，例如 {@code "id": 1,}。
     *
     * @param key        冒号左侧（含缩进和引号）
     * @param value      冒号右侧，已去掉末尾逗号
     * @param needsComma 原行值后面是否有逗号
     * @param comments   行尾 {@code // ...}，没有则为空串
     */
    protected abstract void appendKeyedLine(StringBuilder builder, String key, String value,
                                            boolean needsComma, String comments);

    /** 无冒号的行：括号、数组元素、单独的值等。 */
    protected abstract void appendPlainLine(StringBuilder builder, String line);

    /** HTML 插入 {@code <br>}；Word 每行已是段落，可空实现。 */
    protected abstract void appendLineSeparator(StringBuilder builder);

    /** 注释前补一个空格，和 JSON 正文隔开。 */
    protected static String spacedComment(String comments) {
        return SPACE + comments;
    }
}
