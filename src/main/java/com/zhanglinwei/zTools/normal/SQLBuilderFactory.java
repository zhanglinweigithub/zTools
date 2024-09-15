package com.zhanglinwei.zTools.normal;

import com.zhanglinwei.zTools.builder.SQLBuilder;
import com.zhanglinwei.zTools.builder.impl.DaMengSQLBuilder;
import com.zhanglinwei.zTools.builder.impl.KingBaseSQLBuilder;
import com.zhanglinwei.zTools.builder.impl.MySqlSQLBuilder;
import com.zhanglinwei.zTools.builder.impl.YaShanSQLBuilder;
import com.zhanglinwei.zTools.util.AssertUtils;

import java.util.ArrayList;
import java.util.List;

public class SQLBuilderFactory {

    private SQLBuilderFactory(){}

    private static final List<SQLBuilder> builderList = new ArrayList<>();

    static {
        if (AssertUtils.isEmpty(builderList)) {
            builderList.add(new MySqlSQLBuilder());
            builderList.add(new DaMengSQLBuilder());
            builderList.add(new KingBaseSQLBuilder());
            builderList.add(new YaShanSQLBuilder());
        }
    }

    public static SQLBuilder getSQLbuilder(String dbType) {
        if (AssertUtils.isNotBlank(dbType)) {
            for (SQLBuilder sqlBuilder : builderList) {
                if (sqlBuilder.support(dbType)) {
                    return sqlBuilder;
                }
            }
        }

        return null;
    }

}
