package com.huawei.opensdk.ec_sdk_demo.util;

import android.util.Log;

import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;

public class ExceptionHandler extends CustomExceptionHandler
{
    @Override
    public void uncaughtException(Thread thread, Throwable throwable)
    {
        Log.e(UIConstants.DEMO_TAG, throwable.getMessage());
        super.uncaughtException(thread, throwable);
    }
}
