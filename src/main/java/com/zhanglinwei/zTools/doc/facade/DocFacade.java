package com.zhanglinwei.zTools.doc.facade;

import com.intellij.openapi.project.Project;
import com.zhanglinwei.zTools.doc.apidoc.model.ApiInfo;
import com.zhanglinwei.zTools.doc.config.DocConfig;
import com.zhanglinwei.zTools.doc.factory.DocFactory;
import com.zhanglinwei.zTools.doc.handler.DocHandler;
import com.zhanglinwei.zTools.util.AssertUtils;
import com.zhanglinwei.zTools.util.FileUtils;

import java.util.Collection;

public class DocFacade {

    public static boolean generateApiDoc(Collection<ApiInfo> apiInfos, Project project, String fileName) throws Exception {
        DocConfig docConfig = DocConfig.getInstance(project);
        String saveDir = docConfig.getSaveDir();
        String dirPath = AssertUtils.isNotBlank(saveDir) ? saveDir : project.getBasePath() + "/target/_docs/";

        if (!FileUtils.mkdir(project, dirPath)) {
            return false;
        }

        DocHandler handler = DocFactory.getHandler(docConfig.getDocType());
        return handler.generateApiDoc(apiInfos, dirPath + fileName + handler.support().getSuffix());
    }

}
