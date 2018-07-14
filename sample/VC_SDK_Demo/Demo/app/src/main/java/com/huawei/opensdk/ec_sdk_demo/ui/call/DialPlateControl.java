package com.huawei.opensdk.ec_sdk_demo.ui.call;

import android.view.KeyEvent;
import android.view.View;

import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.ui.base.BaseDialPlateControl;

/**
 * This class is about dial plate control.
 */
public class DialPlateControl extends BaseDialPlateControl
{
    public DialPlateControl(View plate)
    {
        super(plate);
    }

    @Override
    protected void handleOnClick(View v)
    {
        Integer obj = (Integer) v.getTag();
        int index = obj.intValue();
        if (index != -1)
        {
            mNumInputEt.append(CODE_ARRAY[index]);
            mNumInputEt.setSelection(mNumInputEt.length());
        }
    }

    @Override
    protected void handleOnLongClick(View v)
    {
        if (R.id.call_zero == v.getId())
        {
            mNumInputEt.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_PLUS));
            mNumInputEt.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,KeyEvent.KEYCODE_PLUS));
        }
    }

    public String getCallNumber()
    {
        return mNumInputEt.getText().toString();
    }

    public void setCallNumber(String callNumber)
    {
        mNumInputEt.setText(callNumber);
    }
}
