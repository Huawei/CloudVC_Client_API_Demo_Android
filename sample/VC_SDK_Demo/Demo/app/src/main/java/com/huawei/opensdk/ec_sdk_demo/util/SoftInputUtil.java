
package com.huawei.opensdk.ec_sdk_demo.util;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

final public class SoftInputUtil
{
    private SoftInputUtil()
    {

    }

    /**
     * 显示软键盘
     *
     * @param etInput 输入框
     */
    public static void displaySoftInput(EditText etInput)
    {
        if (null == etInput)
        {
            return;
        }
        etInput.dispatchTouchEvent(MotionEvent.obtain(System.currentTimeMillis(),
                System.currentTimeMillis(),
                MotionEvent.ACTION_DOWN,
                0,
                0,
                0));
        etInput.dispatchTouchEvent(MotionEvent.obtain(System.currentTimeMillis(),
                System.currentTimeMillis(),
                MotionEvent.ACTION_UP,
                0,
                0,
                0));
    }

    /**
     * 显示软键盘
     *
     * @param context Context
     * @param view    View
     */
    public static void showSoftInput(Context context, View view)
    {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInputFromWindow(view.getWindowToken(), 0, 0);
    }

    public static void showSoftBoard(View view)
    {
        final Context context = view.getContext();
        String name = Context.INPUT_METHOD_SERVICE;
        Object obj = context.getSystemService(name);

        ((InputMethodManager) obj).showSoftInput(view, 0);
    }

    /**
     * 显示软键盘
     */
    public static void showSoftInput(Context context)
    {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 隐藏软键盘
     */
    public static void hideSoftInput(Context context, View view)
    {
        if (null == view)
        {
            return;
        }

        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * 隐藏软键盘
     * @param context
     */
    public static void hideSoftInput(Context context)
    {
        InputMethodManager imm = (InputMethodManager)context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0,InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
