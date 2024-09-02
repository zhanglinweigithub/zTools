package com.zhanglinwei.zTools.constant;

public enum DBType {

    MYSQL("MySql", "MySQL"),
    DA_MENG("DaMeng", "达梦数据库"),
    KING_BASE("KingBase", "人大金仓数据库"),
    YA_SHAN("YaShan", "崖山数据库"),
    ;


    private String code;
    private String desc;


    DBType(String code, String desc){
        this.code = code;
        this.desc = desc;
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
