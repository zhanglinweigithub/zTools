package com.zhanglinwei.zTools.doc.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.zhanglinwei.zTools.util.AssertUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.zhanglinwei.zTools.util.CommonUtils.SEMICOLON;

@State(name = "DocConfig")
public class DocConfig implements PersistentStateComponent<DocConfig> {

    private boolean overwriteDoc = true;
    private String saveDir = "";
    private String docType = "MarkDown";


    private ApiDocConfig apiDocConfig = new ApiDocConfig();

    private DataBaseDocConfig dataBaseDocConfig = new DataBaseDocConfig();

    public static DocConfig getInstance(Project project) {
        return project.getService(DocConfig.class);
    }

    @Nullable
    @Override
    public DocConfig getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull DocConfig state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public ApiDocConfig getApiDocConfig() {
        return apiDocConfig;
    }

    public void setApiDocConfig(ApiDocConfig apiDocConfig) {
        this.apiDocConfig = apiDocConfig;
    }

    public DataBaseDocConfig getDataBaseDocConfig() {
        return dataBaseDocConfig;
    }

    public void setDataBaseDocConfig(DataBaseDocConfig dataBaseDocConfig) {
        this.dataBaseDocConfig = dataBaseDocConfig;
    }

    public boolean isOverwriteDoc() {
        return overwriteDoc;
    }

    public void setOverwriteDoc(boolean overwriteDoc) {
        this.overwriteDoc = overwriteDoc;
    }

    public String getSaveDir() {
        return saveDir;
    }

    public void setSaveDir(String saveDir) {
        if (AssertUtils.isNotBlank(saveDir) && !saveDir.endsWith("/")) {
            this.saveDir = saveDir + "/";
            return;
        }
        this.saveDir = saveDir;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }



    public static class ApiDocConfig {
        private String prefix = "└";
        private Set<String> excludeFieldList = new HashSet<>();
        private String excludeFields = "serialVersionUID";

        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        public Set<String> getExcludeFieldList() {
            return excludeFieldList;
        }

        public void setExcludeFieldList(Set<String> excludeFieldList) {
            this.excludeFieldList = excludeFieldList;
        }

        public String getExcludeFields() {
            return excludeFields;
        }

        public void setExcludeFields(String excludeFields) {
            this.excludeFields = excludeFields;
            if (AssertUtils.isNotBlank(excludeFields)) {
                String[] splitField = excludeFields.split(SEMICOLON);
                setExcludeFieldList(Arrays.stream(splitField).collect(Collectors.toSet()));
            }
        }
    }

    public static class DataBaseDocConfig {
        private String dataBaseType = "MySql";

        public String getDataBaseType() {
            return dataBaseType;
        }

        public void setDataBaseType(String dataBaseType) {
            this.dataBaseType = dataBaseType;
        }
    }
}
