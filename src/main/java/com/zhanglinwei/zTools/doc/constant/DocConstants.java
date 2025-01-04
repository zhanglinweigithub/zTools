package com.zhanglinwei.zTools.doc.constant;

import com.google.common.collect.Lists;

import java.util.List;

public interface DocConstants {

    /**
     * 标题
     */
    String REQUEST_HEADER = "RequestHeader";
    String PATH_VARIABLE = "PathVariable";
    String REQUEST_PARAM = "RequestParam";
    String FORM_PARAM = "FormParam";
    String REQUEST_BODY = "RequestBody";
    String RESPONSE_BODY = "ResponseBody";

    /**
     * 表格头
     */
    List<String> BODY_TABLE_HEADER = Lists.newArrayList("名称", "类型", "示例", "必填", "描述");
    List<String> COMMON_TABLE_HEADER = Lists.newArrayList("名称", "示例", "必填", "描述");

    /**
     * 对齐方式
     */
    String ALIGN_LEFT = "left";

    String DOCX = ".docx";
    String MD = ".md";
    String HTML = ".html";

}
