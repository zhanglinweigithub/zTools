package com.zhanglinwei.zTools.util;

import com.intellij.psi.*;

public final class ModifierUtils {

    private ModifierUtils(){}

    /** 是否 static */
    public static boolean isStatic(PsiField psiField) {
        if (psiField == null) {
            return false;
        }

        PsiModifierList modifierList = psiField.getModifierList();
        return modifierList != null && modifierList.hasExplicitModifier(PsiModifier.STATIC);
    }

    /** 是否 final */
    public static boolean isFinal(PsiField psiField) {
        if (psiField == null) {
            return false;
        }

        PsiModifierList modifierList = psiField.getModifierList();
        return modifierList != null && modifierList.hasExplicitModifier(PsiModifier.FINAL);
    }

    /** 是否 public */
    public static boolean isPublic(PsiField psiField) {
        if (psiField == null) {
            return false;
        }

        PsiModifierList modifierList = psiField.getModifierList();
        return modifierList != null && modifierList.hasExplicitModifier(PsiModifier.PUBLIC);
    }

    /** 是否 protected */
    public static boolean isProtected(PsiField psiField) {
        if (psiField == null) {
            return false;
        }

        PsiModifierList modifierList = psiField.getModifierList();
        return modifierList != null && modifierList.hasExplicitModifier(PsiModifier.PROTECTED);
    }

    /** 是否 private */
    public static boolean isPrivate(PsiField psiField) {
        if (psiField == null) {
            return false;
        }

        PsiModifierList modifierList = psiField.getModifierList();
        return modifierList != null && modifierList.hasExplicitModifier(PsiModifier.PRIVATE);
    }

    /** 是否忽略 */
    public static boolean isIgnored(PsiField psiField) {
        return isIgnored(psiField.getAnnotations());
    }

    /** 是否 static */
    public static boolean isStatic(PsiMethod psiMethod) {
        if (psiMethod == null) {
            return false;
        }

        PsiModifierList modifierList = psiMethod.getModifierList();
        return modifierList.hasExplicitModifier(PsiModifier.STATIC);
    }

    /** 是否 final */
    public static boolean isFinal(PsiMethod psiMethod) {
        if (psiMethod == null) {
            return false;
        }

        PsiModifierList modifierList = psiMethod.getModifierList();
        return modifierList.hasExplicitModifier(PsiModifier.FINAL);
    }

    /** 是否 public */
    public static boolean isPublic(PsiMethod psiMethod) {
        if (psiMethod == null) {
            return false;
        }

        PsiModifierList modifierList = psiMethod.getModifierList();
        return modifierList.hasExplicitModifier(PsiModifier.PUBLIC);
    }

    /** 是否 protected */
    public static boolean isProtected(PsiMethod psiMethod) {
        if (psiMethod == null) {
            return false;
        }

        PsiModifierList modifierList = psiMethod.getModifierList();
        return modifierList.hasExplicitModifier(PsiModifier.PROTECTED);
    }

    /** 是否 private */
    public static boolean isPrivate(PsiMethod psiMethod) {
        if (psiMethod == null) {
            return false;
        }

        PsiModifierList modifierList = psiMethod.getModifierList();
        return modifierList.hasExplicitModifier(PsiModifier.PRIVATE);
    }

    /** 是否 abstract */
    public static boolean isAbstract(PsiMethod psiMethod) {
        if (psiMethod == null) {
            return false;
        }

        PsiModifierList modifierList = psiMethod.getModifierList();
        return modifierList.hasExplicitModifier(PsiModifier.ABSTRACT);
    }

    /** 是否忽略 */
    public static boolean isIgnored(PsiMethod psiMethod) {
        return isIgnored(psiMethod.getAnnotations());
    }

    /** 是否 static */
    public static boolean isStatic(PsiClass psiClass) {
        if (psiClass == null) {
            return false;
        }

        PsiModifierList modifierList = psiClass.getModifierList();
        return modifierList != null && modifierList.hasExplicitModifier(PsiModifier.STATIC);
    }

    /** 是否 final */
    public static boolean isFinal(PsiClass psiClass) {
        if (psiClass == null) {
            return false;
        }

        PsiModifierList modifierList = psiClass.getModifierList();
        return modifierList != null && modifierList.hasExplicitModifier(PsiModifier.FINAL);
    }

    /** 是否 public */
    public static boolean isPublic(PsiClass psiClass) {
        if (psiClass == null) {
            return false;
        }

        PsiModifierList modifierList = psiClass.getModifierList();
        return modifierList != null && modifierList.hasExplicitModifier(PsiModifier.PUBLIC);
    }

    /** 是否 protected */
    public static boolean isProtected(PsiClass psiClass) {
        if (psiClass == null) {
            return false;
        }

        PsiModifierList modifierList = psiClass.getModifierList();
        return modifierList != null && modifierList.hasExplicitModifier(PsiModifier.PROTECTED);
    }

    /** 是否 private */
    public static boolean isPrivate(PsiClass psiClass) {
        if (psiClass == null) {
            return false;
        }

        PsiModifierList modifierList = psiClass.getModifierList();
        return modifierList != null && modifierList.hasExplicitModifier(PsiModifier.PRIVATE);
    }

    /** 是否 abstract */
    public static boolean isAbstract(PsiClass psiClass) {
        if (psiClass == null) {
            return false;
        }

        PsiModifierList modifierList = psiClass.getModifierList();
        return modifierList != null && modifierList.hasExplicitModifier(PsiModifier.ABSTRACT);
    }

    /** 是否忽略 */
    public static boolean isIgnored(PsiClass psiClass) {
        return isIgnored(psiClass.getAnnotations());
    }

    private static boolean isIgnored(PsiAnnotation[] annotations) {
        if (annotations != null) {
            for (PsiAnnotation annotation : annotations) {
                String annotationText = annotation.getText();
                if (annotationText.contains("Ignore")) {
                    return true;
                }
            }
        }
        return false;
    }

}

