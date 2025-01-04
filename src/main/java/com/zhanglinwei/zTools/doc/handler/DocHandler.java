package com.zhanglinwei.zTools.doc.handler;

import com.zhanglinwei.zTools.doc.constant.DocType;
import com.zhanglinwei.zTools.doc.apidoc.model.ClassInfo;

import java.io.IOException;

public interface DocHandler {

    boolean generateApiDoc(ClassInfo classInfo, String pathName) throws IOException;
    boolean generateDataBaseDoc();

    DocType support();


}
