package com.huawei.opensdk.ec_sdk_demo.util;

import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * This class is about picture and layout dimensions and pixel conversion tools
 * 图片和布局尺寸和像素的转换工具类
 */
public final class SizeUtil
{
    private SizeUtil()
    {
    }

    /**
     * 方法名称：dipToPx
     * 作者：YouJun
     * 方法描述：dip单位转换为px (结果非绝对精确，需要微调)
     * 输入参数：@param context
     * 输入参数：@param dipValue
     * 输入参数：@return
     * 返回类型：int
     * 备注：
     */
    public static int dipToPx(float dipValue)
    {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return Math.round(dipValue * metrics.density + 0.5f);
    }

    /**
     * 方法名称：pxTodip
     * 作者：YouJun
     * 方法描述：px单位转换为dip (结果非绝对精确，需要微调)
     * 输入参数：@param context
     * 输入参数：@param pxValue
     * 输入参数：@return
     * 返回类型：int
     * 备注：
     */
    public static int pxToDip(float pxValue)
    {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return Math.round(pxValue / metrics.density + 0.5f);
    }

}
