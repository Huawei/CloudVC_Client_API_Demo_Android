package com.huawei.opensdk.ec_sdk_demo.ui.call;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;

import com.huawei.opensdk.callmgr.CallMgr;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.util.DialogUtil;

public class CallOutActivity extends BaseMediaActivity
{
    private AlertDialog mDialog;
    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            if (KeyEvent.KEYCODE_BACK == msg.what)
            {
                mDialog = DialogUtil.generateDialog(CallOutActivity.this, R.string.ntf_end_call,
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                CallMgr.getInstance().endCall(mCallID);
                            }
                        });
                mDialog.show();
            }
        }
    };

    @Override
    public void onClick(View v)
    {
        super.onClick(v);
        if (R.id.reject_btn == v.getId())
        {
            CallMgr.getInstance().endCall(mCallID);
            finish();
        }
    }

    @Override
    public void initializeComposition()
    {
        super.initializeComposition();

        mRejectBtn.setOnClickListener(this);

        mCallNumberTv.setText(null == mCallNumber ? "" : mCallNumber);
        mCallNameTv.setText(null == mCallNumber ? "" : mCallNumber);
    }

    @Override
    public void onBackPressed()
    {
        mHandler.sendEmptyMessage(KeyEvent.KEYCODE_BACK);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        dismissDialog(mDialog);
        mHandler.removeCallbacksAndMessages(null);
    }
}
