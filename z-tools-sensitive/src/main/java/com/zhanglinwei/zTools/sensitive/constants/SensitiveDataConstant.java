package com.zhanglinwei.zTools.sensitive.constants;

public interface SensitiveDataConstant {
    // ---- 算法 ----
    String PBE_WITH_MD5_AND_DES = "PBEWithMD5AndDES";
    String PBE_WITH_MD5_AND_TRIPLE_DES = "PBEWithMD5AndTripleDES";
    String PBE_WITH_SHA1_AND_DESEDE = "PBEWithSHA1AndDESEDE";
    String PBE_WITH_HMAC_SHA512_AND_AES_256 = "PBEWithHMACSHA512AndAES_256";

    // ---- 输出类型 ----
    String OUTPUT_TYPE_BASE64 = "base64";
    String OUTPUT_TYPE_HEXADECIMAL = "hexadecimal";

    // ---- Salt Generator ----
    String SALT_RANDOM = "RandomSaltGenerator";
    String SALT_ZERO = "ZeroSaltGenerator";

    // ---- IV Generator ----
    String IV_NO = "NoIvGenerator";
    String IV_RANDOM = "RandomIvGenerator";

    // ---- Jasypt 密文格式 ----
    String ENC_WRAPPER = "ENC(%s)";
}
