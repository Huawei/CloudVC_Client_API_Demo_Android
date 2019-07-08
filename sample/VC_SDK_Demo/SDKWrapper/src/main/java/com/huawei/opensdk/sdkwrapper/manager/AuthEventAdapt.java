package com.huawei.opensdk.sdkwrapper.manager;


import android.util.Log;

import com.huawei.opensdk.sdkwrapper.login.ConfConfigInfo;
import com.huawei.opensdk.sdkwrapper.login.ITupLoginCenterNotify;
import com.huawei.opensdk.sdkwrapper.login.LoginCenter;
import com.huawei.opensdk.sdkwrapper.login.LoginEvent;
import com.huawei.opensdk.sdkwrapper.login.LoginResult;
import com.huawei.opensdk.sdkwrapper.login.LoginStatus;
import com.huawei.opensdk.sdkwrapper.login.SipAccountInfo;
import com.huawei.tup.login.LoginAuthorizeResult;
import com.huawei.tup.login.LoginConfigQueryRes;
import com.huawei.tup.login.LoginFirewallMode;
import com.huawei.tup.login.LoginGetMediaXVersionResult;
import com.huawei.tup.login.LoginGetSiteInfoResult;
import com.huawei.tup.login.LoginIpAddrActiveResult;
import com.huawei.tup.login.LoginOnUc32UportalTokenRefresh;
import com.huawei.tup.login.LoginSmcAuthorizeResult;
import com.huawei.tup.login.LoginTempuserInfo;
import com.huawei.tup.login.LoginUportalAuthorizeResult;
import com.huawei.tup.login.sdk.TupLoginErrorID;
import com.huawei.tup.login.sdk.TupLoginNotify;
import com.huawei.tup.login.sdk.TupLoginOptResult;
import com.huawei.tupcontacts.TupContactsManager;

/**
 * This class is about login module callback
 * 登录模块回调类
 */
class AuthEventAdapt implements TupLoginNotify {

    private static final String TAG = AuthEventAdapt.class.getSimpleName();

    private static final String GENERAL_ERROR = "General error";
    private static final String PARAM_ERROR = "Param error";
    private static final String TIMEOUT_ERROR = "Timeout error";
    private static final String ALLOCATE_MEMORY_ERROR = "Allocate memory error";
    private static final String XML_PARSE_ERROR = "Xml parse error";
    private static final String PTKT_PARSE_ERROR = "Ptkt parse error";
    private static final String DNS_PARSE_ERROR = "Dns parse error";
    private static final String REQUEST_FAILED_ERROR = "Request failed error";
    private static final String AUTHORIZE_FAILED_ERROR = "Authorize failed error";
    private static final String SN_MATCH_ERROR = "Sn match error";
    private static final String SERVICE_ERROR = "Service error";
    private static final String ACCOUNT_LOCKED_ERROR = "Account locked error";
    private static final String CREATE_TIMER_FAILED_ERROR = "Create timer failed error";
    private static final String WRONG_SERVER_TYPE_ERROR = "Wrong server type error";
    private static final String WRONG_SERVER_VERSION_ERROR = "Wrong server version error";
    private static final String QUERY_SERVER_ADDRESS_FAILED_ERROR
            = "Query server address failed error";
    private static final String INVALID_URL_ERROR = "Invalid url error";
    private static final String START_TOKEN_REFRESH_FAILED_ERROR
            = "Start token refresh failed error";
    private static final String LOGIN_SUCCESS = "Login success";
    private static final String LOGIN_FAILED = "login failed";

    /**
     * Login module adapt callback
     * 登录模块adapt层回调
     */
    private ITupLoginCenterNotify loginStatusNotify;

    /**
     * Login module callback
     * 鉴权登录回调
     */
    private TupLoginNotify authNotify;

    /**
     * Uportal authorize result
     * Uportal鉴权登录结果
     */
    private LoginUportalAuthorizeResult uportalAuthorizeResult;

    /**
     * mediaX authorize result
     * mediaX鉴权登录结果
     */
    private LoginAuthorizeResult authorizeResult;

    /**
     * SMC authorize result
     * SMC鉴权登录结果
     */
    private LoginSmcAuthorizeResult smcAuthorizeResult;

    private boolean isForcedRefreshToken;


    public AuthEventAdapt() {
        loginStatusNotify = TupMgr.getInstance().getNotifyMgr().getLoginNotify();
        authNotify = TupMgr.getInstance().getNotifyMgr().getAuthNotify();
    }

    @Override
    public void onPasswordChangeResult(TupLoginOptResult tupLoginOptResult) {
        if (authNotify != null) {
            authNotify.onPasswordChangeResult(tupLoginOptResult);
        }
        if(null == tupLoginOptResult){
            return;
        }
        if (tupLoginOptResult.getOptResult() == 0){
            LoginCenter.getInstance().logout();
        }
    }

    @Override
    public void onGetLicenseTypeResult(TupLoginOptResult tupLoginOptResult, int i) {
        if (authNotify != null) {
            authNotify.onGetLicenseTypeResult(tupLoginOptResult, i);
        }

    }

    @Override
    public void onApplyLicenseResult(TupLoginOptResult tupLoginOptResult) {
        if (authNotify != null) {
            authNotify.onApplyLicenseResult(tupLoginOptResult);
        }

    }

    @Override
    public void onRefreshLicenseFailed(TupLoginOptResult tupLoginOptResult) {
        if (authNotify != null) {
            authNotify.onRefreshLicenseFailed(tupLoginOptResult);
        }


    }

    @Override
    public void onReleaseLicenseResult(TupLoginOptResult tupLoginOptResult) {
        if (authNotify != null) {
            authNotify.onReleaseLicenseResult(tupLoginOptResult);
        }

    }

    @Override
    public void onActiveResult(TupLoginOptResult tupLoginOptResult, LoginIpAddrActiveResult loginIpAddrActiveResult) {
        if (authNotify != null) {
            authNotify.onActiveResult(tupLoginOptResult, loginIpAddrActiveResult);
        }

    }

    @Override
    public void onConfigQueryResult(LoginConfigQueryRes loginConfigQueryRes) {
        if (authNotify != null) {
            authNotify.onConfigQueryResult(loginConfigQueryRes);
        }

    }

    @Override
    public void onSearchServerResult(TupLoginOptResult tupLoginOptResult) {
        if (authNotify != null) {
            authNotify.onSearchServerResult(tupLoginOptResult);
        }

    }

    @Override
    public void onAuthorizeResult(int i, TupLoginOptResult tupLoginOptResult, LoginAuthorizeResult loginAuthorizeResult) {
        if (authNotify != null) {
            authNotify.onAuthorizeResult(i, tupLoginOptResult, loginAuthorizeResult);
        }

        //mediaX下的登陆鉴权结果
        LoginCenter loginCenter = LoginCenter.getInstance();
        LoginFirewallMode firewallMode = LoginFirewallMode.LOGIN_E_FIREWALL_MODE_NULL;

        String errorText;
        int result = -1;
        LoginResult loginResult  = new LoginResult();
        if (tupLoginOptResult == null || loginAuthorizeResult == null)
        {
            if (null == tupLoginOptResult) {
                Log.e(TAG, "authorize Opt result is null");
                errorText = LOGIN_FAILED;
            } else {
                Log.e(TAG, "authorize result is null");
                result = tupLoginOptResult.getOptResult();
                errorText = getAuthorizeErrorText(result);
            }

            loginResult.setResult(1);
            loginResult.setReason(result);
            loginResult.setDescription(errorText);

            loginStatusNotify.onLoginEventNotify(LoginEvent.LOGIN_E_EVT_AUTH_FAILED, loginResult, null);
        }
        else if (tupLoginOptResult.getOptResult() != TupLoginErrorID.LOGIN_E_ERR_SUCCESS)
        {
            Log.e(TAG, "mediaX authorize failed.");
            this.authorizeResult = loginAuthorizeResult;

            result = tupLoginOptResult.getOptResult();
            errorText = getAuthorizeErrorText(result);

            loginResult.setResult(1);
            loginResult.setReason(result);
            loginResult.setDescription(errorText);

            loginStatusNotify.onLoginEventNotify(LoginEvent.LOGIN_E_EVT_AUTH_FAILED, loginResult, null);
        }
        else
        {
            this.authorizeResult = loginAuthorizeResult;

            //获取呼叫账号的配置信息
            if (TupMgr.getInstance().getFeatureMgr().isSupportAudioAndVideoCall()) {
                SipAccountInfo sipAccountInfo = loginCenter.getSipAccountInfoFromAuthResult(loginAuthorizeResult);
                loginCenter.setSipAccountInfo(sipAccountInfo);
            }

            //获取会议相关的配置信息
            if (TupMgr.getInstance().getFeatureMgr().isSupportAudioAndVideoConf()) {
                ConfConfigInfo confConfigInfo = loginCenter.getConfAccountInfoFromAuthResult(loginAuthorizeResult);
                loginCenter.setConfConfigInfo(confConfigInfo);
            }

            result = tupLoginOptResult.getOptResult();
            errorText = getAuthorizeErrorText(result);

            loginResult.setResult(0);
            loginResult.setReason(result);
            loginResult.setDescription(errorText);

            LoginStatus loginStatus = loginCenter.getLoginStatus();
            loginStatus.setAuthResult(this.authorizeResult);

            loginStatusNotify.onLoginEventNotify(LoginEvent.LOGIN_E_EVT_AUTH_SUCCESS, loginResult, loginStatus);

            loginAllService(firewallMode);
        }

        return;
    }

    @Override
    public void onAuthorizeResult(int i, TupLoginOptResult tupLoginOptResult, LoginUportalAuthorizeResult loginUportalAuthorizeResult) {
        Log.e(TAG, "authorize result notify.");
        if (authNotify != null) {
            authNotify.onAuthorizeResult(i, tupLoginOptResult, loginUportalAuthorizeResult);
        }

        return;
    }

    @Override
    public void onAuthorizeResult(int i, TupLoginOptResult tupLoginOptResult, LoginSmcAuthorizeResult loginSmcAuthorizeResult) {
        if (authNotify != null) {
            authNotify.onAuthorizeResult(i, tupLoginOptResult, loginSmcAuthorizeResult);
        }

        //SMC下的登陆鉴权结果
        LoginCenter loginCenter = LoginCenter.getInstance();
        LoginFirewallMode firewallMode = LoginFirewallMode.LOGIN_E_FIREWALL_MODE_NULL;

        //获取会议相关的配置信息
        if (TupMgr.getInstance().getFeatureMgr().isSupportAudioAndVideoConf()) {
            ConfConfigInfo confConfigInfo = loginCenter.getConfAccountInfoFromAuthResult(loginSmcAuthorizeResult);
            loginCenter.setConfConfigInfo(confConfigInfo);
        }
        loginAllService(firewallMode);

        if (tupLoginOptResult == null || loginSmcAuthorizeResult == null)
        {
            if (null == tupLoginOptResult) {
                Log.e(TAG, "authorize Opt result is null");
            } else {
                Log.e(TAG, "authorize result is null");
            }
        }
        else if (tupLoginOptResult.getOptResult() != TupLoginErrorID.LOGIN_E_ERR_SUCCESS)
        {
            Log.e(TAG, "SMC authorize failed.");
        }
        else
        {
            int result = tupLoginOptResult.getOptResult();
            String errorText = getAuthorizeErrorText(result);

            LoginResult loginResult  = new LoginResult();
            loginResult.setResult(0);
            loginResult.setReason(result);
            loginResult.setDescription(errorText);

            LoginStatus loginStatus = loginCenter.getLoginStatus();

            loginStatusNotify.onLoginEventNotify(LoginEvent.LOGIN_E_EVT_AUTH_SUCCESS, loginResult, loginStatus);

        }
        return;
    }

    @Override
    public void onRefreshTokenResult(TupLoginOptResult tupLoginOptResult, String s) {
        if (authNotify != null) {
            authNotify.onRefreshTokenResult(tupLoginOptResult, s);
        }

        if (null == tupLoginOptResult) {
            this.isForcedRefreshToken = true;
            TupMgr.getInstance().getAuthManagerIns().refreshToken();
        } else {
            int result = tupLoginOptResult.getOptResult();
            if (result != 0) {
                this.isForcedRefreshToken = true;
                TupMgr.getInstance().getAuthManagerIns().refreshToken();

            } else {
                this.isForcedRefreshToken = false;
                LoginCenter.getInstance().updateToken(s);
            }
        }
    }

    @Override
    public void onRefreshTokenResult(LoginOnUc32UportalTokenRefresh loginOnUc32UportalTokenRefresh) {
        if (authNotify != null) {
            authNotify.onRefreshTokenResult(loginOnUc32UportalTokenRefresh);
        }

        //loginStatusNotify.onLoginStatusNotify(0, null);;
        return;
    }

    @Override
    public void onGetNonceResult(TupLoginOptResult tupLoginOptResult, String s) {
        if (authNotify != null) {
            authNotify.onGetNonceResult(tupLoginOptResult, s);
        }

    }

    @Override
    public void onFirewallDetectResult(TupLoginOptResult tupLoginOptResult, int i) {
        if (authNotify != null) {
            authNotify.onFirewallDetectResult(tupLoginOptResult, i);
        }

        LoginStatus loginStatus = LoginCenter.getInstance().getLoginStatus();

        String errorText;
        int result = -1;
        LoginResult loginResult = new LoginResult();
        if (null == tupLoginOptResult) {
            loginResult.setResult(1);
            loginResult.setReason(result);
            loginResult.setDescription("Firewall detect failed.");

            loginStatusNotify.onLoginEventNotify(LoginEvent.LOGIN_E_EVT_FIREWALL_DETECT_FAILED, loginResult, loginStatus);
        } else {
            result = tupLoginOptResult.getOptResult();
            //errorText = getAuthorizeErrorText(result);
            if (result != 0) {
                loginResult.setResult(1);
                loginResult.setReason(result);
                loginResult.setDescription("Firewall detect failed.");

                loginStatusNotify.onLoginEventNotify(LoginEvent.LOGIN_E_EVT_FIREWALL_DETECT_FAILED, loginResult, loginStatus);
            } else {
                LoginFirewallMode firewallMode = LoginFirewallMode.values()[i];
                //记录当前防火墙模式
                LoginCenter.getInstance().setFirewallMode(firewallMode);

                //若
                if (firewallMode == LoginFirewallMode.LOGIN_E_FIREWALL_MODE_NULL) {
                    loginAllService(firewallMode);
                } else {
                    LoginCenter.getInstance().buildStgTunnel(LoginCenter.getInstance().getStgParam());
                }
            }
        }

    }

    @Override
    public void onStgTunnelBuildResult(TupLoginOptResult tupLoginOptResult) {
        if (authNotify != null) {
            authNotify.onStgTunnelBuildResult(tupLoginOptResult);
        }

        LoginStatus loginStatus = LoginCenter.getInstance().getLoginStatus();

        String errorText;
        int result = -1;
        LoginResult loginResult = new LoginResult();
        if (null == tupLoginOptResult) {
            loginResult.setResult(1);
            loginResult.setReason(result);
            loginResult.setDescription("Build stg tunnel failed.");

            loginStatusNotify.onLoginEventNotify(LoginEvent.LOGIN_E_EVT_BUILD_STG_TUNNEL_FAILED, loginResult, loginStatus);
        } else {
            result = tupLoginOptResult.getOptResult();
            if (result != 0) {
                loginResult.setResult(1);
                loginResult.setReason(result);
                loginResult.setDescription("Firewall detect failed.");

                loginStatusNotify.onLoginEventNotify(LoginEvent.LOGIN_E_EVT_FIREWALL_DETECT_FAILED, loginResult, loginStatus);
            } else {
                //TODO
                //根据STG隧道创建信息完成后继处理，待实现
            }
        }

    }

    @Override
    public void onStgDestoryTunnelResult(TupLoginOptResult tupLoginOptResult) {
        if (authNotify != null) {
            authNotify.onStgDestoryTunnelResult(tupLoginOptResult);
        }

    }

    @Override
    public void onGetSiteInfoResult(TupLoginOptResult tupLoginOptResult, LoginGetSiteInfoResult loginGetSiteInfoResult) {

    }

    @Override
    public void onGetMediaXVersionResult(TupLoginOptResult tupLoginOptResult, LoginGetMediaXVersionResult loginGetMediaXVersionResult) {

    }

    @Override
    public void onGetTempuserResult(TupLoginOptResult tupLoginOptResult, LoginTempuserInfo loginTempuserInfo) {

    }

    private int loginAllService(LoginFirewallMode mode) {
        LoginCenter loginCenter = LoginCenter.getInstance();
        int result = -1;
        LoginResult loginResult = new LoginResult();

        //如果支持通讯录，则开启本地联系人和通话记录服务
        if (TupMgr.getInstance().getFeatureMgr().isSupportContact())
        {
            Log.d(TAG, "start contact server.");
            TupContactsManager tupContactsManager = TupMgr.getInstance().getContactsManagerIns();
            String account = LoginCenter.getInstance().getAccount() + "@" + LoginCenter.getInstance().getLoginServerAddress();

            //启动本地联系人服务
            boolean startLocalResult = tupContactsManager.startLocalContactServer(account);
            //启动本地通话记录服务
            boolean startRecordResult = tupContactsManager.startCallRecordServer(account);

            if (startLocalResult && startRecordResult)
            {
                Log.i(TAG, "Address book service is started successfully");
            }
        }

        //如果支持音视频呼叫，则发起音视频呼叫的注册
        if (TupMgr.getInstance().getFeatureMgr().isSupportAudioAndVideoCall()) {
            Log.d(TAG, "reg sip account.");

            loginCenter.setSipFirewallMode(mode);
            SipAccountInfo sipAccountInfo = loginCenter.getSipAccountInfo();
            if (loginCenter.getServerType() == loginCenter.LOGIN_E_SERVER_TYPE_MEDIAX)
            {
                result = loginCenter.sipReg(sipAccountInfo);
            }
            else
            {
                result = loginCenter.sipReg();
            }

            if (result != 0) {
                Log.e(TAG, "sip account reg failed" + result);

                loginResult.setResult(1);
                loginResult.setReason(result);
                loginResult.setDescription("call sip reg failed.");

                LoginStatus loginStatus = loginCenter.getLoginStatus();

                if (loginCenter.getServerType() == loginCenter.LOGIN_E_SERVER_TYPE_MEDIAX)
                {
                    loginStatus.setAuthResult(this.authorizeResult);
                }

                loginStatusNotify.onLoginEventNotify(LoginEvent.LOGIN_E_EVT_VOIP_LOGIN_FAILED, loginResult, loginStatus);
            }
        }


        //如果支持音视频会议，则设置会议参数
        if (TupMgr.getInstance().getFeatureMgr().isSupportAudioAndVideoConf()) {
            Log.d(TAG, "set conference param.");

            ConfConfigInfo confInfo = LoginCenter.getInstance().getConfConfigInfo();

            //设置会议环境
            if (loginCenter.getServerType() == loginCenter.LOGIN_E_SERVER_TYPE_MEDIAX)
            {
                TupMgr.getInstance().getConfManagerIns().setConfType(confInfo.getConfEnvType());
                TupMgr.getInstance().getConfManagerIns().setConfServer(confInfo.getServerUri(), confInfo.getServerPort());
                TupMgr.getInstance().getConfManagerIns().setAuthToken(this.authorizeResult.getAuthToken());
            }
            else
            {
                TupMgr.getInstance().getConfManagerIns().setConfType(confInfo.getConfEnvType());
                TupMgr.getInstance().getConfManagerIns().setConfServer(confInfo.getServerUri(), confInfo.getServerPort());
                TupMgr.getInstance().getConfManagerIns().setAuthAccountInfo(LoginCenter.getInstance().getAccount(), LoginCenter.getInstance().getPassword());
            }

        }

        return result;

    }

    private String getAuthorizeErrorText(int result) {
        String str = result + "";
        String errorText = "";

        if (str.equals("")) {
            errorText = LOGIN_FAILED;
        }
        switch (result) {
            case TupLoginErrorID.LOGIN_E_ERR_SUCCESS:
                errorText = LOGIN_SUCCESS;
                break;
            case TupLoginErrorID.LOGIN_E_ERR_GENERAL:
                errorText = GENERAL_ERROR;
                break;
            case TupLoginErrorID.LOGIN_E_ERR_PARAM_ERROR:
                errorText = PARAM_ERROR;
                break;
            case TupLoginErrorID.LOGIN_E_ERR_TIMEOUT:
                errorText = TIMEOUT_ERROR;
                break;
            case TupLoginErrorID.LOGIN_E_ERR_MEM_ERROR:
                errorText = ALLOCATE_MEMORY_ERROR;
                break;
            case TupLoginErrorID.LOGIN_E_ERR_XML_ERROR:
                errorText = XML_PARSE_ERROR;
                break;
            case TupLoginErrorID.LOGIN_E_ERR_PARSE_PTKT_ERROR:
                errorText = PTKT_PARSE_ERROR;
                break;
            case TupLoginErrorID.LOGIN_E_ERR_DNS_ERROR:
                errorText = DNS_PARSE_ERROR;
                break;
            case TupLoginErrorID.LOGIN_E_ERR_REQUEST_FAILED:
                errorText = REQUEST_FAILED_ERROR;
                break;
            case TupLoginErrorID.LOGIN_E_ERR_AUTH_FAILED:
                errorText = AUTHORIZE_FAILED_ERROR;
                break;
            case TupLoginErrorID.LOGIN_E_ERR_SN_FAILED:
                errorText = SN_MATCH_ERROR;
                break;
            case TupLoginErrorID.LOGIN_E_ERR_SERVICE_ERROR:
                errorText = SERVICE_ERROR;
                break;
            case TupLoginErrorID.LOGIN_E_ERR_ACCOUNT_LOCKED:
                errorText = ACCOUNT_LOCKED_ERROR;
                break;
            case TupLoginErrorID.LOGIN_E_ERR_TIMER_ERROR:
                errorText = CREATE_TIMER_FAILED_ERROR;
                break;
            case TupLoginErrorID.LOGIN_E_ERR_WRONG_SERVERTYPE:
                errorText = WRONG_SERVER_TYPE_ERROR;
                break;
            case TupLoginErrorID.LOGIN_E_ERR_WRONG_SERVERVERSION:
                errorText = WRONG_SERVER_VERSION_ERROR;
                break;
            case TupLoginErrorID.LOGIN_E_ERR_SEARCH_SERVER_FAIL:
                errorText = QUERY_SERVER_ADDRESS_FAILED_ERROR;
                break;
            case TupLoginErrorID.LOGIN_E_ERR_INVALID_URL:
                errorText = INVALID_URL_ERROR;
                break;
            case TupLoginErrorID.LOGIN_E_ERR_START_REFRESH_FAIL:
                errorText = START_TOKEN_REFRESH_FAILED_ERROR;
                break;
            default:
                errorText = LOGIN_FAILED + ", reason:" + str;
                break;
        }
        return errorText;
    }
}
