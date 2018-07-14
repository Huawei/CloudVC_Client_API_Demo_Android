package com.huawei.opensdk.ec_sdk_demo.logic.login;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.huawei.opensdk.commonservice.common.LocContext;
import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;
import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.contactmgr.ContactMgr;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.ui.IntentConstant;
import com.huawei.opensdk.ec_sdk_demo.ui.base.ActivityStack;
import com.huawei.opensdk.ec_sdk_demo.ui.login.LoginActivity;
import com.huawei.opensdk.ec_sdk_demo.util.ActivityUtil;
import com.huawei.opensdk.loginmgr.ILoginEventNotifyUI;
import com.huawei.opensdk.loginmgr.LoginConstant;

import java.util.concurrent.Executors;


public class LoginFunc implements ILoginEventNotifyUI
{
    private static final int LOGIN_SUCCESS = 100;
    private static final int LOGIN_FAILED = 101;
    private static final int LOGOUT = 102;

    private static LoginFunc INSTANCE = new LoginFunc();

    public static ILoginEventNotifyUI getInstance()
    {
        return INSTANCE;
    }

    private Handler mMainHandler = new Handler(Looper.getMainLooper())
    {
        @Override
        public void handleMessage(Message msg)
        {
            LogUtil.i(UIConstants.DEMO_TAG, "what:" + msg.what);
            parallelHandleMessage(msg);
        }
    };

    private void sendHandlerMessage(int what, Object object)
    {
        if (mMainHandler == null)
        {
            return;
        }
        Message msg = mMainHandler.obtainMessage(what, object);
        mMainHandler.sendMessage(msg);
    }

    @Override
    public void onLoginEventNotify(LoginConstant.LoginUIEvent evt, int reason, String description)
    {
        switch (evt)
        {
            case LOGIN_SUCCESS:
                LogUtil.i(UIConstants.DEMO_TAG, "login success");
                sendHandlerMessage(LOGIN_SUCCESS, description);
                break;
            case LOGIN_FAILED:
                LogUtil.i(UIConstants.DEMO_TAG, "login fail");
                sendHandlerMessage(LOGIN_FAILED, description);
                break;

            case LOGOUT:
                LogUtil.i(UIConstants.DEMO_TAG, "logout");
                sendHandlerMessage(LOGOUT, description);
                break;
            default:
                break;
        }
    }

    /**
     * handle message
     * @param msg
     */
    private void parallelHandleMessage(Message msg)
    {
        switch (msg.what)
        {
            case LOGIN_SUCCESS:
                ContactMgr.getInstance().setConfig();
                LogUtil.i(UIConstants.DEMO_TAG, "login success,notify UI!");
                Toast.makeText(LocContext.getContext(), ((String) msg.obj), Toast.LENGTH_SHORT).show();
                ActivityUtil.startActivity(LocContext.getContext(), IntentConstant.MAIN_ACTIVITY_ACTION);
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.LOGIN_SUCCESS, null);
                Executors.newSingleThreadExecutor().execute(new Runnable()
                {
                    @Override
                    public void run()
                    {
                    }
                });
                break;
            case LOGIN_FAILED:
                LogUtil.i(UIConstants.DEMO_TAG, "login failed,notify UI!");
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.LOGIN_FAILED, null);
                Toast.makeText(LocContext.getContext(), ((String) msg.obj), Toast.LENGTH_SHORT).show();
                break;
            case LOGOUT:
                LogUtil.i(UIConstants.DEMO_TAG, "logout success,notify UI!");
                ActivityStack.getIns().popupAbove(LoginActivity.class);
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.LOGOUT, null);
                Toast.makeText(LocContext.getContext(), ((String) msg.obj), Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

}
