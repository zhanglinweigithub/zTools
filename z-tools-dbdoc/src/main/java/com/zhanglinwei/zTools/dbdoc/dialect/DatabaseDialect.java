package com.zhanglinwei.zTools.dbdoc.dialect;

import com.zhanglinwei.zTools.dbdoc.config.DataSourceConfig;
import com.zhanglinwei.zTools.dbdoc.model.TableInfo;

import java.util.List;

/**
 * 数据库方言。新增一种数据库时：
 * 1. 实现本接口（通常继承 {@link AbstractJdbcDatabaseDialect}）
 * 2. 在 {@link DatabaseDialectFactory} 中注册
 */
public interface DatabaseDialect {

    String displayName();

    boolean supports(String driverClassName, String jdbcUrl);

    String extractDatabaseName(String jdbcUrl);

    List<TableInfo> loadTables(DataSourceConfig config) throws Exception;
}
