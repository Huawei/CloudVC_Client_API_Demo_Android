package com.huawei.opensdk.ec_sdk_demo.logic.conference.mvp;

import android.content.Intent;

import com.huawei.opensdk.ec_sdk_demo.ui.IntentConstant;


public class ConfCtrlPresenter implements ConfCtrlContract.ConfPresenter
{
    private ConfCtrlContract.ConfView confView;

    public ConfCtrlPresenter(ConfCtrlContract.ConfView confView)
    {
        this.confView = confView;
    }

    @Override
    public void gotoConfList()
    {
        Intent intent = new Intent(IntentConstant.CONF_LIST_ACTIVITY_ACTION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(IntentConstant.DEFAULT_CATEGORY);
        confView.doStartActivity(intent);
    }

    @Override
    public void gotoOneKeyJoin()
    {
        Intent intent = new Intent(IntentConstant.ONE_KEY_JOIN_ACTIVITY_ACTION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(IntentConstant.DEFAULT_CATEGORY);
        confView.doStartActivity(intent);
    }

}
