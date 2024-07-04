package com.zhanglinwei.zTools.template.html;

import com.zhanglinwei.zTools.constant.DocConstants;
import com.zhanglinwei.zTools.model.ClassInfo;
import com.zhanglinwei.zTools.model.FieldInfo;
import com.zhanglinwei.zTools.model.MethodInfo;
import com.zhanglinwei.zTools.model.RequestHeader;
import com.zhanglinwei.zTools.template.AbstractHtmlTemplate;
import com.zhanglinwei.zTools.util.AssertUtils;
import com.zhanglinwei.zTools.util.CommonUtils;

import java.util.List;

public class DefaultHtmlTemplate extends AbstractHtmlTemplate {

    private final String dbStyle = "<style type='text/css'>\n" +
            "body {\n" +
            "    padding-bottom: 50px\n" +
            "}\n" +
            "body,\n" +
            "td {\n" +
            "    font-family: verdana, fantasy;\n" +
            "    font-size: 12px;\n" +
            "    line-height: 150%\n" +
            "}\n" +
            "table {\n" +
            "    width: 100%;\n" +
            "    background-color: #ccc;\n" +
            "    margin: 5px 0\n" +
            "}\n" +
            "td {\n" +
            "    background-color: #fff;\n" +
            "    padding: 3px 3px 3px 10px\n" +
            "}\n" +
            "thead td {\n" +
            "    text-align: center;\n" +
            "    font-weight: bold;\n" +
            "    background-color: #eee\n" +
            "}\n" +
            "a:link,\n" +
            "a:visited,\n" +
            "a:active {\n" +
            "    color: #015fb6;\n" +
            "    text-decoration: none\n" +
            "}\n" +
            "a:hover {\n" +
            "    color: #e33e06\n" +
            "}\n" +
            "</style>";
    private final String apiStyle = "<style type='text/css'>\n" +
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
    public String buildApiContent(ClassInfo classInfo) {
        List<MethodInfo> methodList = classInfo.getMethods();
        StringBuilder builder = new StringBuilder();
        builder.append("<html lang=\"zh\">\n")
                .append("<head>\n")
                    .append("<meta charset=\"UTF-8\">\n")
                    .append("<title>").append(classInfo.getTitle()).append("|接口文档</title>\n")
                .append(apiStyle)
                .append("</head>\n")
                .append("<body style='text-align:center;'>\n")
                    .append("<h1>").append(classInfo.getTitle()).append("|接口文档</h1>\n")
                .append("<div style='width:800px; margin:20px auto; text-align:left;'>\n");
        for (int i = 0; i < methodList.size(); i++) {
            MethodInfo method = methodList.get(i);
            if (method.isEmpty()) {
                continue;
            }
            builder.append("<h2 style='line-height:50px;'>").append(method.getTitle()).append("</h2>\n")
                    .append("<h3>基本信息</h3>\n")
                    .append("<ul>\n")
                        .append("<li>接口描述: ").append(method.getDesc()).append("</li>\n")
                        .append("<li>请求方式: ").append(method.getRequestType()).append("</li>\n")
                        .append("<li>接口路径: ").append(CommonUtils.buildPath(classInfo.getRequestPath(), method.getRequestPath())).append("</li>\n")
                    .append("</ul>\n")
                    .append("<h3>请求参数</h3>\n");

            // RequestHeader
            if (AssertUtils.isNotEmpty(method.getRequestHeaders())) {
                List<RequestHeader> requestHeaders = method.getRequestHeaders();
                builder.append("<h4>").append(DocConstants.REQUEST_HEADER).append("</h4>\n");
                builder.append(super.buildRequestHeaderTable(DocConstants.REQUEST_HEADER_TABLE_HEADER, requestHeaders));
            }

            // PathVariable
            if (AssertUtils.isNotEmpty(method.getPathVariables())) {
                List<FieldInfo> pathVariables = method.getPathVariables();
                builder.append("<h4>").append(DocConstants.PATH_VARIABLE).append("</h4>\n");
                builder.append(super.buildTable(DocConstants.PATH_VARIABLE_TABLE_HEADER, pathVariables));

            }

            // RequestParam
            if (AssertUtils.isNotEmpty(method.getRequestParams())) {
                List<FieldInfo> requestQuerys = method.getRequestParams();
                builder.append("<h4>").append(DocConstants.REQUEST_PARAM).append("</h4>\n");
                builder.append(super.buildTable(DocConstants.REQUEST_PARAM_TABLE_HEADER, requestQuerys));
            }

            // Form
            if (AssertUtils.isNotEmpty(method.getFormParams())) {
                List<FieldInfo> requestBodyForms = method.getFormParams();
                builder.append("<h4>").append(DocConstants.FORM_PARAM).append("</h4>\n");
                builder.append(super.buildTable(DocConstants.FORM_PARAM_TABLE_HEADER, requestBodyForms, true));
            }

            // RequestBody
            FieldInfo requestBody = method.getRequestBody();
            if (requestBody != null && requestBody.hasChildren()) {
                builder.append("<h4>").append(DocConstants.REQUEST_BODY).append("</h4>\n");
                builder.append("<h5>描述</h5>\n");
                builder.append(super.buildTable(DocConstants.REQUEST_BODY_TABLE_HEADER, requestBody.getChildren(), true));

                builder.append("<h5>示例</h5>\n");
                builder.append("<div class=\"json-data\">\n");
                builder.append(super.toHtmlJson(method.getRequestBody(), method.getRequestBodyJson())).append("\n");
                builder.append("</div>");
            }

            builder.append("<h3>响应参数</h3>\n");
            // ResponseBody
            FieldInfo responseBody = method.getResponseBody();
            if (responseBody != null && responseBody.hasChildren()) {
                builder.append("<h4>").append(DocConstants.RESPONSE_BODY).append("</h4>\n");
                builder.append("<h5>描述</h5>\n");
                builder.append(super.buildTable(DocConstants.RESPONSE_BODY_TABLE_HEADER, responseBody.getChildren(), true));

                builder.append("<h5>示例</h5>\n");
                builder.append("<div class=\"json-data\">\n");
//                builder.append(method.getResponseBodyJson().replaceAll(" {2}", "    ")).append("\n");
                builder.append(super.toHtmlJson(method.getResponseBody(), method.getResponseBodyJson())).append("\n");
                builder.append("</div>");
            }
        }
        builder.append("</div>\n<footer></footer>\n</body>\n</html>");
        return builder.toString();
    }


}