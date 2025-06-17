package com.zhanglinwei.zTools.restful.resolver.impl;

import com.google.common.collect.ImmutableSet;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.java.stubs.index.JavaStubIndexKeys;
import com.intellij.psi.impl.search.JavaSourceFilterScope;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.zhanglinwei.zTools.common.constants.WebAnnotation;
import com.zhanglinwei.zTools.common.enums.HttpMethod;
import com.zhanglinwei.zTools.restful.model.IRestful;
import com.zhanglinwei.zTools.restful.resolver.AbstractRestfulResolver;
import com.zhanglinwei.zTools.util.AnnotationUtil;
import com.zhanglinwei.zTools.util.CommonUtils;

import java.util.*;
import java.util.stream.Collectors;

public class SpringRestfulResolver extends AbstractRestfulResolver {

    private final Set<String> SUPPORT_ANNOTATION = ImmutableSet.of(
            WebAnnotation.Controller, WebAnnotation.RestController
    );

    @Override
    public List<IRestful> searchIRestful(Project project, GlobalSearchScope globalSearchScope) {
        return SUPPORT_ANNOTATION.stream()
                .flatMap(annotationName ->
                        StubIndex.getElements(JavaStubIndexKeys.ANNOTATIONS, annotationName, project, new JavaSourceFilterScope(globalSearchScope), PsiAnnotation.class)
                                .stream()
                                .map(psiAnnotation -> {
                                    PsiModifierList psiModifierList = (PsiModifierList) psiAnnotation.getParent();
                                    PsiElement psiElement = psiModifierList.getParent();
                                    return (PsiClass) psiElement;
                                })
                                .flatMap(psiClass -> createIRestful(psiClass).stream())
                )
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<IRestful> createIRestful(PsiClass psiClass) {
        return psiClass == null ? null : Arrays.stream(psiClass.getMethods())
                .flatMap(psiMethod -> createIRestful(psiMethod).stream())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

    }

    public List<IRestful> createIRestful(PsiMethod psiMethod) {
        PsiClass containingClass = psiMethod.getContainingClass();
        if (containingClass == null) {
            return Collections.emptyList();
        }

        PsiAnnotation classXxxMappingAnnotation = AnnotationUtil.getXxxMappingAnnotation(containingClass.getAnnotations());
        PsiAnnotation methodXxxMappingAnnotation = AnnotationUtil.getXxxMappingAnnotation(psiMethod.getAnnotations());
        if (classXxxMappingAnnotation == null || methodXxxMappingAnnotation == null) {
            return Collections.emptyList();
        }

        String classRequestPath = AnnotationUtil.getPathFromAnnotation(classXxxMappingAnnotation);
        String methodRequestPath = AnnotationUtil.getPathFromAnnotation(methodXxxMappingAnnotation);
        String requestPath = buildRequestPath(classRequestPath, methodRequestPath, psiMethod.getProject());

        Set<String> requestTypes = AnnotationUtil.getRequestTypeListFromAnnotation(classXxxMappingAnnotation, methodXxxMappingAnnotation);
        if (requestTypes.isEmpty()) {
            IRestful iRestful = new IRestful();
            iRestful.setPsiMethod(psiMethod);
            iRestful.setRequestPath(requestPath);
            iRestful.setRequestType(HttpMethod.NONE);
            iRestful.setName(requestPath);
            return Collections.singletonList(iRestful);
        }

        return requestTypes.stream()
                .map(requestType -> {
                    IRestful iRestful = new IRestful();
                    iRestful.setPsiMethod(psiMethod);
                    iRestful.setRequestPath(requestPath);
                    iRestful.setRequestType(HttpMethod.of(requestType));
                    iRestful.setName(requestPath);
                    return iRestful;
                }).collect(Collectors.toList());
    }

    private String buildRequestPath(String classRequestPath, String methodRequestPath, Project project) {
        String basePath = CommonUtils.buildPath(classRequestPath, methodRequestPath);
//        return CommonUtils.buildPath(globalRequestPrefix(project), basePath);
        return basePath;
    }

}
