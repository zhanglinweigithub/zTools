package com.zhanglinwei.zTools.common.util;

import java.util.Collections;

/**
 * 按嵌套深度把值包进 List，用于集合/数组类型的示例 JSON。
 *
 * <pre>
 *   NestedUtils.wrapWithNesting("a", 0) → "a"
 *   NestedUtils.wrapWithNesting("a", 2) → [["a"]]
 *   NestedUtils.wrapWithNesting(null, 3) → null
 * </pre>
 */
public final class NestedUtils {

    /** 工具类，禁止实例化 */
    private NestedUtils(){}

    /**
     * 包装嵌套层级：每增加 1 层深度就再套一层单元素 List。
     *
     * <pre>
     *   wrapWithNesting("a", 0) → "a"
     *   wrapWithNesting("a", 1) → ["a"]
     *   wrapWithNesting("a", 2) → [["a"]]
     *   wrapWithNesting(null, 3) → null
     * </pre>
     *
     * @param source 最内层示例值
     * @param depth  集合/数组嵌套层数，{@code 0} 表示不包装
     * @return 包装后的对象；{@code source} 为 {@code null} 时原样返回
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
