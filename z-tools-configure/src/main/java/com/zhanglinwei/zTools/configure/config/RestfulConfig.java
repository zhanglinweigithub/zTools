package com.zhanglinwei.zTools.configure.config;

import com.intellij.ide.util.gotoByName.ChooseByNameFilterConfiguration;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.zhanglinwei.zTools.configure.constants.ZToolsConstant;
import com.zhanglinwei.zTools.common.enums.HttpMethod;

import java.util.Collection;

/**
 * Restful 导航的 HTTP 方法过滤配置（项目级，写入 {@code zTools.xml}）。
 * <p>
 * 继承 IDEA {@link ChooseByNameFilterConfiguration}，记住用户在 Restful 窗口勾选的 GET/POST 等方法。
 */
@Service(value = Service.Level.PROJECT)
@State(name = "RestfulConfig", storages = {@Storage(ZToolsConstant.STORAGE_FILE)})
public final class RestfulConfig extends ChooseByNameFilterConfiguration<HttpMethod> {

    /**
     * 取当前项目的 Restful 过滤配置。
     *
     * @param project 当前工程
     * @return 项目级服务实例
     */
    public static RestfulConfig getInstance(Project project) {
        return project.getService(RestfulConfig.class);
    }

    /**
     * 过滤项在持久化中的名字，使用枚举名（如 {@code GET}）。
     *
     * @param type HTTP 方法
     * @return 存储用名称
     */
    @Override
    protected String nameForElement(HttpMethod type) {
        return type.name();
    }

    /**
     * 该方法是否出现在 Restful 搜索列表中。
     *
     * @param method HTTP 方法
     * @return 未过滤掉则为 {@code true}
     */
    public boolean accepts(HttpMethod method) {
        if (method == null) {
            return true;
        }
        Items items = getState();
        if (items == null) {
            return true;
        }
        return !items.getFilteredOutFileTypeNames().contains(nameForElement(method));
    }

    /**
     * 勾选或取消某 HTTP 方法，并写入工程配置。
     *
     * @param method  HTTP 方法
     * @param accepts {@code true} 表示列表中保留该方法
     */
    public void setAccepts(HttpMethod method, boolean accepts) {
        if (method == null) {
            return;
        }
        Items items = getState();
        if (items == null) {
            return;
        }
        Collection<String> names = items.getFilteredOutFileTypeNames();
        String name = nameForElement(method);
        if (accepts) {
            names.remove(name);
        } else {
            names.add(name);
        }
    }
}
