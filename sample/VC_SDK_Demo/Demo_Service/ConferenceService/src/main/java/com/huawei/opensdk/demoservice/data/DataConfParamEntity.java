package com.huawei.opensdk.demoservice.data;

import java.io.Serializable;

public class DataConfParamEntity implements Serializable
{
    private String passCode;
    private String dataConfUrl;
    private String dataRandom;
    private String dataConfId;

    public String getPassCode()
    {
        return passCode;
    }

    public void setPassCode(String passCode)
    {
        this.passCode = passCode;
    }

    public String getDataConfUrl()
    {
        return dataConfUrl;
    }

    public void setDataConfUrl(String dataConfUrl)
    {
        this.dataConfUrl = dataConfUrl;
    }

    public String getDataRandom()
    {
        return dataRandom;
    }

    public void setDataRandom(String dataRandom)
    {
        this.dataRandom = dataRandom;
    }

    public String getDataConfId()
    {
        return dataConfId;
    }

    public void setDataConfId(String dataConfId)
    {
        this.dataConfId = dataConfId;
    }
}
