package com.zhanglinwei.zTools.configure.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.zhanglinwei.zTools.configure.constants.ZToolsConstant;
import com.zhanglinwei.zTools.configure.enums.JasyptIV;
import com.zhanglinwei.zTools.configure.enums.JasyptOutputType;
import com.zhanglinwei.zTools.configure.enums.JasyptSalt;
import org.jetbrains.annotations.NotNull;

import static com.zhanglinwei.zTools.common.constant.StringPool.EMPTY;

/**
 * Jasypt 加解密配置（项目级，写入 {@code zTools.xml}）。
 * <p>
 * 设置页「Jasypt Crypto」Tab 与加解密 Action 共用本类。密码可写多个，以 {@code ;} 分隔；
 * 加密时若有多个密码会弹窗选择，解密时按顺序尝试直到成功。
 */
@Service(value = Service.Level.PROJECT)
@State(name = "JasyptCryptoConfig", storages = {@Storage(ZToolsConstant.STORAGE_FILE)})
public final class JasyptCryptoConfig implements PersistentStateComponent<JasyptCryptoConfig> {

    /** Jasypt 加密密码，多个密码以 {@code ;} 分隔，不要把真实密钥写进注释或日志 */
    private String password = EMPTY;

    /** Jasypt PBE 加密算法，默认 {@code PBEWITHMD5ANDDES} */
    private String cryptoAlgorithm = ZToolsConstant.DEFAULT_PBE_ALGORITHM;

    /** 密钥派生迭代次数，越大越慢越安全 */
    private int keyObtentionIterations = 1000;

    /** 密文输出编码：{@code base64} / {@code hexadecimal} */
    private String outputType = JasyptOutputType.OUTPUT_OPTIONS.get(0);

    /** Salt Generator 类名简写，见 {@link JasyptSalt} */
    private String saltGenerator = JasyptSalt.SALT_OPTIONS.get(0);

    /** IV Generator 类名简写，见 {@link JasyptIV} */
    private String ivGenerator = JasyptIV.IV_OPTIONS.get(0);

    /** 固定盐值，仅 {@link JasyptSalt#requiresValue()} 为 true 时使用 */
    private String saltValue = EMPTY;

    /** 固定 IV 值，仅 {@link JasyptIV#requiresValue()} 为 true 时使用 */
    private String ivValue = EMPTY;

    /**
     * 密文包裹格式，{@code %s} 为密文占位。
     * 默认 {@code ENC(%s)}，与 Spring {@code ENC(...)} 一致。
     */
    private String encWrapper = ZToolsConstant.ENC_WRAPPER;

    /**
     * 取当前项目的 Jasypt 配置。
     *
     * @param project 当前工程
     * @return 项目级配置
     */
    public static JasyptCryptoConfig getInstance(Project project) {
        return project.getService(JasyptCryptoConfig.class);
    }

    /**
     * 获取密码串（可能含多个，以 {@code ;} 分隔）。
     *
     * @return 密码配置原文
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置密码串。
     *
     * @param password 单个密码，或多个以 {@code ;} 分隔
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取 PBE 算法名。
     *
     * @return 算法，如 {@code PBEWITHMD5ANDDES}
     */
    public String getCryptoAlgorithm() {
        return cryptoAlgorithm;
    }

    /**
     * 设置 PBE 算法名。
     *
     * @param cryptoAlgorithm Jasypt 算法名
     */
    public void setCryptoAlgorithm(String cryptoAlgorithm) {
        this.cryptoAlgorithm = cryptoAlgorithm;
    }

    /**
     * 获取密钥派生迭代次数。
     *
     * @return 迭代次数
     */
    public int getKeyObtentionIterations() {
        return keyObtentionIterations;
    }

    /**
     * 设置密钥派生迭代次数。
     *
     * @param keyObtentionIterations 迭代次数
     */
    public void setKeyObtentionIterations(int keyObtentionIterations) {
        this.keyObtentionIterations = keyObtentionIterations;
    }

    /**
     * 获取密文输出编码。
     *
     * @return {@code base64} 或 {@code hexadecimal}
     */
    public String getOutputType() {
        return outputType;
    }

    /**
     * 设置密文输出编码。
     *
     * @param outputType {@code base64} 或 {@code hexadecimal}
     */
    public void setOutputType(String outputType) {
        this.outputType = outputType;
    }

    /**
     * 获取 Salt 生成器配置名。
     *
     * @return 如 {@code ZeroSaltGenerator}
     */
    public String getSaltGenerator() {
        return saltGenerator;
    }

    /**
     * 设置 Salt 生成器配置名。
     *
     * @param saltGenerator 见 {@link JasyptSalt#getCode()}
     */
    public void setSaltGenerator(String saltGenerator) {
        this.saltGenerator = saltGenerator;
    }

    /**
     * 获取 IV 生成器配置名。
     *
     * @return 如 {@code NoIvGenerator}
     */
    public String getIvGenerator() {
        return ivGenerator;
    }

    /**
     * 设置 IV 生成器配置名。
     *
     * @param ivGenerator 见 {@link JasyptIV#getCode()}
     */
    public void setIvGenerator(String ivGenerator) {
        this.ivGenerator = ivGenerator;
    }

    /**
     * 获取固定盐值。
     *
     * @return 用户填写的盐；未填为空串
     */
    public String getSaltValue() {
        return saltValue == null ? EMPTY : saltValue;
    }

    /**
     * 设置固定盐值。
     *
     * @param saltValue {@link JasyptSalt#BYTE_ARRAY_FIXED} 为 hex 或文本；{@link JasyptSalt#STRING_FIXED} 为字符串原文
     */
    public void setSaltValue(String saltValue) {
        this.saltValue = saltValue;
    }

    /**
     * 获取固定 IV 值。
     *
     * @return 用户填写的 IV；未填为空串
     */
    public String getIvValue() {
        return ivValue == null ? EMPTY : ivValue;
    }

    /**
     * 设置固定 IV 值。
     *
     * @param ivValue {@link JasyptIV#BYTE_ARRAY_FIXED} 为 hex 或文本；{@link JasyptIV#STRING_FIXED} 为字符串原文
     */
    public void setIvValue(String ivValue) {
        this.ivValue = ivValue;
    }

    /**
     * 获取密文包裹模板。
     *
     * @return 如 {@code ENC(%s)}
     */
    public String getEncWrapper() {
        return encWrapper;
    }

    /**
     * 设置密文包裹模板。
     *
     * @param encWrapper 须含 {@code %s} 作为密文占位
     */
    public void setEncWrapper(String encWrapper) {
        this.encWrapper = encWrapper;
    }

    /**
     * 从 encWrapper 中提取前缀，如 {@code ENC(%s)} → {@code ENC(}。
     *
     * @return 包裹前缀；没有 {@code %s} 时返回整个模板
     */
    public String getEncPrefix() {
        int idx = encWrapper.indexOf("%s");
        return idx >= 0 ? encWrapper.substring(0, idx) : encWrapper;
    }

    /**
     * 从 encWrapper 中提取后缀，如 {@code ENC(%s)} → {@code )}。
     *
     * @return 包裹后缀；没有 {@code %s} 时为空串
     */
    public String getEncSuffix() {
        int idx = encWrapper.indexOf("%s");
        return idx >= 0 ? encWrapper.substring(idx + 2) : EMPTY;
    }

    /**
     * 返回待持久化的状态（即自身）。
     *
     * @return 当前配置
     */
    @Override
    public JasyptCryptoConfig getState() {
        return this;
    }

    /**
     * 从磁盘状态拷贝到当前实例。
     *
     * @param jasyptCryptoConfig 反序列化得到的配置
     */
    @Override
    public void loadState(@NotNull JasyptCryptoConfig jasyptCryptoConfig) {
        XmlSerializerUtil.copyBean(jasyptCryptoConfig, this);
    }
}
