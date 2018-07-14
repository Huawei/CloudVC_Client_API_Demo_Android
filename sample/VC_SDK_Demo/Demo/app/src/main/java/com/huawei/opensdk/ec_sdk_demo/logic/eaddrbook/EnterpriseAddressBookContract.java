package com.huawei.opensdk.ec_sdk_demo.logic.eaddrbook;

import android.content.Intent;

import com.huawei.opensdk.ec_sdk_demo.logic.BaseView;

public interface EnterpriseAddressBookContract {

    interface EAddrBookView extends BaseView
    {
        void doStartActivity(Intent intent);
    }

    interface EnterprisePresenter
    {
        void gotoEAddrBookEntry();
    }
}
