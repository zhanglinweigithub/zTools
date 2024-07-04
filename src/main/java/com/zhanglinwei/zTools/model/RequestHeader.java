package com.zhanglinwei.zTools.model;


import com.zhanglinwei.zTools.constant.DocConstants;
import com.zhanglinwei.zTools.util.AssertUtils;

import java.util.StringJoiner;

import static com.zhanglinwei.zTools.util.CommonUtils.VERTICAL;
import static com.zhanglinwei.zTools.util.CommonUtils.YES;

public class RequestHeader {

    private String name;
    private String value;
    private String desc;

    private String required;

    public RequestHeader(String name, String value, String desc) {
        this.name = name;
        this.value = value;
        setDesc(desc);
        this.required = YES;
    }

    public RequestHeader(String name, String value, String required, String desc) {
        this.name = name;
        this.value = value;
        setDesc(desc);
        this.required = required;
    }

    @Override
    public String toString() {
        return name + "," + value + "," + required + "," + desc;
    }

    public static RequestHeader json() {
        return new RequestHeader("Content-Type", "application/json", YES, "JSON");
    }

    public static RequestHeader form() {
        return new RequestHeader("Content-Type", "application/x-www-form-urlencoded", YES, "FORM");
    }

    public static RequestHeader file() {
        return new RequestHeader("Content-Type", "multipart/form-data", YES, "FORM");
    }

    public String toWordString() {
        StringJoiner joiner = new StringJoiner(",");
        return joiner.add(name).add(value).add(required).add(desc).toString();
    }

    public String[] toWordArray() {
        return new String[]{name, value, required, desc};
    }

    public String toMarkDownString() {
        StringJoiner joiner = new StringJoiner(VERTICAL);
        return joiner.add(name).add(value).add(required).add(desc).toString() + "\n";
    }

    public String toHtmlString(String align) {
        return "<tr>\n" +
                "<td align='" + align + "'>" + name + "</td>\n" +
                "<td align='" + align + "'>" + value + "</td>\n" +
                "<td align='" + align + "'>" + required + "</td>\n" +
                "<td align='" + align + "'>" + desc + "</td>\n" +
                "</tr>\n";
    }

    public String toHtmlString() {
        return toHtmlString(DocConstants.ALIGN_LEFT);
    }

    public String getRequire() {
        return required;
    }

    public void setRequire(String required) {
        this.required = required;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = AssertUtils.isBlank(desc) ? " " : "";
    }

}
