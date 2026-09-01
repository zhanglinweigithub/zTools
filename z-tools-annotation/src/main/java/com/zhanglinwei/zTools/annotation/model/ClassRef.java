package com.zhanglinwei.zTools.annotation.model;

/**
 * 方法所在类的引用，不嵌套完整 {@link ClassDefinition}，避免循环。
 */
public final class ClassRef {

    private final String name;
    private final String qualifiedName;
    private final String packageName;

    public ClassRef(String name, String qualifiedName, String packageName) {
        this.name = name;
        this.qualifiedName = qualifiedName;
        this.packageName = packageName;
    }

    public String name() {
        return name;
    }

    public String qualifiedName() {
        return qualifiedName;
    }

    public String packageName() {
        return packageName;
    }
}
