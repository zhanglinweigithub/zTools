package com.zhanglinwei.zTools.configure.enums;

import org.jasypt.iv.IvGenerator;
import org.jasypt.iv.NoIvGenerator;
import org.jasypt.iv.RandomIvGenerator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Jasypt IV 生成器选项，对应设置页「IV Generator」。
 * <p>
 * 部分 PBE 算法（如 {@code PBEWITHMD5ANDDES}）不需要 IV，应选 {@link #NO}。
 */
public enum JasyptIV {

    /** 不生成 IV，适配不含 IV 的算法 */
    NO("NoIvGenerator", new NoIvGenerator()),
    /** 随机 IV，适配需要 IV 的算法 */
    RANDOM("RandomIvGenerator", new RandomIvGenerator()),
    ;

    /** 持久化到配置里的类名简写 */
    private final String code;
    /** 实际交给 Encryptor 的生成器实例 */
    private final IvGenerator generator;

    /**
     * @param code      配置存储值
     * @param generator Jasypt IV 生成器
     */
    JasyptIV(String code, IvGenerator generator) {
        this.code = code;
        this.generator = generator;
    }

    /**
     * 获取配置存储值。
     *
     * @return 如 {@code NoIvGenerator}
     */
    public String getCode() {
        return code;
    }

    /**
     * 获取 Jasypt IV 生成器。
     *
     * @return IV 生成器实例
     */
    public IvGenerator getGenerator() {
        return generator;
    }

    /** 设置页下拉选项 */
    public static final List<String> IV_OPTIONS;

    static {
        List<String> ivList = Arrays.stream(JasyptIV.values())
                .map(JasyptIV::getCode)
                .collect(Collectors.toList());
        IV_OPTIONS = Collections.unmodifiableList(ivList);
    }

    /**
     * 按配置名解析，忽略大小写；未知值回退为无 IV。
     *
     * @param code 配置中保存的生成器名
     * @return 对应枚举
     */
    public static JasyptIV codeOf(String code) {
        return Arrays.stream(JasyptIV.values())
                .filter(item -> item.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElse(JasyptIV.NO);
    }

}
