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

    @Override
    public final List<TableInfo> loadTables(DataSourceConfig config) throws Exception {
        Class.forName(config.getDriverClassName());
        try (
                Connection connection = DriverManager.getConnection(config.getUrl(), config.getUsername(), config.getPassword());
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query(config))
        ) {
            return parse(resultSet);
        }
    }

    protected abstract String query(DataSourceConfig config);

    protected abstract List<TableInfo> parse(ResultSet resultSet) throws SQLException;
}
