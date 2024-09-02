package com.zhanglinwei.zTools.ddlbuilder.impl;

import com.zhanglinwei.zTools.constant.DBType;
import com.zhanglinwei.zTools.model.DDLInfo;

public class KingBaseDDLBuilder extends AbstractDDLBuilder {
    @Override
    public boolean support(String dbType) {
        return dbType.equals(DBType.KING_BASE.getCode());
    }

    @Override
    public String generateDDL(DDLInfo ddlInfo) {
        return null;
    }

    @Override
    public String generateCreateTableDDL(DDLInfo ddlInfo) {
        return null;
    }

    @Override
    public String generateTableIndexDDL(DDLInfo ddlInfo) {
        return null;
    }


}
