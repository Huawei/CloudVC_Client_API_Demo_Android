package com.huawei.opensdk.ec_sdk_demo.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Selection;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.huawei.opensdk.commonservice.common.LocContext;
import com.huawei.opensdk.ec_sdk_demo.R;


/**
 * This class is about unit transform
 */
public final class CommonUtil
{
    /**
     * 页面跳转时,如果传递了数据包Bundle，统一获取数据的key
     */
    public static final String INTENT_TRANSFER_KEY = "intent_transfer_key";
    /**
     * handler传递message数据时,如果传递了数据包Bundle，统一获取数据的key
     */
    public static final String MESSAGE_TRANSFER_KEY = "handler_transfer_key";
    /**
     * 端口号码EditText允许最大输入长度
     */
    public static final int MAX_INPUT_LENGTH_PORT_EDITTEXT = 5;
    /**
     * 号码EditText允许最大输入长度
     */
    public static final int MAX_INPUT_LENGTH_NUMBER_EDITTEXT = 21;
    /**
     * 字符EditText允许最大输入长度
     */
    public static final int MAX_INPUT_LENGTH_CHARACTER_EDITTEXT = 64;
    /**
     * 新建会议主题EditText允许最大输入长度
     */
    public static final int MAX_INPUT_LENGTH_SUBJECT_EDITTEXT = 32;
    /**
     * 文本框EditText允许输入的类型： 号码
     */
    public static final int INPUT_TYPE_PHONE = 0x01;
    /**
     * 文本框EditText允许输入的类型： 数字
     */
    public static final int INPUT_TYPE_NUMBER = 0x02;
    /**
     * EditText不限制输入长度
     */
    public static final int NO_MAX_INPUT_LENGTH_EDITTEXT = -1;


    /**
     * PX与DIP单位之间转换使用的常量值
     */
    // private static final float PX_DIP_TRANSFORM_VALUE = 0.5f;

    private CommonUtil()
    {
    }

    /**
     * 设置EditText控件
     * 默认只允许输入数字类型，默认光标置于文本末尾, 默认限制输入长度为20个字符
     *
     * @param length
     * @param et
     */
    public static void processEditTextWithNumber(EditText et,
                                                 String text, int length, int inputType)
    {
        if (null == et)
        {
            return;
        }
        //输入类型
        if (inputType == INPUT_TYPE_PHONE)
        {
            et.setInputType(InputType.TYPE_CLASS_PHONE);
        }
        else if (inputType == INPUT_TYPE_NUMBER)
        {
            et.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
        //长度
        if (NO_MAX_INPUT_LENGTH_EDITTEXT != length)
        {
            et.setFilters(new InputFilter[]
                    {new InputFilter.LengthFilter(length)});
        }
        et.setText(text);
        Selection.setSelection(et.getText(), et.getText().length());
    }

    /**
     * 生成会议主题
     */
    public static String generateSubject(String name)
    {
        int source = R.string.conf_create_init_subject_suffix;
        return LocContext.getString(source, name);
    }

    /**
     * 切换屏幕横竖
     *
     * @param isLandscape 是横屏
     */
    public static void setScreenOrientation(Context context, boolean isLandscape)
    {
        //TODO 暂时注释，规避CodeC
        ((Activity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    public static void hideSoftInput(Context context)
    {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

}
