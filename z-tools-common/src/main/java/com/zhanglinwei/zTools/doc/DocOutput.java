package com.zhanglinwei.zTools.doc;

import com.intellij.openapi.project.Project;
import com.zhanglinwei.zTools.doc.config.DocumentConfig;
import com.zhanglinwei.zTools.util.NotificationUtil;
import com.zhanglinwei.zTools.util.StringUtils;

import java.io.File;

public final class DocOutput {

    private static final String DFT_SAVE_DIR = "/target/_docs/";

    private DocOutput() {}

    public static String resolveDir(Project project) {
        DocumentConfig documentConfig = DocumentConfig.getInstance(project);
        String saveDir = documentConfig.getSaveDir();
        String dirPath = StringUtils.isNotBlank(saveDir) ? saveDir : project.getBasePath() + DFT_SAVE_DIR;
        File dir = new File(dirPath);
        if (!dir.exists() && !dir.mkdirs()) {
            NotificationUtil.errorNotify("invalid directory path!", project);
        }
        return dirPath;
    }
}
