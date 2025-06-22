package com.zhanglinwei.zTools.restful.component;

import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.IconLoader;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.zhanglinwei.zTools.restful.model.IRestful;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static com.zhanglinwei.zTools.common.constants.SpringPool.*;

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

        String fileLocation = className.concat(HASH).concat(psiMethod.getName());
        return LEFT_BRACKET + fileLocation + RIGHT_BRACKET;
    }

    @Override
    public @Nullable Icon getIcon(boolean b) {
        return IconLoader.getIcon(iRestful.iconPath(), IRestfulPresentation.class);
    }
}
