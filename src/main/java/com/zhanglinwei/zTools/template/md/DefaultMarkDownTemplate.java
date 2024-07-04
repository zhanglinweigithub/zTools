package com.zhanglinwei.zTools.template.md;

import com.zhanglinwei.zTools.constant.DocConstants;
import com.zhanglinwei.zTools.model.ClassInfo;
import com.zhanglinwei.zTools.model.FieldInfo;
import com.zhanglinwei.zTools.model.MethodInfo;
import com.zhanglinwei.zTools.model.RequestHeader;
import com.zhanglinwei.zTools.template.AbstractMarkDownTemplate;
import com.zhanglinwei.zTools.util.AssertUtils;
import com.zhanglinwei.zTools.util.CommonUtils;
import com.zhanglinwei.zTools.util.JsonUtil;

import java.util.List;

public class DefaultMarkDownTemplate extends AbstractMarkDownTemplate {


    @Override
    public String buildContent(ClassInfo classInfo) {
        List<MethodInfo> methodList = classInfo.getMethods();
        StringBuilder builder = new StringBuilder();
        builder.append("# ").append(classInfo.getTitle()).append("|接口文档\n");
        for(int i = 0; i < methodList.size(); i++) {
            MethodInfo method = methodList.get(i);
            if (method.isEmpty()) {
                continue;
            }
            builder.append("## ").append(method.getTitle()).append("\n")
                    .append("### 基本信息\n")
                    .append("- 接口描述: ").append(method.getDesc()).append("\n")
                    .append("- 请求方式: `").append(method.getRequestType()).append("`\n")
                    .append("- 接口路径: `").append(CommonUtils.buildPath(classInfo.getRequestPath(), method.getRequestPath())).append("`\n")
                    .append("### 请求参数\n");

            // RequestHeader
            if (AssertUtils.isNotEmpty(method.getRequestHeaders())) {
                List<RequestHeader> requestHeaders = method.getRequestHeaders();
                builder.append("#### ").append(DocConstants.REQUEST_HEADER).append("\n");
                builder.append(super.buildRequestHeaderTable(DocConstants.REQUEST_HEADER_TABLE_HEADER, requestHeaders));
            }

            // PathVariable
            if (AssertUtils.isNotEmpty(method.getPathVariables())) {
                List<FieldInfo> pathVariables = method.getPathVariables();
                builder.append("#### ").append(DocConstants.PATH_VARIABLE).append("\n");
                builder.append(super.buildTable(DocConstants.PATH_VARIABLE_TABLE_HEADER, pathVariables));

            }

            // RequestParam
            if (AssertUtils.isNotEmpty(method.getRequestParams())) {
                List<FieldInfo> requestQuerys = method.getRequestParams();
                builder.append("#### ").append(DocConstants.REQUEST_PARAM).append("\n");
                builder.append(super.buildTable(DocConstants.REQUEST_PARAM_TABLE_HEADER, requestQuerys));
            }

            // Form
            if (AssertUtils.isNotEmpty(method.getFormParams())) {
                List<FieldInfo> requestBodyForms = method.getFormParams();
                builder.append("#### ").append(DocConstants.FORM_PARAM).append("\n");
                builder.append(super.buildTable(DocConstants.FORM_PARAM_TABLE_HEADER, requestBodyForms, true));
            }

            // RequestBody
            FieldInfo requestBody = method.getRequestBody();
            if (requestBody != null && requestBody.hasChildren()) {
                builder.append("#### ").append(DocConstants.REQUEST_BODY).append("\n");
                builder.append("##### 描述\n");
                builder.append(super.buildTable(DocConstants.REQUEST_BODY_TABLE_HEADER, requestBody.getChildren(), true));

                builder.append("##### 示例\n");
                builder.append("``` json\n");
                builder.append(JsonUtil.buildJson5(method.getRequestBody())).append("\n");
                builder.append("```\n");
            }

            builder.append("### 响应参数\n");
            // ResponseBody
            FieldInfo responseBody = method.getResponseBody();
            if (responseBody != null && responseBody.hasChildren()) {
                builder.append("#### ").append(DocConstants.RESPONSE_BODY).append("\n");
                builder.append("##### 描述\n");
                builder.append(super.buildTable(DocConstants.RESPONSE_BODY_TABLE_HEADER, responseBody.getChildren(), true));

                builder.append("##### 示例\n");
                builder.append("``` json\n");
                builder.append(JsonUtil.buildJson5(method.getResponseBody())).append("\n");
                builder.append("```\n");
            }

        }

        return builder.toString();
    }

}
