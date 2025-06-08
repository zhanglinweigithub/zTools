package com.zhanglinwei.zTools.restful.component;

import com.intellij.ide.util.gotoByName.ChooseByNameFilter;
import com.intellij.ide.util.gotoByName.ChooseByNamePopup;
import com.intellij.openapi.project.Project;
import com.zhanglinwei.zTools.common.enums.HttpMethod;
import com.zhanglinwei.zTools.restful.config.IRestfulConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class IRestfulChooseByNameFilter extends ChooseByNameFilter<HttpMethod> {

    public IRestfulChooseByNameFilter(final ChooseByNamePopup popup, IRestfulChooseByNameModel model, final Project project) {
        super(popup, model, IRestfulConfig.getInstance(project), project);
    }

    @Override
    protected String textForFilterValue(@NotNull HttpMethod httpMethod) {
        return httpMethod.name();
    }

    @Override
    protected @Nullable Icon iconForFilterValue(@NotNull HttpMethod httpMethod) {
        return null;
    }

    @Override
    protected @NotNull Collection<HttpMethod> getAllFilterValues() {
        return Arrays.stream(HttpMethod.values()).collect(Collectors.toList());
    }
}
