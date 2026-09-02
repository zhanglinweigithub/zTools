package com.zhanglinwei.zTools.configure.enums;

import org.jasypt.salt.RandomSaltGenerator;
import org.jasypt.salt.SaltGenerator;
import org.jasypt.salt.ZeroSaltGenerator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Jasypt Salt 生成器选项，对应设置页「Salt Generator」。
 * <p>
 * Zero 适合需要可复现密文的场景；Random 每次加密盐不同，密文也会变。
 */
public enum JasyptSalt {

    /** 固定零盐，同一明文多次加密结果相同 */
    ZERO("ZeroSaltGenerator", new ZeroSaltGenerator()),
    /** 随机盐，同一明文每次密文不同 */
    RANDOM("RandomSaltGenerator", new RandomSaltGenerator()),
    ;

    /** 持久化到配置里的类名简写 */
    private final String code;
    /** 实际交给 Encryptor 的生成器实例 */
    private final SaltGenerator generator;

    /**
     * @param code      配置存储值
     * @param generator Jasypt 盐生成器
     */
    JasyptSalt(String code, SaltGenerator generator) {
        this.code = code;
        this.generator = generator;
    }

    /**
     * 获取配置存储值。
     *
     * @return 如 {@code ZeroSaltGenerator}
     */
    public String getCode() {
        return code;
    }

    /**
     * 获取 Jasypt 盐生成器。
     *
     * @return 盐生成器实例
     */
    public SaltGenerator getGenerator() {
        return generator;
    }

    /** 设置页下拉选项 */
    public static final List<String> SALT_OPTIONS;

    static {
        List<String> saltList = Arrays.stream(JasyptSalt.values())
                .map(JasyptSalt::getCode)
                .collect(Collectors.toList());
        SALT_OPTIONS = Collections.unmodifiableList(saltList);
    }

    /**
     * 按配置名解析，忽略大小写；未知值回退为零盐。
     *
     * @param code 配置中保存的生成器名
     * @return 对应枚举
     */
    public static JasyptSalt codeOf(String code) {
        return Arrays.stream(JasyptSalt.values())
                .filter(item -> item.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElse(JasyptSalt.ZERO);
    }

}
