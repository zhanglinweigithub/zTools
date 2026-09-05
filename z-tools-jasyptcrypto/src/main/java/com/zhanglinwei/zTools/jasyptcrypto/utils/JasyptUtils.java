package com.zhanglinwei.zTools.jasyptcrypto.utils;

import com.intellij.openapi.project.Project;
import com.zhanglinwei.zTools.common.util.StringUtils;
import com.zhanglinwei.zTools.configure.config.JasyptCryptoConfig;
import com.zhanglinwei.zTools.configure.enums.JasyptIV;
import com.zhanglinwei.zTools.configure.enums.JasyptSalt;
import com.zhanglinwei.zTools.jasyptcrypto.facade.JasyptCrypto;

import java.util.ArrayList;
import java.util.List;

import static com.zhanglinwei.zTools.common.constant.StringPool.EMPTY;
import static com.zhanglinwei.zTools.common.constant.StringPool.SEMICOLON;

/**
 * Jasypt 加解密辅助：从设置里拆出密码 / 固定盐 / 固定 IV 列表，并按组合尝试解密。
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
        return getPasswords(config);
    }

    /**
     * 从指定配置拆出密码列表。
     *
     * @param config Jasypt 配置
     * @return 密码数组；未配置时为空数组
     */
    public static String[] getPasswords(JasyptCryptoConfig config) {
        return splitValues(config.getPassword());
    }

    /**
     * 从指定配置拆出固定盐列表。
     *
     * @param config Jasypt 配置
     * @return 盐数组；未配置时为空数组
     */
    public static String[] getSaltValues(JasyptCryptoConfig config) {
        return splitValues(config.getSaltValue());
    }

    /**
     * 从指定配置拆出固定 IV 列表。
     *
     * @param config Jasypt 配置
     * @return IV 数组；未配置时为空数组
     */
    public static String[] getIvValues(JasyptCryptoConfig config) {
        return splitValues(config.getIvValue());
    }

    /**
     * 取数组第一项；空数组时返回空串。
     *
     * @param values 已拆分的值
     * @return 第一项或 {@code ""}
     */
    public static String firstOrEmpty(String[] values) {
        return values == null || values.length == 0 ? EMPTY : values[0];
    }

    /**
     * 按配置中的密码、固定盐、固定 IV 做笛卡尔积尝试解密，任一组合成功即返回。
     * <p>
     * 非 Fixed 的盐 / IV 生成器不参与枚举，只试一次。
     *
     * @param config     Jasypt 配置
     * @param ciphertext 裸密文或带包裹的密文
     * @return 明文；全部失败则为 {@code null}
     */
    public static String tryDecrypt(JasyptCryptoConfig config, String ciphertext) {
        if (StringUtils.isBlank(ciphertext)) {
            return null;
        }
        String[] passwords = getPasswords(config);
        String[] salts = candidates(JasyptSalt.codeOf(config.getSaltGenerator()).requiresValue(), getSaltValues(config));
        String[] ivs = candidates(JasyptIV.codeOf(config.getIvGenerator()).requiresValue(), getIvValues(config));
        for (String password : passwords) {
            for (String salt : salts) {
                for (String iv : ivs) {
                    try {
                        return new JasyptCrypto(config, password, salt, iv).decrypt(ciphertext);
                    } catch (Exception ignored) {
                        // 当前组合失败，试下一组
                    }
                }
            }
        }
        return null;
    }

    /**
     * Fixed 类型用配置里的值列表；否则只保留一个空占位，让外层循环跑一次。
     *
     * @param requiresValue 是否为 Fixed 生成器
     * @param values        已拆分的配置值
     * @return 解密时要枚举的值
     */
    private static String[] candidates(boolean requiresValue, String[] values) {
        if (!requiresValue) {
            return new String[]{EMPTY};
        }
        return values.length == 0 ? new String[]{EMPTY} : values;
    }

    /**
     * 按 {@code ;} 拆分，丢掉空白项。
     *
     * @param raw 设置页原文，如 {@code a;b}
     * @return 非空值数组
     */
    private static String[] splitValues(String raw) {
        if (StringUtils.isBlank(raw)) {
            return new String[0];
        }
        String[] parts = raw.split(SEMICOLON);
        List<String> list = new ArrayList<>();
        for (String part : parts) {
            if (StringUtils.isNotBlank(part)) {
                list.add(part.trim());
            }
        }
        return list.toArray(new String[0]);
    }

}
