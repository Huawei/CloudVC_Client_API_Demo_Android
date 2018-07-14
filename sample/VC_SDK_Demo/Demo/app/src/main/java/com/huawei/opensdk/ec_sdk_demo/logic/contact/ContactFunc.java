package com.huawei.opensdk.ec_sdk_demo.logic.contact;

import android.util.Log;

import com.huawei.common.CallRecordInfo;
import com.huawei.common.PersonalContact;
import com.huawei.opensdk.callmgr.CallInfo;
import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcastReceiver;
import com.huawei.opensdk.contactmgr.ContactMgr;
import com.huawei.opensdk.contactmgr.ContactsInfo;
import com.huawei.opensdk.contactmgr.IContactNotification;
import com.huawei.opensdk.contactmgr.RecordsInfo;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ContactFunc implements IContactNotification, LocBroadcastReceiver {

    private static ContactFunc contactFunc;

    private List<ContactsInfo> contactsInfoList;

    private RecordsInfo recordsInfo;

    private String[] mActions = new String[]{CustomBroadcastConstants.ACTION_CALL_END,
            CustomBroadcastConstants.ACTION_CALL_GOING,
            CustomBroadcastConstants.ACTION_CALL_COMING,
            CustomBroadcastConstants.ACTION_CALL_CONNECTED};

    private ContactFunc()
    {
        LocBroadcast.getInstance().registerBroadcast(this, mActions);
    }

    public static synchronized ContactFunc getInstance()
    {
        if (null == contactFunc)
        {
            contactFunc = new ContactFunc();
        }
        return contactFunc;
    }

    @Override
    public void onLdapSearchNotify(int seqNo, List<PersonalContact> list, boolean lastPage) {
        if (list.size() == 0 || list.isEmpty())
        {
            LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_ENTERPRISE_GET_CONTACTS_NULL, null);
        }
        else
        {
            contactsInfoList = new ArrayList<>();
            for (PersonalContact personalContact : list)
            {
                ContactsInfo contactsInfo = new ContactsInfo();
                contactsInfo.setName(personalContact.getName());
                contactsInfo.setPhone(personalContact.getNumberOne());
                contactsInfo.setOfficePhone(personalContact.getOfficePhone());
                contactsInfo.setMobilePhone(personalContact.getMobilePhone());
                contactsInfo.setAddress(personalContact.getAddress());
                contactsInfo.setEmail(personalContact.getEmail());
                contactsInfo.setId(personalContact.getContactId());
                contactsInfoList.add(contactsInfo);
            }
            LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_ENTERPRISE_GET_CONTACTS, contactsInfoList);
        }
    }

    @Override
    public void onReceive(String broadcastName, Object obj) {
        CallInfo callInfo = (CallInfo) obj;

        switch (broadcastName)
        {
            case CustomBroadcastConstants.ACTION_CALL_COMING:
                //来电新建一条通话记录
                recordsInfo = new RecordsInfo();
                recordsInfo.setCallStartTime(new Date());
                if (callInfo.isVideoCall())
                {
                    recordsInfo.setCallType(CallRecordInfo.DialType.VIDEO);
                }
                else
                {
                    recordsInfo.setCallType(CallRecordInfo.DialType.AUDIO);
                }
                recordsInfo.setNumber(callInfo.getPeerNumber());
                recordsInfo.setRecordType(CallRecordInfo.RecordType.CALL_RECORD_IN);
                break;

            case CustomBroadcastConstants.ACTION_CALL_GOING:
                //去电新建一条通话记录
                recordsInfo = new RecordsInfo();
                recordsInfo.setNumber(callInfo.getPeerNumber());
                recordsInfo.setRecordType(CallRecordInfo.RecordType.CALL_RECORD_OUT);
                recordsInfo.setCallStartTime(new Date());
                break;

            case CustomBroadcastConstants.ACTION_CALL_CONNECTED:

                if (callInfo.isFocus())
                {
                    return;
                }
                //获取通话时间的起始时间
                recordsInfo.setCallTime(new Date().getTime());
                break;

            case CustomBroadcastConstants.ACTION_CALL_END:

                if (callInfo.isFocus())
                {
                    return;
                }
                //更新并且获取到一条通话记录
                if (recordsInfo.getCallTime() == 0 && recordsInfo.getRecordType() == CallRecordInfo.RecordType.CALL_RECORD_OUT)
                {
                    recordsInfo.setCallTime(0);
                }
                else if (recordsInfo.getCallTime() == 0 && recordsInfo.getRecordType() == CallRecordInfo.RecordType.CALL_RECORD_IN)
                {
                    recordsInfo.setCallTime(0);
                    recordsInfo.setRecordType(CallRecordInfo.RecordType.CALL_RECORD_MISS);
                }
                else
                {
                    long callTime = new Date().getTime() - recordsInfo.getCallTime();
                    recordsInfo.setCallTime(callTime/1000);
                }

                if (callInfo.isVideoCall())
                {
                    recordsInfo.setCallType(CallRecordInfo.DialType.VIDEO);
                }
                else
                {
                    recordsInfo.setCallType(CallRecordInfo.DialType.AUDIO);
                }
                int recordId = ContactMgr.getInstance().addCallRecord(recordsInfo);
                recordsInfo.setRecordId(recordId);
                int result = ContactMgr.getInstance().updateCallRecord(recordsInfo);
                if (result != -1)
                {
                    LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_CALL_RECORD, null);
                    Log.i(UIConstants.DEMO_TAG, "Update call record success");
                }
                break;
            default:
                break;
        }
    }


}
