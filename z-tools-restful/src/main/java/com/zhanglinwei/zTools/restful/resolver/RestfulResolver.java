package com.zhanglinwei.zTools.restful.resolver;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.zhanglinwei.zTools.restful.model.IRestful;
import com.zhanglinwei.zTools.restful.resolver.impl.FeignRestfulResolver;
import com.zhanglinwei.zTools.restful.resolver.impl.SpringRestfulResolver;

import java.util.Arrays;
import java.util.List;

/**
 * RESTful 接口解析器。
 * <p>
 * 实现类负责从 Spring MVC / Feign 源码中收集 Mapping 方法。
 */
public interface RestfulResolver {

    /**
     * 按整个工程解析接口。
     *
     * @param project 当前工程
     * @return 工程内全部接口
     */
    List<IRestful> resolverByProject(Project project);

    /**
     * 按单个模块解析接口。
     *
     * @param module 当前模块
     * @return 模块内全部接口
     */
    List<IRestful> resolverByModule(Module module);

    /**
     * 创建全部解析器：Spring MVC 与 Feign。
     *
     * @return 解析器列表
     */
    static List<RestfulResolver> createResolver() {
        return Arrays.asList(new SpringRestfulResolver(), new FeignRestfulResolver());
    }
}
