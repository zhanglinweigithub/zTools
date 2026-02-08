package com.zhanglinwei.zTools.generatebuilder;

import com.intellij.codeInsight.generation.PsiFieldMember;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.psi.PsiClass;
import com.intellij.util.NotNullFunction;
import com.intellij.util.containers.ContainerUtil;

import java.util.Collection;
import java.util.List;


public class IGenerateAccessorProviderRegistrar {

    public static final ExtensionPointName<NotNullFunction<PsiClass, Collection<PsiFieldMember>>> EP_NAME = ExtensionPointName.create("com.intellij.generateAccessorProvider");
    static synchronized List<PsiFieldMember> getEncapsulatableClassMembers(PsiClass psiClass) {
        return ContainerUtil.concat(EP_NAME.getExtensionList(), (s) -> s.fun(psiClass));
    }

}
