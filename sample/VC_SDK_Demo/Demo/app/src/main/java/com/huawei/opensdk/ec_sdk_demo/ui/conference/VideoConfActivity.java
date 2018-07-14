package com.huawei.opensdk.ec_sdk_demo.ui.conference;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.huawei.opensdk.callmgr.CallConstant;
import com.huawei.opensdk.callmgr.CallMgr;
import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.demoservice.MeetingMgr;
import com.huawei.opensdk.demoservice.Member;
import com.huawei.opensdk.demoservice.data.CameraEntity;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.adapter.PopupConfListAdapter;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.logic.conference.mvp.IVideoConfContract;
import com.huawei.opensdk.ec_sdk_demo.logic.conference.mvp.VideoConfBasePresenter;
import com.huawei.opensdk.ec_sdk_demo.logic.conference.mvp.VideoConfPresenter;
import com.huawei.opensdk.ec_sdk_demo.ui.base.ActivityStack;
import com.huawei.opensdk.ec_sdk_demo.ui.base.MVPBaseActivity;
import com.huawei.opensdk.ec_sdk_demo.util.PopupWindowUtil;
import com.huawei.opensdk.ec_sdk_demo.widget.ConfirmDialog;
import com.huawei.opensdk.ec_sdk_demo.widget.TripleDialog;

import java.util.List;

public class VideoConfActivity extends MVPBaseActivity<IVideoConfContract.VideoConfView, VideoConfBasePresenter>
        implements IVideoConfContract.VideoConfView, View.OnClickListener
{
    private FrameLayout mConfShareLayout;
    private FrameLayout mConfSmallLayout;
    private FrameLayout mHideVideoLayout;

    private FrameLayout mHideLocalVideoBtn;
    private FrameLayout mShowLocalVideoBtn;

    private ImageView mBackIV;
    private TextView mTitleTV;
    private ImageView mRightIV;
    private FrameLayout mConfHangup;
    private FrameLayout mConfMute;
    private FrameLayout mConfSpeaker;
    private FrameLayout mConfSwitchCamera;
    private ImageView closeLocalIV;
    private String mSubject;
    private TextView mConfTimeIV;
    private PopupWindow mPopupWindow;
    private ListView mConfMemberListView;
    private PopupConfListAdapter mAdapter;

    private String confID;

    @Override
    protected IVideoConfContract.VideoConfView createView()
    {
        return this;
    }

    @Override
    protected VideoConfBasePresenter createPresenter()
    {
        return new VideoConfPresenter();
    }

    @Override
    public void initializeComposition()
    {
        setContentView(R.layout.video_conf_activity);
        //video layout
        mConfShareLayout = (FrameLayout) findViewById(R.id.conf_share_layout);
        mHideLocalVideoBtn = (FrameLayout) findViewById(R.id.local_video_hide);
        mHideVideoLayout = (FrameLayout) findViewById(R.id.hide_video_view);
        mShowLocalVideoBtn = (FrameLayout) findViewById(R.id.local_video_hide_cancel);
        mConfSmallLayout = (FrameLayout) findViewById(R.id.conf_video_small_logo);

        //title
        mBackIV = (ImageView) findViewById(R.id.back_iv);
        mConfTimeIV = (TextView) findViewById(R.id.conference_time);
        mRightIV = (ImageView) findViewById(R.id.right_iv);
        mTitleTV = (TextView) findViewById(R.id.conf_title);

        mTitleTV.setText(mSubject);
        // 不支持选看时设置
        mRightIV.setVisibility(View.GONE);

        //main tab
        mConfHangup = (FrameLayout) findViewById(R.id.conf_hangup);
        mConfMute = (FrameLayout) findViewById(R.id.conf_mute);
        mConfSpeaker = (FrameLayout) findViewById(R.id.conf_loud_speaker);
        mConfSwitchCamera = (FrameLayout) findViewById(R.id.switch_camera);
        closeLocalIV = (ImageView) findViewById(R.id.conf_btn_close_local);

        mConfHangup.setOnClickListener(this);
        mConfMute.setOnClickListener(this);
        mConfSpeaker.setOnClickListener(this);
        mConfSwitchCamera.setOnClickListener(this);
        closeLocalIV.setOnClickListener(this);
        mHideLocalVideoBtn.setOnClickListener(this);
        mShowLocalVideoBtn.setOnClickListener(this);
        mRightIV.setOnClickListener(this);

        if(null != MeetingMgr.getInstance().getCurrentConferenceSelf()){
            updateMuteButton(MeetingMgr.getInstance().getCurrentConferenceSelf().isMute());
        }
        updateLoudSpeakerButton(CallMgr.getInstance().getCurrentAudioRoute());
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mPresenter.registerBroadcast();
        mPresenter.setVideoContainer(this, mConfSmallLayout, mConfShareLayout, mHideVideoLayout);
        mPresenter.setAutoRotation(this, true);

        // 以下处理用于PBX下的视频会议
        Member self = MeetingMgr.getInstance().getCurrentConferenceSelf();
        if (self == null || self.getCameraEntityList().isEmpty())
        {
            LogUtil.i(UIConstants.DEMO_TAG,  "no camera--------- ");
        }
        else
        {
            //打开前置摄像头
            mPresenter.shareSelfVideo(self.getCameraEntityList().get(1).getDeviceID());
        }
    }

    @Override
    public void initializeData()
    {
        Intent intent = getIntent();
        confID = intent.getStringExtra(UIConstants.CONF_ID);
        if (confID == null)
        {
            showToast(R.string.empty_conf_id);
            return;
        }

        mPresenter.setConfID(confID);
        mAdapter = new PopupConfListAdapter(this);
        mSubject = mPresenter.getSubject();
    }

    @Override
    public void finishActivity()
    {
        finish();
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void dismissLoading() {

    }

    @Override
    public void showCustomToast(final int resID)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                showToast(resID);
            }
        });
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.conf_hangup:
                LogUtil.i(UIConstants.DEMO_TAG, "conference hangup!");
                if (!mPresenter.isChairMan())
                {
                    showLeaveConfDialog();
                }
                else
                {
                    showEndConfDialog();
                }
                break;
            case R.id.conf_mute:
                LogUtil.i(UIConstants.DEMO_TAG, "conference mute!");
                mPresenter.muteSelf();
                break;
            case R.id.conf_loud_speaker:
                LogUtil.i(UIConstants.DEMO_TAG, "conference speaker!");
                updateLoudSpeakerButton(mPresenter.switchLoudSpeaker());
                break;
            case R.id.switch_camera:
                LogUtil.i(UIConstants.DEMO_TAG, "conference switch camera!");
                mPresenter.switchCamera();
                break;
            case R.id.conf_btn_close_local:
                boolean result = mPresenter.closeOrOpenLocalVideo(!closeLocalIV.isActivated());
                if (result)
                {
                    closeLocalIV.setActivated(!closeLocalIV.isActivated());
                }
                else
                {
                    showToast(closeLocalIV.isActivated() ? R.string.close_video_failed : R.string.open_video_failed);
                }
                break;
            case R.id.local_video_hide:

                mPresenter.changeLocalVideoVisible(false);

                mHideLocalVideoBtn.setVisibility(View.GONE);
                mShowLocalVideoBtn.setVisibility(View.VISIBLE);

                mConfSmallLayout.setVisibility(View.GONE);
                mHideVideoLayout.setVisibility(View.VISIBLE);

                break;
            case R.id.local_video_hide_cancel:
                mPresenter.changeLocalVideoVisible(true);

                mHideLocalVideoBtn.setVisibility(View.VISIBLE);
                mShowLocalVideoBtn.setVisibility(View.GONE);

                mHideVideoLayout.setVisibility(View.GONE);
                mConfSmallLayout.setVisibility(View.VISIBLE);

                break;
            case R.id.right_iv:
                final List<Member> memberList = mPresenter.getMemberList();
                if (null == memberList || memberList.size()<=0){
                    return;
                }
                final View popupView = getLayoutInflater().inflate(R.layout.popup_conf_list, null);
                mConfMemberListView = (ListView) popupView.findViewById(R.id.popup_conf_member_list);
                mAdapter.setData(memberList);
                mConfMemberListView.setAdapter(mAdapter);
                mPopupWindow = PopupWindowUtil.getInstance().generatePopupWindow(popupView);
                mPopupWindow.showAsDropDown(findViewById(R.id.right_iv));
                mConfMemberListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    {
                        Member conferenceMemberEntity = memberList.get(position);
                        CameraEntity cameraEntity = conferenceMemberEntity.getOpenedCamera();
                        if (cameraEntity  != null)
                        {
                            mPresenter.attachRemoteVideo(cameraEntity.getUserID(), cameraEntity.getDeviceID());
                        }
                        else
                        {
                            LogUtil.i(UIConstants.DEMO_TAG,  "can't open remote camera :cameraEntity is null ");
                        }
                        mPopupWindow.dismiss();
                    }
                });
                break;
            default:
                break;
        }
    }

    @Override
    public void refreshMemberList(final List<Member> list)
    {
        if (null == list || list.size() <= 0){
            return;
        }
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                mAdapter.setData(list);
                mAdapter.notifyDataSetChanged();
            }
        });

    }

    private void showLeaveConfDialog()
    {
        ConfirmDialog dialog = new ConfirmDialog(this, R.string.leave_conf);
        dialog.setRightButtonListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mPresenter.closeConf();
                ActivityStack.getIns().popup(ConfManagerActivity.class);
                finish();
            }
        });
        dialog.show();
    }

    private void showEndConfDialog()
    {
        TripleDialog dialog = new TripleDialog(this);
        dialog.setRightButtonListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mPresenter.closeConf();
                ActivityStack.getIns().popup(ConfManagerActivity.class);
                finish();
            }
        });
        dialog.setLeftButtonListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mPresenter.finishConf();
                ActivityStack.getIns().popup(ConfManagerActivity.class);
                finish();
            }
        });
        dialog.show();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mPresenter.leaveVideo();
        mPresenter.unregisterBroadcast();
        mPresenter.setAutoRotation(this, false);
        PopupWindowUtil.getInstance().dismissPopupWindow(mPopupWindow);
    }

    @Override
    public void updateMuteButton(final boolean isMute)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                mConfMute.setActivated(isMute);
            }
        });
    }

    @Override
    public void updateLocalVideo()
    {
        mPresenter.setVideoContainer(this, mConfSmallLayout, mConfShareLayout, mHideVideoLayout);
    }

    private void updateLoudSpeakerButton(int type)
    {
        if (type == CallConstant.TYPE_LOUD_SPEAKER)
        {
            mConfSpeaker.setActivated(true);
        }
        else
        {
            mConfSpeaker.setActivated(false);
        }
    }
}
