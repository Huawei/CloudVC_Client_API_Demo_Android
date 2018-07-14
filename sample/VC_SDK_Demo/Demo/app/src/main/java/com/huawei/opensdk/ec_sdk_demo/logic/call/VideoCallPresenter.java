package com.huawei.opensdk.ec_sdk_demo.logic.call;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.SurfaceView;

import com.huawei.opensdk.callmgr.CallConstant;
import com.huawei.opensdk.callmgr.CallInfo;
import com.huawei.opensdk.callmgr.CallMgr;
import com.huawei.opensdk.callmgr.VideoMgr;
import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.ui.base.MVPBasePresenter;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class VideoCallPresenter extends MVPBasePresenter<IVideoCallContract.VideoCallBaseView>
        implements IVideoCallContract.VideoCallBaserPresenter {
    private final CallMgr mCallMgr;
    private final Context mContext;
    private final CallFunc mCallFunc;

    private int mCallID;

    private int mAudioRoute;
    private int mCallType;
    private String mOppositeName;
    private String mOppositeNumber;

    private int mCameraIndex = CallConstant.FRONT_CAMERA;

    private ScheduledExecutorService mService;
    private int mAutoTime = 0;
    private static final int TIME_UPDATE = 100;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TIME_UPDATE:

                    getView().setTime(formatTimeFString(mAutoTime));
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void removeCallback() {
        mHandler.removeCallbacksAndMessages(null);
    }

    public VideoCallPresenter(Context context) {
        this.mContext = context;
        mCallMgr = CallMgr.getInstance();
        mCallFunc = CallFunc.getInstance();
    }

    public void switchLocalView(boolean visible) {
//        CallMgr.getInstance().switchLocalView(mCallID, visible);
    }

    /**
     * formatTimeFString
     *
     * @param longTime
     * @return String
     */
    private String formatTimeFString(long longTime) {
        String time = "%2d:%2d:%2d";
        int hour = parseLongToInt(longTime / (60 * 60));
        int min = parseLongToInt((longTime - hour * (60L * 60)) / 60);
        int sec = parseLongToInt(longTime % 60);
        time = String.format(time, hour, min, sec);

        return time.replace(' ', '0');
    }

    private int parseLongToInt(long value) {
        return Long.valueOf(value).intValue();
    }

    @Override
    public void setCurrentCallInfo(CallInfo callInfo) {
        mOppositeName = callInfo.getPeerDisplayName();
        mOppositeNumber = callInfo.getPeerNumber();
        mAudioRoute = mCallMgr.getCurrentAudioRoute();
        mCallID = callInfo.getCallID();
    }

    @Override
    public void endCall() {
        mCallMgr.endCall(mCallID);
    }

    @Override
    public String getOppositeNumber() {
        if (TextUtils.isEmpty(mOppositeNumber)) {
            return mContext.getString(R.string.unknown);
        }
        return mOppositeNumber;
    }

    @Override
    public boolean getIsSpeakerRoute() {
        return mAudioRoute == CallConstant.TYPE_LOUD_SPEAKER;
    }

    @Override
    public int getCallType() {
        return mCallType;
    }

    @Override
    public String getOppositeName() {
        if (TextUtils.isEmpty(mOppositeName)) {
            return mContext.getString(R.string.unknown);
        }
        return mOppositeName;
    }

    @Override
    public void executorShutDown() {
        if (null != mService) {
            mService.shutdown();
            mService = null;
        }
    }

    @Override
    public void muteCall() {
        boolean currentMuteStatus = mCallFunc.isMuteStatus();
        if (CallMgr.getInstance().muteMic(mCallID, !currentMuteStatus)) {
            mCallFunc.setMuteStatus(!currentMuteStatus);
            getView().switchMuteBtn(currentMuteStatus);
        }
    }

    @Override
    public int switchAudioRoute() {
        return CallMgr.getInstance().switchAudioRoute();
    }

    @Override
    public void videoToAudio() {
        CallMgr.getInstance().delVideo(mCallID);
    }

    @Override
    public void holdVideo() {
        CallMgr.getInstance().holdVideoCall(mCallID);
    }

    @Override
    public void videoDestroy() {
        if (null != CallMgr.getInstance().getVideoDevice()) {
            LogUtil.i(UIConstants.DEMO_TAG, "onCallClosed destroy.");
            CallMgr.getInstance().videoDestroy();
        }
    }

    @Override
    public void switchCamera() {
        mCameraIndex = CallConstant.FRONT_CAMERA == mCameraIndex ?
                CallConstant.BACK_CAMERA : CallConstant.FRONT_CAMERA;
        CallMgr.getInstance().switchCamera(mCallID, mCameraIndex);
    }

    @Override
    public void switchCameraStatus(boolean isCameraClose) {
        if (isCameraClose) {
            CallMgr.getInstance().closeCamera(mCallID);
        } else {
            CallMgr.getInstance().openCamera(mCallID);
        }
    }

    @Override
    public SurfaceView getHideVideoView() {
        return VideoMgr.getInstance().getLocalHideView();
    }

    @Override
    public SurfaceView getLocalVideoView() {
        return VideoMgr.getInstance().getLocalVideoView();
    }

    @Override
    public SurfaceView getRemoteVideoView() {
        return VideoMgr.getInstance().getRemoteVideoView();
    }

    @Override
    public void setAutoRotation(Object object, boolean isOpen) {
        VideoMgr.getInstance().setAutoRotation(object, isOpen, 1);
    }

    /**
     * startTimer
     */
    public void startTimer() {
        if (null == mService) {
            mService = Executors.newScheduledThreadPool(1);
        }
        mService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                ++mAutoTime;
                mHandler.sendEmptyMessage(TIME_UPDATE);
            }
        }, 1, 1, TimeUnit.SECONDS);
    }
}
