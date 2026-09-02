package com.zhanglinwei.zTools.yapi.model;


import com.google.gson.annotations.SerializedName;
import com.zhanglinwei.zTools.common.util.StringUtils;

/**
 * YApi 通用响应体
 * <p>
 * YApi 不同接口返回格式不一致：
 * - /api/project/get 等接口返回 {"code":0, "message":"成功", "data":...}
 * - /api/interface/add 等接口返回 {"errcode":0, "errmsg":"成功", "data":...}
 * 此类同时兼容两种格式
 *
 * @param <T> 业务数据类型
 */
public class YApiResult<T> {

    /** 错误码（errcode 格式） */
    private int errcode;

    /** 错误信息（errmsg 格式） */
    private String errmsg;

    /** 错误码（code 格式，部分接口使用） */
    @SerializedName("code")
    private int code;

    /** 错误信息（message 格式，部分接口使用） */
    @SerializedName("message")
    private String message;

    /** 业务数据 */
    private T data;


    /**
     * 判断是否成功（兼容 errcode 和 code 两种格式）
     *
     * @return 两种错误码均为 0 则为 {@code true}
     */
    public boolean isSuccess() {
        return errcode == 0 && code == 0;
    }

    /**
     * 获取错误信息（兼容两种格式）
     *
     * @return errmsg 或 message；都空时返回“未知错误”
     */
    public String getErrorMsg() {
        if (StringUtils.isNotBlank(errmsg)) {
            return errmsg;
        }
        if (StringUtils.isNotBlank(message)) {
            return message;
        }
        return "未知错误";
    }

    /** errcode 错误码。 */
    public int getErrcode() {
        return errcode;
    }

    /** 设置 errcode。 */
    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }

    /** errmsg 错误信息。 */
    public String getErrmsg() {
        return errmsg;
    }

    /** 设置 errmsg。 */
    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    /** code 错误码。 */
    public int getCode() {
        return code;
    }

    /** 设置 code。 */
    public void setCode(int code) {
        this.code = code;
    }

    /** message 错误信息。 */
    public String getMessage() {
        return message;
    }

    /** 设置 message。 */
    public void setMessage(String message) {
        this.message = message;
    }

    /** 业务数据。 */
    public T getData() {
        return data;
    }

    /** 设置业务数据。 */
    public void setData(T data) {
        this.data = data;
    }
}
