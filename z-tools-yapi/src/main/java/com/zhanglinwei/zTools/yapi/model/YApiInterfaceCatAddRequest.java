package com.zhanglinwei.zTools.yapi.model;


/**
 * YApi 添加接口分类请求
 */
public class YApiInterfaceCatAddRequest {

    /** 分类名称（必填） */
    private String name;

    /** 项目ID（必填） */
    private Number project_id;

    /** 分类描述 */
    private String desc;

    /** 父分类ID */
    private Number parent_id;

    public YApiInterfaceCatAddRequest() {
    }

    public YApiInterfaceCatAddRequest(String name, Number project_id) {
        this.name = name;
        this.project_id = project_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Number getProject_id() {
        return project_id;
    }

    public void setProject_id(Number project_id) {
        this.project_id = project_id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Number getParent_id() {
        return parent_id;
    }

    public void setParent_id(Number parent_id) {
        this.parent_id = parent_id;
    }
}

