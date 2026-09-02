package com.zhanglinwei.zTools.restful.resolver.impl;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.impl.java.stubs.index.JavaStubIndexKeys;
import com.intellij.psi.impl.search.JavaSourceFilterScope;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.zhanglinwei.zTools.annotation.feign.FeignAnnotationParser;
import com.zhanglinwei.zTools.annotation.feign.RequestLineAnnotation;
import com.zhanglinwei.zTools.annotation.parse.AnnotationParser;
import com.zhanglinwei.zTools.common.util.RequestPathUtils;
import com.zhanglinwei.zTools.common.enums.HttpMethod;
import com.zhanglinwei.zTools.restful.model.IRestful;
import com.zhanglinwei.zTools.restful.resolver.AbstractRestfulResolver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 收集 {@code feign.RequestLine} 标注的方法。
 */
public class FeignRestfulResolver extends AbstractRestfulResolver {

    /**
     * 在范围内查找 {@code @RequestLine} 方法。
     *
     * @param project            当前工程
     * @param globalSearchScope  搜索范围
     * @return 解析出的接口列表
     */
    @Override
    public List<IRestful> searchIRestful(Project project, GlobalSearchScope globalSearchScope) {
        if (project == null || globalSearchScope == null) {
            return Collections.emptyList();
        }
        // StubIndex 按 RequestLine 简单名检索
        Collection<PsiAnnotation> annotations = StubIndex.getElements(
                JavaStubIndexKeys.ANNOTATIONS,
                FeignAnnotationParser.REQUEST_LINE.shortName(),
                project,
                new JavaSourceFilterScope(globalSearchScope),
                PsiAnnotation.class
        );
        List<IRestful> result = new ArrayList<IRestful>();
        for (PsiAnnotation annotation : annotations) {
            PsiMethod method = owner(annotation, PsiMethod.class);
            if (method == null) {
                continue;
            }
            IRestful restful = createRestful(method);
            if (restful != null) {
                result.add(restful);
            }
        }
        return result;
    }

    /**
     * 从 {@code @RequestLine} 解析 HTTP 方法与路径。
     *
     * @param method Feign 方法
     * @return 接口项；注解无效时为 {@code null}
     */
    private static IRestful createRestful(PsiMethod method) {
        RequestLineAnnotation requestLine = FeignAnnotationParser.requestLine(AnnotationParser.of(method));
        if (requestLine == null) {
            return null;
        }
        HttpMethod httpMethod = HttpMethod.of(requestLine.httpMethod());
        if (httpMethod == null) {
            httpMethod = HttpMethod.NONE;
        }
        // 规范化路径，如 user/{id} → /user/{id}
        return new IRestful(method, RequestPathUtils.join(requestLine.path()), httpMethod);
    }
}
