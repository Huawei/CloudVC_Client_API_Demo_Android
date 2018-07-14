package com.huawei.opensdk.ec_sdk_demo.logic.conference.mvp;

import android.content.Intent;

import com.huawei.opensdk.ec_sdk_demo.logic.BaseView;


public interface ConfCtrlContract
{
    interface ConfView extends BaseView
    {
        void doStartActivity(Intent intent);
    }

    interface ConfPresenter
    {
        void gotoConfList();

        void gotoOneKeyJoin();

    }
}
