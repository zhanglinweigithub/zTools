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
import java.util.StringJoiner;

import static com.zhanglinwei.zTools.util.CommonUtils.HORIZONTAL4;
import static com.zhanglinwei.zTools.util.CommonUtils.VERTICAL;

public class MarkDownDocHandler implements DocHandler{
    @Override
    public boolean generateApiDoc(ClassInfo classInfo, String pathName) throws IOException {
        File apiDoc = new File(pathName);
        try (OutputStreamWriter md = new OutputStreamWriter(Files.newOutputStream(apiDoc.toPath()), StandardCharsets.UTF_8)) {
            String content = makeApiContent(classInfo);
            md.write(content);
        }
        return true;
    }

    private String makeApiContent(ClassInfo classInfo) {
        List<MethodInfo> methodList = classInfo.getMethods();
        StringBuilder builder = new StringBuilder();
        builder.append("# ").append(classInfo.getTitle()).append("|接口文档\n");
        for(int i = 0; i < methodList.size(); i++) {
            MethodInfo method = methodList.get(i);
            if (method.isEmpty()) {
                continue;
            }
            builder.append("## ").append(i + 1).append("、").append(method.getTitle()).append("\n")
                    .append("### 基本信息\n")
                    .append("- 接口描述: ").append(method.getDesc()).append("\n")
                    .append("- 请求方式: `").append(method.getRequestType()).append("`\n")
                    .append("- 接口路径: `").append(CommonUtils.buildPath(classInfo.getRequestPath(), method.getRequestPath())).append("`\n")
                    .append("### 请求参数\n");

            // RequestHeader
            if (AssertUtils.isNotEmpty(method.getRequestHeaders())) {
                List<FieldInfo> requestHeaders = method.getRequestHeaders();
                builder.append("#### ").append(DocConstants.REQUEST_HEADER).append("\n");
                builder.append(buildTable(DocConstants.COMMON_TABLE_HEADER, requestHeaders));
            }

            // PathVariable
            if (AssertUtils.isNotEmpty(method.getPathVariables())) {
                List<FieldInfo> pathVariables = method.getPathVariables();
                builder.append("#### ").append(DocConstants.PATH_VARIABLE).append("\n");
                builder.append(buildTable(DocConstants.COMMON_TABLE_HEADER, pathVariables));

            }

            // RequestParam
            if (AssertUtils.isNotEmpty(method.getRequestParams())) {
                List<FieldInfo> requestQuerys = method.getRequestParams();
                builder.append("#### ").append(DocConstants.REQUEST_PARAM).append("\n");
                builder.append(buildTable(DocConstants.COMMON_TABLE_HEADER, requestQuerys));
            }

            // Form
            if (AssertUtils.isNotEmpty(method.getFormParams())) {
                List<FieldInfo> requestBodyForms = method.getFormParams();
                builder.append("#### ").append(DocConstants.FORM_PARAM).append("\n");
                builder.append(buildTable(DocConstants.COMMON_TABLE_HEADER, requestBodyForms, true));
            }

            // RequestBody
            FieldInfo requestBody = method.getRequestBody();
            if (requestBody != null && requestBody.hasChildren()) {
                builder.append("#### ").append(DocConstants.REQUEST_BODY).append("\n");
                builder.append("**描述**\n");
                builder.append(buildTable(DocConstants.BODY_TABLE_HEADER, requestBody.getChildren(), true));

                builder.append("**示例**\n");
                builder.append("``` json\n");
                builder.append(JsonUtil.buildJson5(method.getRequestBody())).append("\n");
                builder.append("```\n");
            }

            builder.append("### 响应参数\n");
            // ResponseBody
            FieldInfo responseBody = method.getResponseBody();
            if (responseBody != null && responseBody.hasChildren()) {
                builder.append("#### ").append(DocConstants.RESPONSE_BODY).append("\n");
                builder.append("**描述**\n");
                builder.append(buildTable(DocConstants.BODY_TABLE_HEADER, responseBody.getChildren(), true));

                builder.append("**示例**\n");
                builder.append("``` json\n");
                builder.append(JsonUtil.buildJson5(method.getResponseBody())).append("\n");
                builder.append("```\n");
            }

        }

        return builder.toString();
    }

    @Override
    public boolean generateDataBaseDoc() {
        return false;
    }

    @Override
    public DocType support() {
        return DocType.MD;
    }

    private String buildTable(List<String> headerList, List<FieldInfo> fieldInfoList, boolean withType) {
        return buildTableHeader(headerList) + buildTableContent(fieldInfoList, withType);
    }

    private String buildTable(List<String> headerList, List<FieldInfo> fieldInfoList) {
        return buildTable(headerList, fieldInfoList, false);
    }

    private String buildTableContent(List<FieldInfo> fieldInfoList, boolean withType) {
        if (AssertUtils.isNotEmpty(fieldInfoList)) {
            StringBuilder builder = new StringBuilder();

            for (FieldInfo info : fieldInfoList) {
                builder.append(info.toMarkDownString(withType));
                if (info.hasChildren()) {
                    for (FieldInfo field : info.getChildren()) {
                        builder.append(buildChildrenFieldInfo(field, CommonUtils.getPrefix(), withType));
                    }
                }
            }

            return builder.toString();
        }

        return "";
    }

    private String buildChildrenFieldInfo(FieldInfo info, String prefix, boolean withType) {
        StringBuilder builder = new StringBuilder();
        builder.append(info.toMarkDownString(withType, prefix));
        if (info.hasChildren()) {
            for (FieldInfo fieldInfo : info.getChildren()) {
                builder.append(buildChildrenFieldInfo(fieldInfo, prefix + CommonUtils.getPrefix(), withType));
            }
        }

        return builder.toString();
    }

    private String buildTableHeader(List<String> headerList) {
        if (AssertUtils.isNotEmpty(headerList)) {
            StringJoiner title = new StringJoiner(VERTICAL);
            StringJoiner joiner = new StringJoiner(VERTICAL);

            for (String header : headerList) {
                title.add(header);
                joiner.add(HORIZONTAL4);
            }

            return title.toString() + "\n" + joiner.toString() + "\n";
        }

        return "";
    }
}
