package com.zhanglinwei.zTools.util;

import java.util.Collections;

public final class NestedUtils {

    private NestedUtils(){}

    /**
     * 包装嵌套层级
     */
    public static Object wrapWithNesting(Object source, int depth) {
        if (source == null) {
            return source;
        }

        Object target = source;
        for (int i = 0; i < depth; i++) {
            target = Collections.singletonList(target);
        }
        return target;
    }
}
