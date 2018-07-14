package com.huawei.opensdk.ec_sdk_demo.ui.eaddrbook;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.huawei.opensdk.commonservice.common.LocContext;
import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcastReceiver;
import com.huawei.opensdk.contactmgr.ContactMgr;
import com.huawei.opensdk.contactmgr.ContactsInfo;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.adapter.ContactListAdapter;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.ui.IntentConstant;
import com.huawei.opensdk.ec_sdk_demo.ui.base.BaseActivity;
import com.huawei.opensdk.ec_sdk_demo.util.ActivityUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is about search ldap contact activity.
 */
public class ContactLdapActivity extends BaseActivity implements View.OnClickListener, LocBroadcastReceiver,
        AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private ImageView mBack;
    private EditText mKeyWords;
    private ImageView mFind;
    private ListView mLdapList;
    private ContactListAdapter ldapContactsAdapter;

    private String ldapFind;
    private int pageNum = 0;
    private String lastFind;
    private List<ContactsInfo> ldapContacts = new ArrayList<>();
    private ContactsInfo contactsInfo;

    private String[] actions = {CustomBroadcastConstants.ACTION_ENTERPRISE_GET_CONTACTS,
            CustomBroadcastConstants.ACTION_ENTERPRISE_GET_CONTACTS_NULL};

    @Override
    public void initializeComposition() {
        setContentView(R.layout.contact_ldap);
        mBack = (ImageView) findViewById(R.id.ldap_back);
        mKeyWords = (EditText) findViewById(R.id.ldap_keys);
        mFind = (ImageView) findViewById(R.id.ldap_find);
        mLdapList = (ListView) findViewById(R.id.enterprise_contacts);

        mBack.setOnClickListener(this);
        mFind.setOnClickListener(this);
        mLdapList.setOnItemClickListener(this);
        mLdapList.setOnItemLongClickListener(this);

        ldapContactsAdapter = new ContactListAdapter(this);
        ldapContactsAdapter.setDate(ldapContacts);
        mLdapList.setAdapter(ldapContactsAdapter);
    }

    @Override
    public void initializeData() {
        LocBroadcast.getInstance().registerBroadcast(this, actions);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.ldap_back:
                finish();
                break;
            case R.id.ldap_find:
                ldapFind = mKeyWords.getText().toString();
                if (ldapFind.equals("") || ldapFind.isEmpty())
                {
                    Toast.makeText(ContactLdapActivity.this, "Search content can not be empty!", Toast.LENGTH_SHORT).show();
                }
                else if (ldapFind.equals(lastFind))
                {
                    ldapContactsAdapter.setDate(ldapContacts);
                }
                else
                {
                    ContactMgr.getInstance().findLdapContacts(ldapFind, pageNum);
                }
                lastFind = ldapFind;
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        contactsInfo = ldapContacts.get(position);
        Intent intent = new Intent(IntentConstant.CONTACT_LDAP_INFO_ACTIVITY_ACTION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(IntentConstant.DEFAULT_CATEGORY);
        intent.putExtra(UIConstants.ENTER_ADDRESS_BOOK_CONTACT_INFO, contactsInfo);
        ActivityUtil.startActivity(LocContext.getContext(), intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        int ldapIndex = position;
        String ldapName = ldapContacts.get(position).getName();
        boolean addLdapRepeat = ContactMgr.getInstance().isContactNameRepeat(ldapName,"");
        if (addLdapRepeat)
        {
            Toast.makeText(ContactLdapActivity.this,"Contact " + ldapName + " already exists",Toast.LENGTH_SHORT).show();
        }
        else
        {
            ContactsInfo contactsInfo = new ContactsInfo();
            contactsInfo.setName(ldapContacts.get(ldapIndex).getName());
            contactsInfo.setPhone(ldapContacts.get(ldapIndex).getPhone());
            contactsInfo.setMobilePhone(ldapContacts.get(ldapIndex).getMobilePhone());
            contactsInfo.setOfficePhone(ldapContacts.get(ldapIndex).getOfficePhone());
            contactsInfo.setEmail(ldapContacts.get(ldapIndex).getEmail());
            contactsInfo.setAddress(ldapContacts.get(ldapIndex).getAddress());
            final int addResult = ContactMgr.getInstance().addLocalContact(contactsInfo);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.circle_add_friend);
            builder.setMessage("Whether to add contacts '" + ldapName + "' as a local contact");
            builder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (addResult == 0)
                    {
                        LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_ENTERPRISE_ADD_CONTACT, null);
                        Toast.makeText(ContactLdapActivity.this,"Save successfully",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(ContactLdapActivity.this, "Save failed" + addResult, Toast.LENGTH_SHORT).show();
                    }
                }
            });
            builder.setNegativeButton(R.string.cancel, null);
            builder.create();
            builder.show();
        }
        return true;
    }

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case UIConstants.ENTER_ADDRESS_BOOK_CONTACTS_LIST:
                    ldapContacts = (List<ContactsInfo>) msg.obj;
                    ldapContactsAdapter.setDate(ldapContacts);
                    break;
                case UIConstants.ENTER_ADDRESS_BOOK_CONTACTS_NULL:
                    ldapContacts = new ArrayList<>();
                    ldapContactsAdapter.setDate(ldapContacts);
                    mKeyWords.setText("");
                    Toast.makeText(ContactLdapActivity.this, "No query to contact", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onReceive(String broadcastName, Object obj) {
        switch (broadcastName)
        {
            case CustomBroadcastConstants.ACTION_ENTERPRISE_GET_CONTACTS:
                Message message = handler.obtainMessage(UIConstants.ENTER_ADDRESS_BOOK_CONTACTS_LIST, obj);
                handler.sendMessage(message);
                break;
            case CustomBroadcastConstants.ACTION_ENTERPRISE_GET_CONTACTS_NULL:
                handler.sendEmptyMessage(UIConstants.ENTER_ADDRESS_BOOK_CONTACTS_NULL);
                break;
            default:
                break;
        }

    }

}
