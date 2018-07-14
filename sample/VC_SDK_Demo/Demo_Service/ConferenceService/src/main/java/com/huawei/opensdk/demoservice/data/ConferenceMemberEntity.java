package com.huawei.opensdk.demoservice.data;

import com.huawei.tup.confctrl.ConfctrlConfRole;
import com.huawei.tup.confctrl.ConfctrlParticipantStatus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is about attendee information
 * 与会者信息实体类
 */
public class ConferenceMemberEntity implements Serializable
{
    /**
     * Attendee status
     * 与会人状态
     */
    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_LEAVE_CONF = 2;
    public static final int STATUS_MUTE = 3;
    public static final int STATUS_INVITE = 5;

    /**
     * attendee identity ：presider or member
     * 主持人 / 与会人
     */
    public static final int ROLE_PRESIDER = 1;
    public static final int ROLE_MEMBER = 2;

    /**
     * conf Id
     * 会议ID
     */
    private String confId;
    /**
     * number
     * 与会号码
     */
    private String number;


    private String displayName;

    /**
     * present
     * 是否是主讲人
     */
    private boolean present;

    /**
     * Data conference user id
     * 数据会议使用ID
     */
    private long dataUserId;

    /**
     * Join in data conference
     * 是否已经加入数据会议
     */
    private boolean inDataConference;

    /**
     * attendee identity
     * 主持人 / 与会人
     */
    private ConfctrlConfRole role = ConfctrlConfRole.CONFCTRL_E_CONF_ROLE_ATTENDEE;

    /**
     * Mute
     * 是否静音
     */
    private boolean isMute = false;

    /**
     * Self
     * 是否是自己
     */
    private boolean isSelf = false;

    /**
     * Quiet
     * 静音会场
     */
    private boolean isQuiet = false;

    private List<CameraEntity> cameraEntityList = new ArrayList<>();


    /**
     * Conference status
     * 与会状态
     * @see #STATUS_SUCCESS
     * @see #STATUS_LEAVE_CONF
     */
    private ConfctrlParticipantStatus status = ConfctrlParticipantStatus.CONFCTRL_E_PARTICIPANT_STATUS_CALL_FAILED;

    /**
     * hand up
     * 举手状态
     */
    private boolean handUp = false;

    public ConferenceMemberEntity(String number)
    {
        setNumber(number);
    }

    public String getConfId()
    {
        return confId;
    }

    public void setConfId(String confId)
    {
        this.confId = confId;
    }

    public String getNumber()
    {
        return number;
    }

    public void setNumber(String number)
    {
        this.number = number;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }

    public boolean isPresent()
    {
        return present;
    }

    public void setPresent(boolean present)
    {
        this.present = present;
    }

    public long getDataUserId()
    {
        return dataUserId;
    }

    public void setDataUserId(long dataUserId)
    {
        this.dataUserId = dataUserId;
    }

    public boolean isInDataConference()
    {
        return inDataConference;
    }

    public void setInDataConference(boolean inDataConference)
    {
        this.inDataConference = inDataConference;
    }

    public ConfctrlParticipantStatus getStatus()
    {
        return status;
    }

    public void setStatus(ConfctrlParticipantStatus status)
    {
        this.status = status;
    }

    public boolean isHandUp()
    {
        return handUp;
    }

    public void setHangUp(boolean handUp)
    {
        this.handUp = handUp;
    }

    public ConfctrlConfRole getRole()
    {
        return role;
    }

    public void setRole(ConfctrlConfRole role)
    {
        this.role = role;
    }

    public boolean isMute()
    {
        return isMute;
    }

    public void setMute(boolean mute)
    {
        isMute = mute;
    }

    public boolean isSelf()
    {
        return isSelf;
    }

    public void setSelf(boolean self)
    {
        isSelf = self;
    }

    public boolean isQuiet()
    {
        return isQuiet;
    }

    public void setQuiet(boolean quiet)
    {
        isQuiet = quiet;
    }

    public List<CameraEntity> getCameraEntityList()
    {
        return cameraEntityList;
    }

    public void setCameraEntityList(List<CameraEntity> cameraEntityList)
    {
        this.cameraEntityList = cameraEntityList;
    }

    public void replaceCamera(CameraEntity cameraEntity)
    {
        int index = cameraEntityList.indexOf(cameraEntity);
        if (index == -1)
        {
            cameraEntityList.add(cameraEntity);
        }
        else
        {
            cameraEntityList.set(index, cameraEntity);
        }
    }

    public void updateCamera(long deviceID, int status)
    {
        for (CameraEntity cameraEntity : cameraEntityList)
        {
            if (cameraEntity.getDeviceID() == deviceID)
            {
                cameraEntity.setCameraStatus(status);
                return;
            }
        }
    }

    public CameraEntity getOpenedCamera()
    {
        for (CameraEntity cameraEntity : cameraEntityList)
        {
            if (cameraEntity.getCameraStatus() == CameraEntity.CAMERA_STATUS_OPENED)
            {
                return cameraEntity;
            }
        }
        return null;
    }
}
