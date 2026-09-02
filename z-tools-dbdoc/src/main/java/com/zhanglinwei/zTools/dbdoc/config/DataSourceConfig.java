package com.zhanglinwei.zTools.dbdoc.config;

import com.intellij.openapi.project.Project;
import com.zhanglinwei.zTools.dbdoc.dialect.DatabaseDialect;
import com.zhanglinwei.zTools.dbdoc.dialect.DatabaseDialectFactory;
import com.zhanglinwei.zTools.common.enums.SpringConfigProperties;
import com.zhanglinwei.zTools.common.util.ProjectConfigs;
import com.zhanglinwei.zTools.common.util.StringUtils;

/**
 * 生成库表文档用的数据源快照。
 * <p>
 * 从项目 {@code zTools.yaml} 读取驱动、URL、账号、密码，再匹配方言并解析库名。
 * 任一必填项缺失或方言不支持时，{@link #hasError()} 为 {@code true}，应提示 {@link #getErrorMsg()} 而非连库。
 */
public class DataSourceConfig {

    private static final String ERROR_MSG_EXPRESSION = "The 【\"%s\"】 config not found in file [ zTools.yaml ]";

    /** JDBC 驱动全限定名 */
    private String driverClassName;
    /** JDBC URL */
    private String url;
    /** 数据库用户名 */
    private String username;
    /** 数据库密码 */
    private String password;
    /** 从 URL 解析出的库名，也用作输出文件名 */
    private String databaseName;
    /** 匹配到的方言 */
    private DatabaseDialect dialect;
    /** 配置不完整或不支持时的错误说明；正常配置时为 {@code null} */
    private String errorMsg;

    /**
     * 配置完整时的成功构造。
     *
     * @param driverClassName JDBC 驱动
     * @param url             JDBC URL
     * @param username        用户名
     * @param password        密码
     * @param databaseName    库名
     * @param dialect         已匹配方言
     */
    private DataSourceConfig(String driverClassName, String url, String username, String password,
                             String databaseName, DatabaseDialect dialect) {
        this.driverClassName = driverClassName;
        this.url = url;
        this.username = username;
        this.password = password;
        this.databaseName = databaseName;
        this.dialect = dialect;
    }

    /**
     * 配置失败时的错误构造，仅填充 {@link #errorMsg}。
     *
     * @param errorMsg 给用户看的原因
     */
    private DataSourceConfig(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    /**
     * 从当前项目的 {@code zTools.yaml} 组装数据源。
     *
     * @param project 当前工程
     * @return 成功或带错误信息的配置，不会返回 {@code null}
     */
    public static DataSourceConfig from(Project project) {
        String driverClassName = ProjectConfigs.zTools(project, SpringConfigProperties.DATASOURCE_DRIVER);
        if (StringUtils.isBlank(driverClassName)) {
            return new DataSourceConfig(String.format(ERROR_MSG_EXPRESSION, SpringConfigProperties.DATASOURCE_DRIVER.getValue()));
        }

        String url = ProjectConfigs.zTools(project, SpringConfigProperties.DATASOURCE_URL);
        if (StringUtils.isBlank(url)) {
            return new DataSourceConfig(String.format(ERROR_MSG_EXPRESSION, SpringConfigProperties.DATASOURCE_URL.getValue()));
        }

        DatabaseDialect dialect = DatabaseDialectFactory.createInstance(driverClassName, url);
        if (dialect == null) {
            return new DataSourceConfig("Unsupported database. Supported: " + DatabaseDialectFactory.supportedDisplayNames());
        }

        String username = ProjectConfigs.zTools(project, SpringConfigProperties.DATASOURCE_USERNAME);
        if (StringUtils.isBlank(username)) {
            return new DataSourceConfig(String.format(ERROR_MSG_EXPRESSION, SpringConfigProperties.DATASOURCE_USERNAME.getValue()));
        }

        String password = ProjectConfigs.zTools(project, SpringConfigProperties.DATASOURCE_PASSWORD);
        if (StringUtils.isBlank(password)) {
            return new DataSourceConfig(String.format(ERROR_MSG_EXPRESSION, SpringConfigProperties.DATASOURCE_PASSWORD.getValue()));
        }

        String dbName = dialect.extractDatabaseName(url);
        if (StringUtils.isBlank(dbName)) {
            return new DataSourceConfig(String.format(ERROR_MSG_EXPRESSION, "database name"));
        }

        return new DataSourceConfig(driverClassName, url, username, password, dbName, dialect);
    }

    /**
     * 配置是否不可用（缺项或不支持的数据库）。
     *
     * @return 有错误信息则为 {@code true}
     */
    public boolean hasError() {
        return StringUtils.isNotBlank(this.errorMsg);
    }

    /**
     * 获取库名。
     *
     * @return 库名；错误配置时可能为 {@code null}
     */
    public String getDatabaseName() {
        return databaseName;
    }

    /**
     * 获取错误说明。
     *
     * @return 失败原因；成功时为 {@code null}
     */
    public String getErrorMsg() {
        return errorMsg;
    }

    /**
     * 获取 JDBC 驱动类名。
     *
     * @return 驱动全限定名
     */
    public String getDriverClassName() {
        return driverClassName;
    }

    /**
     * 获取 JDBC URL。
     *
     * @return 连接串
     */
    public String getUrl() {
        return url;
    }

    /**
     * 获取用户名。
     *
     * @return 数据库用户
     */
    public String getUsername() {
        return username;
    }

    /**
     * 获取密码。
     *
     * @return 数据库密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 获取已匹配方言。
     *
     * @return 方言；错误配置时可能为 {@code null}
     */
    public DatabaseDialect getDialect() {
        return dialect;
    }
}
