package com.zhanglinwei.zTools.dbdoc.dialect;

import com.zhanglinwei.zTools.dbdoc.dialect.mysql.MysqlDialectAbstract;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.zhanglinwei.zTools.common.constant.StringPool.COMMA_SPACE;

/**
 * 方言注册表。新增数据库时在 {@link #createDialects()} 中追加即可。
 */
public final class DatabaseDialectFactory {

    private static final List<DatabaseDialect> DIALECTS = createDialects();

    /**
     * 工具类，禁止实例化。
     */
    private DatabaseDialectFactory() {}

    /**
     * 注册内置方言。目前仅 MySQL，后续可在此追加 PostgreSQL 等实现。
     *
     * @return 不可变方言列表
     */
    private static List<DatabaseDialect> createDialects() {
        List<DatabaseDialect> dialects = new ArrayList<>();
        dialects.add(new MysqlDialectAbstract());
        return Collections.unmodifiableList(dialects);
    }

    /**
     * 按驱动类名与 JDBC URL 匹配第一个支持的方言。
     *
     * @param driverClassName JDBC 驱动全限定名，如 {@code com.mysql.cj.jdbc.Driver}
     * @param jdbcUrl         JDBC URL，如 {@code jdbc:mysql://host:3306/demo}
     * @return 匹配到的方言；都不支持则为 {@code null}
     */
    public static DatabaseDialect createInstance(String driverClassName, String jdbcUrl) {
        return DIALECTS.stream()
                .filter(dialect -> dialect.supports(driverClassName, jdbcUrl))
                .findFirst()
                .orElse(null);
    }

    /**
     * 已注册方言的展示名，逗号分隔，用于「不支持的数据库」提示。
     *
     * @return 如 {@code MySQL}；多种方言时为 {@code MySQL, PostgreSQL}
     */
    public static String supportedDisplayNames() {
        return DIALECTS.stream()
                .map(DatabaseDialect::displayName)
                .collect(Collectors.joining(COMMA_SPACE));
    }
}
