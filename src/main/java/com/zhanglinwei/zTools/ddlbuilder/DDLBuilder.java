package com.zhanglinwei.zTools.ddlbuilder;

import com.zhanglinwei.zTools.model.DDLInfo;

public interface DDLBuilder {

    boolean support(String dbType);

    String generateDDLString(DDLInfo ddlInfo);

}
