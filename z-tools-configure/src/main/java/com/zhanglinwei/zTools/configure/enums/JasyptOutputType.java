package com.zhanglinwei.zTools.configure.enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Jasypt 密文输出编码，对应 {@link org.jasypt.encryption.pbe.StandardPBEStringEncryptor#setStringOutputType(String)}。
 */
public enum JasyptOutputType {

    /** Base64 编码密文，形如 {@code xK8a...} */
    BASE_64("base64"),
    /** 十六进制密文，形如 {@code 7a3f...} */
    HEXADECIMAL("hexadecimal"),
    ;

    /** 写入配置与传给 Jasypt 的编码名 */
    private final String code;

    /**
     * @param code Jasypt {@code stringOutputType} 取值
     */
    JasyptOutputType(String code) {
        this.code = code;
    }

    /**
     * 获取编码名。
     *
     * @return {@code base64} 或 {@code hexadecimal}
     */
    public String getCode() {
        return code;
    }

    /** 设置页下拉选项，顺序与枚举声明一致 */
    public static final List<String> OUTPUT_OPTIONS;

    static {
        List<String> outputTypeList = Arrays.stream(JasyptOutputType.values())
                .map(JasyptOutputType::getCode)
                .collect(Collectors.toList());
        OUTPUT_OPTIONS = Collections.unmodifiableList(outputTypeList);
    }

    /**
     * 按编码名解析，忽略大小写；未知值回退为 Base64。
     *
     * @param code 配置中保存的编码名
     * @return 对应枚举
     */
    public static JasyptOutputType codeOf(String code) {
        return Arrays.stream(JasyptOutputType.values())
                .filter(item -> item.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElse(JasyptOutputType.BASE_64);
    }

}
