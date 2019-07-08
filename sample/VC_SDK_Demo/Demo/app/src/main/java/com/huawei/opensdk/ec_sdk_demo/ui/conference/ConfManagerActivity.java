package com.huawei.opensdk.ec_sdk_demo.ui.conference;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huawei.opensdk.callmgr.CallConstant;
import com.huawei.opensdk.callmgr.CallMgr;
import com.huawei.opensdk.commonservice.common.LocContext;
import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.demoservice.ConfBaseInfo;
import com.huawei.opensdk.demoservice.Member;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.adapter.PopupConfListAdapter;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.logic.conference.mvp.ConfManagerPresenter;
import com.huawei.opensdk.ec_sdk_demo.logic.conference.mvp.IConfManagerContract;
import com.huawei.opensdk.ec_sdk_demo.ui.IntentConstant;
import com.huawei.opensdk.ec_sdk_demo.ui.base.ActivityStack;
import com.huawei.opensdk.ec_sdk_demo.ui.base.MVPBaseActivity;
import com.huawei.opensdk.ec_sdk_demo.util.ActivityUtil;
import com.huawei.opensdk.ec_sdk_demo.util.CommonUtil;
import com.huawei.opensdk.ec_sdk_demo.util.PopupWindowUtil;
import com.huawei.opensdk.ec_sdk_demo.widget.ConfirmDialog;
import com.huawei.opensdk.ec_sdk_demo.widget.EditDialog;
import com.huawei.opensdk.ec_sdk_demo.widget.SimpleListDialog;
import com.huawei.opensdk.ec_sdk_demo.widget.ThreeInputDialog;
import com.huawei.opensdk.ec_sdk_demo.widget.TripleDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class ConfManagerActivity extends MVPBaseActivity<IConfManagerContract.IConfManagerView, ConfManagerPresenter>
        implements IConfManagerContract.IConfManagerView, View.OnClickListener
{

    private RelativeLayout mVideoConfLayout;
    private RelativeLayout mTitleLayout;
    private LinearLayout mConfMediaLayout;
    private FrameLayout mConfRemoteVideoLayout;
    private FrameLayout mConfSmallLayout;
    private FrameLayout mHideVideoLayout;

    private FrameLayout mHideLocalVideoBtn;
    private FrameLayout mShowLocalVideoBtn;

    private LinearLayout mConfButton;
    private ImageView mLeaveIV;
    private TextView mTitleTV;
    private ImageView mRightIV;
    private ImageView mShareIV;
    private FrameLayout mConfHangup;
    private FrameLayout mConfMute;
    private FrameLayout mConfSpeaker;
    private FrameLayout mConfAddAttendee;
    private FrameLayout mConfAttendee;
    private PopupWindow mPopupWindow;
    private ListView mConfMemberListView;
    private PopupConfListAdapter mAdapter;
    private FrameLayout mConfMore;
    private ImageView cameraStatusIV;
    private TextView cameraStatusTV;
    private RelativeLayout mAudioConfLayout;
    private TextView mAudioConfAttendeeTV;

    private String confID;
    private boolean isCameraClose = false;
    private boolean isVideo = false;
    private boolean isDateConf = false;
    private List<Object> items = new ArrayList<>();
    private int mOrientation = 1;

    private MyTimerTask myTimerTask;
    private Timer timer;
    private boolean isFirstStart = true;
    private boolean isPressTouch = false;
    private boolean isShowBar = false;

    private boolean isStartShare = false;

    //无会控功能标识
    private boolean noConfctrl = true;

    @Override
    protected IConfManagerContract.IConfManagerView createView()
    {
        return this;
    }

    @Override
    protected ConfManagerPresenter createPresenter()
    {
        return new ConfManagerPresenter();
    }

    @Override
    public void initializeComposition()
    {
        setContentView(R.layout.conf_manager_activity);
        mConfButton = (LinearLayout) findViewById(R.id.media_btn_group);
        mVideoConfLayout = (RelativeLayout) findViewById(R.id.conference_video_layout);
        mTitleLayout = (RelativeLayout) findViewById(R.id.title_layout_transparent);
        mConfMediaLayout = (LinearLayout) findViewById(R.id.media_btn_group);

        //title
        mLeaveIV = (ImageView) findViewById(R.id.leave_iv);
        mTitleTV = (TextView) findViewById(R.id.conf_title);
        mRightIV = (ImageView) findViewById(R.id.right_iv);
        mShareIV = (ImageView) findViewById(R.id.share_iv);

        //main tab
        mConfHangup = (FrameLayout) findViewById(R.id.conf_hangup);
        mConfMute = (FrameLayout) findViewById(R.id.conf_mute);
        mConfSpeaker = (FrameLayout) findViewById(R.id.conf_loud_speaker);
        mConfAddAttendee = (FrameLayout) findViewById(R.id.conf_add_attendee);
        mConfAttendee = (FrameLayout) findViewById(R.id.conf_attendee);
        mConfMore = (FrameLayout) findViewById(R.id.btn_conf_more);

        // 在与会者列表上报之前会控按钮全部屏蔽
        mConfAddAttendee.setVisibility(View.GONE);
        mConfAttendee.setVisibility(View.GONE);

        if (isVideo)
        {
            //video layout
            mVideoConfLayout.setVisibility(View.VISIBLE);
            mConfRemoteVideoLayout = (FrameLayout) findViewById(R.id.conf_remote_video_layout);
            mHideLocalVideoBtn = (FrameLayout) findViewById(R.id.local_video_hide);
            mHideVideoLayout = (FrameLayout) findViewById(R.id.hide_video_view);
            mShowLocalVideoBtn = (FrameLayout) findViewById(R.id.local_video_hide_cancel);
            mConfSmallLayout = (FrameLayout) findViewById(R.id.conf_video_small_logo);

            //title
            mRightIV.setVisibility(View.VISIBLE);

            mHideLocalVideoBtn.setOnClickListener(this);
            mShowLocalVideoBtn.setOnClickListener(this);
            mRightIV.setOnClickListener(this);
        }
        else
        {
            mAudioConfAttendeeTV = (TextView) findViewById(R.id.tv_audio_conf_attendee);
            mAudioConfLayout = (RelativeLayout) findViewById(R.id.audio_conf_layout_logo);

            mAudioConfAttendeeTV.setSelected(true);

            mAudioConfLayout.setVisibility(View.VISIBLE);
            mVideoConfLayout.setVisibility(View.INVISIBLE);

            //title
            mRightIV.setVisibility(View.GONE);
        }

        mVideoConfLayout.setOnClickListener(this);
        mConfHangup.setOnClickListener(this);
        mConfMute.setOnClickListener(this);
        mConfSpeaker.setOnClickListener(this);
        mConfAddAttendee.setOnClickListener(this);
        mConfAttendee.setOnClickListener(this);
        mConfMore.setOnClickListener(this);
        mLeaveIV.setOnClickListener(this);
        mShareIV.setOnClickListener(this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mPresenter.registerBroadcast();

        // 刷新当前扬声器状态
        updateLoudSpeakerButton(CallMgr.getInstance().getCurrentAudioRoute());

        if (!isVideo)
        {
            return;
        }

        if (isFirstStart)
        {
            startTimer();
        }

        if (mOrientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            mConfSmallLayout.getLayoutParams().width = dp2ps(this, 160);
            mConfSmallLayout.getLayoutParams().height = dp2ps(this, 90);
        }
        else
        {
            mConfSmallLayout.getLayoutParams().width = dp2ps(this, 90);
            mConfSmallLayout.getLayoutParams().height = dp2ps(this, 160);
        }
        mPresenter.setVideoContainer(this, mConfSmallLayout, mConfRemoteVideoLayout, mHideVideoLayout);
        mPresenter.setAutoRotation(this, true, mOrientation);
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopTimer();
        isFirstStart = true;
        isPressTouch = false;
        isShowBar = false;
    }

    @Override
    public void initializeData()
    {
        Intent intent = getIntent();
        confID = intent.getStringExtra(UIConstants.CONF_ID);
        isVideo = intent.getBooleanExtra(UIConstants.IS_VIDEO_CONF, false);
        if (confID == null)
        {
            showToast(R.string.empty_conf_id);
            return;
        }

        mPresenter.setConfID(confID);
        mAdapter = new PopupConfListAdapter(this);

        if (!isVideo)
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
            return;
        }

        // 获取屏幕方向
        Configuration configuration = this.getResources().getConfiguration();
        mOrientation = configuration.orientation;
    }

    @Override
    public void finishActivity()
    {
        stopTimer();
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
            case R.id.conference_video_layout:
                if (!isVideo)
                {
                    return;
                }
                if (isFirstStart)
                {
                    return;
                }
                if (isPressTouch)
                {
                    return;
                }
                else
                {
                    isPressTouch = true;
                    startTimer();
                }
                break;
            case R.id.conf_hangup:
                LogUtil.i(UIConstants.DEMO_TAG, "conference hangup!");
                if(noConfctrl){
                    mPresenter.endCall(Integer.valueOf(confID));
                }else {
                    if (!mPresenter.isChairMan())
                    {
                        showLeaveConfDialog();
                    }
                    else
                    {
                        showEndConfDialog();
                    }
                }
                break;
            case R.id.conf_mute:
                LogUtil.i(UIConstants.DEMO_TAG, "conference mute!");
                if (noConfctrl){
                    mPresenter.muteCall(Integer.valueOf(confID));
                }else {
                    mPresenter.muteSelf();
                }
                break;
            case R.id.conf_loud_speaker:
                LogUtil.i(UIConstants.DEMO_TAG, "conference speaker!");
                updateLoudSpeakerButton(mPresenter.switchLoudSpeaker());
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
                if (null == memberList || memberList.size()<=0)
                {
                    return;
                }
                final View popupView = getLayoutInflater().inflate(R.layout.popup_conf_list, null);
                mConfMemberListView = (ListView) popupView.findViewById(R.id.popup_conf_member_list);

                View headView = getLayoutInflater().inflate(R.layout.popup_video_conf_list_item, null);
                final TextView tvDisplayName = (TextView) headView.findViewById(R.id.name_tv);
                ImageView isMainHall = (ImageView) headView.findViewById(R.id.host_logo);
                tvDisplayName.setText(LocContext.getString(R.string.main_conference));
                isMainHall.setImageResource(R.drawable.group_detail_group_icon);
                mConfMemberListView.addHeaderView(headView);

                mAdapter.setData(memberList);
                mConfMemberListView.setAdapter(mAdapter);
                mPopupWindow = PopupWindowUtil.getInstance().generatePopupWindow(popupView);
                mPopupWindow.showAsDropDown(findViewById(R.id.right_iv));
                mConfMemberListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    {
                        Member conferenceMemberEntity = new Member();
                        if (0 == position)
                        {
                            conferenceMemberEntity.setDisplayName(tvDisplayName.getText().toString());
                            conferenceMemberEntity.setNumber("");
                        }
                        else
                        {
                            conferenceMemberEntity = memberList.get(position - 1);
                        }

                        if (null != conferenceMemberEntity)
                        {
                            mPresenter.watchAttendee(conferenceMemberEntity);
                        }
                        mPopupWindow.dismiss();
                    }
                });
                break;
            case R.id.conf_add_attendee:
                showAddMemberDialog();
                break;
            case R.id.conf_attendee:
                Intent intent = new Intent(IntentConstant.CONF_MEMBER_LIST_ACTIVITY_ACTION);
                intent.putExtra(UIConstants.CONF_ID, confID);
                intent.putExtra(UIConstants.IS_VIDEO_CONF, isVideo);
                intent.putExtra(UIConstants.IS_DATE_CONF, isDateConf);
                ActivityUtil.startActivity(this, intent);
                break;
            case R.id.btn_conf_more:
                showMoreConfCtrl();
                break;
            case R.id.leave_iv:
                if (!mPresenter.isChairMan())
                {
                    showLeaveConfDialog();
                }
                else
                {
                    showEndConfDialog();
                }
                break;
            case R.id.share_iv:
                Intent shareIntent = new Intent(IntentConstant.CONF_DATA_ACTIVITY_ACTION);
                shareIntent.putExtra(UIConstants.CONF_ID, confID);
                shareIntent.putExtra(UIConstants.IS_VIDEO_CONF, isVideo);
                shareIntent.putExtra(UIConstants.IS_START_SHARE_CONF, isStartShare);
                ActivityUtil.startActivity(this, shareIntent);
                break;
            default:
                break;
        }
    }

    private View.OnClickListener moreButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mPopupWindow != null && mPopupWindow.isShowing())
            {
                mPopupWindow.dismiss();
            }
            switch (v.getId())
            {
                case R.id.switch_camera_ll:
                    LogUtil.i(UIConstants.DEMO_TAG, "conference switch camera!");
                    mPresenter.switchCamera();
                    break;
                case R.id.close_camera_ll:
                    isCameraClose = !isCameraClose;
                    boolean result = mPresenter.closeOrOpenLocalVideo(isCameraClose);
                    if (!result)
                    {
                        showToast(cameraStatusIV.isActivated() ? R.string.close_video_failed : R.string.open_video_failed);
                    }
                    break;
                case R.id.upgrade_conf_ll:
                    mPresenter.updateConf();
                    break;
                case R.id.request_chairman_ll:
                    showRequestChairmanDialog();
                    break;
                case R.id.release_chairman_ll:
                    mPresenter.releaseChairman();
                    break;
                case R.id.set_conf_mode_ll:
                    showConfMode();
                    break;
                default:
                    break;
            }
        }
    };

    private void showRequestChairmanDialog()
    {
        final EditDialog dialog = new EditDialog(this, R.string.input_chairman_password);
        dialog.setRightButtonListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                CommonUtil.hideSoftInput(ConfManagerActivity.this);
                mPresenter.requestChairman(dialog.getText());
            }
        });
        dialog.show();
    }

    private void showConfMode()
    {
        final SimpleListDialog dialog = new SimpleListDialog(this, items);
        dialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog.dismiss();
                mPresenter.onItemDetailClick((String) items.get(position), null);
            }
        });
        dialog.show();
    }

    @Override
    public void refreshMemberList(final List<Member> list)
    {
        if (null == list || list.size() <= 0)
        {
            return;
        }
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                showMoreButton();
                mAdapter.setData(list);
                mAdapter.notifyDataSetChanged();

                if (!isVideo)
                {
                    mAudioConfAttendeeTV.setText(getAttendeeName(list));
                }
            }
        });
    }

    private String getAttendeeName(List<Member> list)
    {
        if (1 == list.size())
        {
            return list.get(0).getDisplayName();
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < list.size(); i ++)
        {
            if (i == list.size() - 1)
            {
                builder.append(list.get(i).getDisplayName());
            }
            else
            {
                builder.append(list.get(i).getDisplayName() + ", ");
            }
        }

        return builder.toString();
    }

    private void showMoreButton()
    {
        noConfctrl = false;
        mConfAttendee.setVisibility(View.VISIBLE);
        if (mPresenter.isChairMan())
        {
            mConfAddAttendee.setVisibility(View.VISIBLE);
        }
        else
        {
            mConfAddAttendee.setVisibility(View.GONE);
        }
    }

    @Override
    public void showItemClickDialog(final List<Object> items, final Member member) {
        final SimpleListDialog dialog = new SimpleListDialog(this, items);
        dialog.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                dialog.dismiss();
                mPresenter.onItemDetailClick((String) items.get(position), member);
            }
        });
        dialog.show();
    }

    @Override
    public void updateUpgradeConfBtn(final boolean isInDataConf) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                isDateConf = isInDataConf;
                mShareIV.setVisibility(isInDataConf ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public void updateConfTypeIcon(final ConfBaseInfo confBaseInfo) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTitleTV.setText(mPresenter.getSubject());
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
                mPresenter.leaveConf();
                ActivityStack.getIns().popup(ConfMemberListActivity.class);
                stopTimer();
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
                mPresenter.leaveConf();
                ActivityStack.getIns().popup(ConfMemberListActivity.class);
                finish();
            }
        });
        dialog.setLeftButtonListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mPresenter.endConf();
                ActivityStack.getIns().popup(ConfMemberListActivity.class);
                stopTimer();
                finish();
            }
        });
        dialog.show();
    }

    @Override
    protected void onBack() {
        super.onBack();
        mPresenter.leaveConf();
        mPresenter.unregisterBroadcast();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mPresenter.leaveConf();
        mPresenter.unregisterBroadcast();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mPresenter.unregisterBroadcast();
        mPresenter.setAutoRotation(this, false, mOrientation);
        PopupWindowUtil.getInstance().dismissPopupWindow(mPopupWindow);
    }

    @Override
    public void updateLocalVideo()
    {
        mPresenter.setVideoContainer(this, mConfSmallLayout, mConfRemoteVideoLayout, mHideVideoLayout);
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

    private synchronized PopupWindow generatePopupWindow(View view, int width, int height)
    {
        final PopupWindow popupWindow = new PopupWindow(view, width, height);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.setOnKeyListener(new View.OnKeyListener()
        {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (keyCode == KeyEvent.KEYCODE_MENU && popupWindow.isShowing())
                {
                    popupWindow.dismiss();
                    return true;
                }
                return false;
            }
        });

        return popupWindow;
    }

    private void showAddMemberDialog()
    {
        final ThreeInputDialog editDialog = new ThreeInputDialog(this);
        editDialog.setRightButtonListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mPresenter.addMember(editDialog.getInput2(), editDialog.getInput1(), editDialog.getInput3());
            }
        });

        editDialog.setHint1(R.string.input_number);
        editDialog.setHint2(R.string.input_name);
        editDialog.setHint3(R.string.input_account);
        editDialog.show();
    }

    private void showMoreConfCtrl()
    {
        int wrap = LinearLayout.LayoutParams.WRAP_CONTENT;
        View popupView = getLayoutInflater().inflate(R.layout.popup_conf_btn_list, null);

        LinearLayout switchCameraBtn = (LinearLayout) popupView.findViewById(R.id.switch_camera_ll);
        LinearLayout closeCameraBtn = (LinearLayout) popupView.findViewById(R.id.close_camera_ll);
        LinearLayout handUpLayout = (LinearLayout) popupView.findViewById(R.id.hand_up_ll);
        LinearLayout muteAllLayout = (LinearLayout) popupView.findViewById(R.id.mute_all_ll);
        LinearLayout cancelMuteAllLayout = (LinearLayout) popupView.findViewById(R.id.cancel_mute_all_ll);
        LinearLayout lockLayout = (LinearLayout) popupView.findViewById(R.id.lock_conf_ll);
        LinearLayout unlockLayout = (LinearLayout) popupView.findViewById(R.id.un_lock_conf_ll);
        LinearLayout upgradeLayout = (LinearLayout) popupView.findViewById(R.id.upgrade_conf_ll);
        LinearLayout requestChairManLayout = (LinearLayout) popupView.findViewById(R.id.request_chairman_ll);
        LinearLayout releaseChairManLayout = (LinearLayout) popupView.findViewById(R.id.release_chairman_ll);
        LinearLayout seConfModeLayout = (LinearLayout) popupView.findViewById(R.id.set_conf_mode_ll);
        cameraStatusIV = (ImageView) popupView.findViewById(R.id.iv_camera_status);
        cameraStatusTV = (TextView) popupView.findViewById(R.id.tv_camera_status);
        ImageView handUpIV = (ImageView) popupView.findViewById(R.id.hand_up_iv);
        TextView handUpTV = (TextView) popupView.findViewById(R.id.hand_up_tv);

        //VC下不支持这几项功能，屏蔽入口
        handUpLayout.setVisibility(View.GONE);
        muteAllLayout.setVisibility(View.GONE);
        cancelMuteAllLayout.setVisibility(View.GONE);
        lockLayout.setVisibility(View.GONE);
        unlockLayout.setVisibility(View.GONE);
        upgradeLayout.setVisibility(View.GONE);
        seConfModeLayout.setVisibility(View.GONE);

        cameraStatusIV.setActivated(isCameraClose);
        cameraStatusTV.setText(isCameraClose ? getString(R.string.open_local_camera) :
                getString(R.string.close_local_camera));

        // 主席：会场静音、锁定、释放主席权限； 普通与会者：举手、申请主席
        if (mPresenter.isChairMan())
        {

            requestChairManLayout.setVisibility(View.GONE);
            releaseChairManLayout.setVisibility(View.VISIBLE);
        }
        else
        {
            //无会控时，屏蔽申请主席入口
            if(noConfctrl){
                requestChairManLayout.setVisibility(View.GONE);
                releaseChairManLayout.setVisibility(View.GONE);
            }else {
                requestChairManLayout.setVisibility(View.VISIBLE);
                releaseChairManLayout.setVisibility(View.GONE);
            }
        }

        // 音频不显示关闭、切换摄像头、选看和广播以及设置会议模式
        if (!isVideo)
        {
            switchCameraBtn.setVisibility(View.GONE);
            closeCameraBtn.setVisibility(View.GONE);
            seConfModeLayout.setVisibility(View.GONE);
        }
        else
        {
            switchCameraBtn.setOnClickListener(moreButtonListener);
            closeCameraBtn.setOnClickListener(moreButtonListener);
            seConfModeLayout.setOnClickListener(moreButtonListener);
        }


        upgradeLayout.setOnClickListener(moreButtonListener);
        requestChairManLayout.setOnClickListener(moreButtonListener);
        releaseChairManLayout.setOnClickListener(moreButtonListener);

        mPopupWindow = generatePopupWindow(popupView, wrap, wrap);
        mPopupWindow.showAtLocation(findViewById(R.id.media_btn_group), Gravity.RIGHT | Gravity.BOTTOM, 0, mConfButton.getHeight());
    }

    /**
     * 屏幕旋转时调用此方法
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == 2)
        {
            mConfSmallLayout.getLayoutParams().width = dp2ps(this, 160);
            mConfSmallLayout.getLayoutParams().height = dp2ps(this, 90);
        }
        else
        {
            mConfSmallLayout.getLayoutParams().width = dp2ps(this, 90);
            mConfSmallLayout.getLayoutParams().height = dp2ps(this, 160);
        }

        if (this.mOrientation == newConfig.orientation)
        {
            return;
        }
        else
        {
            this.mOrientation = newConfig.orientation;
            mPresenter.setAutoRotation(this, true, this.mOrientation);
        }
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     * @param context
     * @param dpValue
     * @return
     */
    private int dp2ps(Context context, float dpValue)
    {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (scale * dpValue + 0.5f);
    }
    
    private void showButton()
    {
        if (mTitleLayout.getVisibility() == View.GONE || mConfMediaLayout.getVisibility() == View.GONE)
        {
            mTitleLayout.setVisibility(View.VISIBLE);
            mConfMediaLayout.setVisibility(View.VISIBLE);
            isShowBar = true;
        }
    }
    
    private void hideButton()
    {
        if (mTitleLayout.getVisibility() == View.VISIBLE || mConfMediaLayout.getVisibility() == View.VISIBLE)
        {
            mTitleLayout.setVisibility(View.GONE);
            mConfMediaLayout.setVisibility(View.GONE);
            isShowBar = false;
        }
    }
    
    private void startTimer()
    {
        initTimer();
        try {
            if (isFirstStart)
            {
                timer.schedule(myTimerTask, 5000);
            }
            else 
            {
                timer.schedule(myTimerTask, 200, 5000);
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
            initTimer();
            timer.schedule(myTimerTask, 5000);
        }
    }
    
    private void stopTimer()
    {
        if (null != timer)
        {
            timer.cancel();
            timer = null;
        }
    }

    private void initTimer()
    {
        timer = new Timer();
        myTimerTask = new MyTimerTask();
    }

    class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isFirstStart)
                    {
                        hideButton();
                        isFirstStart = false;
                        stopTimer();
                    }
                    else 
                    {
                        if (isShowBar)
                        {
                            hideButton();
                            isPressTouch = false;
                            stopTimer();
                        }
                        else
                        {
                            showButton();
                        }
                    }
                }
            });
        }
    }

    @Override
    public void updateButtons(Member conferenceMemberEntity)
    {
        updateMuteButton(conferenceMemberEntity.isMute());
    }

    public void updateMuteButton(final boolean mute)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                mConfMute.setActivated(mute);
            }
        });
    }

    @Override
    public void switchMuteBtn(boolean currentMuteStatus) {
        mConfMute.setActivated(!currentMuteStatus);
    }
}
