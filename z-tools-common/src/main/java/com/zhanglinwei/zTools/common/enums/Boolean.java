package com.zhanglinwei.zTools.common.enums;

/**
 * 三形态布尔值：布尔、数字（1/0）、字符串（Y/N）。
 * <p>
 * 用于配置项、文档表格等需要在多种写法间对照的场景，与 {@code java.lang.Boolean} 无关。
 */
public enum Boolean {

    /** 真：{@code true} / {@code 1} / {@code Y} */
    TRUE(true, 1, "Y"),
    /** 假：{@code false} / {@code 0} / {@code N} */
    FALSE(false, 0, "N");

    /** 布尔值 */
    private final boolean booleanValue;
    /** 数字表示，1 或 0 */
    private final int numberValue;
    /** 字符串表示，Y 或 N */
    private final String stringValue;

    /**
     * @param booleanValue 布尔值
     * @param numberValue  数字表示
     * @param stringValue  字符串表示
     */
    Boolean(boolean booleanValue, int numberValue, String stringValue) {
        this.booleanValue = booleanValue;
        this.numberValue = numberValue;
        this.stringValue = stringValue;
    }

    /** 布尔值。 */
    public boolean isBooleanValue() {
        return booleanValue;
    }

    /** 数字表示（1/0）。 */
    public int getNumberValue() {
        return numberValue;
    }

    /** 字符串表示（Y/N）。 */
    public String getStringValue() {
        return stringValue;
    }

}
