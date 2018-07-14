package com.huawei.opensdk.contactmgr;

import android.util.Log;

import com.huawei.common.CallRecordInfo;
import com.huawei.common.PersonalContact;
import com.huawei.opensdk.sdkwrapper.login.ContactConfigInfo;
import com.huawei.opensdk.sdkwrapper.login.LoginCenter;
import com.huawei.opensdk.sdkwrapper.manager.TupMgr;
import com.huawei.tupcontacts.TupContactsBaseNotify;
import com.huawei.tupcontacts.TupContactsManager;
import com.huawei.tupcontacts.TupLdapContactsCfg;

import java.util.ArrayList;
import java.util.List;

import common.EUAType;

/**
 * This class is about contacts manager.
 * 地址本管理对象类
 */
public class ContactMgr extends TupContactsBaseNotify {

    private final static String TAG = ContactMgr.class.getSimpleName();

    /**
     * The TupContactsManager function object.
     * TupContactsManager对象
     */
    private TupContactsManager tupContactsManager = null;

    private static ContactMgr contactMgr;

    /**
     * This method is used to get instance of this class.
     * 获取通讯录管理实例
     * @return ContactMgr   Return a instance object of this class
     *				        地址本管理对象
     */
    public static synchronized ContactMgr getInstance()
    {
        if (contactMgr == null)
        {
            contactMgr = new ContactMgr();
        }
        return contactMgr;
    }

    /**
     * UI notification.
     * 回调接口对象
     */
    private IContactNotification contactNotification;

    /**
     * This method is used to register contact module UI callback.
     * 注册回调
     */
    public void setContactNotification(IContactNotification contactNotification) {
        this.contactNotification = contactNotification;
    }

    /**
     * This method is used to set ldap configuration.
     * 设置LDAP配置
     * @return int       If success return true, otherwise return corresponding error code
     *				     成功返回0, 失败返回相应错误码
     */
    public int setConfig()
    {
        tupContactsManager = TupMgr.getInstance().getContactsManagerIns();
        ContactConfigInfo configInfo = LoginCenter.getInstance().getContactConfigInfo();
        int setConfigResult = 0;
        if (null != configInfo && !configInfo.equals(""))
        {
            if (configInfo.getEuaType().equals(EUAType.CALL_E_EUA_TYPE_LDAP))
            {
                TupLdapContactsCfg ldapContactsCfg = new TupLdapContactsCfg();
                ldapContactsCfg.setPassword(configInfo.getPassword());
                ldapContactsCfg.setUserName(configInfo.getUserName());
                ldapContactsCfg.setServerAddr(configInfo.getServerAddr());
                ldapContactsCfg.setBaseDN(configInfo.getBaseDN());
                setConfigResult = tupContactsManager.setLdapConfig(ldapContactsCfg);

                //启动LDAP联系人服务
                boolean startLdapResult = tupContactsManager.startLdapContactsServer();
                if (!startLdapResult)
                {
                    Log.e(TAG,startLdapResult + "start ldap server failed");
                }
            }
        }

        return setConfigResult;
    }

    /**
     * This method is used to stop Ldap contacts server.
     * 停止LDAP联系人服务
     */
    public void stopLdapContactsServer()
    {
        if (tupContactsManager != null)
        {
            tupContactsManager.stopLdapContactsServer();
        }
    }

    /**
     * This method is used to search ldap contacts.
     * 查询LDAP联系人
     * @param keyWords Indicates key word
     *                 关键字
     * @param page     Indicates page number
     *                 页码
     * @return int     If less or equal than 0 return false, otherwise return success
     *				   小于等于0返回失败，其他成功
     */
    public int findLdapContacts(String keyWords, int page)
    {
        int findLdapResult = tupContactsManager.searchLdapContacts(keyWords, page);
        if (findLdapResult <= 0)
        {
            Log.e(TAG, findLdapResult + "Failed to query the ldap contact");
        }
        return findLdapResult;
    }

    /**
     * This method is used to get all local contacts.
     * 获取本地所有联系人
     * @return List<PersonalContact> Return list of local contacts
     *                               本地联系人列表
     */
    public List<ContactsInfo> getLocalAllContacts()
    {
        List<PersonalContact> contacts = tupContactsManager.getLocalAllContacts();
        List<ContactsInfo> contactsList = new ArrayList<>();
        if (contacts != null && contacts.size() != 0)
        {
            for(PersonalContact contact : contacts)
            {
                ContactsInfo contactsInfo = new ContactsInfo();
                contactsInfo.setName(contact.getName());
                contactsInfo.setPhone(contact.getNumberOne());
                contactsInfo.setMobilePhone(contact.getMobilePhone());
                contactsInfo.setOfficePhone(contact.getOfficePhone());
                contactsInfo.setEmail(contact.getEmail());
                contactsInfo.setAddress(contact.getAddress());
                contactsInfo.setId(contact.getContactId());
                contactsList.add(contactsInfo);
            }
        }
        return contactsList;
    }

    /**
     * This method is used to determine if the contact exists.
     * 判断该联系人是否存在
     * @param contactName Indicates contact name
     *                    联系人姓名
     * @param contactId   Indicates contact id
     *                    联系人id
     * @return
     */
    public boolean isContactNameRepeat(String contactName, String contactId)
    {
        boolean repeatResult = false;
        List<ContactsInfo> contactsList = contactMgr.getLocalAllContacts();
        for (int i = 0; i < contactsList.size(); i++)
        {
            if (contactId.equals(contactsList.get(i).getId()))
            {
                Log.i(TAG,contactsList.get(i).getId() + "--->" + contactId);
                continue;
            }
            else if (contactName.equals(contactsList.get(i).getName()) || contactName == contactsList.get(i).getName())
            {
                repeatResult = true;
                Log.i(TAG,"The contact '" + contactName + "'is existed.");
                break;
            }
        }
        return repeatResult;
    }

    /**
     * This method is used to add local contact.
     * 添加本地联系人
     * @param contact Indicates contact object
     *                联系人对象
     * @return int  If success return true, otherwise return corresponding error code
     *				成功返回0, 失败返回相应错误码
     */
    public int addLocalContact(ContactsInfo contact)
    {
        PersonalContact tupContact = new PersonalContact();
        tupContact.setName(contact.getName());
        tupContact.setNumberOne(contact.getPhone());
        tupContact.setMobilePhone(contact.getMobilePhone());
        tupContact.setOfficePhone(contact.getOfficePhone());
        tupContact.setEmail(contact.getEmail());
        tupContact.setAddress(contact.getAddress());
//        tupContact.setContactId(contact.getId());

        int ret = tupContactsManager.addLocalContact(tupContact);

        if (ret != 0)
        {
            ret = -1;
            Log.e(TAG,ret + "add local contact failed");
        }
        return ret;
    }

    /**
     * This method is used to delete local contact.
     * 删除本地联系人
     * @param contactId Indicates contact id
     *                  联系人id
     * @return int      If success return true, otherwise return corresponding error code
     *				    成功返回0, 失败返回相应错误码
     */
    public int delLocalContact(String contactId)
    {
        //通过id获取联系人对象
        PersonalContact personalContact = tupContactsManager.getContactById(contactId);
        int deleteResult = tupContactsManager.delLocalContact(personalContact);
        if (deleteResult != 0)
        {
            Log.e(TAG,"Delete failed,the error code is " + deleteResult);
        }
        return deleteResult;
    }

    /**
     * This method is used to delete local contact.
     * 删除所有联系人
     * @return result
     */
    public int delAllContacts()
    {
        int flag = -1;
        List<PersonalContact> personalContacts = tupContactsManager.getLocalAllContacts();

        for(int i = 0; i < personalContacts.size(); i++)
        {
            flag = tupContactsManager.delLocalContact(personalContacts.get(i));
        }

        if(personalContacts.size() != 0)
        {
            Log.i(TAG, "No clear all contacts " + personalContacts.size());
        }
        Log.i(TAG , "Delete all contacts.");
        return flag;
    }

    /**
     * This method is used to get local contact by id.
     * 通过id 获取联系人信息
     * @param contactId Indicates contact id
     *                  联系人id
     * @return ContactsInfo  Return contact object
     *                       返回联系人对象
     */
    public ContactsInfo getContactById(String contactId) {
        PersonalContact personalContact = tupContactsManager.getContactById(contactId);
        ContactsInfo contact = new ContactsInfo();
        contact.setName(personalContact.getName());
        contact.setPhone(personalContact.getNumberOne());
        contact.setMobilePhone(personalContact.getMobilePhone());
        contact.setOfficePhone(personalContact.getOfficePhone());
        contact.setEmail(personalContact.getEmail());
        contact.setAddress(personalContact.getAddress());
        contact.setId(personalContact.getContactId());
        return contact;
    }

    /**
     * This method is used to modify local contact.
     * 修改联系人信息
     * @param contact Indicates contact object.
     *                联系人对象
     * @return int    If success return true, otherwise return corresponding error code
     *				  成功返回0, 失败返回相应错误码
     */
    public int updateLocalContact(ContactsInfo contact)
    {
        PersonalContact personalContact = new PersonalContact();
        personalContact.setName(contact.getName());
        personalContact.setNumberOne(contact.getPhone());
        personalContact.setMobilePhone(contact.getMobilePhone());
        personalContact.setOfficePhone(contact.getOfficePhone());
        personalContact.setEmail(contact.getEmail());
        personalContact.setAddress(contact.getAddress());
        personalContact.setContactId(contact.getId());
        int modifyResult = tupContactsManager.modifyLocalContact(personalContact);

        if (modifyResult != 0)
        {
            modifyResult = -1;
            Log.e(TAG,modifyResult + "modify local contact failed");
        }
        return modifyResult;
    }

    /**
     * This method is used to search local contacts by name, phone, mailbox, or address blur
     * 搜索联系人
     * @param keyWord Indicates query criteria.
     *                查询条件
     * @return Query list
     */
    public List<ContactsInfo> searchLocalContacts(String keyWord)
    {
        List<PersonalContact> listContact = tupContactsManager.searchLocalContacts(keyWord);
        List<ContactsInfo> listInfo = new ArrayList<>();
        for(PersonalContact contacts : listContact)
        {
            ContactsInfo contactsInfo = new ContactsInfo();
            contactsInfo.setId(contacts.getContactId());
            contactsInfo.setName(contacts.getName());
            contactsInfo.setPhone(contacts.getNumberOne());
            contactsInfo.setMobilePhone(contacts.getMobilePhone());
            contactsInfo.setOfficePhone(contacts.getOfficePhone());
            contactsInfo.setEmail(contacts.getEmail());
            contactsInfo.setAddress(contacts.getAddress());
            listInfo.add(contactsInfo);
        }
        return listInfo;
    }

    /**
     * This method is used to get all local call records.
     * 获取本地所有通话记录
     * @return List<RecordsInfo> Return all call records list
     *                           返回获取结果
     */
    public List<RecordsInfo> getLocalRecords()
    {
        List<CallRecordInfo> callRecordInfoList = tupContactsManager.getCallRecords();
        List<RecordsInfo> recordsInfoList = new ArrayList<>();
        if (callRecordInfoList.size() != 0 && callRecordInfoList != null)
        {
            for (CallRecordInfo recordInfo : callRecordInfoList)
            {
                RecordsInfo recordsInfo = new RecordsInfo();
                recordsInfo.setNumber(recordInfo.getNumber());
                recordsInfo.setCallTime(recordInfo.getCallTime());
                recordsInfo.setCallStartTime(recordInfo.getCallStartTime());
                recordsInfo.setRecordType(recordInfo.getCallType());
                recordsInfo.setCallType(recordInfo.getCallOutType());
                recordsInfo.setRecordId(recordInfo.getId());
                recordsInfo.setInterval(recordInfo.getInterval());
                recordsInfoList.add(recordsInfo);
            }
        }
        return recordsInfoList;
    }

    /**
     * This method is used to insert call record.
     * 插入一条通话记录
     * @param recordsInfo Indicates call record info
     *                    通话记录信息
     * @return int    Return id of this record, -1 means insert failed
     *				  返回当前插入的ID，-1标示插入失败
     */
    public int addCallRecord(RecordsInfo recordsInfo)
    {
        CallRecordInfo callRecordInfo = new CallRecordInfo();
        callRecordInfo.setCallStartTime(recordsInfo.getCallStartTime());
        callRecordInfo.setCallOutType(recordsInfo.getCallType());
        callRecordInfo.setCallType(recordsInfo.getRecordType());
        callRecordInfo.setNumber(recordsInfo.getNumber());
        callRecordInfo.setCallTime(recordsInfo.getCallTime());
        return tupContactsManager.insertCallRecord(callRecordInfo);
    }

    /**
     * This method is used to modify call record.
     * 更新通话记录
     * @param recordsInfo Indicates call record info
     *                    通话记录信息
     * @return int    If success return 0, otherwise return corresponding error code
     *				  成功返回0, 失败返回相应错误码
     */
    public int updateCallRecord(RecordsInfo recordsInfo)
    {
        long callTime = recordsInfo.getCallTime();
        CallRecordInfo recordInfo = new CallRecordInfo();
        recordInfo.setCallTime(callTime);
        recordInfo.setId(recordsInfo.getRecordId());
        int updateResult = tupContactsManager.modifyCallRecord(recordInfo);
        if (updateResult == -1)
        {
            Log.e(TAG,updateResult + "update call record failed");
        }
        return updateResult;
    }

    /**
     * This method is used to delete call record by id.
     * 通过id 删除一条通话记录
     * @param recordId Indicates record id
     *                 通话记录id
     * @return int If success return 0, otherwise return corresponding error code
     *			   成功返回0, 失败返回相应错误码
     */
    public int deleteCallRecordByRecordId(int recordId)
    {
        int deleteResult = tupContactsManager.deleteCallRecordById(recordId);
        if (deleteResult == -1)
        {
            Log.e(TAG,deleteResult + "delete call record failed");
        }
        return deleteResult;
    }

    /**
     * This method is used to delete call record by record type.
     * 根据记录类型删除通话记录
     * @param recordType Indicates call record type
     *                   通话记录类型
     * @return int   If success return true, otherwise return corresponding error code
     *				 成功返回0, 失败返回相应错误码
     */
    public int deleteCallRecordByRecordType(CallRecordInfo.RecordType recordType)
    {
        int deleteResult = -1;
        List<CallRecordInfo> recordsList = tupContactsManager.getCallRecords();
        for (int i = 0; i <recordsList.size(); i++)
        {
            if (recordsList.get(i).getCallType() == recordType)
            {
                deleteResult = tupContactsManager.delCallRecordByRecordType(recordType);
            }
        }
        return deleteResult;
    }

    /**
     * This method is used to return Ldap search result.
     * 搜索结果回调
     * @param iSeqNo             Indicate search sequence number
     *                           查询SeqNo
     * @param searchResultList   Indicate searched result list, if search failed value is null.
     *                           查询到的联系人结果列表，搜索失败是为null
     * @param bLastPageFlag      Indicate whether search to the last page
     *                           是否查询到最后一页
     */
    @Override
    public void onLdapSearchResult(int iSeqNo, List<PersonalContact> searchResultList, boolean bLastPageFlag) {
        int seqNo = iSeqNo;
        List<PersonalContact> contacts = searchResultList;
        boolean isLastPage = bLastPageFlag;
        contactNotification.onLdapSearchNotify(seqNo, contacts, isLastPage);
    }

}
