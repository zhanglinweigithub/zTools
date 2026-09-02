package com.zhanglinwei.zTools.configure.config;

import com.intellij.ide.util.gotoByName.ChooseByNameFilterConfiguration;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.zhanglinwei.zTools.configure.constants.ZToolsConstant;
import com.zhanglinwei.zTools.common.enums.HttpMethod;

@Service(value = Service.Level.PROJECT)
@State(name = "RestfulConfig", storages = {@Storage(ZToolsConstant.STORAGE_FILE)})
public final class RestfulConfig extends ChooseByNameFilterConfiguration<HttpMethod> {

    public static RestfulConfig getInstance(Project project) {
        return project.getService(RestfulConfig.class);
    }

    @Override
    protected String nameForElement(HttpMethod type) {
        return type.name();
    }
}
