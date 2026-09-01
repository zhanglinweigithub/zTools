package com.zhanglinwei.zTools.yapi.model;

import com.google.gson.annotations.SerializedName;

/**
 * YApi Form 表单参数
 */
public class YApiFormParam {

    /** ID */
    @SerializedName("_id")
    private String _id;

    /** 参数名称 */
    private String name;

    /** 参数类型：text / file */
    private String type;

    /** 示例 */
    private String example;

    /** 描述 */
    private String desc;

    /** 是否必填：1-必填 / 0-非必填 */
    private String required;

    public YApiFormParam() {
    }

    public YApiFormParam(String name, String type) {
        this.name = name;
        this.type = type;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
