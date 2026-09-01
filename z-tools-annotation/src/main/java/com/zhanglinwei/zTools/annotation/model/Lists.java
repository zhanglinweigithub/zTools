package com.zhanglinwei.zTools.annotation.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Lists {

    private Lists() {}

    public static <T> List<T> copy(List<T> items) {
        return items == null || items.isEmpty()
                ? Collections.emptyList()
                : Collections.unmodifiableList(new ArrayList<T>(items));
    }
}
