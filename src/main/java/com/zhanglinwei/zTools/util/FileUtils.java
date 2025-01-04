package com.zhanglinwei.zTools.util;

import com.intellij.openapi.project.Project;
import com.zhanglinwei.zTools.doc.config.DocConfig;
import com.zhanglinwei.zTools.doc.apidoc.model.ClassInfo;
import com.zhanglinwei.zTools.doc.apidoc.model.MethodInfo;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;

import java.io.File;
import java.io.FileReader;

/**
 * 文件工具类
 */
public class FileUtils {

    private FileUtils(){}

    public static String getFileName(MethodInfo methodInfo, Project project) {
        String fileName = methodInfo.getName();
        if (AssertUtils.isNotBlank(methodInfo.getDesc())) {
            fileName = methodInfo.getDesc().contains(" ") ? methodInfo.getDesc().split(" ")[0] : methodInfo.getDesc();
        }

        return getFileName(fileName, project);
    }

    public static String getFileName(ClassInfo classInfo, boolean forMethod, Project project) {
        return forMethod ? getFileName(classInfo.getMethods().get(0), project) : getFileName(classInfo, project);
    }

    public static String getFileName(ClassInfo classInfo, Project project) {
        String fileName = AssertUtils.isNotEmpty(classInfo.getClassDesc()) ? classInfo.getClassDesc() : classInfo.getClassName();
        return getFileName(fileName, project);
    }

    public static String getFileName(String fileName, Project project) {
        DocConfig apiDocConfig = DocConfig.getInstance(project);
        return apiDocConfig.isOverwriteDoc() ? fileName : fileName + System.currentTimeMillis();
    }

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
