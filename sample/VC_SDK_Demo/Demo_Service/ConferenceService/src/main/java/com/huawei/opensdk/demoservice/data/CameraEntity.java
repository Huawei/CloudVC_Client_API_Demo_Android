package com.huawei.opensdk.demoservice.data;

import java.io.Serializable;

/**
 * This class is about camera
 * 摄像头实体类
 */
public class CameraEntity implements Serializable
{
    /**
     * camera status
     * 摄像头状态
     */
    public static final int CAMERA_STATUS_CLOSED = 0;
    public static final int CAMERA_STATUS_OPENED = 1;
    public static final int CAMERA_STATUS_RESUMED = 2;
    public static final int CAMERA_STATUS_PAUSED = 4;

    /**
     * camera index
     * 摄像头索引
     */
    private int index;

    /**
     * device ID
     * 设备id
     */
    private long deviceID;

    /**
     * device name
     * 设备名称
     */
    private String deviceName;

    /**
     * user ID
     * 用户id
     */
    private long userID;

    /**
     * device status
     * 设备状态
     */
    private int deviceStatus;

    /**
     * camera status
     * 摄像头状态
     */
    private int cameraStatus = CAMERA_STATUS_CLOSED;


    public int getIndex()
    {
        return index;
    }

    public void setIndex(int index)
    {
        this.index = index;
    }

    public long getDeviceID()
    {
        return deviceID;
    }

    public void setDeviceID(long deviceID)
    {
        this.deviceID = deviceID;
    }

    public String getDeviceName()
    {
        return deviceName;
    }

    public void setDeviceName(String deviceName)
    {
        this.deviceName = deviceName;
    }

    public long getUserID()
    {
        return userID;
    }

    public void setUserID(long userID)
    {
        this.userID = userID;
    }

    public int getDeviceStatus()
    {
        return deviceStatus;
    }

    public void setDeviceStatus(int deviceStatus)
    {
        this.deviceStatus = deviceStatus;
    }

    public int getCameraStatus()
    {
        return cameraStatus;
    }

    public void setCameraStatus(int cameraStatus)
    {
        this.cameraStatus = cameraStatus;
    }

    @Override
    public boolean equals(Object o)
    {
        CameraEntity cameraEntity = (CameraEntity) o;
        return deviceID == cameraEntity.getDeviceID();
    }

    @Override
    public int hashCode()
    {
        return (int) deviceID;
    }
}
