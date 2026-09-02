package com.zhanglinwei.zTools.common.util;

import static com.zhanglinwei.zTools.common.constant.StringPool.EMPTY;
import static com.zhanglinwei.zTools.common.constant.StringPool.SLASH;

/**
 * URL / 文件 path：补前导 {@code /}、去掉末尾多余 {@code /}。
 */
public final class PathUtils {

    private PathUtils() {}

    public static String leadingSlash(String path) {
        if (StringUtils.isBlank(path) || StringUtils.isBlank(path.replaceAll(SLASH, EMPTY))) {
            return EMPTY;
        }
        path = path.replaceAll("/+$", EMPTY);
        return path.startsWith(SLASH) ? path : SLASH + path;
    }
}
