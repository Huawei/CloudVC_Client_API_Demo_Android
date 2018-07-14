package com.huawei.opensdk.callmgr;


/**
 * Call module and UI callback.
 */
public interface ICallNotification
{
//    void onRegisterResult(int status, int errorCode);
//
//    void onKickedOut(int status, int tupSuccess);

    void onCallEventNotify(CallConstant.CallEvent event, Object params);
}
