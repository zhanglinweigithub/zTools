package com.zhanglinwei.zTools.yapi.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;

/**
 * YApi 连接配置（项目级持久化）。
 */
@State(name = "YApiSettings", storages = {@Storage("yapi.xml")})
public final class YApiSettings implements PersistentStateComponent<YApiSettings> {

    public String serverUrl = "";
    public String token = "";
    public String projectId = "";

    public YApiSettings() {
    }

    public static YApiSettings getInstance(Project project) {
        return project.getService(YApiSettings.class);
    }

    @Override
    public YApiSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull YApiSettings state) {
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
