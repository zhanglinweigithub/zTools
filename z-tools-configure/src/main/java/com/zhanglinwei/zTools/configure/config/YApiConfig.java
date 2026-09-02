package com.zhanglinwei.zTools.configure.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.zhanglinwei.zTools.configure.constants.ZToolsConstant;
import org.jetbrains.annotations.NotNull;

import static com.zhanglinwei.zTools.common.constant.StringPool.EMPTY;

/**
 * YApi 连接配置（项目级持久化）。
 */
@State(name = "YApiConfig", storages = {@Storage(ZToolsConstant.STORAGE_FILE)})
public final class YApiConfig implements PersistentStateComponent<YApiConfig> {

    public String serverUrl = EMPTY;
    public String token = EMPTY;
    public String projectId = EMPTY;

    public YApiConfig() {
    }

    public static YApiConfig getInstance(Project project) {
        return project.getService(YApiConfig.class);
    }

    @Override
    public YApiConfig getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull YApiConfig state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public boolean isConfigured() {
        return serverUrl != null && !serverUrl.trim().isEmpty()
                && token != null && !token.trim().isEmpty();
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
}
