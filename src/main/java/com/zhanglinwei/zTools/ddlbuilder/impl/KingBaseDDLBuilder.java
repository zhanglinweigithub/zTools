package com.zhanglinwei.zTools.ddlbuilder.impl;

import com.zhanglinwei.zTools.model.DDLInfo;

public class KingBaseDDLBuilder extends AbstractDDLBuilder {
    @Override
    public boolean support(String dbType) {
        return dbType.equals("KingBase");
    }

    @Override
    public String generateDDLString(DDLInfo ddlInfo) {
        return null;
    }
}
