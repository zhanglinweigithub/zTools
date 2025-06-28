package com.zhanglinwei.zTools.doc.handler;


import com.zhanglinwei.zTools.doc.constant.DocType;
import com.zhanglinwei.zTools.util.AssertUtils;

import static com.zhanglinwei.zTools.common.constants.SpringPool.*;

public class WordDocHandler extends AbstractDocHandler {

    private static final String JSON_RED = "a72020";
    private static final String JSON_BLUE = "0451a5";
    private static final String JSON_GREEN = "0a850a";
    private static final String JSON_BLACK = "000000";

    private static final String SIMPLE_PARAGRAPH_EXPRESSION = "<w:p w14:paraId=\"320F952D\"><w:pPr><w:rPr><w:rFonts w:hint=\"default\" w:ascii=\"Times New Roman\" w:hAnsi=\"Times New Roman\" w:cs=\"Times New Roman\" /></w:rPr></w:pPr><w:r><w:rPr><w:rFonts w:hint=\"default\" w:ascii=\"Times New Roman\" w:hAnsi=\"Times New Roman\" w:cs=\"Times New Roman\" /><w:color w:val=\"%s\" /></w:rPr><w:t xml:space=\"preserve\">%s</w:t></w:r></w:p>";

    private static final String PARAGRAPH_OPEN = "<w:p w14:paraId=\"7C11989A\"><w:pPr><w:rPr><w:rFonts w:hint=\"default\" w:ascii=\"Times New Roman\" w:hAnsi=\"Times New Roman\" w:cs=\"Times New Roman\" /></w:rPr></w:pPr>";
    private static final String RECORD_EXPRESSION = "<w:r><w:rPr><w:rFonts w:hint=\"default\" w:ascii=\"Times New Roman\" w:hAnsi=\"Times New Roman\" w:cs=\"Times New Roman\" /><w:color w:val=\"%s\" /></w:rPr><w:t xml:space=\"preserve\">%s</w:t></w:r>";
    private static final String PARAGRAPH_CLOSE = "</w:p>";

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
                builder.append(PARAGRAPH_OPEN);

                // 写入key并设置颜色
                String[] split = lineJson.split(COLON, 2);
                builder.append(createRecord(JSON_RED, split[0]));

                // 写入:并设置颜色
                builder.append(createRecord(JSON_BLACK, COLON));

                // 处理值
                String afterStr = split[1];

                // 是否需要添加逗号
                boolean needsComma = afterStr.endsWith(COMMA);
                if (needsComma) {
                    // 移除末尾的逗号，但不设置颜色（因为后面会单独处理）
                    afterStr = afterStr.substring(0, afterStr.length() - 1);
                }

                // 写入值并设置颜色
                builder.append(createRecord(createColor(afterStr), afterStr));

                // 如果需要添加逗号
                if (needsComma) {
                    builder.append(createRecord(JSON_BLACK, COMMA));
                }

                // 写入注释并设置颜色
                if (AssertUtils.isNotBlank(comments)) {
                    builder.append(createRecord(JSON_GREEN, SPACE + comments));
                }

                builder.append(PARAGRAPH_CLOSE);
            } else {
                builder.append(createSimpleParagraph(lineJson));
            }
        }

        return builder.toString();
    }

    private String createRecord(String color, String content) {
        return String.format(RECORD_EXPRESSION, color, content);
    }

    private String createSimpleParagraph(String content) {
        return String.format(SIMPLE_PARAGRAPH_EXPRESSION, createColor(content), content);
    }

    /**
     * 字符串、Boolean类型: BLUE
     * 数字类型: GREEN
     * 其它: 默认
     */
    private String createColor(String afterStr) {
        String trimmed = afterStr.trim();

        if (trimmed.startsWith("\"") || trimmed.startsWith(SINGLE_QUOTE) || trimmed.startsWith(TRUE) || trimmed.startsWith(FALSE)) {
            return JSON_BLUE;
        }
        if (trimmed.startsWith(ZERO) || trimmed.startsWith(ONE)) {
            return JSON_GREEN;
        }

        return JSON_BLACK;
    }

    @Override
    protected String apiTemplateName() {
        return "api-doc-word.ftl";
    }

    @Override
    protected String dbTemplateName() {
        return "db-doc-word.ftl";
    }

    @Override
    public DocType support() {
        return DocType.WORD;
    }
}
