package com.zhanglinwei.zTools.configure.enums;

import org.jasypt.iv.ByteArrayFixedIvGenerator;
import org.jasypt.iv.IvGenerator;
import org.jasypt.iv.NoIvGenerator;
import org.jasypt.iv.RandomIvGenerator;
import org.jasypt.iv.StringFixedIvGenerator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.zhanglinwei.zTools.common.constant.StringPool.EMPTY;

/**
 * Jasypt IV 生成器选项，对应设置页「IV Generator」。
 * <p>
 * 部分 PBE 算法（如 {@code PBEWITHMD5ANDDES}）不需要 IV，应选 {@link #NO}。
 * {@link #BYTE_ARRAY_FIXED} / {@link #STRING_FIXED} 需要配合配置中的 IV 值使用。
 */
public enum JasyptIV {

    /** 不生成 IV，适配不含 IV 的算法 */
    NO("NoIvGenerator", false),
    /** 随机 IV，适配需要 IV 的算法 */
    RANDOM("RandomIvGenerator", false),
    /** 固定字节 IV，值为十六进制或 UTF-8 文本 */
    BYTE_ARRAY_FIXED("ByteArrayFixedIvGenerator", true),
    /** 固定字符串 IV */
    STRING_FIXED("StringFixedIvGenerator", true),
    ;

    /** 持久化到配置里的类名简写 */
    private final String code;
    /** 是否需要用户填写固定 IV 值 */
    private final boolean requiresValue;

    /**
     * @param code          配置存储值
     * @param requiresValue 是否需要固定 IV 值
     */
    JasyptIV(String code, boolean requiresValue) {
        this.code = code;
        this.requiresValue = requiresValue;
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
     * 是否需要在设置页填写固定 IV。
     *
     * @return Fixed 类型为 {@code true}
     */
    public boolean requiresValue() {
        return requiresValue;
    }

    /**
     * 按配置创建 IV 生成器。Fixed 类型使用 {@code value}；其它类型忽略该参数。
     *
     * @param value 固定 IV；{@link #BYTE_ARRAY_FIXED} 按 hex / UTF-8 解析
     * @return Jasypt IV 生成器
     */
    public IvGenerator create(String value) {
        switch (this) {
            case RANDOM:
                return new RandomIvGenerator();
            case BYTE_ARRAY_FIXED:
                return new ByteArrayFixedIvGenerator(JasyptFixedBytes.parse(value));
            case STRING_FIXED:
                return new StringFixedIvGenerator(value == null ? EMPTY : value);
            case NO:
            default:
                return new NoIvGenerator();
        }
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
