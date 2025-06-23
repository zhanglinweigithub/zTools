package com.zhanglinwei.zTools.doc.handler;

import com.zhanglinwei.zTools.doc.constant.DocType;
import com.zhanglinwei.zTools.util.AssertUtils;

import static com.zhanglinwei.zTools.common.constants.SpringPool.*;

public class HtmlDocHandler extends AbstractDocHandler {

    private final String JSON_RED = "#a72020";
    private final String JSON_BLUE = "#0451a5";
    private final String JSON_GREEN = "#0a850a";

    @Override
    public DocType support() {
        return DocType.HTML;
    }

    @Override
    protected String decorateJsonString(String prettyString) {
        StringBuilder builder = new StringBuilder();

        // 分隔JSON为行
        String[] jsonSplit = prettyString.split(NEWLINE);

        for (int i = 0; i < jsonSplit.length; i++) {
            String lineJson = jsonSplit[i];

            // 提取注释
            String comments = EMPTY;
            if (lineJson.contains(DOUBLE_SLASH)) {
                int commentIndexOf = lineJson.lastIndexOf(DOUBLE_SLASH);
                comments = lineJson.substring(commentIndexOf);
                lineJson = lineJson.substring(0, commentIndexOf - 1);
            }

            // 检查行是否包含冒号
            if (lineJson.contains(COLON)) {
                // 写入key并设置颜色
                String[] split = lineJson.split(COLON, 2);
                builder.append(buildSpanColor(JSON_RED, split[0]));

                // 写入:并设置颜色
                builder.append(COLON);

                // 处理值
                String afterStr = split[1];

                // 是否需要添加逗号
                boolean needsComma = afterStr.endsWith(COMMA);
                if (needsComma) {
                    // 移除末尾的逗号，但不设置颜色（因为后面会单独处理）
                    afterStr = afterStr.substring(0, afterStr.length() - 1);
                }

                // 写入值并设置颜色
                builder.append(buildSpanColor(afterStr));

                // 如果需要添加逗号
                if (needsComma) {
                    builder.append(COMMA);
                }

                // 写入注释并设置颜色
                if (AssertUtils.isNotBlank(comments)) {
                    builder.append(buildSpanColor(JSON_GREEN, SPACE + comments));
                }
            } else {
                builder.append(buildSpanColor(lineJson));
            }

            // 添加换行，最后一行不添加
            if (i < jsonSplit.length - 1) {
                builder.append("<br>");
            }
        }

        return builder.toString();
    }

    private String buildSpanColor(String color, String content) {
        return "<span style='color:" + color + "'>" + content + "</span>";
    }

    private String buildSpanColor(String content) {
        return "<span style='color:" + buildColor(content) + "'>" + content + "</span>";
    }

    /**
     * 字符串、Boolean类型: BLUE
     * 数字类型: GREEN
     * 其它: 默认
     */
    private String buildColor(String afterStr) {
        String trimmed = afterStr.trim();

        if (trimmed.startsWith("\"") || trimmed.startsWith(SINGLE_QUOTE) || trimmed.startsWith(TRUE) || trimmed.startsWith(FALSE)) {
            return JSON_BLUE;
        }
        if (trimmed.startsWith(ZERO) || trimmed.startsWith(ONE)) {
            return JSON_GREEN;
        }

        return EMPTY;
    }

    @Override
    protected String templateName() {
        return "api-doc-html.ftl";
    }
}
