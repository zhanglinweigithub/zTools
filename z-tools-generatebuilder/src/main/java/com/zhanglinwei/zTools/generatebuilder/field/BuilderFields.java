package com.zhanglinwei.zTools.generatebuilder.field;

import com.intellij.codeInsight.generation.PsiFieldMember;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiModifier;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.NotNullFunction;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.java.generate.exception.GenerateCodeException;

import java.util.Collection;
import java.util.List;

/**
 * 收集能放进 Builder 的实例字段。
 * <p>
 * 不自己扫 {@code PsiField}，而是走 IDEA 扩展点 {@code generateAccessorProvider}
 *（和 Generate Getter/Setter 同源），Lombok 等插件贡献的字段也能选到。
 * static 字段丢掉；{@link GenerateCodeException} 时仍保留该成员，与 IDEA 原逻辑一致。
 */
public final class BuilderFields {

    /** IDEA 内置：给「可封装字段」提供 PsiFieldMember 列表。 */
    private static final ExtensionPointName<NotNullFunction<PsiClass, Collection<PsiFieldMember>>> ACCESSOR_PROVIDERS =
            ExtensionPointName.create("com.intellij.generateAccessorProvider");

    /**
     * 工具类，禁止实例化。
     */
    private BuilderFields() {}

    /**
     * 菜单是否可用：至少有一个实例字段。
     *
     * @param psiClass 目标类
     * @return 存在可放入 Builder 的字段则为 {@code true}
     */
    public static boolean hasInstanceFields(PsiClass psiClass) {
        return instanceFields(psiClass).length > 0;
    }

    /**
     * 合并所有 Accessor Provider 的结果，再去掉 static。
     * synchronized 沿用原先 Registrar 的写法，避免扩展点列表在迭代时被改。
     *
     * @param psiClass 目标类
     * @return 实例字段数组，可能为空数组
     */
    public static synchronized PsiFieldMember[] instanceFields(PsiClass psiClass) {
        List<PsiFieldMember> provided = ContainerUtil.concat(ACCESSOR_PROVIDERS.getExtensionList(), provider -> provider.fun(psiClass));
        if (provided.isEmpty()) {
            return new PsiFieldMember[0];
        }

        List<PsiFieldMember> eligible = ContainerUtil.findAll(provided, member -> {
            try {
                return isInstanceField(member);
            } catch (GenerateCodeException ignored) {
                return true;
            } catch (IncorrectOperationException ignored) {
                return false;
            }
        });
        return eligible.toArray(new PsiFieldMember[0]);
    }

    /**
     * Builder 只收实例字段，static 常量等排除。
     *
     * @param member 候选字段
     * @return 非 static 则为 {@code true}
     */
    private static boolean isInstanceField(PsiFieldMember member) {
        return !member.getElement().hasModifierProperty(PsiModifier.STATIC);
    }
}
