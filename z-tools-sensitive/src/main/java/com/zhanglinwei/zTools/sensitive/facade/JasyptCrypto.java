package com.zhanglinwei.zTools.sensitive.facade;

import com.zhanglinwei.zTools.sensitive.config.SensitiveDataConfig;
import com.zhanglinwei.zTools.sensitive.constants.SensitiveDataConstant;
import com.zhanglinwei.zTools.util.StringUtils;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.iv.IvGenerator;
import org.jasypt.iv.NoIvGenerator;
import org.jasypt.iv.RandomIvGenerator;
import org.jasypt.salt.RandomSaltGenerator;
import org.jasypt.salt.SaltGenerator;
import org.jasypt.salt.ZeroSaltGenerator;

public class JasyptCrypto {
    private final StandardPBEStringEncryptor encryptor;
    private final String encPrefix;
    private final String encSuffix;

    public JasyptCrypto(SensitiveDataConfig config) {
        this(config.getCryptoAlgorithm(), config.getPassword(),
                config.getKeyObtentionIterations(), config.getOutputType(),
                config.getSaltGenerator(), config.getIvGenerator(),
                config.getEncPrefix(), config.getEncSuffix());
    }

    public JasyptCrypto(String algorithm, String password, int keyObtentionIterations,
                        String outputType, String saltGeneratorName, String ivGeneratorName,
                        String encPrefix, String encSuffix) {
        if (StringUtils.isBlank(password)) {
            throw new IllegalArgumentException("Password is required. Please configure it in Settings > zzz-sensitive.");
        }
        if (StringUtils.isBlank(algorithm)) {
            throw new IllegalArgumentException("Algorithm is required.");
        }
        this.encPrefix = StringUtils.isBlank(encPrefix) ? "ENC(" : encPrefix;
        this.encSuffix = encSuffix == null ? ")" : encSuffix;
        this.encryptor = new StandardPBEStringEncryptor();
        this.encryptor.setAlgorithm(algorithm);
        this.encryptor.setPassword(password);
        this.encryptor.setKeyObtentionIterations(keyObtentionIterations);

        // 设置 Salt Generator
        this.encryptor.setSaltGenerator(createSaltGenerator(saltGeneratorName));

        // 设置 IV Generator
        this.encryptor.setIvGenerator(createIvGenerator(ivGeneratorName));

        // 设置输出类型
        if (SensitiveDataConstant.OUTPUT_TYPE_HEXADECIMAL.equalsIgnoreCase(outputType)) {
            this.encryptor.setStringOutputType("hexadecimal");
        } else {
            this.encryptor.setStringOutputType("base64");
        }
    }

    private static SaltGenerator createSaltGenerator(String name) {
        if (StringUtils.isBlank(name)) {
            return new RandomSaltGenerator();
        }
        switch (name) {
            case SensitiveDataConstant.SALT_ZERO:
                return new ZeroSaltGenerator();
            case SensitiveDataConstant.SALT_RANDOM:
            default:
                return new RandomSaltGenerator();
        }
    }

    private static IvGenerator createIvGenerator(String name) {
        if (StringUtils.isBlank(name)) {
            return new NoIvGenerator();
        }
        switch (name) {
            case SensitiveDataConstant.IV_RANDOM:
                return new RandomIvGenerator();
            case SensitiveDataConstant.IV_NO:
            default:
                return new NoIvGenerator();
        }
    }

    /**
     * 加密明文，返回不带包裹标签的密文
     */
    public String encrypt(String plaintext) {
        return encryptor.encrypt(plaintext);
    }

    /**
     * 解密密文（不带包裹标签）
     */
    public String decrypt(String ciphertext) {
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
            return null;
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

    /**
     * 解密 prefix...suffix 格式的密文，返回明文；如果不是加密格式，原样返回
     */
    public String decryptIfEncrypted(String text) {
        if (!isEncrypted(text)) {
            return text;
        }
        String ciphertext = extractEncryptedValue(text);
        return decrypt(ciphertext);
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
