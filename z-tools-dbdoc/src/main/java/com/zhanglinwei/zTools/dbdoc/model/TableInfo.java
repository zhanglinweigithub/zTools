package com.zhanglinwei.zTools.dbdoc.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 一张数据库表的文档模型：表名、表注释及列列表。
 * <p>
 * 由方言把 {@code ResultSet} 按表名分组后填充，再交给模板生成 HTML / Markdown / Word。
 */
public class TableInfo {

    /** 表名，对应 {@code information_schema.TABLES.TABLE_NAME} */
    private String tableName;
    /** 表注释，对应 {@code TABLE_COMMENT} */
    private String tableComment;
    /** 列列表，顺序与 {@code ORDINAL_POSITION} 一致 */
    private final List<ColumnInfo> columns = new ArrayList<>();

    /**
     * 返回列列表（可直接增删，与 {@link #addColumn(ColumnInfo)} 共用同一集合）。
     *
     * @return 当前表的列
     */
    public List<ColumnInfo> getColumns() {
        return columns;
    }

    /**
     * 追加一列。
     *
     * @param columnInfo 列信息，不应为 {@code null}
     */
    public void addColumn(ColumnInfo columnInfo) {
        columns.add(columnInfo);
    }

    /**
     * 获取表注释。
     *
     * @return 表注释，可能为空
     */
    public String getTableComment() {
        return tableComment;
    }

    /**
     * 设置表注释。
     *
     * @param tableComment 表注释
     */
    public void setTableComment(String tableComment) {
        this.tableComment = tableComment;
    }

    /**
     * 获取表名。
     *
     * @return 数据库表名
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * 设置表名。
     *
     * @param tableName 数据库表名
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
