package com.huawei.opensdk.sdkwrapper.login;

/**
 * This class is about sip account registration parameter class.
 * SIP账号注册参数类
 */
public class SipAccountInfo {

    /**
     * Local ip address
     * 本地ip地址
     */
    private String localIpAddress = "127.0.0.1";

    /**
     * Local SIP port
     * sip端口号
     */
    private int localSIPPort = 5060;

    /**
     * Proxy server address
     * 代理服务器地址
     */
    private String proxyServerAddr;

    /**
     * Proxy server port
     * 代理服务器端口号
     */
    private int proxyServerPort = 0;

    /**
     * Register server address
     * 注册服务器地址
     */
    private String registerServerAddr;

    /**
     * Register server port
     * 注册服务器端口号
     */
    private int registerServerPort = 0;

    /**
     * Sip name
     * SIP帐号
     */
    private String sipName;

    /**
     * sip ip multi
     * SIP IP多媒体私有标识
     */
    private String sipImpi;

    /**
     * Sip number
     * SIP号码
     */
    private String sipNumber = "";

    /**
     * Sip password
     * SIP注册密码
     */
    private String sipPassword;

    /**
     * domain
     * SIP域名
     */
    private String domain = "";

    /**
     * terminal
     * 软终端号码
     */
    private String terminal = "";

    /**
     * Sip password type
     * SIP密码类型
     */
    private int sipAuthPasswordType;


    public int getLocalSIPPort() {
        return localSIPPort;
    }

    public void setLocalSIPPort(int localSIPPort) {
        this.localSIPPort = localSIPPort;
    }

    public String getProxyServerAddr() {
        return proxyServerAddr;
    }

    public void setProxyServerAddr(String proxyServerAddr) {
        this.proxyServerAddr = proxyServerAddr;
    }

    public String getRegisterServerAddr() {
        return registerServerAddr;
    }

    public void setRegisterServerAddr(String registerServerAddr) {
        this.registerServerAddr = registerServerAddr;
    }

    public int getProxyServerPort() {
        return proxyServerPort;
    }

    public void setProxyServerPort(int proxyServerPort) {
        this.proxyServerPort = proxyServerPort;
    }

    public int getRegisterServerPort() {
        return registerServerPort;
    }

    public void setRegisterServerPort(int registerServerPort) {
        this.registerServerPort = registerServerPort;
    }

    public String getSipName() {
        if ((this.sipName == null) || (this.sipName.equals("")))
        {
            this.sipName = this.sipNumber;
        }
        return this.sipName;
    }

    public void setSipName(String sipName) {
        this.sipName = sipName;
    }

    public String getSipImpi() {
        return sipImpi;
    }

    public void setSipImpi(String sipImpi) {
        this.sipImpi = sipImpi;
    }

    public String getSipNumber() {
        return sipNumber;
    }

    public void setSipNumber(String sipNumber) {
        this.sipNumber = sipNumber;
    }

    public String getSipPassword() {
        return sipPassword;
    }

    public void setSipPassword(String sipPassword) {
        this.sipPassword = sipPassword;
    }


    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getLocalIpAddress() {
        return localIpAddress;
    }

    public void setLocalIpAddress(String localIpAddress) {
        this.localIpAddress = localIpAddress;
    }

    public int getSipAuthPasswordType() {
        return sipAuthPasswordType;
    }

    public void setSipAuthPasswordType(int sipAuthPasswordType) {
        this.sipAuthPasswordType = sipAuthPasswordType;
    }

    public String getTerminal()
    {
        if ((this.terminal == null) || (this.terminal.equals("")))
        {
            return this.sipNumber;
        }
        return terminal;
    }

    public void setTerminal(String terminal)
    {
        this.terminal = terminal;
    }

}
