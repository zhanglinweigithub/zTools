package com.zhanglinwei.zTools.template;

import com.zhanglinwei.zTools.model.*;
import com.zhanglinwei.zTools.util.AssertUtils;
import com.zhanglinwei.zTools.util.CommonUtils;

import java.util.List;
import java.util.StringJoiner;

import static com.zhanglinwei.zTools.util.CommonUtils.HORIZONTAL4;
import static com.zhanglinwei.zTools.util.CommonUtils.VERTICAL;

public abstract class AbstractMarkDownTemplate {

    public abstract String buildContent(ClassInfo classInfo);

    protected String buildTable(List<String> headerList, List<FieldInfo> fieldInfoList, boolean withType) {
        return buildTableHeader(headerList) + buildTableContent(fieldInfoList, withType);
    }

    protected String buildTable(List<String> headerList, List<FieldInfo> fieldInfoList) {
        return buildTable(headerList, fieldInfoList, false);
    }

    protected String buildDBTable(List<String> dbTableHeader, List<TableInfo> tableInfoList) {
        return buildTableHeader(dbTableHeader) + buildTableContent(tableInfoList);
    }

    protected String buildRequestHeaderTable(List<String> headerList, List<RequestHeader> requestHeaders) {
        return buildTableHeader(headerList) + buildRequestHeaderTableContent(requestHeaders);
    }

    private String buildRequestHeaderTableContent(List<RequestHeader> requestHeaders) {
        if (AssertUtils.isNotEmpty(requestHeaders)) {
            StringBuilder builder = new StringBuilder();

            for (RequestHeader header : requestHeaders) {
                builder.append(header.toMarkDownString());
            }

            return builder.toString();
        }

        return "";
    }

    private String buildTableContent(List<TableInfo> tableInfoList) {
        StringBuilder builder = new StringBuilder();
        for (TableInfo tableInfo : tableInfoList) {
            builder.append(tableInfo.toMarkDownString());
        }

        return builder.toString();
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
