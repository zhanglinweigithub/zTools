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

@Service(value = Service.Level.PROJECT)
@State(name = "JasyptCryptoConfig", storages = {@Storage(ZToolsConstant.STORAGE_FILE)})
public final class JasyptCryptoConfig implements PersistentStateComponent<JasyptCryptoConfig> {

    /** Jasypt 加密密码，多个密码以 ; 分隔 */
    private String password = EMPTY;

    /** Jasypt PBE 加密算法 */
    private String cryptoAlgorithm = ZToolsConstant.DEFAULT_PBE_ALGORITHM;

    /** 密钥派生迭代次数 */
    private int keyObtentionIterations = 1000;

    /** 输出类型：base64 / hexadecimal */
    private String outputType = JasyptOutputType.OUTPUT_OPTIONS.get(0);

    /** Salt Generator 类名 */
    private String saltGenerator = JasyptSalt.SALT_OPTIONS.get(0);

    /** IV Generator 类名 */
    private String ivGenerator = JasyptIV.IV_OPTIONS.get(0);

    /** 密文包裹格式，如 ENC(%s)，%s 为密文占位符 */
    private String encWrapper = ZToolsConstant.ENC_WRAPPER;

    public static JasyptCryptoConfig getInstance(Project project) {
        return project.getService(JasyptCryptoConfig.class);
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
        return idx >= 0 ? encWrapper.substring(idx + 2) : EMPTY;
    }

    @Override
    public JasyptCryptoConfig getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull JasyptCryptoConfig jasyptCryptoConfig) {
        XmlSerializerUtil.copyBean(jasyptCryptoConfig, this);
    }
}
