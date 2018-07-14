package com.huawei.opensdk.ec_sdk_demo.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RoundCornerPhotoView extends ImageView
{
    // 套在遮罩形状外的一层
    private boolean useBigMaskBg = false;
    /** 新UCD设计使用，是否使用圆形头像 */
    private boolean useCircleMaskBg = false;
    // private boolean needMask = false;

    public RoundCornerPhotoView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setScaleType(ScaleType.FIT_XY);
    }

    @Override
    public void setImageBitmap(Bitmap bitmap)
    {
        // 大的圆角图用的bg是大的，内存里存的是用小的
        if (useBigMaskBg)
        {
            // Bitmap roundBm = HeadPhotoUtil.getIns().getRoundCornerBgBig();
            // bitmap = BitmapUtil.getRoundCornerBitmap(bitmap, roundBm, true);
//            bitmap = BitmapUtil.getRoundCornerBitmap(bitmap, getBigMaskBg(), true);
        }

        if (useCircleMaskBg)
        {
//            bitmap = maskCircleBg(bitmap);
        }

        super.setImageBitmap(bitmap);
    }

    @Override
    public void setImageResource(int resId)
    {
        // Bitmap defaultBitmap = HeadPhotoUtil.getIns().getDefaultBitmap(resId);
        String key = createKey(resId);
//        Bitmap defaultBitmap = HeadCache.getIns().getDefaultBitmap(key);

//        if (defaultBitmap == null)
//        {
//            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resId);
//            // Bitmap roundBm = HeadPhotoUtil.getIns().getRoundCornerBgSmall();
//            // defaultBitmap = BitmapUtil.getRoundCornerBitmap(bitmap, roundBm);
//            defaultBitmap = BitmapUtil.getRoundCornerBitmap(bitmap, getBigMaskBg(), useBigMaskBg);
//            // HeadCache.ins().setDefaultBitmap(resId, defaultBitmap);
//            HeadCache.getIns().setDefaultBitmap(key, defaultBitmap);
//        }

//        if (useCircleMaskBg)
//        {
//            defaultBitmap = maskCircleBg(defaultBitmap);
//        }
//
//        super.setImageBitmap(defaultBitmap);
    }

//    private Bitmap maskCircleBg(Bitmap bitmap)
//    {
//        int maskBgId = R.drawable.mask_bg;
//        Bitmap mask = BitmapFactory.decodeResource(getResources(), maskBgId);
//        return BitmapUtil.mixtureBitmap(mask, bitmap);
//    }

    private String createKey(int resId)
    {
        String key = String.valueOf(resId);
        return useCircleMaskBg ? key + "_circle" : key;
    }

//    private Bitmap getBigMaskBg()
//    {
//        if (useCircleMaskBg)
//        {
//            return HeadCache.getIns().getCircleBgBig();
//        }
//        else
//        {
//            return HeadCache.getIns().getRoundCornerBgSmall();
//        }
//    }

    public void setBigMaskImage()
    {
        useBigMaskBg = true;
    }

    public void setUseCircleMaskBg(boolean useCircleMaskBg)
    {
        this.useCircleMaskBg = useCircleMaskBg;
    }

    /*public void setNeedMask(boolean needMask)
    {
        this.needMask = needMask;
    }*/
}
