package com.huawei.opensdk.ec_sdk_demo.logic.conference.mvp;

import android.text.TextUtils;

import com.huawei.opensdk.demoservice.BookConferenceInfo;
import com.huawei.opensdk.demoservice.ConfConstant;
import com.huawei.opensdk.demoservice.MeetingMgr;
import com.huawei.opensdk.demoservice.Member;
import com.huawei.opensdk.ec_sdk_demo.util.DateUtil;

import java.util.ArrayList;
import java.util.List;


public class ConfCreatePresenter implements ConfCreateContract.IConfCreatePresenter
{
    private ConfCreateContract.ConfCreateView confCreateView;
    private BookConferenceInfo bookConferenceInfo;
    private List<Member> memberList;

    public ConfCreatePresenter(ConfCreateContract.ConfCreateView confCreateView)
    {
        this.confCreateView = confCreateView;

        memberList = new ArrayList<>();
        bookConferenceInfo = new BookConferenceInfo();
    }

    @Override
    public void setStartTime(String startTime)
    {
        bookConferenceInfo.setStartTime(DateUtil.localTimeUtc(startTime));
    }

    @Override
    public void setMediaType(ConfConstant.ConfMediaType mediaType)
    {
        bookConferenceInfo.setMediaType(mediaType);
    }

    @Override
    public void setBookType(boolean isInstantConference)
    {
        bookConferenceInfo.setInstantConference(isInstantConference);
    }

    @Override
    public void setDuration(int duration)
    {
        bookConferenceInfo.setDuration(duration);
    }

    @Override
    public void setSubject(String subject)
    {
        bookConferenceInfo.setSubject(subject);
    }

    @Override
    public void setPassWorld(String passWorld)
    {
        bookConferenceInfo.setPassWord(passWorld);
    }

    @Override
    public void addMember(Member member)
    {
//        if (!isValidMember(member.getNumber()))
//        {
//            confCreateView.showCustomToast(R.string.invalid_number);
//            return;
//        }
        memberList.add(member);
        confCreateView.refreshListView(memberList);
    }

    @Override
    public void createConference()
    {
        bookConferenceInfo.setMemberList(memberList);
        int result = MeetingMgr.getInstance().bookConference(bookConferenceInfo);
        if (result != 0)
        {
            confCreateView.createFailed();
        }
    }

    @Override
    public void receiveBroadcast(String broadcastName, Object obj)
    {
        switch (broadcastName)
        {
            default:
                break;
        }
    }

    @Override
    public void updateAccessNumber(String accessNumber)
    {
        memberList.get(0).setNumber(accessNumber);
        confCreateView.refreshListView(memberList);
        confCreateView.updateAccessNumber(accessNumber);
    }

    private boolean isValidMember(String member)
    {
        if (TextUtils.isEmpty(member))
        {
            return false;
        }
        return true;
    }
}
