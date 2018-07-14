package com.huawei.opensdk.contactmgr;

import com.huawei.common.PersonalContact;

import java.util.List;

/**
 * This interface is about contact notify.
 * 企业通讯录相关回调通知接口
 */
public interface IContactNotification {

    /**
     * This method is used to return Ldap search result.
     * LDAP地址本搜索结果回调
     * @param seqNo              Indicate search sequence number
     *                           查询SeqNo
     * @param list               Indicate searched result list, if search failed value is null.
     *                           查询到的联系人结果列表，搜索失败是为null
     * @param lastPage           Indicate whether search to the last page
     *                           是否查询到最后一页
     */
    void onLdapSearchNotify(int seqNo, List<PersonalContact> list, boolean lastPage);
}
