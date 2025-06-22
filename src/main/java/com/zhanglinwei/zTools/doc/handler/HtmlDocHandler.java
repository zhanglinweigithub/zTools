package com.zhanglinwei.zTools.doc.handler;

import com.zhanglinwei.zTools.doc.apidoc.model.ApiInfo;
import com.zhanglinwei.zTools.doc.constant.DocType;
import com.zhanglinwei.zTools.util.AssertUtils;

import java.util.Collection;

import static com.zhanglinwei.zTools.common.constants.SpringPool.COLON;
import static com.zhanglinwei.zTools.common.constants.SpringPool.DOUBLE_SLASH;

public class HtmlDocHandler extends AbstractDocHandler {

    private final String JSON_RED = "#a72020";
    private final String JSON_BLUE = "#0451a5";
    private final String JSON_GREEN = "#0a850a";

    @Override
    public DocType support() {
        return DocType.HTML;
    }

    @Override
    protected void customProcess(Collection<ApiInfo> apiInfos) {
        if (AssertUtils.isEmpty(apiInfos)) {
            return;
        }

        apiInfos.forEach(apiInfo -> {
            if (apiInfo == null) {
                return;
            }

            // 处理JSON
            processJson(apiInfo);
            // 处理转义字符
            processEscapeCharacter(apiInfo);

        });
    }

    private void processJson(ApiInfo apiInfo) {
        // 装饰 Json
        ApiInfo.ApiRequestInfo requestInfo = apiInfo.getRequestInfo();
        if (requestInfo != null) {
            String requestBodyJson = requestInfo.getRequestBodyJson();
            if (AssertUtils.isNotBlank(requestBodyJson)) {
                String decorated = decorateJsonString(requestBodyJson);
                requestInfo.setRequestBodyJson(decorated);
            }
        }

        // 装饰 Json
        ApiInfo.ApiResponseInfo responseInfo = apiInfo.getResponseInfo();
        if (responseInfo != null) {
            String responseBodyJson = responseInfo.getResponseBodyJson();
            if (AssertUtils.isNotBlank(responseBodyJson)) {
                String decorated = decorateJsonString(responseBodyJson);
                responseInfo.setResponseBodyJson(decorated);
            }
        }
    }

    private String decorateJsonString(String prettyString) {
        StringBuilder builder = new StringBuilder();

        // 分隔JSON为行
        String[] jsonSplit = prettyString.split("\n");

        for (int i = 0; i < jsonSplit.length; i++) {
            String lineJson = jsonSplit[i];

            // 提取注释
            String comments = "";
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
                boolean needsComma = afterStr.endsWith(",");
                if (needsComma) {
                    // 移除末尾的逗号，但不设置颜色（因为后面会单独处理）
                    afterStr = afterStr.substring(0, afterStr.length() - 1);
                }

                // 写入值并设置颜色
                builder.append(buildSpanColor(afterStr));

                // 如果需要添加逗号
                if (needsComma) {
                    builder.append(",");
                }

                // 写入注释并设置颜色
                if (AssertUtils.isNotBlank(comments)) {
                    builder.append(buildSpanColor(JSON_GREEN, " " + comments));
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

        if (trimmed.startsWith("\"") || trimmed.startsWith("'") || trimmed.startsWith("true") || trimmed.startsWith("false")) {
            return JSON_BLUE;
        }
        if (trimmed.startsWith("0") || trimmed.startsWith("1")) {
            return JSON_GREEN;
        }

        return "";
    }

    @Override
    protected String templateName() {
        return "api-doc-html.ftl";
    }
}
