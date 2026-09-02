package com.zhanglinwei.zTools.restful;

import com.intellij.ide.actions.GotoActionBase;
import com.intellij.ide.util.gotoByName.ChooseByNameFilter;
import com.intellij.ide.util.gotoByName.ChooseByNameItemProvider;
import com.intellij.ide.util.gotoByName.ChooseByNamePopup;
import com.intellij.ide.util.gotoByName.DefaultChooseByNameItemProvider;
import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformCoreDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.zhanglinwei.zTools.common.enums.HttpMethod;
import com.zhanglinwei.zTools.common.util.RequestPathUtils;
import com.zhanglinwei.zTools.restful.component.IRestfulChooseByNameFilter;
import com.zhanglinwei.zTools.restful.component.IRestfulChooseByNameModel;
import com.zhanglinwei.zTools.restful.model.IRestful;
import com.zhanglinwei.zTools.restful.resolver.RestfulResolver;
import com.zhanglinwei.zTools.common.util.CollectionUtils;
import com.zhanglinwei.zTools.common.util.ProjectConfigs;
import com.zhanglinwei.zTools.common.util.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 按 URL 跳转到 Spring MVC / Feign 接口的 GoTo Action。
 * <p>
 * 扫描当前工程（或当前模块）中的 Mapping 方法，弹出 ChooseByName 窗口，
 * 选中后跳转到对应 {@link com.intellij.psi.PsiMethod}。
 */
public class GoToRestfulAction extends GotoActionBase implements DumbAware {


    /**
     * 收集接口并弹出 GoTo 窗口。
     *
     * @param actionEvent 当前 Action 事件，用于取 Project / Module
     */
    @Override
    protected void gotoActionPerformed(@NotNull AnActionEvent actionEvent) {
        Project project = actionEvent.getProject();
        if (project == null) {
            return;
        }

        String requestPrefix = ProjectConfigs.globalRequestPrefix(project);
        Module module = actionEvent.getData(PlatformCoreDataKeys.MODULE);
        ChooseByNameContributor chooseByNameContributor = createChooseByNameContributor(module, requestPrefix);
        IRestfulChooseByNameModel chooseByNameModel = new IRestfulChooseByNameModel(project, chooseByNameContributor);

        GotoActionBase.GotoActionCallback<HttpMethod> iRestfulCallback = new GotoActionBase.GotoActionCallback<HttpMethod>() {
            /**
             * 按 HTTP 方法过滤列表。
             *
             * @param popup 当前 GoTo 弹窗
             * @return HTTP 方法过滤器
             */
            @Override
            protected ChooseByNameFilter<HttpMethod> createFilter(@NotNull ChooseByNamePopup popup) {
                return new IRestfulChooseByNameFilter(popup, chooseByNameModel, project);
            }

            /**
             * 选中某条接口后跳转到方法源码。
             *
             * @param chooseByNamePopup 当前弹窗
             * @param element           选中项，期望为 {@link IRestful}
             */
            @Override
            public void elementChosen(ChooseByNamePopup chooseByNamePopup, Object element) {
                if (element instanceof IRestful) {
                    IRestful iRestful = (IRestful) element;
                    if (iRestful.canNavigate()) {
                        iRestful.navigate(true);
                    }
                }
            }
        };

        showNavigationPopup(
                actionEvent, chooseByNameModel, iRestfulCallback,
                "Searching...", true, true,
                (ChooseByNameItemProvider) new DefaultChooseByNameItemProvider(getPsiContext(actionEvent))
        );
    }

    /**
     * 创建名称贡献者：按工程或模块解析接口，再拼上全局请求前缀。
     *
     * @param module        当前模块，勾选 “Current Module” 时使用
     * @param requestPrefix 全局请求前缀，如 {@code /api}
     * @return 向 GoTo 窗口提供名称与导航项的贡献者
     */
    private ChooseByNameContributor createChooseByNameContributor(Module module, String requestPrefix) {
        return new ChooseByNameContributor() {
            List<IRestful> restfulList = new ArrayList<>();

            /**
             * 收集接口路径作为搜索名称。
             *
             * @param project        当前工程
             * @param onlyThisModule 是否仅当前模块
             * @return 接口路径数组，如 {@code /api/user/{id}}
             */
            @Override
            public String @NotNull [] getNames(Project project, boolean onlyThisModule) {
                restfulList = collect(onlyThisModule && module != null
                        ? resolver -> resolver.resolverByModule(module)
                        : resolver -> resolver.resolverByProject(project));
                appendGlobalRequestPrefix(restfulList, requestPrefix);
                return restfulList.stream().map(IRestful::getName).toArray(String[]::new);
            }

            /**
             * 按名称取出对应导航项。
             *
             * @param name           接口路径
             * @param pattern        用户输入
             * @param project        当前工程
             * @param onlyThisModule 是否仅当前模块
             * @return 名称匹配的 {@link IRestful} 数组
             */
            @Override
            public NavigationItem @NotNull [] getItemsByName(String name, String pattern, Project project, boolean onlyThisModule) {
                return restfulList.stream()
                        .filter(restful -> name.equals(restful.getName()))
                        .toArray(NavigationItem[]::new);
            }

            /**
             * 把全局前缀拼到接口路径上。
             * <p>
             * 例：{@code /api} + {@code /user/{id}} → {@code /api/user/{id}}
             *
             * @param restfulList   已解析的接口列表
             * @param requestPrefix 全局请求前缀，空白则跳过
             */
            private void appendGlobalRequestPrefix(List<IRestful> restfulList, String requestPrefix) {
                if (CollectionUtils.isEmpty(restfulList) || StringUtils.isBlank(requestPrefix)) {
                    return;
                }

                restfulList.forEach(restful -> {
                    String fullPath = RequestPathUtils.join(requestPrefix, restful.getName());
                    restful.setName(fullPath);
                    restful.setRequestPath(fullPath);
                });
            }
        };
    }

    /**
     * 用全部解析器收集接口。
     *
     * @param search 按工程或模块调用解析器的策略
     * @return 合并后的接口列表
     */
    private List<IRestful> collect(Function<RestfulResolver, List<IRestful>> search) {
        return RestfulResolver.createResolver().stream()
                .flatMap(resolver -> search.apply(resolver).stream())
                .collect(Collectors.toList());
    }

}
