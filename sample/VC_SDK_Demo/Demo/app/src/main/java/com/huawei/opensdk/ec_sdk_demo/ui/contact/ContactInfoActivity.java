package com.huawei.opensdk.ec_sdk_demo.ui.contact;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.opensdk.contactmgr.ContactMgr;
import com.huawei.opensdk.contactmgr.ContactsInfo;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.ui.IntentConstant;
import com.huawei.opensdk.ec_sdk_demo.ui.base.BaseActivity;

/**
 * This class is about display contact info activity.
 */
public class ContactInfoActivity extends BaseActivity implements View.OnClickListener {

    private TextView iEdit;
    private TextView iName;
    private TextView iPhone;
    private TextView iMobilePhone;
    private TextView iOfficePhone;
    private TextView iEmail;
    private TextView iAddress;
    private TextView iDelete;

    private String contactId;
    private String contactName;
    private ContactsInfo contactsInfo;

    @Override
    public void initializeComposition() {
        setContentView(R.layout.contact_info);
        iEdit = (TextView) findViewById(R.id.right_text);
        iEdit.setText(R.string.editcontact);
        iName = (TextView) findViewById(R.id.contact_info_name);
        iPhone = (TextView) findViewById(R.id.contact_info_phone);
        iMobilePhone = (TextView) findViewById(R.id.contact_info_mobile_phone);
        iOfficePhone = (TextView) findViewById(R.id.contact_info_office_phone);
        iEmail = (TextView) findViewById(R.id.contact_info_email);
        iAddress = (TextView) findViewById(R.id.contact_info_address);
        iDelete = (TextView) findViewById(R.id.contact_info_delete);

        iEdit.setOnClickListener(this);
        iDelete.setOnClickListener(this);

        iName.setText(contactsInfo.getName());
        iPhone.setText(contactsInfo.getPhone());
        iMobilePhone.setText(contactsInfo.getMobilePhone());
        iOfficePhone.setText(contactsInfo.getOfficePhone());
        iEmail.setText(contactsInfo.getEmail());
        iAddress.setText(contactsInfo.getAddress());
    }

    @Override
    public void initializeData() {
        Intent intent = getIntent();
        contactId = intent.getStringExtra(UIConstants.CONTACT_INDEX);
        contactsInfo = ContactMgr.getInstance().getContactById(contactId);
        contactName = contactsInfo.getName();
    }

    @Override
    protected void initBackView(int resource) {
        View mBack = findViewById(resource);
        if (null != mBack)
        {
            mBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setResult(UIConstants.CONTACT_RESULT_CHANGE);
                    finish();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode)
        {
            case UIConstants.CONTACT_REQUEST_INFO:
                if (resultCode == UIConstants.CONTACT_RESULT_INFO)
                {
                    contactsInfo = ContactMgr.getInstance().getContactById(contactId);
                    iName.setText(contactsInfo.getName());
                    iPhone.setText(contactsInfo.getPhone());
                    iMobilePhone.setText(contactsInfo.getMobilePhone());
                    iOfficePhone.setText(contactsInfo.getOfficePhone());
                    iEmail.setText(contactsInfo.getEmail());
                    iAddress.setText(contactsInfo.getAddress());
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.right_text:
                Intent intent = new Intent(IntentConstant.CONTACT_EDIT_ACTIVITY_ACTION);
                intent.putExtra(UIConstants.CONTACT_ID, contactId);
                startActivityForResult(intent, UIConstants.CONTACT_REQUEST_INFO);
                break;
            case R.id.contact_info_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.delete);
                builder.setMessage("Delete the contact? " + contactName);
                builder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int delResult = ContactMgr.getInstance().delLocalContact(contactId);
                        if (delResult == 0)
                        {
                            Toast.makeText(ContactInfoActivity.this,"Delete success",Toast.LENGTH_SHORT).show();
                            setResult(UIConstants.CONTACT_RESULT_CHANGE);
                            finish();
                        }
                    }
                });
                builder.setNegativeButton(R.string.cancel, null);
                builder.create();
                builder.show();
                break;
            default:
                break;
        }
    }

}
