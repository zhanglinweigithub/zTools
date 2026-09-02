package com.zhanglinwei.zTools.yapi.enums;

/**
 * YApi 请求体类型。
 */
public enum ReqBodyType {

    /** JSON 请求体，对应 {@code req_body_other} */
    JSON("json"),
    /** 表单请求体，对应 {@code req_body_form} */
    FORM("form"),
    ;

    /** YApi 接口中的 type 取值 */
    private final String code;

    /**
     * @param code YApi 侧类型码
     */
    ReqBodyType(String code) {
        this.code = code;
    }

    /**
     * YApi 请求体类型码。
     *
     * @return {@code json} 或 {@code form}
     */
    public String getCode() {
        return code;
    }
}
