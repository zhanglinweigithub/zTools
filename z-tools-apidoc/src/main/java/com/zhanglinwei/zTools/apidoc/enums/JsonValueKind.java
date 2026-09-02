package com.zhanglinwei.zTools.apidoc.enums;

import static com.zhanglinwei.zTools.common.constant.StringPool.FALSE;
import static com.zhanglinwei.zTools.common.constant.StringPool.ONE;
import static com.zhanglinwei.zTools.common.constant.StringPool.QUOTE;
import static com.zhanglinwei.zTools.common.constant.StringPool.SINGLE_QUOTE;
import static com.zhanglinwei.zTools.common.constant.StringPool.TRUE;
import static com.zhanglinwei.zTools.common.constant.StringPool.ZERO;

/**
 * 示例 JSON「值」这一段该用哪种颜色。
 * <p>
 * HTML / Word 格式化器在着色时调用：只看 trim 后的前缀，不做完整 JSON 解析。
 * 以引号或 true/false 开头视为字符串/布尔，以 0 或 1 开头视为数字（覆盖常见示例值），
 * 其余交给调用方的默认色。
 * <p>
 * 类型到示例值（及本枚举分类）的对应关系：
 * <pre>
 * String / 日期等     → "stringValue" / "yyyy-MM-dd ..."  → STRING_OR_BOOLEAN
 * boolean / Boolean   → true / false                       → STRING_OR_BOOLEAN
 * int / Integer 等    → 1 / 0                              → NUMBER
 * 对象 / 数组 / null  → {  / [  / null                     → OTHER
 * </pre>
 */
public enum JsonValueKind {
    /** 字符串、true/false，一般用蓝色。 */
    STRING_OR_BOOLEAN,
    /** 数字，一般用绿色。 */
    NUMBER,
    /** 对象/数组括号、null 等，用调用方传入的默认色。 */
    OTHER
    ;

    /**
     * 根据值的字面前缀分类。{@code value} 可能仍带缩进，内部会 trim。
     *
     * @param value JSON 行里冒号右侧的文本，例如 {@code "stringValue"}、{@code  1}、{@code true}
     * @return {@link #STRING_OR_BOOLEAN}、{@link #NUMBER} 或 {@link #OTHER}
     */
    public static JsonValueKind of(String value) {
        String trimmed = value.trim();
        if (trimmed.startsWith(QUOTE) || trimmed.startsWith(SINGLE_QUOTE)
                || trimmed.startsWith(TRUE) || trimmed.startsWith(FALSE)) {
            return STRING_OR_BOOLEAN;
        }
        if (trimmed.startsWith(ZERO) || trimmed.startsWith(ONE)) {
            return NUMBER;
        }
        return OTHER;
    }

    /**
     * 按当前种类挑颜色字符串。HTML 传入带 {@code #} 的色值，Word 传入不带 {@code #} 的 hex。
     *
     * @param stringOrBoolean 字符串 / 布尔使用的颜色
     * @param number          数字使用的颜色
     * @param other           其余值使用的颜色
     * @return 与当前种类对应的那一个颜色参数
     */
    public String color(String stringOrBoolean, String number, String other) {
        if (this == STRING_OR_BOOLEAN) {
            return stringOrBoolean;
        }
        if (this == NUMBER) {
            return number;
        }
        return other;
    }
}
