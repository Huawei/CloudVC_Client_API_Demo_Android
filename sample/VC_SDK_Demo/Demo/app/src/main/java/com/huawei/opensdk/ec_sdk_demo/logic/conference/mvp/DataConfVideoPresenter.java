package com.huawei.opensdk.ec_sdk_demo.logic.conference.mvp;

import android.content.Context;
import android.os.Handler;
import android.view.SurfaceView;
import android.view.ViewGroup;

import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.demoservice.MeetingMgr;
import com.huawei.opensdk.demoservice.data.CameraEntity;
import com.huawei.opensdk.demoservice.data.callBackWapper.VideoSwitchInfoWapper;


public class DataConfVideoPresenter extends VideoConfBasePresenter
{

    private Handler handler = new Handler();

    private boolean first = true;

    private long currentRemoteVideoUserID;
    private long currentRemoteVideoDeviceID;


    public DataConfVideoPresenter()
    {
        broadcastNames = new String[]{CustomBroadcastConstants.CONF_STATE_UPDATE,
                CustomBroadcastConstants.DATA_CONFERENCE_CAMERA_STATUS_UPDATE};
    }

    @Override
    protected void onBroadcastReceive(String broadcastName, Object obj)
    {
        switch (broadcastName)
        {
            case CustomBroadcastConstants.DATA_CONFERENCE_CAMERA_STATUS_UPDATE:
                updateVideo((VideoSwitchInfoWapper)obj);
                break;
            default:
                break;
        }
    }


    private void updateVideo(final VideoSwitchInfoWapper switchInfoWapper)
    {
        if (first)
        {
            first = false;
            return;
        }
        if (switchInfoWapper.getStatus() != CameraEntity.CAMERA_STATUS_OPENED)
        {
            return;
        }

        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                MeetingMgr.getInstance().attachVideo(switchInfoWapper.getUserID(), switchInfoWapper.getDeviceID());
            }
        });

    }

    private void updateCameraInfo(VideoSwitchInfoWapper videoSwitchInfo)
    {
//        ConferenceMemberEntity conferenceMemberEntity = conferenceEntity.queryMemberEntityByNumber(videoSwitchInfo.getUserID() + "");
//        if (conferenceMemberEntity == null)
//        {
//            LogUtil.i(Constant.DEMO_TAG,  "save device info failed: can't find conf member ");
//            return;
//        }
//        conferenceMemberEntity.updateCamera(videoSwitchInfo.getDeviceID(), videoSwitchInfo.getStatus());
    }

    @Override
    public void switchCamera()
    {
        MeetingMgr.getInstance().switchCamera();
    }


    @Override
    public void setVideoContainer(Context context, ViewGroup smallLayout, ViewGroup bigLayout, ViewGroup hideLayout)
    {
        MeetingMgr.getInstance().setVideoContainer(context, smallLayout, bigLayout);
    }

    @Override
    public void setAutoRotation(Object object, boolean isOpen) {
        return;
    }


    @Override
    public void attachRemoteVideo(long userID, long deviceID)
    {
        if (currentRemoteVideoUserID != 0)
        {
            MeetingMgr.getInstance().detachRemoteVideo(currentRemoteVideoUserID, currentRemoteVideoDeviceID);
        }

        boolean result = MeetingMgr.getInstance().attachRemoteVideo(userID, deviceID);
        if (result == true)
        {
            currentRemoteVideoUserID = userID;
            currentRemoteVideoDeviceID = deviceID;
        }
    }

    @Override
    public void shareSelfVideo(long deviceID)
    {
        MeetingMgr.getInstance().videoOpen(deviceID);
    }

    @Override
    public void leaveVideo()
    {
        MeetingMgr.getInstance().leaveVideo();
    }

    @Override
    public void changeLocalVideoVisible(boolean visible)
    {
        MeetingMgr.getInstance().changeLocalVideoVisible(visible);
    }

    @Override
    public boolean closeOrOpenLocalVideo(boolean close)
    {
        if (close) {
            return MeetingMgr.getInstance().closeLocalVideo();
        } else {
            return MeetingMgr.getInstance().openLocalVideo();
        }
    }

    @Override
    public SurfaceView getHideVideoView()
    {
        return null;//VideoMgr.getInstance().getLocalHideView();
    }

    @Override
    public SurfaceView getLocalVideoView()
    {
        return null;//VideoMgr.getInstance().getLocalVideoView();
    }

    @Override
    public SurfaceView getRemoteVideoView()
    {
        return null;//VideoMgr.getInstance().getRemoteVideoView();
    }
}
