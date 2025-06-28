package com.zhanglinwei.zTools.restful.resolver.impl;

import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.zhanglinwei.zTools.restful.model.IRestful;
import com.zhanglinwei.zTools.restful.resolver.AbstractRestfulResolver;

import java.util.Collections;
import java.util.List;

public class WebFluxRestfulResolver extends AbstractRestfulResolver {


    @Override
    public List<IRestful> searchIRestful(Project project, GlobalSearchScope globalSearchScope) {
        return Collections.emptyList();
    }
}
