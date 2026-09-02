package com.zhanglinwei.zTools.dbdoc.config;

import com.intellij.openapi.project.Project;
import com.zhanglinwei.zTools.dbdoc.dialect.DatabaseDialect;
import com.zhanglinwei.zTools.dbdoc.dialect.DatabaseDialectFactory;
import com.zhanglinwei.zTools.common.enums.SpringConfigProperties;
import com.zhanglinwei.zTools.common.util.ProjectConfigs;
import com.zhanglinwei.zTools.common.util.StringUtils;

public class DataSourceConfig {

    private static final String ERROR_MSG_EXPRESSION = "The 【\"%s\"】 config not found in file [ zTools.yaml ]";

    private String driverClassName;
    private String url;
    private String username;
    private String password;
    private String databaseName;
    private DatabaseDialect dialect;
    private String errorMsg;

    private DataSourceConfig(String driverClassName, String url, String username, String password,
                             String databaseName, DatabaseDialect dialect) {
        this.driverClassName = driverClassName;
        this.url = url;
        this.username = username;
        this.password = password;
        this.databaseName = databaseName;
        this.dialect = dialect;
    }

    private DataSourceConfig(String errorMsg) {
        this.errorMsg = errorMsg;
    }

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

    public boolean hasError() {
        return StringUtils.isNotBlank(this.errorMsg);
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public DatabaseDialect getDialect() {
        return dialect;
    }
}
