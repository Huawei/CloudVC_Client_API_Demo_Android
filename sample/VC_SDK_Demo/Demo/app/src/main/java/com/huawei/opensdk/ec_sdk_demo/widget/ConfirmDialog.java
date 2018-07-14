package com.huawei.opensdk.ec_sdk_demo.widget;

import android.content.Context;

import com.huawei.opensdk.ec_sdk_demo.R;


/**
 * This class is about confirmation dialog box, left and right two buttons
 * 确认对话框，左右两个按钮。
 * 该对话框，不带标题，已经弃用，请使用ConfirmTitleDialog替代。
 */
public class ConfirmDialog extends BaseDialog
{
    public ConfirmDialog(Context context, String message)
    {
        super(context);
        setContentView(R.layout.dialog_confirm_title);
        setMessage(message);
        setCanceledOnTouchOutside(false);
        setLeftButtonListener(null);
        setRightButtonListener(null);
    }

    public ConfirmDialog(Context context, int resId)
    {
        this(context, context.getString(resId));
    }

}
