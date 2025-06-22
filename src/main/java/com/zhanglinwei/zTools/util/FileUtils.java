package com.zhanglinwei.zTools.util;

import com.intellij.openapi.project.Project;
import com.zhanglinwei.zTools.doc.config.DocConfig;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;

import java.io.File;
import java.io.FileReader;

/**
 * 文件工具类
 */
public class FileUtils {

    private FileUtils(){}

    public static String getDirPath(Project project) {
        DocConfig docConfig = DocConfig.getInstance(project);
        String saveDir = docConfig.getSaveDir();
        return AssertUtils.isNotBlank(saveDir) ? saveDir : project.getBasePath() + "/target/_docs/";
    }

    /** 创建文件夹 */
    public static boolean mkDirectory(Project project, String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            boolean success = dir.mkdirs();
            if (!success) {
                NotificationUtil.errorNotify("invalid directory path!", project);
                return false;
            }
        }
        return true;
    }

//    public static Model getPomModel(Project project) {
//        PsiFile pomFile = FilenameIndex.getFilesByName(project, "pom.xml", GlobalSearchScope.projectScope(project))[0];
//        String pomPath = pomFile.getContainingDirectory().getVirtualFile().getPath() + "/pom.xml";
//        return readPom(pomPath);
//    }

    private static Model readPom(String pom) {
        MavenXpp3Reader reader = new MavenXpp3Reader();
        try {
            return reader.read(new FileReader(pom));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
