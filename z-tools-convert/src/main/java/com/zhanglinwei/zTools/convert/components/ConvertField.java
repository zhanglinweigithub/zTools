package com.zhanglinwei.zTools.convert.components;

/**
 * 一对同名属性：源 getter → 目标 setter，以及生成时的判空方式。
 */
public final class ConvertField {

    /**
     * 源属性类型决定如何判空。
     */
    public enum Kind {
        /** 基本类型，直接赋值 */
        PRIMITIVE,
        /** 数组：非 null 且 length &gt; 0 */
        ARRAY,
        /** Collection / Map：非 null 且非 empty */
        COLLECTION,
        /** 其它引用类型：非 null */
        OBJECT,
        /** 目标有 setter、源没有对应属性，生成无参 setter 占位 */
        UNMATCHED
    }

    private final String getterName;
    private final String setterName;
    private final Kind kind;

    /**
     * @param getterName 源方法名，如 {@code getName} / {@code isEnabled}；未匹配时为 {@code null}
     * @param setterName 目标方法名，如 {@code setName}
     * @param kind       判空策略
     */
    public ConvertField(String getterName, String setterName, Kind kind) {
        this.getterName = getterName;
        this.setterName = setterName;
        this.kind = kind;
    }

    public String getGetterName() {
        return getterName;
    }

    public String getSetterName() {
        return setterName;
    }

    public Kind getKind() {
        return kind;
    }
}
