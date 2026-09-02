package com.zhanglinwei.zTools.dbdoc.dialect;

import com.zhanglinwei.zTools.dbdoc.config.DataSourceConfig;
import com.zhanglinwei.zTools.dbdoc.model.TableInfo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * JDBC 方言骨架：负责建连、查表、关资源。子类只关心 SQL 与结果映射。
 */
public abstract class AbstractJdbcDatabaseDialect implements DatabaseDialect {

    /**
     * 加载驱动、按配置建连，执行子类 SQL，再把 {@link ResultSet} 交给 {@link #parse(ResultSet)}。
     *
     * @param config 数据源（驱动、URL、账号、库名）
     * @return 表及列信息；库中无表时为空列表
     * @throws Exception 驱动加载失败、连库失败或 SQL 执行失败
     */
    @Override
    public final List<TableInfo> loadTables(DataSourceConfig config) throws Exception {
        // 按配置加载 JDBC 驱动
        Class.forName(config.getDriverClassName());
        try (
                Connection connection = DriverManager.getConnection(config.getUrl(), config.getUsername(), config.getPassword());
                Statement statement = connection.createStatement();
                // 执行子类拼好的查表 SQL
                ResultSet resultSet = statement.executeQuery(query(config))
        ) {
            // 由子类把 ResultSet 映成 TableInfo / ColumnInfo
            return parse(resultSet);
        }
    }

    /**
     * 拼出查表结构的 SQL（通常带库名）。
     *
     * @param config 数据源，用于替换库名等占位符
     * @return 可直接 {@code executeQuery} 的 SQL
     */
    protected abstract String query(DataSourceConfig config);

    /**
     * 把查询结果映射为表列表。一行通常对应一列，同一表名需合并到同一个 {@link TableInfo}。
     *
     * @param resultSet {@link #query(DataSourceConfig)} 的查询结果
     * @return 按表分组后的结构信息
     * @throws SQLException 读取列失败
     */
    protected abstract List<TableInfo> parse(ResultSet resultSet) throws SQLException;
}
