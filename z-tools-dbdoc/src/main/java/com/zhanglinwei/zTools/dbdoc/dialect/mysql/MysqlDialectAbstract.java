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

/**
 * MySQL 方言：识别 {@code jdbc:mysql:} URL，从 {@code information_schema} 拉表和列。
 * <p>
 * ResultSet → {@link ColumnInfo} 对应关系（一行一列，同表多行合并）：
 * <pre>
 *   TABLE_NAME               → TableInfo.tableName
 *   TABLE_COMMENT            → TableInfo.tableComment
 *   COLUMN_NAME              → ColumnInfo.columnName
 *   DATA_TYPE                → ColumnInfo.dataType
 *   CHARACTER_MAXIMUM_LENGTH → ColumnInfo.characterMaximumLength
 *   NUMERIC_PRECISION        → ColumnInfo.numericPrecision
 *   NUMERIC_SCALE            → ColumnInfo.numericScale
 *   IS_NULLABLE              → ColumnInfo.isNullable
 *   COLUMN_DEFAULT           → ColumnInfo.columnDefault
 *   COLUMN_COMMENT           → ColumnInfo.columnComment
 *   COLUMN_KEY 含 "PRI"      → ColumnInfo.primaryKey = true
 * </pre>
 */
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

    /**
     * {@inheritDoc}
     *
     * @return 固定为 {@code MySQL}
     */
    @Override
    public String displayName() {
        return "MySQL";
    }

    /**
     * 驱动为 MySQL 且 URL 含 {@code jdbc:mysql:} 时才处理。
     *
     * @param driverClassName JDBC 驱动类名
     * @param jdbcUrl         JDBC URL
     * @return 匹配 MySQL 则为 {@code true}
     */
    @Override
    public boolean supports(String driverClassName, String jdbcUrl) {
        if (StringUtils.isBlank(driverClassName) || StringUtils.isBlank(jdbcUrl)) {
            return false;
        }
        return DRIVERS.contains(driverClassName) && jdbcUrl.contains("jdbc:mysql:");
    }

    /**
     * 从 {@code jdbc:mysql://host:port/库名} 中取出库名，忽略 {@code ?}、{@code ;} 后的参数。
     *
     * @param jdbcUrl JDBC URL
     * @return 库名；解析失败为空串
     */
    @Override
    public String extractDatabaseName(String jdbcUrl) {
        if (StringUtils.isBlank(jdbcUrl)) {
            return EMPTY;
        }
        Matcher matcher = JDBC_URL_PATTERN.matcher(jdbcUrl);
        return matcher.find() ? matcher.group(1) : EMPTY;
    }

    /**
     * 把 SQL 里的库名占位符换成带引号的实际库名。
     *
     * @param config 含库名的数据源
     * @return 可执行的 information_schema 查询
     */
    @Override
    protected String query(DataSourceConfig config) {
        return LOAD_TABLES_SQL.replace(DATABASE_NAME_PLACEHOLDER, "'" + config.getDatabaseName() + "'");
    }

    /**
     * 按 {@code TABLE_NAME} 分组：同一张表的多列行合并到同一个 {@link TableInfo}，列顺序与 {@code ORDINAL_POSITION} 一致。
     *
     * @param resultSet information_schema 查询结果
     * @return 表列表
     * @throws SQLException 读列失败
     */
    @Override
    protected List<TableInfo> parse(ResultSet resultSet) throws SQLException {
        // LinkedHashMap 保表出现顺序（SQL 已按 table_name, ORDINAL_POSITION 排序）
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

            // 当前行对应一列，列名与 information_schema.COLUMNS 一致（JDBC 通常忽略大小写）
            ColumnInfo columnInfo = new ColumnInfo();
            columnInfo.setColumnName(resultSet.getString("COLUMN_NAME"));
            columnInfo.setDataType(resultSet.getString("DATA_TYPE"));
            columnInfo.setCharacterMaximumLength(resultSet.getLong("CHARACTER_MAXIMUM_LENGTH"));
            columnInfo.setNumericPrecision(resultSet.getLong("NUMERIC_PRECISION"));
            columnInfo.setNumericScale(resultSet.getLong("NUMERIC_SCALE"));
            columnInfo.setIsNullable(resultSet.getString("IS_NULLABLE"));
            columnInfo.setColumnDefault(resultSet.getString("COLUMN_DEFAULT"));
            columnInfo.setColumnComment(resultSet.getString("COLUMN_COMMENT"));
            // COLUMN_KEY 含 PRI 视为主键（复合主键时多列都会带 PRI）
            String columnKey = resultSet.getString("COLUMN_KEY");
            columnInfo.setPrimaryKey(StringUtils.isNotBlank(columnKey) && columnKey.contains("PRI"));
            tableInfo.addColumn(columnInfo);
        }
        return new ArrayList<>(tableMap.values());
    }
}
