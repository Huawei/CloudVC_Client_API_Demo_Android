package com.huawei.opensdk.sdkwrapper.login;

/**
 * This class is about login operation result.
 * 登陆操作结果类
 */
public class LoginResult {

    /**
     * login operation result
     * 登陆操作结果
     */
    private int result;

    /**
     * login error type
     * 登陆错误类型
     */
    private int reason;

    /**
     * the result description
     * 登陆结果描述
     */
    private String description;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public int getReason() {
        return reason;
    }

    public void setReason(int reason) {
        this.reason = reason;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
