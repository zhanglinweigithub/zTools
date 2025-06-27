package com.zhanglinwei.zTools.doc.dbdoc.model;

import com.intellij.openapi.project.Project;
import com.zhanglinwei.zTools.common.enums.SpringConfigProperties;
import com.zhanglinwei.zTools.doc.dbdoc.common.DBType;
import com.zhanglinwei.zTools.util.AssertUtils;
import com.zhanglinwei.zTools.util.SpringConfigUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.zhanglinwei.zTools.common.constants.SpringPool.EMPTY;

public class DSCfg {

    private static final String ERROR_MSG_EXPRESSION = "The 【\"%s\"】 config not found in file [ application.yaml | application.yml | application.properties ]";

    private String driverClassName;
    private String url;
    private String username;
    private String password;
    private String databaseName;
    private DBType dbType;

    private String errorMsg;
    private Project project;

    private DSCfg(String driverClassName, String url, String username, String password, String databaseName, DBType dbType, Project project) {
        this.driverClassName = driverClassName;
        this.url = url;
        this.username = username;
        this.password = password;
        this.databaseName = databaseName;
        this.dbType = dbType;
        this.project = project;
    }

    public static DSCfg newInstance(Project project) {
        String driverClassName = SpringConfigUtils.propertyAsString(project, SpringConfigProperties.DATASOURCE_DRIVER);
        if (AssertUtils.isBlank(driverClassName)) {
            return new DSCfg(String.format(ERROR_MSG_EXPRESSION, SpringConfigProperties.DATASOURCE_DRIVER.getValue()));
        }

        String url = SpringConfigUtils.propertyAsString(project, SpringConfigProperties.DATASOURCE_URL);
        if (AssertUtils.isBlank(driverClassName)) {
            return new DSCfg(String.format(ERROR_MSG_EXPRESSION, SpringConfigProperties.DATASOURCE_URL.getValue()));
        }

        String username = SpringConfigUtils.propertyAsString(project, SpringConfigProperties.DATASOURCE_USERNAME);
        if (AssertUtils.isBlank(driverClassName)) {
            return new DSCfg(String.format(ERROR_MSG_EXPRESSION, SpringConfigProperties.DATASOURCE_USERNAME.getValue()));
        }

        String password = SpringConfigUtils.propertyAsString(project, SpringConfigProperties.DATASOURCE_PASSWORD);
        if (AssertUtils.isBlank(driverClassName)) {
            return new DSCfg(String.format(ERROR_MSG_EXPRESSION, SpringConfigProperties.DATASOURCE_PASSWORD.getValue()));
        }

        String dbName = extractDatabaseName(url);
        if (AssertUtils.isBlank(dbName)) {
            return new DSCfg(String.format(ERROR_MSG_EXPRESSION, "database name"));
        }

        return new DSCfg(driverClassName, url, username, password, dbName, DBType.MySQL, project);
    }

    private static String extractDatabaseName(String jdbcUrl) {
        // MySQL, PostgreSQL 格式
        Pattern mysqlPattern = Pattern.compile("jdbc:(mysql|postgresql)://[^/]+/([^?;]+)");
        Matcher mysqlMatcher = mysqlPattern.matcher(jdbcUrl);
        return mysqlMatcher.find() ? mysqlMatcher.group(2) : EMPTY;
    }

    private DSCfg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Boolean hasError() {
        return AssertUtils.isNotBlank(this.errorMsg);
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public Project getProject() {
        return project;
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

    public DBType getDbType() {
        return dbType;
    }
}
