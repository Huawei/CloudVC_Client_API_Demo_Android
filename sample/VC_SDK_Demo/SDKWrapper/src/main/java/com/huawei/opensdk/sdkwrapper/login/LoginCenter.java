package com.huawei.opensdk.sdkwrapper.login;

import android.text.TextUtils;
import android.util.Log;

import com.huawei.opensdk.sdkwrapper.manager.TupMgr;
import com.huawei.tup.confctrl.ConfctrlConfEnvType;
import com.huawei.tup.login.LoginAuthorizeParam;
import com.huawei.tup.login.LoginAuthorizeResult;
import com.huawei.tup.login.LoginChangePwdParam;
import com.huawei.tup.login.LoginDetectServer;
import com.huawei.tup.login.LoginFirewallMode;
import com.huawei.tup.login.LoginProtocolType;
import com.huawei.tup.login.LoginServerType;
import com.huawei.tup.login.LoginSingleServerInfo;
import com.huawei.tup.login.LoginSmcAuthorizeResult;
import com.huawei.tup.login.LoginStgParam;

import java.util.ArrayList;
import java.util.List;

import common.FireWallMode;
import common.TupCallParam;
import object.TupCallCfgAccount;
import object.TupCallCfgMedia;
import object.TupCallCfgSIP;

/**
 * This class is about login configuration.
 * 登陆配置相关信息类
 */
public class LoginCenter {

    private static final String TAG = LoginCenter.class.getSimpleName();

    /**
     * mediaX net type
     * mediaX 组网
     */
    public static final int LOGIN_E_SERVER_TYPE_MEDIAX  = 1;

    /**
     * smc net type
     * SMC 组网
     */
    public static final int LOGIN_E_SERVER_TYPE_SMC  = 2;

    /**
     * Define a LoginCenter object
     * LoginCenter对象
     */
    private static LoginCenter instance;

    /**
     * Define a LoginStatus object
     * LoginStatus对象
     */
    private LoginStatus loginStatus;

    /**
     * Define a SipAccountInfo object
     * Sip账号信息对象
     */
    private SipAccountInfo sipAccountInfo;

    /**
     * Define a conference configuration information object
     * 会控配置信息对象
     */
    private ConfConfigInfo confConfigInfo;

    /**
     * Define a enterprise address book configuration information object
     * VC 企业通讯录配置信息对象
     */
    private ContactConfigInfo contactConfigInfo;

    /**
     * Define a STG Information object
     * STG隧道参数对象
     */
    private LoginStgParam stgParam;

    /**
     * Firewall type
     * 防火墙模式
     */
    private LoginFirewallMode firewallMode = LoginFirewallMode.LOGIN_E_FIREWALL_MODE_NULL;

    /**
     * account
     * 账号
     */
    private String account;

    /**
     * password
     * 密码
     */
    private String password;

    /**
     * Login server address
     * 服务器地址
     */
    private String loginServerAddress;

    /**
     * Login server port
     * 端口号
     */
    private int loginServerPort;

    /**
     * local IP address
     * 本地ip地址
     */
    private String localIPAddress;

    /**
     * Type of conference
     * 会议类型
     */
    private int deployMode;

    /**
     * Login server type
     */
    private int serverType = LOGIN_E_SERVER_TYPE_MEDIAX;

    /**
     * SRTP mode
     */
    private int srtpMode = 0;

    /**
     * Sip signaling transport mode
     */
    private int sipTransportMode = 0;

    /**
     * SMC组网下保存UI端口，用于sip注册
     */
    private int sipPort;

    /**
     * This is a constructor of LoginCenter class.
     * 构造方法
     */
    private LoginCenter() {
        this.loginStatus = new LoginStatus();
    }

    /**
     * This method is used to get instance object of LoginCenter.
     * 获取LoginCenter对象实例
     * @return LoginCenter Return instance object of LoginCenter
     *                     返回一个LoginCenter对象实例
     * @return LoginCenter
     */
    public synchronized static LoginCenter getInstance() {
        if (instance == null) {
            instance = new LoginCenter();
        }
        return instance;
    }

    /**
     * This method is used to login.
     * 鉴权登陆
     * @param loginParam Indicates authorize param
     *                   鉴权参数
     * @return int If success return 0, otherwise return corresponding error code
     *             成功返回0，失败返回相应错误码
     */
    public int login(LoginAuthorizeParam loginParam) {
        int ret;

        if (loginParam == null) {
            return -1;
        }

        this.account = loginParam.getAuthInfo().getUserName();
        this.password = loginParam.getAuthInfo().getPassword();
        this.loginServerAddress = loginParam.getAuthServer().getServerUrl();
        this.loginServerPort = loginParam.getAuthServer().getServerPort();
        ret = TupMgr.getInstance().getAuthManagerIns().authorize(loginParam);
        if (ret != 0) {
            Log.e(TAG, "login is failed" + ret);
        }

        return ret;
    }

    /**
     * This method is used to logout.
     * 账号注销
     * @return int If success return 0, otherwise return corresponding error code
     *             成功返回0，失败返回相应错误码
     */
    public int logout() {
        int ret = 0;

        /* Unregister Sip */
        if (TupMgr.getInstance().getFeatureMgr().isSupportAudioAndVideoCall() == true) {
            //SIP账号注销
            ret = this.sipUnReg();
            if (ret != 0) {
                Log.e(TAG, "Sip unregister failed, return " + ret);
            }
        }

        return ret;
    }

    /**
     * This method is used to change password
     * 修改密码
     *
     * @param newPassword       新密码
     * @param oldPassword       旧密码
     * @param account           登录账号
     */
    public void changePassword(String newPassword, String oldPassword, String account )
    {
        LoginChangePwdParam changePwdParam = new LoginChangePwdParam();
        changePwdParam.setNewPassword(newPassword);
        changePwdParam.setOldPassword(oldPassword);
        changePwdParam.setAccount(account);
        changePwdParam.setNumber(account);
        changePwdParam.setPort(443);
        changePwdParam.setServer(loginServerAddress);
        changePwdParam.setProtocol(LoginProtocolType.LOGIN_D_PROTOCOL_TYPE_SIP);
        changePwdParam.setServerType(LoginServerType.LOGIN_E_SERVER_TYPE_SMC);


        TupMgr.getInstance().getAuthManagerIns().changeRegisterPassword(changePwdParam);
    }

    /**
     * This method is used to register sip account.
     * VC-smc SIP 注册
     * @return int If success return 0, otherwise return corresponding error code
     *             成功返回0，失败返回相应错误码
     */
    public int sipReg()
    {
        int ret;

        TupCallCfgMedia tupCallCfgMedia = TupMgr.getInstance().getTupCallCfgMedia();
        tupCallCfgMedia.setMediaSrtpMode(this.getSrtpMode());
        TupMgr.getInstance().getCallManagerIns().setCfgMedia(tupCallCfgMedia);

        //设置sip注册参数
        TupCallCfgSIP tupCallCfgSIP = TupMgr.getInstance().getTupCallCfgSIP();
        tupCallCfgSIP.setServerRegPrimary(loginServerAddress, this.sipPort);
        tupCallCfgSIP.setServerProxyPrimary(loginServerAddress, this.sipPort);
        tupCallCfgSIP.setSipPort(this.sipPort);

        tupCallCfgSIP.setNetAddress(localIPAddress);
        tupCallCfgSIP.setSipTransMode(getSipTransportMode());
        TupMgr.getInstance().getCallManagerIns().setCfgSIP(tupCallCfgSIP);

        //注册sip账号
        SipAccountInfo sipAccountInfo = new SipAccountInfo();
        sipAccountInfo.setSipImpi(account);
        sipAccountInfo.setSipPassword(password);
        LoginCenter.getInstance().setSipAccountInfo(sipAccountInfo);
        ret = TupMgr.getInstance().getCallManagerIns().callRegister(sipAccountInfo.getSipImpi(), sipAccountInfo.getSipImpi(), sipAccountInfo.getSipPassword());

        if (ret != 0) {
            Log.e(TAG, "Sip register failed, return " + ret);
        }
        return ret;
    }

    /**
     * This method is used to register sip account.
     * VC-mediaX SIP账号注册
     * @param sipAccountInfo Indicates sip account info
     *                       sip账号的相关信息
     * @return int If success return 0, otherwise return corresponding error code
     *             成功返回0，失败返回相应错误码
     */
    public int sipReg(SipAccountInfo sipAccountInfo) {
        int ret;

        TupMgr.getInstance().getCallManagerIns().setTelNum(LoginCenter.getInstance().getSipAccountInfo().getSipNumber());

        TupCallCfgMedia tupCallCfgMedia = TupMgr.getInstance().getTupCallCfgMedia();
        tupCallCfgMedia.setMediaSrtpMode(this.getSrtpMode());
        TupMgr.getInstance().getCallManagerIns().setCfgMedia(tupCallCfgMedia);

        /* Set server address and local address info */
        TupCallCfgSIP tupCallCfgSIP = TupMgr.getInstance().getTupCallCfgSIP();

        if (this.getSipTransportMode() == TupCallParam.CALL_E_TRANSPORTMODE.CALL_E_TRANSPORTMODE_UDP)
        {
            tupCallCfgSIP.setServerRegPrimary(sipAccountInfo.getRegisterServerAddr(), sipAccountInfo.getRegisterServerPort());
            tupCallCfgSIP.setServerProxyPrimary(sipAccountInfo.getProxyServerAddr(), sipAccountInfo.getProxyServerPort());
            tupCallCfgSIP.setSipPort(sipAccountInfo.getLocalSIPPort());
        }
        else
        {
            if (5060 == sipAccountInfo.getRegisterServerPort())
            {
                tupCallCfgSIP.setServerRegPrimary(sipAccountInfo.getRegisterServerAddr(), sipAccountInfo.getRegisterServerPort() + 1);
                tupCallCfgSIP.setServerProxyPrimary(sipAccountInfo.getProxyServerAddr(), sipAccountInfo.getProxyServerPort() + 1);
                tupCallCfgSIP.setSipPort(sipAccountInfo.getLocalSIPPort() + 1);
            }
            else
            {
                //目前在tls传输模式下，sip注册的端口号选择5061，如果服务器配置端口号不是5061，可在此处进行相应修改
                tupCallCfgSIP.setServerRegPrimary(sipAccountInfo.getRegisterServerAddr(), 5061);
                tupCallCfgSIP.setServerProxyPrimary(sipAccountInfo.getProxyServerAddr(), 5061);
                tupCallCfgSIP.setSipPort(5061);
            }
        }

        tupCallCfgSIP.setSipTransMode(this.getSipTransportMode());
        tupCallCfgSIP.setNetAddress(sipAccountInfo.getLocalIpAddress());

        TupMgr.getInstance().getCallManagerIns().setCfgSIP(tupCallCfgSIP);

        /* Set sip password type */
        TupCallCfgAccount tupCallCfgAccount = new TupCallCfgAccount();
        tupCallCfgAccount.setauthPasswordType(sipAccountInfo.getSipAuthPasswordType());
        TupMgr.getInstance().getCallManagerIns().setCfgAccount(tupCallCfgAccount);

        /* Start register sip account */
        ret = TupMgr.getInstance().getCallManagerIns().callRegister(sipAccountInfo.getSipImpi(), sipAccountInfo.getSipImpi(), sipAccountInfo.getSipPassword());
        if (ret != 0) {
            Log.e(TAG, "Sip register failed, return " + ret);
        }

        return ret;
    }

    /**
     * This method is used to deregister sip account.
     * SIP账号注销
     *
     * @return int If success return 0, otherwise return corresponding error code
     *             成功返回0，失败返回相应错误码
     */
    public int sipUnReg() {
        return TupMgr.getInstance().getCallManagerIns().callDeregister();
    }

    /**
     * This method is used to set sip service firewall mode.
     * 设置防火墙模式
     *
     * @param mode Indicates the fire wall mode
     *             防火墙模式
     */
    public void setSipFirewallMode(LoginFirewallMode mode) {
        FireWallMode fireWallMode = FireWallMode.CALL_E_FIREWALL_MODE_LINE;
        switch (mode) {
            //直连模式
            case LOGIN_E_FIREWALL_MODE_NULL:
                fireWallMode = FireWallMode.CALL_E_FIREWALL_MODE_LINE;
                break;
            //内置SVN模式
            case LOGIN_E_FIREWALL_MODE_ONLY_HTTP:
                fireWallMode = FireWallMode.CALL_E_FIREWALL_MODE_INNERSVN;
                break;
            //启用STG模式
            case LOGIN_E_FIREWALL_MODE_HTTP_AND_SVN:
                fireWallMode = FireWallMode.CALL_E_FIREWALL_MODE_STG;
                break;

            default:
                break;
        }

        TupCallCfgSIP tupCallCfgSIP = TupMgr.getInstance().getTupCallCfgSIP();
        tupCallCfgSIP.setFireWallMode(fireWallMode);

        //设置SIP相关配置参数
        TupMgr.getInstance().getCallManagerIns().setCfgSIP(tupCallCfgSIP);
    }

    /**
     * This method is used to get the control configuration information.
     * 从鉴权登陆结果中获取会控的配置信息(mediaX)
     * @param authorizeResult             Indicates login authorize result
     *                                    鉴权登陆结果信息
     * @return ConfConfigInfo Return the control configuration information
     *                        返回会控的配置信息
     */
    public ConfConfigInfo getConfAccountInfoFromAuthResult(LoginAuthorizeResult authorizeResult)
    {
        ConfConfigInfo confConfigInfo = new ConfConfigInfo();

        //设置会议环境类型
        confConfigInfo.setConfEnvType(ConfctrlConfEnvType.CONFCTRL_E_CONF_ENV_HOSTED_VC);

        //设置会议服务器地址和端口号
        LoginSingleServerInfo loginSingleServerInfo = authorizeResult.getAuthSerinfo();
        if (null != loginSingleServerInfo)
        {
            confConfigInfo.setServerUri(loginSingleServerInfo.getServerUri());
            confConfigInfo.setServerPort(loginSingleServerInfo.getServerPort());
        }

        return confConfigInfo;
    }

    /**
     * This method is used to get the control configuration information.
     * 从鉴权登陆结果中获取会控的配置信息(SMC)
     * @param loginSmcAuthorizeResult             Indicates login authorize result
     *                                    鉴权登陆结果信息
     * @return ConfConfigInfo Return the control configuration information
     *                        返回会控的配置信息
     */
    public ConfConfigInfo getConfAccountInfoFromAuthResult(LoginSmcAuthorizeResult loginSmcAuthorizeResult)
    {
        ConfConfigInfo confConfigInfo = new ConfConfigInfo();

        //设置会议环境类型
        confConfigInfo.setConfEnvType(ConfctrlConfEnvType.CONFCTRL_E_CONF_ENV_ON_PREMISE_VC);

        //设置会议服务器地址和端口号
        if (null != loginSmcAuthorizeResult && null != loginSmcAuthorizeResult.getSmcServers())
        {
            confConfigInfo.setServerUri(loginSmcAuthorizeResult.getSmcServers().get(0).getServerUri());
            confConfigInfo.setServerPort(loginSmcAuthorizeResult.getSmcServers().get(0).getServerPort());
        }else {
            //设置会议服务器地址和端口号
            confConfigInfo.setServerUri(loginServerAddress);
            confConfigInfo.setServerPort(443);
        }
        return confConfigInfo;
    }

    /**
     * This method is used to get sip register account information.
     * 获取sip 注册时的账号信息(mediaX)
     * @param authorizeResult Indicates login authorize result
     *                        鉴权登陆结果信息
     * @return SipAccountInfo Return the sip register account information
     *                        返回SIP 注册时的账号信息
     */
    public SipAccountInfo getSipAccountInfoFromAuthResult(LoginAuthorizeResult authorizeResult)
    {
        String proxyAddress = authorizeResult.getSipInfo().getProxyAddress();
        String ip = proxyAddress.substring(0, proxyAddress.indexOf(':'));
        String port = proxyAddress.substring(proxyAddress.indexOf(':') + 1);
        String name = authorizeResult.getSipInfo().getAuthInfo().getUserName();
        String password = authorizeResult.getSipInfo().getAuthInfo().getPassword();
        String displayName = authorizeResult.getSipInfo().getDisplayName();

        SipAccountInfo loginParam = new SipAccountInfo();
        loginParam.setLocalIpAddress(LoginCenter.getInstance().getLocalIPAddress());
        loginParam.setLocalSIPPort(Integer.parseInt(port));

        loginParam.setProxyServerAddr(ip);
        loginParam.setProxyServerPort(Integer.valueOf(port).intValue());
        loginParam.setRegisterServerAddr(ip);
        loginParam.setRegisterServerPort(Integer.valueOf(port).intValue());

        loginParam.setSipName(name);
        loginParam.setSipImpi(displayName);
        loginParam.setSipPassword(password);

        //设置登陆界面上显示的号码
        loginParam.setTerminal(name);

        return loginParam;
    }

    /**
     * This method is used to build stg tunnel.
     * [cn]创建stg通道
     * @param stgParam Indicates stg server
     *                 stg服务器信息
     * @return
     */
    public int buildStgTunnel(LoginStgParam stgParam) {
        //待实现
        return 0;
    }

    /**
     * This method is used to refresh token.
     * 更新token凭证
     * @param token Indicates token
     *              token值
     */
    public void updateToken(String token) {

        if (TupMgr.getInstance().getFeatureMgr().isSupportAudioAndVideoConf()) {
            //do nothing
        }

        if (TupMgr.getInstance().getFeatureMgr().isSupportAudioAndVideoConf()) {
            TupMgr.getInstance().getConfManagerIns().setAuthToken(token);
        }
    }

    /**
     * This method is used to get firewall detect server.
     * 获取防火墙探测服务器
     * @param svnUriList Indicates the list of svn service
     *                   svn服务器地址列表
     * @return LoginDetectServer Return the firewall detect server information
     *                           返回防火墙探测服务器信息
     */
    private LoginDetectServer getFireDetectServer(List<String> svnUriList) {
        List<LoginSingleServerInfo> serverList = new ArrayList<>();

        if (svnUriList != null && !svnUriList.isEmpty()) {
            for (String svn : svnUriList) {
                if (TextUtils.isEmpty(svn)) {
                    continue;
                }

                if (svn.contains(":")) {
                    String[] svnAddress = svn.split(":");

                    if (svnAddress.length > 1) {
                        svn = svnAddress[0];
                    }
                }

                serverList.add(new LoginSingleServerInfo(svn, 0));
            }
        }

        if (serverList.size() > 0) {
            LoginDetectServer detectServer = new LoginDetectServer(serverList.size(), serverList);
            return detectServer;
        }

        return null;
    }

    public String getLocalIPAddress() {
        return localIPAddress;
    }

    public void setLocalIPAddress(String localIPAddress) {
        this.localIPAddress = localIPAddress;
    }

    public SipAccountInfo getSipAccountInfo() {
        return sipAccountInfo;
    }

    public void setSipAccountInfo(SipAccountInfo sipAccountInfo) {
        this.sipAccountInfo = sipAccountInfo;
    }

    public LoginStatus getLoginStatus() {
        return loginStatus;
    }

    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }

    public String getLoginServerAddress() {
        return loginServerAddress;
    }

    public int getLoginServerPort() {
        return loginServerPort;
    }

    public int getDeployMode() {
        return deployMode;
    }

    public void setDeployMode(int deployMode) {
        this.deployMode = deployMode;
    }

    public ConfConfigInfo getConfConfigInfo() {
        return confConfigInfo;
    }

    public void setConfConfigInfo(ConfConfigInfo confConfigInfo) {
        this.confConfigInfo = confConfigInfo;
    }

    public ContactConfigInfo getContactConfigInfo() {
        return contactConfigInfo;
    }

    public void setContactConfigInfo(ContactConfigInfo contactConfigInfo) {
        this.contactConfigInfo = contactConfigInfo;
    }

    public LoginFirewallMode getFirewallMode() {
        return firewallMode;
    }

    public void setFirewallMode(LoginFirewallMode firewallMode) {
        this.firewallMode = firewallMode;
    }


    public LoginStgParam getStgParam() {
        return stgParam;
    }

    public void setStgParam(LoginStgParam stgParam) {
        this.stgParam = stgParam;
    }

    public int getSrtpMode() {
        int srtpMode = TupCallParam.CALL_E_SRTP_MODE.CALL_E_SRTP_MODE_DISABLE;
        switch (this.srtpMode) {
            case 0:
                srtpMode = TupCallParam.CALL_E_SRTP_MODE.CALL_E_SRTP_MODE_DISABLE;
                break;
            case 1:
                srtpMode = TupCallParam.CALL_E_SRTP_MODE.CALL_E_SRTP_MODE_OPTION;
                break;
            case 2:
                srtpMode = TupCallParam.CALL_E_SRTP_MODE.CALL_E_SRTP_MODE_FORCE;
                break;
            default:
                break;
        }
        return srtpMode;
    }

    public void setSrtpMode(int mode) {
        this.srtpMode = mode;
    }

    public int getSipTransportMode() {
        int sipTransport = TupCallParam.CALL_E_TRANSPORTMODE.CALL_E_TRANSPORTMODE_UDP;
        switch (this.sipTransportMode) {
            case 0:
                sipTransport = TupCallParam.CALL_E_TRANSPORTMODE.CALL_E_TRANSPORTMODE_UDP;
                break;
            case 1:
                sipTransport = TupCallParam.CALL_E_TRANSPORTMODE.CALL_E_TRANSPORTMODE_TLS;
                break;
            case 2:
                sipTransport = TupCallParam.CALL_E_TRANSPORTMODE.CALL_E_TRANSPORTMODE_TCP;
                break;
            default:
                break;
        }
        return sipTransport;
    }

    public void setSipTransportMode(int sipTransportMode) {
        this.sipTransportMode = sipTransportMode;
    }

    public int getServerType() {
        int vcServerType = LOGIN_E_SERVER_TYPE_SMC;
        switch (this.serverType)
        {
            case 1:
                vcServerType = LOGIN_E_SERVER_TYPE_MEDIAX;
                break;
            case 2:
                vcServerType = LOGIN_E_SERVER_TYPE_SMC;
                break;
            default:
                break;
        }
        return vcServerType;
    }

    public void setServerType(int serverType) {
        this.serverType = serverType;
    }

    public void setSipPort(int sipPort) {
        this.sipPort = sipPort;
    }
}
