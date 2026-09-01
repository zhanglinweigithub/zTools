package com.zhanglinwei.zTools.yapi.model;


import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * YApi 项目环境配置
 */
public class YApiProjectEnv {

    /** 环境ID */
    @SerializedName("_id")
    private String _id;

    /** 环境名称 */
    private String name;

    /** 环境域名 */
    private String domain;

    /** 环境请求头 */
    private List<YApiHeader> header;

    /** 全局脚本 */
    private List<Object> global;

    public YApiProjectEnv() {
    }

    public YApiProjectEnv(String name, String domain) {
        this.name = name;
        this.domain = domain;
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

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public List<YApiHeader> getHeader() {
        return header;
    }

    public void setHeader(List<YApiHeader> header) {
        this.header = header;
    }

    public List<Object> getGlobal() {
        return global;
    }

    public void setGlobal(List<Object> global) {
        this.global = global;
    }
}

