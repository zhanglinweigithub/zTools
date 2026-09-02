package com.zhanglinwei.zTools.jasyptcrypto.utils;

import com.intellij.openapi.project.Project;
import com.zhanglinwei.zTools.configure.config.JasyptCryptoConfig;
import com.zhanglinwei.zTools.common.util.StringUtils;

import static com.zhanglinwei.zTools.common.constant.StringPool.SEMICOLON;

public class JasyptUtils {

    /**
     * 获取配置中的密码列表（已去除空白项）
     */
    public static String[] getPasswords(Project project) {
        JasyptCryptoConfig config = JasyptCryptoConfig.getInstance(project);
        return splitPasswords(config.getPassword());
    }

    public static String[] getPasswords(JasyptCryptoConfig config) {
        return splitPasswords(config.getPassword());
    }

    private static String[] splitPasswords(String password) {
        if (StringUtils.isBlank(password)) {
            return new String[0];
        }
        String[] raw = password.split(SEMICOLON);
        java.util.List<String> list = new java.util.ArrayList<>();
        for (String pwd : raw) {
            if (StringUtils.isNotBlank(pwd)) {
                list.add(pwd.trim());
            }
        }
        return list.toArray(new String[0]);
    }

}
