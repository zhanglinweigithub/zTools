package com.zhanglinwei.zTools.restful.config;

import com.intellij.ide.util.gotoByName.ChooseByNameFilterConfiguration;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.openapi.project.Project;
import com.zhanglinwei.zTools.common.enums.HttpMethod;

@State(name = "IRestfulModalConfig", storages = @Storage(StoragePathMacros.WORKSPACE_FILE))
public class IRestfulModalConfig extends ChooseByNameFilterConfiguration<HttpMethod> {

    public static IRestfulModalConfig getInstance(Project project) {
        return project.getService(IRestfulModalConfig.class);
    }

    @Override
    protected String nameForElement(HttpMethod type) {
        return type.name();
    }

}
