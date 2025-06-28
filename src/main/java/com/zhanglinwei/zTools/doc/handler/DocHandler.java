package com.zhanglinwei.zTools.doc.handler;

import com.zhanglinwei.zTools.doc.apidoc.model.ApiInfo;
import com.zhanglinwei.zTools.doc.constant.DocType;
import com.zhanglinwei.zTools.doc.dbdoc.model.DBTableInfo;

import java.util.Collection;

public interface DocHandler {

    boolean generateApiDoc(Collection<ApiInfo> apiInfos, String pathName) throws Exception;
    boolean generateDataBaseDoc(Collection<DBTableInfo> dbTableInfos, String pathName) throws Exception;

    DocType support();


}
