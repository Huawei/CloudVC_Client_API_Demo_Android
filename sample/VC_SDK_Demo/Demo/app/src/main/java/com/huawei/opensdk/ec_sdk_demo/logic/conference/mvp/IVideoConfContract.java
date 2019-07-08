package com.huawei.opensdk.ec_sdk_demo.logic.conference.mvp;

import android.content.Context;
import android.view.SurfaceView;
import android.view.ViewGroup;

import com.huawei.opensdk.demoservice.Member;
import com.huawei.opensdk.ec_sdk_demo.logic.BaseView;

import java.util.List;


public interface IVideoConfContract
{
    interface VideoConfView extends BaseView
    {
        void finishActivity();

        void updateMuteButton(boolean isMute);

        void updateLocalVideo();

        void refreshMemberList(final List<Member> list);
    }

    interface VideoConfPresenter
    {
        void registerBroadcast();

        void unregisterBroadcast();

        void setConfID(String confID);

        boolean muteSelf();

        int switchLoudSpeaker();

        void switchCamera();

        boolean isChairMan();

        void setVideoContainer(Context context, ViewGroup smallLayout, ViewGroup bigLayout, ViewGroup hideLayout);

        void setAutoRotation(Object object, boolean isOpen, int orientation);

        /**
         * 打开指定与会者的视频
         * @param userID
         */
        void attachRemoteVideo(long userID, long deviceID);

        /**
         * 共享自己的视频
         */
        void shareSelfVideo(long deviceID);

        void closeConf();

        void finishConf();

        void leaveVideo();

        List<Member> getMemberList();

        void changeLocalVideoVisible(boolean visible);

        boolean closeOrOpenLocalVideo(boolean close);

        SurfaceView getHideVideoView();

        SurfaceView getLocalVideoView();

        SurfaceView getRemoteVideoView();
    }
}
