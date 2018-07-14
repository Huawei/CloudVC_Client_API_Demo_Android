package com.huawei.opensdk.commonservice.common;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import java.io.File;

/**
 * This class is about context.
 * 上下文数据类
 */
public class LocContext
{
    private static Context context;

    public static void init(Context context)
    {
        LocContext.context = context;
    }

    public static Context getContext()
    {
        return context;
    }

    public static ContentResolver getContentResolver()
    {
        return context.getContentResolver();
    }

    public static Resources getResources()
    {
        return context.getResources();
    }

    public static String getString(int resId)
    {
        return context.getString(resId);
    }

    public static String getString(int resId, Object... formatArgs)
    {
        return context.getString(resId, formatArgs);
    }

    public static File getFilesDir()
    {
        return context.getFilesDir();
    }

    public static String getPackageName()
    {
        return context.getPackageName();
    }

    public static int getColor(int res)
    {
        return getResources().getColor(res);
    }

    // 这个接口放在这里欠考虑
    public static DisplayMetrics getDisplayMetrics()
    {
        return getResources().getDisplayMetrics();
    }
}
