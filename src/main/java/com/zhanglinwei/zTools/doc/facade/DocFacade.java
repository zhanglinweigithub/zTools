package com.zhanglinwei.zTools.doc.facade;

import com.intellij.openapi.project.Project;
import com.zhanglinwei.zTools.doc.apidoc.model.ClassInfo;
import com.zhanglinwei.zTools.doc.config.DocConfig;
import com.zhanglinwei.zTools.doc.factory.DocFactory;
import com.zhanglinwei.zTools.doc.handler.DocHandler;
import com.zhanglinwei.zTools.util.FileUtils;

import java.io.IOException;

public class DocFacade {

    public static boolean generateApiDoc(ClassInfo classInfo, Project project, boolean forMethod) throws IOException {
        String dirPath = FileUtils.getDirPath(project);
        if (!FileUtils.mkDirectory(project, dirPath)) {
            return false;
        }
        String fileName = FileUtils.getFileName(classInfo, forMethod, project);

        DocConfig docConfig = DocConfig.getInstance(project);
        DocHandler handler = DocFactory.getHandler(docConfig.getDocType());
        return handler.generateApiDoc(classInfo, dirPath + fileName + handler.support().getSuffix());
    }

}
