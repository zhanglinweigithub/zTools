package com.zhanglinwei.zTools.generatebuilder;

import com.intellij.codeInsight.generation.OverrideImplementUtil;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;

public final class PsiClasses {

    private PsiClasses() {}

    public static boolean isRecord(PsiClass psiClass) {
        return psiClass != null && psiClass.getRecordComponents().length > 0;
    }

    public static PsiClass contextClass(Editor editor, PsiFile file) {
        return OverrideImplementUtil.getContextClass(file.getProject(), editor, file, false);
    }
}
