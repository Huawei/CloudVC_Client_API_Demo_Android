package com.huawei.opensdk.ec_sdk_demo.logic.conference.mvp;


import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.huawei.opensdk.callmgr.CallConstant;
import com.huawei.opensdk.callmgr.CallMgr;
import com.huawei.opensdk.callmgr.VideoMgr;
import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.demoservice.MeetingMgr;

public class VideoConfPresenter extends VideoConfBasePresenter
{
    private static final int ADD_LOCAL_VIEW = 101;

    private int mCameraIndex = CallConstant.FRONT_CAMERA;

    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case ADD_LOCAL_VIEW:
                    //setVideoContainer(false);
                    getView().updateLocalVideo();
                    break;

                default:
                    break;
            }
        }
    };

    public VideoConfPresenter()
    {
        broadcastNames = new String[]{CustomBroadcastConstants.CONF_STATE_UPDATE,
                CustomBroadcastConstants.ADD_LOCAL_VIEW,
                CustomBroadcastConstants.DEL_LOCAL_VIEW,
                CustomBroadcastConstants.GET_CONF_END};
    }

    @Override
    protected void onBroadcastReceive(String broadcastName, Object obj)
    {
        switch (broadcastName)
        {
            case CustomBroadcastConstants.CONF_STATE_UPDATE:
                break;

            case CustomBroadcastConstants.ADD_LOCAL_VIEW:
                mHandler.sendEmptyMessage(ADD_LOCAL_VIEW);
                break;

            case CustomBroadcastConstants.DEL_LOCAL_VIEW:
                break;

            case CustomBroadcastConstants.GET_CONF_END:
                getView().finishActivity();
                break;

            default:
                break;
        }
    }

    @Override
    public void switchCamera()
    {
        int callID = MeetingMgr.getInstance().getCurrentConferenceCallID();
        if (callID == 0) {
            return;
        }

        mCameraIndex = CallConstant.FRONT_CAMERA == mCameraIndex ?
                CallConstant.BACK_CAMERA : CallConstant.FRONT_CAMERA;

        CallMgr.getInstance().switchCamera(callID, mCameraIndex);
    }

    @Override
    public void setVideoContainer(Context context, ViewGroup smallLayout, ViewGroup bigLayout, ViewGroup hideLayout)
    {
        //TODO
        //VideoDeviceManager.getInstance().addRenderToContain((FrameLayout) smallLayout, (FrameLayout) bigLayout);
        if (bigLayout != null) {
            addSurfaceView(bigLayout, getRemoteVideoView());
        }

        if (smallLayout != null) {
            addSurfaceView(smallLayout, getLocalVideoView());
        }

        if (hideLayout != null) {
            addSurfaceView(hideLayout, getHideVideoView());
        }
    }

    @Override
    public void setAutoRotation(Object object, boolean isOpen) {
        VideoMgr.getInstance().setAutoRotation(object, isOpen, 2);
    }


    @Override
    public void attachRemoteVideo(long userID, long deviceID)
    {
        //do nothing
    }

    @Override
    public void shareSelfVideo(long deviceID)
    {
        //do nothing
    }

    @Override
    public void leaveVideo()
    {
        //do nothing
    }

    @Override
    public void changeLocalVideoVisible(boolean visible)
    {
        if (visible) {
            //重新显示本地窗口，无需再打开本地视频
            getLocalVideoView().setVisibility(View.VISIBLE);
        } else {
            //只隐藏本地窗口，并不关闭本地视频
            getLocalVideoView().setVisibility(View.GONE);
        }
//        int callID = MeetingMgr.getInstance().getCurrentConferenceCallID();
//        if (callID == 0) {
//            return;
//        }
//
//        CallMgr.getInstance().switchLocalView(callID, visible);
    }

    @Override
    public boolean closeOrOpenLocalVideo(boolean close)
    {
        int callID = MeetingMgr.getInstance().getCurrentConferenceCallID();
        if (callID == 0) {
            return false;
        }

        if (close) {
            CallMgr.getInstance().closeCamera(callID);
        } else {
            CallMgr.getInstance().openCamera(callID);
            VideoMgr.getInstance().setVideoOrient(callID, CallConstant.FRONT_CAMERA);
        }

        return true;
    }

    @Override
    public SurfaceView getHideVideoView()
    {
        return VideoMgr.getInstance().getLocalHideView();
    }

    @Override
    public SurfaceView getLocalVideoView()
    {
        return VideoMgr.getInstance().getLocalVideoView();
    }

    @Override
    public SurfaceView getRemoteVideoView()
    {
        return VideoMgr.getInstance().getRemoteVideoView();
    }

    private void addSurfaceView(ViewGroup container, SurfaceView child)
    {
        if (child == null)
        {
            return;
        }
        if (child.getParent() != null)
        {
            ViewGroup vGroup = (ViewGroup) child.getParent();
            vGroup.removeAllViews();
        }
        container.addView(child);
    }

}
