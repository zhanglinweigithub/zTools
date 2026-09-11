package com.zhanglinwei.zTools.jasyptcrypto.utils;

import com.zhanglinwei.zTools.common.util.StringUtils;
import com.zhanglinwei.zTools.configure.config.JasyptCryptoConfig;

import java.util.function.Function;

import static com.zhanglinwei.zTools.common.constant.StringPool.EMPTY;

/**
 * 密文包裹：按配置处理前缀 / 后缀的四种组合。
 * <p>
 * {@code ENC(%s)} 有前有后；{@code ENC(%s} 有前无后；{@code %s)} 有后无前；{@code %s} 都没有。
 */
public final class EncWrapper {

    private final String prefix;
    private final String suffix;

    /**
     * @param prefix 前缀，空白视为没有
     * @param suffix 后缀，空白视为没有
     */
    public EncWrapper(String prefix, String suffix) {
        this.prefix = prefix == null ? EMPTY : prefix;
        this.suffix = suffix == null ? EMPTY : suffix;
    }

    /**
     * 从项目 Jasypt 配置构造。
     *
     * @param config 配置
     * @return 包裹器
     */
    public static EncWrapper from(JasyptCryptoConfig config) {
        if (config == null) {
            return new EncWrapper(EMPTY, EMPTY);
        }
        return new EncWrapper(config.getEncPrefix(), config.getEncSuffix());
    }

    /**
     * 是否配置了前缀。
     *
     * @return 有前缀则为 {@code true}
     */
    public boolean hasPrefix() {
        return StringUtils.isNotEmpty(prefix);
    }

    /**
     * 是否配置了后缀。
     *
     * @return 有后缀则为 {@code true}
     */
    public boolean hasSuffix() {
        return StringUtils.isNotEmpty(suffix);
    }

    /**
     * 是否同时配置了前缀和后缀。Decrypt File 需要两边都有才能扫描。
     *
     * @return 有前有后则为 {@code true}
     */
    public boolean hasPrefixAndSuffix() {
        return hasPrefix() && hasSuffix();
    }

    /**
     * 密文前缀。
     *
     * @return 可能为空串
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * 密文后缀。
     *
     * @return 可能为空串
     */
    public String getSuffix() {
        return suffix;
    }

    /**
     * 给裸密文加上已配置的前缀、后缀。
     *
     * @param ciphertext 裸密文
     * @return 包裹后的文本
     */
    public String wrap(String ciphertext) {
        String body = ciphertext == null ? EMPTY : ciphertext;
        return prefix + body + suffix;
    }

    /**
     * 去掉已配置且实际出现的前缀、后缀。缺哪边就只剥哪边。
     *
     * @param text 可能带包裹的文本
     * @return 中间密文；空白输入原样返回
     */
    public String unwrap(String text) {
        if (text == null) {
            return null;
        }
        String trimmed = text.trim();
        if (hasPrefix() && trimmed.startsWith(prefix)) {
            trimmed = trimmed.substring(prefix.length());
        }
        if (hasSuffix() && trimmed.endsWith(suffix)) {
            trimmed = trimmed.substring(0, trimmed.length() - suffix.length());
        }
        return trimmed;
    }

    /**
     * 文本是否带齐当前配置要求的前缀 / 后缀。两边都未配置时不算包裹格式。
     *
     * @param text 待判断文本
     * @return 符合包裹格式则为 {@code true}
     */
    public boolean isWrapped(String text) {
        if (StringUtils.isBlank(text) || (!hasPrefix() && !hasSuffix())) {
            return false;
        }
        String trimmed = text.trim();
        if (hasPrefix() && !trimmed.startsWith(prefix)) {
            return false;
        }
        if (hasSuffix() && !trimmed.endsWith(suffix)) {
            return false;
        }
        return true;
    }

    /**
     * 扫描全文，把能解开的密文片段换成明文。
     * <p>
     * 有前有后：按 {@code prefix...suffix} 成对切。
     * 只有前缀：前缀后的一段密文 token。
     * 只有后缀：后缀前的一段密文 token。
     * 都没有：无法定位密文，原文返回。
     *
     * @param content          文件全文
     * @param decryptOrNull    解裸密文；失败返回 {@code null}
     * @return 替换后的全文
     */
    public String replaceAll(String content, Function<String, String> decryptOrNull) {
        if (content == null || decryptOrNull == null) {
            return content;
        }
        if (hasPrefix() && hasSuffix()) {
            return replacePrefixedAndSuffixed(content, decryptOrNull);
        }
        if (hasPrefix()) {
            return replacePrefixedOnly(content, decryptOrNull);
        }
        if (hasSuffix()) {
            return replaceSuffixedOnly(content, decryptOrNull);
        }
        return content;
    }

    /**
     * 有前有后：{@code prefix + 密文 + suffix}。
     *
     * @param content       全文
     * @param decryptOrNull 解密
     * @return 替换后的全文
     */
    private String replacePrefixedAndSuffixed(String content, Function<String, String> decryptOrNull) {
        StringBuilder result = new StringBuilder();
        int i = 0;
        while (i < content.length()) {
            int encStart = content.indexOf(prefix, i);
            if (encStart < 0) {
                result.append(content.substring(i));
                break;
            }
            result.append(content.substring(i, encStart));
            int encEnd = content.indexOf(suffix, encStart + prefix.length());
            if (encEnd < 0) {
                result.append(content.substring(encStart));
                break;
            }
            String ciphertext = content.substring(encStart + prefix.length(), encEnd);
            String plaintext = decryptOrNull.apply(ciphertext);
            if (plaintext != null) {
                result.append(plaintext);
            } else {
                result.append(prefix).append(ciphertext).append(suffix);
            }
            i = encEnd + suffix.length();
        }
        return result.toString();
    }

    /**
     * 有前无后：{@code prefix + 密文 token}。
     *
     * @param content       全文
     * @param decryptOrNull 解密
     * @return 替换后的全文
     */
    private String replacePrefixedOnly(String content, Function<String, String> decryptOrNull) {
        StringBuilder result = new StringBuilder();
        int i = 0;
        while (i < content.length()) {
            int encStart = content.indexOf(prefix, i);
            if (encStart < 0) {
                result.append(content.substring(i));
                break;
            }
            result.append(content.substring(i, encStart));
            int cipherStart = encStart + prefix.length();
            int cipherEnd = endOfCipherToken(content, cipherStart);
            if (cipherEnd == cipherStart) {
                result.append(prefix);
                i = cipherStart;
                continue;
            }
            String ciphertext = content.substring(cipherStart, cipherEnd);
            String plaintext = decryptOrNull.apply(ciphertext);
            if (plaintext != null) {
                result.append(plaintext);
            } else {
                result.append(prefix).append(ciphertext);
            }
            i = cipherEnd;
        }
        return result.toString();
    }

    /**
     * 有后无前：{@code 密文 token + suffix}。
     *
     * @param content       全文
     * @param decryptOrNull 解密
     * @return 替换后的全文
     */
    private String replaceSuffixedOnly(String content, Function<String, String> decryptOrNull) {
        StringBuilder result = new StringBuilder();
        int i = 0;
        while (i < content.length()) {
            int encEnd = content.indexOf(suffix, i);
            if (encEnd < 0) {
                result.append(content.substring(i));
                break;
            }
            int cipherStart = startOfCipherToken(content, i, encEnd);
            if (cipherStart == encEnd) {
                result.append(content.substring(i, encEnd + suffix.length()));
                i = encEnd + suffix.length();
                continue;
            }
            result.append(content.substring(i, cipherStart));
            String ciphertext = content.substring(cipherStart, encEnd);
            String plaintext = decryptOrNull.apply(ciphertext);
            if (plaintext != null) {
                result.append(plaintext);
            } else {
                result.append(ciphertext).append(suffix);
            }
            i = encEnd + suffix.length();
        }
        return result.toString();
    }

    /**
     * 从 {@code from} 起读到第一个非密文字符。
     *
     * @param content 全文
     * @param from    起点
     * @return 密文结束下标（不含）
     */
    private static int endOfCipherToken(String content, int from) {
        int i = from;
        while (i < content.length() && isCipherChar(content.charAt(i))) {
            i++;
        }
        return i;
    }

    /**
     * 从 {@code end} 往回读到非密文字符或区间起点。
     *
     * @param content 全文
     * @param floor   不得再往前
     * @param end     后缀起点
     * @return 密文起始下标
     */
    private static int startOfCipherToken(String content, int floor, int end) {
        int i = end;
        while (i > floor && isCipherChar(content.charAt(i - 1))) {
            i--;
        }
        return i;
    }

    /**
     * base64 / hex 密文里会出现的字符。
     *
     * @param ch 字符
     * @return 可作为密文 token 则为 {@code true}
     */
    private static boolean isCipherChar(char ch) {
        return (ch >= 'A' && ch <= 'Z')
                || (ch >= 'a' && ch <= 'z')
                || (ch >= '0' && ch <= '9')
                || ch == '+' || ch == '/' || ch == '=' || ch == '-' || ch == '_';
    }
}
