package com.huawei.opensdk.demoservice.data;

import com.huawei.opensdk.sdkwrapper.login.LoginCenter;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.CopyOnWriteArrayList;

import object.DataConfParam;

/**
 * This class is about conference information entity
 * 会议信息实体类
 */
public class ConferenceEntity implements Serializable
{

    /**
     * 会议状态
     */
    public static final int STATUS_CREATED = 0;
    public static final int STATUS_TO_ATTEND = 1;
    public static final int STATUS_IN_PROGRESS = 2;
    public static final int STATUS_END = 3;
    public static final int STATUS_CREATING = 4;


    public static final int WORK_FLOW_STEP_CREATE = 11;
    public static final int WORK_FLOW_STEP_JOIN = 12;
    public static final int WORK_FLOW_STEP_ADD_ATTENDEE = 13;
    public static final int WORK_FLOW_STEP_FINISH = 14;

    public static final int LOGIN_E_DEPLOY_ENTERPRISE_IPT = 0;

    /** 会议类型 TYPE_INSTANT:即时会议  TYPE_BOOKING：预约会议 */
    public static final int TYPE_INSTANT = 1;
    public static final int TYPE_BOOKING = 2;

    //语音会议 ConfctrlConfMediatypeFlag.CONFCTRL_E_CONF_MEDIATYPE_FLAG_VOICE;
    public static final int AUDIO_CONFERENCE_TYPE = 0x01;
    //视频+语音会议
    public static final int VIDEO_CONFERENCE_TYPE = 0x01|0x02;
    //数据+语音会议
    public static final int DATA_CONFERENCE_TYPE = 0x01|0x10;
    //语音+视频+数据
    public static final int MULTI_CONFERENCE_TYPE = 0x01|0x02|0x10;

    //语音+高清视频+数据
    public static final int VIDEO_MULTI_CONFERENCE_TYPE = 21;

    /**
     * 会议ID
     */
    private String confId; //primary Key
    /**
     * 会议主题
     */
    private String subject;
    /**
     * 开始时间
     */
    private Timestamp beginTime;

    private String startTimeStr;

    /**
     * 结束时间
     */
    private Timestamp endTime;

    private String endTimeStr;

    private String selfAccessNumber;

    /**
     * 会议主持人(入会号码)
     */
    private String host;

    /**
     * 会议主持人(账号)
     */
    private String hostAccount;

    /**
     * 会议类型
     */
    private int type = TYPE_INSTANT;

    /**
     * 多媒体会议类型
     */
    private int mediaType = AUDIO_CONFERENCE_TYPE;

    /**
     * 会议状态
     */
    private int state = STATUS_END;



    /**
     * 会议接入码
     */
    private String passCode;

    /**
     * 主席密码
     */
    private String hostCode;

    /**
     * 与会者密码
     */
    private String memberCode;

    /**
     * 不知道是主席合适与会者情况下的接入密码
     */
    private String unknowCode;

    private String groupUri;

    /**
     * 是否已经加入数据会议
     */
    private boolean isInDataConf;

    private String dataConfChairman;

    private DataConfParamEntity dataConfParam;

    private Stack<Integer> workFlowStack;


    /**
     * 与会人列表
     */
    private List<ConferenceMemberEntity> confMemberList = new CopyOnWriteArrayList<>();


    public String getConfId()
    {
        return confId;
    }

    public void setConfId(String confId)
    {
        this.confId = confId;
    }

    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public Timestamp getBeginTime()
    {
        return beginTime;
    }

    public void setBeginTime(Timestamp beginTime)
    {
        this.beginTime = beginTime;
    }

    public Timestamp getEndTime()
    {
        return endTime;
    }

    public void setEndTime(Timestamp endTime)
    {
        this.endTime = endTime;
    }

    public String getHost()
    {
        return host;
    }

    public void setHost(String host)
    {
        this.host = host;
    }

    public String getHostAccount()
    {
        return hostAccount;
    }

    public void setHostAccount(String hostAccount)
    {
        this.hostAccount = hostAccount;
    }

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    public int getMediaType()
    {
        return mediaType;
    }

    public void setMediaType(int mediaType)
    {
        this.mediaType = mediaType;
    }

    public int getState()
    {
        return state;
    }

    public void setState(int state)
    {
        this.state = state;
    }

    public String getPassCode()
    {
        return passCode;
    }

    public void setPassCode(String passCode)
    {
        this.passCode = passCode;
    }

    public String getHostCode()
    {
        return hostCode;
    }

    public void setHostCode(String hostCode)
    {
        this.hostCode = hostCode;
    }

    public String getMemberCode()
    {
        return memberCode;
    }

    public void setMemberCode(String memberCode)
    {
        this.memberCode = memberCode;
    }

    public void addConfMember(ConferenceMemberEntity conferenceMemberEntity)
    {
        confMemberList.add(conferenceMemberEntity);
    }

    public void updateSelfAccessNumber(String accessNumber)
    {
        selfAccessNumber = accessNumber;
        confMemberList.get(0).setNumber(accessNumber);
    }

    public List<ConferenceMemberEntity> getConfMemberList()
    {
        return confMemberList;
    }

    public void setConfMemberList(List<ConferenceMemberEntity> confMemberList)
    {
        this.confMemberList = confMemberList;
    }

    public String getStartTimeStr()
    {
        return startTimeStr;
    }

    public void setStartTimeStr(String startTimeStr)
    {
        this.startTimeStr = startTimeStr;
    }

    public String getEndTimeStr()
    {
        return endTimeStr;
    }

    public void setEndTimeStr(String endTimeStr)
    {
        this.endTimeStr = endTimeStr;
    }


    public String getSelfAccessNumber()
    {
        return selfAccessNumber;
    }

    public void setSelfAccessNumber(String selfAccessNumber)
    {
        this.selfAccessNumber = selfAccessNumber;
    }

    public boolean isInDataConf()
    {
        return isInDataConf;
    }

    public void setInDataConf(boolean inDataConf)
    {
        isInDataConf = inDataConf;
    }

    public DataConfParamEntity getDataConfParam()
    {
        return dataConfParam;
    }

    public void setDataConfParam(DataConfParam dataConfParam)
    {
        DataConfParamEntity dataConfParamEntity = new DataConfParamEntity();
        dataConfParamEntity.setDataConfId(dataConfParam.getDataConfId());
        dataConfParamEntity.setDataConfUrl(dataConfParam.getDataConfUrl());
        dataConfParamEntity.setDataRandom(dataConfParam.getDataRandom());
        dataConfParamEntity.setPassCode(dataConfParam.getPassCode());
        this.dataConfParam = dataConfParamEntity;
    }

    public String getDataConfChairman()
    {
        return dataConfChairman;
    }

    public void setDataConfChairman(String dataConfChairman)
    {
        this.dataConfChairman = dataConfChairman;
    }

    public Stack<Integer> getWorkFlowStack()
    {
        return workFlowStack;
    }

    public void setWorkFlowStack(Stack<Integer> workFlowStack)
    {
        this.workFlowStack = workFlowStack;
    }

    public String getUnknowCode()
    {
        return unknowCode;
    }

    public void setUnknowCode(String unknowCode)
    {
        this.unknowCode = unknowCode;
    }

    public String getGroupUri()
    {
        return groupUri;
    }

    public void setGroupUri(String groupUri)
    {
        this.groupUri = groupUri;
    }

    public ConferenceMemberEntity queryMemberEntityByNumber(String number)
    {
        for (ConferenceMemberEntity conferenceMemberEntity : confMemberList)
        {
            if (number.equals(conferenceMemberEntity.getNumber()))
            {
                return conferenceMemberEntity;
            }
        }
        return null;
    }

    public ConferenceMemberEntity getSelfMemberEntity()
    {
        String number = LoginCenter.getInstance().getSipAccountInfo().getTerminal();
        for (ConferenceMemberEntity conferenceMemberEntity : confMemberList)
        {
            if (conferenceMemberEntity.getNumber().equals(number))
            {
                return conferenceMemberEntity;
            }
        }
        return null;
    }

    public List<CameraEntity> getSelfCameraEntityList()
    {
        ConferenceMemberEntity conferenceMemberEntity = getSelfMemberEntity();
        if (conferenceMemberEntity != null)
        {
            return conferenceMemberEntity.getCameraEntityList();
        }
        return null;
    }
}
