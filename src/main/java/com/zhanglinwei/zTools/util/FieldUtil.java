package com.zhanglinwei.zTools.util;

import com.intellij.psi.*;

/**
 * 字段工具类
 */
public class FieldUtil {

    private FieldUtil(){}

    /** 是否static字段 */
    public static boolean isStaticField(PsiField psiField) {
        PsiModifierList modifierList = psiField.getModifierList();
        if(modifierList == null) {
            return false;
        }
        String modifierText = modifierList.getText();
        if (modifierText != null && modifierText.contains("static")) {
            return true;
        }
        for (PsiElement child : modifierList.getChildren()) {
            if(child instanceof PsiKeyword) {
                if(child.getText().equals("static")) {
                    return true;
                }
            }
        }
        return false;
    }

    /** 是否final字段 */
    public static boolean isFinalField(PsiField psiField) {
        PsiModifierList modifierList = psiField.getModifierList();
        if(modifierList == null) {
            return false;
        }
        String modifierText = modifierList.getText();
        if (modifierText != null && modifierText.contains("final")) {
            return true;
        }
        for (PsiElement child : modifierList.getChildren()) {
            if(child instanceof PsiKeyword) {
                if(child.getText().equals("final")) {
                    return true;
                }
            }
        }
        return false;
    }



    /** 是否忽略字段 */
    public static boolean isIgnoredField(PsiField psiField) {
        PsiAnnotation[] annotations = psiField.getAnnotations();
        for (PsiAnnotation annotation : annotations) {
            String qualifiedName = annotation.getText();
            if(qualifiedName.contains("Ignore")) {
                return true;
            }
        }
        return false;
    }


}

