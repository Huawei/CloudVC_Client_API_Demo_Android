package com.huawei.opensdk.sdkwrapper.login;

import common.EUAType;

/**
 * This class is about enterprise address book configuration information.
 * 企业通讯录配置信息类
 */
public class ContactConfigInfo {

    /**
     * Server address, multiple addresses use ";" separated
     * 地址，多个地址使用“;”隔开
     */
    private String serverAddr;

    /**
     * LDAP server address
     * ldap服务器地址
     */
    private String baseDN;

    /**
     * FTP user name
     * FTP用户名
     */
    private String userName;

    /**
     * password
     * 密码
     */
    private String password;

    /**
     * Server type
     * 服务器类型
     */
    private EUAType euaType;

    public String getServerAddr() {
        return serverAddr;
    }

    public void setServerAddr(String serverAddr) {
        this.serverAddr = serverAddr;
    }

    public String getBaseDN() {
        return baseDN;
    }

    public void setBaseDN(String baseDN) {
        this.baseDN = baseDN;
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

    public EUAType getEuaType() {
        return euaType;
    }

    public void setEuaType(EUAType euaType) {
        this.euaType = euaType;
    }

}
