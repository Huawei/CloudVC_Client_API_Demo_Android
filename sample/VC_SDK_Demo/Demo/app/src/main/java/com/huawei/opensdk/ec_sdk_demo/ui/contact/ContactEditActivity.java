package com.huawei.opensdk.ec_sdk_demo.ui.contact;

import android.content.Intent;
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
 * This class is about edit contact activity.
 */
public class ContactEditActivity extends BaseActivity implements View.OnClickListener {

    private TextView eSave;
    private EditText eName;
    private EditText ePhone;
    private EditText eMobilePhone;
    private EditText eOfficePhone;
    private EditText eEmail;
    private EditText eAddress;

    private String contactId;
    private String contactName;
    private ContactsInfo contactInfo;
    private Boolean isNameRepeat;

    @Override
    public void initializeComposition() {
        setContentView(R.layout.contact_edit);
        eSave = (TextView) findViewById(R.id.right_text);
        eSave.setText(R.string.save);
        eName = (EditText) findViewById(R.id.contact_name);
        ePhone = (EditText) findViewById(R.id.contact_phone);
        eMobilePhone = (EditText) findViewById(R.id.contact_mobile_phone);
        eOfficePhone = (EditText) findViewById(R.id.contact_office_phone);
        eEmail = (EditText) findViewById(R.id.contact_email);
        eAddress = (EditText) findViewById(R.id.contact_address);

        eSave.setOnClickListener(this);

        eName.setText(contactInfo.getName());
        ePhone.setText(contactInfo.getPhone());
        eMobilePhone.setText(contactInfo.getMobilePhone());
        eOfficePhone.setText(contactInfo.getOfficePhone());
        eEmail.setText(contactInfo.getEmail());
        eAddress.setText(contactInfo.getAddress());
    }

    @Override
    public void initializeData() {
        Intent intent = getIntent();
        contactId = intent.getStringExtra(UIConstants.CONTACT_ID);
        contactInfo = ContactMgr.getInstance().getContactById(contactId);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.right_text:
                contactName = eName.getText().toString();
                isNameRepeat = ContactMgr.getInstance().isContactNameRepeat(contactName, contactId);
                if (contactName.equals("") || null == contactName)
                {
                    Toast.makeText(this,"Name cannot be empty!",Toast.LENGTH_SHORT).show();
                }
                else if (isNameRepeat)
                {
                    Toast.makeText(this,"Contact " + contactName + " already exists",Toast.LENGTH_SHORT).show();
                    break;
                }
                else
                {
                    contactInfo.setName(contactName);
                    contactInfo.setPhone(ePhone.getText().toString());
                    contactInfo.setMobilePhone(eMobilePhone.getText().toString());
                    contactInfo.setOfficePhone(eOfficePhone.getText().toString());
                    contactInfo.setEmail(eEmail.getText().toString());
                    contactInfo.setAddress(eAddress.getText().toString());
                    int modResult = ContactMgr.getInstance().updateLocalContact(contactInfo);
                    if (modResult == 0)
                    {
                        Toast.makeText(this, "Modify success", Toast.LENGTH_SHORT).show();
                        setResult(UIConstants.CONTACT_RESULT_INFO);
                        finish();
                    }
                    else
                    {
                        Toast.makeText(this,"Modify failed,the error code is " + modResult, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
                break;
        }
    }

}
