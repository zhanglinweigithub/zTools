package com.zhanglinwei.zTools.restful.resolver;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.zhanglinwei.zTools.restful.model.IRestful;
import com.zhanglinwei.zTools.restful.resolver.impl.FeignRestfulResolver;
import com.zhanglinwei.zTools.restful.resolver.impl.SpringRestfulResolver;

import java.util.Arrays;
import java.util.List;

public interface RestfulResolver {

    List<IRestful> resolverByProject(Project project);

    List<IRestful> resolverByModule(Module module);

    static List<RestfulResolver> createResolver() {
        return Arrays.asList(new SpringRestfulResolver(), new FeignRestfulResolver());
    }
}
