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

public final class TemplateDocWriter {

    private TemplateDocWriter() {}

    public static boolean writeUtf8(Class<?> loaderClass, String templateDir, String templateName,
                                    Map<String, Object> dataModel, String pathName) throws Exception {
        return write(loaderClass, templateDir, templateName, dataModel, pathName, StandardCharsets.UTF_8);
    }

    public static boolean writePlatform(Class<?> loaderClass, String templateDir, String templateName,
                                        Map<String, Object> dataModel, String pathName) throws Exception {
        return write(loaderClass, templateDir, templateName, dataModel, pathName, null);
    }

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
