package com.zhanglinwei.zTools.common.util;

import static com.zhanglinwei.zTools.common.constant.StringPool.EMPTY;
import static com.zhanglinwei.zTools.common.constant.StringPool.SLASH;

/**
 * URL / 文件 path：补前导 {@code /}、去掉末尾多余 {@code /}。
 * 仅由斜杠组成的路径视为空。
 *
 * <pre>
 *   PathUtils.leadingSlash("api/v1") → "/api/v1"
 *   PathUtils.leadingSlash("/api/v1/") → "/api/v1"
 *   PathUtils.leadingSlash("/") → ""
 * </pre>
 */
public final class PathUtils {

    /** 工具类，禁止实例化 */
    private PathUtils() {}

    /**
     * 规范化 path：补前导 {@code /}，去掉末尾多余 {@code /}。
     * 空白或全是斜杠时返回空串（不是 {@code "/"}）。
     *
     * <pre>
     *   leadingSlash("api/v1") → "/api/v1"
     *   leadingSlash("/api/v1/") → "/api/v1"
     *   leadingSlash("/") → ""
     *   leadingSlash(null) → ""
     * </pre>
     *
     * @param path 原始路径，可为 {@code null}
     * @return 规范化后的路径，或空串
     */
    public static String leadingSlash(String path) {
        if (StringUtils.isBlank(path) || StringUtils.isBlank(path.replaceAll(SLASH, EMPTY))) {
            // 空白或仅由斜杠组成的路径视为空，避免产出无意义的 "/"
            return EMPTY;
        }
        path = path.replaceAll("/+$", EMPTY);
        return path.startsWith(SLASH) ? path : SLASH + path;
    }
}
