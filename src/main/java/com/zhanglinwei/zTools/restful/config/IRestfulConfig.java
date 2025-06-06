package com.zhanglinwei.zTools.restful.config;

import com.intellij.ide.util.gotoByName.ChooseByNameFilterConfiguration;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.openapi.project.Project;
import com.zhanglinwei.zTools.common.enums.HttpMethod;

@State(name = "IRestfulConfig", storages = @Storage(StoragePathMacros.WORKSPACE_FILE))
public class IRestfulConfig extends ChooseByNameFilterConfiguration<HttpMethod> {

    public static IRestfulConfig getInstance(Project project) {
        return project.getService(IRestfulConfig.class);
    }

    @Override
    protected String nameForElement(HttpMethod type) {
        return type.name();
    }
}
