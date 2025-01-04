package com.zhanglinwei.zTools.doc.apidoc.model;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.zhanglinwei.zTools.doc.apidoc.constant.WebAnnotation;
import com.zhanglinwei.zTools.util.AnnotationUtil;
import com.zhanglinwei.zTools.util.AssertUtils;
import com.zhanglinwei.zTools.util.DesUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 类对象
 */
public class ClassInfo {

    private String className;

    private Project project;

    private String classDesc;

    private String requestPath = "";

    private PsiAnnotation controllerAnnotation;

    private PsiAnnotation responseBodyAnnotation;

    private PsiAnnotation xxxRequestMappingAnnotation;

    private List<MethodInfo> methods = new ArrayList<>();

    public ClassInfo(PsiClass psiClass) {
        this.project = psiClass.getProject();
        // 1、设置Controller、xxxMapping、ResponseBody注解
        for (PsiAnnotation annotation : psiClass.getAnnotations()) {
            String text = annotation.getText();
            if (text.endsWith(WebAnnotation.Controller)) {
                this.controllerAnnotation = annotation;
            } else if (text.contains(WebAnnotation.RequestMapping)) {
                this.xxxRequestMappingAnnotation = annotation;
            } else if (text.contains(WebAnnotation.ResponseBody)) {
                this.responseBodyAnnotation = annotation;
            }
        }
        // 2、设置请求路径
        this.requestPath = AnnotationUtil.getPathFromAnnotation(this.xxxRequestMappingAnnotation);
        // 3、设置class名称
        this.className = psiClass.getName();
        // 4、设置class注释
        String description = DesUtil.getDescription(psiClass.getDocComment(), psiClass.getAnnotations());
        this.classDesc = AssertUtils.isBlank(description) ? this.className : description;
    }

    public ClassInfo(PsiClass psiClass, boolean allMethod) {
        this(psiClass);

        if (allMethod) {
            PsiMethod[] allMethodList = psiClass.getMethods();
            for (PsiMethod psiMethod : allMethodList) {
                if (AnnotationUtil.hasMappingAnnotation(psiMethod)) {
                    this.methods.add(new MethodInfo(psiMethod));
                }
            }
        }
    }

    public ClassInfo(PsiClass psiClass, MethodInfo method) {
        this(psiClass, false);
        this.methods = Collections.singletonList(method);
    }

    public String getTitle() {
        return AssertUtils.isNotEmpty(getClassDesc()) ? getClassDesc() : getClassName();
    }

    public boolean hasRestController() {
        return hasController() && this.controllerAnnotation.getText().contains("Rest");
    }

    public boolean hasController() {
        return this.controllerAnnotation != null;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassDesc() {
        return classDesc;
    }

    public void setClassDesc(String classDesc) {
        this.classDesc = classDesc;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public void setRequestPath(String requestPath) {
        this.requestPath = requestPath;
    }

    public PsiAnnotation getControllerAnnotation() {
        return controllerAnnotation;
    }

    public void setControllerAnnotation(PsiAnnotation controllerAnnotation) {
        this.controllerAnnotation = controllerAnnotation;
    }

    public PsiAnnotation getResponseBodyAnnotation() {
        return responseBodyAnnotation;
    }

    public void setResponseBodyAnnotation(PsiAnnotation responseBodyAnnotation) {
        this.responseBodyAnnotation = responseBodyAnnotation;
    }

    public PsiAnnotation getXxxRequestMappingAnnotation() {
        return xxxRequestMappingAnnotation;
    }

    public void setXxxRequestMappingAnnotation(PsiAnnotation xxxRequestMappingAnnotation) {
        this.xxxRequestMappingAnnotation = xxxRequestMappingAnnotation;
    }

    public List<MethodInfo> getMethods() {
        return methods;
    }

    public void setMethods(List<MethodInfo> methods) {
        this.methods = methods;
    }

}
