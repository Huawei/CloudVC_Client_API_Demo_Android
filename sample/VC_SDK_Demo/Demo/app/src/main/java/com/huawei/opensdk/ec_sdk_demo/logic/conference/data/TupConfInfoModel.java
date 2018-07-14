package com.huawei.opensdk.ec_sdk_demo.logic.conference.data;

import java.io.Serializable;


public class TupConfInfoModel implements Serializable
{
    private String confID;
    private String subject;
    private String accessNumber;
    private String chairmanPwd;
    private String guestPwd;
    private String startTime;
    private String endTime;
    private int mediaType;
    private String scheduserNumber;
    private String scheduserName;
    private int confState;

    public String getConfID()
    {
        return confID;
    }

    public void setConfID(String confID)
    {
        this.confID = confID;
    }

    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public String getAccessNumber()
    {
        return accessNumber;
    }

    public void setAccessNumber(String accessNumber)
    {
        this.accessNumber = accessNumber;
    }

    public String getChairmanPwd()
    {
        return chairmanPwd;
    }

    public void setChairmanPwd(String chairmanPwd)
    {
        this.chairmanPwd = chairmanPwd;
    }

    public String getGuestPwd()
    {
        return guestPwd;
    }

    public void setGuestPwd(String guestPwd)
    {
        this.guestPwd = guestPwd;
    }

    public String getStartTime()
    {
        return startTime;
    }

    public void setStartTime(String startTime)
    {
        this.startTime = startTime;
    }

    public String getEndTime()
    {
        return endTime;
    }

    public void setEndTime(String endTime)
    {
        this.endTime = endTime;
    }

    public int getMediaType()
    {
        return mediaType;
    }

    public void setMediaType(int mediaType)
    {
        this.mediaType = mediaType;
    }

    public String getScheduserNumber()
    {
        return scheduserNumber;
    }

    public void setScheduserNumber(String scheduserNumber)
    {
        this.scheduserNumber = scheduserNumber;
    }

    public String getScheduserName()
    {
        return scheduserName;
    }

    public void setScheduserName(String scheduserName)
    {
        this.scheduserName = scheduserName;
    }

    public int getConfState()
    {
        return confState;
    }

    public void setConfState(int confState)
    {
        this.confState = confState;
    }
}
