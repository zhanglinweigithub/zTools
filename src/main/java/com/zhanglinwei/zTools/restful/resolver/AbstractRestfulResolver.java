package com.zhanglinwei.zTools.restful.resolver;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.zhanglinwei.zTools.restful.model.IRestful;

import java.util.Collections;
import java.util.List;

public abstract class AbstractRestfulResolver implements RestfulResolver {

    public abstract List<IRestful> searchIRestful(Project project, GlobalSearchScope globalSearchScope);

    @Override
    public List<IRestful> resolverByProject(Project project) {
        return project == null ? Collections.emptyList() : searchIRestful(project, GlobalSearchScope.projectScope(project));
    }

    @Override
    public List<IRestful> resolverByModule(Module module) {
        return module == null ? Collections.emptyList() : searchIRestful(module.getProject(), GlobalSearchScope.moduleScope(module));
    }
}
