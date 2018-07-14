package com.huawei.opensdk.ec_sdk_demo.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * This class is about To cancel the focus of the happy lights,
 * while running a number of otherwise can only run one
 * 取消跑马灯的焦点，同时可以跑多个
 * 否则只能跑一个
 *
 */
public class MarqueeTextView extends TextView
{
    public MarqueeTextView(Context con)
    {
        super(con);
    }

    public MarqueeTextView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean isFocused()
    {
        return true;
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction,
            Rect previouslyFocusedRect)
    {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
    }
}