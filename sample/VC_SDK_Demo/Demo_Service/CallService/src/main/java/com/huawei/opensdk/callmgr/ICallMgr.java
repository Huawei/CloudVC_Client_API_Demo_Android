package com.huawei.opensdk.callmgr;


/**
 * This class is about UI call Call functions
 * UI回调函数
 */
public interface ICallMgr
{
    /**
     * Config call service param
     * 配置呼叫参数
     */
    void configCallServiceParam();

    /**
     * Register call module UI callback
     * @param callNotification
     */
    void regCallServiceNotification(ICallNotification callNotification);

    /**
     *
     * @return
     */
    int switchAudioRoute();

    /**
     *
     * @return
     */
    int getCurrentAudioRoute();

    /**
     *
     * @param callID
     * @return
     */
    CallConstant.CallStatus getCallStatus(int callID);

    /**
     * This method is used to access reserved conf
     * @param confId
     * @param accessCode
     * @param password
     * @param isVideo
     * @return
     */
    int accessReservedConf(String confId, String accessCode, String password, boolean isVideo);

    /**
     *
     * @param toNumber
     * @param isVideoCall
     * @return
     */
    int startCall(String toNumber, boolean isVideoCall);

    /**
     * This method is used to answer incoming call
     * @param callID
     * @param isVideo
     * @return true:success, false:failed
     */
    boolean answerCall(int callID, boolean isVideo);

    /**
     * This method is used to reject or hangup call
     * @param callID
     * @return true:success, false:failed
     */
    boolean endCall(int callID);

    /**
     * This method is used to divert incoming call
     * @param callID
     * @param divertNumber
     * @return true:success, false:failed
     */
    boolean divertCall(int callID, String divertNumber);

    /**
     * This method is used to blind transfer call
     * @param callID
     * @param transferNumber
     * @return true:success, false:failed
     */
    boolean blindTransfer(int callID, String transferNumber);

    /**
     * This method is used to hold call
     * @param callID
     * @return true:success, false:failed
     */
    boolean holdCall(int callID);

    /**
     *
     * @param callID
     * @return
     */
    boolean holdVideoCall(int callID);

    /**
     *
     * @param callID
     * @return
     */
    boolean unHoldCall(int callID);

    /**
     * This method is used to send DTMF tone
     * @param callID
     * @param code
     * @return true:success, false:failed
     */
    boolean reDial(int callID, int code);

    /**
     * This method is used to request change from an audio call to a video call
     * @param callID
     * @return true:success, false:failed
     */
    boolean addVideo(int callID);

    /**
     * This method is used to request a change from a video call to an audio call
     * @param callID
     * @return true:success, false:failed
     */
    boolean delVideo(int callID);

    /**
     * This method is used to reject change from an audio call to a video call
     * @param callID
     * @return true:success, false:failed
     */
    boolean rejectAddVideo(int callID);

    /**
     * This method is used to accept change from an audio call to a video call
     * @param callID
     * @return true:success, false:failed
     */
    boolean acceptAddVideo(int callID);

    /**
     * This method is used to set whether mute the microphone
     * @param callID
     * @param mute
     * @return true:success, false:failed
     */
    boolean muteMic(int callID, boolean mute);

    /**
     *
     * @param callID
     * @param mute
     * @return
     */
    boolean muteSpeak(int callID, boolean mute);

    /**
     *
     * @param callID
     * @param isClose
     */
    void switchLocalView(int callID, boolean isClose);

    /**
     *
     * @param callID
     * @param cameraIndex
     */
    void switchCamera(int callID, int cameraIndex);

    /**
     * This method is used to play ringing tone
     * @param ringingFile
     */
    void startPlayRingingTone(String ringingFile);

    /**
     * This method is used to stop play ringing tone
     */
    void stopPlayRingingTone();

    /**
     * This method is used to play ring back tone
     * @param ringingFile
     */
    void startPlayRingBackTone(String ringingFile);

    /**
     * This method is used to stop play ring back tone
     */
    void stopPlayRingBackTone();

}
