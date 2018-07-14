package com.huawei.opensdk.sdkwrapper.login;

/**
 * This interface is about login event notify.
 * 登录事件回调接口
 */
public interface ITupLoginCenterNotify {

    /**
     * This method is used to describe authorize login result.
     * 鉴权登陆结果通知
     * @param evt    Indicates login event
     *               登陆成功或者失败的事件
     * @param result Indicates login operation result
     *               登陆操作结果
     * @param status Indicates landing result information under different network
     *               不同组网下的登陆结果信息
     */
    void onLoginEventNotify(LoginEvent evt, LoginResult result, LoginStatus status);
}
