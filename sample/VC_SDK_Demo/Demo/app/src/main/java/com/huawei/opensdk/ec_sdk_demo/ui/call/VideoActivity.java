package com.huawei.opensdk.ec_sdk_demo.ui.call;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.huawei.opensdk.callmgr.CallConstant;
import com.huawei.opensdk.callmgr.CallInfo;
import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcastReceiver;
import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.logic.call.IVideoCallContract;
import com.huawei.opensdk.ec_sdk_demo.logic.call.VideoCallPresenter;
import com.huawei.opensdk.ec_sdk_demo.ui.base.MVPBaseActivity;
import com.huawei.opensdk.ec_sdk_demo.util.DialogUtil;
import com.huawei.opensdk.ec_sdk_demo.util.PopupWindowUtil;

/**
 * This class is about video call activity.
 */
public class VideoActivity extends MVPBaseActivity<IVideoCallContract.VideoCallBaseView, VideoCallPresenter>
        implements View.OnClickListener, IVideoCallContract.VideoCallBaseView, LocBroadcastReceiver
{
    private static final int ADD_LOCAL_VIEW = 101;

    private FrameLayout mHideView;
    private FrameLayout mRemoteView;
    private FrameLayout mLocalView;
    private FrameLayout mVideoHangupArea;
    private TextView mShowTimeView;
    private TextView mCallNameTv;
    private FrameLayout mHideLocalBtn;
    private FrameLayout mShowLocalBtn;
    private ImageView mCameraStatusImage;
    private TextView mCameraStatusText;
    private ImageView mMediaMoreBtn;
    private LinearLayout mMediaGroupBtn;
    private LinearLayout mSwitchCameraBtn;
    private ImageView mSwitchCameraImage;
    private TextView mSwitchCameraText;
    private FrameLayout mVideoMuteArea;
    private FrameLayout mVideoSpeakerArea;
    private FrameLayout mPlateBtn;
    private PopupWindow mPopupWindow;
    private LinearLayout mPlateArea;
    private ImageView mCloseArea;

    private boolean mIsCameraClose = false;
    private SecondDialPlateControl mPlateControl;
    private AlertDialog mDialog;

    private String[] mActions = new String[]{CustomBroadcastConstants.ACTION_CALL_END,
            CustomBroadcastConstants.ADD_LOCAL_VIEW,
            CustomBroadcastConstants.DEL_LOCAL_VIEW};

    private static final int NOT_ALPHA = 255;
    private static final int HALF_ALPHA = 127;

    private CallInfo mCallInfo;
    private int mCallID;
    private Object thisVideoActivity = this;
    private int changeNumber = 0;

    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case ADD_LOCAL_VIEW:
                    addSurfaceView(true);
                    mPresenter.setAutoRotation(thisVideoActivity, true);
                    break;

                default:
                    break;
            }
        }
    };

    private void showDialog()
    {
        mDialog = DialogUtil.generateDialog(VideoActivity.this, R.string.ntf_end_call,
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        mPresenter.endCall();
                    }
                });
        mDialog.show();
    }

    @Override
    public void initializeComposition()
    {
        // 保持屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_call_video);
        mRemoteView = (FrameLayout) findViewById(R.id.remote_video);
        mLocalView = (FrameLayout) findViewById(R.id.local_video);
        mHideView = (FrameLayout) findViewById(R.id.hide_video_view);
        mVideoHangupArea = (FrameLayout) findViewById(R.id.video_hangup_area);
        mVideoMuteArea = (FrameLayout) findViewById(R.id.video_mute_area);
        mPlateBtn = (FrameLayout) findViewById(R.id.video_plate_area);
        mVideoSpeakerArea = (FrameLayout) findViewById(R.id.video_speaker_area);
        mShowTimeView = (TextView) findViewById(R.id.call_time);
        mCallNameTv = (TextView) findViewById(R.id.call_name);
        mHideLocalBtn = (FrameLayout) findViewById(R.id.local_video_hide_btn);
        mShowLocalBtn = (FrameLayout) findViewById(R.id.local_video_show_btn);
        mMediaMoreBtn = (ImageView) findViewById(R.id.media_btn_more);
        mMediaGroupBtn = (LinearLayout) findViewById(R.id.media_btn_group);
        mPlateArea = (LinearLayout) findViewById(R.id.dial_plate_area);
        mCloseArea = (ImageView) findViewById(R.id.hide_dial_btn);
        ImageView audioBtn = (ImageView) findViewById(R.id.call_audio_btn);
        ImageView videoBtn = (ImageView) findViewById(R.id.call_video_btn);
        audioBtn.setVisibility(View.GONE);
        videoBtn.setVisibility(View.GONE);
        mPlateArea.setVisibility(View.GONE);

        mCallNameTv.setText(getString(R.string.call_number) + mPresenter.getOppositeNumber());

        mVideoHangupArea.setOnClickListener(this);
        mHideLocalBtn.setOnClickListener(this);
        mShowLocalBtn.setOnClickListener(this);
        mMediaMoreBtn.setOnClickListener(this);
        mVideoMuteArea.setOnClickListener(this);
        mVideoSpeakerArea.setOnClickListener(this);
        mPlateBtn.setOnClickListener(this);
        mCloseArea.setOnClickListener(this);
        mLocalView.setOnClickListener(this);

        mLocalView.setVisibility(View.VISIBLE);
        mPlateControl = new SecondDialPlateControl(mPlateArea, this.mCallID);
        mVideoSpeakerArea.setActivated(mPresenter.getIsSpeakerRoute());
    }

    @Override
    public void onReceive(String broadcastName, Object obj)
    {
        switch (broadcastName)
        {
            case CustomBroadcastConstants.ACTION_CALL_END:
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        callClosed();
                        finish();
                    }
                });
                break;

            case CustomBroadcastConstants.ADD_LOCAL_VIEW:
                mHandler.sendEmptyMessage(ADD_LOCAL_VIEW);
                break;

            case CustomBroadcastConstants.DEL_LOCAL_VIEW:
                break;

            default:
                break;
        }
    }

    /**
     * On call closed.
     */
    private void callClosed()
    {
        LogUtil.i(UIConstants.DEMO_TAG, "onCallClosed enter.");
        mPresenter.executorShutDown();
        mPresenter.videoDestroy();
    }

    @Override
    public void initializeData()
    {
        Intent intent = getIntent();
        mCallInfo = (CallInfo) intent.getSerializableExtra(UIConstants.CALL_INFO);
        mPresenter.setCurrentCallInfo(mCallInfo);
        this.mCallID = mCallInfo.getCallID();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        LocBroadcast.getInstance().registerBroadcast(this, mActions);
        addSurfaceView(false);
        mPresenter.setAutoRotation(this, true);
        mPresenter.startTimer();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        LocBroadcast.getInstance().unRegisterBroadcast(this, mActions);
        mPresenter.removeCallback();
        mPresenter.setAutoRotation(this, false);
        dismissDialog(mDialog);
        mPresenter.executorShutDown();
        PopupWindowUtil.getInstance().dismissPopupWindow(mPopupWindow);
    }

    private void dismissDialog(AlertDialog dialog)
    {
        if (null != dialog)
        {
            dialog.dismiss();
        }
    }

    @Override
    protected IVideoCallContract.VideoCallBaseView createView()
    {
        return this;
    }

    @Override
    protected VideoCallPresenter createPresenter()
    {
        return new VideoCallPresenter(this);
    }

    @Override
    public void onBackPressed()
    {
        showDialog();
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.video_hangup_area:
                mPresenter.endCall();
                break;
            case R.id.local_video_hide_btn:
                //只隐藏本地窗口，并不关闭本地视频
                mShowLocalBtn.setVisibility(View.VISIBLE);
                mHideLocalBtn.setVisibility(View.INVISIBLE);

                mLocalView.setVisibility(View.INVISIBLE);
                mHideView.setVisibility(View.VISIBLE);
                mPresenter.getLocalVideoView().setVisibility(View.INVISIBLE);

                break;
            case R.id.local_video_show_btn:
                //重新显示本地窗口，无需再打开本地视频
                mShowLocalBtn.setVisibility(View.INVISIBLE);
                mHideLocalBtn.setVisibility(View.VISIBLE);

                mLocalView.setVisibility(View.VISIBLE);
                mHideView.setVisibility(View.INVISIBLE);
                mPresenter.getLocalVideoView().setVisibility(View.VISIBLE);
                break;

            case R.id.media_btn_more:
                View popupView = getLayoutInflater().inflate(R.layout.popup_video_call, null);

                mSwitchCameraBtn = (LinearLayout) popupView.findViewById(R.id.switch_camera_btn);
                LinearLayout switchCameraStatusBtn = (LinearLayout) popupView.findViewById(R.id.switch_camera_status_btn);
                LinearLayout videoSwitchAudioBtn = (LinearLayout) popupView.findViewById(R.id.video_switch_audio_btn);
                LinearLayout videoHoldBtn = (LinearLayout) popupView.findViewById(R.id.video_hold);

                mCameraStatusImage = (ImageView) popupView.findViewById(R.id.iv_camera_status);
                mCameraStatusText = (TextView) popupView.findViewById(R.id.tv_camera_status);
                mSwitchCameraImage = (ImageView) popupView.findViewById(R.id.iv_camera_switch);
                mSwitchCameraText = (TextView) popupView.findViewById(R.id.tv_camera_switch);

                mCameraStatusText.setText(mIsCameraClose ? getString(R.string.open_local_camera) :
                        getString(R.string.close_local_camera));

                switchCameraStatusBtn.setOnClickListener(this);
                mSwitchCameraBtn.setOnClickListener(this);
                videoSwitchAudioBtn.setOnClickListener(this);
                videoHoldBtn.setOnClickListener(this);
                mPopupWindow = PopupWindowUtil.getInstance().generatePopupWindow(popupView);
                mPopupWindow.showAtLocation(findViewById(R.id.video_call_area), Gravity.RIGHT | Gravity.BOTTOM, 0, mMediaGroupBtn.getHeight());
                break;
            case R.id.switch_camera_btn:
                if (mIsCameraClose)
                {
                    return;
                }
                mPresenter.switchCamera();
                break;
            case R.id.switch_camera_status_btn:
                mIsCameraClose = !mIsCameraClose;
                mCameraStatusImage.setActivated(mIsCameraClose);
                mCameraStatusText.setText(mIsCameraClose ? getString(R.string.open_local_camera) :
                    getString(R.string.close_local_camera));

                mPresenter.switchCameraStatus(mIsCameraClose);

                mSwitchCameraImage.getDrawable().setAlpha(!mIsCameraClose ? NOT_ALPHA : HALF_ALPHA);
                mSwitchCameraText.setActivated(mIsCameraClose);
                break;
            case R.id.video_switch_audio_btn:
                mPresenter.videoToAudio();
                break;
            case R.id.video_hold:
                mPresenter.holdVideo();
                break;
            case R.id.video_mute_area:
                mPresenter.muteCall();
                break;
            case R.id.video_speaker_area:
                mVideoSpeakerArea.setActivated(mPresenter.switchAudioRoute() == CallConstant.TYPE_LOUD_SPEAKER);
                break;
            case R.id.video_plate_area:
                mPlateControl.showDialPlate();
                break;
            case R.id.hide_dial_btn:
                mPlateControl.hideDialPlate();
                break;
            case R.id.local_video:
                changeShowView();
                break;
            default:
                break;
        }
    }

    private void addSurfaceView(ViewGroup container, SurfaceView child)
    {
        if (child == null)
        {
            return;
        }
        if (child.getParent() != null)
        {
            ViewGroup vGroup = (ViewGroup) child.getParent();
            vGroup.removeAllViews();
        }
        container.addView(child);
    }

    private void addSurfaceView(boolean onlyLocal)
    {
        if (!onlyLocal) {
            addSurfaceView(mRemoteView, mPresenter.getRemoteVideoView());
        }
        addSurfaceView(mLocalView, mPresenter.getLocalVideoView());
        addSurfaceView(mHideView, mPresenter.getHideVideoView());
    }

    @Override
    public void switchMuteBtn(boolean currentMuteStatus)
    {
        mVideoMuteArea.setActivated(!currentMuteStatus);
    }

    @Override
    public void setTime(String time)
    {
        mShowTimeView.setText(time);
    }

    /**
     * 视频画面切换功能
     */
    private void changeShowView(){
        mRemoteView.removeAllViews();
        mLocalView.removeAllViews();
        changeNumber++;
        if(changeNumber%2 == 0){
            mPresenter.getRemoteVideoView().setZOrderMediaOverlay(false);
            mPresenter.getLocalVideoView().setZOrderMediaOverlay(true);
            addSurfaceView(mRemoteView, mPresenter.getRemoteVideoView());
            addSurfaceView(mLocalView, mPresenter.getLocalVideoView());
        }else {
            mPresenter.getRemoteVideoView().setZOrderMediaOverlay(true);
            mPresenter.getLocalVideoView().setZOrderMediaOverlay(false);
            addSurfaceView(mLocalView, mPresenter.getRemoteVideoView());
            addSurfaceView(mRemoteView, mPresenter.getLocalVideoView());
        }
    }
}
