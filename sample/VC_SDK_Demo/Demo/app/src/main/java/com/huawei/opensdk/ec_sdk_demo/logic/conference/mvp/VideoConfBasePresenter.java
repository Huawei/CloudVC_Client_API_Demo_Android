package com.huawei.opensdk.ec_sdk_demo.logic.conference.mvp;


import com.huawei.opensdk.callmgr.CallMgr;
import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcastReceiver;
import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.demoservice.ConfBaseInfo;
import com.huawei.opensdk.demoservice.ConfConstant;
import com.huawei.opensdk.demoservice.MeetingMgr;
import com.huawei.opensdk.demoservice.Member;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.ui.base.MVPBasePresenter;

import java.util.List;



public abstract class VideoConfBasePresenter extends MVPBasePresenter<IVideoConfContract.VideoConfView>
        implements IVideoConfContract.VideoConfPresenter
{
    private String confID;

    protected String[] broadcastNames = {CustomBroadcastConstants.CONF_STATE_UPDATE};
    protected LocBroadcastReceiver receiver = new LocBroadcastReceiver()
    {
        @Override
        public void onReceive(String broadcastName, Object obj)
        {
            switch (broadcastName)
            {
                case CustomBroadcastConstants.CONF_STATE_UPDATE:
                    LogUtil.i(UIConstants.DEMO_TAG, "conf state update ");

//                    String conferenceID = (String) obj;
//                    if (!conferenceID.equals(confID))
//                    {
//                        return;
//                    }

                    //判断会议状态，如果会议结束，则关闭会议界面
                    ConfBaseInfo confBaseInfo = MeetingMgr.getInstance().getCurrentConferenceBaseInfo();
                    ConfConstant.ConfConveneStatus status = confBaseInfo.getConfState();
                    if (status == ConfConstant.ConfConveneStatus.DESTROYED)
                    {
                        LocBroadcast.getInstance().unRegisterBroadcast(receiver, broadcastNames);
                        getView().finishActivity();
                        return;
                    }

                    List<Member> memberList = MeetingMgr.getInstance().getCurrentConferenceMemberList();
                    if (memberList == null)
                    {
                        return;
                    }

                    Member self = MeetingMgr.getInstance().getCurrentConferenceSelf();
                    if (self != null)
                    {
                        getView().updateMuteButton(self.isMute());
                    }
                    getView().refreshMemberList(MeetingMgr.getInstance().getCurrentConferenceMemberList());

                    return;

                default:
                    break;
            }
            onBroadcastReceive(broadcastName, obj);
        }
    };

    protected abstract void onBroadcastReceive(String broadcastName, Object obj);



    @Override
    public void registerBroadcast()
    {
        LocBroadcast.getInstance().registerBroadcast(receiver, broadcastNames);
    }

    @Override
    public void unregisterBroadcast()
    {
        LocBroadcast.getInstance().unRegisterBroadcast(receiver, broadcastNames);
    }

    @Override
    public void setConfID(String confID) {
        this.confID = confID;
    }

    public String getSubject()
    {
        return MeetingMgr.getInstance().getCurrentConferenceBaseInfo().getSubject();
    }

    @Override
    public void closeConf()
    {
        int callID = MeetingMgr.getInstance().getCurrentConferenceCallID();
        if (callID != 0) {
            CallMgr.getInstance().endCall(callID);
            MeetingMgr.getInstance().setCurrentConferenceCallID(0);
        }

        int result = MeetingMgr.getInstance().leaveConf();
        if (result != 0) {
            getView().showCustomToast(R.string.leave_conf_fail);
            return;
        }

        LocBroadcast.getInstance().unRegisterBroadcast(receiver, broadcastNames);
    }

    @Override
    public void finishConf()
    {
//        int callID = MeetingMgr.getInstance().getCurrentConferenceCallID();
//        if (callID != 0) {
//            CallMgr.getInstance().endCall(callID);
//            MeetingMgr.getInstance().setCurrentConferenceCallID(0);
//        }

        int result = MeetingMgr.getInstance().endConf();
        if (result != 0) {
            getView().showCustomToast(R.string.end_audio_conf);
            return;
        }

        LocBroadcast.getInstance().unRegisterBroadcast(receiver, broadcastNames);
    }


    @Override
    public boolean muteSelf()
    {
        Member self = MeetingMgr.getInstance().getCurrentConferenceSelf();

        int result = MeetingMgr.getInstance().muteAttendee(self, !self.isMute());
        if (result != 0)
        {
            return false;
        }
        return true;
    }

    @Override
    public int switchLoudSpeaker()
    {
        return CallMgr.getInstance().switchAudioRoute();
    }

    @Override
    public boolean isChairMan()
    {
        Member self = MeetingMgr.getInstance().getCurrentConferenceSelf();

        return (self.getRole() == ConfConstant.ConfRole.ATTENDEE ? false:true);
    }

    @Override
    public List<Member> getMemberList()
    {
        return MeetingMgr.getInstance().getCurrentConferenceMemberList();
    }
}
