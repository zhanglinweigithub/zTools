package com.zhanglinwei.zTools.sensitive.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.zhanglinwei.zTools.sensitive.constants.SensitiveDataConstant;
import org.jetbrains.annotations.NotNull;

@State(name = "SensitiveDataConfig")
public class SensitiveDataConfig implements PersistentStateComponent<SensitiveDataConfig> {

    /** Jasypt 加密密码，多个密码以 ; 分隔 */
    private String password = "";

    /** Jasypt PBE 加密算法 */
    private String cryptoAlgorithm = SensitiveDataConstant.PBE_WITH_MD5_AND_DES;

    /** 密钥派生迭代次数 */
    private int keyObtentionIterations = 1000;

    /** 输出类型：base64 / hexadecimal */
    private String outputType = SensitiveDataConstant.OUTPUT_TYPE_BASE64;

    /** Salt Generator 类名 */
    private String saltGenerator = SensitiveDataConstant.SALT_RANDOM;

    /** IV Generator 类名 */
    private String ivGenerator = SensitiveDataConstant.IV_NO;

    /** 密文包裹格式，如 ENC(%s)，%s 为密文占位符 */
    private String encWrapper = SensitiveDataConstant.ENC_WRAPPER;

    public static SensitiveDataConfig getInstance(Project project) {
        return project.getService(SensitiveDataConfig.class);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCryptoAlgorithm() {
        return cryptoAlgorithm;
    }

    public void setCryptoAlgorithm(String cryptoAlgorithm) {
        this.cryptoAlgorithm = cryptoAlgorithm;
    }

    public int getKeyObtentionIterations() {
        return keyObtentionIterations;
    }

    public void setKeyObtentionIterations(int keyObtentionIterations) {
        this.keyObtentionIterations = keyObtentionIterations;
    }

    public String getOutputType() {
        return outputType;
    }

    public void setOutputType(String outputType) {
        this.outputType = outputType;
    }

    public String getSaltGenerator() {
        return saltGenerator;
    }

    public void setSaltGenerator(String saltGenerator) {
        this.saltGenerator = saltGenerator;
    }

    public String getIvGenerator() {
        return ivGenerator;
    }

    public void setIvGenerator(String ivGenerator) {
        this.ivGenerator = ivGenerator;
    }

    public String getEncWrapper() {
        return encWrapper;
    }

    public void setEncWrapper(String encWrapper) {
        this.encWrapper = encWrapper;
    }

    /**
     * 从 encWrapper 中提取前缀，如 "ENC(%s)" → "ENC("
     */
    public String getEncPrefix() {
        int idx = encWrapper.indexOf("%s");
        return idx >= 0 ? encWrapper.substring(0, idx) : encWrapper;
    }

    /**
     * 从 encWrapper 中提取后缀，如 "ENC(%s)" → ")"
     */
    public String getEncSuffix() {
        int idx = encWrapper.indexOf("%s");
        return idx >= 0 ? encWrapper.substring(idx + 2) : "";
    }

    @Override
    public SensitiveDataConfig getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull SensitiveDataConfig sensitiveDataConfig) {
        XmlSerializerUtil.copyBean(sensitiveDataConfig, this);
    }
}
