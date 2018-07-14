package com.huawei.opensdk.ec_sdk_demo.widget;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.huawei.opensdk.ec_sdk_demo.R;

public class ThreeInputDialog extends TwoInputDialog
{
    private EditText inputET3;
    private ImageView clearInput3;
    private RelativeLayout relativeLayout;

    public ThreeInputDialog(Context context)
    {
        super(context);
        setTitle(R.string.add_member);
        setLeftButtonListener(null);
        setCanceledOnTouchOutside(false);
        inputET3 = (EditText) findViewById(R.id.dialog_edittext3);
        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout03);
        relativeLayout.setVisibility(View.VISIBLE);

        clearInput3 = (ImageView) findViewById(R.id.ivClearText3);
        clearInput3.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                inputET3.setText("");
            }
        });
    }

    public String getInput3()
    {
        return inputET3.getText().toString();
    }

    public void setHint3(int hint3)
    {
        inputET3.setHint(hint3);
    }
}
