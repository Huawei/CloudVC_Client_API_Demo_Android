package com.huawei.opensdk.callmgr;

import com.huawei.opensdk.commonservice.util.LogUtil;

import common.TupCallParam;
import common.VideoWndType;
import object.VideoRenderInfo;
import tupsdk.TupCall;

/**
 * This class is about call session
 * 呼叫会话类
 */
public class Session {
    private static final String TAG = Session.class.getSimpleName();

    /**
     * call information
     * 呼叫信息
     */
    private TupCall tupCall;

    /**
     * call type
     * 呼叫类型
     */
    private CallConstant.CallStatus callStatus = CallConstant.CallStatus.IDLE;

    /**
     * hold video
     * 是否视频保持
     */
    private boolean isVideoHold;

    /**
     * Blind transfer
     * 是否盲转
     */
    private boolean isBlindTransfer;

    public Session(TupCall tupCall){
        this.tupCall = tupCall;
    }

    public TupCall getTupCall() {
        return tupCall;
    }

    public int getCallID() {
        return this.tupCall.getCallId();
    }

    public CallConstant.CallStatus getCallStatus() {
        return callStatus;
    }

    public void setCallStatus(CallConstant.CallStatus callStatus) {
        this.callStatus = callStatus;
    }

    public boolean isVideoHold() {
        return isVideoHold;
    }

    public void setVideoHold(boolean videoHold) {
        isVideoHold = videoHold;
    }

    public boolean isBlindTransfer() {
        return isBlindTransfer;
    }

    public void setBlindTransfer(boolean blindTransfer) {
        this.isBlindTransfer = blindTransfer;
    }

    /**
     * This method is used to answer the call
     * @param isVideo
     * @return
     */
    public boolean answerCall(boolean isVideo)
    {
        int iVideoCall;

        CallMgr.getInstance().setDefaultAudioRoute(isVideo);
        if (isVideo)
        {
            initVideoWindow();
            //VideoDeviceManager.getInstance().setVideoOrient(tupCall.getCallId(), Constant.VideoDevice.FRONT_CAMERA);
            VideoMgr.getInstance().setVideoOrient(tupCall.getCallId(), CallConstant.FRONT_CAMERA);
            iVideoCall = TupCallParam.CALL_E_CALL_TYPE.TUP_CALLTYPE_VIDEO;
        }
        else
        {
            iVideoCall = TupCallParam.CALL_E_CALL_TYPE.TUP_CALLTYPE_AUDIO;
        }

        int result = tupCall.acceptCall(iVideoCall);
        if (result != 0)
        {
            LogUtil.e(TAG, "acceptCall return failed, result = " + result);
            return false;
        }
        return true;
    }

    /**
     * This method is used to end Call
     * @return
     */
    public boolean endCall()
    {
        int result = tupCall.endCall();
        if (result != 0)
        {
            LogUtil.e(TAG, "endCall return failed, result = " + result);
            return false;
        }
        return true;
    }


    /**
     *  发起偏转呼叫
     * divert call
     * @param divertNumber
     * @return
     */
    public boolean divertCall(String divertNumber)
    {
        int result = tupCall.divertCall(divertNumber);
        if (result != 0)
        {
            LogUtil.e(TAG, "divertCall return failed, result = " + result);
            return false;
        }
        return true;
    }

    /**
     * start blind transfer request
     * 发起盲转呼叫请求
     * @param transferNumber 盲转号码
     * @return
     */
    public boolean blindTransfer(String transferNumber)
    {
        int result = tupCall.blindTransfer(transferNumber);
        if (result != 0)
        {
            LogUtil.e(TAG, "blindTransfer return failed, result = " + result);
            return false;
        }
        return true;
    }

    /**
     * Call hold
     * @return
     */
    public boolean holdCall()
    {
        int result = tupCall.holdCall();
        if (result != 0)
        {
            LogUtil.e(TAG, "holdCall return failed, result = " + result);
            return false;
        }
        return true;
    }


    /**
     * Cancel Call hold
     * @return
     */
    public boolean unHoldCall()
    {
        int result = tupCall.unholdCall();
        if (result != 0)
        {
            LogUtil.e(TAG, "unholdCall return failed, result = " + result);
            return false;
        }
        return true;
    }

    /**
     * send DTMF during call
     * 二次拨号
     * @param code  （0到9，*为10,#为11）
     * @return
     */
    public boolean reDial(int code)
    {
        int result = tupCall.sendDTMF(code);
        if (result != 0)
        {
            LogUtil.e(TAG, "sendDTMF return failed, result = " + result);
            return false;
        }
        return true;
    }

    /**
     * add video
     * 音频转视频请求
     * @return
     */
    public boolean addVideo()
    {
        initVideoWindow();

        int result = tupCall.addVideo();
        if (result != 0)
        {
            LogUtil.e(TAG, "addVideo return failed, result = " + result);
            return false;
        }

        setCallStatus(CallConstant.CallStatus.VIDEO_CALLING);
        return true;
    }

    /**
     * delet video
     * 删除视频请求
     * @return
     */
    public boolean delVideo()
    {
        int result = tupCall.delVideo();
        if (result != 0)
        {
            LogUtil.e(TAG, "delVideo return failed, result = " + result);
            return false;
        }

        setCallStatus(CallConstant.CallStatus.AUDIO_CALLING);

        return true;
    }

    /**
     * Reject Audio Transfer Video Call
     * 拒绝音频转视频呼叫
     * @return
     */
    public boolean rejectAddVideo()
    {
        int result = tupCall.replyAddVideo(0);
        if (result != 0)
        {
            LogUtil.e(TAG, "replyAddVideo(reject) return failed, result = " + result);
            return false;
        }
        return true;
    }

    /**
     * Agree to Audio transfer video Call
     * 同意音频转视频呼叫
     * @return
     */
    public boolean acceptAddVideo()
    {
        initVideoWindow();

        int result = tupCall.replyAddVideo(1);
        if (result != 0)
        {
            LogUtil.e(TAG, "replyAddVideo(accept) return failed, result = " + result);
            return false;
        }
        return true;
    }


    /**
     * set media microphone mute
     * 设置(或取消)麦克风静音
     * @param mute
     * @return
     */
    public boolean muteMic(boolean mute)
    {
        int result = tupCall.mediaMuteMic(mute ? 1 : 0);
        if (result != 0)
        {
            LogUtil.e(TAG, "mediaMuteMic return failed, result = " + result);
            return false;
        }
        return true;
    }


    /**
     * set media speaker mute
     * 设置(或取消)扬声器静音
     * @param mute
     * @return
     */
    public boolean muteSpeak(boolean mute)
    {
        int result = tupCall.mediaMuteSpeak(mute ? 1 : 0);
        if (result != 0)
        {
            LogUtil.e(TAG, "mediaMuteSpeak return failed, result = " + result);
            return false;
        }
        return true;
    }



    public void switchCamera(int cameraIndex)
    {

    }


    public void initVideoWindow()
    {
        VideoMgr.getInstance().initVideoWindow(tupCall.getCallId());
    }


    /**
     * 恢复视频采集
     * @param index
     * @param rotation
     * @return
     */
    public boolean setCaptureRotation(int index, int rotation)
    {
        tupCall.setCaptureRotation(index, rotation);

        // 前置摄像头
        if (1 == index)
        {
            // 窗口镜像模式 0:不做镜像(默认值) 1:上下镜像(目前未支持) 2:左右镜像
            // 本地视频前置摄像头做左右镜像，所以设置mirror type为 2
            VideoRenderInfo videoRenderInfo = new VideoRenderInfo();
            videoRenderInfo.setRederType(VideoWndType.local);
            videoRenderInfo.setUlMirrortype(2);
            tupCall.setMobileVideoRender(videoRenderInfo);
        }
        else
        {
            VideoRenderInfo videoRenderInfo = new VideoRenderInfo();
            videoRenderInfo.setRederType(VideoWndType.local);
            videoRenderInfo.setUlMirrortype(0);
            tupCall.setMobileVideoRender(videoRenderInfo);
        }

        return true;
    }

    /**
     * 恢复视频采集
     * @param index
     * @param rotation
     * @return
     */
    public boolean setDisplayRotation(VideoWndType index, int rotation)
    {
        tupCall.setDisplayRotation(index, rotation);
        return true;
    }
}
