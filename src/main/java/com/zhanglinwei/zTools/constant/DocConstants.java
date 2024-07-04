package com.zhanglinwei.zTools.constant;

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
    List<String> REQUEST_HEADER_TABLE_HEADER = Lists.newArrayList("名称", "值", "必填", "描述");
    List<String> REQUEST_PARAM_TABLE_HEADER = Lists.newArrayList("名称", "必填", "值域范围", "示例","描述");
    List<String> PATH_VARIABLE_TABLE_HEADER = Lists.newArrayList("名称", "必填", "值域范围", "示例","描述");
    List<String> REQUEST_BODY_TABLE_HEADER = Lists.newArrayList("名称", "类型", "必填", "值域范围", "示例","描述");
    List<String> RESPONSE_BODY_TABLE_HEADER = Lists.newArrayList("名称", "类型", "必填", "值域范围", "示例","描述");
    List<String> FORM_PARAM_TABLE_HEADER = Lists.newArrayList("名称", "类型", "必填", "值域范围", "示例","描述");
    List<String> DB_TABLE_HEADER = Lists.newArrayList("名称", "数据类型", "长度", "精度", "允许空值", "主键", "默认值", "说明");

    /**
     * 对齐方式
     */
    String ALIGN_CENTER = "center";
    String ALIGN_LEFT = "left";
    String ALIGN_RIGHT = "right";

    String DOCX = ".docx";
    String DOC = ".doc";
    String MD = ".md";
    String HTML = ".html";
    String JAR = "jar";

}
