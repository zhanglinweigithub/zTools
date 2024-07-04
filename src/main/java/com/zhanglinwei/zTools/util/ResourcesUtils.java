package com.zhanglinwei.zTools.util;

import java.io.InputStream;

public class ResourcesUtils {

    private ResourcesUtils(){}

    public static InputStream readFile(String fileName) {
        return ResourcesUtils.class.getClassLoader().getResourceAsStream(fileName);
    }


}
