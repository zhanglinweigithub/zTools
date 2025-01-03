package com.zhanglinwei.zTools.sensitive.model;

public class SensitivePair {

    private String oldData;
    private String newData;

    public SensitivePair(String oldData, String newData) {
        this.oldData = oldData;
        this.newData = newData;
    }

    public String getOldData() {
        return oldData;
    }

    public String getNewData() {
        return newData;
    }
}
