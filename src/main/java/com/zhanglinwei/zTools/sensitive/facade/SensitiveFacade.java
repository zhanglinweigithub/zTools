package com.zhanglinwei.zTools.sensitive.facade;

import com.intellij.openapi.project.Project;
import com.zhanglinwei.zTools.sensitive.config.SensitiveDataConfig;
import com.zhanglinwei.zTools.sensitive.crypto.Crypto;
import com.zhanglinwei.zTools.sensitive.crypto.CryptoFactory;
import com.zhanglinwei.zTools.sensitive.model.SensitivePair;
import com.zhanglinwei.zTools.util.AssertUtils;
import com.zhanglinwei.zTools.util.CommonUtils;

public class SensitiveFacade {

    public static SensitivePair encrypt(Project project, String targetContent) throws Exception {
        if (AssertUtils.isBlank(targetContent)) {
            return null;
        }
        String text = targetContent.trim();

        SensitiveDataConfig sensitiveDataConfig = SensitiveDataConfig.getInstance(project);
        Crypto cryptoHandler = CryptoFactory.getHandler(sensitiveDataConfig.getCryptoAlgorithm());
        String encrypted = cryptoHandler.encrypt(text, sensitiveDataConfig.getSecretKey(), sensitiveDataConfig.getIv());

        return new SensitivePair(targetContent, String.format(cryptoHandler.getExpression(), encrypted));
    }

    public static SensitivePair decrypt(Project project, String targetContent) throws Exception {
        if (AssertUtils.isBlank(targetContent)) {
            return null;
        }

        SensitiveDataConfig sensitiveDataConfig = SensitiveDataConfig.getInstance(project);
        Crypto cryptoHandler = CryptoFactory.getHandler(sensitiveDataConfig.getCryptoAlgorithm());

        String trimmedContent = targetContent.trim();
        String textWithoutPrefixSuffix = CommonUtils.substringBetween(trimmedContent, cryptoHandler.getPrefix(), cryptoHandler.getSuffix());

        String decrypted = cryptoHandler.isEncrypted(trimmedContent) ?
                cryptoHandler.decrypt(textWithoutPrefixSuffix, sensitiveDataConfig.getSecretKey(), sensitiveDataConfig.getIv()) :
                targetContent;

        return new SensitivePair(trimmedContent, decrypted);
    }
}
