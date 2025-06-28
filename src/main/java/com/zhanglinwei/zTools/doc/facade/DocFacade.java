package com.zhanglinwei.zTools.doc.facade;

import com.intellij.openapi.project.Project;
import com.zhanglinwei.zTools.doc.apidoc.model.ApiInfo;
import com.zhanglinwei.zTools.doc.config.DocConfig;
import com.zhanglinwei.zTools.doc.dbdoc.model.DBTableInfo;
import com.zhanglinwei.zTools.doc.dbdoc.model.DSCfg;
import com.zhanglinwei.zTools.doc.factory.DocFactory;
import com.zhanglinwei.zTools.doc.handler.DocHandler;
import com.zhanglinwei.zTools.util.AssertUtils;
import com.zhanglinwei.zTools.util.FileUtils;

import java.util.Collection;

public class DocFacade {

    private static final String DFT_SAVE_DIR = "/target/_docs/";

    public static boolean generateApiDoc(Collection<ApiInfo> apiInfos, Project project, String fileName) throws Exception {
        DocConfig docConfig = DocConfig.getInstance(project);
        DocHandler handler = DocFactory.getHandler(docConfig.getDocType());
        return handler.generateApiDoc(apiInfos, mkdir(project) + fileName + handler.support().getSuffix());
    }

    public static boolean generateDbDoc(Collection<DBTableInfo> dbTableInfos, DSCfg dsCfg) throws Exception {
        Project project = dsCfg.getProject();
        String fileName = dsCfg.getDatabaseName();

        DocConfig docConfig = DocConfig.getInstance(project);
        DocHandler handler = DocFactory.getHandler(docConfig.getDocType());
        return handler.generateDataBaseDoc(dbTableInfos, mkdir(project) + fileName + handler.support().getSuffix());
    }

    private static String mkdir(Project project) {
        DocConfig docConfig = DocConfig.getInstance(project);
        String saveDir = docConfig.getSaveDir();
        String dirPath = AssertUtils.isNotBlank(saveDir) ? saveDir : project.getBasePath() + DFT_SAVE_DIR;

        FileUtils.mkdir(project, dirPath);

        return dirPath;
    }
}
