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


public class CallInActivity extends BaseMediaActivity
{
    private AlertDialog mDialog;
    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            if (KeyEvent.KEYCODE_BACK == msg.what)
            {
                mDialog = DialogUtil.generateDialog(CallInActivity.this, R.string.ntf_end_call,
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                if (mIsConfCall)
                                {
                                    finish();
                                }
                                CallMgr.getInstance().endCall(mCallID);
                            }
                        });
                mDialog.show();
            }
        }
    };

    @Override
    public void initializeData()
    {
        super.initializeData();
    }

    @Override
    public void initializeComposition()
    {
        super.initializeComposition();

        mRejectBtn.setVisibility(View.VISIBLE);
        mAudioAcceptCallArea.setVisibility(View.VISIBLE);
        mAudioAcceptCallArea.setActivated(false);

        if (mIsVideoCall == true)
        {
            mVideoAcceptCallArea.setVisibility(View.VISIBLE);
            mVideoAcceptCallArea.setActivated(true);
        }

        mCallNameTv.setText(null == mDisplayName ? "" : mDisplayName);
        mCallNumberTv.setText(null == mCallNumber ? "" : mCallNumber);

        mRejectBtn.setOnClickListener(this);
        mAudioAcceptCallArea.setOnClickListener(this);
        mVideoAcceptCallArea.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        super.onClick(v);
        switch (v.getId())
        {
            case R.id.reject_btn:
                CallMgr.getInstance().endCall(mCallID);
                finish();
                break;
            case R.id.audio_accept_call_area:
                CallMgr.getInstance().answerCall(mCallID, false);
                break;

            case R.id.video_accept_call_area:
                CallMgr.getInstance().answerCall(mCallID, mIsVideoCall);
                break;

            default:
                break;
        }
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
