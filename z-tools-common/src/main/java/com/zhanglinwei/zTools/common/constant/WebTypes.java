package com.zhanglinwei.zTools.common.constant;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Web 请求头名、以及文档 / curl 生成时需要跳过的框架参数。
 */
public final class WebTypes {

    public static final String CONTENT_TYPE = "Content-Type";
    public static final String ACCEPT = "Accept";

    private static final String[] SKIP_PACKAGES = {
            "javax.servlet",
            "jakarta.servlet",
            "org.springframework.ui",
            "org.springframework.validation"
    };

    private static final Set<String> SKIP_SIMPLE_NAMES = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
            "BindingResult", "Model", "ModelMap", "Principal", "HttpSession"
    )));

    private WebTypes() {}

    public static boolean skipParameter(String packageName, String type) {
        if (packageName != null) {
            for (String skipPackage : SKIP_PACKAGES) {
                if (packageName.startsWith(skipPackage)) {
                    return true;
                }
            }
        }
        return type != null && SKIP_SIMPLE_NAMES.contains(type);
    }
}
