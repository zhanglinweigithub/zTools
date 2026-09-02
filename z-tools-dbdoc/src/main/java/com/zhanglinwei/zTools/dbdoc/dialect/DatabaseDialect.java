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

    /**
     * 方言展示名，出现在「不支持的数据库」提示里。
     *
     * @return 如 {@code MySQL}
     */
    String displayName();

    /**
     * 是否能处理该驱动与 URL。
     *
     * @param driverClassName JDBC 驱动全限定名
     * @param jdbcUrl         JDBC 连接串
     * @return 驱动与 URL 均匹配时为 {@code true}
     */
    boolean supports(String driverClassName, String jdbcUrl);

    /**
     * 从 JDBC URL 中解析库名。
     *
     * @param jdbcUrl 如 {@code jdbc:mysql://host:3306/demo?useSSL=false}
     * @return 库名（上例为 {@code demo}）；解析失败则为空串
     */
    String extractDatabaseName(String jdbcUrl);

    /**
     * 连接数据库并加载全部表结构。
     *
     * @param config 数据源配置
     * @return 表列表；无表时为空列表
     * @throws Exception 连库或查询失败
     */
    List<TableInfo> loadTables(DataSourceConfig config) throws Exception;
}
