package com.huawei.opensdk.ec_sdk_demo.logic.conference.mvp;

import android.content.Context;
import android.view.SurfaceView;
import android.view.ViewGroup;

import com.huawei.opensdk.demoservice.ConfBaseInfo;
import com.huawei.opensdk.demoservice.Member;
import com.huawei.opensdk.ec_sdk_demo.logic.BaseView;

import java.util.List;


public interface IAttendeeListContract
{
    interface IAttendeeListView extends BaseView
    {
        void refreshMemberList(List<Member> list);

        void updateAddAttendeeButton(boolean isChairman);

        void updateMuteButton(boolean isMute);

        void updateLoudSpeakerButton(int type);

        void updateTitle(String title);

        void showItemClickDialog(List<Object> items, Member member);

        void finishActivity();

        void updateConfTypeIcon(ConfBaseInfo confBaseInfo);

        void updateVideoBtn(boolean show);

        void updateUpgradeConfBtn(boolean isInDataConf);

        void showMessage(String message);

        void updateSpeaker(String[] speakers, boolean noSpeaker);
    }

    interface IAttendeeListPresenter
    {
        void registerBroadcast();

        void unregisterBroadcast();

        String getConfID();

        void setConfID(String confID);

        ConfBaseInfo getConfBaseInfo();

        void leaveConf();

        void endConf();

        void addMember(String name, String number, String account);

        void delMember(Member member);

        void muteSelf();

        void muteMember(Member member, boolean isMute);

        void muteConf(boolean isMute);

        void lockConf(boolean islock);

        void handUpSelf();

        void cancelMemberHandUp(Member member);

        void requestChairman(String chairmanPassword);

        void releaseChairman();

        void postponeConf(int time);

        void updateConf();

        void switchConfMode();

        void broadcastMember(Member member);

        void setPresenter(Member member);

        void setHost(Member member);

        void switchLoudSpeaker();

        void onItemClick(int position);

        void onItemDetailClick(String clickedItem, Member conferenceMemberEntity);

        boolean isChairMan();

        boolean isHandUp();

        boolean isInDataConf();

        boolean isPresenter();

        boolean isHost();

//        boolean isConfMute();

//        boolean isConfLock();

        List<Member> updateAttendeeList();

        ConfBaseInfo updateConfBaseInfo();

        Member selfInfo();

        boolean isMuteSelf();

        void setVideoContainer(Context context, ViewGroup smallLayout, ViewGroup hideLayout);

        SurfaceView getHideVideoView();

        SurfaceView getLocalVideoView();

        void broadcastAttendee(Member member, boolean isBroad);

    }
}
