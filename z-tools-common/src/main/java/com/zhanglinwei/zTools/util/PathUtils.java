package com.zhanglinwei.zTools.util;

import static com.zhanglinwei.zTools.constant.StringPool.EMPTY;
import static com.zhanglinwei.zTools.constant.StringPool.SLASH;

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
