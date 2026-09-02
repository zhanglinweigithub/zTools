package com.zhanglinwei.zTools.configure.enums;

import org.jasypt.iv.IvGenerator;
import org.jasypt.iv.NoIvGenerator;
import org.jasypt.iv.RandomIvGenerator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public enum JasyptIV {

    NO("NoIvGenerator", new NoIvGenerator()),
    RANDOM("RandomIvGenerator", new RandomIvGenerator()),
    ;

    private final String code;
    private final IvGenerator generator;

    JasyptIV(String code, IvGenerator generator) {
        this.code = code;
        this.generator = generator;
    }

    public String getCode() {
        return code;
    }

    public IvGenerator getGenerator() {
        return generator;
    }

    public static final List<String> IV_OPTIONS;

    static {
        List<String> ivList = Arrays.stream(JasyptIV.values())
                .map(JasyptIV::getCode)
                .collect(Collectors.toList());
        IV_OPTIONS = Collections.unmodifiableList(ivList);
    }

    public static JasyptIV codeOf(String code) {
        return Arrays.stream(JasyptIV.values())
                .filter(item -> item.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElse(JasyptIV.NO);
    }

}
