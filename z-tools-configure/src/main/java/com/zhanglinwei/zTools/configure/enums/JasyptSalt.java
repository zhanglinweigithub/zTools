package com.zhanglinwei.zTools.configure.enums;

import org.jasypt.salt.ByteArrayFixedSaltGenerator;
import org.jasypt.salt.RandomSaltGenerator;
import org.jasypt.salt.SaltGenerator;
import org.jasypt.salt.StringFixedSaltGenerator;
import org.jasypt.salt.ZeroSaltGenerator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.zhanglinwei.zTools.common.constant.StringPool.EMPTY;

/**
 * Jasypt Salt 生成器选项，对应设置页「Salt Generator」。
 * <p>
 * Zero 适合需要可复现密文的场景；Random 每次加密盐不同，密文也会变。
 * {@link #BYTE_ARRAY_FIXED} / {@link #STRING_FIXED} 需要配合配置中的 Salt 值使用。
 */
public enum JasyptSalt {

    /** 固定零盐，同一明文多次加密结果相同 */
    ZERO("ZeroSaltGenerator", false),
    /** 随机盐，同一明文每次密文不同 */
    RANDOM("RandomSaltGenerator", false),
    /** 固定字节盐，值为十六进制或 UTF-8 文本 */
    BYTE_ARRAY_FIXED("ByteArrayFixedSaltGenerator", true),
    /** 固定字符串盐 */
    STRING_FIXED("StringFixedSaltGenerator", true),
    ;

    /** 持久化到配置里的类名简写 */
    private final String code;
    /** 是否需要用户填写固定盐值 */
    private final boolean requiresValue;

    /**
     * @param code          配置存储值
     * @param requiresValue 是否需要固定盐值
     */
    JasyptSalt(String code, boolean requiresValue) {
        this.code = code;
        this.requiresValue = requiresValue;
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
     * 是否需要在设置页填写固定盐。
     *
     * @return Fixed 类型为 {@code true}
     */
    public boolean requiresValue() {
        return requiresValue;
    }

    /**
     * 按配置创建盐生成器。Fixed 类型使用 {@code value}；其它类型忽略该参数。
     *
     * @param value 固定盐；{@link #BYTE_ARRAY_FIXED} 按 hex / UTF-8 解析
     * @return Jasypt 盐生成器
     */
    public SaltGenerator create(String value) {
        switch (this) {
            case RANDOM:
                return new RandomSaltGenerator();
            case BYTE_ARRAY_FIXED:
                return new ByteArrayFixedSaltGenerator(JasyptFixedBytes.parse(value));
            case STRING_FIXED:
                return new StringFixedSaltGenerator(value == null ? EMPTY : value);
            case ZERO:
            default:
                return new ZeroSaltGenerator();
        }
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
