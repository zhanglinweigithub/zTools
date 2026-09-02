package com.zhanglinwei.zTools.yapi.enums;

/**
 * YApi Form 参数类型。
 */
public enum ParameterType {

    /** 文件上传控件 */
    FILE("file"),
    /** 普通文本 */
    TEXT("text"),
    ;

    /** YApi 接口中的 type 取值 */
    private final String code;

    /**
     * @param code YApi 侧类型码
     */
    ParameterType(String code) {
        this.code = code;
    }

    /**
     * YApi Form 参数类型码。
     *
     * @return {@code file} 或 {@code text}
     */
    public String getCode() {
        return code;
    }
}
