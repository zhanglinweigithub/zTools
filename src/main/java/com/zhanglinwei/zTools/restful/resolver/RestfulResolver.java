package com.zhanglinwei.zTools.restful.resolver;

import com.google.common.collect.Lists;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.zhanglinwei.zTools.restful.model.IRestful;
import com.zhanglinwei.zTools.restful.resolver.impl.SpringRestfulResolver;
import com.zhanglinwei.zTools.restful.resolver.impl.WebFluxRestfulResolver;

import java.util.List;

public interface RestfulResolver {

    List<IRestful> resolverByProject(Project project);

    List<IRestful> resolverByModule(Module module);

    static List<RestfulResolver> createResolver() {
        return Lists.newArrayList(
                new SpringRestfulResolver(), new WebFluxRestfulResolver()
        );
    }

}
