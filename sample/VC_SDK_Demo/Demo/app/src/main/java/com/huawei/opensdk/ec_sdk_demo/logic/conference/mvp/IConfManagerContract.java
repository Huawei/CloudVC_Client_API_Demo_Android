package com.huawei.opensdk.ec_sdk_demo.logic.conference.mvp;

import com.huawei.opensdk.demoservice.ConfBaseInfo;
import com.huawei.opensdk.demoservice.Member;
import com.huawei.opensdk.ec_sdk_demo.logic.BaseView;

import java.util.List;


public interface IConfManagerContract
{
    interface IConfManagerView extends BaseView
    {
        void refreshMemberList(List<Member> list);

        void updateButtons(Member conferenceMemberEntity);

        void updateLoudSpeakerButton(int type);

        void updateTitle(String title);

        void showItemClickDialog(List<Object> items, Member member);

        void finishActivity();

        void updateConfTypeIcon(ConfBaseInfo confBaseInfo);

        //之前判断是否在数据会议中是通过数据会议入会结果判断的，现在是通过加入数据会议回调判断
//        void updateDataConfBtn(boolean show);

        void updateVideoBtn(boolean show);

        void updateUpgradeConfBtn(boolean isInDataConf);
    }

    interface IConfManagerPresenter
    {
        void registerBroadcast();

        String getConfID();

        void setConfID(String confID);

        ConfBaseInfo getConfBaseInfo();

        void leaveConf();

        void endConf();

        void addMember(String name, String number, String account);

        void delMember(Member member);

        void hangupMember(Member member);

        void muteSelf();

        void muteMember(Member member, boolean isMute);

//        void muteConf(boolean isMute);

//        void lockConf(boolean islock);

//        void handUpSelf();

//        void cancelMemberHandUp(Member member);

        void requestChairman(String chairmanPassword);

        void releaseChairman();

        void postponeConf(int time);

        void updateConf();

        void switchConfMode();

        void broadcastMember(Member member, boolean isBroad);

        void watchMember(Member member);

        void setPresenter(Member member);

        void setHost(Member member);

        void switchLoudSpeaker();

        void onItemClick(int position);

        void onItemDetailClick(String clickedItem, Member conferenceMemberEntity);

        boolean isChairMan();

//        boolean isHandUp();

        boolean isInDataConf();

        boolean isPresenter();

        boolean isHost();

//        boolean isConfMute();

//        boolean isConfLock();

    }
}
