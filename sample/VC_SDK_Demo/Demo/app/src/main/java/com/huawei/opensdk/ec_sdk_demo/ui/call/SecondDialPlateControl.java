package com.huawei.opensdk.ec_sdk_demo.ui.call;

import android.view.View;

import com.huawei.opensdk.callmgr.CallMgr;
import com.huawei.opensdk.ec_sdk_demo.ui.base.BaseDialPlateControl;

/**
 * This class is about secondary dial plate control.
 */
public class SecondDialPlateControl extends BaseDialPlateControl
{
    private int callID;
    public SecondDialPlateControl(View plate, int callID)
    {
        super(plate);
        this.callID = callID;
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
            CallMgr.getInstance().reDial(this.callID, index);
        }
    }

    @Override
    protected void handleOnLongClick(View v)
    {
    }
}
