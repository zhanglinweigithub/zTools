package com.zhanglinwei.zTools.ddlbuilder.impl;

import com.zhanglinwei.zTools.model.DDLInfo;

public class DmDDLBuilder extends AbstractDDLBuilder{
    @Override
    public boolean support(String dbType) {
        return dbType.equals("DaMeng");
    }

    @Override
    public String generateDDLString(DDLInfo ddlInfo) {
        return null;
    }
}
