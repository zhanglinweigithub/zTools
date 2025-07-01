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
import com.zhanglinwei.zTools.restful.component.IRestfulChooseByNameFilter;
import com.zhanglinwei.zTools.restful.component.IRestfulChooseByNameModel;
import com.zhanglinwei.zTools.restful.model.IRestful;
import com.zhanglinwei.zTools.restful.resolver.RestfulResolver;
import com.zhanglinwei.zTools.util.AssertUtils;
import com.zhanglinwei.zTools.util.CommonUtils;
import com.zhanglinwei.zTools.util.SpringConfigUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GoToRestfulAction extends GotoActionBase implements DumbAware {


    @Override
    protected void gotoActionPerformed(@NotNull AnActionEvent actionEvent) {
        Project project = actionEvent.getProject();
        if (project == null) {
            return;
        }

        String requestPrefix = SpringConfigUtils.globalRequestPrefix(project);
        Module module = actionEvent.getData(PlatformCoreDataKeys.MODULE);
        ChooseByNameContributor chooseByNameContributor = createChooseByNameContributor(module, requestPrefix);
        IRestfulChooseByNameModel chooseByNameModel = new IRestfulChooseByNameModel(project, chooseByNameContributor);

        GotoActionBase.GotoActionCallback<HttpMethod> iRestfulCallback = new GotoActionBase.GotoActionCallback<HttpMethod>() {
            @Override
            protected ChooseByNameFilter<HttpMethod> createFilter(@NotNull ChooseByNamePopup popup) {
                return new IRestfulChooseByNameFilter(popup, chooseByNameModel, project);
            }

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

    private ChooseByNameContributor createChooseByNameContributor(Module module, String requestPrefix) {
        return new ChooseByNameContributor() {
            List<IRestful> restfulList = new ArrayList<>();

            @Override
            public String @NotNull [] getNames(Project project, boolean onlyThisModule) {
                restfulList = onlyThisModule && module != null ? createRestfulByModule(module) : createRestfulByProject(project);
                appendGlobalRequestPrefix(restfulList, requestPrefix);
                return restfulList.stream().map(IRestful::getName).toArray(String[]::new);
            }

            @Override
            public NavigationItem @NotNull [] getItemsByName(String name, String pattern, Project project, boolean onlyThisModule) {
                return restfulList.stream()
                        .filter(restful -> name.equals(restful.getName()))
                        .toArray(NavigationItem[]::new);
            }

            private void appendGlobalRequestPrefix(List<IRestful> restfulList, String requestPrefix) {
                if (AssertUtils.isEmpty(requestPrefix) || AssertUtils.isBlank(requestPrefix)) {
                    return;
                }

                restfulList.forEach(restful -> {
                    String fullPath = CommonUtils.buildPath(requestPrefix, restful.getName());
                    restful.setName(fullPath);
                    restful.setRequestPath(fullPath);
                });
            }

            public List<IRestful> getRestfulList() {
                return restfulList;
            }
        };
    }

    private List<IRestful> createRestfulByProject(Project project) {
        return RestfulResolver.createResolver().stream()
                .flatMap(resolver -> resolver.resolverByProject(project).stream())
                .collect(Collectors.toList());
    }

    private List<IRestful> createRestfulByModule(Module module) {
        return RestfulResolver.createResolver().stream()
                .flatMap(resolver -> resolver.resolverByModule(module).stream())
                .collect(Collectors.toList());
    }

}
