package com.huawei.opensdk.demoservice;

import com.huawei.opensdk.demoservice.data.CameraEntity;
import com.huawei.tup.confctrl.ConfctrlConfRole;
import com.huawei.tup.confctrl.sdk.TupConfECAttendeeInfo;
import com.huawei.tup.confctrl.sdk.TupConfVCAttendeeInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * This method is used to conf member info.
 * 与会者信息
 */
public class Member {
    /**
     * 与会者MT号
     */
    private String MT;
    /**
     * 是否广播
     */
    private boolean isBroadcast;
    /**
     * 是否有主席
     */
    private boolean hasChairman;
    /**
     * 是否会议静音
     */
    private boolean isConfMute;
    /**
     * 会议名称
     */
    private String siteName;
    /**
     * 是否已加入会议
     */
    private boolean isJoinConf;
    /**
     * 是否离开会议
     */
    private boolean isLeaveConf;

    /**
     * 与会者号码
     */
    private String number;

    /**
     * 来电姓名
     */
    private String displayName;

    /**
     * email
     * 邮箱
     */
    private String email;

    /**
     * SMS
     */
    private String sms;

    /**
     * 帐号
     */
    private String accountId;

    /**
     * 与会者状态
     */
    private ConfConstant.ParticipantStatus status;

    /**
     * 与会者角色
     */
    private ConfConstant.ConfRole role;

    /**
     * 是否静音
     */
    private boolean isMute;

    /**
     * 是否举手
     */
    private boolean isHandUp;

    /**
     * 是否已在数据会议中
     */
    private boolean inDataConference;

    /**
     * 是否是主席
     */
    private boolean isHost;

    /**
     * 是否主讲人
     */
    private boolean isPresent;

    /**
     * 用户id
     */
    private long dataUserId;

    /**
     * 与会者id
     */
    private String participantId;

    /**
     * 是否是自己
     */
    private boolean isSelf;

    /**
     * 是否自动邀请
     */
    private boolean isAutoInvite;

    /**
     * 摄像头列表
     */
    private List<CameraEntity> cameraEntityList = new ArrayList<>();

    public Member()
    {

    }

    public Member(String MT)
    {
        this.MT = MT;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDisplayName() {
        if ((displayName == null) || (displayName.equals("")))
        {
            return number;
        }
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSms() {
        return sms;
    }

    public void setSms(String sms) {
        this.sms = sms;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public boolean isMute() {
        return isMute;
    }

    public void setMute(boolean mute) {
        isMute = mute;
    }

    public boolean isAutoInvite() {
        return isAutoInvite;
    }

    public void setAutoInvite(boolean autoInvite) {
        isAutoInvite = autoInvite;
    }

    public ConfConstant.ConfRole getRole() {
        return role;
    }

    public void setRole(ConfConstant.ConfRole role) {
        this.role = role;
    }



    public boolean isHandUp() {
        return isHandUp;
    }

    public void setHandUp(boolean handUp) {
        isHandUp = handUp;
    }


    public ConfConstant.ParticipantStatus getStatus() {
        return status;
    }

    public void setStatus(ConfConstant.ParticipantStatus status) {
        this.status = status;
    }

    public boolean isSelf() {
        return isSelf;
    }

    public void setSelf(boolean self) {
        isSelf = self;
    }

    public boolean isBroadcast() {
        return isBroadcast;
    }

    public void setBroadcast(boolean broadcast) {
        isBroadcast = broadcast;
    }

    public boolean isHasChairman() {
        return hasChairman;
    }

    public void setHasChairman(boolean hasChairman) {
        this.hasChairman = hasChairman;
    }

    public boolean isConfMute() {
        return isConfMute;
    }

    public void setConfMute(boolean confMute) {
        isConfMute = confMute;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public boolean isJoinConf() {
        return isJoinConf;
    }

    public void setJoinConf(boolean joinConf) {
        isJoinConf = joinConf;
    }

    public boolean isLeaveConf() {
        return isLeaveConf;
    }

    public void setLeaveConf(boolean leaveConf) {
        isLeaveConf = leaveConf;
    }

    public List<CameraEntity> getCameraEntityList() {
        return cameraEntityList;
    }

    public void setCameraEntityList(List<CameraEntity> cameraEntityList) {
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

    public boolean isHost() {
        return isHost;
    }

    public void setHost(boolean host) {
        isHost = host;
    }

    public boolean isPresent() {
        return isPresent;
    }

    public void setPresent(boolean present) {
        this.isPresent = present;
    }

    public boolean isInDataConference() {
        return inDataConference;
    }

    public void setInDataConference(boolean inDataConference) {
        this.inDataConference = inDataConference;
    }

    public long getDataUserId() {
        return dataUserId;
    }

    public void setDataUserId(long dataUserId) {
        this.dataUserId = dataUserId;
    }

    public String getMT() {
        return MT;
    }

    public void setMT(String MT) {
        this.MT = MT;
    }

    public String getParticipantId() {
        return participantId;
    }

    public void setParticipantId(String participantId) {
        this.participantId = participantId;
    }

    public void update(Member newMember) {
        this.number = newMember.number;
        this.displayName = newMember.displayName;
        this.email = newMember.email;
        this.sms = newMember.sms;
        this.accountId = newMember.accountId;

        this.isMute = newMember.isMute;
        this.isAutoInvite = newMember.isAutoInvite;
        this.role = newMember.role;

        this.isHandUp = newMember.isHandUp;
        this.status = newMember.status;

        //this.isPresent = newMember.isPresent;
        //this.inDataConference = n;

        this.participantId = newMember.participantId;
    }

    public void update(TupConfECAttendeeInfo attendeeInfo)
    {
        setParticipantId(attendeeInfo.getParticipantId());
        setNumber(attendeeInfo.getNumber());
        setDisplayName(attendeeInfo.getName());
        setAccountId(attendeeInfo.getAcountId());
        setEmail(attendeeInfo.getEmail());
        setSms(attendeeInfo.getSms());
        setMute(attendeeInfo.isMute());
        setHandUp(attendeeInfo.isHandup());
        ConfConstant.ConfRole role = ((attendeeInfo.getRole() == ConfctrlConfRole.CONFCTRL_E_CONF_ROLE_CHAIRMAN) ?
                ConfConstant.ConfRole.CHAIRMAN : ConfConstant.ConfRole.ATTENDEE);
        setRole(role);
        setStatus(ConfConvertUtil.convertConfctrlParticipantStatus(attendeeInfo.getStatus()));

        //setSelf((attendeeInfo.getIsSelf()==1));
    }

    public void update(TupConfVCAttendeeInfo attendeeInfo)
    {
//        setParticipantId(attendeeInfo.getParticipantId());
        setNumber(attendeeInfo.getNumber());
//        setDisplayName(attendeeInfo.getName());
//        setAccountId(attendeeInfo.getAcountId());
//        setEmail(attendeeInfo.getEmail());
//        setSms(attendeeInfo.getSms());
        setMute(attendeeInfo.isMute());
        setHandUp(attendeeInfo.isHandup());
        ConfConstant.ConfRole role = ((attendeeInfo.getRole() == ConfctrlConfRole.CONFCTRL_E_CONF_ROLE_CHAIRMAN) ?
                ConfConstant.ConfRole.CHAIRMAN : ConfConstant.ConfRole.ATTENDEE);
        setRole(role);
//        setStatus(ConfConvertUtil.convertConfctrlParticipantStatus(attendeeInfo.getStatus()));

        //setSelf((attendeeInfo.getIsSelf()==1));
    }


    @Override
    public int hashCode() {
        return MT.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (obj.getClass() != this.getClass())) {
            return false;
        }

        Member member = (Member) obj;

        if (member.MT.equals(this.MT)) {
            return true;
        }
        return false;
    }

}
