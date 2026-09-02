package com.zhanglinwei.zTools.common.doc;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.File;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;

import static com.zhanglinwei.zTools.common.constant.StringPool.UTF_8;

/**
 * 基于 FreeMarker 将数据模型渲染到文件。
 * <p>
 * 模板从 {@code loaderClass} 所在 classpath 的 {@code templateDir} 加载，
 * 用于生成 Markdown / HTML / Word 等接口文档。
 */
public final class TemplateDocWriter {

    /** 工具类，禁止实例化 */
    private TemplateDocWriter() {}

    /**
     * 以 UTF-8 写出模板渲染结果。
     *
     * @param loaderClass  用于定位 classpath 模板的类
     * @param templateDir  模板目录（相对该类的 classpath）
     * @param templateName 模板文件名
     * @param dataModel    FreeMarker 数据模型
     * @param pathName     输出文件完整路径
     * @return 写出成功为 {@code true}；父目录创建失败为 {@code false}
     * @throws Exception 模板加载或渲染失败
     */
    public static boolean writeUtf8(Class<?> loaderClass, String templateDir, String templateName,
                                    Map<String, Object> dataModel, String pathName) throws Exception {
        return write(loaderClass, templateDir, templateName, dataModel, pathName, StandardCharsets.UTF_8);
    }

    /**
     * 以平台默认编码写出模板渲染结果（走 {@code FileWriter}）。
     *
     * @param loaderClass  用于定位 classpath 模板的类
     * @param templateDir  模板目录（相对该类的 classpath）
     * @param templateName 模板文件名
     * @param dataModel    FreeMarker 数据模型
     * @param pathName     输出文件完整路径
     * @return 写出成功为 {@code true}；父目录创建失败为 {@code false}
     * @throws Exception 模板加载或渲染失败
     */
    public static boolean writePlatform(Class<?> loaderClass, String templateDir, String templateName,
                                        Map<String, Object> dataModel, String pathName) throws Exception {
        return write(loaderClass, templateDir, templateName, dataModel, pathName, null);
    }

    /**
     * 渲染模板并写文件。{@code charset} 非空则按该编码写；为 {@code null} 则用平台默认 {@code FileWriter}。
     *
     * @param loaderClass  用于定位 classpath 模板的类
     * @param templateDir  模板目录
     * @param templateName 模板文件名
     * @param dataModel    数据模型
     * @param pathName     输出路径
     * @param charset      指定编码；{@code null} 表示平台默认
     * @return 写出成功为 {@code true}
     * @throws Exception 模板加载或渲染失败
     */
    private static boolean write(Class<?> loaderClass, String templateDir, String templateName,
                                 Map<String, Object> dataModel, String pathName, Charset charset) throws Exception {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
        cfg.setTemplateLoader(new ClassTemplateLoader(loaderClass, templateDir));
        cfg.setDefaultEncoding(charset != null ? charset.name() : UTF_8);

        Template template = cfg.getTemplate(templateName);
        File outputFile = new File(pathName);
        if (!outputFile.getParentFile().exists() && !outputFile.getParentFile().mkdirs()) {
            return false;
        }

        if (charset != null) {
            try (Writer out = new OutputStreamWriter(Files.newOutputStream(outputFile.toPath()), charset)) {
                template.process(dataModel, out);
            }
        } else {
            try (FileWriter out = new FileWriter(outputFile)) {
                template.process(dataModel, out);
            }
        }
        return true;
    }
}
