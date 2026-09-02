package com.zhanglinwei.zTools.configure.enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public enum JasyptOutputType {

    BASE_64("base64"),
    HEXADECIMAL("hexadecimal"),
    ;

    private final String code;

    JasyptOutputType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static final List<String> OUTPUT_OPTIONS;

    static {
        List<String> outputTypeList = Arrays.stream(JasyptOutputType.values())
                .map(JasyptOutputType::getCode)
                .collect(Collectors.toList());
        OUTPUT_OPTIONS = Collections.unmodifiableList(outputTypeList);
    }

    public static JasyptOutputType codeOf(String code) {
        return Arrays.stream(JasyptOutputType.values())
                .filter(item -> item.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElse(JasyptOutputType.BASE_64);
    }

}
