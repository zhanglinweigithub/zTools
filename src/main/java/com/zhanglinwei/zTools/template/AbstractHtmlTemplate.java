package com.zhanglinwei.zTools.template;

import com.zhanglinwei.zTools.model.*;
import com.zhanglinwei.zTools.util.AssertUtils;
import com.zhanglinwei.zTools.util.CommonUtils;
import com.zhanglinwei.zTools.util.JsonUtil;

import java.util.List;

import static com.zhanglinwei.zTools.util.CommonUtils.COLON;

public abstract class AbstractHtmlTemplate {

    private final String JSON_RED = "#a72020";
    private final String JSON_BLUE = "#0451a5";
    private final String JSON_GREEN = "#0a850a";

    private final String openTable = "<table cellspacing='1'>";
    private final String closeTable = "</table\n>";

    public abstract String buildApiContent(ClassInfo classInfo);

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

    protected String buildTable(List<String> tableHeaderList, List<FieldInfo> contentList) {
        return buildTable(tableHeaderList, contentList, false);
    }

    protected String buildDBTable(List<String> dbTableHeader, List<TableInfo> tableInfoList) {
        return openTable + buildTableHeader(dbTableHeader) + buildColumTableContent(tableInfoList) + closeTable;
    }

    protected String buildTable(List<String> tableHeaderList, List<FieldInfo> contentList, boolean withType) {
        return openTable + buildTableHeader(tableHeaderList) + buildFieldTableContent(contentList, withType) + closeTable;
    }

    protected String buildRequestHeaderTable(List<String> tableHeaderList, List<RequestHeader> requestHeaders) {
        return openTable + buildTableHeader(tableHeaderList) + buildRequestHeaderTableContent(requestHeaders) + closeTable;
    }

    private String buildDivColor(String color, String content) {
        return "<div style='color:" + color + "'>" + content + "</div>";
    }

    private String buildDivColor(String content) {
        return "<div style='color:" + buildColor(content) + "'>" + content + "</div>";
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

    private String buildRequestHeaderTableContent(List<RequestHeader> requestHeaders) {
        if (AssertUtils.isNotEmpty(requestHeaders)) {

            StringBuilder builder = new StringBuilder();
            for (RequestHeader header : requestHeaders) {
                builder.append(header.toHtmlString());
            }

            return builder.toString();
        }

        return "";
    }

    private String buildTableHeader(List<String> tableHeaderList) {
        if (AssertUtils.isNotEmpty(tableHeaderList)) {
            StringBuilder builder = new StringBuilder();
            builder.append("<thead>\n")
                    .append("<tr>\n");
            for (String header : tableHeaderList) {
                builder.append("<td>").append(header).append("</td>\n");
            }
            builder.append("</tr>\n")
                    .append("</thead>\n");

            return builder.toString();
        }

        return "";
    }

    private String buildColumTableContent(List<TableInfo> tableInfoList) {
        StringBuilder builder = new StringBuilder();
        for (TableInfo tableInfo : tableInfoList) {
            builder.append(tableInfo.toHtmlString());
        }

        return builder.toString();
    }

    private String buildFieldTableContent(List<FieldInfo> fieldInfoList, boolean withType) {
        if (AssertUtils.isNotEmpty(fieldInfoList)) {
            StringBuilder builder = new StringBuilder();
            for (FieldInfo fieldInfo : fieldInfoList) {
                builder.append(fieldInfo.toHtmlString(withType));
                if (fieldInfo.hasChildren()) {
                    for (FieldInfo field : fieldInfo.getChildren()) {
                        builder.append(buildChildrenFieldInfo(field, CommonUtils.getPrefix(), withType));
                    }
                }
            }
            return builder.toString();
        }

        return "";
    }

    private String buildChildrenFieldInfo(FieldInfo field, String prefix, boolean withType) {
        StringBuilder builder = new StringBuilder();
        builder.append(field.toHtmlString(withType, prefix));
        if (field.hasChildren()) {
            for (FieldInfo fieldInfo : field.getChildren()) {
                builder.append(buildChildrenFieldInfo(fieldInfo, prefix + CommonUtils.getPrefix(), withType));
            }
        }
        return builder.toString();
    }

}
