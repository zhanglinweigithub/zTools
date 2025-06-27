package com.zhanglinwei.zTools.doc.dbdoc.model;

import com.zhanglinwei.zTools.util.AssertUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBTableInfo {

    private final static String DATABASE_NAME_EXPRESSION = "#dataBaseName";
    private final static String GENERATE_DATABASE_DOC_SQL = "SELECT t.table_name, t.table_comment, c.column_name, c.data_type, c.character_maximum_length, c.numeric_precision, c.numeric_scale, c.is_nullable, c.column_key, c.column_default, c.column_comment FROM information_schema.COLUMNS c INNER JOIN information_schema.TABLES t ON t.table_name = c.table_name WHERE c.table_schema = #dataBaseName and t.table_schema = #dataBaseName ORDER BY c.table_name, c.ORDINAL_POSITION;";

    private String tableName;
    private String tableComment;
    private List<DBTableColumnInfo> columns = new ArrayList<>();

    public void addColumns(DBTableColumnInfo columnInfo) {
        columns.add(columnInfo);
    }

    public String commentOrName() {
        return AssertUtils.isNotBlank(tableComment) ? tableComment : tableName;
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

    public static List<DBTableInfo> create(DSCfg dsCfg) throws SQLException {
        Connection connection = getConnection(dsCfg);
        ResultSet resultSet = connection.createStatement().executeQuery(GENERATE_DATABASE_DOC_SQL.replaceAll(DATABASE_NAME_EXPRESSION, "'" + dsCfg.getDatabaseName() + "'"));
        return parseResult(resultSet);
    }

    private static Connection getConnection(DSCfg dsCfg) throws SQLException {
        return DriverManager.getConnection(
                dsCfg.getUrl(), dsCfg.getUsername(), dsCfg.getPassword()
        );
    }

    private static List<DBTableInfo> parseResult(ResultSet resultSet) throws SQLException {
        Map<String, DBTableInfo> tableMap = new HashMap<>();
        while (resultSet.next()) {
            String tableName = resultSet.getString("TABLE_NAME");
            String tableComment = resultSet.getString("TABLE_COMMENT");
            String columnName = resultSet.getString("COLUMN_NAME");
            String dataType = resultSet.getString("DATA_TYPE");
            Long characterMaximumLength = resultSet.getLong("CHARACTER_MAXIMUM_LENGTH");
            Long numericPrecision = resultSet.getLong("NUMERIC_PRECISION");
            Long numericScale = resultSet.getLong("NUMERIC_SCALE");
            String isNullable = resultSet.getString("IS_NULLABLE");
            String columnKey = resultSet.getString("column_key");
            String columnDefault = resultSet.getString("COLUMN_DEFAULT");
            String columnComment = resultSet.getString("COLUMN_COMMENT");


            DBTableInfo tableInfo = tableMap.computeIfAbsent(tableName, key -> {
                DBTableInfo table = new DBTableInfo();
                table.setTableName(tableName);
                table.setTableComment(tableComment);
                return table;
            });

            DBTableColumnInfo columnInfo = new DBTableColumnInfo();
            columnInfo.setColumnName(columnName);
            columnInfo.setDataType(dataType);
            columnInfo.setCharacterMaximumLength(characterMaximumLength);
            columnInfo.setNumericPrecision(numericPrecision);
            columnInfo.setNumericScale(numericScale);
            columnInfo.setIsNullable(isNullable);
            columnInfo.setColumnKey(columnKey);
            columnInfo.setColumnDefault(columnDefault);
            columnInfo.setColumnComment(columnComment);

            tableInfo.addColumns(columnInfo);
        }
        return new ArrayList<>(tableMap.values());
    }

    public static class DBTableColumnInfo {
        private String columnName;
        private String dataType;
        private Long characterMaximumLength;
        private Long numericPrecision;
        private Long numericScale;
        private String isNullable;
        private String columnKey;
        private String columnDefault;
        private String columnComment;


        public Boolean isPrimaryKey() {
            return AssertUtils.isNotBlank(columnKey) && columnKey.contains("PRI");
        }

        public String isPrimaryKeyAsString() {
            return isPrimaryKey() ? "Y" : "N";
        }

        public String commentOrName() {
            return AssertUtils.isNotBlank(columnComment) ? columnComment : columnName;
        }

        public String getColumnName() {
            return columnName;
        }

        public void setColumnName(String columnName) {
            this.columnName = columnName;
        }

        public String getDataType() {
            return dataType;
        }

        public void setDataType(String dataType) {
            this.dataType = dataType;
        }

        public Long getCharacterMaximumLength() {
            return characterMaximumLength;
        }

        public void setCharacterMaximumLength(Long characterMaximumLength) {
            this.characterMaximumLength = characterMaximumLength;
        }

        public Long getNumericPrecision() {
            return numericPrecision;
        }

        public void setNumericPrecision(Long numericPrecision) {
            this.numericPrecision = numericPrecision;
        }

        public Long getNumericScale() {
            return numericScale;
        }

        public void setNumericScale(Long numericScale) {
            this.numericScale = numericScale;
        }

        public String getIsNullable() {
            return isNullable;
        }

        public void setIsNullable(String isNullable) {
            this.isNullable = isNullable;
        }

        public String getColumnKey() {
            return columnKey;
        }

        public void setColumnKey(String columnKey) {
            this.columnKey = columnKey;
        }

        public String getColumnDefault() {
            return columnDefault;
        }

        public void setColumnDefault(String columnDefault) {
            this.columnDefault = columnDefault;
        }

        public String getColumnComment() {
            return columnComment;
        }

        public void setColumnComment(String columnComment) {
            this.columnComment = columnComment;
        }
    }
}
