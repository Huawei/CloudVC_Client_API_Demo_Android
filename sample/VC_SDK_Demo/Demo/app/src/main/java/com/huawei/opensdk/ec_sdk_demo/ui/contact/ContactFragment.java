package com.huawei.opensdk.ec_sdk_demo.ui.contact;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcastReceiver;
import com.huawei.opensdk.contactmgr.ContactMgr;
import com.huawei.opensdk.contactmgr.ContactsInfo;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.adapter.ContactListAdapter;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.ui.IntentConstant;
import com.huawei.opensdk.ec_sdk_demo.ui.base.AbsFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is about contacts list fragment.
 */
public class ContactFragment extends AbsFragment implements View.OnClickListener, LocBroadcastReceiver,
        AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, TextWatcher {

    private ImageView mAddContacts;
    private ListView mListView;
    private ContactListAdapter mContactListAdapter;
    private Button mClear;
    private EditText mSearch;

    private List<ContactsInfo> mContactList = new ArrayList<>();
    private String[] action = new String[]{CustomBroadcastConstants.ACTION_ENTERPRISE_ADD_CONTACT};

    @Override
    public int getLayoutId() {
        return R.layout.contact_fragment;
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        LocBroadcast.getInstance().registerBroadcast(this,action);
        mAddContacts = (ImageView) mView.findViewById(R.id.contact_add);
        mListView = (ListView) mView.findViewById(R.id.contact_list);
        mClear = (Button) mView.findViewById(R.id.clear_condition);
        mSearch = (EditText) mView.findViewById(R.id.et_search);

        mContactList = ContactMgr.getInstance().getLocalAllContacts();
        if (mContactList.size() == 0 || mContactList == null)
        {
            //可以添加一些暂无联系人的界面，暂未实现
        }
        mContactListAdapter = new ContactListAdapter(getActivity());
        mContactListAdapter.setDate(mContactList);
        mListView.setAdapter(mContactListAdapter);

        mAddContacts.setOnClickListener(this);
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);
        mSearch.addTextChangedListener(this);
        mClear.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.contact_add:
                Intent intent = new Intent(IntentConstant.CONTACT_ADD_ACTIVITY_ACTION);
                startActivityForResult(intent, UIConstants.CONTACT_REQUEST_LIST);
                break;
            case R.id.clear_condition:
                mSearch.setText("");
                mClear.setVisibility(View.INVISIBLE);
                mContactListAdapter.setDate(ContactMgr.getInstance().getLocalAllContacts());
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode)
        {
            case UIConstants.CONTACT_REQUEST_LIST:
            case UIConstants.CONTACT_REQUEST_ITEM:
                if (resultCode == UIConstants.CONTACT_RESULT_CHANGE)
                {
                    mContactList = ContactMgr.getInstance().getLocalAllContacts();
                    mContactListAdapter.setDate(mContactList);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView mTextView = (TextView) view.findViewById(R.id.contact_list_id);
        String tvId = mTextView.getText().toString();
        Intent intent = new Intent(IntentConstant.CONTACT_INFO_ACTIVITY_ACTION);
        intent.putExtra(UIConstants.CONTACT_INDEX, tvId);
        startActivityForResult(intent, UIConstants.CONTACT_REQUEST_ITEM);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        final TextView mContactId = (TextView) view.findViewById(R.id.contact_list_id);
        TextView mContactName = (TextView) view.findViewById(R.id.contact_list_name);
        final String contactId = mContactId.getText().toString();
        final String contactName = mContactName.getText().toString();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.delete);
        builder.setMessage("Delete the contact? " + contactName);
        builder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int delResult = ContactMgr.getInstance().delLocalContact(contactId);
                        mContactList = ContactMgr.getInstance().getLocalAllContacts();
                        if (delResult == 0)
                        {
                            Toast.makeText(getActivity(),"Delete success",Toast.LENGTH_SHORT).show();
                            mContactListAdapter.setDate(mContactList);
                        }
                    }
                });
        builder.setNegativeButton(R.string.cancel, null);
        builder.create();
        builder.show();
        return true;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (!s.toString().equals("") && s.toString().length() != 0)
        {
            mClear.setVisibility(View.VISIBLE);
            mContactList = ContactMgr.getInstance().searchLocalContacts(s.toString());
            mContactListAdapter.setDate(mContactList);
        }
        else
        {
            mContactListAdapter.setDate(ContactMgr.getInstance().getLocalAllContacts());
            mClear.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onReceive(String broadcastName, Object obj) {
        switch (broadcastName)
        {
            case CustomBroadcastConstants.ACTION_ENTERPRISE_ADD_CONTACT:
                mContactListAdapter.setDate(ContactMgr.getInstance().getLocalAllContacts());
                break;
            default:
                break;
        }
    }
}
