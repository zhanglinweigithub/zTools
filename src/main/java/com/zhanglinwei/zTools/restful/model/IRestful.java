package com.zhanglinwei.zTools.restful.model;

import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.psi.PsiMethod;
import com.zhanglinwei.zTools.common.enums.HttpMethod;
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
        return null;
    }

    @Override
    public void navigate(boolean b) {

    }

    @Override
    public boolean canNavigate() {
        return false;
    }

    @Override
    public boolean canNavigateToSource() {
        return false;
    }

    public String iconPath() {
        switch (requestType) {
            case GET: return "/icons/http-method/get.svg";
            case POST: return "/icons/http-method/pos.svg";
            case PUT: return "/icons/http-method/put.svg";
            case DELETE: return "/icons/http-method/del.svg";
            case PATCH: return "/icons/http-method/pat.svg";

            default: return null;
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
