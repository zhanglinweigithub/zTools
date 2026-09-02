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
 * YApi 连接配置（项目级持久化到 {@code zTools.xml}）。
 * <p>
 * 设置页「YApi」Tab 读写本类：服务地址、项目 Token、解析得到的项目 ID。
 */
@State(name = "YApiConfig", storages = {@Storage(ZToolsConstant.STORAGE_FILE)})
public final class YApiConfig implements PersistentStateComponent<YApiConfig> {

    /** YApi 服务根地址，如 {@code http://yapi.example.com}，不要带末尾接口路径 */
    public String serverUrl = EMPTY;
    /** 项目 Token，用于鉴权与解析项目 ID */
    public String token = EMPTY;
    /** 由 Token 解析出的 YApi 项目数字 ID，上传接口时使用 */
    public String projectId = EMPTY;

    /**
     * IDEA 反序列化需要的无参构造。
     */
    public YApiConfig() {
    }

    /**
     * 取当前项目的 YApi 配置。
     *
     * @param project 当前工程
     * @return 项目级配置实例
     */
    public static YApiConfig getInstance(Project project) {
        return project.getService(YApiConfig.class);
    }

    /**
     * 返回待持久化的状态（即自身）。
     *
     * @return 当前配置
     */
    @Override
    public YApiConfig getState() {
        return this;
    }

    /**
     * 从磁盘状态拷贝到当前实例。
     *
     * @param state 反序列化得到的配置
     */
    @Override
    public void loadState(@NotNull YApiConfig state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    /**
     * 服务地址与 Token 都已填写时视为可连接。
     *
     * @return 已配置则为 {@code true}
     */
    public boolean isConfigured() {
        return serverUrl != null && !serverUrl.trim().isEmpty()
                && token != null && !token.trim().isEmpty();
    }

    /**
     * 获取 YApi 服务地址。
     *
     * @return 服务根 URL
     */
    public String getServerUrl() {
        return serverUrl;
    }

    /**
     * 设置 YApi 服务地址。
     *
     * @param serverUrl 服务根 URL
     */
    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    /**
     * 获取项目 Token。
     *
     * @return Token
     */
    public String getToken() {
        return token;
    }

    /**
     * 设置项目 Token。
     *
     * @param token 项目 Token
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * 获取 YApi 项目 ID。
     *
     * @return 数字 ID 字符串
     */
    public String getProjectId() {
        return projectId;
    }

    /**
     * 设置 YApi 项目 ID。
     *
     * @param projectId 数字 ID 字符串
     */
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
}
