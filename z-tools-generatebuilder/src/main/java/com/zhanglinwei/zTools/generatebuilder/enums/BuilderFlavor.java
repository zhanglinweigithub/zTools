package com.zhanglinwei.zTools.generatebuilder.enums;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiModifier;
import com.zhanglinwei.zTools.generatebuilder.psi.PsiClasses;

import java.util.Set;

/**
 * Builder 的三种形态，由目标是否 Record、以及是否勾选 lite 决定。
 * <ul>
 *   <li>RECORD：全部组件字段，字段无显式修饰符，API 与 lite 相同（无 C/B 泛型）</li>
 *   <li>LITE：用户勾选的字段，private，无泛型，{@code build()} 返回外层类型</li>
 *   <li>INHERITABLE：protected 字段，{@code Builder<C extends Outer, B extends Outer.Builder<C, B>>}，便于子类扩展</li>
 * </ul>
 */
public enum BuilderFlavor {

    /** Record：用全部组件字段，字段不加修饰符，API 与轻量版相同。 */
    RECORD,
    /** 轻量：用户勾选的字段，private，无泛型。 */
    LITE,
    /** 可继承：protected 字段，{@code Builder<C extends Outer, B extends Outer.Builder<C, B>>}。 */
    INHERITABLE,
    ;

    /** Record 优先于 lite 勾选：Record 不能按字段弹窗，始终走 RECORD。 */
    public static BuilderFlavor of(PsiClass targetClass, Set<BuilderOption> options) {
        if (PsiClasses.isRecord(targetClass)) {
            return RECORD;
        }
        if (options.contains(BuilderOption.USE_LITE_BUILDER)) {
            return LITE;
        }
        return INHERITABLE;
    }

    /** true 时忽略对话框勾选，用 {@code getAllFields()}。 */
    public boolean usesAllClassFields() {
        return this == RECORD;
    }

    /** 仅可继承形态需要类型参数 C、B。 */
    public boolean usesGenerics() {
        return this == INHERITABLE;
    }

    /**
     * Record 保持默认可见性（与历史输出一致，不是 private）；
     * lite 为 private；可继承为 protected，方便子类 Builder 访问。
     */
    public String fieldModifier() {
        if (this == RECORD) {
            return null;
        }
        return this == LITE ? PsiModifier.PRIVATE : PsiModifier.PROTECTED;
    }
}
