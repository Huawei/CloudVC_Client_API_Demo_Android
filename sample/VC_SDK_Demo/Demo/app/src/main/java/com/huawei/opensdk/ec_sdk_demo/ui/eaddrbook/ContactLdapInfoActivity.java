package com.huawei.opensdk.ec_sdk_demo.ui.eaddrbook;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;
import com.huawei.opensdk.contactmgr.ContactMgr;
import com.huawei.opensdk.contactmgr.ContactsInfo;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.ui.base.BaseActivity;

/**
 * This class is about display ldap contact info activity.
 */
public class ContactLdapInfoActivity extends BaseActivity implements View.OnClickListener {

    private TextView iName;
    private TextView iPhone;
    private TextView iMobilePhone;
    private TextView iOfficePhone;
    private TextView iEmail;
    private TextView iAddress;
    private TextView iId;
    private TextView iDelete;
    private TextView iAdd;

    private ContactsInfo iContactsInfo;
    private String ldapName;

    @Override
    public void initializeComposition() {
        setContentView(R.layout.contact_info);
        iName = (TextView) findViewById(R.id.contact_info_name);
        iPhone = (TextView) findViewById(R.id.contact_info_phone);
        iMobilePhone = (TextView) findViewById(R.id.contact_info_mobile_phone);
        iOfficePhone = (TextView) findViewById(R.id.contact_info_office_phone);
        iEmail = (TextView) findViewById(R.id.contact_info_email);
        iAddress = (TextView) findViewById(R.id.contact_info_address);
        iId = (TextView) findViewById(R.id.contact_info_id);
        iDelete = (TextView) findViewById(R.id.contact_info_delete);
        iAdd = (TextView) findViewById(R.id.contact_info_add);

        hideDetailView();

        iAdd.setOnClickListener(this);

        iName.setText(iContactsInfo.getName());
        iPhone.setText(iContactsInfo.getPhone());
        iMobilePhone.setText(iContactsInfo.getMobilePhone());
        iOfficePhone.setText(iContactsInfo.getOfficePhone());
        iEmail.setText(iContactsInfo.getEmail());
        iAddress.setText(iContactsInfo.getAddress());
        iId.setText(iContactsInfo.getId());

        ldapName = iName.getText().toString();

        boolean addLdapRepeat = ContactMgr.getInstance().isContactNameRepeat(ldapName,"");
        if (!addLdapRepeat)
        {
            showDetailView();
        }
    }

    @Override
    public void initializeData() {
        Intent intent = getIntent();
        iContactsInfo = (ContactsInfo) intent.getSerializableExtra(UIConstants.ENTER_ADDRESS_BOOK_CONTACT_INFO);
    }

    private void hideDetailView()
    {
        iDelete.setVisibility(View.GONE);
    }

    private void showDetailView()
    {
        iAdd.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.contact_info_add:
                int addResult = ContactMgr.getInstance().addLocalContact(iContactsInfo);
                if (addResult == 0)
                {
                    LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_ENTERPRISE_ADD_CONTACT, null);
                    Toast.makeText(this,"Save successfully",Toast.LENGTH_SHORT).show();
                    iAdd.setVisibility(View.GONE);
                }
                else
                {
                    Toast.makeText(this, addResult + "Save failed",Toast.LENGTH_SHORT).show();
                }
            default:
                break;
        }
    }
}
