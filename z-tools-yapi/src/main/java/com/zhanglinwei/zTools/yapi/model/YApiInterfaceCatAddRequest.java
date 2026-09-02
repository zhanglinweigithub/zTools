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

    /** 空构造。 */
    public YApiInterfaceCatAddRequest() {
    }

    /**
     * 用分类名和项目 ID 构造。
     *
     * @param name       分类名，通常取 Controller 类名
     * @param project_id 项目 ID
     */
    public YApiInterfaceCatAddRequest(String name, Number project_id) {
        this.name = name;
        this.project_id = project_id;
    }

    /** 分类名称。 */
    public String getName() {
        return name;
    }

    /** 设置分类名称。 */
    public void setName(String name) {
        this.name = name;
    }

    /** 项目 ID。 */
    public Number getProject_id() {
        return project_id;
    }

    /** 设置项目 ID。 */
    public void setProject_id(Number project_id) {
        this.project_id = project_id;
    }

    /** 分类描述。 */
    public String getDesc() {
        return desc;
    }

    /** 设置分类描述。 */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /** 父分类 ID。 */
    public Number getParent_id() {
        return parent_id;
    }

    /** 设置父分类 ID。 */
    public void setParent_id(Number parent_id) {
        this.parent_id = parent_id;
    }
}
