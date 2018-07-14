package com.huawei.opensdk.sdkwrapper.manager;


import android.content.Context;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;

import com.huawei.meeting.ConfDefines;
import com.huawei.meeting.ConfInstance;
import com.huawei.meeting.Conference;
import com.huawei.tup.TUPInterfaceService;
import com.huawei.tup.confctrl.sdk.TupConfManager;
import com.huawei.tup.login.LoginLogLevel;
import com.huawei.tup.login.LoginVerifyMode;
import com.huawei.tup.login.sdk.TupLoginManager;
import com.huawei.tupcontacts.TupContactsManager;

import java.io.File;

import common.ReInviteNoSdpReplyMode;
import common.TupBool;
import common.TupCallParam;
import object.TupCallCfgAudioVideo;
import object.TupCallCfgBFCP;
import object.TupCallCfgMedia;
import object.TupCallCfgSIP;
import tupsdk.TupCallManager;

/**
 * This class is about SDK management.
 * Provides component instance management and initialization management.
 */
public class TupMgr
{

    private static final String TAG = TupMgr.class.getSimpleName();

    private static final TupMgr sdkManger = new TupMgr();

    /* component instance */
    private TUPInterfaceService baseServiceIns = null;

    /**
     * Login manager
     * 登录管理
     */
    private TupLoginManager authManagerIns = null;

    /**
     * Call manager
     * 呼叫管理
     */
    private TupCallManager callManagerIns = null;

    /**
     * Conference manager
     * 会议管理
     */
    private TupConfManager confManagerIns = null;

    /**
     * Data conference manager
     * 数据会议管理
     */
    private Conference dataConfManagerIns = null;

    /**
     * Data conference instance
     * 数据会议实体
     */
    private ConfInstance dataConfInstance = null;

    /**
     * Contact manager
     * 本地通讯录管理
     */
    private TupContactsManager contactsManagerIns = null;

    /**
     * Library  Load Path
     * 库加载路径
     */
    private String appPath = null;

    /**
     * 上下文
     */
    private Context context = null;

    /**
     * 支持功能
     */
    private TupFeatureParam featureMgr = null;

    /**
     * 回调管理
     */
    private TupEventNotifyMgr notifyMgr = null;

    /**
     * 呼叫sip注册信息
     */
    private TupCallCfgSIP tupCallCfgSIP = null;

    /**
     * 媒体注册信息
     */
    private TupCallCfgMedia tupCallCfgMedia = null;

    /**
     * 辅流BFCP的配置信息
     */
    private TupCallCfgBFCP tupCallCfgBFCP = null;

    /**
     * 音视频注册信息
     */
    private TupCallCfgAudioVideo tupCallCfgAudioVideo = null;

    /* log config param */
    private String log_path = Environment.getExternalStorageDirectory() + File.separator + "TupSdkLog";
    private int maxsize_kb = 10 * 1024;
    private int file_count = 1;
    private int log_level = 2;

    public static TupMgr getInstance() {
        return sdkManger;
    }

    public TupLoginManager getAuthManagerIns() {
        return authManagerIns;
    }

    public TupCallManager getCallManagerIns() {
        return callManagerIns;
    }

    public TupConfManager getConfManagerIns() {
        return confManagerIns;
    }

    public Conference getDataConfManagerIns() {
        return dataConfManagerIns;
    }

    public TupFeatureParam getFeatureMgr() {
        return featureMgr;
    }

    public TUPInterfaceService getBaseServiceIns()
    {
        return baseServiceIns;
    }

    public ConfInstance getDataConfInstance() {
        return dataConfInstance;
    }

    public TupContactsManager getContactsManagerIns() {
        return contactsManagerIns;
    }

    public TupCallCfgSIP getTupCallCfgSIP() {
        if (tupCallCfgSIP == null) {
            TupCallCfgSIP sipCfg = new TupCallCfgSIP();
            tupCallCfgSIP = sipCfg;
        }
        return tupCallCfgSIP;
    }


    public TupCallCfgMedia getTupCallCfgMedia() {
        if (tupCallCfgMedia == null) {
            TupCallCfgMedia mediaCfg = new TupCallCfgMedia();
            tupCallCfgMedia = mediaCfg;
        }
        return tupCallCfgMedia;
    }

    public TupCallCfgAudioVideo getTupCallCfgAudioVideo() {
        if (tupCallCfgAudioVideo == null) {
            TupCallCfgAudioVideo avCfg = new TupCallCfgAudioVideo();
            tupCallCfgAudioVideo = avCfg;
        }
        return tupCallCfgAudioVideo;
    }

    public TupCallCfgBFCP getTupCallCfgBFCP() {
        if (tupCallCfgBFCP == null) {
            TupCallCfgBFCP bfcpCfg = new TupCallCfgBFCP();
            tupCallCfgBFCP = bfcpCfg;
        }
        return tupCallCfgBFCP;
    }

    /**
     * This method is used to set log param
     * @param logLevel
     * @param maxSizeKB
     * @param fileCount
     * @param logPath
     */
    public void setLogParam(int logLevel, int maxSizeKB, int fileCount, String logPath) {
        this.log_level = logLevel;
        this.maxsize_kb = maxSizeKB;
        this.file_count = fileCount;
        this.log_path = logPath;
    }


    /**
     * This method is used to reg event notify
     * @param notifyMgr
     */
    public void regEventNotify(TupEventNotifyMgr notifyMgr) {
        this.notifyMgr = notifyMgr;
    }


    public TupEventNotifyMgr getNotifyMgr() {
        return notifyMgr;
    }

    public void setNotifyMgr(TupEventNotifyMgr notifyMgr) {
        this.notifyMgr = notifyMgr;
    }


    /**
     * This method is used to init sdk
     * @param context
     * @param featureMgr
     * @return
     */
    public int sdkInit(Context context, String appPath, TupFeatureParam featureMgr)
    {
        int ret;

        if ((featureMgr == null) || (context == null) || (appPath == null)) {
            return -1;
        }

        if (this.notifyMgr == null){
            return -1;
        }

        this.featureMgr = featureMgr;
        this.context = context;
        this.appPath = appPath;

        /* init base sdk component*/
        initBaseSdk();

        /* init authorize component*/
        ret = initAuth();
        if (ret != 0) {
            return ret;
        }

        /* If support audio and video calls, initialize the call module  */
        if (featureMgr.isSupportAudioAndVideoCall()) {
            ret = initCall();
            if (ret != 0) {
                return ret;
            }
        }

        /* If support audio and video conference, initialize the control module  */
        if (featureMgr.isSupportAudioAndVideoConf()) {
            ret = initConfCtrl();
            if (ret != 0){
                return ret;
            }
        }

        if (featureMgr.isSupportDataConf()) {
            //在需要加入会议时再初始化,减少无需要的资源占用。
            /*ret = initDataConf();
            if (ret != 0) {
                return ret;
            }*/
        }

        if (featureMgr.isSupportContact())
        {
            initContact();
        }

        return 0;
    }


    /**
     * This method is used to uninit sdk
     * @return
     */
    public int sdkUninit()
    {
        return 0;
    }

    private void initBaseSdk() {
        if (this.baseServiceIns == null) {
            this.baseServiceIns = new TUPInterfaceService();

            /* start base service */
            this.baseServiceIns.StartUpService();

            /* set app path */
            this.baseServiceIns.SetAppPath(this.appPath);
        }

        return;
    }

    private int initAuth() {
        int ret = 0;

        if (this.authManagerIns == null) {

            this.authManagerIns = TupLoginManager.getIns(new AuthEventAdapt(), this.context);

            /* set log param */
            LoginLogLevel logLevel = LoginLogLevel.values()[this.log_level];
            int logSize = this.maxsize_kb;
            int logCount = this.file_count;
            String logPath = this.log_path;
            this.authManagerIns.setLogParam(logLevel, logSize, logCount, logPath);

            /* set verify mode, option */
            this.authManagerIns.setVerifyMode(LoginVerifyMode.LOGIN_E_VERIFY_MODE_NONE);

            /* login init */
            ret = this.authManagerIns.loginInit(this.baseServiceIns);
            if (ret != 0)
            {
                Log.e(TAG, "login init is failed");
            }
        }

        return ret;
    }

    private int initCall()
    {
        int ret = 0;
        if (this.callManagerIns == null) {
            this.callManagerIns = new TupCallManager(new CallEventAdapt(), this.context);

            /*  根据应用类型加载动态库 */
            ret = this.callManagerIns.loadLibForTEEx();

            if (ret != 0){
                Log.e(TAG, "load Lib is failed");
                return ret;
            }

            this.callManagerIns.setAndroidObjects();

            this.callManagerIns.logStart(this.log_level, this.maxsize_kb, this.file_count, this.log_path);

            ret = this.callManagerIns.callInit();

            if (ret != 0) {
                Log.e(TAG, "call init is failed");
                return ret;
            }

            initCallDefaultConfig();
        }

        return ret;
    }

    private int initConfCtrl() {
        int ret;
        if (this.confManagerIns == null) {
            this.confManagerIns = TupConfManager.getIns(this.getNotifyMgr().getConfNotify(), this.context);
//            this.confManagerIns = TupConfManager.getIns(new ConfEventAdapt(), this.context);

            /* set log param */
            int logLevel = this.log_level;
            int logSize = this.maxsize_kb;
            int logCount = this.file_count;
            String logPath = this.log_path;
            this.confManagerIns.setLogParam(logLevel, logSize, logCount, logPath);

            int batchUpdate = 0;
            int saveParticipantList = 0;
            int connectCall = 1;
            int waitMsgpThread = 1;
            this.confManagerIns.setInitParam(batchUpdate, saveParticipantList, connectCall, waitMsgpThread);

            ret = this.confManagerIns.confInit(baseServiceIns);
            if (ret != 0) {
                Log.e(TAG, "conf init is failed");
                return ret;
            }
        }
        return 0;
    }

    /**
     * 初始化数据会议，需要加入数据会议时才初始化，以减少资源浪费
     */
    public void initDataConfSDK() {
        System.loadLibrary("TupConf");
        if (this.dataConfManagerIns == null) {
            this.dataConfManagerIns = Conference.getInstance();

            /* set log path and temp work path */
            String logPath = this.log_path;
            String tempPath = Environment.getExternalStorageDirectory() + File.separator + "DataConf" + File.separator + "temp";
            this.dataConfManagerIns.setPath(logPath, tempPath);

            /* set sdk log level */
            int mediaLogLevel = 3;
            int sdkLogLevel = 3;
            this.dataConfManagerIns.setLogLevel(mediaLogLevel, sdkLogLevel);

            //set device xdpi, ydpi
            DisplayMetrics dm = this.context.getResources().getDisplayMetrics();
            float xdpi = dm.xdpi;
            float ydpi = dm.ydpi;
            this.dataConfManagerIns.setDpi(xdpi, ydpi);

            String backCamera = "Back Camera";
            String frontCamera = "Front Camera";
            this.dataConfManagerIns.setCaneraName(backCamera, frontCamera);

            //init conf sdk
            boolean bSelfThread = false;
            int devType = ConfDefines.CONF_DEV_PHONE;
            this.dataConfManagerIns.initSDK(bSelfThread, devType);

        }
    }

    /**
     * 初始化本地通讯录
     */
    private void initContact()
    {
        if (this.contactsManagerIns == null)
        {
            this.contactsManagerIns = TupContactsManager.getIns(this.context
                    , this.getNotifyMgr().getContactsNotify());

            //Set Log parameters
            int logLevel = this.log_level;
            int maxSize = this.maxsize_kb;
            int fileCount = this.file_count;
            String logPath = this.log_path;
            this.contactsManagerIns.setLogParam(logLevel,maxSize,fileCount,logPath);
        }
    }

    /**
     * 去初始化数据会议数据
     */
    public void uninitDataConfSDK() {

        if (this.dataConfManagerIns != null) {
            this.dataConfManagerIns.exitSDK();
        }
        this.dataConfManagerIns = null;
    }

    private void initCallDefaultConfig()
    {
        //如果需要查询企业通讯录，则需要开启订阅网络地址本（终端向智真SC订阅）,默认关闭
        this.callManagerIns.enableCorporate_directory(TupBool.TUP_TRUE);

        setDefaultAudioAndVideoConfig();
        setDefaultSipConfig();
        setDefaultMediaConfig();
    }

    private void setDefaultAudioAndVideoConfig()
    {
        TupCallCfgAudioVideo tupCallCfgAudioVideo = getTupCallCfgAudioVideo();

        // 降噪参数ANR设置，取值1,2,3,4
        tupCallCfgAudioVideo.setAudioAnr(2);

        // 回声消除参数AEC设置，默认开启
        tupCallCfgAudioVideo.setAudioAec(1);

        // 自动增益补偿功能AGC设置，根据参数下发确定
        tupCallCfgAudioVideo.setAudioAgc(1);

        // 视频编码质量，默认设置 15，不可修改
        tupCallCfgAudioVideo.setVideoCoderQuality(15);

        // 视频关键帧间隔，默认设置 5，不可修改
        tupCallCfgAudioVideo.setVideoKeyframeinterval(5);

        // 初始化网络丢包百分率，用于设置给HME控制fec初始冗余，默认设置 100，不可修改
        tupCallCfgAudioVideo.setVideoNetLossRate(100);

        // SIP呼叫是否启用2833模式,取值为1使用2833模式、0使用dtmf模式，默认为1
        tupCallCfgAudioVideo.setAudioDtmfMode(1);

        // 设置抗丢包冗余
        tupCallCfgAudioVideo.setVideoErrorcorrecting(TupBool.TUP_TRUE);

        tupCallCfgAudioVideo.setVideoForceSingleH264Pt(TupBool.TUP_TRUE);

        // 设置视频编码优先级，目前默认设置 106
        tupCallCfgAudioVideo.setVideoCodec("106");

        // 设置默认分辨率，最小分辨率到SQCIF
        tupCallCfgAudioVideo.setVideoFramesize(8, 1, 11);

        // 设置默认码率、最大带宽
        tupCallCfgAudioVideo.setVideoDatarate(768, 1, 2048, 768);

        // 设置默认帧率和最小帧率
        tupCallCfgAudioVideo.setVideoFramerate(25, 10);

        // 设置视频Ars信息
        tupCallCfgAudioVideo.setVideoArs(1, 1, 0, 1, 1, 1);

        // 设置支持硬编，需要根据设备是否支持来设置
        //tupCallCfgAudioVideo.setVideoHdaccelerate(videoHdacceleRate);

        this.callManagerIns.setCfgAudioAndVideo(tupCallCfgAudioVideo);

        //设置默认横竖屏信息
        this.callManagerIns.setMboileVideoOrient(0, 1, 1, 0, 0, 0);
    }

    private void setDefaultSipConfig()
    {
        TupCallCfgSIP tupCallCfgSIP = TupMgr.getInstance().getTupCallCfgSIP();

        tupCallCfgSIP.setSipSessionTimerEnable(TupBool.TUP_TRUE);
        tupCallCfgSIP.setEnvUseagent("Huawei TE Mobile");
        tupCallCfgSIP.setEnvProductType(TupCallParam.CALL_E_PRODUCT_TYPE.CALL_E_PRODUCT_TYPE_MOBILE);
        tupCallCfgSIP.setRegSub(TupBool.TUP_TRUE);
        tupCallCfgSIP.setSipSupport100rel(TupBool.TUP_TRUE);
        tupCallCfgSIP.setEnableLogOut(TupBool.TUP_TRUE);
        tupCallCfgSIP.setReInviteNoSDPReplyMode(ReInviteNoSdpReplyMode.CALL_E_REINVITE_NOSDP_REPLY_STARTCALL_CAP);

        this.callManagerIns.setCfgSIP(tupCallCfgSIP);
    }

    private void setDefaultMediaConfig()
    {
        TupCallCfgMedia tupCallCfgMedia = TupMgr.getInstance().getTupCallCfgMedia();
        tupCallCfgMedia.setEnableBFCP(TupBool.TUP_TRUE);
        tupCallCfgMedia.setLoosePortNego(TupBool.TUP_TRUE);
        tupCallCfgMedia.setLooseIPNego(TupBool.TUP_TRUE);

        this.callManagerIns.setCfgMedia(tupCallCfgMedia);

        //this.callManagerIns.setAssistStreamEnable(true);

    }

}
