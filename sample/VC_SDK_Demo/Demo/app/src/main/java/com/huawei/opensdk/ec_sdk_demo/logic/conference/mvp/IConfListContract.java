package com.huawei.opensdk.ec_sdk_demo.logic.conference.mvp;

import android.content.Context;

import com.huawei.opensdk.demoservice.ConfBaseInfo;
import com.huawei.opensdk.ec_sdk_demo.logic.BaseView;

import java.util.List;


public interface IConfListContract
{
    interface ConfListView extends BaseView
    {
        void refreshConfList(List<ConfBaseInfo> tupConfInfoList);

        void gotoConfDetailActivity(String confID);
    }

    interface IConfListPresenter
    {
        void receiveBroadcast(String broadcastName, Object obj);

//        void queryConfList();

        void onItemClick(int position);

        void joinReserveConf(String confID, String accessCode, String password);
    }
}
