package com.huawei.opensdk.ec_sdk_demo.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.huawei.opensdk.ec_sdk_demo.R;


/**
 * This class is about description
 */
public class BaseDialog extends Dialog
{
    private TextView singleButton;

    private TextView leftButton;

    private TextView rightButton;

    private ListView listView;

    public BaseDialog(Context context)
    {
        super(context, R.style.Theme_dialog);
    }

    protected void setTitle(String title)
    {
        TextView titleTV = (TextView) findViewById(R.id.dialog_title);
        if (titleTV != null && title != null)
        {
            titleTV.setText(title);
        }
    }

    protected void setMessage(String content)
    {
        TextView textView = (TextView) findViewById(R.id.dialog_message);
        if (textView != null && content != null)
        {
            textView.setText(content);
        }
    }

    protected void setMessage(int resId)
    {
        setMessage(getContext().getString(resId));
    }

    /**
     * 设置对话框单个按钮点击事件
     *
     * @param lsn 点击事件
     */
    public void setSingleButtonListener(View.OnClickListener lsn)
    {
        setSingleButtonListener(lsn, true);
    }

    /**
     * 设置对话框单个按钮点击事件，同时设置是否先关闭对话框
     *
     * @param lsn          点击事件
     * @param dismissFirst 是否先关闭对话框
     */
    public void setSingleButtonListener(final View.OnClickListener lsn, boolean dismissFirst)
    {
        setButtonListener(getSingleButton(), lsn, dismissFirst);
    }

    /**
     * 设置对话框左边确认按钮点击事件
     *
     * @param lsn 点击事件
     */
    public void setLeftButtonListener(final View.OnClickListener lsn)
    {
        setLeftButtonListener(lsn, true);
    }

    /**
     * 设置对话框左边按钮点击事件，同时设置是否先关闭对话框
     *
     * @param lsn          点击事件
     * @param dismissFirst 是否先关闭对话框
     */
    public void setLeftButtonListener(final View.OnClickListener lsn, boolean dismissFirst)
    {
        setButtonListener(getLeftButton(), lsn, dismissFirst);
    }

    /**
     * 设置对话框右边按钮点击事件
     *
     * @param lsn 点击事件
     */
    public void setRightButtonListener(final View.OnClickListener lsn)
    {
        setRightButtonListener(lsn, true);
    }

    /**
     * 设置对话框右边按钮点击事件，同时设置是否先关闭对话框
     *
     * @param lsn          点击事件
     * @param dismissFirst 是否先关闭对话框
     */
    public void setRightButtonListener(final View.OnClickListener lsn, boolean dismissFirst)
    {
        setButtonListener(getRightButton(), lsn, dismissFirst);
    }

    private void setButtonListener(View button, final View.OnClickListener lsn, boolean dismissFirst)
    {
        if (button == null)
        {
            return;
        }

        if (dismissFirst)
        {
            button.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    dismiss();
                    if (lsn != null)
                    {
                        lsn.onClick(v);
                    }
                }
            });
        }
        else
        {
            button.setOnClickListener(lsn);
        }
    }

    public void setSingleButtonText(int textId)
    {
        getSingleButton().setText(textId);
    }

    public void setSingleButtonText(String text)
    {
        getSingleButton().setText(text);
    }

    public void setLeftBackgroundResource(int resId)
    {
        getLeftButton().setBackgroundResource(resId);
    }

    public void setRightBackgroundResource(int resId)
    {
        getRightButton().setBackgroundResource(resId);
    }

    public void setLeftText(String text)
    {
        getLeftButton().setText(text);
    }

    public void setLeftText(int resId)
    {
        getLeftButton().setText(resId);
    }

    public void setRightText(String text)
    {
        getRightButton().setText(text);
    }

    public void setRightText(int resId)
    {
        getRightButton().setText(resId);
    }

    public void dismiss()
    {
        if (isShowing())
        {
            super.dismiss();
        }
    }


    public void show()
    {
        if (!isShowing())
        {
            super.show();
        }
    }

    private TextView getSingleButton()
    {
        if (singleButton == null)
        {
            singleButton = (TextView) findViewById(R.id.dialog_single_button);
        }
        return singleButton;
    }

    private TextView getLeftButton()
    {
        if (leftButton == null)
        {
            leftButton = (TextView) findViewById(R.id.dialog_leftbutton);
        }
        return leftButton;

    }

    public TextView getRightButton()
    {
        if (rightButton == null)
        {
            rightButton = (TextView) findViewById(R.id.dialog_rightbutton);

        }
        return rightButton;
    }

}
