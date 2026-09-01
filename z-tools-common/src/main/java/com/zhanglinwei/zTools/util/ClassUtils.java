package com.zhanglinwei.zTools.util;

import com.intellij.psi.PsiClass;

public final class ClassUtils {

    private ClassUtils() {}

    public static boolean isRecordClass(PsiClass psiClass) {
        if (psiClass == null) {
            return false;
        }

        return psiClass.getRecordComponents().length > 0;
    }
}
