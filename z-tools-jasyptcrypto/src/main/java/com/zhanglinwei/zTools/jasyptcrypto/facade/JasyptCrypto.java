package com.zhanglinwei.zTools.jasyptcrypto.facade;

import com.zhanglinwei.zTools.configure.config.JasyptCryptoConfig;
import com.zhanglinwei.zTools.configure.enums.JasyptIV;
import com.zhanglinwei.zTools.configure.enums.JasyptOutputType;
import com.zhanglinwei.zTools.configure.enums.JasyptSalt;
import com.zhanglinwei.zTools.common.util.StringUtils;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

/**
 * 按项目 Jasypt 配置封装 {@link StandardPBEStringEncryptor}。
 * <p>
 * 每个实例绑定一个密码。明文 / 密文形态见各加解密方法注释（不要把真实密钥写进代码）。
 */
public class JasyptCrypto {
    private final StandardPBEStringEncryptor encryptor;
    private final String encPrefix;
    private final String encSuffix;

    /**
     * 用配置与单个密码初始化 Encryptor（算法、迭代、盐、IV、输出编码）。
     *
     * @param config   项目 Jasypt 配置
     * @param password 本次使用的密码（多密码场景由调用方先选出一个）
     */
    public JasyptCrypto(JasyptCryptoConfig config, String password) {
        this.encPrefix = config.getEncPrefix();
        this.encSuffix = config.getEncSuffix();

        this.encryptor = new StandardPBEStringEncryptor();
        this.encryptor.setAlgorithm(config.getCryptoAlgorithm());
        this.encryptor.setPassword(password);
        this.encryptor.setKeyObtentionIterations(config.getKeyObtentionIterations());

        // 按配置名解析盐生成器（Zero 可复现，Random 每次密文不同，Fixed 使用配置中的盐值）
        JasyptSalt jasyptSalt = JasyptSalt.codeOf(config.getSaltGenerator());
        this.encryptor.setSaltGenerator(jasyptSalt.create(config.getSaltValue()));

        // 按配置名解析 IV；PBEWITHMD5ANDDES 等算法应使用 NoIvGenerator
        JasyptIV jasyptIV = JasyptIV.codeOf(config.getIvGenerator());
        this.encryptor.setIvGenerator(jasyptIV.create(config.getIvValue()));

        // 密文编码：base64 或 hexadecimal
        JasyptOutputType jasyptOutputType = JasyptOutputType.codeOf(config.getOutputType());
        this.encryptor.setStringOutputType(jasyptOutputType.getCode());
    }

    /**
     * 加密明文，返回不带包裹标签的密文。
     * <p>
     * 示例（形态示意，非真实密钥/密文）：
     * <pre>
     *   输入明文：hello
     *   输出密文：xK8a2b...（base64）或 7a3f9c...（hexadecimal）
     *            这是 PBE 加密后再编码的结果，不是明文的简单 Base64
     * </pre>
     * 需要 {@code ENC(...)} 包裹请用 {@link #encryptWithWrapper(String)}。
     *
     * @param plaintext 明文
     * @return 裸密文
     */
    public String encrypt(String plaintext) {
        return encryptor.encrypt(plaintext);
    }

    /**
     * 解密密文。若带 {@code ENC(...)} 包裹会先剥掉再解密。
     * <p>
     * 示例：
     * <pre>
     *   输入：ENC(xK8a2b...) 或裸密文 xK8a2b...
     *   输出：hello
     * </pre>
     *
     * @param ciphertext 裸密文或带包裹的密文
     * @return 明文
     */
    public String decrypt(String ciphertext) {
        // 带 ENC(...) 时先取出中间密文
        if (isEncrypted(ciphertext)) {
            ciphertext = extractEncryptedValue(ciphertext);
        }
        return encryptor.decrypt(ciphertext);
    }

    /**
     * 判断文本是否为加密格式：prefix...suffix（默认 {@code ENC(} 与 {@code )}）。
     *
     * @param text 待判断文本
     * @return 同时具备前后缀则为 {@code true}
     */
    public boolean isEncrypted(String text) {
        if (StringUtils.isBlank(text)) {
            return false;
        }
        String trimmed = text.trim();
        return trimmed.startsWith(encPrefix)
                && trimmed.endsWith(encSuffix);
    }

    /**
     * 提取 prefix...suffix 中的密文部分。
     *
     * @param text 可能带包裹的文本
     * @return 中间密文；不是加密格式时原样返回
     */
    public String extractEncryptedValue(String text) {
        if (!isEncrypted(text)) {
            return text;
        }
        String trimmed = text.trim();
        return trimmed.substring(encPrefix.length(),
                trimmed.length() - encSuffix.length());
    }

    /**
     * 将明文加密并包裹为 prefix+密文+suffix。
     * <p>
     * 示例（默认包裹）：{@code hello} → {@code ENC(xK8a2b...)}。
     *
     * @param plaintext 明文
     * @return 带包裹的密文，可直接写入 {@code application.yml}
     */
    public String encryptWithWrapper(String plaintext) {
        return encPrefix + encrypt(plaintext) + encSuffix;
    }

    /**
     * 获取密文包裹前缀。
     *
     * @return 如 {@code ENC(}
     */
    public String getPrefix() {
        return encPrefix;
    }

    /**
     * 获取密文包裹后缀。
     *
     * @return 如 {@code )}
     */
    public String getSuffix() {
        return encSuffix;
    }

    /**
     * 还原为带 {@code %s} 的包裹模板。
     *
     * @return 如 {@code ENC(%s)}
     */
    public String getExpression() {
        return encPrefix + "%s" + encSuffix;
    }
}
