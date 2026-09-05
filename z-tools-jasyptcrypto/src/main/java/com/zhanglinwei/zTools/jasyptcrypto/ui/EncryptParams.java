package com.zhanglinwei.zTools.jasyptcrypto.ui;

/**
 * 加密时用户选定（或唯一）的密码、固定盐、固定 IV。
 */
public class EncryptParams {

    private final String password;
    private final String saltValue;
    private final String ivValue;

    /**
     * @param password  本次加密密码
     * @param saltValue 本次固定盐；非 Fixed 可为空串
     * @param ivValue   本次固定 IV；非 Fixed 可为空串
     */
    public EncryptParams(String password, String saltValue, String ivValue) {
        this.password = password;
        this.saltValue = saltValue;
        this.ivValue = ivValue;
    }

    /**
     * 获取本次加密密码。
     *
     * @return 密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 获取本次固定盐。
     *
     * @return 盐值，可能为空串
     */
    public String getSaltValue() {
        return saltValue;
    }

    /**
     * 获取本次固定 IV。
     *
     * @return IV 值，可能为空串
     */
    public String getIvValue() {
        return ivValue;
    }

}
