package com.zhanglinwei.zTools.restful.component;

import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.ui.IconManager;
import com.zhanglinwei.zTools.restful.model.IRestful;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class IRestfulPresentation implements ItemPresentation {

    private final IRestful iRestful;

    public IRestfulPresentation(IRestful iRestful) {
        this.iRestful = iRestful;
    }


    @Override
    public @Nullable String getPresentableText() {
        return iRestful.getRequestPath();
    }

    @Override
    public @Nullable String getLocationString() {
        PsiMethod psiMethod = iRestful.getPsiMethod();

        PsiClass containingClass = psiMethod.getContainingClass();
        if (containingClass == null) {
            return null;
        }

        String className = containingClass.getName();
        if (className == null) {
            return null;
        }

        String fileLocation = className.concat("#").concat(psiMethod.getName());
        return "(" + fileLocation + ")";
    }

    @Override
    public @Nullable Icon getIcon(boolean b) {
        return IconManager.getInstance().getIcon(iRestful.iconPath(), IRestfulPresentation.class);
    }
}
