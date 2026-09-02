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

    /** 空构造。 */
    public YApiProjectEnv() {
    }

    /**
     * 用名称和域名构造。
     *
     * @param name   环境名
     * @param domain 域名
     */
    public YApiProjectEnv(String name, String domain) {
        this.name = name;
        this.domain = domain;
    }

    /** 环境 ID。 */
    public String get_id() {
        return _id;
    }

    /** 设置环境 ID。 */
    public void set_id(String _id) {
        this._id = _id;
    }

    /** 环境名称。 */
    public String getName() {
        return name;
    }

    /** 设置环境名称。 */
    public void setName(String name) {
        this.name = name;
    }

    /** 环境域名。 */
    public String getDomain() {
        return domain;
    }

    /** 设置环境域名。 */
    public void setDomain(String domain) {
        this.domain = domain;
    }

    /** 环境请求头。 */
    public List<YApiHeader> getHeader() {
        return header;
    }

    /** 设置环境请求头。 */
    public void setHeader(List<YApiHeader> header) {
        this.header = header;
    }

    /** 全局脚本。 */
    public List<Object> getGlobal() {
        return global;
    }

    /** 设置全局脚本。 */
    public void setGlobal(List<Object> global) {
        this.global = global;
    }
}
