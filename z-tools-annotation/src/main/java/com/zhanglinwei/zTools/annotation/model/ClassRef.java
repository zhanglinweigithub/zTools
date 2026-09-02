package com.zhanglinwei.zTools.annotation.model;

/**
 * 方法所在类的引用，不嵌套完整 {@link ClassDefinition}，避免循环。
 */
public final class ClassRef {

    /** 简单类名。 */
    private final String name;
    /** 全限定名。 */
    private final String qualifiedName;
    /** 所在包；缺省包为 {@code null}。 */
    private final String packageName;

    /**
     * @param name          简单类名
     * @param qualifiedName 全限定名
     * @param packageName   所在包
     */
    public ClassRef(String name, String qualifiedName, String packageName) {
        this.name = name;
        this.qualifiedName = qualifiedName;
        this.packageName = packageName;
    }

    /** 简单类名。 */
    public String name() {
        return name;
    }

    /** 全限定名。 */
    public String qualifiedName() {
        return qualifiedName;
    }

    /** 所在包；缺省包为 {@code null}。 */
    public String packageName() {
        return packageName;
    }
}
