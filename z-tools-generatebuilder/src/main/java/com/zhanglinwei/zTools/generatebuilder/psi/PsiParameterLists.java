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

    /**
     * 工具类，禁止实例化。
     */
    private PsiParameterLists() {}

    /**
     * 参数个数或某一位类型对不上即视为不同方法。
     *
     * @param left  已有方法的参数列表
     * @param right 待插入方法的参数列表
     * @return 视为同一签名则为 {@code true}
     */
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

    /**
     * 用 presentableText 比，避免 FQCN 和短名被当成两种类型。
     *
     * @param left  左侧类型
     * @param right 右侧类型
     * @return 展示文本相同则为 {@code true}
     */
    private static boolean presentableEqual(PsiType left, PsiType right) {
        if (left == null || right == null) {
            return false;
        }
        return stripJavaLang(left.getPresentableText()).equals(stripJavaLang(right.getPresentableText()));
    }

    /**
     * {@code java.lang.String} → {@code String}，其它包名保持原样。
     *
     * @param type 类型展示文本
     * @return 去掉 {@code java.lang.} 前缀后的文本
     */
    private static String stripJavaLang(String type) {
        return type.startsWith(JAVA_LANG_PREFIX) ? type.substring(JAVA_LANG_PREFIX.length()) : type;
    }
}
