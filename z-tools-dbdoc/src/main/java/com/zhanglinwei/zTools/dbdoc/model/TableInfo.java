package com.zhanglinwei.zTools.dbdoc.model;

import java.util.ArrayList;
import java.util.List;

public class TableInfo {

    private String tableName;
    private String tableComment;
    private final List<ColumnInfo> columns = new ArrayList<>();

    public List<ColumnInfo> getColumns() {
        return columns;
    }

    public void addColumn(ColumnInfo columnInfo) {
        columns.add(columnInfo);
    }

    public String getTableComment() {
        return tableComment;
    }

    public void setTableComment(String tableComment) {
        this.tableComment = tableComment;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
