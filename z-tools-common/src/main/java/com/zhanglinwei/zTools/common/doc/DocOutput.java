package com.zhanglinwei.zTools.common.doc;

import com.intellij.openapi.project.Project;
import com.zhanglinwei.zTools.common.util.NotificationUtil;
import com.zhanglinwei.zTools.common.util.StringUtils;

import java.io.File;

public final class DocOutput {

    private static final String DFT_SAVE_DIR = "/target/_docs/";

    private DocOutput() {}

    public static String resolveDir(Project project, String saveDir) {
        String dirPath = StringUtils.isNotBlank(saveDir) ? saveDir : project.getBasePath() + DFT_SAVE_DIR;
        File dir = new File(dirPath);
        if (!dir.exists() && !dir.mkdirs()) {
            NotificationUtil.errorNotify("invalid directory path!", project);
        }
        return dirPath;
    }
}
