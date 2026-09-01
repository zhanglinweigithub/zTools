package com.zhanglinwei.zTools.sensitive.facade;

import com.intellij.openapi.project.Project;
import com.zhanglinwei.zTools.sensitive.config.SensitiveDataConfig;
import com.zhanglinwei.zTools.sensitive.model.SensitivePair;
import com.zhanglinwei.zTools.util.StringUtils;

public class SensitiveFacade {
    /**
     * 获取配置中的密码列表（已去除空白项）
     */
    public static String[] getPasswords(Project project) {
        SensitiveDataConfig config = SensitiveDataConfig.getInstance(project);
        return splitPasswords(config.getPassword());
    }

    private static String[] splitPasswords(String password) {
        if (StringUtils.isBlank(password)) {
            return new String[0];
        }
        String[] raw = password.split(";");
        java.util.List<String> list = new java.util.ArrayList<>();
        for (String pwd : raw) {
            if (StringUtils.isNotBlank(pwd)) {
                list.add(pwd.trim());
            }
        }
        return list.toArray(new String[0]);
    }

    public static SensitivePair encrypt(Project project, String targetContent) throws Exception {
        return encrypt(project, targetContent, null);
    }

    public static SensitivePair encrypt(Project project, String targetContent, String chosenPassword) throws Exception {
        if (StringUtils.isBlank(targetContent)) {
            return null;
        }
        String text = targetContent.trim();

        SensitiveDataConfig config = SensitiveDataConfig.getInstance(project);

        // 确定加密使用的密码
        String encryptPassword;
        if (StringUtils.isNotBlank(chosenPassword)) {
            encryptPassword = chosenPassword;
        } else {
            // 单个密码直接使用，多个密码返回 null 让调用方弹窗选择
            String[] passwords = splitPasswords(config.getPassword());
            if (passwords.length == 0) {
                throw new IllegalArgumentException("Password is required. Please configure it in Settings > zzz-sensitive.");
            }
            if (passwords.length == 1) {
                encryptPassword = passwords[0];
            } else {
                // 多密码，需要调用方弹窗选择
                return null;
            }
        }

        SensitiveDataConfig tempConfig = new SensitiveDataConfig();
        tempConfig.setPassword(encryptPassword);
        tempConfig.setCryptoAlgorithm(config.getCryptoAlgorithm());
        tempConfig.setKeyObtentionIterations(config.getKeyObtentionIterations());
        tempConfig.setOutputType(config.getOutputType());
        tempConfig.setSaltGenerator(config.getSaltGenerator());
        tempConfig.setIvGenerator(config.getIvGenerator());
        tempConfig.setEncWrapper(config.getEncWrapper());

        JasyptCrypto crypto = new JasyptCrypto(tempConfig);
        String encrypted = crypto.encrypt(text);
        return new SensitivePair(targetContent, String.format(crypto.getExpression(), encrypted));
    }

    public static SensitivePair decrypt(Project project, String targetContent) throws Exception {
        if (StringUtils.isBlank(targetContent)) {
            return null;
        }

        SensitiveDataConfig config = SensitiveDataConfig.getInstance(project);
        String trimmedContent = targetContent.trim();

        if (!new JasyptCrypto(config).isEncrypted(trimmedContent)) {
            return new SensitivePair(trimmedContent, targetContent);
        }

        // 提取密文部分
        String ciphertext = new JasyptCrypto(config).extractEncryptedValue(trimmedContent);
        if (ciphertext == null) {
            return new SensitivePair(trimmedContent, targetContent);
        }

        // 多密码逐个尝试解密
        String[] passwords = config.getPassword().split(";");
        String plaintext = null;
        for (String pwd : passwords) {
            if (StringUtils.isBlank(pwd)) {
                continue;
            }
            try {
                SensitiveDataConfig tempConfig = new SensitiveDataConfig();
                tempConfig.setPassword(pwd.trim());
                tempConfig.setCryptoAlgorithm(config.getCryptoAlgorithm());
                tempConfig.setKeyObtentionIterations(config.getKeyObtentionIterations());
                tempConfig.setOutputType(config.getOutputType());
                tempConfig.setSaltGenerator(config.getSaltGenerator());
                tempConfig.setIvGenerator(config.getIvGenerator());
                tempConfig.setEncWrapper(config.getEncWrapper());

                JasyptCrypto crypto = new JasyptCrypto(tempConfig);
                plaintext = crypto.decrypt(ciphertext);
                break;
            } catch (Exception ignored) {
                // 当前密码解密失败，尝试下一个
            }
        }

        if (plaintext != null) {
            return new SensitivePair(trimmedContent, plaintext);
        } else {
            // 所有密码都解密失败，保留原文
            return new SensitivePair(trimmedContent, trimmedContent);
        }
    }

    /**
     * 批量解密文本中所有 prefix...suffix 密文，返回替换后的完整文本
     * 支持多密码：以 ; 分隔的密码依次尝试解密
     */
    public static String decryptAll(Project project, String content) throws Exception {
        if (StringUtils.isBlank(content)) {
            return content;
        }

        SensitiveDataConfig config = SensitiveDataConfig.getInstance(project);
        String[] passwords = config.getPassword().split(";");
        String prefix = config.getEncPrefix();
        String suffix = config.getEncSuffix();

        StringBuilder result = new StringBuilder();
        int i = 0;

        while (i < content.length()) {
            int encStart = content.indexOf(prefix, i);
            if (encStart == -1) {
                result.append(content.substring(i));
                break;
            }

            // 追加 prefix 之前的原文
            result.append(content.substring(i, encStart));

            int encEnd = content.indexOf(suffix, encStart + prefix.length());
            if (encEnd == -1) {
                result.append(content.substring(encStart));
                break;
            }

            // 提取密文，依次用每个密码尝试解密
            String ciphertext = content.substring(encStart + prefix.length(), encEnd);
            String plaintext = null;
            for (String pwd : passwords) {
                if (StringUtils.isBlank(pwd)) {
                    continue;
                }
                try {
                    SensitiveDataConfig tempConfig = new SensitiveDataConfig();
                    tempConfig.setPassword(pwd.trim());
                    tempConfig.setCryptoAlgorithm(config.getCryptoAlgorithm());
                    tempConfig.setKeyObtentionIterations(config.getKeyObtentionIterations());
                    tempConfig.setOutputType(config.getOutputType());
                    tempConfig.setSaltGenerator(config.getSaltGenerator());
                    tempConfig.setIvGenerator(config.getIvGenerator());
                    tempConfig.setEncWrapper(config.getEncWrapper());

                    JasyptCrypto crypto = new JasyptCrypto(tempConfig);
                    plaintext = crypto.decrypt(ciphertext);
                    break;
                } catch (Exception ignored) {
                    // 当前密码解密失败，尝试下一个
                }
            }

            if (plaintext != null) {
                result.append(plaintext);
            } else {
                // 所有密码都解密失败，保留原文
                result.append(prefix).append(ciphertext).append(suffix);
            }

            i = encEnd + suffix.length();
        }

        return result.toString();
    }
}
