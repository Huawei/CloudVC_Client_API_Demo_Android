package com.huawei.opensdk.servicemgr;

import android.content.Context;
import android.os.Environment;

import com.huawei.opensdk.callmgr.CallMgr;
import com.huawei.opensdk.commonservice.common.LocContext;
import com.huawei.opensdk.commonservice.util.CrashUtil;
import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.contactmgr.ContactMgr;
import com.huawei.opensdk.demoservice.MeetingMgr;
import com.huawei.opensdk.loginmgr.LoginMgr;
import com.huawei.opensdk.sdkwrapper.manager.TupEventNotifyMgr;
import com.huawei.opensdk.sdkwrapper.manager.TupFeatureParam;
import com.huawei.opensdk.sdkwrapper.manager.TupMgr;

import java.io.File;

/**
 * This class is about init and uninit business component classes.
 * 初始化与去初始化业务组件类
 */
public class ServiceMgr
{

    private static final String TAG = ServiceMgr.class.getSimpleName();

    /**
     * Instance object of ServiceMgr component.
     * 新建一个ServiceMgr对象
     */
    private static final ServiceMgr serviceMgr = new ServiceMgr();

    /**
     * The context
     * 上下文
     */
    private Context context;

    /**
     * The app path
     * APP路径
     */
    private String appPath;

    /* 应用程序根据自身业务支持情况进行设置 */
    private boolean isSupportAudioAndVideoCall = true;
    private boolean isSupportAudioAndVideoConf = true;
    private boolean isSupportDataConf = true;
    private boolean isSupportAddressbook = true;
    private boolean isSupportContact = true;

    /**
     * This method is used to get instance object of ServiceMgr.
     * 获取ServiceMgr对象实例
     * @return ImMgr Return instance object of ServiceMgr
     *               返回一个ServiceMgr对象实例
     */
    public static ServiceMgr getServiceMgr()
    {
        return serviceMgr;
    }

    /**
     * This method is used to init service.
     * 初始化业务组件
     * @param context
     * @param appPath
     * @return
     */
    public boolean startService(Context context, String appPath)
    {
        int ret;

        /*init crash util*/
        CrashUtil.getInstance().init(context);
        /*set demo log path */
        LogUtil.setLogPath("VCSDKDemo");

        LocContext.init(context);

        LogUtil.i(TAG, "sdk init is begin.");

        TupMgr tupMgr = TupMgr.getInstance();

        /* Step 1, set log param */
        int logLevel = 3; //info level
        int maxSizeKB = 1024 * 4;
        int fileCount = 1;
        String logPath = Environment.getExternalStorageDirectory() + File.separator + "VCSDKDemo" + "/";
        tupMgr.setLogParam(logLevel, maxSizeKB, fileCount, logPath);

        /* Step 2, reg service event notify process */
        TupEventNotifyMgr notifyMgr = new TupEventNotifyMgr();

        notifyMgr.setLoginNotify(LoginMgr.getInstance());
        notifyMgr.setCallNotify(CallMgr.getInstance());
        notifyMgr.setConfNotify(MeetingMgr.getInstance());
        //notifyMgr.setDataConfNotify(DataConfMgr.getInstance());
        notifyMgr.setContactsNotify(ContactMgr.getInstance());

        tupMgr.regEventNotify(notifyMgr);

        /* Step 3, init sdk */
        TupFeatureParam featureParam = new TupFeatureParam(
                this.isSupportAudioAndVideoCall,
                this.isSupportAudioAndVideoConf,
                this.isSupportDataConf,
                this.isSupportAddressbook,
                this.isSupportContact
        );
        ret = tupMgr.sdkInit(context, appPath, featureParam);
        if (ret != 0)
        {
            LogUtil.e(TAG, "sdk init failed, return " + ret);
            return false;
        }
        LogUtil.i(TAG, "sdk init is success.");

        /*Step 4, config service param */
        ret = configServiceParam();
        if (ret != 0)
        {
            LogUtil.e(TAG, "config service param failed, return " + ret);
            return false;
        }
        LogUtil.i(TAG, "config service param is success.");

        return true;

    }

    /**
     * This method is used to uninit service.
     * 去初始化基础组件
     */
    public void stopService()
    {
        //待实现
    }

    /**
     * This method is used to config service param.
     * 各种服务的配置
     * @return
     */
    private int configServiceParam()
    {
        /* config call service param */
        CallMgr.getInstance().configCallServiceParam();

        //待实现，配置各种业务的基本配置
        return 0;
    }
}

