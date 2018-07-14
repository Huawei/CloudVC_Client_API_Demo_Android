package com.huawei.opensdk.sdkwrapper.manager;


import com.huawei.meeting.IConferenceUI;
import com.huawei.opensdk.sdkwrapper.login.ITupLoginCenterNotify;
import com.huawei.tup.confctrl.sdk.TupConfNotify;
import com.huawei.tup.ctd.sdk.TupCtdNotify;
import com.huawei.tup.login.sdk.TupLoginNotify;
import com.huawei.tupcontacts.TupContactsNotify;

import common.TupCallNotify;

/**
 * This class is about callback manager
 * 回调管理类
 */
public class TupEventNotifyMgr {

    /**
     * Login module adapt callback
     * 登录模块adapt层回调
     */
    private ITupLoginCenterNotify loginNotify;

    /**
     * Login module callback
     * 登录回调
     */
    private TupLoginNotify authNotify;

    /**
     * Call module callback
     * 呼叫回调
     */
    private TupCallNotify callNotify;

    /**
     * Conference module callback
     * 会控回调
     */
    private TupConfNotify confNotify;

    /**
     * ctd callback
     * ctd回调
     */
    private TupCtdNotify ctdNotify;

    /**
     * Data conference callback
     *  数据会议回调
     */
    private IConferenceUI dataConfNotify;

    /**
     * Local contact callback
     * 本地通讯录回调
     */
    private TupContactsNotify contactsNotify;

    //待补充其他


    public TupEventNotifyMgr() {
    }

    /**
     *
     * @param loginNotify   登录模块adapt层回调
     * @param callNotify    呼叫回调
     * @param confNotify    会控回调
     * @param ctdNotify     ctd回调
     * @param dataConfNotify    数据会议回调
     */
    public TupEventNotifyMgr(ITupLoginCenterNotify loginNotify, TupCallNotify callNotify, TupConfNotify confNotify, TupCtdNotify ctdNotify, IConferenceUI dataConfNotify) {
        this.loginNotify = loginNotify;
        this.callNotify = callNotify;
        this.confNotify = confNotify;
        this.ctdNotify = ctdNotify;
        this.dataConfNotify = dataConfNotify;
    }

    public ITupLoginCenterNotify getLoginNotify() {
        return loginNotify;
    }

    public void setLoginNotify(ITupLoginCenterNotify loginNotify) {
        this.loginNotify = loginNotify;
    }

    public TupLoginNotify getAuthNotify() {
        return authNotify;
    }

    public void setAuthNotify(TupLoginNotify authNotify) {
        this.authNotify = authNotify;
    }

    public TupCallNotify getCallNotify() {
        return callNotify;
    }

    public void setCallNotify(TupCallNotify callNotify) {
        this.callNotify = callNotify;
    }

    public TupConfNotify getConfNotify() {
        return confNotify;
    }

    public void setConfNotify(TupConfNotify confNotify) {
        this.confNotify = confNotify;
    }

    public TupCtdNotify getCtdNotify() {
        return ctdNotify;
    }

    public void setCtdNotify(TupCtdNotify ctdNotify) {
        this.ctdNotify = ctdNotify;
    }

    public IConferenceUI getDataConfNotify() {
        return dataConfNotify;
    }

    public void setDataConfNotify(IConferenceUI dataConfNotify) {
        this.dataConfNotify = dataConfNotify;
    }

    public TupContactsNotify getContactsNotify() {
        return contactsNotify;
    }

    public void setContactsNotify(TupContactsNotify contactsNotify) {
        this.contactsNotify = contactsNotify;
    }

}
