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

    /** 空构造。 */
    public YApiPathParam() {
    }

    /**
     * 用名称和描述构造。
     *
     * @param name 参数名，对应路径中的 {@code {id}}
     * @param desc 描述
     */
    public YApiPathParam(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    /** 参数 ID。 */
    public String get_id() {
        return _id;
    }

    /** 设置参数 ID。 */
    public void set_id(String _id) {
        this._id = _id;
    }

    /** 参数名称。 */
    public String getName() {
        return name;
    }

    /** 设置参数名称。 */
    public void setName(String name) {
        this.name = name;
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
}
