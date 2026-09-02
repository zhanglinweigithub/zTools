package com.zhanglinwei.zTools.common.doc;

import com.intellij.openapi.project.Project;
import com.zhanglinwei.zTools.common.util.NotificationUtil;
import com.zhanglinwei.zTools.common.util.StringUtils;

import java.io.File;

/**
 * 接口文档输出目录解析：优先使用调用方指定路径，否则落到项目下 {@code /target/_docs/}。
 */
public final class DocOutput {

    /** 未指定保存目录时的默认相对路径（相对项目根） */
    private static final String DFT_SAVE_DIR = "/target/_docs/";

    /** 工具类，禁止实例化 */
    private DocOutput() {}

    /**
     * 解析文档保存目录：{@code saveDir} 非空则用之，否则为 {@code 项目根 + /target/_docs/}。
     * 目录不存在时会尝试创建；创建失败则弹出错误通知，仍返回原路径。
     *
     * <pre>
     *   resolveDir(project, "/tmp/docs") → "/tmp/docs"
     *   resolveDir(project, null) → "{projectBase}/target/_docs/"
     * </pre>
     *
     * @param project 当前 IntelliJ 项目
     * @param saveDir 调用方指定的保存目录，空白则用默认
     * @return 最终目录路径（不一定已成功创建）
     */
    public static String resolveDir(Project project, String saveDir) {
        String dirPath = StringUtils.isNotBlank(saveDir) ? saveDir : project.getBasePath() + DFT_SAVE_DIR;
        File dir = new File(dirPath);
        if (!dir.exists() && !dir.mkdirs()) {
            NotificationUtil.errorNotify("invalid directory path!", project);
        }
        return dirPath;
    }
}
