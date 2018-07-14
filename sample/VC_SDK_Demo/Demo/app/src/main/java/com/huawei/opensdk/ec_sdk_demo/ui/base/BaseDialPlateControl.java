package com.huawei.opensdk.ec_sdk_demo.ui.base;

import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.huawei.opensdk.ec_sdk_demo.R;

/**
 * This abstract class is about base dial plate control.
 */
public abstract class BaseDialPlateControl implements View.OnClickListener, View.OnLongClickListener
{
    protected static final String[] CODE_ARRAY = {"0", "1",
            "2", "3", "4", "5", "6", "7", "8", "9", "*", "#"};

    protected static final int[] NUM_ID_ARRAY;

    protected View dialPlateView;
    protected EditText mNumInputEt;

    static
    {
        NUM_ID_ARRAY = new int[12];
        NUM_ID_ARRAY[0] = R.id.call_zero;
        NUM_ID_ARRAY[1] = R.id.callOne;
        NUM_ID_ARRAY[2] = R.id.callTwo;
        NUM_ID_ARRAY[3] = R.id.callThree;
        NUM_ID_ARRAY[4] = R.id.callFour;
        NUM_ID_ARRAY[5] = R.id.callFive;
        NUM_ID_ARRAY[6] = R.id.callSix;
        NUM_ID_ARRAY[7] = R.id.callSeven;
        NUM_ID_ARRAY[8] = R.id.callEight;
        NUM_ID_ARRAY[9] = R.id.callNine;
        NUM_ID_ARRAY[10] = R.id.callX;
        NUM_ID_ARRAY[11] = R.id.callJ;
    }

    public BaseDialPlateControl(View plate)
    {
        dialPlateView = plate;
        mNumInputEt = (EditText) plate.findViewById(R.id.callNumber);
        mNumInputEt.setInputType(InputType.TYPE_NULL);
        mNumInputEt.setSelectAllOnFocus(false);
        mNumInputEt.setSelected(false);

        ImageView[] layouts = new ImageView[NUM_ID_ARRAY.length];
        for (int i = 0; i < NUM_ID_ARRAY.length; i++)
        {
            layouts[i] = (ImageView) plate.findViewById(NUM_ID_ARRAY[i]);
            layouts[i].setOnClickListener(this);
            layouts[i].setOnLongClickListener(this);
            layouts[i].setTag(i);
        }
    }

    public void showDialPlate()
    {
        dialPlateView.setVisibility(View.VISIBLE);
    }

    public void hideDialPlate()
    {
        mNumInputEt.setText("");
        mNumInputEt.clearFocus();
        dialPlateView.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v)
    {
        handleOnClick(v);
    }

    protected abstract void handleOnClick(View v);

    @Override
    public boolean onLongClick(View v)
    {
        handleOnLongClick(v);
        return true;
    }

    protected abstract void handleOnLongClick(View v);
}
