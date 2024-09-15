package com.zhanglinwei.zTools.builder;

import com.zhanglinwei.zTools.model.DbTableInfo;

public interface SQLBuilder {

    boolean support(String dbType);

    String generateDDL(DbTableInfo dbTableInfo);
    String generateSelect(DbTableInfo dbTableInfo);
    String generateInsert(DbTableInfo dbTableInfo);
    String generateUpdate(DbTableInfo dbTableInfo);
    String generateDelete(DbTableInfo dbTableInfo);

    String generateDropTableDDL(String tableName);

    String generateCreateTableDDL(DbTableInfo dbTableInfo);

    String generateTableIndexDDL(DbTableInfo dbTableInfo);

}
