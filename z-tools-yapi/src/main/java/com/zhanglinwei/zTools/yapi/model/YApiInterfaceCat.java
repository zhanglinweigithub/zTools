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

    public Number get_id() {
        return _id;
    }

    public void set_id(Number _id) {
        this._id = _id;
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

    public Number getIndex() {
        return index;
    }

    public void setIndex(Number index) {
        this.index = index;
    }

    public List<YApiInterface> getList() {
        return list;
    }

    public void setList(List<YApiInterface> list) {
        this.list = list;
    }

    public long getAdd_time() {
        return add_time;
    }

    public void setAdd_time(long add_time) {
        this.add_time = add_time;
    }

    public long getUp_time() {
        return up_time;
    }

    public void setUp_time(long up_time) {
        this.up_time = up_time;
    }

    public Number getUid() {
        return uid;
    }

    public void setUid(Number uid) {
        this.uid = uid;
    }

    public Number get__v() {
        return __v;
    }

    public void set__v(Number __v) {
        this.__v = __v;
    }
}

