package com.huawei.opensdk.callmgr;

import java.io.Serializable;

/**
 * This class is about transfer between call information modules
 * 呼叫信息模块间传递类
 */
public class CallInfo implements Serializable
{
    /**
     * call ID
     * 呼叫id
     */
    private int callID;

    /**
     * conference ID
     * 会议id
     */
    private String confID;

    /**
     * Peer number
     * 来电号码
     */
    private String peerNumber;

    /**
     * Peer display name
     * 来电人
     */
    private String peerDisplayName;

    /**
     * Video call
     * 是否是视频来电
     */
    private boolean isVideoCall;

    /**
     *  Conference identify
     * 是否是会议
     */
    private boolean isFocus;

    /**
     * Caller
     * 主叫
     */
    private boolean isCaller;

    /**
     * Reason code
     * 错误码
     */
    private int reasonCode;

    @Override
    public String toString() {
        return "CallInfo{" +
                "callID=" + callID +
                ", confID='" + confID + '\'' +
                ", peerNumber='" + peerNumber + '\'' +
                ", peerDisplayName='" + peerDisplayName + '\'' +
                ", isVideoCall=" + isVideoCall +
                ", isFocus=" + isFocus +
                ", isCaller=" + isCaller +
                ", reasonCode=" + reasonCode +
                '}';
    }


    public int getCallID() {
        return callID;
    }

    public void setCallID(int callID) {
        this.callID = callID;
    }

    public String getConfID() {
        return confID;
    }

    public void setConfID(String confID) {
        this.confID = confID;
    }

    public String getPeerNumber() {
        return peerNumber;
    }

    public void setPeerNumber(String peerNumber) {
        this.peerNumber = peerNumber;
    }

    public String getPeerDisplayName() {
        return peerDisplayName;
    }

    public void setPeerDisplayName(String peerDisplayName) {
        this.peerDisplayName = peerDisplayName;
    }

    public boolean isVideoCall() {
        return isVideoCall;
    }

    public void setVideoCall(boolean videoCall) {
        isVideoCall = videoCall;
    }

    public boolean isFocus() {
        return isFocus;
    }

    public void setFocus(boolean focus) {
        isFocus = focus;
    }

    public boolean isCaller() {
        return isCaller;
    }

    public void setCaller(boolean caller) {
        isCaller = caller;
    }


    public int getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(int reasonCode) {
        this.reasonCode = reasonCode;
    }


    public static class Builder
    {
        private int callID;
        private String confID;
        private String peerNumber;
        private String peerDisplayName;

        private boolean isVideoCall;
        private boolean isFocus;
        private boolean isCaller;

        private int reasonCode;

        public Builder setCallID(int callID) {
            this.callID = callID;
            return this;
        }

        public Builder setConfID(String confID) {
            this.confID = confID;
            return this;
        }

        public Builder setPeerNumber(String peerNumber) {
            this.peerNumber = peerNumber;
            return this;
        }

        public Builder setPeerDisplayName(String peerDisplayName) {
            this.peerDisplayName = peerDisplayName;
            return this;
        }

        public Builder setVideoCall(boolean videoCall) {
            isVideoCall = videoCall;
            return this;
        }

        public Builder setFocus(boolean focus) {
            isFocus = focus;
            return this;
        }

        public Builder setCaller(boolean caller) {
            isCaller = caller;
            return this;
        }

        public Builder setReasonCode(int reasonCode) {
            this.reasonCode = reasonCode;
            return this;
        }


        private void apply(CallInfo params)
        {
            params.callID = this.callID;
            params.confID = this.confID;
            params.peerNumber = this.peerNumber;
            params.peerDisplayName = this.peerDisplayName;

            params.isVideoCall = this.isVideoCall;
            params.isFocus = this.isFocus;
            params.isCaller = this.isCaller;
            params.reasonCode = this.reasonCode;
        }

        public CallInfo build()
        {
            CallInfo callInfo = new CallInfo();
            apply(callInfo);
            return callInfo;
        }
    }
}
