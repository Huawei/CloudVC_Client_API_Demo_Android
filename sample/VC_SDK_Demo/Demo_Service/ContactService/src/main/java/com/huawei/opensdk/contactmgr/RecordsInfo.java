package com.huawei.opensdk.contactmgr;


import com.huawei.common.CallRecordInfo;

import java.util.Date;

/**
 * This class is about call record information.
 * 通话记录信息类
 */
public class RecordsInfo {

    /**
     * The dial type
     * 通话类型（语音/视频）
     */
    private CallRecordInfo.DialType callType;

    /**
     * Call time
     * 通过时长
     */
    private long callTime;

    /**
     * Call record type
     * 呼叫类型（呼入/呼出/未接来电）
     */
    private CallRecordInfo.RecordType recordType;

    /**
     * Record call id
     * 通话记录id
     */
    private int recordId;

    /**
     * Start call time
     * 开始呼叫时间
     */
    private Date callStartTime;

    /**
     * Number
     * 号码
     */
    private String number;

    /**
     * interval
     * 时间间隔
     */
    private String interval;

    public RecordsInfo() {
    }

    public CallRecordInfo.DialType getCallType() {
        return callType;
    }

    public void setCallType(CallRecordInfo.DialType callType) {
        this.callType = callType;
    }

    public long getCallTime() {
        return callTime;
    }

    public void setCallTime(long callTime) {
        this.callTime = callTime;
    }

    public CallRecordInfo.RecordType getRecordType() {
        return recordType;
    }

    public void setRecordType(CallRecordInfo.RecordType recordType) {
        this.recordType = recordType;
    }

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public Date getCallStartTime() {
        return callStartTime;
    }

    public void setCallStartTime(Date callStartTime) {
        this.callStartTime = callStartTime;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }
}
