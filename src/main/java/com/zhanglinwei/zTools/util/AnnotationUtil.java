package com.zhanglinwei.zTools.util;

import com.intellij.psi.*;
import com.zhanglinwei.zTools.common.constants.FQN;
import com.zhanglinwei.zTools.common.constants.WebAnnotation;
import com.zhanglinwei.zTools.common.enums.HttpMethod;

import java.util.*;

import static com.zhanglinwei.zTools.common.constants.SpringPool.*;

/**
 * 注解工具类
 */
public final class AnnotationUtil {

    private static List<String> REQUIRED_ANNOTATION_LIST = Arrays.asList("@NotNull", "@NotBlank", "@NotEmpty");

    private AnnotationUtil(){}

    /**
     * 方法是否存在xxxMapping注解
     */
    public static boolean hasMappingAnnotation(PsiMethod psiMethod) {
        if (psiMethod != null) {
            PsiAnnotation[] annotations = psiMethod.getAnnotations();
            for (PsiAnnotation annotation : annotations) {
                if (annotation.getText().contains("Mapping")) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 获得请求类型
     */
    public static String extractRequestTypeFromAnnotation(PsiAnnotation classMapping, PsiAnnotation methodMapping) {
        Set<String> requestTypeList = extractRequestTypeListFromAnnotation(classMapping, methodMapping);
        return String.join(COMMA_SPACE, requestTypeList);
    }

    public static Set<String> extractRequestTypeListFromAnnotation(PsiAnnotation classMapping, PsiAnnotation methodMapping) {
        Set<String> requestTypeList = new HashSet<>();
        requestTypeList.addAll(extractRequestTypeListByMappingAnnotation(classMapping));
        requestTypeList.addAll(extractRequestTypeListByMappingAnnotation(methodMapping));

        return requestTypeList;
    }

    /**
     * 根据mapping注解，获得请求类型集合
     */
    public static List<String> extractRequestTypeListByMappingAnnotation(PsiAnnotation mappingAnnotation) {
        List<String> requestTypeList = new ArrayList<>();
        if (isRequestMapping(mappingAnnotation)) {
            PsiNameValuePair[] attributes = mappingAnnotation.getParameterList().getAttributes();
            for (PsiNameValuePair pair : attributes) {
                if ("method".equals(pair.getAttributeName())) {
                    PsiAnnotationMemberValue attrValue = pair.getValue();
                    if (attrValue != null) {
                        String text = attrValue.getText();
                        if (AssertUtils.isNotBlank(text)) {
                            if (text.contains(HttpMethod.GET.name())) {
                                requestTypeList.add(HttpMethod.GET.name());
                            }
                            if (text.contains(HttpMethod.POST.name())) {
                                requestTypeList.add(HttpMethod.POST.name());
                            }
                            if (text.contains(HttpMethod.PUT.name())) {
                                requestTypeList.add(HttpMethod.PUT.name());
                            }
                            if (text.contains(HttpMethod.PATCH.name())) {
                                requestTypeList.add(HttpMethod.PATCH.name());
                            }
                            if (text.contains(HttpMethod.DELETE.name())) {
                                requestTypeList.add(HttpMethod.DELETE.name());
                            }
                        }
                    }
                }
            }
        } else {
            requestTypeList.add(extractRequestTypeStringByMappingText(mappingAnnotation.getText()));
        }

        return requestTypeList;
    }

    /**
     * 根据mapping注解，获得请求类型字符串
     */
    private static String extractRequestTypeStringByMappingText(String text) {
        if (text.startsWith(WebAnnotation.GetMapping)) {
            return HttpMethod.GET.name();
        }
        if (text.startsWith(WebAnnotation.PutMapping)) {
            return HttpMethod.PUT.name();
        }
        if (text.startsWith(WebAnnotation.DeleteMapping)) {
            return HttpMethod.DELETE.name();
        }
        if (text.startsWith(WebAnnotation.PatchMapping)) {
            return HttpMethod.PATCH.name();
        }

        return HttpMethod.POST.name();
    }

    /**
     * 是否@RequestMapping注解
     */
    public static boolean isRequestMapping(PsiAnnotation annotation) {
        return annotation != null && annotation.getText().contains(WebAnnotation.RequestMapping);
    }

    public static boolean isXxxRequestMapping(PsiAnnotation annotation) {
        return annotation != null && annotation.getText().contains("Mapping");
    }

    /**
     * 获取xxxMapping注解请求路径
     */
    public static String extractPathFromAnnotation(PsiAnnotation mappingAnnotation) {
        if (isXxxRequestMapping(mappingAnnotation)) {
            PsiNameValuePair[] attributes = mappingAnnotation.getParameterList().getAttributes();
            if (attributes.length != 0) {
                if (attributes.length == 1 && attributes[0].getName() == null) {
                    return CommonUtils.appendSlash(attributes[0].getLiteralValue());
                }

                for (PsiNameValuePair attr : attributes) {
                    String attrName = attr.getAttributeName();

                    if ("value".equals(attrName) || "path".equals(attrName)) {
                        PsiAnnotationMemberValue attrValue = attr.getValue();
                        if (attrValue != null) {
                            String text = attrValue.getText().replace("{\"", EMPTY).replace("\"}", EMPTY).replace("\"", EMPTY);
                            if (text.contains(COMMA)) {
                                return CommonUtils.appendSlash(text.split(COMMA)[0]);
                            }
                            return CommonUtils.appendSlash(text);
                        }
                    }
                }
            }
        }

        return EMPTY;
    }

    /**
     * 根据注解名称查找注解
     */
    public static PsiAnnotation findAnnotationByName(PsiAnnotation[] annotations, String annotationName) {
        if (annotations != null && AssertUtils.isNotBlank(annotationName)) {
            for (PsiAnnotation annotation : annotations) {
                if (annotation.getText().contains(annotationName)) {
                    return annotation;
                }
            }
        }

        return null;
    }

    /**
     * 获取xxxMapping注解
     */
    public static PsiAnnotation findXxxMappingAnnotation(PsiAnnotation[] annotations) {
        return findAnnotationByName(annotations, "Mapping");
    }

    /** 是否存在 Controller */
    public static boolean hasController(PsiClass psiClass) {
        if (psiClass == null) {
            return false;
        }

        PsiModifierList modifierList = psiClass.getModifierList();
        if (modifierList == null) {
            return false;
        }
        if (modifierList.getText().contains(WebAnnotation.Controller)) {
            return true;
        }
        if (modifierList.findAnnotation(FQN.Controller) != null) {
            return true;
        }
        if (modifierList.findAnnotation(FQN.RestController) != null) {
            return true;
        }

        return false;
    }

    /** 是否必填 */
    public static boolean isRequired(PsiAnnotation psiAnnotation) {
        if (psiAnnotation == null) {
            return false;
        }

        PsiAnnotationMemberValue required = psiAnnotation.findAttributeValue("required");
        if (required != null && TRUE.equals(required.getText())) {
            return true;
        }
        if (REQUIRED_ANNOTATION_LIST.contains(psiAnnotation.getText().split("\\(")[0])) {
            return true;
        }

        return false;
    }

    /** 是否必填 */
    public static boolean isRequired(PsiAnnotation[] psiAnnotations) {
        return psiAnnotations != null && Arrays.stream(psiAnnotations)
                .anyMatch(AnnotationUtil::isRequired);
    }

    /** 解析参数名称 */
    public static String extractParamName(String originName, PsiAnnotation[] annotations) {
        if (annotations == null || annotations.length == 0 || AssertUtils.isBlank(originName)) {
            return originName;
        }

        for (PsiAnnotation annotation : annotations) {
            String annotationText = annotation.getText();
            if (
                    annotationText.contains(WebAnnotation.RequestParam) ||
                    annotationText.contains(WebAnnotation.PathVariable) ||
                    annotationText.contains(WebAnnotation.RequestPart) ||
                    annotationText.contains(WebAnnotation.RequestHeader)
            ) {
                String value = resolveAttributeAsString(annotation, "value");
                if (AssertUtils.isNotBlank(value)) {
                    return value;
                }
                break;
            }
        }

        return originName;
    }

    /** 解析注解属性 */
    private static String resolveAttributeAsString(PsiAnnotation annotation, String attribute) {
        if (annotation == null || AssertUtils.isBlank(attribute)) {
            return null;
        }

        for (PsiNameValuePair pair : annotation.getParameterList().getAttributes()) {
            if (attribute.equals(pair.getAttributeName())) {
                return pair.getLiteralValue();
            }
        }

        return null;
    }

}
