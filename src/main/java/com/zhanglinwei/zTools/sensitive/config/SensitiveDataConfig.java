package com.zhanglinwei.zTools.sensitive.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.zhanglinwei.zTools.util.AssertUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.zhanglinwei.zTools.sensitive.constant.SensitiveDataConstant.DEFAULT_ALGORITHM;

@State(name = "SensitiveDataConfig")
public class SensitiveDataConfig implements PersistentStateComponent<SensitiveDataConfig> {

    private String cryptoAlgorithm;
    private String iv;
    private String secretKey;

    public static SensitiveDataConfig getInstance(Project project) {
        return project.getService(SensitiveDataConfig.class);
    }

    public String getCryptoAlgorithm() {
        return AssertUtils.isBlank(cryptoAlgorithm) ? DEFAULT_ALGORITHM : cryptoAlgorithm;
    }

    public void setCryptoAlgorithm(String cryptoAlgorithm) {
        this.cryptoAlgorithm = cryptoAlgorithm;
    }

    public String getIv() {
//        return iv;
        return "823c4a7371bb2982c93ee05424850c7a";
    }

    public void setIv(String iv) {
        this.iv = iv;
    }

    public String getSecretKey() {
        return "5ecbdbfa8c06742c84a6f17cdb9ee221";
//        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public @Nullable SensitiveDataConfig getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull SensitiveDataConfig sensitiveDataConfig) {
        XmlSerializerUtil.copyBean(sensitiveDataConfig, this);
    }
}
