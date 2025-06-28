package com.zhanglinwei.zTools.sensitive.crypto;

import com.zhanglinwei.zTools.util.AssertUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class AES extends Crypto {

    private static final String AES_PREFIX = "AES[";
    private static final String AES_SUFFIX = "]";
    private static final String EXPRESSION = "AES[%s]";
    private static final String AES = "AES";
    private static final String SHA_256 = "SHA-256";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final int IV_SIZE = 16;
    private static final String DEFAULT_KEY = "cc8894db9c1f1284dee5f5389baba43c";

    @Override
    public String encrypt(String content, String secretKey, String iv) throws Exception {
        return encrypt(secretKey, content);
    }

    @Override
    public String decrypt(String content, String secretKey, String iv) throws Exception {
        return decrypt(secretKey, content);
    }

    @Override
    public boolean isEncrypted(String text) {
        return AssertUtils.isNotBlank(text) && text.startsWith(AES_PREFIX) && text.endsWith(AES_SUFFIX);
    }

    @Override
    public String getPrefix() {
        return AES_PREFIX;
    }

    @Override
    public String getSuffix() {
        return AES_SUFFIX;
    }

    @Override
    public String getExpression() {
        return EXPRESSION;
    }

    public static String encrypt(String content) throws Exception {
        return encrypt(DEFAULT_KEY, content);
    }

    public static String decrypt(String content) throws Exception {
        return decrypt(DEFAULT_KEY, content);
    }

    public static String encrypt(String key, String content) throws Exception {
        byte[] encrypted = encrypt(key, content.getBytes(StandardCharsets.UTF_8));
        return encrypted == null ? null : new String(Base64.getEncoder().encode(encrypted), StandardCharsets.UTF_8);
    }

    public static String decrypt(String key, String content) throws Exception {
        byte[] decrypted = decrypt(key, Base64.getDecoder().decode(content.getBytes(StandardCharsets.UTF_8)));
        return decrypted == null ? null : new String(decrypted, StandardCharsets.UTF_8);
    }

    public static byte[] encrypt(String key, byte[] content) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        IvParameterSpec ivSpec = createIv(key);
        SecretKeySpec secretKey = createSecretKey(key);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
        return cipher.doFinal(content);
    }

    public static byte[] decrypt(String key, byte[] content) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        IvParameterSpec ivSpec = createIv(key);
        SecretKeySpec secretKey = createSecretKey(key);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
        return cipher.doFinal(content);
    }

    private static IvParameterSpec createIv(String key) throws Exception {
        byte[] keyBytes = MessageDigest.getInstance(SHA_256).digest(key.getBytes(StandardCharsets.UTF_8));
        byte[] iv = new byte[IV_SIZE];
        System.arraycopy(keyBytes, 0, iv, 0, IV_SIZE);
        return new IvParameterSpec(iv);
    }

    private static SecretKeySpec createSecretKey(String key) throws Exception {
        byte[] keyBytes = MessageDigest.getInstance(SHA_256).digest(key.getBytes(StandardCharsets.UTF_8));
        return new SecretKeySpec(keyBytes, AES);
    }

}
