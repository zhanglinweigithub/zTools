package com.zhanglinwei.zTools.doc.handler;

import com.zhanglinwei.zTools.doc.apidoc.model.FieldInfo;
import com.zhanglinwei.zTools.doc.constant.DocType;
import com.zhanglinwei.zTools.util.AssertUtils;
import com.zhanglinwei.zTools.util.JsonUtil;

import java.util.List;

import static com.zhanglinwei.zTools.util.CommonUtils.COLON;

public class HtmlDocHandler extends AbstractDocHandler {

    private final String JSON_RED = "#a72020";
    private final String JSON_BLUE = "#0451a5";
    private final String JSON_GREEN = "#0a850a";

    private final String API_STYLE = "<style type='text/css'>\n" +
            "    .json-data {\n" +
            "        white-space: pre;\n" +
            "         " +
            "        font-family: Consolas;\n" +
            "        font-size: 1rem;\n" +
            "        padding: 10px;\n" +
            "        margin: 15px;\n" +
            "        " +
            "    }\n" +
            "    body {\n" +
            "        padding-bottom: 50px;\n" +
            "    }\n" +
            "    body, td {\n" +
            "        font-family: verdana, fantasy, arial, sans-serif;\n" +
            "        font-size: 12px;\n" +
            "        line-height: 150%;\n" +
            "    }\n" +
            "    table {\n" +
            "        width: 100%;\n" +
            "        background-color: #ccc;\n" +
            "        margin: 5px 0;\n" +
            "    }\n" +
            "    td {\n" +
            "        background-color: #fff;\n" +
            "        padding: 3px 3px 3px 10px;\n" +
            "    }\n" +
            "    thead td {\n" +
            "        text-align: center;\n" +
            "        font-weight: bold;\n" +
            "        background-color: #eee;\n" +
            "    }\n" +
            "    a:link, a:visited, a:active {\n" +
            "        color: #015fb6;\n" +
            "        text-decoration: none;\n" +
            "    }\n" +
            "    a:hover {\n" +
            "        color: #e33e06;\n" +
            "    }\n" +
            "</style>";


    @Override
    public DocType support() {
        return DocType.HTML;
    }

    protected String toHtmlJson(FieldInfo fieldInfo, String jsonStr) {
        StringBuilder builder = new StringBuilder();

        // 获得注释内容
        List<String> descList = JsonUtil.buildFieldDescList(fieldInfo.getChildren());
        // 两个空格替换为四个空格
        jsonStr = jsonStr.replaceAll(" {2}", "    ");
        // 分隔JSON为行
        String[] jsonSplit = jsonStr.split("\n");
        int descIndex = 0;
        for (String lineJson : jsonSplit) {
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
                boolean needsComma = afterStr.endsWith(",");
                if (needsComma) {
                    // 移除末尾的逗号，但不设置颜色（因为后面会单独处理）
                    afterStr = afterStr.substring(0, afterStr.length() - 1);
                }
                // 设置值和颜色
                builder.append(buildSpanColor(afterStr));
                // 添加逗号
                if (needsComma) {
                    builder.append(",");
                }
                // 设置注释
                if (AssertUtils.isNotBlank(descList.get(descIndex))) {
                    String desc = descList.get(descIndex);
                    builder.append(buildSpanColor(JSON_GREEN, " // " + desc));
                }
                descIndex++;
            } else {
                builder.append(buildSpanColor(lineJson));
            }
            builder.append("<br>");
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
        if (afterStr.contains("\"") || afterStr.contains("true") || afterStr.contains("false")) {
            return JSON_BLUE;
        }
        if (afterStr.contains("0") || afterStr.contains("1")) {
            return JSON_GREEN;
        }

        return "";
    }

    @Override
    protected String templateName() {
        return "api-doc-html.ftl";
    }
}
