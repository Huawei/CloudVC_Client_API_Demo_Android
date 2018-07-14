package com.huawei.opensdk.ec_sdk_demo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.util.DisplayUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is about expression bottom indicator view
 * 表情底部指示器视图
 */
public class EmojiIndicatorView extends LinearLayout
{

    private Context mContext;
    private List<View> mImageViews;
    private int pointSize;
    private int marginLeft;

    public EmojiIndicatorView(Context context)
    {
        this(context, null);
    }

    public EmojiIndicatorView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public EmojiIndicatorView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        mContext = context;
        int size = 6;
        pointSize = DisplayUtils.dp2px(context, size);
        int marginSize = 15;
        marginLeft = DisplayUtils.dp2px(context, marginSize);
    }

    /**
     * 初始化指示器
     * @param count 指示器的数量
     */
    public void initIndicator(int count)
    {
        mImageViews = new ArrayList<View>();
        this.removeAllViews();
        LayoutParams lp;
        for (int i = 0; i < count; i++)
        {
            View v = new View(mContext);
            lp = new LayoutParams(pointSize, pointSize);
            if (i != 0)
            {
                lp.leftMargin = marginLeft;
            }
            v.setLayoutParams(lp);
            if (i == 0)
            {
                v.setBackgroundResource(R.drawable.shape_bg_indicator_point_select);
            }
            else
            {
                v.setBackgroundResource(R.drawable.shape_bg_indicator_point_nomal);
            }
            mImageViews.add(v);
            this.addView(v);
        }
    }

    //设计表情指示器点亮逻辑
    public final void playPointByIndex(int index)
    {
        if (0 > index || index >= mImageViews.size())
        {
            index = 0;
        }

        for (View view : mImageViews)
        {
            if (index == mImageViews.indexOf(view))
            {
                turnOn(view);
            }
            else
            {
                turnOff(view);
            }
        }
    }

    private static void turnOn(View view)
    {
        view.setBackgroundResource(R.drawable.shape_bg_indicator_point_select);
    }

    private static void turnOff(View view)
    {
        view.setBackgroundResource(R.drawable.shape_bg_indicator_point_nomal);
    }
}
