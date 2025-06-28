package com.zhanglinwei.zTools.doc.handler;

import com.zhanglinwei.zTools.doc.constant.DocType;

public class MarkDownDocHandler extends AbstractDocHandler {

    @Override
    public DocType support() {
        return DocType.MD;
    }

    @Override
    protected String apiTemplateName() {
        return "api-doc-md.ftl";
    }

    @Override
    protected String dbTemplateName() {
        return "db-doc-md.ftl";
    }

    @Override
    protected String decorateJsonString(String prettyJson) {
        return prettyJson;
    }
}
