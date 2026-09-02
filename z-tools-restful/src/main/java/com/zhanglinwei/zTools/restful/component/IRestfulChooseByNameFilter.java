package com.zhanglinwei.zTools.restful.component;

import com.intellij.ide.util.gotoByName.ChooseByNameFilter;
import com.intellij.ide.util.gotoByName.ChooseByNamePopup;
import com.intellij.openapi.project.Project;
import com.zhanglinwei.zTools.common.enums.HttpMethod;
import com.zhanglinwei.zTools.configure.config.RestfulConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * GoTo 窗口的 HTTP 方法过滤器。
 * <p>
 * 可按 GET / POST / PUT 等筛选接口列表。
 */
public class IRestfulChooseByNameFilter extends ChooseByNameFilter<HttpMethod> {

    /**
     * 绑定弹窗、模型和工程配置。
     *
     * @param popup   当前 GoTo 弹窗
     * @param model   名称模型
     * @param project 当前工程
     */
    public IRestfulChooseByNameFilter(final ChooseByNamePopup popup, IRestfulChooseByNameModel model, final Project project) {
        super(popup, model, RestfulConfig.getInstance(project), project);
    }

    /**
     * 过滤器选项的展示文本。
     *
     * @param httpMethod HTTP 方法
     * @return 方法名，如 {@code GET}
     */
    @Override
    protected String textForFilterValue(@NotNull HttpMethod httpMethod) {
        return httpMethod.name();
    }

    /**
     * 过滤器选项图标。当前不展示图标。
     *
     * @param httpMethod HTTP 方法
     * @return 始终为 {@code null}
     */
    @Override
    protected @Nullable Icon iconForFilterValue(@NotNull HttpMethod httpMethod) {
        return null;
    }

    /**
     * 全部可选的 HTTP 方法。
     *
     * @return {@link HttpMethod} 全部取值
     */
    @Override
    protected @NotNull Collection<HttpMethod> getAllFilterValues() {
        return Arrays.stream(HttpMethod.values()).collect(Collectors.toList());
    }
}
