package com.zhanglinwei.zTools.ddlbuilder;

import com.zhanglinwei.zTools.model.DDLInfo;

public interface DDLBuilder {

    boolean support(String dbType);

    String generateDDL(DDLInfo ddlInfo);

    String generateDropTableDDL(String tableName);

    String generateCreateTableDDL(DDLInfo ddlInfo);

    String generateTableIndexDDL(DDLInfo ddlInfo);

}
