package com.zhanglinwei.zTools.yapi.model;

import java.util.List;

/**
 * YApi 接口查询路径信息
 */
public class YApiQueryPath {

    /** 查询路径 */
    private String path;

    /** 路径参数列表 */
    private List<String> params;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<String> getParams() {
        return params;
    }

    public void setParams(List<String> params) {
        this.params = params;
    }
}
