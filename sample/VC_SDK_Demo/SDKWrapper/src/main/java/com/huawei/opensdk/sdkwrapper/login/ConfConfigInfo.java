package com.huawei.opensdk.sdkwrapper.login;

import com.huawei.tup.confctrl.ConfctrlConfEnvType;

import java.util.List;

/**
 * This class is about conference configuration information.
 * 会控配置相关参数类
 */
public class ConfConfigInfo
{
    /**
     * Conference environment type.
     * 会议环境类型类
     */
    private ConfctrlConfEnvType confEnvType;

    /**
     * Conference server uri : obtained by login authentication result
     * 会议服务器地址，从鉴权登陆结果中获取
     */
    private String serverUri;

    /**
     * Conference server port : obtained by login authentication result
     * 会议服务器端口号，从鉴权登陆结果中获取
     */
    private int serverPort;

    /**
     * MS meeting parameters Get server address
     * MS会议参数获取服务器地址
     */
    private String msParamUri;

    /**
     * MS meeting parameters get server path
     * MS会议参数获取服务器路径
     */
    private String msParamPathUri;

    /**
     * MS Meeting parameter Server list
     * MS会议参数服务器列表
     */
    private List<String> msUriList;

    public ConfctrlConfEnvType getConfEnvType() {
        return confEnvType;
    }

    public void setConfEnvType(ConfctrlConfEnvType confEnvType) {
        this.confEnvType = confEnvType;
    }

    public int getServerPort()
    {
        return serverPort;
    }

    public void setServerPort(int serverPort)
    {
        this.serverPort = serverPort;
    }

    public String getServerUri()
    {
        return serverUri;
    }

    public void setServerUri(String serverUri)
    {
        this.serverUri = serverUri;
    }

    public String getMsParamUri() {
        return msParamUri;
    }

    public void setMsParamUri(String msParamUri) {
        this.msParamUri = msParamUri;
    }

    public String getMsParamPathUri() {
        return msParamPathUri;
    }

    public void setMsParamPathUri(String msParamPathUri) {
        this.msParamPathUri = msParamPathUri;
    }

    public List<String> getMsUriList() {
        return msUriList;
    }

    public void setMsUriList(List<String> msUriList) {
        this.msUriList = msUriList;
    }

}
