package com.zhanglinwei.zTools.restful.component;

import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.IconLoader;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.zhanglinwei.zTools.restful.model.IRestful;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static com.zhanglinwei.zTools.common.constant.StringPool.*;

/**
 * GoTo 列表中单条接口的展示信息。
 * <p>
 * 主文本为请求路径，位置为 {@code [ClassName#methodName]}，图标按 HTTP 方法区分。
 */
public class IRestfulPresentation implements ItemPresentation {

    /** 对应的接口项 */
    private final IRestful iRestful;

    /**
     * 绑定接口项。
     *
     * @param iRestful 接口导航项
     */
    public IRestfulPresentation(IRestful iRestful) {
        this.iRestful = iRestful;
    }


    /**
     * 列表主文本：请求路径。
     *
     * @return 请求路径
     */
    @Override
    public @Nullable String getPresentableText() {
        return iRestful.getRequestPath();
    }

    /**
     * 列表副文本：所在类与方法。
     *
     * @return 形如 {@code [UserController#getById]}；无法解析时为 {@code null}
     */
    @Override
    public @Nullable String getLocationString() {
        @NotNull Computable<String> locationString = () -> {
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
        };

        return ApplicationManager.getApplication().runReadAction(locationString);

    }

    /**
     * 按 HTTP 方法加载图标。
     *
     * @param b 未使用
     * @return 方法对应图标
     */
    @Override
    public @Nullable Icon getIcon(boolean b) {
        return IconLoader.getIcon(iRestful.iconPath(), IRestfulPresentation.class);
    }
}
