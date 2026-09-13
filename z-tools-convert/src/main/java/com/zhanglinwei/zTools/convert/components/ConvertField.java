package com.zhanglinwei.zTools.convert.components;

/**
 * 一对同名属性：源 getter → 目标 setter。
 */
public final class ConvertField {

    public enum Kind {
        /** 源有对应 getter，直接赋值 */
        MATCHED,
        /** 目标有 setter、源没有对应属性，生成无参 setter 占位 */
        UNMATCHED
    }

    private final String getterName;
    private final String setterName;
    private final Kind kind;

    /**
     * @param getterName 源方法名，如 {@code getName} / {@code isEnabled}；未匹配时为 {@code null}
     * @param setterName 目标方法名，如 {@code setName}
     * @param kind       是否匹配到源属性
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
