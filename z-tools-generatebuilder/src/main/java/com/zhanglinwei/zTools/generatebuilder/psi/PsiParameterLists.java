package com.zhanglinwei.zTools.generatebuilder.psi;

import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiParameterList;
import com.intellij.psi.PsiType;

/**
 * 判断两个方法/构造器是否已生成过：只比参数个数和展示类型，不比参数名。
 * {@code java.lang.String} 与 {@code String} 视为相同。
 */
public final class PsiParameterLists {

    private static final String JAVA_LANG_PREFIX = "java.lang.";

    private PsiParameterLists() {}

    /** 参数个数或某一位类型对不上即视为不同方法。 */
    public static boolean equal(PsiParameterList left, PsiParameterList right) {
        if (left.getParametersCount() != right.getParametersCount()) {
            return false;
        }
        PsiParameter[] leftParams = left.getParameters();
        PsiParameter[] rightParams = right.getParameters();
        for (int i = 0; i < leftParams.length; i++) {
            if (!presentableEqual(leftParams[i].getType(), rightParams[i].getType())) {
                return false;
            }
        }
        return true;
    }

    /** 用 presentableText 比，避免 FQCN 和短名被当成两种类型。 */
    private static boolean presentableEqual(PsiType left, PsiType right) {
        if (left == null || right == null) {
            return false;
        }
        return stripJavaLang(left.getPresentableText()).equals(stripJavaLang(right.getPresentableText()));
    }

    /** {@code java.lang.String} → {@code String}，其它包名保持原样。 */
    private static String stripJavaLang(String type) {
        return type.startsWith(JAVA_LANG_PREFIX) ? type.substring(JAVA_LANG_PREFIX.length()) : type;
    }
}
