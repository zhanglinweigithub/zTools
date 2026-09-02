package com.zhanglinwei.zTools.jasyptcrypto.utils;

import com.intellij.openapi.project.Project;
import com.zhanglinwei.zTools.configure.config.JasyptCryptoConfig;
import com.zhanglinwei.zTools.common.util.StringUtils;

import static com.zhanglinwei.zTools.common.constant.StringPool.SEMICOLON;

/**
 * Jasypt 加解密辅助：从设置里拆出密码列表。
 * <p>
 * 真正加解密在 {@link com.zhanglinwei.zTools.jasyptcrypto.facade.JasyptCrypto}。
 * 明文 / 密文形态（密钥来自设置，此处不写真实值）：
 * <pre>
 *   明文输入：hello
 *   加密输出（默认包裹）：ENC(&lt;base64 或 hex 密文&gt;)
 *   例如：ENC(xK8a2b...)          ← base64
 *         ENC(7a3f9c...)          ← hexadecimal
 *   解密输入：ENC(xK8a2b...) 或裸密文 xK8a2b...
 *   解密输出：hello
 * </pre>
 * 配置文件里多处 {@code ENC(...)} 由 {@code DecryptFileAction} 批量还原为明文。
 */
public class JasyptUtils {

    /**
     * 获取当前项目配置中的密码列表（已去除空白项）。
     *
     * @param project 当前工程
     * @return 密码数组；未配置时为空数组
     */
    public static String[] getPasswords(Project project) {
        JasyptCryptoConfig config = JasyptCryptoConfig.getInstance(project);
        return splitPasswords(config.getPassword());
    }

    /**
     * 从指定配置拆出密码列表。
     *
     * @param config Jasypt 配置
     * @return 密码数组；未配置时为空数组
     */
    public static String[] getPasswords(JasyptCryptoConfig config) {
        return splitPasswords(config.getPassword());
    }

    /**
     * 按 {@code ;} 拆分，丢掉空白项。
     *
     * @param password 设置页里的密码原文，如 {@code pwdA;pwdB}
     * @return 非空密码数组
     */
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
