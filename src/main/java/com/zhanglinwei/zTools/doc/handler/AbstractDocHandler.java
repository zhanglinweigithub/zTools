package com.zhanglinwei.zTools.doc.handler;

import com.zhanglinwei.zTools.doc.apidoc.model.ApiInfo;
import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.File;
import java.io.FileWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractDocHandler implements DocHandler {

    @Override
    public boolean generateApiDoc(Collection<ApiInfo> apiInfos, String pathName) throws Exception {
        Configuration mdCfg = new Configuration(Configuration.VERSION_2_3_31);
        mdCfg.setTemplateLoader(new ClassTemplateLoader(getClass(), "/template"));
        mdCfg.setDefaultEncoding("UTF-8");

        Template template = mdCfg.getTemplate(templateName());
        File outputFile = new File(pathName);
        if (!outputFile.getParentFile().exists()) {
            if (!outputFile.getParentFile().mkdirs()) {
                return false;
            }
        }

        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("apiList", apiInfos);
        try (FileWriter out = new FileWriter(outputFile)) {
            template.process(dataModel, out);
        }
        return true;
    }

    @Override
    public boolean generateDataBaseDoc() {
        return false;
    }

    protected abstract String templateName();
}
