package com.huawei.opensdk.demoservice.data.callBackWapper;

/**
 * This class is about video information wapper
 * 视频信息包装类
 */
public class VideoSwitchInfoWapper
{
    /**
     * status
     * 状态
     */
    private int status;

    /**
     * user ID
     * 用户id
     */
    private long userID;

    /**
     * device ID
     * 设备id
     */
    private long deviceID;

    public VideoSwitchInfoWapper(int status, long deviceID, long userID)
    {
        this.status = status;
        this.userID = userID;
        this.deviceID = deviceID;
    }

    public int getStatus()
    {
        return status;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

    public long getUserID()
    {
        return userID;
    }

    public void setUserID(long userID)
    {
        this.userID = userID;
    }

    public long getDeviceID()
    {
        return deviceID;
    }

    public void setDeviceID(long deviceID)
    {
        this.deviceID = deviceID;
    }
}
