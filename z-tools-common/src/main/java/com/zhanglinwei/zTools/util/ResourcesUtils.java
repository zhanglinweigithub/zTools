package com.zhanglinwei.zTools.util;

import java.io.InputStream;

public final class ResourcesUtils {

    private ResourcesUtils(){}

    public static InputStream readFile(String fileName) {
        return ResourcesUtils.class.getClassLoader().getResourceAsStream(fileName);
    }


}
