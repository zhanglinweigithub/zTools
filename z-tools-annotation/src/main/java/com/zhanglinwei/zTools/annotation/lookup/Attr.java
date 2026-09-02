package com.zhanglinwei.zTools.annotation.lookup;

/**
 * 注解属性名常量。匹配源码写出的属性名，不对应注解 default。
 */
public final class Attr {

    /** {@code value}，含简写 {@code @GetMapping("/x")}。 */
    public static final String VALUE = "value";
    /** {@code name}。 */
    public static final String NAME = "name";
    /** {@code path}。 */
    public static final String PATH = "path";
    /** {@code required}。 */
    public static final String REQUIRED = "required";
    /** {@code hidden}。 */
    public static final String HIDDEN = "hidden";
    /** {@code description}。 */
    public static final String DESCRIPTION = "description";
    /** {@code example}。 */
    public static final String EXAMPLE = "example";

    /** 常量类，禁止实例化。 */
    private Attr() {}
}
