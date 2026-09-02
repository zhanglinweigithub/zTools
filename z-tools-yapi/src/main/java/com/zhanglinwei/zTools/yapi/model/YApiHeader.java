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

    /** 空构造。 */
    public YApiHeader() {
    }

    /**
     * 用名称和值构造。
     *
     * @param name  请求头名，如 {@code Content-Type}
     * @param value 请求头值
     */
    public YApiHeader(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /** 请求头 ID。 */
    public String get_id() {
        return _id;
    }

    /** 设置请求头 ID。 */
    public void set_id(String _id) {
        this._id = _id;
    }

    /** 请求头名称。 */
    public String getName() {
        return name;
    }

    /** 设置请求头名称。 */
    public void setName(String name) {
        this.name = name;
    }

    /** 请求头值。 */
    public String getValue() {
        return value;
    }

    /** 设置请求头值。 */
    public void setValue(String value) {
        this.value = value;
    }

    /** 示例。 */
    public String getExample() {
        return example;
    }

    /** 设置示例。 */
    public void setExample(String example) {
        this.example = example;
    }

    /** 描述。 */
    public String getDesc() {
        return desc;
    }

    /** 设置描述。 */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /** 是否必填。 */
    public String getRequired() {
        return required;
    }

    /** 设置是否必填。 */
    public void setRequired(String required) {
        this.required = required;
    }
}
