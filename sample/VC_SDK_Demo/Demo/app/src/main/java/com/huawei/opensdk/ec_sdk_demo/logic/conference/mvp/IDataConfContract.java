package com.huawei.opensdk.ec_sdk_demo.logic.conference.mvp;

import android.content.Context;
import android.view.ViewGroup;

import com.huawei.opensdk.ec_sdk_demo.logic.BaseView;



public interface IDataConfContract
{
    interface DataConfView extends BaseView
    {

    }

    interface IDataConfPresenter
    {
        void setConfID(String confID);

        String getSubject();

        boolean muteSelf();

        int switchLoudSpeaker();

        boolean isChairMan();

        void closeConf();

        void finishConf();

        void attachSurfaceView(ViewGroup container, Context context);
    }
}
