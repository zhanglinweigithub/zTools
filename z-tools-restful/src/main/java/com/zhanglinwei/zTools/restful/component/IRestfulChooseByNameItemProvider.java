package com.zhanglinwei.zTools.restful.component;

import com.intellij.ide.util.gotoByName.DefaultChooseByNameItemProvider;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

public class IRestfulChooseByNameItemProvider extends DefaultChooseByNameItemProvider {

    public IRestfulChooseByNameItemProvider(@Nullable PsiElement context) {
        super(context);
    }
}
