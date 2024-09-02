package com.zhanglinwei.zTools.ddlbuilder.impl;

import com.zhanglinwei.zTools.ddlbuilder.DDLBuilder;

public abstract class AbstractDDLBuilder implements DDLBuilder {

    public String generateDropTableDDL(String tableName) {
        return "-- DROP TABLE\nDROP TABLE IF EXISTS " + tableName + ";\n";
    }

}
