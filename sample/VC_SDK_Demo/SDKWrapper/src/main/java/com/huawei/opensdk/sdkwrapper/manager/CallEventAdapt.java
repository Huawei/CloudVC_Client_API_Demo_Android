package com.huawei.opensdk.sdkwrapper.manager;


import android.util.Log;

import com.huawei.opensdk.sdkwrapper.login.ContactConfigInfo;
import com.huawei.opensdk.sdkwrapper.login.ITupLoginCenterNotify;
import com.huawei.opensdk.sdkwrapper.login.LoginCenter;
import com.huawei.opensdk.sdkwrapper.login.LoginEvent;
import com.huawei.opensdk.sdkwrapper.login.LoginResult;
import com.huawei.opensdk.sdkwrapper.login.LoginStatus;
import com.huawei.opensdk.sdkwrapper.login.SipAccountInfo;

import java.util.List;

import common.AuthType;
import common.DeviceStatus;
import common.TupCallNotify;
import common.TupCallParam;
import object.Conf;
import object.DecodeSuccessInfo;
import object.KickOutInfo;
import object.NetAddress;
import object.OnLineState;
import object.TupAudioQuality;
import object.TupAudioStatistic;
import object.TupCallLocalQos;
import object.TupCallQos;
import object.TupMsgWaitInfo;
import object.TupRegisterResult;
import object.TupServiceRightCfg;
import object.TupUnSupportConvene;
import object.TupVideoQuality;
import object.TupVideoStatistic;
import tupsdk.TupCall;

/**
 * This class is about call module callback
 * 呼叫模块回调类
 */
class CallEventAdapt implements TupCallNotify{

    private static final String TAG = CallEventAdapt.class.getSimpleName();

    /**
     * Call module callback
     * 呼叫回调
     */
    private TupCallNotify callNotify;

    /**
     * Login module adapt callback
     * 登录模块adapt层回调
     */
    private ITupLoginCenterNotify loginCenterNotify;
    private int currentVoipLoginStatus = TupCallParam.CALL_E_REG_STATE.CALL_E_REG_STATE_BUTT;

    public CallEventAdapt() {
        loginCenterNotify = TupMgr.getInstance().getNotifyMgr().getLoginNotify();
        callNotify = TupMgr.getInstance().getNotifyMgr().getCallNotify();
    }

    private void registerResultProc(TupRegisterResult tupRegisterResult) {
        if (null == tupRegisterResult)
        {
            Log.e(TAG, "tupRegisterResult is null");
            return;
        }

        LoginResult loginResult = new LoginResult();
        LoginStatus loginStatus = LoginCenter.getInstance().getLoginStatus();
        loginStatus.setCallResult(tupRegisterResult);

        int regState = tupRegisterResult.getRegState();
        int errorCode = tupRegisterResult.getReasonCode();

        loginResult.setResult(errorCode);
        loginResult.setReason(errorCode);

        switch (regState)
        {
            case TupCallParam.CALL_E_REG_STATE.CALL_E_REG_STATE_UNREGISTER:
                Log.i(TAG, "CALL_E_REG_STATE.CALL_E_REG_STATE_UNREGISTER");

                if (currentVoipLoginStatus == TupCallParam.CALL_E_REG_STATE.CALL_E_REG_STATE_REGISTERING) {
                    loginResult.setDescription("voip account login failed, error code:" + errorCode);
                    loginCenterNotify.onLoginEventNotify(LoginEvent.LOGIN_E_EVT_VOIP_LOGIN_FAILED, loginResult, loginStatus);
                }
                else if (currentVoipLoginStatus == TupCallParam.CALL_E_REG_STATE.CALL_E_REG_STATE_DEREGISTERING) {
                    loginResult.setDescription("voip account logout success");
                    loginCenterNotify.onLoginEventNotify(LoginEvent.LOGIN_E_EVT_VOIP_LOGOUT_SUCCESS, loginResult, loginStatus);
                }

                currentVoipLoginStatus = TupCallParam.CALL_E_REG_STATE.CALL_E_REG_STATE_UNREGISTER;
                break;

            case TupCallParam.CALL_E_REG_STATE.CALL_E_REG_STATE_REGISTERING:
                Log.i(TAG, "CALL_E_REG_STATE.CALL_E_REG_STATE_REGISTERING");

                currentVoipLoginStatus = TupCallParam.CALL_E_REG_STATE.CALL_E_REG_STATE_REGISTERING;
                break;

            case TupCallParam.CALL_E_REG_STATE.CALL_E_REG_STATE_DEREGISTERING:
                Log.i(TAG, "CALL_E_REG_STATE.CALL_E_REG_STATE_DEREGISTERING");

                currentVoipLoginStatus = TupCallParam.CALL_E_REG_STATE.CALL_E_REG_STATE_DEREGISTERING;
                break;

            case TupCallParam.CALL_E_REG_STATE.CALL_E_REG_STATE_REGISTERED:
                Log.i(TAG, "CALL_E_REG_STATE.CALL_E_REG_STATE_REGISTERED");

                //SMC组网下，注册成功后，从sip注册结果中获取sip号码（长号）
                if (LoginCenter.getInstance().getServerType() == LoginCenter.LOGIN_E_SERVER_TYPE_SMC)
                {
                    SipAccountInfo sipAccountInfo = LoginCenter.getInstance().getSipAccountInfo();
                    sipAccountInfo.setTerminal(tupRegisterResult.getTelNum());
                    LoginCenter.getInstance().setSipAccountInfo(sipAccountInfo);
                }

                loginResult.setDescription("voip account login success");
                loginCenterNotify.onLoginEventNotify(LoginEvent.LOGIN_E_EVT_VOIP_LOGIN_SUCCESS, loginResult, loginStatus);

                currentVoipLoginStatus = TupCallParam.CALL_E_REG_STATE.CALL_E_REG_STATE_REGISTERED;
                break;

            case TupCallParam.CALL_E_REG_STATE.CALL_E_REG_STATE_BUTT:
                Log.i(TAG, "CALL_E_REG_STATE.CALL_E_REG_STATE_BUTT");

                if (currentVoipLoginStatus == TupCallParam.CALL_E_REG_STATE.CALL_E_REG_STATE_DEREGISTERING) {
                    loginResult.setDescription("voip account logout success");
                    loginCenterNotify.onLoginEventNotify(LoginEvent.LOGIN_E_EVT_VOIP_LOGOUT_SUCCESS, loginResult, loginStatus);
                }

                currentVoipLoginStatus = TupCallParam.CALL_E_REG_STATE.CALL_E_REG_STATE_BUTT;
                break;

            default:
                break;
        }

    }


    @Override
    public void onCallComing(TupCall tupCall) {
        Log.i(TAG, "call coming");
        callNotify.onCallComing(tupCall);

    }

    @Override
    public void onRegisterResult(TupRegisterResult tupRegisterResult) {
        Log.i(TAG, "reg result");
        callNotify.onRegisterResult(tupRegisterResult);
        this.registerResultProc(tupRegisterResult);
    }

    @Override
    public void onCallStartResult(TupCall tupCall) {
        callNotify.onCallStartResult(tupCall);

    }

    @Override
    public void onCallGoing(TupCall tupCall) {
        callNotify.onCallGoing(tupCall);

    }

    @Override
    public void onCallRingBack(TupCall tupCall) {
        callNotify.onCallRingBack(tupCall);
    }

    @Override
    public void onBeKickedOut(KickOutInfo kickOutInfo) {
        callNotify.onBeKickedOut(kickOutInfo);

        if (null == kickOutInfo)
        {
            Log.e(TAG, "tupRegisterResult is null");
            return;
        }

        LoginResult loginResult = new LoginResult();
        LoginStatus loginStatus = LoginCenter.getInstance().getLoginStatus();

        loginResult.setResult(0);
        loginResult.setReason(0);
        loginResult.setDescription("The account is logged on at another terminal.");

        loginCenterNotify.onLoginEventNotify(LoginEvent.LOGIN_E_EVT_VOIP_FORCE_LOGOUT, loginResult, loginStatus);

    }

    @Override
    public void onCallConnected(TupCall tupCall) {
        callNotify.onCallConnected(tupCall);
    }

    @Override
    public void onCallEnded(TupCall tupCall) {
        callNotify.onCallEnded(tupCall);
    }

    @Override
    public void onCallDestroy(TupCall tupCall) {
        callNotify.onCallDestroy(tupCall);
    }

    @Override
    public void onCallRTPCreated(TupCall tupCall) {
        callNotify.onCallRTPCreated(tupCall);
    }

    @Override
    public void onCallAddVideo(TupCall tupCall) {
        callNotify.onCallAddVideo(tupCall);
    }

    @Override
    public void onCallDelVideo(TupCall tupCall) {
        callNotify.onCallDelVideo(tupCall);
    }

    @Override
    public void onCallViedoResult(TupCall tupCall) {
        callNotify.onCallViedoResult(tupCall);
    }

    @Override
    public void onCallRefreshView(TupCall tupCall) {
        callNotify.onCallRefreshView(tupCall);
    }

    @Override
    public void onMobileRouteChange(TupCall tupCall) {
        callNotify.onMobileRouteChange(tupCall);
    }

    @Override
    public void onAudioEndFile(int i) {
        callNotify.onAudioEndFile(i);
    }

    @Override
    public void onNetQualityChange(TupAudioQuality tupAudioQuality) {
        callNotify.onNetQualityChange(tupAudioQuality);
    }

    @Override
    public void onStatisticNetinfo(TupAudioStatistic tupAudioStatistic) {
        callNotify.onStatisticNetinfo(tupAudioStatistic);
    }

    @Override
    public void onStatisticMos(int i, int i1) {
        callNotify.onStatisticMos(i, i1);
    }

    @Override
    public void onNotifyQosinfo(TupCallQos tupCallQos) {
        callNotify.onNotifyQosinfo(tupCallQos);
    }

    @Override
    public void onNotifyLocalQosinfo(TupCallLocalQos tupCallLocalQos) {
        callNotify.onNotifyLocalQosinfo(tupCallLocalQos);
    }

    @Override
    public void onVideoOperation(TupCall tupCall) {
        callNotify.onVideoOperation(tupCall);
    }

    @Override
    public void onVideoStatisticNetinfo(TupVideoStatistic tupVideoStatistic) {
        callNotify.onVideoStatisticNetinfo(tupVideoStatistic);
    }

    @Override
    public void onVideoQuality(TupVideoQuality tupVideoQuality) {
        callNotify.onVideoQuality(tupVideoQuality);
    }

    @Override
    public void onVideoFramesizeChange(TupCall tupCall) {
        callNotify.onVideoFramesizeChange(tupCall);
    }

    @Override
    public void onSessionModified(TupCall tupCall) {
        callNotify.onSessionModified(tupCall);
    }

    @Override
    public void onSessionCodec(TupCall tupCall) {
        callNotify.onSessionCodec(tupCall);
    }

    @Override
    public void onCallHoldSuccess(TupCall tupCall) {
        callNotify.onCallHoldSuccess(tupCall);
    }

    @Override
    public void onCallHoldFailed(TupCall tupCall) {
        callNotify.onCallHoldFailed(tupCall);
    }

    @Override
    public void onCallUnHoldSuccess(TupCall tupCall) {
        callNotify.onCallUnHoldSuccess(tupCall);
    }

    @Override
    public void onCallUnHoldFailed(TupCall tupCall) {
        callNotify.onCallUnHoldFailed(tupCall);
    }

    @Override
    public void onCallBldTransferRecvSucRsp(TupCall tupCall) {
        callNotify.onCallBldTransferRecvSucRsp(tupCall);
    }

    @Override
    public void onCallBldTransferSuccess(TupCall tupCall) {
        callNotify.onCallBldTransferSuccess(tupCall);
    }

    @Override
    public void onCallBldTransferFailed(TupCall tupCall) {
        callNotify.onCallBldTransferFailed(tupCall);
    }

    @Override
    public void onCallAtdTransferSuccess(TupCall tupCall) {
        callNotify.onCallAtdTransferSuccess(tupCall);
    }

    @Override
    public void onCallAtdTransferFailed(TupCall tupCall) {
        callNotify.onCallAtdTransferFailed(tupCall);
    }

    @Override
    public void onSetIptServiceSuc(int i) {
        callNotify.onSetIptServiceSuc(i);
    }

    @Override
    public void onSetIptServiceFal(int i) {
        callNotify.onSetIptServiceFal(i);
    }

    @Override
    public void onSipaccountWmi(List<TupMsgWaitInfo> list) {
        callNotify.onSipaccountWmi(list);
    }

    @Override
    public void onServiceRightCfg(List<TupServiceRightCfg> list) {
        callNotify.onServiceRightCfg(list);
    }

    @Override
    public void onVoicemailSubSuc() {
        callNotify.onVoicemailSubSuc();
    }

    @Override
    public void onVoicemailSubFal() {
        callNotify.onVoicemailSubFal();
    }

    @Override
    public void onImsForwardResult(List<String> list) {
        callNotify.onImsForwardResult(list);
    }

    @Override
    public void onCallUpateRemoteinfo(TupCall tupCall) {
        callNotify.onCallUpateRemoteinfo(tupCall);
    }

    @Override
    public void onNotifyNetAddress(NetAddress netAddress) {
        callNotify.onNotifyNetAddress(netAddress);
        ContactConfigInfo contactConfigInfo = new ContactConfigInfo();
        contactConfigInfo.setBaseDN(netAddress.getDNValue());
        contactConfigInfo.setServerAddr(netAddress.getAddress());
        contactConfigInfo.setUserName(netAddress.getUserName());
        contactConfigInfo.setPassword(netAddress.getPassword());
        contactConfigInfo.setEuaType(netAddress.getEuaType());
        LoginCenter.getInstance().setContactConfigInfo(contactConfigInfo);
    }

    @Override
    public void onDataReady(int i, int i1) {
        callNotify.onDataReady(i, i1);
    }

    @Override
    public void onBFCPReinited(int i) {
        callNotify.onBFCPReinited(i);
    }

    @Override
    public void onDataSending(int i) {
        callNotify.onDataSending(i);
    }

    @Override
    public void onDataReceiving(int i) {
        callNotify.onDataReceiving(i);
    }

    @Override
    public void onDataStopped(int i) {
        callNotify.onDataStopped(i);
    }

    @Override
    public void onDataStartErr(int i, int i1) {
        callNotify.onDataStartErr(i, i1);
    }

    @Override
    public void onLineStateNotify(OnLineState onLineState) {
        callNotify.onLineStateNotify(onLineState);
    }

    @Override
    public void onDataFramesizeChange(TupCall tupCall) {
        callNotify.onDataFramesizeChange(tupCall);
    }

    @Override
    public void onDecodeSuccess(DecodeSuccessInfo decodeSuccessInfo) {
        callNotify.onDecodeSuccess(decodeSuccessInfo);
    }

    @Override
    public void onOnLineStateResult(int i, int i1) {
        callNotify.onOnLineStateResult(i, i1);
    }

    @Override
    public void onOnSRTPStateChange(int i, int i1) {
        callNotify.onOnSRTPStateChange(i, i1);
    }

    @Override
    public void onPasswordChangedResult(int i) {
        callNotify.onPasswordChangedResult(i);
    }

    @Override
    public void onGetLicenseTypeResult(int i, int i1) {
        callNotify.onGetLicenseTypeResult(i, i1);
    }

    @Override
    public void onApplyLicenseResult(int i) {
        callNotify.onApplyLicenseResult(i);
    }

    @Override
    public void onRefreshLicenseFailed() {
        callNotify.onRefreshLicenseFailed();
    }

    @Override
    public void onReleaseLicenseResult(int i) {
        callNotify.onReleaseLicenseResult(i);
    }

    @Override
    public void onIdoOverBFCPSupport(int i, int i1) {
        callNotify.onIdoOverBFCPSupport(i, i1);
    }

    @Override
    public void onDeviceStatusNotify(DeviceStatus deviceStatus) {
        callNotify.onDeviceStatusNotify(deviceStatus);
    }

    @Override
    public void onAuthorizeTypeNotify(int i, AuthType authType) {
        callNotify.onAuthorizeTypeNotify(i, authType);
    }

    @Override
    public void onReferNotify(int i) {
        callNotify.onReferNotify(i);
    }

    @Override
    public void onCallDialoginfo(int i, String s, String s1, String s2) {
        callNotify.onCallDialoginfo(i, s, s1, s2);
    }

    @Override
    public void onConfNotify(int i, Conf conf) {
        callNotify.onConfNotify(i, conf);
    }

    @Override
    public void onCtdInfo(TupCall tupCall) {
        callNotify.onCtdInfo(tupCall);
    }

    @Override
    public void onBeTransferToPresenceConf(TupCall tupCall) {
        callNotify.onBeTransferToPresenceConf(tupCall);
    }

    @Override
    public void onUnSupportConvene(TupUnSupportConvene tupUnSupportConvene) {
        callNotify.onUnSupportConvene(tupUnSupportConvene);
    }

    @Override
    public void onNotifyLogOut() {
        callNotify.onNotifyLogOut();
    }

    @Override
    public void onNoStream(int i, int i1) {
        callNotify.onNoStream(i, i1);
    }

    @Override
    public void onAudioHowlStatus(int i, int i1) {

    }
}
