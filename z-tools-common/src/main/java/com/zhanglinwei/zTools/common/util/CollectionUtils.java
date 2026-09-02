package com.zhanglinwei.zTools.common.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 集合判空与防御性拷贝。{@code null} 与空集合一律视为 empty。
 *
 * <pre>
 *   CollectionUtils.isEmpty(null) → true
 *   CollectionUtils.isEmpty(Collections.emptyList()) → true
 *   CollectionUtils.isNotEmpty(Collections.singletonList(1)) → true
 * </pre>
 */
public final class CollectionUtils {

    /** 工具类，禁止实例化 */
    private CollectionUtils() {}

    /**
     * {@code null} 或空集合为 {@code true}。
     *
     * <pre>
     *   isEmpty(null) → true
     *   isEmpty(emptyList()) → true
     *   isEmpty(singletonList(1)) → false
     * </pre>
     *
     * @param collection 任意集合，可为 {@code null}
     * @return 空则为 {@code true}
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * {@link #isEmpty(Collection)} 的否定。
     *
     * @param collection 任意集合，可为 {@code null}
     * @return 非空则为 {@code true}
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    /**
     * 拷贝后包装为不可变列表；{@code null}/空则返回 {@link Collections#emptyList()}。
     *
     * <pre>
     *   unmodifiableList(null) → []
     *   unmodifiableList(Arrays.asList("a")) → 不可变 ["a"]
     * </pre>
     *
     * @param items 源列表，可为 {@code null}
     * @param <T>   元素类型
     * @return 不可变副本或空列表
     */
    public static <T> List<T> unmodifiableList(List<T> items) {
        return isEmpty(items) ? Collections.emptyList() : Collections.unmodifiableList(new ArrayList<T>(items));
    }
}
