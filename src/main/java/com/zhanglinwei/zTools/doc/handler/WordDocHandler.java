package com.zhanglinwei.zTools.doc.handler;


import com.zhanglinwei.zTools.doc.apidoc.model.ApiInfo;
import com.zhanglinwei.zTools.doc.constant.DocType;
import com.zhanglinwei.zTools.util.AssertUtils;

import java.util.Collection;

public class WordDocHandler extends AbstractDocHandler {

    private static final String JSON_RED = "a72020";
    private static final String JSON_BLUE = "0451a5";
    private static final String JSON_GREEN = "0a850a";

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

    }

    @Override
    protected String templateName() {
        return "api-doc-word.ftl";
    }

    @Override
    public DocType support() {
        return DocType.WORD;
    }
}
