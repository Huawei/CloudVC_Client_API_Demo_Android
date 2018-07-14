package com.huawei.opensdk.ec_sdk_demo.logic.eaddrbook;

import android.content.Intent;

import com.huawei.opensdk.ec_sdk_demo.ui.IntentConstant;

public class EnterpriseAddressBookPresenter implements EnterpriseAddressBookContract.EnterprisePresenter {

    private EnterpriseAddressBookContract.EAddrBookView pBookView;

    public EnterpriseAddressBookPresenter(EnterpriseAddressBookContract.EAddrBookView pBookView)
    {
        this.pBookView = pBookView;
    }

    @Override
    public void gotoEAddrBookEntry() {
        Intent intent = new Intent(IntentConstant.CONTACT_LDAP_ACTIVITY_ACTION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(IntentConstant.DEFAULT_CATEGORY);
        pBookView.doStartActivity(intent);
    }
}
