package com.zhanglinwei.zTools.util;

import com.intellij.openapi.project.Project;
import com.zhanglinwei.zTools.model.ClassInfo;
import com.zhanglinwei.zTools.model.MethodInfo;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;

import java.io.File;
import java.io.FileReader;

/**
 * 文件工具类
 */
public class FileUtils {

    private FileUtils(){}

    public static String getFileName(MethodInfo methodInfo) {
        String fileName = methodInfo.getName();
        if (StringUtils.isNotBlank(methodInfo.getDesc())) {
            fileName = methodInfo.getDesc().contains(" ") ? methodInfo.getDesc().split(" ")[0] : methodInfo.getDesc();
        }

        return getFileName(fileName);
    }

    public static String getFileName(ClassInfo classInfo, boolean forMethod) {
        return forMethod ? getFileName(classInfo.getMethods().get(0)) : getFileName(classInfo);
    }

    public static String getFileName(ClassInfo classInfo) {
        String fileName = AssertUtils.isNotEmpty(classInfo.getClassDesc()) ? classInfo.getClassDesc() : classInfo.getClassName();
        return getFileName(fileName);
    }

    public static String getFileName(String fileName) {

        return ConfigUtils.isOverwriteDoc() ? fileName : fileName + System.currentTimeMillis();
    }

    public static String getDirPath(Project project) {
        String saveDir = ConfigUtils.getSaveDir();
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
