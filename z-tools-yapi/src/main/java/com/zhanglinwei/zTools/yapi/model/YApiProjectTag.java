package com.zhanglinwei.zTools.yapi.model;


/**
 * YApi 项目标签
 */
public class YApiProjectTag {

    /** 标签名称 */
    private String name;

    /** 标签描述 */
    private String desc;

    /** 空构造。 */
    public YApiProjectTag() {
    }

    /**
     * 用名称和描述构造。
     *
     * @param name 标签名
     * @param desc 描述
     */
    public YApiProjectTag(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    /** 标签名称。 */
    public String getName() {
        return name;
    }

    /** 设置标签名称。 */
    public void setName(String name) {
        this.name = name;
    }

    /** 标签描述。 */
    public String getDesc() {
        return desc;
    }

    /** 设置标签描述。 */
    public void setDesc(String desc) {
        this.desc = desc;
    }
}
