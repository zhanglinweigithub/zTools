package com.zhanglinwei.zTools.doc.facade;

import com.intellij.openapi.project.Project;
import com.zhanglinwei.zTools.doc.apidoc.model.ApiInfo;
import com.zhanglinwei.zTools.doc.config.DocConfig;
import com.zhanglinwei.zTools.doc.factory.DocFactory;
import com.zhanglinwei.zTools.doc.handler.DocHandler;
import com.zhanglinwei.zTools.util.FileUtils;

import java.util.Collection;

public class DocFacade {

    public static boolean generateApiDoc(Collection<ApiInfo> apiInfos, Project project, String fileName) throws Exception {
        String dirPath = FileUtils.getDirPath(project);
        if (!FileUtils.mkDirectory(project, dirPath)) {
            return false;
        }

        DocConfig docConfig = DocConfig.getInstance(project);
        DocHandler handler = DocFactory.getHandler(docConfig.getDocType());
        return handler.generateApiDoc(apiInfos, dirPath + fileName + handler.support().getSuffix());
    }

}
