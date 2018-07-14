package com.huawei.opensdk.loginmgr;

/**
 * This class is about login parameter settings
 * 登录入参类
 */
public class LoginParam {

    /**
     * Proxy server url
     * 代理服务器URL
     */
    private String proxyUrl;

    /**
     * Proxy server port
     * 代理服务器端口
     */
    private int proxyPort;

    /**
     * Server url
     * 服务器URL
     */
    private String serverUrl;

    /**
     * Server port
     * 服务器端口
     */
    private int serverPort;

    /**
     * user name
     * 用户名
     */
    private String userName;

    /**
     * password
     * 密码
     */
    private String password;

    /**
     *  VPN
     */
    private boolean isVPN = false;

    /**
     * SRTP mode
     */
    private int srtpMode = 0;

    /**
     * Sip signaling transport mode
     */
    private int sipTransportMode = 0;

    /**
     * server type
     */
    private int serverType;

    public LoginParam(){

    }

    /**
     * This method is used to constructor of this class
     * @param proxyUrl      :Proxy server url   代理服务器URL
     * @param proxyPort     :Proxy server port  代理服务器端口
     * @param serverUrl     :Server url 服务器URL
     * @param serverPort    :Server port    服务器端口
     * @param userName      :User name  用户名
     * @param password      :Password   密码
     */
    public LoginParam(String proxyUrl, int proxyPort, String serverUrl, int serverPort, String userName, String password) {
        this.proxyUrl = proxyUrl;
        this.proxyPort = proxyPort;
        this.serverUrl = serverUrl;
        this.serverPort = serverPort;
        this.userName = userName;
        this.password = password;
    }

    /**
     * This method is used to constructor of this class
     * @param serverUrl     :Server url 服务器URL
     * @param serverPort    :Server port    服务器端口
     * @param userName      :User name  用户名
     * @param password      :Password   密码
     */
    public LoginParam(String serverUrl, int serverPort, String userName, String password) {
        this.serverUrl = serverUrl;
        this.serverPort = serverPort;
        this.userName = userName;
        this.password = password;
    }

    public String getProxyUrl() {
        return proxyUrl;
    }

    public void setProxyUrl(String proxyUrl) {
        this.proxyUrl = proxyUrl;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isVPN() {
        return isVPN;
    }

    public void setVPN(boolean VPN) {
        isVPN = VPN;
    }

    public int getSrtpMode() {
        return srtpMode;
    }

    public void setSrtpMode(int srtpMode) {
        this.srtpMode = srtpMode;
    }

    public int getSipTransportMode() {
        return sipTransportMode;
    }

    public void setSipTransportMode(int sipTransportMode) {
        this.sipTransportMode = sipTransportMode;
    }

    public int getServerType() {
        return serverType;
    }

    public void setServerType(int serverType) {
        this.serverType = serverType;
    }

}
