package com.huawei.opensdk.ec_sdk_demo.logic.conference.mvp;

import com.huawei.opensdk.demoservice.ConfConstant;
import com.huawei.opensdk.demoservice.Member;
import com.huawei.opensdk.ec_sdk_demo.logic.BaseView;

import java.util.List;


public interface ConfCreateContract
{
    interface ConfCreateView extends BaseView
    {
        void refreshListView(List<Member> memberList);

        void createFailed();

        void createSuccess();

        void updateAccessNumber(String accessNumber);
    }

    interface IConfCreatePresenter
    {
        void setStartTime(String startTime);

        void setMediaType(ConfConstant.ConfMediaType mediaType);

        void setBookType(boolean isInstantConference);

        void setDuration(int duration);

        void setSubject(String subject);

        void setPassWorld(String passWorld);

        void addMember(Member member);

        void createConference();

        void receiveBroadcast(String broadcastName, Object obj);

        void updateAccessNumber(String accessNumber);
    }
}
