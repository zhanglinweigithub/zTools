package com.zhanglinwei.zTools.restful.component;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.ide.util.gotoByName.CustomMatcherModel;
import com.intellij.ide.util.gotoByName.FilteringGotoByModel;
import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.psi.codeStyle.MinusculeMatcher;
import com.intellij.psi.codeStyle.NameUtil;
import com.zhanglinwei.zTools.common.enums.HttpMethod;
import com.zhanglinwei.zTools.restful.matcher.AntPathMatcher;
import com.zhanglinwei.zTools.restful.model.IRestful;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;

public class IRestfulChooseByNameModel extends FilteringGotoByModel<HttpMethod> implements DumbAware, CustomMatcherModel {

    private final String CURRENT_MODULE_PERSISTENT_KEY = "CURRENT_MODULE_PERSISTENT_KEY";
    private final Project currentProject;

    public IRestfulChooseByNameModel(@NotNull Project project, @NotNull ChooseByNameContributor contributors) {
        super(project, Collections.singletonList(contributors));
        this.currentProject = project;
    }

    @Override
    protected @Nullable HttpMethod filterValueFor(NavigationItem navigationItem) {
        if (navigationItem instanceof IRestful) {
            String requestType = ((IRestful) navigationItem).getRequestTypeList().get(0);
            return HttpMethod.of(requestType);
        }

        return null;
    }

    @Override
    public @Nls(capitalization = Nls.Capitalization.Sentence) String getPromptText() {
        return "Input URL Path";
    }

    @Override
    public @NotNull @NlsContexts.Label String getNotInMessage() {
        return "Not found";
    }

    @Override
    public @NotNull @NlsContexts.Label String getNotFoundMessage() {
        return "Not found";
    }

    @Override
    public @Nullable @NlsContexts.Label String getCheckBoxName() {
        return "Current Module";
    }

    @Override
    public boolean loadInitialCheckBoxState() {
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance(currentProject);
        return propertiesComponent.isTrueValue(CURRENT_MODULE_PERSISTENT_KEY);
    }

    @Override
    public void saveInitialCheckBoxState(boolean state) {
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance(currentProject);
        propertiesComponent.setValue(CURRENT_MODULE_PERSISTENT_KEY, state);
    }

    @Override
    public String @NotNull [] getSeparators() {
        return new String[]{"/", "?"};
    }

    @Override
    public @Nullable String getFullName(@NotNull Object element) {
        return getElementName(element);
    }

    @Override
    public boolean willOpenEditor() {
        return true;
    }

    @Override
    public boolean matches(@NotNull String item, @NotNull String input) {
        if(input.equals("/")) {
            return true;
        }

        MinusculeMatcher matcher = NameUtil.buildMatcher("*" + input, NameUtil.MatchingCaseSensitivity.NONE);
        boolean matches = matcher.matches(item);
        if (!matches) {
            AntPathMatcher pathMatcher = new AntPathMatcher();
            matches = pathMatcher.match(item,input);
        }
        return matches;
    }

}
