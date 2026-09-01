package com.zhanglinwei.zTools.yapi.model;


/**
 * YApi 项目标签
 */
public class YApiProjectTag {

    /** 标签名称 */
    private String name;

    /** 标签描述 */
    private String desc;

    public YApiProjectTag() {
    }

    public YApiProjectTag(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}

