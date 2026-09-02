package com.zhanglinwei.zTools.common.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class CollectionUtils {

    private CollectionUtils() {}

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    public static <T> List<T> unmodifiableList(List<T> items) {
        return isEmpty(items) ? Collections.emptyList() : Collections.unmodifiableList(new ArrayList<T>(items));
    }
}
