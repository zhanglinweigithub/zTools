package com.zhanglinwei.zTools.ddlbuilder.impl;

import com.zhanglinwei.zTools.constant.DBType;
import com.zhanglinwei.zTools.model.DDLInfo;

public class DmDDLBuilder extends AbstractDDLBuilder{
    @Override
    public boolean support(String dbType) {
        return dbType.equals(DBType.DA_MENG.getCode());
    }

    @Override
    public String generateDDL(DDLInfo ddlInfo) {
        String dropTableDDL = generateDropTableDDL(ddlInfo.getTableName());
        String createTableDDL = generateCreateTableDDL(ddlInfo);

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
