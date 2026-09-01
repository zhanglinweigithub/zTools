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
import com.zhanglinwei.zTools.annotation.web.RequestPaths;
import com.zhanglinwei.zTools.enums.HttpMethod;
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

    @Override
    public List<IRestful> searchIRestful(Project project, GlobalSearchScope globalSearchScope) {
        if (project == null || globalSearchScope == null) {
            return Collections.emptyList();
        }
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

    private static IRestful createRestful(PsiMethod method) {
        RequestLineAnnotation requestLine = FeignAnnotationParser.requestLine(AnnotationParser.of(method));
        if (requestLine == null) {
            return null;
        }
        HttpMethod httpMethod = HttpMethod.of(requestLine.httpMethod());
        if (httpMethod == null) {
            httpMethod = HttpMethod.NONE;
        }
        return new IRestful(method, RequestPaths.join(requestLine.path()), httpMethod);
    }
}
