package com.zhanglinwei.zTools.apidoc.enums;

import static com.zhanglinwei.zTools.common.constant.StringPool.FALSE;
import static com.zhanglinwei.zTools.common.constant.StringPool.ONE;
import static com.zhanglinwei.zTools.common.constant.StringPool.SINGLE_QUOTE;
import static com.zhanglinwei.zTools.common.constant.StringPool.TRUE;
import static com.zhanglinwei.zTools.common.constant.StringPool.ZERO;

/**
 * 示例 JSON「值」这一段该用哪种颜色。
 * <p>
 * 只看 trim 后的前缀，不做完整 JSON 解析：以引号或 true/false 开头视为字符串/布尔，
 * 以 0 或 1 开头视为数字（覆盖常见示例值），其余交给调用方的默认色。
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
     */
    public static JsonValueKind of(String value) {
        String trimmed = value.trim();
        if (trimmed.startsWith("\"") || trimmed.startsWith(SINGLE_QUOTE)
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
