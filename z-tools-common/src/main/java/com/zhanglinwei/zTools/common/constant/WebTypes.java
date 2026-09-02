package com.zhanglinwei.zTools.common.constant;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Web 请求头名、以及文档 / curl 生成时需要跳过的框架参数。
 * <p>
 * Servlet、Spring MVC 的 {@code Model}/{@code BindingResult} 等不是业务入参，导出接口文档时应忽略。
 */
public final class WebTypes {

    /** HTTP 请求头：Content-Type */
    public static final String CONTENT_TYPE = "Content-Type";
    /** HTTP 请求头：Accept */
    public static final String ACCEPT = "Accept";

    /** 这些包下的类型一律视为框架参数，不写入文档 */
    private static final String[] SKIP_PACKAGES = {
            "javax.servlet",
            "jakarta.servlet",
            "org.springframework.ui",
            "org.springframework.validation",
            "org.springframework.web.context.request"
    };

    /** 仅凭简单类名即可判定为框架参数的类型 */
    private static final Set<String> SKIP_SIMPLE_NAMES = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
            "BindingResult", "Model", "ModelMap", "Principal", "HttpSession",
            "HttpServletRequest", "HttpServletResponse", "ServletRequest", "ServletResponse",
            "ServletContext", "WebRequest", "NativeWebRequest", "ServletWebRequest"
    )));

    /** 工具类，禁止实例化 */
    private WebTypes() {}

    /**
     * 是否应在接口文档中跳过该参数。
     * <p>
     * 包名命中框架包前缀，或简单类名在跳过名单中，均视为框架参数。
     *
     * <pre>
     *   skipParameter("javax.servlet.http", "HttpServletRequest") → true
     *   skipParameter(null, "BindingResult") → true
     *   skipParameter("com.example", "User") → false
     * </pre>
     *
     * @param packageName 参数类型所在包名，可为 {@code null}
     * @param type        参数类型名（简单名或带包名均可），可为 {@code null}
     * @return 应跳过则为 {@code true}
     */
    public static boolean skipParameter(String packageName, String type) {
        if (packageName != null) {
            for (String skipPackage : SKIP_PACKAGES) {
                if (packageName.startsWith(skipPackage)) {
                    return true;
                }
            }
        }
        return type != null && SKIP_SIMPLE_NAMES.contains(simpleName(type));
    }

    /**
     * 从类型文本取出简单类名：去掉泛型、数组后缀和包前缀。
     * 不依赖 {@code TypeUtils}，避免 constant 包反向依赖 util。
     *
     * @param type 类型文本
     * @return 简单类名
     */
    private static String simpleName(String type) {
        String simple = type.trim();
        int generic = simple.indexOf('<');
        if (generic > 0) {
            simple = simple.substring(0, generic);
        }
        if (simple.endsWith("[]")) {
            simple = simple.substring(0, simple.length() - 2);
        }
        int dot = simple.lastIndexOf('.');
        return dot >= 0 ? simple.substring(dot + 1) : simple;
    }
}
