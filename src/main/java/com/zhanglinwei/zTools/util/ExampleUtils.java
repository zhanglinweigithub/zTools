package com.zhanglinwei.zTools.util;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiNameValuePair;
import com.intellij.psi.PsiType;
import com.zhanglinwei.zTools.common.constants.NormalType;
import com.zhanglinwei.zTools.doc.apidoc.constant.JacksonAnnotation;
import com.zhanglinwei.zTools.doc.apidoc.model.JavaProperty;

import java.util.Arrays;
import java.util.List;

public final class ExampleUtils {

    private ExampleUtils() {}

    public static String normalExample(JavaProperty javaProperty) {
        PsiType psiType = javaProperty.getPsiType();
        String typeName = psiType.getPresentableText();

        if (TypeUtils.isNormalType(psiType)) {
            return normalValue(typeName, Arrays.asList(javaProperty.getPsiAnnotations()));
        }

        return "?";
    }

    public static String normalIterableExample(JavaProperty javaProperty) {
        PsiType psiType = javaProperty.getPsiType();
        String typeName = psiType.getPresentableText();

        if (TypeUtils.isIterableType(psiType)) {
            TypeUtils.NestedInfo nestedInfo = TypeUtils.deepExtractIterableType(psiType);
            PsiType realType = nestedInfo.getRealType();
            if (realType == null) {
                return "[]";
            }
            if (TypeUtils.isNormalType(realType)) {
                String value = normalValue(typeName, Arrays.asList(javaProperty.getPsiAnnotations()));

                for (int i = 0; i < nestedInfo.getDepth(); i++) {
                    if (value == null) {
                        return "?";
                    } else {
                        value = "[" + value + ", " + value + "]";
                    }
                }

                return AssertUtils.isBlank(value) ? "[]" : value;
            }
        }

        return null;
    }

    private static String normalValue(String typeName, List<PsiAnnotation> annotations) {
        if(Arrays.asList("LocalDate", "LocalDateTime", "Date").contains(typeName)) {
            for (PsiAnnotation annotation : annotations) {
                if(annotation.getText().contains(JacksonAnnotation.JsonFormat)) {
                    PsiNameValuePair[] attributes = annotation.getParameterList().getAttributes();
                    if (attributes.length >= 1) {
                        for (PsiNameValuePair attribute : attributes) {
                            if ("pattern".equals(attribute.getName())) {
                                return attribute.getLiteralValue();
                            }
                        }
                    }
                }
            }
        }
        Object value = NormalType.get(typeName);
        return value == null ? "?" : value.toString();
    }
}
