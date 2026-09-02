package com.zhanglinwei.zTools.yapi.model;


import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * YApi 接口分类
 */
public class YApiInterfaceCat {

    /** 分类ID */
    @SerializedName("_id")
    private Number _id;

    /** 分类名称 */
    private String name;

    /** 所属项目ID */
    private Number project_id;

    /** 分类描述 */
    private String desc;

    /** 父分类ID */
    private Number parent_id;

    /** 排序索引 */
    private Number index;

    /** 分类下的接口列表 */
    private List<YApiInterface> list;

    /** 创建时间 */
    private long add_time;

    /** 更新时间 */
    private long up_time;

    /** 创建者UID */
    private Number uid;

    /** MongoDB 版本号 */
    @SerializedName("__v")
    private Number __v;

    /** 分类 ID。 */
    public Number get_id() {
        return _id;
    }

    /** 设置分类 ID。 */
    public void set_id(Number _id) {
        this._id = _id;
    }

    /** 分类名称。 */
    public String getName() {
        return name;
    }

    /** 设置分类名称。 */
    public void setName(String name) {
        this.name = name;
    }

    /** 所属项目 ID。 */
    public Number getProject_id() {
        return project_id;
    }

    /** 设置所属项目 ID。 */
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

    /** 排序索引。 */
    public Number getIndex() {
        return index;
    }

    /** 设置排序索引。 */
    public void setIndex(Number index) {
        this.index = index;
    }

    /** 分类下的接口列表。 */
    public List<YApiInterface> getList() {
        return list;
    }

    /** 设置分类下的接口列表。 */
    public void setList(List<YApiInterface> list) {
        this.list = list;
    }

    /** 创建时间。 */
    public long getAdd_time() {
        return add_time;
    }

    /** 设置创建时间。 */
    public void setAdd_time(long add_time) {
        this.add_time = add_time;
    }

    /** 更新时间。 */
    public long getUp_time() {
        return up_time;
    }

    /** 设置更新时间。 */
    public void setUp_time(long up_time) {
        this.up_time = up_time;
    }

    /** 创建者 UID。 */
    public Number getUid() {
        return uid;
    }

    /** 设置创建者 UID。 */
    public void setUid(Number uid) {
        this.uid = uid;
    }

    /** MongoDB 版本号。 */
    public Number get__v() {
        return __v;
    }

    /** 设置 MongoDB 版本号。 */
    public void set__v(Number __v) {
        this.__v = __v;
    }
}
