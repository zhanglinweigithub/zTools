package com.zhanglinwei.zTools.constant;

import com.zhanglinwei.zTools.util.AssertUtils;

public enum IndexType {

    PRIMARY("primary", "主键索引"),
    UNIQUE("unique", "唯一索引"),
    NORMAL("normal", "普通索引"),
    ;

    private String code;
    private String desc;

    IndexType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static IndexType findByCode(String code) {
        if(AssertUtils.isNotBlank(code)) {
            for (IndexType indexType : IndexType.values()) {
                if (code.equalsIgnoreCase(indexType.code)) {
                    return indexType;
                }
            }
        }

        return NORMAL;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
