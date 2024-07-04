package com.zhanglinwei.zTools.constant;

public enum DBType {

    MySQL("MySQL", "", "SELECT c.TABLE_NAME AS tableName, t.TABLE_COMMENT AS tableComment, c.COLUMN_NAME AS columnName, c.DATA_TYPE AS dataType, c.CHARACTER_MAXIMUM_LENGTH AS maxLength, CASE WHEN c.IS_NULLABLE = 'YES' THEN 'Y' ELSE 'N' END AS isNullable, c.NUMERIC_PRECISION AS dataPrecision, CASE WHEN c.COLUMN_KEY = 'PRI' THEN 'Y' ELSE 'N' END AS primaryKey, c.COLUMN_DEFAULT AS columnDefault, c.COLUMN_COMMENT AS columnComment FROM INFORMATION_SCHEMA.COLUMNS c JOIN INFORMATION_SCHEMA.TABLES t ON c.TABLE_NAME = t.TABLE_NAME AND c.TABLE_SCHEMA = t.TABLE_SCHEMA WHERE c.TABLE_SCHEMA = ?;"),
    DM("DM", "", "SELECT tb.table_name AS tableName, tc.comments AS tableComment, col.column_name AS columnName, col.data_type AS dataType, col.data_length AS maxLength, CASE WHEN col.nullable = 'Y' THEN 'Y' ELSE 'N' END AS isNullable, col.data_precision AS dataPrecision, CASE WHEN pk.column_name IS NOT NULL THEN 'Y' ELSE 'N' END AS primaryKey, col.data_default AS columnDefault, col_comments.comments AS columnComment FROM all_tables tb JOIN all_tab_comments tc ON tb.table_name = tc.table_name AND tb.owner = tc.owner JOIN all_tab_columns col ON tb.table_name = col.table_name AND tb.owner = col.owner LEFT JOIN all_col_comments col_comments ON col.column_name = col_comments.column_name AND col.table_name = col_comments.table_name AND col.owner = col_comments.owner LEFT JOIN( SELECT cols.table_name, cols.column_name FROM all_constraints cons JOIN all_cons_columns cols ON cons.constraint_name = cols.constraint_name WHERE cons.constraint_type = 'P') pk ON tb.table_name = pk.table_name AND col.column_name = pk.column_name WHERE tb.owner = ?;"),
    KingBase("KingBase", "", ""),
    ;


    private String dbName;
    private String tableSql;
    private String infoSql;


    DBType(String dbName, String tableSql, String infoSql){
        this.dbName = dbName;
        this.tableSql = tableSql;
        this.infoSql = infoSql;
    }

    public static String getSqlByDbType(String dbType) {
        for (DBType db : DBType.values()) {
            if (dbType.equals(db.getDbName())) {
                return db.getInfoSql();
            }
        }

        return "";
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getTableSql() {
        return tableSql;
    }

    public void setTableSql(String tableSql) {
        this.tableSql = tableSql;
    }

    public String getInfoSql() {
        return infoSql;
    }

    public void setInfoSql(String infoSql) {
        this.infoSql = infoSql;
    }
}
