package com.zhanglinwei.zTools.ddlbuilder.impl;

import com.zhanglinwei.zTools.ddlbuilder.DDLBuilder;

public abstract class AbstractDDLBuilder implements DDLBuilder {

    String generateDropTable(String tableName) {
        return "Drop table IF EXISTS " + tableName + ";\n";
    }

}
