package com.zhanglinwei.zTools.doc.handler;

import com.intellij.ide.impl.ProjectUtil;
import com.zhanglinwei.zTools.doc.apidoc.model.ApiInfo;
import com.zhanglinwei.zTools.doc.dbdoc.model.DBTableInfo;
import com.zhanglinwei.zTools.util.AssertUtils;
import com.zhanglinwei.zTools.util.CommonUtils;
import com.zhanglinwei.zTools.util.SpringConfigUtils;
import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.zhanglinwei.zTools.common.constants.SpringPool.*;

public abstract class AbstractDocHandler implements DocHandler {

    @Override
    public boolean generateApiDoc(Collection<ApiInfo> apiInfos, String pathName) throws Exception {
        customProcess(apiInfos);

        Configuration mdCfg = new Configuration(Configuration.VERSION_2_3_31);
        mdCfg.setTemplateLoader(new ClassTemplateLoader(getClass(), "/template/api"));
        mdCfg.setDefaultEncoding(StandardCharsets.UTF_8.name());

        Template template = mdCfg.getTemplate(apiTemplateName());
        File outputFile = new File(pathName);
        if (!outputFile.getParentFile().exists()) {
            if (!outputFile.getParentFile().mkdirs()) {
                return false;
            }
        }
        String requestPrefix = SpringConfigUtils.globalRequestPrefix(ProjectUtil.getActiveProject());
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("apiList", apiInfos);
        dataModel.put("requestPrefix", CommonUtils.buildPath(requestPrefix));
        try (Writer out =  new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8)) {
            template.process(dataModel, out);
        }
        return true;
    }

    @Override
    public boolean generateDataBaseDoc(Collection<DBTableInfo> dbTableInfos, String pathName) throws Exception {
        Configuration mdCfg = new Configuration(Configuration.VERSION_2_3_31);
        mdCfg.setTemplateLoader(new ClassTemplateLoader(getClass(), "/template/db"));
        mdCfg.setDefaultEncoding(UTF_8);

        Template template = mdCfg.getTemplate(dbTemplateName());
        File outputFile = new File(pathName);
        if (!outputFile.getParentFile().exists()) {
            if (!outputFile.getParentFile().mkdirs()) {
                return false;
            }
        }

        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("tableList", dbTableInfos);
        try (FileWriter out = new FileWriter(outputFile)) {
            template.process(dataModel, out);
        }
        return true;
    }

    protected abstract String apiTemplateName();
    protected abstract String dbTemplateName();
    protected abstract String decorateJsonString(String prettyJson);

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

    protected void processEscapeCharacter(ApiInfo apiInfo) {
        ApiInfo.ApiRequestInfo requestInfo = apiInfo.getRequestInfo();
        if (requestInfo != null) {
            ApiInfo.ApiTableInfo requestBody = requestInfo.getRequestBody();
            ApiInfo.ApiTableInfo requestHeader = requestInfo.getRequestHeader();
            ApiInfo.ApiTableInfo requestParam = requestInfo.getRequestParam();
            ApiInfo.ApiTableInfo pathVariable = requestInfo.getPathVariable();
            ApiInfo.ApiTableInfo formParam = requestInfo.getFormParam();

            if (requestBody != null && requestBody.getRowList() != null) {
                requestBody.getRowList().forEach(row -> {
                    row.setType(escapeCharacter(row.getType()));
                });
            }
            if (requestHeader != null && requestHeader.getRowList() != null) {
                requestHeader.getRowList().forEach(row -> {
                    row.setType(escapeCharacter(row.getType()));
                });
            }
            if (requestParam != null && requestParam.getRowList() != null) {
                requestParam.getRowList().forEach(row -> {
                    row.setType(escapeCharacter(row.getType()));
                });
            }
            if (pathVariable != null && pathVariable.getRowList() != null) {
                pathVariable.getRowList().forEach(row -> {
                    row.setType(escapeCharacter(row.getType()));
                });
            }
            if (formParam != null && formParam.getRowList() != null) {
                formParam.getRowList().forEach(row -> {
                    row.setType(escapeCharacter(row.getType()));
                });
            }
        }

        ApiInfo.ApiResponseInfo responseInfo = apiInfo.getResponseInfo();
        if (responseInfo != null) {
            ApiInfo.ApiTableInfo responseBody = responseInfo.getResponseBody();

            if (responseBody != null && responseBody.getRowList() != null) {
                responseBody.getRowList().forEach(row -> {
                    row.setType(escapeCharacter(row.getType()));
                });
            }
        }
    }

    protected String escapeCharacter(String string) {
        if (string != null) {
            return string.replaceAll(LEFT_CHEV, HTML_LT)
                    .replaceAll(RIGHT_CHEV, HTML_GT);
        }
        return string;
    }

}
