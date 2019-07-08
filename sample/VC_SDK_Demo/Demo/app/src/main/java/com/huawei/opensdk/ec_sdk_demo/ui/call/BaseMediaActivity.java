package com.huawei.opensdk.ec_sdk_demo.ui.call;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huawei.opensdk.callmgr.CallConstant;
import com.huawei.opensdk.callmgr.CallInfo;
import com.huawei.opensdk.callmgr.CallMgr;
import com.huawei.opensdk.commonservice.common.LocContext;
import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcastReceiver;
import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.logic.call.CallFunc;
import com.huawei.opensdk.ec_sdk_demo.ui.IntentConstant;
import com.huawei.opensdk.ec_sdk_demo.ui.base.BaseActivity;
import com.huawei.opensdk.ec_sdk_demo.util.ActivityUtil;
import com.huawei.opensdk.ec_sdk_demo.util.DialogUtil;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;

/**
 * This class is about base media activity.
 */
public class BaseMediaActivity extends BaseActivity implements View.OnClickListener, LocBroadcastReceiver
{
    private static final int CALL_CONNECTED = 100;
    private static final int CALL_UPGRADE = 101;
    private static final int HOLD_CALL_SUCCESS = 102;
    private static final int VIDEO_HOLD_CALL_SUCCESS = 103;
    private static final int MEDIA_CONNECTED = 104;

    protected ImageView mRejectBtn;
    protected FrameLayout mAudioAcceptCallArea;
    protected FrameLayout mVideoAcceptCallArea;
    private Timer mDismissDialogTimer;
    private static final int CANCEL_TIME = 25000;

    protected LinearLayout mPlateButton;
    protected LinearLayout mHoldCallButton;
    protected LinearLayout mMuteArea;
    protected LinearLayout mPlateArea;
    protected ImageView mCloseArea;

    protected TextView mCallNumberTv;
    protected TextView mCallNameTv;
    protected TextView mHoldCallText;


    protected String mCallNumber;
    protected String mDisplayName;
    protected boolean mIsVideoCall;
    protected int mCallID;
    protected String mConfID;
    protected boolean mIsConfCall;
    protected boolean mIsCaller;

    protected SecondDialPlateControl mPlateControl;

    private String[] mActions = new String[]{CustomBroadcastConstants.ACTION_CALL_CONNECTED,
            CustomBroadcastConstants.CALL_MEDIA_CONNECTED,
            CustomBroadcastConstants.CONF_CALL_CONNECTED,
            CustomBroadcastConstants.ACTION_CALL_END, CustomBroadcastConstants.CALL_UPGRADE_ACTION,
            CustomBroadcastConstants.HOLD_CALL_RESULT};
    private LinearLayout mSpeakerButton;
    private LinearLayout mUpgradeVideoArea;

    private CallFunc mCallFunc;

    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MEDIA_CONNECTED:
                    mPlateButton.setVisibility(View.VISIBLE);
                    break;

                case CALL_CONNECTED:
                    showButtons();
                    mAudioAcceptCallArea.setVisibility(View.GONE);
                    mVideoAcceptCallArea.setVisibility(View.GONE);

                    if (msg.obj instanceof CallInfo)
                    {
                        CallInfo callInfo = (CallInfo)msg.obj;
                        if (callInfo.isVideoCall())
                        {
                            Intent intent = new Intent(IntentConstant.VIDEO_ACTIVITY_ACTION);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addCategory(IntentConstant.DEFAULT_CATEGORY);

                            intent.putExtra(UIConstants.CALL_INFO, callInfo);
                            ActivityUtil.startActivity(BaseMediaActivity.this, intent);
                            finish();
                        }
                    }

                    break;
                case CALL_UPGRADE:
                    mDialog = DialogUtil.generateDialog(BaseMediaActivity.this, R.string.ntf_upgrade_videocall,
                            R.string.accept, R.string.refuse,
                            new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    cancelDisDiaTimer();
                                    Executors.newSingleThreadExecutor().execute(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            if (null == Looper.myLooper())
                                            {
                                                Looper.prepare();
                                            }
                                            CallMgr.getInstance().acceptAddVideo(mCallID);
                                        }
                                    });
                                }
                            }, new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    cancelDisDiaTimer();
                                    CallMgr.getInstance().rejectAddVideo(mCallID);
                                }
                            });
                    mDialog.show();
                    startDismissDiaLogTimer();
                    break;
                case HOLD_CALL_SUCCESS:
                {
                    String textDisplayName = null == mDisplayName ? "" : mDisplayName;
                    String textCallNumber = null == mCallNumber ? "" : mCallNumber;
                    if ("Hold".equals(mCallNumberTv.getTag()))
                    {
                        textCallNumber = textCallNumber+"Holding";
                    }
                    mCallNameTv.setText(textDisplayName);
                    mCallNumberTv.setText(textCallNumber);
                }
                break;
                case VIDEO_HOLD_CALL_SUCCESS:
                {
                    String textDisplayName = null == mDisplayName ? "" : mDisplayName;
                    String textCallNumber = null == mCallNumber ? "" : mCallNumber;
                    textCallNumber = textCallNumber+"Holding";
                    mCallNameTv.setText(textDisplayName);
                    mCallNumberTv.setText(textCallNumber);
                    mHoldCallText.setText(R.string.un_hold_call);
                }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void initializeComposition()
    {
        setContentView(R.layout.call_media);
        mPlateButton = (LinearLayout) findViewById(R.id.plate_btn);
        mHoldCallButton = (LinearLayout) findViewById(R.id.hold_call);
        mSpeakerButton = (LinearLayout) findViewById(R.id.speaker_btn);
        mMuteArea = (LinearLayout) findViewById(R.id.mute_btn);
        mPlateArea = (LinearLayout) findViewById(R.id.dial_plate_area);
        mUpgradeVideoArea = (LinearLayout) findViewById(R.id.upgrade_video_btn);
        mRejectBtn = (ImageView) findViewById(R.id.reject_btn);
        mAudioAcceptCallArea = (FrameLayout) findViewById(R.id.audio_accept_call_area);
        mVideoAcceptCallArea = (FrameLayout) findViewById(R.id.video_accept_call_area);
        mCallNumberTv = (TextView) findViewById(R.id.call_number);

        mCloseArea = (ImageView) findViewById(R.id.hide_dial_btn);
        mCallNameTv = (TextView) findViewById(R.id.call_name);
        mHoldCallText = (TextView) findViewById(R.id.hold_call_text);

        mPlateControl = new SecondDialPlateControl(mPlateArea, mCallID);
        mPlateControl.hideDialPlate();

        mMuteArea.setOnClickListener(this);
        mCloseArea.setOnClickListener(this);
        mPlateButton.setOnClickListener(this);
        mSpeakerButton.setOnClickListener(this);
        mUpgradeVideoArea.setOnClickListener(this);
        mHoldCallButton.setOnClickListener(this);
        mHoldCallText.setOnClickListener(this);

        hideViews();
    }

    private void startDismissDiaLogTimer()
    {
        cancelDisDiaTimer();

        mDismissDialogTimer = new Timer("Dismiss Dialog");
        DismissDialogTimerTask dismissDialogTimerTask = new DismissDialogTimerTask(mDialog, mCallID);
        mDismissDialogTimer.schedule(dismissDialogTimerTask, CANCEL_TIME);
    }

    private void cancelDisDiaTimer()
    {
        if (mDismissDialogTimer != null)
        {
            mDismissDialogTimer.cancel();
            mDismissDialogTimer = null;
        }
    }

    private AlertDialog mDialog;

    private static class DismissDialogTimerTask extends TimerTask
    {
        private final AlertDialog dialog;
        private int callID;

        public DismissDialogTimerTask(AlertDialog dialog, int callID)
        {
            this.dialog = dialog;
            this.callID = callID;
        }

        @Override
        public void run()
        {
            if (null != dialog)
            {
                dialog.dismiss();
            }

            CallMgr.getInstance().rejectAddVideo(this.callID);
            //CallMgr.getInstance().rejectUpgradeVideo();
            LogUtil.i(UIConstants.DEMO_TAG, "dialog time out disAgreeUpg");
        }
    }

    private void hideViews()
    {
        ImageView deleteNumber = (ImageView) findViewById(R.id.delete_panel_btn);
        ImageView audioBtn = (ImageView) findViewById(R.id.call_audio_btn);
        ImageView videoBtn = (ImageView) findViewById(R.id.call_video_btn);

        audioBtn.setVisibility(View.GONE);
        videoBtn.setVisibility(View.GONE);
        deleteNumber.setVisibility(View.GONE);
        CallConstant.CallStatus callStatus = CallMgr.getInstance().getCallStatus(mCallID);
        boolean isCall = (CallConstant.CallStatus.AUDIO_CALLING == callStatus || CallConstant.CallStatus.VIDEO_CALLING == callStatus);

        mMuteArea.setVisibility(isCall ? View.VISIBLE : View.GONE);
        mPlateButton.setVisibility(isCall ? View.VISIBLE : View.GONE);
        //mSpeakerButton.setVisibility(isCall ? View.VISIBLE : View.GONE);
        mSpeakerButton.setVisibility(View.VISIBLE);
        mUpgradeVideoArea.setVisibility(isCall ? View.VISIBLE : View.GONE);
        mHoldCallButton.setVisibility(isCall ? View.VISIBLE : View.GONE);
    }

    @Override
    public void initializeData()
    {
        mCallFunc = CallFunc.getInstance();

        Intent intent = getIntent();
        CallInfo callInfo = (CallInfo) intent.getSerializableExtra(UIConstants.CALL_INFO);

        mCallNumber = callInfo.getPeerNumber();
        mDisplayName = callInfo.getPeerDisplayName();
        mIsVideoCall = callInfo.isVideoCall();
        mCallID = callInfo.getCallID();
        mConfID = callInfo.getConfID();
        mIsConfCall = callInfo.isFocus();
        mIsCaller = callInfo.isCaller();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.plate_btn:
                mPlateControl.showDialPlate();
                break;
            case R.id.hide_dial_btn:
                mPlateControl.hideDialPlate();
                break;
            case R.id.mute_btn:
                boolean mMuteStatus = mCallFunc.isMuteStatus();
                if (CallMgr.getInstance().muteMic(mCallID, !mMuteStatus))
                {
                    mCallFunc.setMuteStatus(!mMuteStatus);
                    mMuteArea.setActivated(!mMuteStatus);
                }
                break;
            case R.id.speaker_btn:
                mSpeakerButton.setActivated(CallMgr.getInstance().switchAudioRoute() == CallConstant.TYPE_LOUD_SPEAKER);
                break;
            case R.id.upgrade_video_btn:
                //CallMgr.getInstance().audioToVideo();
                CallMgr.getInstance().addVideo(mCallID);
                break;
            case R.id.hold_call:
                if ("Hold Call".equals(mHoldCallText.getText()))
                {
                    mHoldCallText.setText(R.string.un_hold_call);
                    CallMgr.getInstance().holdCall(mCallID);
                }else if ("Un Hold Call".equals(mHoldCallText.getText()))
                {
                    mHoldCallText.setText(R.string.hold_call);
                    CallMgr.getInstance().unHoldCall(mCallID);
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        LocBroadcast.getInstance().unRegisterBroadcast(this, mActions);
        dismissDialog(mDialog);
    }

    protected void dismissDialog(AlertDialog dialog)
    {
        if (null != dialog)
        {
            dialog.dismiss();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        LocBroadcast.getInstance().registerBroadcast(this, mActions);
    }

    @Override
    public void onReceive(final String broadcastName, final Object obj)
    {
        switch (broadcastName)
        {
            case CustomBroadcastConstants.ACTION_CALL_CONNECTED:
                mHandler.sendMessage(mHandler.obtainMessage(CALL_CONNECTED, obj));
                break;
            case CustomBroadcastConstants.CALL_MEDIA_CONNECTED:
                mHandler.sendMessage(mHandler.obtainMessage(MEDIA_CONNECTED, obj));
                break;

            case CustomBroadcastConstants.CONF_CALL_CONNECTED:
                CallInfo callInfo = (CallInfo)obj;
                String confID = callInfo.getCallID()+"";
                Intent intent = new Intent(IntentConstant.CONF_MANAGER_ACTIVITY_ACTION);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(UIConstants.CONF_ID, confID);
                intent.putExtra(UIConstants.IS_VIDEO_CONF, callInfo.isVideoCall());
                ActivityUtil.startActivity(LocContext.getContext(), intent);
                finish();
                break;
            case CustomBroadcastConstants.ACTION_CALL_END:
                finish();
                break;
            case CustomBroadcastConstants.CALL_UPGRADE_ACTION:
                mHandler.sendEmptyMessage(CALL_UPGRADE);
                break;
            case CustomBroadcastConstants.HOLD_CALL_RESULT:
                if ("HoldSuccess".equals(obj))
                {
                    mCallNumberTv.setTag("Hold");
                    mHandler.sendEmptyMessage(HOLD_CALL_SUCCESS);
                }else if ("UnHoldSuccess".equals(obj))
                {
                    mCallNumberTv.setTag("UnHold");
                    mHandler.sendEmptyMessage(HOLD_CALL_SUCCESS);
                }else if ("VideoHoldSuccess".equals(obj))
                {
                    mHandler.sendEmptyMessage(VIDEO_HOLD_CALL_SUCCESS);
                }
                break;
            default:
                break;
        }
    }

    private void showButtons()
    {
        mMuteArea.setVisibility(View.VISIBLE);
        mPlateButton.setVisibility(View.VISIBLE);
        mSpeakerButton.setVisibility(View.VISIBLE);
        mUpgradeVideoArea.setVisibility(View.VISIBLE);
        mHoldCallButton.setVisibility(View.VISIBLE);
        mSpeakerButton.setActivated(CallMgr.getInstance().getCurrentAudioRoute() == CallConstant.TYPE_LOUD_SPEAKER);
    }
}
