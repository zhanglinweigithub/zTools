package com.zhanglinwei.zTools.doc.handler;

import com.zhanglinwei.zTools.doc.apidoc.model.ApiInfo;
import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.File;
import java.io.FileWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.zhanglinwei.zTools.common.constants.SpringPool.*;

public abstract class AbstractDocHandler implements DocHandler {

    @Override
    public boolean generateApiDoc(Collection<ApiInfo> apiInfos, String pathName) throws Exception {
        customProcess(apiInfos);

        Configuration mdCfg = new Configuration(Configuration.VERSION_2_3_31);
        mdCfg.setTemplateLoader(new ClassTemplateLoader(getClass(), "/template"));
        mdCfg.setDefaultEncoding("UTF-8");

        Template template = mdCfg.getTemplate(templateName());
        File outputFile = new File(pathName);
        if (!outputFile.getParentFile().exists()) {
            if (!outputFile.getParentFile().mkdirs()) {
                return false;
            }
        }

        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("apiList", apiInfos);
        try (FileWriter out = new FileWriter(outputFile)) {
            template.process(dataModel, out);
        }
        return true;
    }

    protected abstract String templateName();

    @Override
    public boolean generateDataBaseDoc() {
        return false;
    }

    protected void customProcess(Collection<ApiInfo> apiInfos) {

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
