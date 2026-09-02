package com.zhanglinwei.zTools.dbdoc.dialect.mysql;

import com.zhanglinwei.zTools.dbdoc.dialect.AbstractJdbcDatabaseDialect;
import com.zhanglinwei.zTools.dbdoc.model.ColumnInfo;
import com.zhanglinwei.zTools.dbdoc.config.DataSourceConfig;
import com.zhanglinwei.zTools.dbdoc.model.TableInfo;
import com.zhanglinwei.zTools.common.util.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.zhanglinwei.zTools.common.constant.StringPool.EMPTY;

public class MysqlDialectAbstract extends AbstractJdbcDatabaseDialect {

    private static final Set<String> DRIVERS = new HashSet<>(Arrays.asList(
            "com.mysql.cj.jdbc.Driver",
            "com.mysql.jdbc.Driver"
    ));

    private static final Pattern JDBC_URL_PATTERN = Pattern.compile("jdbc:mysql://[^/]+/([^?;]+)");

    private static final String DATABASE_NAME_PLACEHOLDER = "#dataBaseName";
    private static final String LOAD_TABLES_SQL =
            "SELECT t.table_name, t.table_comment, c.column_name, c.data_type, c.character_maximum_length, "
                    + "c.numeric_precision, c.numeric_scale, c.is_nullable, c.column_key, c.column_default, c.column_comment "
                    + "FROM information_schema.COLUMNS c "
                    + "INNER JOIN information_schema.TABLES t ON t.table_name = c.table_name "
                    + "WHERE c.table_schema = #dataBaseName and t.table_schema = #dataBaseName "
                    + "ORDER BY c.table_name, c.ORDINAL_POSITION;";

    @Override
    public String displayName() {
        return "MySQL";
    }

    @Override
    public boolean supports(String driverClassName, String jdbcUrl) {
        if (StringUtils.isBlank(driverClassName) || StringUtils.isBlank(jdbcUrl)) {
            return false;
        }
        return DRIVERS.contains(driverClassName) && jdbcUrl.contains("jdbc:mysql:");
    }

    @Override
    public String extractDatabaseName(String jdbcUrl) {
        if (StringUtils.isBlank(jdbcUrl)) {
            return EMPTY;
        }
        Matcher matcher = JDBC_URL_PATTERN.matcher(jdbcUrl);
        return matcher.find() ? matcher.group(1) : EMPTY;
    }

    @Override
    protected String query(DataSourceConfig config) {
        return LOAD_TABLES_SQL.replace(DATABASE_NAME_PLACEHOLDER, "'" + config.getDatabaseName() + "'");
    }

    @Override
    protected List<TableInfo> parse(ResultSet resultSet) throws SQLException {
        Map<String, TableInfo> tableMap = new LinkedHashMap<>();
        while (resultSet.next()) {
            String tableName = resultSet.getString("TABLE_NAME");
            TableInfo tableInfo = tableMap.get(tableName);
            if (tableInfo == null) {
                tableInfo = new TableInfo();
                tableInfo.setTableName(tableName);
                tableInfo.setTableComment(resultSet.getString("TABLE_COMMENT"));
                tableMap.put(tableName, tableInfo);
            }

            ColumnInfo columnInfo = new ColumnInfo();
            columnInfo.setColumnName(resultSet.getString("COLUMN_NAME"));
            columnInfo.setDataType(resultSet.getString("DATA_TYPE"));
            columnInfo.setCharacterMaximumLength(resultSet.getLong("CHARACTER_MAXIMUM_LENGTH"));
            columnInfo.setNumericPrecision(resultSet.getLong("NUMERIC_PRECISION"));
            columnInfo.setNumericScale(resultSet.getLong("NUMERIC_SCALE"));
            columnInfo.setIsNullable(resultSet.getString("IS_NULLABLE"));
            columnInfo.setColumnDefault(resultSet.getString("COLUMN_DEFAULT"));
            columnInfo.setColumnComment(resultSet.getString("COLUMN_COMMENT"));
            String columnKey = resultSet.getString("COLUMN_KEY");
            columnInfo.setPrimaryKey(StringUtils.isNotBlank(columnKey) && columnKey.contains("PRI"));
            tableInfo.addColumn(columnInfo);
        }
        return new ArrayList<>(tableMap.values());
    }
}
