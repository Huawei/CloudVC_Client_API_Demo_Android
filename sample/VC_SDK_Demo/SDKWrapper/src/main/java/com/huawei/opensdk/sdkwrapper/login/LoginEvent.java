package com.huawei.opensdk.sdkwrapper.login;

/**
 * This is a enum which is about login result event.
 * 鉴权登陆结果枚举
 */
public enum LoginEvent {

    //账号鉴权成功
    LOGIN_E_EVT_AUTH_SUCCESS(1),

    //账号鉴权失败
    LOGIN_E_EVT_AUTH_FAILED(2),

    //VOIP 登录成功
    LOGIN_E_EVT_VOIP_LOGIN_SUCCESS(3),

    //VOIP 登录失败
    LOGIN_E_EVT_VOIP_LOGIN_FAILED(4),

    //VOIP 登出成功
    LOGIN_E_EVT_VOIP_LOGOUT_SUCCESS(5),

    //VOIP 登出失败
    LOGIN_E_EVT_VOIP_LOGOUT_FAILED(6),

    //VOIP 强制退出(账号被踢)
    LOGIN_E_EVT_VOIP_FORCE_LOGOUT(7),

    //IM 登录成功(暂不支持)
    LOGIN_E_EVT_IM_LOGIN_SUCCESS(8),

    //IM 登录失败(暂不支持)
    LOGIN_E_EVT_IM_LOGIN_FAILED(9),

    //IM 登出成功(暂不支持)
    LOGIN_E_EVT_IM_LOGOUT_SUCCESS(10),

    //IM 登出失败(暂不支持)
    LOGIN_E_EVT_IM_LOGOUT_FAILED(11),

    //IM 强制退出(账号被踢)(暂不支持)
    LOGIN_E_EVT_IM_FORCE_LOGOUT(12),

    LOGIN_E_EVT_FIREWALL_DETECT_FAILED(13),

    LOGIN_E_EVT_BUILD_STG_TUNNEL_FAILED(14),

    LOGIN_E_EVT_BUILD_REFRESH_TOKEN_FAILED(15);

    private int index;

    LoginEvent(int i) {index = i;}

    public int getIndex() {return index;}
    public String toString()
    {
        return String.valueOf(index);
    }

}
