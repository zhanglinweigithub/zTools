package com.zhanglinwei.zTools.configure.config;

import com.intellij.ide.util.gotoByName.ChooseByNameFilterConfiguration;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.zhanglinwei.zTools.configure.constants.ZToolsConstant;
import com.zhanglinwei.zTools.common.enums.HttpMethod;

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
}
