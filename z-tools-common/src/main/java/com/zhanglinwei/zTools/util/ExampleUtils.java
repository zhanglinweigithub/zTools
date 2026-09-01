package com.zhanglinwei.zTools.util;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiNameValuePair;
import com.intellij.psi.PsiType;
import com.zhanglinwei.zTools.common.constants.NormalType;
import com.zhanglinwei.zTools.doc.apidoc.constant.JacksonAnnotation;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.zhanglinwei.zTools.common.constants.SpringPool.*;

public final class ExampleUtils {

    private ExampleUtils() {}

    public static String createExampleAsString(PsiType psiType, PsiAnnotation[] annotations) {
        // 普通类型
        if (TypeUtils.isNormalType(psiType)) {
            Object normalValue = normalValue(psiType.getPresentableText(), Arrays.asList(annotations));
            return normalValue != null ? normalValue.toString() : EMPTY;
        }

        // 可迭代类型
        if (TypeUtils.isIterableType(psiType)) {
            Object wrapped = null;
            TypeUtils.NestedInfo nestedInfo = TypeUtils.deepExtractIterableType(psiType);
            PsiType realType = nestedInfo.getRealType();

            // 普通类型
            if (TypeUtils.isNormalType(realType)) {
                Object example = normalValue(realType.getPresentableText(), Arrays.asList(annotations));
                wrapped = NestedUtils.wrapWithNesting(example + COMMA_SPACE + example, nestedInfo.getDepth());
            } else {
                wrapped = NestedUtils.wrapWithNesting(Collections.emptyMap(), nestedInfo.getDepth());
            }

            return JsonUtil.toJsonString(wrapped, false);
        }

        return JsonUtil.toJsonString(Collections.emptyMap(), false);
    }

    public static Object createNormalExample(PsiType psiType, PsiAnnotation[] annotations) {
        TypeUtils.NestedInfo nestedInfo = TypeUtils.deepExtractIterableType(psiType);
        PsiType realType = nestedInfo.getRealType();

        if (TypeUtils.isNormalType(realType)) {
            Object normalValue = normalValue(realType.getPresentableText(), Arrays.asList(annotations));
            if (normalValue != null) {
                Object wrapped = NestedUtils.wrapWithNesting(normalValue, nestedInfo.getDepth());
                return JsonUtil.toJsonString(wrapped, false);
            }
        }

        return NA;
    }

    public static Object createSimpleJsonExample(PsiType psiType, PsiAnnotation[] annotations) {
        return normalValue(psiType.getPresentableText(), Arrays.asList(annotations));
    }

    private static Object normalValue(String typeName, List<PsiAnnotation> annotations) {
        if(Arrays.asList("LocalDate", "LocalDateTime", "Date").contains(typeName)) {
            for (PsiAnnotation annotation : annotations) {
                if(annotation.getText().contains(JacksonAnnotation.JsonFormat)) {
                    PsiNameValuePair[] attributes = annotation.getParameterList().getAttributes();
                    if (attributes.length >= 1) {
                        for (PsiNameValuePair attribute : attributes) {
                            if ("pattern".equals(attribute.getAttributeName())) {
                                return attribute.getLiteralValue();
                            }
                        }
                    }
                }
            }
        }

        return NormalType.get(typeName);
    }
}
