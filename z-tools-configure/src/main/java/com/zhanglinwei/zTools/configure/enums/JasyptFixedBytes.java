package com.zhanglinwei.zTools.configure.enums;

import java.nio.charset.StandardCharsets;

/**
 * 将设置页里的固定 IV / Salt 文本转成字节。
 * <p>
 * 去掉空白后若是偶数长度十六进制（可带 {@code 0x} 前缀），按 hex 解码；否则按 UTF-8。
 */
public final class JasyptFixedBytes {

    /** 工具类，禁止实例化 */
    private JasyptFixedBytes() {
    }

    /**
     * 解析固定字节值。
     *
     * @param value 用户输入，可为 {@code null}
     * @return 对应字节；空白输入返回空数组
     */
    public static byte[] parse(String value) {
        if (value == null || value.isEmpty()) {
            return new byte[0];
        }
        String compact = stripSpaces(value);
        if (compact.length() >= 2 && (compact.startsWith("0x") || compact.startsWith("0X"))) {
            compact = compact.substring(2);
        }
        if (isEvenHex(compact)) {
            return hexToBytes(compact);
        }
        return value.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * 去掉所有空白字符。
     *
     * @param value 原文
     * @return 无空白的字符串
     */
    private static String stripSpaces(String value) {
        StringBuilder builder = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            if (!Character.isWhitespace(ch)) {
                builder.append(ch);
            }
        }
        return builder.toString();
    }

    /**
     * 是否为偶数长度的纯十六进制。
     *
     * @param value 已去掉空白和 {@code 0x} 前缀
     * @return 可按 hex 解码则为 {@code true}
     */
    private static boolean isEvenHex(String value) {
        int length = value.length();
        if (length == 0 || (length & 1) != 0) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            if (Character.digit(value.charAt(i), 16) < 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * 十六进制字符串转字节，调用前须已确认 {@link #isEvenHex(String)}。
     *
     * @param hex 偶数长度 hex
     * @return 解码后的字节
     */
    private static byte[] hexToBytes(String hex) {
        int length = hex.length();
        byte[] bytes = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            bytes[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return bytes;
    }

}
