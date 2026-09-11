package com.zhanglinwei.zTools.jasyptcrypto.facade;

import com.zhanglinwei.zTools.configure.config.JasyptCryptoConfig;
import com.zhanglinwei.zTools.configure.enums.JasyptIV;
import com.zhanglinwei.zTools.configure.enums.JasyptOutputType;
import com.zhanglinwei.zTools.configure.enums.JasyptSalt;
import com.zhanglinwei.zTools.jasyptcrypto.utils.EncWrapper;
import com.zhanglinwei.zTools.jasyptcrypto.utils.JasyptUtils;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

/**
 * 按项目 Jasypt 配置封装 {@link StandardPBEStringEncryptor}。
 * <p>
 * 每个实例绑定一个密码。明文 / 密文形态见各加解密方法注释（不要把真实密钥写进代码）。
 */
public class JasyptCrypto {
    private final StandardPBEStringEncryptor encryptor;
    private final EncWrapper encWrapper;

    /**
     * 用配置与单个密码初始化 Encryptor；盐 / IV 取配置中第一项（多值场景请用四参数构造）。
     *
     * @param config   项目 Jasypt 配置
     * @param password 本次使用的密码（多密码场景由调用方先选出一个）
     */
    public JasyptCrypto(JasyptCryptoConfig config, String password) {
        this(config, password,
                JasyptUtils.firstOrEmpty(JasyptUtils.getSaltValues(config)),
                JasyptUtils.firstOrEmpty(JasyptUtils.getIvValues(config)));
    }

    /**
     * 用配置、单个密码以及本次使用的固定盐 / IV 初始化 Encryptor。
     *
     * @param config    项目 Jasypt 配置
     * @param password  本次使用的密码
     * @param saltValue 本次使用的固定盐；非 Fixed 生成器可传空
     * @param ivValue   本次使用的固定 IV；非 Fixed 生成器可传空
     */
    public JasyptCrypto(JasyptCryptoConfig config, String password, String saltValue, String ivValue) {
        this.encWrapper = EncWrapper.from(config);

        this.encryptor = new StandardPBEStringEncryptor();
        this.encryptor.setAlgorithm(config.getCryptoAlgorithm());
        this.encryptor.setPassword(password);
        this.encryptor.setKeyObtentionIterations(config.getKeyObtentionIterations());

        // 按配置名解析盐生成器（Zero 可复现，Random 每次密文不同，Fixed 使用本次传入的盐值）
        JasyptSalt jasyptSalt = JasyptSalt.codeOf(config.getSaltGenerator());
        this.encryptor.setSaltGenerator(jasyptSalt.create(saltValue));

        // 按配置名解析 IV；PBEWITHMD5ANDDES 等算法应使用 NoIvGenerator
        JasyptIV jasyptIV = JasyptIV.codeOf(config.getIvGenerator());
        this.encryptor.setIvGenerator(jasyptIV.create(ivValue));

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
     * 需要按配置加前缀 / 后缀请用 {@link #encryptWithWrapper(String)}。
     *
     * @param plaintext 明文
     * @return 裸密文
     */
    public String encrypt(String plaintext) {
        return encryptor.encrypt(plaintext);
    }

    /**
     * 解密密文。若带当前配置的前缀 / 后缀会先剥掉再解密。
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
        return encryptor.decrypt(encWrapper.unwrap(ciphertext));
    }

    /**
     * 判断文本是否为当前配置下的加密包裹格式。
     * <p>
     * 有前缀则须以该前缀开头，有后缀则须以该后缀结尾；两边都未配置时返回 {@code false}。
     *
     * @param text 待判断文本
     * @return 符合包裹格式则为 {@code true}
     */
    public boolean isEncrypted(String text) {
        return encWrapper.isWrapped(text);
    }

    /**
     * 去掉已配置且实际出现的前缀、后缀。
     *
     * @param text 可能带包裹的文本
     * @return 中间密文
     */
    public String extractEncryptedValue(String text) {
        return encWrapper.unwrap(text);
    }

    /**
     * 将明文加密并按配置加上前缀 / 后缀（缺哪边就不加哪边）。
     *
     * @param plaintext 明文
     * @return 可写入配置文件的密文
     */
    public String encryptWithWrapper(String plaintext) {
        return encWrapper.wrap(encrypt(plaintext));
    }

    /**
     * 获取密文包裹前缀。
     *
     * @return 如 {@code ENC(}；未配置则为空串
     */
    public String getPrefix() {
        return encWrapper.getPrefix();
    }

    /**
     * 获取密文包裹后缀。
     *
     * @return 如 {@code )}；未配置则为空串
     */
    public String getSuffix() {
        return encWrapper.getSuffix();
    }

    /**
     * 还原为带 {@code %s} 的包裹模板。
     *
     * @return 如 {@code ENC(%s)}
     */
    public String getExpression() {
        return encWrapper.getPrefix() + "%s" + encWrapper.getSuffix();
    }
}
