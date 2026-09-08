package com.zhanglinwei.zTools.restful.model;

import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.psi.PsiMethod;
import com.zhanglinwei.zTools.common.enums.HttpMethod;
import com.zhanglinwei.zTools.common.util.RequestPathUtils;
import com.zhanglinwei.zTools.restful.component.IRestfulPresentation;
import org.jetbrains.annotations.Nullable;

/**
 * 一条可跳转的 RESTful 接口。
 * <p>
 * 绑定请求路径、HTTP 方法与对应 {@link PsiMethod}，作为 GoTo 窗口中的导航项。
 */
public class IRestful implements NavigationItem {

    /** 展示名称，通常等于完整请求路径 */
    private String name;
    /** 请求路径，如 {@code /api/user/{id}}，切换全局前缀后会更新 */
    private String requestPath;
    /** 方法 Mapping 原始路径，不含全局前缀 */
    private final String mappingPath;
    /** HTTP 方法 */
    private HttpMethod requestType;

    /** 对应的方法 PSI，用于跳转源码 */
    private PsiMethod psiMethod;

    /**
     * 用方法、路径和 HTTP 方法构造接口项。
     *
     * @param psiMethod   接口方法
     * @param requestPath 请求路径
     * @param requestType HTTP 方法
     */
    public IRestful(PsiMethod psiMethod, String requestPath, HttpMethod requestType) {
        this.psiMethod = psiMethod;
        this.mappingPath = requestPath;
        this.requestPath = requestPath;
        this.requestType = requestType;
        this.name = requestPath;
    }

    /**
     * 列表中的展示信息（路径、位置、图标）。
     *
     * @return 展示对象
     */
    @Override
    public @Nullable ItemPresentation getPresentation() {
        return new IRestfulPresentation(this);
    }

    /**
     * 跳转到方法源码。
     *
     * @param b 是否请求聚焦编辑器
     */
    @Override
    public void navigate(boolean b) {
        psiMethod.navigate(b);
    }

    /**
     * 是否可以跳转。
     *
     * @return 方法 PSI 可导航时为 {@code true}
     */
    @Override
    public boolean canNavigate() {
        return psiMethod.canNavigate();
    }

    /**
     * 是否可以跳转到源码。
     *
     * @return 始终为 {@code true}
     */
    @Override
    public boolean canNavigateToSource() {
        return true;
    }

    /**
     * 按 HTTP 方法返回图标路径。
     *
     * @return 图标资源路径
     */
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



    /**
     * 把全局前缀拼到原始 Mapping 上，更新展示名称与请求路径。
     *
     * @param prefix 全局请求前缀，空白则只保留 Mapping
     */
    public void applyPrefix(String prefix) {
        String fullPath = RequestPathUtils.join(prefix, mappingPath);
        this.name = fullPath;
        this.requestPath = fullPath;
    }

    /**
     * 方法 Mapping 原始路径（不含全局前缀）。
     *
     * @return 原始路径
     */
    public String getMappingPath() {
        return mappingPath;
    }

    /** 请求路径。 */
    public String getRequestPath() {
        return requestPath;
    }

    /** 设置请求路径。 */
    public void setRequestPath(String requestPath) {
        this.requestPath = requestPath;
    }

    /** HTTP 方法。 */
    public HttpMethod getRequestType() {
        return requestType;
    }

    /** 设置 HTTP 方法。 */
    public void setRequestType(HttpMethod requestType) {
        this.requestType = requestType;
    }

    /** 对应的方法 PSI。 */
    public PsiMethod getPsiMethod() {
        return psiMethod;
    }

    /** 设置方法 PSI。 */
    public void setPsiMethod(PsiMethod psiMethod) {
        this.psiMethod = psiMethod;
    }

    /** 展示名称。 */
    public String getName() {
        return name;
    }

    /** 设置展示名称。 */
    public void setName(String name) {
        this.name = name;
    }

}
