package com.zhanglinwei.zTools.restful.resolver;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.search.GlobalSearchScope;
import com.zhanglinwei.zTools.restful.model.IRestful;

import java.util.Collections;
import java.util.List;

/**
 * RESTful 解析器基类。
 * <p>
 * 按工程 / 模块范围委托给 {@link #searchIRestful}，并提供从注解回溯宿主元素的工具方法。
 */
public abstract class AbstractRestfulResolver implements RestfulResolver {

    /**
     * 在指定范围内搜索接口。
     *
     * @param project            当前工程
     * @param globalSearchScope  搜索范围（工程或模块）
     * @return 解析出的接口列表，无结果时为空列表
     */
    public abstract List<IRestful> searchIRestful(Project project, GlobalSearchScope globalSearchScope);

    /**
     * 按整个工程解析接口。
     *
     * @param project 当前工程，为 {@code null} 时返回空列表
     * @return 工程内全部接口
     */
    @Override
    public List<IRestful> resolverByProject(Project project) {
        return project == null ? Collections.emptyList() : searchIRestful(project, GlobalSearchScope.projectScope(project));
    }

    /**
     * 按单个模块解析接口。
     *
     * @param module 当前模块，为 {@code null} 时返回空列表
     * @return 模块内全部接口
     */
    @Override
    public List<IRestful> resolverByModule(Module module) {
        return module == null ? Collections.emptyList() : searchIRestful(module.getProject(), GlobalSearchScope.moduleScope(module));
    }

    /**
     * 从注解回溯到指定类型的宿主元素。
     * <p>
     * 注解 → {@link PsiModifierList} → 类或方法。类型不符则返回 {@code null}。
     *
     * @param annotation 注解 PSI
     * @param type       期望的宿主类型，如 {@link com.intellij.psi.PsiClass}、{@link com.intellij.psi.PsiMethod}
     * @param <T>        宿主元素类型
     * @return 匹配的宿主元素，找不到则为 {@code null}
     */
    protected static <T extends PsiElement> T owner(PsiAnnotation annotation, Class<T> type) {
        if (annotation == null) {
            return null;
        }
        // 注解挂在修饰符列表上，真正的宿主是列表的父元素
        PsiElement parent = annotation.getParent();
        if (!(parent instanceof PsiModifierList)) {
            return null;
        }
        PsiElement owner = parent.getParent();
        return type.isInstance(owner) ? type.cast(owner) : null;
    }
}
