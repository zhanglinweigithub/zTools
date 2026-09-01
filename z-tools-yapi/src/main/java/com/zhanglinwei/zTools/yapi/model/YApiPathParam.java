package com.zhanglinwei.zTools.yapi.model;


import com.google.gson.annotations.SerializedName;

/**
 * YApi 路径参数（REST路径中的参数）
 */
public class YApiPathParam {

    /** ID */
    @SerializedName("_id")
    private String _id;

    /** 参数名称 */
    private String name;

    /** 示例 */
    private String example;

    /** 描述 */
    private String desc;

    public YApiPathParam() {
    }

    public YApiPathParam(String name, String desc) {
        this.name = name;
        this.desc = desc;
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
}

