package com.huawei.opensdk.ec_sdk_demo.widget;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.huawei.opensdk.ec_sdk_demo.R;


/**
 * This class is about tie three buttons dialog box
 * 并列三个按钮对话框
 */
public class TripleDialog extends BaseDialog
{

    private final TextView fourButton;
    private TextView thirdButton;
    public TripleDialog(Context context)
    {
        super(context);
        setContentView(R.layout.dialog_triple);
        thirdButton = (TextView) findViewById(R.id.dialog_third_button);
        fourButton = (TextView) findViewById(R.id.dialog_four_button);
        setSingleButtonListener(null);
    }
    public void setTitle(String title){
        TextView titleTV = (TextView) findViewById(R.id.dialog_header);
        if (titleTV != null && title != null)
        {
            titleTV.setText(title);
        }
    }
    public void setTitle(int resid)
    {
        TextView titleTV = (TextView) findViewById(R.id.dialog_header);
        // TODO 暂时注释，规避CodeC
        if (titleTV != null/* && resid != 0*/)
        {
            titleTV.setText(resid);
        }
    }
    public void hideDownloadButton(){
        View bt = findViewById(R.id.dialog_leftbutton);
        if(bt != null){
            bt.setVisibility(View.GONE);
        }
    }

    public void hideDeleteButton(){
        View bt = findViewById(R.id.dialog_rightbutton);
        if(bt != null){
            bt.setVisibility(View.GONE);
        }
    }

    public void setThirdButtonListener(final View.OnClickListener onClickListener)
    {
        thirdButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dismiss();
                onClickListener.onClick(v);
            }
        });
    }

    public void setFourButtonListener(final View.OnClickListener onClickListener)
    {
        fourButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dismiss();
                onClickListener.onClick(v);
            }
        });
    }

    public void setThirdText(int str)
    {
        thirdButton.setVisibility(View.VISIBLE);
        thirdButton.setText(str);
    }

    public void setFourText(int fourText)
    {
        fourButton.setVisibility(View.VISIBLE);
        fourButton.setText(fourText);
    }
}
