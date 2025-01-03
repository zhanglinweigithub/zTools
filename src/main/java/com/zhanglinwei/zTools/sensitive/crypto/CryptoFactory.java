package com.zhanglinwei.zTools.sensitive.crypto;

import com.zhanglinwei.zTools.sensitive.constant.SensitiveDataConstant;

import java.util.HashMap;
import java.util.Map;

public class CryptoFactory {

    private final static Map<String, Crypto> cryptoMap;

    static {
        cryptoMap = new HashMap<>();
        cryptoMap.put(SensitiveDataConstant.SM4_CBC, new SM4_CBC());
        cryptoMap.put(SensitiveDataConstant.SM4_ECB, new SM4_ECB());
        cryptoMap.put(SensitiveDataConstant.AES, new AES());
    }

    public static Crypto getHandler(String algorithm) {
        return cryptoMap.get(algorithm);
    }
}
