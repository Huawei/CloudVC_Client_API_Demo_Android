package com.huawei.opensdk.ec_sdk_demo.ui.contact;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.opensdk.contactmgr.ContactMgr;
import com.huawei.opensdk.contactmgr.ContactsInfo;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.ui.base.BaseActivity;

/**
 * This class is about add contact activity.
 */
public class ContactAddActivity extends BaseActivity implements View.OnClickListener {

    private TextView mSave;
    private EditText mName;
    private EditText mPhone;
    private EditText mMobilePhone;
    private EditText mOfficePhone;
    private EditText mEmail;
    private EditText mAddress;
    @Override
    public void initializeComposition() {
        setContentView(R.layout.contact_add);
        mSave = (TextView) findViewById(R.id.right_text);
        mName = (EditText) findViewById(R.id.contact_name);
        mPhone = (EditText) findViewById(R.id.contact_phone);
        mMobilePhone = (EditText) findViewById(R.id.contact_mobile_phone);
        mOfficePhone = (EditText) findViewById(R.id.contact_office_phone);
        mEmail = (EditText) findViewById(R.id.contact_email);
        mAddress = (EditText) findViewById(R.id.contact_address);

        mSave.setText(R.string.save);
        mSave.setOnClickListener(this);
    }

    @Override
    public void initializeData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.right_text:
                String newName = mName.getText().toString();
                boolean addRepeat = ContactMgr.getInstance().isContactNameRepeat(newName,"");
                if (addRepeat)
                {
                    Toast.makeText(this,"Contact " + newName + " already exists",Toast.LENGTH_SHORT).show();
                }
                else if (newName.isEmpty() || null == newName)
                {
                    Toast.makeText(this,"Name cannot be empty!",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    ContactsInfo contactsInfo = new ContactsInfo();
                    contactsInfo.setName(mName.getText().toString());
                    contactsInfo.setPhone(mPhone.getText().toString());
                    contactsInfo.setMobilePhone(mMobilePhone.getText().toString());
                    contactsInfo.setOfficePhone(mOfficePhone.getText().toString());
                    contactsInfo.setEmail(mEmail.getText().toString());
                    contactsInfo.setAddress(mAddress.getText().toString());
                    int addResult = ContactMgr.getInstance().addLocalContact(contactsInfo);
                    if (addResult == 0)
                    {
                        Toast.makeText(this,"Save successfully",Toast.LENGTH_SHORT).show();
                        setResult(UIConstants.CONTACT_RESULT_CHANGE);
                        finish();
                    }
                    else
                    {
                        Toast.makeText(this, addResult + "Save failed",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
                break;
        }

    }
}
