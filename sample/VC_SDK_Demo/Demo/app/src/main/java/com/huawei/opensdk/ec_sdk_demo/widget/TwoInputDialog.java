package com.huawei.opensdk.ec_sdk_demo.widget;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.huawei.opensdk.ec_sdk_demo.R;

public class TwoInputDialog extends BaseDialog
{
    private EditText inputET1;
    private EditText inputET2;
    private ImageView clearNameIV;
    private ImageView clearNumberIV;


    public TwoInputDialog(Context context)
    {
        super(context);
        setContentView(R.layout.three_input_layout);
        setTitle(R.string.add_member);
        setLeftButtonListener(null);
        setCanceledOnTouchOutside(false);
        inputET1 = (EditText) findViewById(R.id.dialog_edittext);
        inputET2 = (EditText) findViewById(R.id.dialog_edittext1);

        clearNameIV = (ImageView) findViewById(R.id.ivClearText);
        clearNumberIV = (ImageView) findViewById(R.id.ivClearText1);
        clearNameIV.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                inputET1.setText("");
            }
        });
        clearNumberIV.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                inputET2.setText("");
            }
        });
    }

    public String getInput1()
    {
        return inputET1.getText() == null ? "" : inputET1.getText().toString().trim();
    }

    public String getInput2()
    {
        return inputET2.getText() == null ? "" : inputET2.getText().toString().trim();
    }

    public void setHint1(int hint1)
    {
        inputET1.setHint(hint1);
    }

    public void setHint2(int hint2)
    {
        inputET2.setHint(hint2);
    }
}
