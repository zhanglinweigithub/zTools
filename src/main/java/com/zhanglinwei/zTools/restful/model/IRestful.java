package com.zhanglinwei.zTools.restful.model;

import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiMethod;
import com.zhanglinwei.zTools.common.enums.HttpMethod;
import com.zhanglinwei.zTools.restful.component.IRestfulPresentation;
import org.jetbrains.annotations.Nullable;

public class IRestful implements NavigationItem {

    private String name;
    private String requestPath;
    private HttpMethod requestType;

    private PsiMethod psiMethod;

    public IRestful() {
    }

    @Override
    public @Nullable ItemPresentation getPresentation() {
        return new IRestfulPresentation(this);
    }

    @Override
    public void navigate(boolean b) {
        psiMethod.navigate(b);
    }

    @Override
    public boolean canNavigate() {
        return psiMethod.canNavigate();
    }

    @Override
    public boolean canNavigateToSource() {
        return true;
    }

    public String iconPath() {
        switch (requestType) {
            case GET: return "/icons/http-method/get.svg";
            case POST: return "/icons/http-method/pos.svg";
            case PUT: return "/icons/http-method/put.svg";
            case DELETE: return "/icons/http-method/del.svg";
            case PATCH: return "/icons/http-method/pat.svg";

            default: return "/icons/http-method/unknow.svg";
        }
    }



    public String getRequestPath() {
        return requestPath;
    }

    public void setRequestPath(String requestPath) {
        this.requestPath = requestPath;
    }

    public HttpMethod getRequestType() {
        return requestType;
    }

    public void setRequestType(HttpMethod requestType) {
        this.requestType = requestType;
    }

    public PsiMethod getPsiMethod() {
        return psiMethod;
    }

    public void setPsiMethod(PsiMethod psiMethod) {
        this.psiMethod = psiMethod;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
