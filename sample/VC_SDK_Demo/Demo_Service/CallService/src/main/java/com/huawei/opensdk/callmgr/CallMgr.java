package com.huawei.opensdk.callmgr;

import android.text.TextUtils;
import android.util.Log;

import com.huawei.opensdk.sdkwrapper.login.LoginCenter;
import com.huawei.opensdk.sdkwrapper.manager.TupMgr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.TupCallBackBaseNotify;
import common.TupCallParam;
import object.Conf;
import object.KickOutInfo;
import object.TupRegisterResult;
import object.TupServiceRightCfg;
import tupsdk.TupCall;
import tupsdk.TupCallManager;

/**
 * This class is about call manager
 * 呼叫管理类
 */
public class CallMgr extends TupCallBackBaseNotify implements ICallMgr
{
    private static final String TAG = CallMgr.class.getSimpleName();
    /**
     * Single Case Call Management instance
     * 单例呼叫管理实例
     */
    private static final CallMgr mInstance = new CallMgr();

    /**
     * Call Session map collection  include call ID and call session
     * 呼叫会话集合  呼叫id和呼叫会话的集合
     */
    private Map<Integer, Session> callSessionMap = new HashMap<Integer, Session>();

    /**
     * UI callback
     * UI回调
     * */
    private ICallNotification mCallNotification;

    /**
     * Call Bell Sound handle
     * 呼叫铃音句柄
     */
    private int ringingToneHandle = -1;

    /**
     * Ring back tone handle
     * 回铃音句柄
     */
    private int ringBackToneHandle = -1;

    private CallMgr()
    {
    }

    public static CallMgr getInstance()
    {
        return mInstance;
    }

    /**
     * This method is used to store call session
     * @param session 会话信息
     */
    private void putCallSessionToMap(Session session)
    {
        callSessionMap.put(session.getCallID(), session);
    }

    /**
     * This method is used to remove call information
     * @param session 会话信息
     */
    private void removeCallSessionFromMap(Session session)
    {
        callSessionMap.remove(session.getCallID());
    }

    /**
     * This method is used to get call information by ID
     * @param callID 呼叫id
     * @return Session 会话信息
     */
    //TODO
    public Session getCallSessionByCallID(int callID)
    {
        return callSessionMap.get(callID);
    }

    /**
     * This method is used to Video Destroy.
     */
    public void videoDestroy()
    {
        //VideoDeviceManager.getInstance().destroyVideoWindow();
        VideoMgr.getInstance().clearCallVideo();
    }

    /**
     * This method is used to gets video device.
     * @return the video device
     */
    public VideoMgr getVideoDevice()
    {
        return VideoMgr.getInstance();
    }


    @Override
    public void regCallServiceNotification(ICallNotification callNotification)
    {
        this.mCallNotification = callNotification;
    }

    /**
     * This method is used to set the default audio output device
     * @param isVideoCall
     */
    public void setDefaultAudioRoute(boolean isVideoCall)
    {
        //获取移动音频路由设备
        int currentAudioRoute = TupMgr.getInstance().getCallManagerIns().getMobileAudioRoute();

        if (isVideoCall)
        {
            //如果当前是听筒，则切换默认设备为杨声器
            if (currentAudioRoute == TupCallParam.CALL_E_MOBILE_AUIDO_ROUTE.CALL_MOBILE_AUDIO_ROUTE_EARPIECE)
            {
                //This method is used to set mobile audio route
                //设置移动音频路由设备
                TupMgr.getInstance().getCallManagerIns().setMobileAudioRoute(TupCallParam.CALL_E_MOBILE_AUIDO_ROUTE.CALL_MOBILE_AUDIO_ROUTE_LOUDSPEAKER);
            }
        }
        else
        {
            //This method is used to set mobile audio route
            //设置移动音频路由设备
            TupMgr.getInstance().getCallManagerIns().setMobileAudioRoute(TupCallParam.CALL_E_MOBILE_AUIDO_ROUTE.CALL_MBOILE_AUDIO_ROUTE_DEFAULT);
        }
    }

    /**
     * This method is used to configure Call Parameters
     */
    @Override
    public void configCallServiceParam()
    {
        configAudioAndVideo();
        configSip();
        configMedia();
    }

    @Override
    public int switchAudioRoute()
    {
        //获取移动音频路由设备
        int audioRoute = getCurrentAudioRoute();
        Log.i(TAG, "audioRoute is" + audioRoute);

        if (audioRoute == TupCallParam.CALL_E_MOBILE_AUIDO_ROUTE.CALL_MOBILE_AUDIO_ROUTE_LOUDSPEAKER)
        {
            setAudioRoute(TupCallParam.CALL_E_MOBILE_AUIDO_ROUTE.CALL_MBOILE_AUDIO_ROUTE_DEFAULT);
            Log.i(TAG, "set telReceiver Success");
            return TupCallParam.CALL_E_MOBILE_AUIDO_ROUTE.CALL_MBOILE_AUDIO_ROUTE_DEFAULT;
        }
        else
        {
            //设置移动音频路由设备
            //set up a mobile audio routing device
            setAudioRoute(TupCallParam.CALL_E_MOBILE_AUIDO_ROUTE.CALL_MOBILE_AUDIO_ROUTE_LOUDSPEAKER);

            //设置扬声器输出音量大小
            //set speaker output Volume size
            int setMediaSpeakVolumeResult = TupMgr.getInstance().getCallManagerIns().mediaSetSpeakVolume(
                    TupCallParam.CALL_E_DEVICE_TYPE.CALL_E_CALL_DEVICE_SPEAK, 60);
            Log.i(TAG, "setMediaSpeakVolumeResult" + setMediaSpeakVolumeResult);
            return TupCallParam.CALL_E_MOBILE_AUIDO_ROUTE.CALL_MOBILE_AUDIO_ROUTE_LOUDSPEAKER;
        }
    }

    /**
     * This method is used to get mobile audio route
     * 获取移动音频路由设备
     * @return the audio route
     */
    @Override
    public int getCurrentAudioRoute()
    {
        return TupMgr.getInstance().getCallManagerIns().getMobileAudioRoute();
    }

    /**
     * This method is used to get speak volume of media.
     * 获取扬声器输出音量大小
     * @return the media speak volume
     */
    private int getMediaSpeakVolume()
    {
        int ret = TupMgr.getInstance().getCallManagerIns().mediaGetSpeakVolume();
        return ret;
    }

    /**
     * This method is used to get call status
     * 获取呼叫状态
     * @param callID  呼叫id
     * @return
     */
    @Override
    public CallConstant.CallStatus getCallStatus(int callID)
    {
        Session callSession = getCallSessionByCallID(callID);
        if (callSession == null)
        {
            return CallConstant.CallStatus.UNKNOWN;
        }

        return callSession.getCallStatus();
    }

    /**
     * This method is used to access reserved conf
     * 接入预约会议
     * @param confId  conference ID
     * @param accessCode reservation conference access code
     * @param password enrollment password
     * @param isVideo whether video access
     * @return int If it's success return call id, otherwise return 0
     */
    @Override
    public synchronized int accessReservedConf(String confId, String accessCode, String password, boolean isVideo)
    {
//        int solution = LoginCenter.getInstance().getSolution();

        if (LoginCenter.getInstance().getServerType() == LoginCenter.getInstance().LOGIN_E_SERVER_TYPE_SMC)
        {
            return startCall(accessCode + "*" + password + "#", isVideo);
        }
        else
        {
            int callType = isVideo ? 1 : 0;
            TupCall call = TupMgr.getInstance().getCallManagerIns().makeAccessReservedConfCall(callType, confId, accessCode, password);
            if (call != null)
            {
                Session newSession = new Session(call);
                putCallSessionToMap(newSession);

                setDefaultAudioRoute(isVideo);
                if (isVideo)
                {
                    newSession.initVideoWindow();
                }

                Log.i(TAG, "access reserved conf is success.");
                return call.getCallId();
            }
        }

        Log.e(TAG, "access reserved conf is failed.");
        return 0;
    }

    /**
     * This method is used to make call or make video call
     * 创建一个音频或者视频呼叫
     * @param toNumber  呼叫号码
     * @param isVideoCall  是否是视频
     * @return int 0 success
     */
    @Override
    public synchronized int startCall(String toNumber, boolean isVideoCall)
    {
        if (TextUtils.isEmpty(toNumber))
        {
            Log.e(TAG, "call number is null!");
            return 0;
        }

        //创建一路呼叫
        TupCall call = isVideoCall ? TupMgr.getInstance().getCallManagerIns().makeVideoCall(toNumber)
                : TupMgr.getInstance().getCallManagerIns().makeCall(toNumber);
        if (call != null)
        {
            Session newSession = new Session(call);
            putCallSessionToMap(newSession);

            setDefaultAudioRoute(isVideoCall);
            if (isVideoCall)
            {
                newSession.initVideoWindow();
            }

            Log.i(TAG, "make call is success.");
            return call.getCallId();
        }

        Log.e(TAG, "make call is failed.");
        return 0;
    }



    /**
     * This method is used to answer incoming call
     * 接听一路呼叫
     * @param callID 呼叫id
     * @param isVideo 是否是视频
     * @return true:success, false:failed
     */
    @Override
    public boolean answerCall(int callID, boolean isVideo)
    {
        Session callSession = getCallSessionByCallID(callID);
        if (callSession == null)
        {
            return false;
        }

        return callSession.answerCall(isVideo);
    }

    /**
     * This method is used to reject or hangup call
     * @param callID 呼叫id
     * @return true:success, false:failed
     */
    @Override
    public boolean endCall(int callID)
    {
        Session callSession = getCallSessionByCallID(callID);
        if (callSession == null)
        {
            return false;
        }

        return callSession.endCall();
    }

    /**
     * This method is used to divert incoming call
     * 发起偏转呼叫
     * @param callID 呼叫id
     * @param divertNumber 偏转号码
     * @return true:success, false:failed
     */
    @Override
    public boolean divertCall(int callID, String divertNumber)
    {
        Session callSession = getCallSessionByCallID(callID);
        if (callSession == null)
        {
            return false;
        }

        return callSession.divertCall(divertNumber);
    }

    /**
     * This method is used to blind transfer call
     * 发起盲转呼叫请求
     * @param callID 呼叫id
     * @param transferNumber 盲转号码
     * @return true:success, false:failed
     */
    @Override
    public boolean blindTransfer(int callID, String transferNumber)
    {
        Session callSession = getCallSessionByCallID(callID);
        if (callSession == null)
        {
            return false;
        }

        return callSession.blindTransfer(transferNumber);
    }

    /**
     * This method is used to hold call
     * 保持一路音频呼叫
     * @param callID 呼叫id
     * @return true:success, false:failed
     */
    @Override
    public boolean holdCall(int callID)
    {
        Session callSession = getCallSessionByCallID(callID);
        if (callSession == null)
        {
            return false;
        }

        return callSession.holdCall();
    }

    /**
     * This method is used to hold the video Call
     * 保持一路视频呼叫
     * @param callID 呼叫id
     * @return
     */
    @Override
    public boolean holdVideoCall(int callID)
    {
        Session callSession = getCallSessionByCallID(callID);
        if (callSession == null)
        {
            return false;
        }

        //视频保持先移除视频，待视频移除成功后，再保持
        boolean result = callSession.delVideo();
        if (result)
        {
            callSession.setVideoHold(true);
        }

        return result;
    }

    /**
     * This method is used to unhold call
     * 取消保持呼叫
     * @param callID 呼叫id
     * @return true:success, false:failed
     */
    @Override
    public boolean unHoldCall(int callID)
    {
        Session callSession = getCallSessionByCallID(callID);
        if (callSession == null)
        {
            return false;
        }

        return callSession.unHoldCall();
    }



    /**
     * This method is used to send DTMF tone
     * 二次拨号
     * @param callID 呼叫id
     * @param code （0到9，*为10,#为11）
     * @return true:success, false:failed
     */
    @Override
    public boolean reDial(int callID, int code)
    {
        Session callSession = getCallSessionByCallID(callID);
        if (callSession == null)
        {
            return false;
        }

        return callSession.reDial(code);
    }

    /**
     * This method is used to request change from an audio call to a video call
     * 音频转视频
     * @param callID 呼叫id
     * @return true:success, false:failed
     */
    @Override
    public boolean addVideo(int callID)
    {
        Session callSession = getCallSessionByCallID(callID);
        if (callSession == null)
        {
            return false;
        }

        return callSession.addVideo();
    }

    /**
     * This method is used to request a change from a video call to an audio call
     * 视频转音频
     * @param callID 呼叫id
     * @return true:success, false:failed
     */
    @Override
    public boolean delVideo(int callID)
    {
        Session callSession = getCallSessionByCallID(callID);
        if (callSession == null)
        {
            return false;
        }

        return callSession.delVideo();
    }

    /**
     * This method is used to reject change from an audio call to a video call
     * 拒绝音频转视频请求
     * @param callID 呼叫id
     * @return true:success, false:failed
     */
    @Override
    public boolean rejectAddVideo(int callID)
    {
        Session callSession = getCallSessionByCallID(callID);
        if (callSession == null)
        {
            return false;
        }

        return callSession.rejectAddVideo();
    }

    /**
     * This method is used to accept change from an audio call to a video call
     * 接受音频转视频请求
     * @param callID 呼叫id
     * @return true:success, false:failed
     */
    @Override
    public boolean acceptAddVideo(int callID)
    {
        Session callSession = getCallSessionByCallID(callID);
        if (callSession == null)
        {
            return false;
        }

        boolean result = callSession.acceptAddVideo();
        if (result)
        {
            setDefaultAudioRoute(true);
            callSession.setCallStatus(CallConstant.CallStatus.VIDEO_CALLING);

            CallInfo callInfo = getCallInfo(callSession.getTupCall());
            mCallNotification.onCallEventNotify(CallConstant.CallEvent.OPEN_VIDEO, callInfo);
        }

        return result;
    }


    /**
     * This method is used to set whether mute the microphone
     * 设置麦克风静音
     * @param callID 呼叫id
     * @param mute 是否静音
     * @return true:success, false:failed
     */
    @Override
    public boolean muteMic(int callID, boolean mute)
    {
        Session callSession = getCallSessionByCallID(callID);
        if (callSession == null)
        {
            return false;
        }

        return callSession.muteMic(mute);
    }

    /**
     * This method is used to set whether mute the speaker
     * 设置扬声器静音
     * @param callID 呼叫id
     * @param mute 是否静音
     * @return true:success, false:failed
     */
    @Override
    public boolean muteSpeak(int callID, boolean mute)
    {
        Session callSession = getCallSessionByCallID(callID);
        if (callSession == null)
        {
            return false;
        }

        return callSession.muteSpeak(mute);
    }

    /**
     * Local preview
     * @param callID
     * @param visible
     */
    @Override
    public void switchLocalView(int callID, boolean visible)
    {

    }

    /**
     * This method is used to switch camera
     * 切换摄像头
     * @param callID    call id
     * @param cameraIndex   camera subscript
     */
    @Override
    public void switchCamera(int callID, int cameraIndex)
    {
        VideoMgr.getInstance().switchCamera(callID, cameraIndex);

    }


    /**
     * This method is used to open camera
     * 打开摄像头
     * @param callID 呼叫id
     */
    public void openCamera(int callID) {
        Session callSession = getCallSessionByCallID(callID);
        if (callSession == null)
        {
            return;
        }

        VideoMgr.getInstance().openCamera(callSession);
    }

    /**
     * This method is used to close camera
     * 关闭摄像头
     * @param callID 呼叫id
     */
    public void closeCamera(int callID) {
        Session callSession = getCallSessionByCallID(callID);
        if (callSession == null)
        {
            return;
        }

        VideoMgr.getInstance().closeCamera(callSession);
    }

    /**
     * This method is used to play ringing tone
     * 播放铃音
     * @param ringingFile
     */
    @Override
    public void startPlayRingingTone(String ringingFile) {
        int result;
        TupCallManager callManager = TupMgr.getInstance().getCallManagerIns();

        //处理可能的异常
        if (ringingToneHandle != -1) {
            result = callManager.mediaStopplay(ringingToneHandle);
            if (result != 0) {
                Log.e(TAG, "mediaStopplay is return failed, result = " + result);
            }
        }

        //振铃默认使用扬声器播放
        //Ringing by default using speaker playback
        callManager.setMobileAudioRoute(TupCallParam.CALL_E_MOBILE_AUIDO_ROUTE.CALL_MOBILE_AUDIO_ROUTE_LOUDSPEAKER);

        //播放指定振铃
        //Play the specified ringing
        ringingToneHandle = callManager.mediaStartplay(0, ringingFile);
        if (ringingToneHandle == -1) {
            Log.e(TAG, "mediaStartplay is return failed.");
        }
    }

    /**
     * This method is used to stop play ringing tone
     * 停止播放铃音
     */
    @Override
    public void stopPlayRingingTone() {
        if (ringingToneHandle != -1) {
            TupCallManager callManager = TupMgr.getInstance().getCallManagerIns();
            int result = callManager.mediaStopplay(ringingToneHandle);
            if (result != 0) {
                Log.e(TAG, "mediaStopPlay is return failed, result = " + result);
            }
            ringingToneHandle = -1;
        }
    }

    /**
     * This method is used to play ring back tone
     * 播放回铃音
     * @param ringingFile
     */
    @Override
    public void startPlayRingBackTone(String ringingFile) {
        int result;
        TupCallManager callManager = TupMgr.getInstance().getCallManagerIns();

        //处理可能的异常
        if (ringBackToneHandle != -1) {
            result = callManager.mediaStopplay(ringBackToneHandle);
            if (result != 0) {
                Log.e(TAG, "mediaStopPlay is return failed, result = " + result);
            }
        }

        //回铃音使用默认设备播放
        //Ring tone Use default device playback
        callManager.setMobileAudioRoute(TupCallParam.CALL_E_MOBILE_AUIDO_ROUTE.CALL_MBOILE_AUDIO_ROUTE_DEFAULT);

        //播放指定回铃音
        //Play the specified ring tone
        ringBackToneHandle = callManager.mediaStartplay(0, ringingFile);
        if (ringBackToneHandle == -1) {
            Log.e(TAG, "mediaStartPlay is return failed.");
        }
    }

    /**
     * This method is used to stop play ring back tone
     * 停止播放回铃音
     */
    @Override
    public void stopPlayRingBackTone() {
        if (ringBackToneHandle != -1) {
            TupCallManager callManager = TupMgr.getInstance().getCallManagerIns();
            int result = callManager.mediaStopplay(ringBackToneHandle);
            if (result != 0) {
                Log.e(TAG, "mediaStopPlay is return failed, result = " + result);
            }
            ringBackToneHandle = -1;
        }
    }

    /**
     * This method is used to get call information
     * @param callID
     * @return
     */
    public CallInfo getCallInfo(int callID)
    {
        Session callSession = getCallSessionByCallID(callID);
        if (callSession == null)
        {
            return null;
        }

        return getCallInfo(callSession.getTupCall());
    }

    /**
     * This method is used to handle the call information
     * 保持一路通话信息
     * @param tupCall
     */
    private void handleCallComing(TupCall tupCall){

        //直接通知对方本方正在振铃
        //Directly notify the other party is ringing
        tupCall.alertingCall();

        Session newSession = new Session(tupCall);
        putCallSessionToMap(newSession);

        CallInfo callInfo = getCallInfo(tupCall);

        mCallNotification.onCallEventNotify(CallConstant.CallEvent.CALL_COMING, callInfo);

    }

    /**
     * This method is used to handle call connected
     * 保持一路通话接通
     * @param tupCall
     */
    private void handleCallConnected(TupCall tupCall)
    {
        CallInfo callInfo = getCallInfo(tupCall);
        Session callSession = getCallSessionByCallID(tupCall.getCallId());
        if (callSession == null)
        {
            Log.e(TAG, "call session obj is null");
            return;
        }

        if (callInfo.isVideoCall())
        {
            callSession.setCallStatus(CallConstant.CallStatus.VIDEO_CALLING);
        }
        else
        {
            callSession.setCallStatus(CallConstant.CallStatus.AUDIO_CALLING);
        }

        mCallNotification.onCallEventNotify(CallConstant.CallEvent.CALL_CONNECTED, callInfo);

    }

    /**
     * This method is used to get call information
     * @param call
     * @return
     */
    private CallInfo getCallInfo(TupCall call)
    {
        String peerNumber;
        String peerDisplayName;
        boolean isFocus = false;
        boolean isVideoCall = false;
        boolean isCaller = call.isCaller();

        if (isCaller) {
            peerNumber = call.getToNumber();
        }
        else {
            peerNumber = call.getTelNumber();
        }

        peerDisplayName = call.getFromDisplayName();

        if (call.getIsFocus() == 1) {
            isFocus = true;
        }
        else {
            if (call.getConfMediaType() != 0) {
                isFocus = true;
            }
        }

        if (call.getCallType() == TupCallParam.CALL_E_CALL_TYPE.TUP_CALLTYPE_VIDEO) {
            isVideoCall = true;
        }

        return new CallInfo.Builder()
                .setCallID(call.getCallId())
                .setConfID(call.getServerConfID())
                .setPeerNumber(peerNumber)
                .setPeerDisplayName(peerDisplayName)
                .setVideoCall(isVideoCall)
                .setFocus(isFocus)
                .setCaller(isCaller)
                .setReasonCode(call.getReasonCode())
                .build();
    }

    /**
     * This method is used to handle call add video.
     * 保持一路音频转视频
     * @param call the call
     */
    private void handleCallAddVideo(TupCall call)
    {
        Session callSession = getCallSessionByCallID(call.getCallId());
        if (callSession == null)
        {
            Log.e(TAG, "call session obj is null");
            return;
        }

        CallConstant.CallStatus callStatus = callSession.getCallStatus();
        boolean isSupportVideo = isSupportVideo();

        if ((!isSupportVideo) || (CallConstant.CallStatus.AUDIO_CALLING != callStatus))
        {
            callSession.rejectAddVideo();
            return;
        }

        mCallNotification.onCallEventNotify(CallConstant.CallEvent.RECEIVED_REMOTE_ADD_VIDEO_REQUEST, null);
    }


    /**
     * Handle call delete video.
     * @param call the call
     */
    private void handleCallDeleteVideo(TupCall call)
    {
        //删除视频不可以拒绝
        //Removing video cannot be denied
        call.replyDelVideo(1);

        Session callSession = getCallSessionByCallID(call.getCallId());
        if (callSession == null)
        {
            Log.e(TAG, "call session obj is null");
            return;
        }

        if (callSession.getCallStatus() != CallConstant.CallStatus.VIDEO_CALLING)
        {
            return;
        }
        callSession.setCallStatus(CallConstant.CallStatus.AUDIO_CALLING);

        //Clear video data
        //VideoDeviceManager.getInstance().clearCallVideo();
        VideoMgr.getInstance().clearCallVideo();

        if (null != mCallNotification)
        {
            CallInfo callInfo = getCallInfo(call);
            mCallNotification.onCallEventNotify(CallConstant.CallEvent.CLOSE_VIDEO, callInfo);
        }
    }

    /**
     * This method is used to audio and video conversion
     * 修改视频结果
     * @param call
     */
    private void handleCallVideoModifyResult(TupCall call)
    {
        int result  = call.getModifyVideoResult(); // 0:success, other:failed
        int isVideo = call.getIsviedo(); // 1:video, 0: audio
        int callId  = call.getCallId();
        Log.i(TAG, "result: " + result + "isVideo: " + isVideo + "callId: " + callId);

        Session callSession = getCallSessionByCallID(callId);
        if (callSession == null)
        {
            return;
        }

        CallInfo callInfo = getCallInfo(call);

        //modify success
        if (result == 0)
        {
            //video --> audio success
            if (isVideo == 0)
            {
                Log.i(TAG, "Video to audio Call");
                //VideoDeviceManager.getInstance().clearCallVideo();
                VideoMgr.getInstance().clearCallVideo();

                callSession.setCallStatus(CallConstant.CallStatus.AUDIO_CALLING);
                mCallNotification.onCallEventNotify(CallConstant.CallEvent.CLOSE_VIDEO, callInfo);

                //如果是由“视频保持”而发起的“视频”转“音频”，则在转音频成功后，再“保持”呼叫
                //If video "Audio" is initiated by "video retention", then "keep" the call after the audio is successful.
                if (callSession.isVideoHold())
                {
                    callSession.holdCall();
                }
            }
            //audio --> video success
            else
            {
                Log.i(TAG, "Upgrade To Video Call");
                //VideoDeviceManager.getInstance().setVideoOrient(callId, Constant.VideoDevice.FRONT_CAMERA);
                VideoMgr.getInstance().setVideoOrient(callId, CallConstant.FRONT_CAMERA);

                callSession.setCallStatus(CallConstant.CallStatus.VIDEO_CALLING);
                mCallNotification.onCallEventNotify(CallConstant.CallEvent.OPEN_VIDEO, callInfo);
            }
        }
        //modify failed, remote refuse
        else {
            //remote refuse audio --> video
            if (isVideo == 0)
            {
                //TODO
                //VideoDeviceManager.getInstance().setVideoOrient(callId, Constant.VideoDevice.FRONT_CAMERA);
                VideoMgr.getInstance().clearCallVideo();
                callSession.setCallStatus(CallConstant.CallStatus.AUDIO_CALLING);
                mCallNotification.onCallEventNotify(CallConstant.CallEvent.REMOTE_REFUSE_ADD_VIDEO_SREQUEST, callInfo);
            }
            //remote refuse video --> audio
            else
            {
                //The scene does not exist, and the remote cannot reject it.
            }
        }
    }


    /**
     * This method is used to refresh local view.
     * 刷新本地窗口
     * @param call the call
     */
    private void handleRefreshLocalView(TupCall call)
    {
        Log.i(TAG, "refreshLocalView");
        int mediaType = call.getMediaType();
        int eventType = call.getEvent();
        int callId = call.getCallId();

        switch (mediaType)
        {
            case 1: //local video preview
            case 2: //general video
                if (eventType == 1) //add local view
                {
                    //VideoDeviceManager.getInstance().refreshLocalVideo(true, callId);
                    mCallNotification.onCallEventNotify(CallConstant.CallEvent.ADD_LOCAL_VIEW, callId);
                }
                else //remove local view
                {
                    //VideoDeviceManager.getInstance().refreshLocalVideo(false, callId);
                    mCallNotification.onCallEventNotify(CallConstant.CallEvent.DEL_LOCAL_VIEW, callId);
                }
                break;

            case 3: //auxiliary data
                break;

            default:
                break;
        }
    }

    private void configAudioAndVideo()
    {
        //大部分参数使用SDKWarpper中的默认配置，有需要可采用如下样例修改
        //TupCallCfgAudioVideo tpCllCfgAdVd = TupMgr.getInstance().getTupCallCfgAudioVideo();
        //tpCllCfgAdVd.setAudioAec(1);
        //TupMgr.getInstance().getCallManagerIns().setCfgAudioAndVideo(tpCllCfgAdVd);
    }

    private void configSip()
    {
        //大部分参数使用SDKWarpper中的默认配置，有需要可采用如下样例修改
        //TupCallCfgSIP tupCallCfgSIP = TupMgr.getInstance().getTupCallCfgSIP();

        //TupMgr.getInstance().getCallManagerIns().setCfgSIP(tupCallCfgSIP);
    }

    private void configMedia()
    {
        //大部分参数使用SDKWarpper中的默认配置，有需要可采用如下样例修改
        //TupCallCfgMedia tupCallCfgMedia = TupMgr.getInstance().getTupCallCfgMedia();
        //tupCallCfgMedia.setEnableBFCP(TupBool.TUP_FALSE);
        //TupMgr.getInstance().getCallManagerIns().setCfgMedia(tupCallCfgMedia);
    }

    private void configCfgAccount()
    {
        //大部分参数使用SDKWarpper中的默认配置，有需要可采用如下样例修改
        //TupCallCfgAccount tupCallCfgAccount = new TupCallCfgAccount();
        //tupCallCfgAccount.setauthPasswordType(LoginCenter.getInstance().getSipAccountInfo().getSipAuthPasswordType());
        //TupMgr.getInstance().getCallManagerIns().setCfgAccount(tupCallCfgAccount);
    }

    /**
     * This method is used to sets audio route.
     * @param audioSwitch the audio switch
     * @return the audio route
     */
    private boolean setAudioRoute(int audioSwitch)
    {
        return TupMgr.getInstance().getCallManagerIns().setMobileAudioRoute(audioSwitch) == 0;
    }


    /**
     * This method is used to support video.
     * @return the boolean
     */
    private boolean isSupportVideo()
    {
        return VideoMgr.getInstance().isSupportVideo();
    }


    /***************************************************CALL BACK***************************************************/

    @Override
    public void onCallAddVideo(TupCall tupCall)
    {
        Log.i(TAG, "onCallAddVideo");
        if (null == tupCall)
        {
            Log.e(TAG, "onCallAddVideo tupCall is null");
            return;
        }
        //音频转视频
        handleCallAddVideo(tupCall);
    }

    @Override
    public void onCallDelVideo(TupCall tupCall)
    {
        if (null == tupCall)
        {
            Log.e(TAG, "onCallDelVideo tupCall is null");
            return;
        }

        //视频转音频
        handleCallDeleteVideo(tupCall);
    }

    @Override
    public void onCallViedoResult(TupCall call)
    {
        Log.d(TAG, "onCallViedoResult");
        if (null == call)
        {
            Log.e(TAG, "onCallViedoResult call is null");
            return;
        }
        //音视频转换结果
        handleCallVideoModifyResult(call);
    }

    @Override
    public void onSessionModified(TupCall tupCall)
    {
        //TODO
        //这个消息应该是一个很重要的消息,如何处理待确认


        Log.d(TAG, "onSessionModified");
        Log.i(TAG, "conf id:" + tupCall.getServerConfID());
        if (!TextUtils.isEmpty(tupCall.getServerConfID()))
        {
            mCallNotification.onCallEventNotify(CallConstant.CallEvent.SESSION_MODIFIED, tupCall);
        }
    }

    @Override
    public void onCallRefreshView(TupCall tupCall)
    {
        if (null == tupCall)
        {
            Log.e(TAG, "onCallRefreshView tupCall is null");
            return;
        }
        //刷新本地窗口
        handleRefreshLocalView(tupCall);
    }

    @Override
    public void onConfNotify(int code, Conf conf)
    {
        Log.i(TAG, "onConfNotify eventType->" + code);

        if (TupCallParam.CallEvent.CALL_E_EVT_SERVERCONF_DATACONF_PARAM == code)
        {
            mCallNotification.onCallEventNotify(CallConstant.CallEvent.DATACONF_INFO_NOTIFY, conf);
        }
    }


    @Override
    public void onCallHoldSuccess(TupCall tupCall)
    {
        Session callSession = getCallSessionByCallID(tupCall.getCallId());

        CallInfo callInfo = getCallInfo(tupCall);
        if (callSession.isVideoHold())
        {
            mCallNotification.onCallEventNotify(CallConstant.CallEvent.VIDEO_HOLD_SUCCESS, callInfo);
        }
        else
        {
            mCallNotification.onCallEventNotify(CallConstant.CallEvent.AUDIO_HOLD_SUCCESS, callInfo);
        }

    }

    @Override
    public void onCallHoldFailed(TupCall tupCall)
    {
        Session callSession = getCallSessionByCallID(tupCall.getCallId());

        CallInfo callInfo = getCallInfo(tupCall);
        if (callSession.isVideoHold())
        {
            callSession.setVideoHold(false);

            //保持失败，只直接通知UI失败，不自动动恢复视频
            mCallNotification.onCallEventNotify(CallConstant.CallEvent.VIDEO_HOLD_FAILED, callInfo);
        }
        else
        {
            mCallNotification.onCallEventNotify(CallConstant.CallEvent.AUDIO_HOLD_FAILED, callInfo);
        }
    }

    @Override
    public void onCallUnHoldSuccess(TupCall tupCall)
    {
        Log.d(TAG, "onCallUnHoldSuccess");

        Session callSession = getCallSessionByCallID(tupCall.getCallId());
        if (callSession == null)
        {
            Log.e(TAG, "call session obj is null");
            return;
        }

        //如果此保持发起时是“视频保持”，则再在“保持恢复”后，请求远端“增加视频”
        if (callSession.isVideoHold())
        {
            addVideo(tupCall.getCallId());
            callSession.setVideoHold(false);
        }

        CallInfo callInfo = getCallInfo(tupCall);
        mCallNotification.onCallEventNotify(CallConstant.CallEvent.UN_HOLD_SUCCESS, callInfo);

    }

    @Override
    public void onReferNotify(int i)
    {
        //mTupCall.setCallId(i);
    }

    @Override
    public void onCallBldTransferRecvSucRsp(TupCall tupCall)
    {
        Log.d(TAG, "onCallUnHoldSuccess");

        Session callSession = getCallSessionByCallID(tupCall.getCallId());
        if (callSession == null)
        {
            Log.e(TAG, "call session obj is null");
            return;
        }

        callSession.endCall();
    }


    /**
     * This method is used to set value for ipt
     * @param list service right info lists
     */
    @Override
    public void onServiceRightCfg(List<TupServiceRightCfg> list)
    {
        //设置ipt权限值
        //TODO
    }

    /**
     * This method is used to register ipt success
     * @param serviceCallType type of call service
     */
    @Override
    public void onSetIptServiceSuc(int serviceCallType)
    {

    }

    /**
     * This method is used to register ipt fail
     * @param serviceCallType type of call service
     */
    @Override
    public void onSetIptServiceFal(int serviceCallType)
    {

    }

    @Override
    public void onRegisterResult(TupRegisterResult tupRegisterResult)
    {
        Log.d(TAG, "onRegisterResult");
        //相关处理已由LoginCenter完成，此接口中无需再作处理
    }

    @Override
    public void onBeKickedOut(KickOutInfo kickOutInfo)
    {
        Log.d(TAG, "onBeKickedOut");
        //相关处理已由LoginCenter完成，此接口中无需再作处理
    }

    @Override
    public void onCallComing(TupCall tupCall)
    {
        Log.i(TAG, "onCallComing");
        if (null == tupCall)
        {
            Log.e(TAG, "onCallComing call is null");
            return;
        }

        //保持一路呼叫信息
        handleCallComing(tupCall);
    }

    @Override
    public void onCallRingBack(TupCall tupCall)
    {
        Log.i(TAG, "onCallRingBack");
        if (null == tupCall)
        {
            Log.e(TAG, "onCallRingBack call is null");
            return;
        }

        int haveSDP = tupCall.getHaveSDP();
        Log.i(TAG, "haveSDP->" + haveSDP);
        if (haveSDP != 1)
        {
            if (null != mCallNotification)
            {
                mCallNotification.onCallEventNotify(CallConstant.CallEvent.PLAY_RING_BACK_TONE, null);
            }
        }
    }

    @Override
    public void onCallGoing(TupCall tupCall)
    {
        Log.i(TAG, "onCallGoing");
        if (null == tupCall)
        {
            Log.e(TAG, "tupCall obj is null");
            return;
        }

        CallInfo callInfo = getCallInfo(tupCall);

        mCallNotification.onCallEventNotify(CallConstant.CallEvent.CALL_GOING, callInfo);
    }

    @Override
    public void onCallRTPCreated(TupCall tupCall)
    {
        Log.i(TAG, "onCallRTPCreated");
        if (null == tupCall)
        {
            Log.e(TAG, "tupCall obj is null");
            return;
        }

        CallInfo callInfo = getCallInfo(tupCall);

        mCallNotification.onCallEventNotify(CallConstant.CallEvent.RTP_CREATED, callInfo);
    }

    @Override
    public void onCallConnected(TupCall call)
    {
        Log.i(TAG, "onCallConnected");
        if (null == call)
        {
            Log.e(TAG, "call obj is null");
            return;
        }

        //保持一路通话连接
        handleCallConnected(call);
    }

    @Override
    public void onCallEnded(TupCall call)
    {
        Log.i(TAG, "onCallEnded");
        if (null == call)
        {
            Log.e(TAG, "onCallEnded call is null");
            return;
        }

        CallInfo callInfo = getCallInfo(call);
        //TODO
        mCallNotification.onCallEventNotify(CallConstant.CallEvent.CALL_ENDED, callInfo);
    }

    @Override
    public void onCallDestroy(TupCall call)
    {
        Log.i(TAG, "onCallDestroy");
        if (null == call)
        {
            Log.e(TAG, "call obj is null");
            return;
        }

        Session callSession = getCallSessionByCallID(call.getCallId());
        if (callSession == null)
        {
            Log.e(TAG, "call session obj is null");
            return;
        }

        //从会话列表中移除一路会话
        removeCallSessionFromMap(callSession);
    }

    @Override
    public void onIdoOverBFCPSupport(int callId, int idoOverBFCP) {

        Conf conf = new Conf();
        conf.setCallId(callId);
        conf.setConfId(callId);
        mCallNotification.onCallEventNotify(CallConstant.CallEvent.CONF_INFO_NOTIFY, conf);
    }

}
