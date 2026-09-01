package com.zhanglinwei.zTools.yapi.model;


import com.google.gson.annotations.SerializedName;

/**
 * YApi HTTP 请求头
 */
public class YApiHeader {

    /** 请求头ID */
    @SerializedName("_id")
    private String _id;

    /** 请求头名称 */
    private String name;

    /** 请求头值 */
    private String value;

    /** 示例 */
    private String example;

    /** 描述 */
    private String desc;

    /** 是否必填：1-必填 / 0-非必填 */
    private String required;

    public YApiHeader() {
    }

    public YApiHeader(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
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

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getRequired() {
        return required;
    }

    public void setRequired(String required) {
        this.required = required;
    }
}

