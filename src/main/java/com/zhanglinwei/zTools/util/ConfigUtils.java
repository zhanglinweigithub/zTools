package com.zhanglinwei.zTools.util;

import com.intellij.openapi.project.Project;
import com.zhanglinwei.zTools.config.ZToolsConfig;

import java.util.Set;

public class ConfigUtils {

    private static ZToolsConfig config;

    public static void init(Project project) {
        config = project.getService(ZToolsConfig.class);
    }

    public static String getPrefix() {
        return config.getPrefix();
    }

    public static Set<String> getExcludeFieldList() {
        return config.getExcludeFieldList();
    }

    public static boolean isOverwriteDoc() {
        return config.isOverwriteDoc();
    }

    public static String getSaveDir() {
        return config.getSaveDir();
    }

    public static String getDocType() {
        return config.getDocType();
    }

}
