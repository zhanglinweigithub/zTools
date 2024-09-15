package com.zhanglinwei.zTools.builder;

import com.zhanglinwei.zTools.model.DbTableInfo;

public interface SQLBuilder {

    boolean support(String dbType);

    String generateDDL(DbTableInfo ddlInfo);
    String generateSelect(DbTableInfo ddlInfo);
    String generateInsert(DbTableInfo ddlInfo);
    String generateUpdate(DbTableInfo ddlInfo);
    String generateDelete(DbTableInfo ddlInfo);

    String generateDropTableDDL(String tableName);

    String generateCreateTableDDL(DbTableInfo ddlInfo);

    String generateTableIndexDDL(DbTableInfo ddlInfo);

}
