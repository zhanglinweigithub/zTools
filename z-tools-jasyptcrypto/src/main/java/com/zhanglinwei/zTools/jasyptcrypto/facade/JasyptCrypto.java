package com.zhanglinwei.zTools.jasyptcrypto.facade;

import com.zhanglinwei.zTools.configure.config.JasyptCryptoConfig;
import com.zhanglinwei.zTools.configure.enums.JasyptIV;
import com.zhanglinwei.zTools.configure.enums.JasyptOutputType;
import com.zhanglinwei.zTools.configure.enums.JasyptSalt;
import com.zhanglinwei.zTools.common.util.StringUtils;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

public class JasyptCrypto {
    private final StandardPBEStringEncryptor encryptor;
    private final String encPrefix;
    private final String encSuffix;

    public JasyptCrypto(JasyptCryptoConfig config, String password) {
        this.encPrefix = config.getEncPrefix();
        this.encSuffix = config.getEncSuffix();

        this.encryptor = new StandardPBEStringEncryptor();
        this.encryptor.setAlgorithm(config.getCryptoAlgorithm());
        this.encryptor.setPassword(password);
        this.encryptor.setKeyObtentionIterations(config.getKeyObtentionIterations());

        // 设置 Salt Generator
        JasyptSalt jasyptSalt = JasyptSalt.codeOf(config.getSaltGenerator());
        this.encryptor.setSaltGenerator(jasyptSalt.getGenerator());

        // 设置 IV Generator
        JasyptIV jasyptIV = JasyptIV.codeOf(config.getIvGenerator());
        this.encryptor.setIvGenerator(jasyptIV.getGenerator());

        // 设置输出类型
        JasyptOutputType jasyptOutputType = JasyptOutputType.codeOf(config.getOutputType());
        this.encryptor.setStringOutputType(jasyptOutputType.getCode());
    }

    /**
     * 加密明文，返回不带包裹标签的密文
     */
    public String encrypt(String plaintext) {
        return encryptor.encrypt(plaintext);
    }

    /**
     * 解密密文
     */
    public String decrypt(String ciphertext) {
        if (isEncrypted(ciphertext)) {
            ciphertext = extractEncryptedValue(ciphertext);
        }
        return encryptor.decrypt(ciphertext);
    }

    /**
     * 判断文本是否为加密格式：prefix...suffix
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
     * 提取 prefix...suffix 中的密文部分
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
     * 将明文加密并包裹为 prefix+密文+suffix 格式
     */
    public String encryptWithWrapper(String plaintext) {
        return encPrefix + encrypt(plaintext) + encSuffix;
    }

    public String getPrefix() {
        return encPrefix;
    }

    public String getSuffix() {
        return encSuffix;
    }

    public String getExpression() {
        return encPrefix + "%s" + encSuffix;
    }
}
