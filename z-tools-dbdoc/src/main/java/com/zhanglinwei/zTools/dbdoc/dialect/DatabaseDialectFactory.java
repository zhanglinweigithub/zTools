package com.zhanglinwei.zTools.dbdoc.dialect;

import com.zhanglinwei.zTools.dbdoc.dialect.mysql.MysqlDialectAbstract;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 方言注册表。新增数据库时在 {@link #createDialects()} 中追加即可。
 */
public final class DatabaseDialectFactory {

    private static final List<DatabaseDialect> DIALECTS = createDialects();

    private DatabaseDialectFactory() {}

    private static List<DatabaseDialect> createDialects() {
        List<DatabaseDialect> dialects = new ArrayList<>();
        dialects.add(new MysqlDialectAbstract());
        return Collections.unmodifiableList(dialects);
    }

    public static DatabaseDialect createInstance(String driverClassName, String jdbcUrl) {
        return DIALECTS.stream()
                .filter(dialect -> dialect.supports(driverClassName, jdbcUrl))
                .findFirst()
                .orElse(null);
    }

    public static String supportedDisplayNames() {
        return DIALECTS.stream()
                .map(DatabaseDialect::displayName)
                .collect(Collectors.joining(", "));
    }
}
