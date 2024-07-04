package com.zhanglinwei.zTools.normal;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiUtil;
import com.zhanglinwei.zTools.constant.TypeEnum;
import com.zhanglinwei.zTools.constant.WebAnnotation;
import com.zhanglinwei.zTools.model.FieldInfo;
import com.zhanglinwei.zTools.util.CommonUtils;
import com.zhanglinwei.zTools.util.FieldUtil;

import java.util.*;
import java.util.stream.Collectors;

import static com.zhanglinwei.zTools.util.CommonUtils.NULL;

public class FieldFactory {


    public static FieldInfo buildByPsiType(PsiType psiType, FieldInfo parent, Project project) {
        FieldInfo fieldInfo = new FieldInfo();
        fieldInfo.setFieldType(FieldType.PSI_TYPE);
        fieldInfo.setPsiType(psiType);
        fieldInfo.setGenericTypeMap(resolveGenerics(psiType));
        fieldInfo.setProject(project);
        if (FieldUtil.isNormalType(psiType)) {
            fieldInfo.setParamType(TypeEnum.LITERAL);
        } else if (FieldUtil.isIterableType(psiType)) {
            fieldInfo.setParamType(TypeEnum.ARRAY);
        } else {
            fieldInfo.setParamType(TypeEnum.OBJECT);
        }
        fieldInfo.setExample(FieldUtil.buildExample(fieldInfo));
        fieldInfo.setParent(parent);
        if (needResolveChildren(psiType)) {
            fieldInfo.resolveChildren();
        }
        return fieldInfo;
    }

    public static FieldInfo buildField(String name, PsiType psiType, String desc, PsiAnnotation[] annotations, Project project) {
        FieldInfo fieldInfo = new FieldInfo();
        fieldInfo.setPsiType(psiType);
        fieldInfo.setGenericTypeMap(resolveGenerics(psiType));
        TypeEnum paramType = FieldUtil.getTypeEnum(psiType);
        fieldInfo.setParamType(paramType);
        fieldInfo.setProject(project);
        if (needResolveChildren(psiType)) {
            fieldInfo.resolveChildren();
        }
        RequireAndRange requireAndRange = FieldUtil.getRequireAndRange(annotations);
        String fieldName = getParamName(name, annotations);
        fieldInfo.setAnnotations(Arrays.asList(annotations));
        fieldInfo.setName(fieldName == null ? name : fieldName);
        fieldInfo.setRequired(CommonUtils.convertRequired(requireAndRange.isRequire()));
        fieldInfo.setRange(requireAndRange.getRange());
        fieldInfo.setDesc(desc == null ? " " : desc);
        fieldInfo.setExample(FieldUtil.buildExample(fieldInfo));
        return fieldInfo;
    }

    public static List<FieldInfo> buildFieldList(PsiParameterList parameterList, Map<String, String> paramNameDescMap, Project project) {
        PsiParameter[] psiParameterList = parameterList.getParameters();

        return Arrays.stream(psiParameterList).map(item -> {
            return buildField(item.getName(),
                    item.getType(),
                    paramNameDescMap.get(item.getName()),
                    item.getAnnotations(),
                    project);
        }).collect(Collectors.toList());
    }

    public static FieldInfo buildFieldWithParent(Project project,FieldInfo parent,String name, PsiType psiType, String desc, PsiAnnotation[] annotations) {
        FieldInfo fieldInfo = new FieldInfo();
        fieldInfo.setFieldType(FieldType.FIELD);
        fieldInfo.setProject(project);
        RequireAndRange requireAndRange = FieldUtil.getRequireAndRange(annotations);
        String fieldName = getParamName(name, annotations);
        fieldInfo.setName(fieldName == null ? NULL : fieldName);
        fieldInfo.setPsiType(psiType);
        fieldInfo.setRequired(CommonUtils.convertRequired(requireAndRange.isRequire()));
        fieldInfo.setRange(requireAndRange.getRange());
        fieldInfo.setDesc(desc == null ? " " : desc);
        fieldInfo.setAnnotations(Arrays.asList(annotations));
        fieldInfo.setGenericTypeMap(resolveGenerics(psiType));
        fieldInfo.setParent(parent);
        fieldInfo.setExample(FieldUtil.buildExample(fieldInfo));
        if (psiType != null) {
            if (FieldUtil.isNormalType(psiType)) {
                fieldInfo.setParamType(TypeEnum.LITERAL);
            } else if (FieldUtil.isIterableType(psiType)) {
                fieldInfo.setParamType(TypeEnum.ARRAY);
            } else {
                fieldInfo.setParamType(TypeEnum.OBJECT);
            }
            if (needResolveChildren(parent, psiType,fieldInfo)) {
                fieldInfo.resolveChildren();
            }else {
                fieldInfo.setChildren(null);
            }
        } else {
            fieldInfo.setParamType(TypeEnum.OBJECT);
        }
        return fieldInfo;
    }

    /** 是否需要解析children */
    public static boolean needResolveChildren(PsiType psiType) {
        String typeName = psiType.getPresentableText();
        if(FieldUtil.isNormalType(typeName)) {
            return false;
        }
        if(FieldUtil.isNormalCollectionType(typeName)) {
            return false;
        }
        if(FieldUtil.isNormalArrayType(typeName)) {
            return false;
        }
        if(FieldUtil.isEnum(psiType)) {
            return false;
        }
        if(FieldUtil.isMapType(typeName)) {
            return false;
        }
        if(FieldUtil.isMultipartType(psiType)) {
            return false;
        }

        return true;
    }

    private static boolean needResolveChildren(FieldInfo parent, PsiType psiType,FieldInfo fieldInfo) {
        if(!needResolveChildren(psiType)) {
            return false;
        }
        if (parent == null) {
            return true;
        }

        Set<PsiType> resolvedTypeSet = new HashSet<>();
        FieldInfo p = parent;
        while (p != null) {
            resolvedTypeSet.add(p.getPsiType());
            p = p.getParent();
        }
        TypeEnum paramType = fieldInfo.getParamType();
        if (TypeEnum.ARRAY.equals(paramType)) {
            if (psiType instanceof PsiArrayType) {
                psiType = ((PsiArrayType) psiType).getComponentType();
            } else {
                psiType = PsiUtil.extractIterableTypeParameter(psiType, false);
            }
        }
        if(resolvedTypeSet.contains(psiType)) {
            fieldInfo.setCycle(true);
            return false;
        }
        return true;
    }

    private static Map<PsiTypeParameter, PsiType> resolveGenerics(PsiType psiType){
        if(psiType instanceof PsiArrayType) {
            return new HashMap<>();
        }
        if(psiType instanceof PsiClassType) {
            PsiClassType psiClassType = (PsiClassType) psiType;
            PsiType[] realParameters = psiClassType.getParameters();
            PsiTypeParameter[] formParameters = psiClassType.resolve().getTypeParameters();
            int i = 0;
            Map<PsiTypeParameter, PsiType> map = new HashMap<>();
            for (PsiType realParameter : realParameters) {
                map.put(formParameters[i], realParameter);
                i ++;
            }
            return map;
        }
        return new HashMap<>();
    }

    private static String getParamName(String name, PsiAnnotation[] annotations) {
        PsiAnnotation requestParamAnnotation = null;
        PsiAnnotation requestHeaderAnnotation = null;
        PsiAnnotation pathVariableAnnotation = null;
        PsiAnnotation requestPartAnnotation = null;

        for (PsiAnnotation annotation : annotations) {
            String text = annotation.getText();
            if (text.contains(WebAnnotation.RequestParam)) {
                requestParamAnnotation = annotation;
            } else if (text.contains(WebAnnotation.RequestHeader)) {
                requestHeaderAnnotation = annotation;
            } else if (text.contains(WebAnnotation.PathVariable)) {
                pathVariableAnnotation = annotation;
            } else if (text.contains(WebAnnotation.RequestPart)) {
                requestPartAnnotation = annotation;
            }
        }

        PsiNameValuePair[] attributes = null;
        if (requestParamAnnotation != null) {
            attributes = requestParamAnnotation.getParameterList().getAttributes();
        } else if (requestHeaderAnnotation != null) {
            attributes = requestHeaderAnnotation.getParameterList().getAttributes();
        } else if (pathVariableAnnotation != null) {
            attributes = pathVariableAnnotation.getParameterList().getAttributes();
        } else if (requestPartAnnotation != null) {
            attributes = requestPartAnnotation.getParameterList().getAttributes();
        }

        if (attributes == null) {
            return name;
        }
        if (attributes.length == 1 && attributes[0].getName() == null) {
            return attributes[0].getLiteralValue();
        }

        for (PsiNameValuePair psiNameValuePair : attributes) {
            String pairName = psiNameValuePair.getName();
            if ("value".equals(pairName) || "name".equals(pairName)) {
                return psiNameValuePair.getLiteralValue();
            }
        }

        return name;
    }

}
