package com.huawei.opensdk.demoservice;


/**
 * This class is about query join data conference param info.
 * 查询数据会议参数
 */
public class QueryJoinDataConfParamInfo {

    /**
     * 呼叫id，VC组网下 accessConf时 传入的是呼叫id
     * 因此和会议回调报上来的会议id不一致，
     * 不能用会议id来判断是否是同一个会议。
     */
    private int callId;
    /**
     * 会议id
     */
    private String confId;

    /**
     * 会议接入码
     */
    private String passCode;

    /**
     * 会议URL
     */
    private String confUrl;

    /**
     * 随机码
     */
    private String random;

    /**
     * 是否是数据会议，如果是数据会议需要获取大参数
     */
    private boolean isGetDataParam = false;

    public String getConfId() {
        return confId;
    }

    public void setConfId(String confId) {
        this.confId = confId;
    }

    public String getPassCode() {
        return passCode;
    }

    public void setPassCode(String passCode) {
        this.passCode = passCode;
    }

    public String getConfUrl() {
        return confUrl;
    }

    public void setConfUrl(String confUrl) {
        this.confUrl = confUrl;
    }

    public String getRandom() {
        return random;
    }

    public void setRandom(String random) {
        this.random = random;
    }

    public int getCallId() {
        return callId;
    }

    public void setCallId(int callId) {
        this.callId = callId;
    }

    public boolean isGetDataParam() {
        return isGetDataParam;
    }

    public void setGetDataParam(boolean getDataParam) {
        isGetDataParam = getDataParam;
    }
}
