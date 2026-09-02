package com.zhanglinwei.zTools.configure.constants;

/**
 * 插件配置相关常量：持久化文件名、Jasypt 默认算法与密文包裹格式。
 */
public class ZToolsConstant {

    /** 项目级持久化文件，位于 {@code .idea/zTools.xml} */
    public static final String STORAGE_FILE = "zTools.xml";

    /** Jasypt 默认 PBE 算法，与 Spring 常见配置一致 */
    public static final String DEFAULT_PBE_ALGORITHM = "PBEWITHMD5ANDDES";

    /**
     * 密文包裹模板，{@code %s} 为密文占位。
     * 例：明文 {@code hello} 加密后写入配置为 {@code ENC(<密文>)}。
     */
    public static final String ENC_WRAPPER = "ENC(%s)";

}
