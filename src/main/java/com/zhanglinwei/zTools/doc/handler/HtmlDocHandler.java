package com.zhanglinwei.zTools.doc.handler;

import com.zhanglinwei.zTools.doc.constant.DocConstants;
import com.zhanglinwei.zTools.doc.constant.DocType;
import com.zhanglinwei.zTools.doc.apidoc.model.ClassInfo;
import com.zhanglinwei.zTools.doc.apidoc.model.FieldInfo;
import com.zhanglinwei.zTools.doc.apidoc.model.MethodInfo;
import com.zhanglinwei.zTools.util.AssertUtils;
import com.zhanglinwei.zTools.util.CommonUtils;
import com.zhanglinwei.zTools.util.JsonUtil;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import static com.zhanglinwei.zTools.util.CommonUtils.COLON;

public class HtmlDocHandler implements DocHandler{

    private final String JSON_RED = "#a72020";
    private final String JSON_BLUE = "#0451a5";
    private final String JSON_GREEN = "#0a850a";

    private final String API_STYLE = "<style type='text/css'>\n" +
            "    .json-data {\n" +
            "        white-space: pre;\n" +
            "         "+
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
    public boolean generateApiDoc(ClassInfo classInfo, String pathName) throws IOException {
        File apiDoc = new File(pathName);
        try (OutputStreamWriter html = new OutputStreamWriter(Files.newOutputStream(apiDoc.toPath()), StandardCharsets.UTF_8)) {
            String content = makeApiContent(classInfo);
            html.write(content);
        }
        return true;
    }

    private String makeApiContent(ClassInfo classInfo) {
        List<MethodInfo> methodList = classInfo.getMethods();
        StringBuilder builder = new StringBuilder();
        builder.append("<html lang=\"zh\">\n")
                .append("<head>\n")
                .append("<meta charset=\"UTF-8\">\n")
                .append("<title>").append(classInfo.getTitle()).append("|接口文档</title>\n")
                .append(API_STYLE)
                .append("</head>\n")
                .append("<body style='text-align:center;'>\n")
                .append("<h1>").append(classInfo.getTitle()).append("|接口文档</h1>\n")
                .append("<div style='width:800px; margin:20px auto; text-align:left;'>\n");
        for (int i = 0; i < methodList.size(); i++) {
            MethodInfo method = methodList.get(i);
            if (method.isEmpty()) {
                continue;
            }
            builder.append("<h2 style='line-height:50px;'>").append(i + 1).append("、").append(method.getTitle()).append("</h2>\n")
                    .append("<h3>基本信息</h3>\n")
                    .append("<ul>\n")
                    .append("<li>接口描述: ").append(method.getDesc()).append("</li>\n")
                    .append("<li>请求方式: ").append(method.getRequestType()).append("</li>\n")
                    .append("<li>接口路径: ").append(CommonUtils.buildPath(classInfo.getRequestPath(), method.getRequestPath())).append("</li>\n")
                    .append("</ul>\n")
                    .append("<h3>请求参数</h3>\n");

            // RequestHeader
            if (AssertUtils.isNotEmpty(method.getRequestHeaders())) {
                List<FieldInfo> requestHeaders = method.getRequestHeaders();
                builder.append("<h4>").append(DocConstants.REQUEST_HEADER).append("</h4>\n");
                builder.append(buildTable(DocConstants.COMMON_TABLE_HEADER, requestHeaders));
            }

            // PathVariable
            if (AssertUtils.isNotEmpty(method.getPathVariables())) {
                List<FieldInfo> pathVariables = method.getPathVariables();
                builder.append("<h4>").append(DocConstants.PATH_VARIABLE).append("</h4>\n");
                builder.append(buildTable(DocConstants.COMMON_TABLE_HEADER, pathVariables));

            }

            // RequestParam
            if (AssertUtils.isNotEmpty(method.getRequestParams())) {
                List<FieldInfo> requestQuerys = method.getRequestParams();
                builder.append("<h4>").append(DocConstants.REQUEST_PARAM).append("</h4>\n");
                builder.append(buildTable(DocConstants.COMMON_TABLE_HEADER, requestQuerys));
            }

            // Form
            if (AssertUtils.isNotEmpty(method.getFormParams())) {
                List<FieldInfo> requestBodyForms = method.getFormParams();
                builder.append("<h4>").append(DocConstants.FORM_PARAM).append("</h4>\n");
                builder.append(buildTable(DocConstants.COMMON_TABLE_HEADER, requestBodyForms, true));
            }

            // RequestBody
            FieldInfo requestBody = method.getRequestBody();
            if (requestBody != null && requestBody.hasChildren()) {
                builder.append("<h4>").append(DocConstants.REQUEST_BODY).append("</h4>\n");
                builder.append("<b>描述</b>\n");
                builder.append(buildTable(DocConstants.BODY_TABLE_HEADER, requestBody.getChildren(), true));

                builder.append("<b>示例</b>\n");
                builder.append("<div class=\"json-data\">\n");
                builder.append(toHtmlJson(method.getRequestBody(), method.getRequestBodyJson())).append("\n");
                builder.append("</div>");
            }

            builder.append("<h3>响应参数</h3>\n");
            // ResponseBody
            FieldInfo responseBody = method.getResponseBody();
            if (responseBody != null && responseBody.hasChildren()) {
                builder.append("<h4>").append(DocConstants.RESPONSE_BODY).append("</h4>\n");
                builder.append("<b>描述</b>\n");
                builder.append(buildTable(DocConstants.BODY_TABLE_HEADER, responseBody.getChildren(), true));

                builder.append("<b>示例</b>\n");
                builder.append("<div class=\"json-data\">\n");
                builder.append(toHtmlJson(method.getResponseBody(), method.getResponseBodyJson())).append("\n");
                builder.append("</div>");
            }
        }
        builder.append("</div>\n<footer></footer>\n</body>\n</html>");
        return builder.toString();
    }

    @Override
    public boolean generateDataBaseDoc() {
        return false;
    }

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

    private String buildTable(List<String> tableHeaderList, List<FieldInfo> contentList) {
        return buildTable(tableHeaderList, contentList, false);
    }

    private String buildTable(List<String> tableHeaderList, List<FieldInfo> contentList, boolean withType) {
        return "<table cellspacing='1'>" + buildTableHeader(tableHeaderList) + buildFieldTableContent(contentList, withType) + "</table\n>";
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
