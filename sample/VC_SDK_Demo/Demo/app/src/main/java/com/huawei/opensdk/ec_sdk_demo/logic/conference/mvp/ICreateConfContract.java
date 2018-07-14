package com.huawei.opensdk.ec_sdk_demo.logic.conference.mvp;

import com.huawei.opensdk.demoservice.ConfDetailInfo;
import com.huawei.opensdk.ec_sdk_demo.logic.BaseView;


public interface ICreateConfContract
{
    interface ConfDetailView extends BaseView
    {
        void refreshView(ConfDetailInfo confDetailInfo);
    }

    interface IConfDetailPresenter
    {

//        void queryConfDetail(String confID);

        void joinConf(String selfJoinNumber);

        void receiveBroadcast(String broadcastName, Object obj);

        void updateAccessNumber(String accessNumber);

        void endConf();

    }
}
