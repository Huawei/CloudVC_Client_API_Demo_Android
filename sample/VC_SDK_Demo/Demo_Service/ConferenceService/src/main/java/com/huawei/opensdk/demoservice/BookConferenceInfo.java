package com.huawei.opensdk.demoservice;

import java.io.Serializable;
import java.util.List;


/**
 * This class is about book conference information
 * 预约会议信息类
 */
public class BookConferenceInfo implements Serializable {

    /**
     * subject
     * 会议主题
     */
    private String subject;

    /**
     * passWorld
     * 会议密码
     */
    private String passWord;

    /**
     * media type
     * 媒体类型
     */
    ConfConstant.ConfMediaType mediaType;

    /**
     * Conference start time
     * 会议开始时间
     */
    private String startTime;

    /**
     * duration
     * 会议时长
     */
    private int duration;

    /**
     * size
     * 会议人数
     */
    private int size;

    /**
     * Member list
     * 与会者列表
     */
    private List<Member> memberList;

    /**
     * Start now
     * 是否是即时会议
     */
    private boolean isInstantConference = true; //默认为立即会议

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public ConfConstant.ConfMediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(ConfConstant.ConfMediaType mediaType) {
        this.mediaType = mediaType;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<Member> getMemberList() {
        return memberList;
    }

    public void setMemberList(List<Member> memberList) {
        this.memberList = memberList;
    }

    public boolean isInstantConference() {
        return isInstantConference;
    }

    public void setInstantConference(boolean instantConference) {
        isInstantConference = instantConference;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }
}
