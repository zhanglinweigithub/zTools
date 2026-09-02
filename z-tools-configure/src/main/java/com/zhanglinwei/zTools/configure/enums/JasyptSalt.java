package com.zhanglinwei.zTools.configure.enums;

import org.jasypt.salt.RandomSaltGenerator;
import org.jasypt.salt.SaltGenerator;
import org.jasypt.salt.ZeroSaltGenerator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public enum JasyptSalt {

    ZERO("ZeroSaltGenerator", new ZeroSaltGenerator()),
    RANDOM("RandomSaltGenerator", new RandomSaltGenerator()),
    ;

    private final String code;
    private final SaltGenerator generator;

    JasyptSalt(String code, SaltGenerator generator) {
        this.code = code;
        this.generator = generator;
    }

    public String getCode() {
        return code;
    }

    public SaltGenerator getGenerator() {
        return generator;
    }

    public static final List<String> SALT_OPTIONS;

    static {
        List<String> saltList = Arrays.stream(JasyptSalt.values())
                .map(JasyptSalt::getCode)
                .collect(Collectors.toList());
        SALT_OPTIONS = Collections.unmodifiableList(saltList);
    }

    public static JasyptSalt codeOf(String code) {
        return Arrays.stream(JasyptSalt.values())
                .filter(item -> item.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElse(JasyptSalt.ZERO);
    }

}
