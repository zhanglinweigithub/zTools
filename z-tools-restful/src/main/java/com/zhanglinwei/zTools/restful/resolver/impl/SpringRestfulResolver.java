package com.zhanglinwei.zTools.restful.resolver.impl;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.impl.java.stubs.index.JavaStubIndexKeys;
import com.intellij.psi.impl.search.JavaSourceFilterScope;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.zhanglinwei.zTools.annotation.model.AnnotationDefinition;
import com.zhanglinwei.zTools.annotation.parse.AnnotationParser;
import com.zhanglinwei.zTools.annotation.web.MappingAnnotation;
import com.zhanglinwei.zTools.annotation.web.RequestPaths;
import com.zhanglinwei.zTools.annotation.web.WebAnnotationParser;
import com.zhanglinwei.zTools.enums.HttpMethod;
import com.zhanglinwei.zTools.restful.model.IRestful;
import com.zhanglinwei.zTools.restful.resolver.AbstractRestfulResolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 从 {@code @Controller} / {@code @RestController} 收集 Mapping 方法。
 * 只读注解定义，不展开参数类型树；类上没有 {@code @RequestMapping} 时仍解析方法上的 Mapping。
 */
public class SpringRestfulResolver extends AbstractRestfulResolver {

    /** StubIndex 按简单名检索，命中后再用 FQN 过滤。 */
    private static final List<String> CONTROLLER_SIMPLE_NAMES = Collections.unmodifiableList(Arrays.asList(
            WebAnnotationParser.CONTROLLER.shortName(), WebAnnotationParser.REST_CONTROLLER.shortName()
    ));

    @Override
    public List<IRestful> searchIRestful(Project project, GlobalSearchScope globalSearchScope) {
        if (project == null || globalSearchScope == null) {
            return Collections.emptyList();
        }
        List<IRestful> result = new ArrayList<IRestful>();
        JavaSourceFilterScope scope = new JavaSourceFilterScope(globalSearchScope);
        for (int i = 0; i < CONTROLLER_SIMPLE_NAMES.size(); i++) {
            Collection<PsiAnnotation> annotations = StubIndex.getElements(
                    JavaStubIndexKeys.ANNOTATIONS,
                    CONTROLLER_SIMPLE_NAMES.get(i),
                    project,
                    scope,
                    PsiAnnotation.class
            );
            for (PsiAnnotation annotation : annotations) {
                PsiClass psiClass = owner(annotation, PsiClass.class);
                if (psiClass != null) {
                    result.addAll(createRestfuls(psiClass));
                }
            }
        }
        return result;
    }

    private static List<IRestful> createRestfuls(PsiClass psiClass) {
        List<AnnotationDefinition> classAnnotations = AnnotationParser.of(psiClass);
        if (!WebAnnotationParser.isController(classAnnotations)) {
            return Collections.emptyList();
        }

        MappingAnnotation classMapping = WebAnnotationParser.mapping(classAnnotations);
        return Arrays.stream(psiClass.getMethods())
                .filter(method -> !method.isConstructor())
                .filter(method -> psiClass == method.getContainingClass())
                .flatMap(method -> createRestfuls(method, classMapping).stream())
                .collect(Collectors.toList());
    }

    private static List<IRestful> createRestfuls(PsiMethod method, MappingAnnotation classMapping) {
        MappingAnnotation methodMapping = WebAnnotationParser.mapping(AnnotationParser.of(method));
        if (methodMapping == null) {
            return Collections.emptyList();
        }

        List<String> paths = RequestPaths.combine(
                classMapping == null ? null : classMapping.paths(),
                methodMapping.paths()
        );
        List<HttpMethod> verbs = verbs(classMapping, methodMapping);
        List<IRestful> items = new ArrayList<>(paths.size() * verbs.size());
        for (String path : paths) {
            for (HttpMethod verb : verbs) {
                items.add(new IRestful(method, path, verb));
            }
        }
        return items;
    }

    /** 方法上写出的 method 优先，否则用类上的；都未写则为 {@link HttpMethod#NONE}。 */
    private static List<HttpMethod> verbs(MappingAnnotation classMapping, MappingAnnotation methodMapping) {
        List<String> names = methodMapping.methods();
        if (names == null || names.isEmpty()) {
            names = classMapping == null ? null : classMapping.methods();
        }
        if (names == null || names.isEmpty()) {
            return Collections.singletonList(HttpMethod.NONE);
        }
        List<HttpMethod> verbs = new ArrayList<HttpMethod>(names.size());
        for (int i = 0; i < names.size(); i++) {
            HttpMethod httpMethod = HttpMethod.of(names.get(i));
            if (httpMethod != null) {
                verbs.add(httpMethod);
            }
        }
        return verbs.isEmpty() ? Collections.singletonList(HttpMethod.NONE) : verbs;
    }
}
