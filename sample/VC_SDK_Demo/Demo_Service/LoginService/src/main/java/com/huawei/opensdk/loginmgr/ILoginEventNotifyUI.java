package com.huawei.opensdk.loginmgr;


/**
 * Login module and UI callback.
 * 登录模块UI回调
 */
public interface ILoginEventNotifyUI {
    void onLoginEventNotify(LoginConstant.LoginUIEvent evt, int reason, String description);
}
