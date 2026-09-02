package com.zhanglinwei.zTools.generatebuilder.psi;

import com.intellij.codeInsight.generation.OverrideImplementUtil;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;

/**
 * PSI 类定位与 Record 判断。
 * <p>
 * Record 走全部组件字段且不弹窗，普通类才弹出字段选择框，见 {@link com.zhanglinwei.zTools.generatebuilder.GenerateBuilderHandler}。
 */
public final class PsiClasses {

    /**
     * 工具类，禁止实例化。
     */
    private PsiClasses() {}

    /**
     * 是否为 Java Record（存在 record 组件）。
     *
     * @param psiClass 待判断的类，可为 {@code null}
     * @return 是 Record 则为 {@code true}
     */
    public static boolean isRecord(PsiClass psiClass) {
        return psiClass != null && psiClass.getRecordComponents().length > 0;
    }

    /**
     * 取光标所在的上下文类，供 Generate 菜单定位生成目标。
     *
     * @param editor 当前编辑器
     * @param file   当前文件
     * @return 光标所在类；解析不到则为 {@code null}
     */
    public static PsiClass contextClass(Editor editor, PsiFile file) {
        return OverrideImplementUtil.getContextClass(file.getProject(), editor, file, false);
    }
}
