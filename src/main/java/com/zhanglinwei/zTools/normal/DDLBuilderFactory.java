package com.zhanglinwei.zTools.normal;

import com.zhanglinwei.zTools.ddlbuilder.DDLBuilder;
import com.zhanglinwei.zTools.ddlbuilder.impl.DmDDLBuilder;
import com.zhanglinwei.zTools.ddlbuilder.impl.KingBaseDDLBuilder;
import com.zhanglinwei.zTools.ddlbuilder.impl.MySqlDDLBuilder;
import com.zhanglinwei.zTools.ddlbuilder.impl.YaShanDDLBuilder;
import com.zhanglinwei.zTools.util.AssertUtils;

import java.util.ArrayList;
import java.util.List;

public class DDLBuilderFactory {

    private DDLBuilderFactory(){}

    private static final List<DDLBuilder> builderList = new ArrayList<>();

    static {
        if (AssertUtils.isEmpty(builderList)) {
            builderList.add(new MySqlDDLBuilder());
            builderList.add(new DmDDLBuilder());
            builderList.add(new KingBaseDDLBuilder());
            builderList.add(new YaShanDDLBuilder());
        }
    }

    public static DDLBuilder getDDLbuilder(String dbType) {
        if (AssertUtils.isNotBlank(dbType)) {
            for (DDLBuilder ddlBuilder : builderList) {
                if (ddlBuilder.support(dbType)) {
                    return ddlBuilder;
                }
            }
        }

        return null;
    }

}
