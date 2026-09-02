package com.zhanglinwei.zTools.configure.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.zhanglinwei.zTools.configure.constants.ZToolsConstant;
import com.zhanglinwei.zTools.common.util.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.zhanglinwei.zTools.common.constant.StringPool.EMPTY;
import static com.zhanglinwei.zTools.common.constant.StringPool.SEMICOLON;
import static com.zhanglinwei.zTools.common.constant.StringPool.SLASH;

/**
 * 文档生成配置（项目级，写入 {@code zTools.xml}）。
 * <p>
 * 设置页「API &amp; DB Document」Tab 读写本类：保存目录、文档类型、是否覆盖，
 * 以及 API 文档排除字段、库表文档相关子配置。
 */
@Service(value = Service.Level.PROJECT)
@State(name = "DocumentConfig", storages = {@Storage(ZToolsConstant.STORAGE_FILE)})
public final class DocumentConfig implements PersistentStateComponent<DocumentConfig> {

    /** 目标路径已有同名文件时是否覆盖 */
    private boolean overwriteDoc = true;
    /** 文档保存目录（相对项目或绝对路径），写入时会补齐末尾 {@code /} */
    private String saveDir = EMPTY;
    /** 文档类型展示名，如 {@code MarkDown} / {@code HTML} / {@code Word}，与 {@code DocumentType} 对应 */
    private String docType = "MarkDown";

    /** API 接口文档子配置（排除字段、树前缀） */
    private ApiDocConfig apiDocConfig = new ApiDocConfig();
    /** 库表文档子配置（数据库种类展示名） */
    private DataBaseDocConfig dataBaseDocConfig = new DataBaseDocConfig();

    /**
     * 取当前项目的文档配置。
     *
     * @param project 当前工程
     * @return 项目级配置
     */
    public static DocumentConfig getInstance(Project project) {
        return project.getService(DocumentConfig.class);
    }

    /**
     * 返回待持久化的状态（即自身）。
     *
     * @return 当前配置
     */
    @Nullable
    @Override
    public DocumentConfig getState() {
        return this;
    }

    /**
     * 从磁盘状态拷贝到当前实例。
     *
     * @param state 反序列化得到的配置
     */
    @Override
    public void loadState(@NotNull DocumentConfig state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    /**
     * 获取 API 文档子配置。
     *
     * @return API 文档配置
     */
    public ApiDocConfig getApiDocConfig() {
        return apiDocConfig;
    }

    /**
     * 设置 API 文档子配置。
     *
     * @param apiDocConfig API 文档配置
     */
    public void setApiDocConfig(ApiDocConfig apiDocConfig) {
        this.apiDocConfig = apiDocConfig;
    }

    /**
     * 获取库表文档子配置。
     *
     * @return 库表文档配置
     */
    public DataBaseDocConfig getDataBaseDocConfig() {
        return dataBaseDocConfig;
    }

    /**
     * 设置库表文档子配置。
     *
     * @param dataBaseDocConfig 库表文档配置
     */
    public void setDataBaseDocConfig(DataBaseDocConfig dataBaseDocConfig) {
        this.dataBaseDocConfig = dataBaseDocConfig;
    }

    /**
     * 是否覆盖已有文档。
     *
     * @return 覆盖则为 {@code true}
     */
    public boolean isOverwriteDoc() {
        return overwriteDoc;
    }

    /**
     * 设置是否覆盖已有文档。
     *
     * @param overwriteDoc 是否覆盖
     */
    public void setOverwriteDoc(boolean overwriteDoc) {
        this.overwriteDoc = overwriteDoc;
    }

    /**
     * 获取保存目录。
     *
     * @return 目录路径，通常以 {@code /} 结尾
     */
    public String getSaveDir() {
        return saveDir;
    }

    /**
     * 设置保存目录；非空且不以 {@code /} 结尾时自动补齐。
     *
     * @param saveDir 目录路径
     */
    public void setSaveDir(String saveDir) {
        if (StringUtils.isNotBlank(saveDir) && !saveDir.endsWith(SLASH)) {
            this.saveDir = saveDir + SLASH;
            return;
        }
        this.saveDir = saveDir;
    }

    /**
     * 获取文档类型。
     *
     * @return 类型展示名
     */
    public String getDocType() {
        return docType;
    }

    /**
     * 设置文档类型。
     *
     * @param docType 类型展示名
     */
    public void setDocType(String docType) {
        this.docType = docType;
    }

    /**
     * API 接口文档选项：字段树前缀、排除字段（设置页用分号分隔的字符串，运行时拆成集合）。
     */
    public static class ApiDocConfig {
        /** 嵌套字段树的行前缀，默认 {@code └} */
        private String prefix = "└";
        /** 已拆分的排除字段名集合，由 {@link #setExcludeFields(String)} 同步 */
        private Set<String> excludeFieldList = new HashSet<String>();
        /** 排除字段原文，多个以 {@code ;} 分隔，默认排除 {@code serialVersionUID} */
        private String excludeFields = "serialVersionUID";

        /**
         * 获取字段树前缀。
         *
         * @return 前缀字符
         */
        public String getPrefix() {
            return prefix;
        }

        /**
         * 设置字段树前缀。
         *
         * @param prefix 前缀
         */
        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        /**
         * 获取已拆分的排除字段集合。
         *
         * @return 字段名集合
         */
        public Set<String> getExcludeFieldList() {
            return excludeFieldList;
        }

        /**
         * 设置排除字段集合。
         *
         * @param excludeFieldList 字段名集合
         */
        public void setExcludeFieldList(Set<String> excludeFieldList) {
            this.excludeFieldList = excludeFieldList;
        }

        /**
         * 获取排除字段原文。
         *
         * @return 分号分隔的字段名
         */
        public String getExcludeFields() {
            return excludeFields;
        }

        /**
         * 设置排除字段原文，并按 {@code ;} 拆到 {@link #excludeFieldList}。
         *
         * @param excludeFields 分号分隔的字段名
         */
        public void setExcludeFields(String excludeFields) {
            this.excludeFields = excludeFields;
            if (StringUtils.isNotBlank(excludeFields)) {
                String[] splitField = excludeFields.split(SEMICOLON);
                setExcludeFieldList(Arrays.stream(splitField).collect(Collectors.toSet()));
            }
        }
    }

    /**
     * 库表文档选项。当前仅保留数据库种类展示名，实际连库走 {@code zTools.yaml} 与方言。
     */
    public static class DataBaseDocConfig {
        /** 数据库种类展示名，默认 {@code MySql} */
        private String dataBaseType = "MySql";

        /**
         * 获取数据库种类展示名。
         *
         * @return 如 {@code MySql}
         */
        public String getDataBaseType() {
            return dataBaseType;
        }

        /**
         * 设置数据库种类展示名。
         *
         * @param dataBaseType 种类名
         */
        public void setDataBaseType(String dataBaseType) {
            this.dataBaseType = dataBaseType;
        }
    }
}
