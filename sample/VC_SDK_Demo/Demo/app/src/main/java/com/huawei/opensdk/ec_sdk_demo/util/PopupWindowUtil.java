package com.huawei.opensdk.ec_sdk_demo.util;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

public class PopupWindowUtil
{
    private static final PopupWindowUtil mInstance = new PopupWindowUtil();

    private PopupWindowUtil()
    {
    }

    public static PopupWindowUtil getInstance()
    {
        return mInstance;
    }

    /**
     * 生成PopupWindow对象
     * @return PopupWindow
     */
    public synchronized PopupWindow generatePopupWindow(View view)
    {
        final PopupWindow popupWindow = new PopupWindow(view,
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.setOnKeyListener(new View.OnKeyListener()
        {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (keyCode == KeyEvent.KEYCODE_MENU && popupWindow.isShowing())
                {
                    dismissPopupWindow(popupWindow);
                    return true;
                }
                return false;
            }
        });

        return popupWindow;
    }

    public void dismissPopupWindow(PopupWindow popupWindow)
    {
        if (null != popupWindow)
        {
            popupWindow.dismiss();
        }
    }

}
