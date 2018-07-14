package com.huawei.opensdk.ec_sdk_demo.util;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.huawei.opensdk.ec_sdk_demo.R;

/**
 * This class is about dialog util.
 */
public class DialogUtil
{
    private DialogUtil()
    {
    }

    public static AlertDialog generateDialog(Context context, int content,
                                             DialogInterface.OnClickListener positiveListener)
    {
        return new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.msg_tip))
                .setMessage(context.getString(content))
                .setPositiveButton(context.getString(R.string.conform), positiveListener)
                .setNegativeButton(context.getString(R.string.cancel), null).create();
    }

    public static AlertDialog generateDialog(Context context, int content, int positive, int negative,
                                             DialogInterface.OnClickListener positiveListener,
                                             DialogInterface.OnClickListener negativeListener)
    {
        return new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.msg_tip))
                .setMessage(context.getString(content))
                .setPositiveButton(context.getString(positive), positiveListener)
                .setNegativeButton(context.getString(negative), negativeListener).create();
    }
}
