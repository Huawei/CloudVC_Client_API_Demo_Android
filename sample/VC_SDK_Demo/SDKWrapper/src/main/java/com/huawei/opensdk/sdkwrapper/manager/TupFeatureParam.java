package com.huawei.opensdk.sdkwrapper.manager;

/**
 * This class is about already functional
 *  已有功能类
 */
public class TupFeatureParam {

    /**
     * Type
     * 类型
     */
    private int appType;

    /**
     * Support audio and video call
     * 是否支持音视频呼叫
     */
    private boolean isSupportAudioAndVideoCall;

    /**
     * Support audio and video conf
     * 是否支持音视频会议
     */
    private boolean isSupportAudioAndVideoConf;

    /**
     * Support data conf
     * 是否支持数据会议
     */
    private boolean isSupportDataConf;

    /**
     * Support address book
     * 是否支持地址本
     */
    private boolean isSupportAddressbook;

    /**
     * Support contact
     * 是否支持本地通讯录
     */
    private boolean isSupportContact;

    /**
     *
     * @param appType   类型
     * @param isSupportAudioAndVideoCall 音视频呼叫
     * @param isSupportAudioAndVideoConf    音视频会议
     * @param isSupportDataConf 数据会议
     * @param isSupportAddressbook  地址本
     */
    public TupFeatureParam(int appType, boolean isSupportAudioAndVideoCall, boolean isSupportAudioAndVideoConf, boolean isSupportDataConf, boolean isSupportAddressbook) {
        this.appType = appType;
        this.isSupportAudioAndVideoCall = isSupportAudioAndVideoCall;
        this.isSupportAudioAndVideoConf = isSupportAudioAndVideoConf;
        this.isSupportDataConf = isSupportDataConf;
        this.isSupportAddressbook = isSupportAddressbook;
    }


    /**
     *
     * @param isSupportAudioAndVideoCall    音视频呼叫
     * @param isSupportAudioAndVideoConf    音视频会议
     * @param isSupportDataConf 数据会议
     * @param isSupportAddressbook  地址本
     */
    public TupFeatureParam(boolean isSupportAudioAndVideoCall, boolean isSupportAudioAndVideoConf, boolean isSupportDataConf, boolean isSupportAddressbook, boolean isSupportContact) {
        this.isSupportAudioAndVideoCall = isSupportAudioAndVideoCall;
        this.isSupportAudioAndVideoConf = isSupportAudioAndVideoConf;
        this.isSupportDataConf = isSupportDataConf;
        this.isSupportAddressbook = isSupportAddressbook;
        this.isSupportContact = isSupportContact;
    }

    public int getAppType() {
        return appType;
    }

    public void setAppType(int appType) {
        this.appType = appType;
    }

    public boolean isSupportAudioAndVideoCall() {
        return isSupportAudioAndVideoCall;
    }

    public void setSupportAudioAndVideoCall(boolean supportAudioAndVideoCall) {
        isSupportAudioAndVideoCall = supportAudioAndVideoCall;
    }

    public boolean isSupportAudioAndVideoConf() {
        return isSupportAudioAndVideoConf;
    }

    public void setSupportAudioAndVideoConf(boolean supportAudioAndVideoConf) {
        isSupportAudioAndVideoConf = supportAudioAndVideoConf;
    }

    public boolean isSupportDataConf() {
        return isSupportDataConf;
    }

    public void setSupportDataConf(boolean supportDataConf) {
        isSupportDataConf = supportDataConf;
    }

    public boolean isSupportAddressbook() {
        return isSupportAddressbook;
    }

    public void setSupportAddressbook(boolean supportAddressbook) {
        isSupportAddressbook = supportAddressbook;
    }

    public boolean isSupportContact() {
        return isSupportContact;
    }

    public void setSupportContact(boolean supportContact) {
        isSupportContact = supportContact;
    }

}
