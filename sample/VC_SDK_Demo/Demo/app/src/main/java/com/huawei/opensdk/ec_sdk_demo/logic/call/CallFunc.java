package com.huawei.opensdk.ec_sdk_demo.logic.call;

import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.huawei.opensdk.callmgr.CallConstant;
import com.huawei.opensdk.callmgr.CallInfo;
import com.huawei.opensdk.callmgr.CallMgr;
import com.huawei.opensdk.callmgr.ICallNotification;
import com.huawei.opensdk.commonservice.common.LocContext;
import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;
import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.demoservice.MeetingMgr;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.ui.IntentConstant;
import com.huawei.opensdk.ec_sdk_demo.ui.base.ActivityStack;
import com.huawei.opensdk.ec_sdk_demo.util.ActivityUtil;
import com.huawei.opensdk.ec_sdk_demo.util.FileUtil;

import java.io.File;


public class CallFunc implements ICallNotification
{
    private static final int UPGRADE_FAILED = 100;
    private static final int CTD_FAILED = 101;
    private static final int CTD_SUCCESS = 102;
    private static final String RINGING_FILE = "ringing.wav";
    private static final String RING_BACK_FILE = "ring_back.wav";

    private boolean mMuteStatus;
    private String mFilePath;

    private static CallFunc mInstance = new CallFunc();

    private Handler mHandler = new Handler(Looper.getMainLooper())
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case UPGRADE_FAILED:
                    Toast.makeText(LocContext.getContext(), LocContext.getContext().getString(R.string.video_be_refused),
                            Toast.LENGTH_LONG).show();
                    break;
                case CTD_FAILED:
                    Toast.makeText(LocContext.getContext(), LocContext.getContext().getString(R.string.ctd_failed),
                            Toast.LENGTH_SHORT).show();
                    break;
                case CTD_SUCCESS:
                    Toast.makeText(LocContext.getContext(), LocContext.getContext().getString(R.string.ctd_success),
                            Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    public static CallFunc getInstance()
    {
        return mInstance;
    }

    @Override
    public void onCallEventNotify(CallConstant.CallEvent event, Object obj)
    {
        switch (event)
        {
            //来电
            case CALL_COMING:
                LogUtil.i(UIConstants.DEMO_TAG, "call coming!");

                if (obj instanceof CallInfo)
                {
                    CallInfo callInfo = (CallInfo)obj;

                    //如果是会议，则判断是否需要自动接听
                    if (callInfo.isFocus() == true)
                    {
                        boolean isAutoAnswer = MeetingMgr.getInstance().judgeInviteFormMySelf(callInfo.getConfID());
                        if (isAutoAnswer == true)
                        {
                            LogUtil.i(UIConstants.DEMO_TAG, "auto answer conf incoming!");
                            //自动接听使用来电类型进行接听
                            CallMgr.getInstance().answerCall(callInfo.getCallID(), callInfo.isVideoCall());
                            return;
                        }
                    }
                    else
                    {
                        LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_CALL_COMING, obj);
                    }

                    mFilePath = Environment.getExternalStorageDirectory() + File.separator + RINGING_FILE;
                    CallMgr.getInstance().startPlayRingingTone(mFilePath);

                    Intent intent = new Intent(IntentConstant.CALL_IN_ACTIVITY_ACTION);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addCategory(IntentConstant.DEFAULT_CATEGORY);

                    intent.putExtra(UIConstants.CALL_INFO, callInfo);
                    ActivityUtil.startActivity(LocContext.getContext(), intent);
                }
                break;

            //去电
            case CALL_GOING:
                LogUtil.i(UIConstants.DEMO_TAG, "call going!");

                if (obj instanceof CallInfo)
                {
                    CallInfo callInfo = (CallInfo)obj;

                    Intent intent = new Intent(IntentConstant.CALL_OUT_ACTIVITY_ACTION);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addCategory(IntentConstant.DEFAULT_CATEGORY);

                    intent.putExtra(UIConstants.CALL_INFO, callInfo);
                    ActivityUtil.startActivity(LocContext.getContext(), intent);

                    LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_CALL_GOING, obj);
                }
                break;

            //播放回铃音
            case PLAY_RING_BACK_TONE:
                LogUtil.i(UIConstants.DEMO_TAG, "play ring back!");

                if (FileUtil.isSdCardExist()){
                    mFilePath = Environment.getExternalStorageDirectory() + File.separator + RING_BACK_FILE;
                    CallMgr.getInstance().startPlayRingBackTone(mFilePath);
                }
                break;

            //媒体通道建立
            case RTP_CREATED:
                if (obj instanceof CallInfo)
                {
                    CallMgr.getInstance().stopPlayRingingTone();
                    CallMgr.getInstance().stopPlayRingBackTone();
                    LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.CALL_MEDIA_CONNECTED, obj);
                }

                break;

            //呼叫建立成功
            case CALL_CONNECTED:
                LogUtil.i(UIConstants.DEMO_TAG, "call connected ");

                if (obj instanceof CallInfo)
                {
                    CallMgr.getInstance().stopPlayRingingTone();
                    CallMgr.getInstance().stopPlayRingBackTone();

                    CallInfo callInfo = (CallInfo) obj;
                    //stopMedia();
                    if (callInfo.isFocus())
                    {
                        if (callInfo.isVideoCall()) {
                            LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.CONF_CALL_CONNECTED, callInfo);
                        } else {
                            LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_CALL_CONNECTED, callInfo);
                        }
                    }
                    else
                    {
                        LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_CALL_CONNECTED, callInfo);
                    }
                }
                break;

            //呼叫结束
            case CALL_ENDED:
                LogUtil.i(UIConstants.DEMO_TAG, "call end!");

                if (obj instanceof CallInfo)
                {
                    CallInfo params = (CallInfo) obj;
                    if (params.isFocus() == true)
                    {
                        LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.GET_CONF_END, params);
                    }
                    //呼叫可能没有接通，结束时停止可能存在的振铃音和回铃音
                    CallMgr.getInstance().stopPlayRingingTone();
                    CallMgr.getInstance().stopPlayRingBackTone();

                    LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_CALL_END, params);

                    resetData();
                }
                break;

            //语音呼叫保持成功
            case AUDIO_HOLD_SUCCESS:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.HOLD_CALL_RESULT, "HoldSuccess");
                break;

            //语音呼叫保持失败
            case AUDIO_HOLD_FAILED:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.HOLD_CALL_RESULT, "HoldFailed");
                break;

            //视频呼叫保持成功
            case VIDEO_HOLD_SUCCESS:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.HOLD_CALL_RESULT, "VideoHoldSuccess");
                break;

            //视频呼叫保持失败
            case VIDEO_HOLD_FAILED:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.HOLD_CALL_RESULT, "VideoHoldFailed");
                break;

            //取消保持(恢复)成功
            case UN_HOLD_SUCCESS:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.HOLD_CALL_RESULT, "UnHoldSuccess");
                break;

            //取消保持(恢复)失败
            case UN_HOLD_FAILED:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.HOLD_CALL_RESULT, "UnHoldFailed");
                break;

            //关闭视频
            case CLOSE_VIDEO:
                LogUtil.i(UIConstants.DEMO_TAG, "close video.");

                if (obj instanceof CallInfo)
                {
                    CallInfo callInfo = (CallInfo)obj;

                    Intent intent = new Intent(IntentConstant.CALL_OUT_ACTIVITY_ACTION);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addCategory(IntentConstant.DEFAULT_CATEGORY);
                    intent.putExtra(UIConstants.CALL_INFO, callInfo);

                    ActivityStack.getIns().popup(ActivityStack.getIns().getCurActivity());
                    ActivityUtil.startActivity(LocContext.getContext(), intent);
                }
                break;

            //打开视频
            case OPEN_VIDEO:
                LogUtil.i(UIConstants.DEMO_TAG, "open video.");

                if (obj instanceof CallInfo)
                {
                    CallInfo callInfo = (CallInfo)obj;

                    Intent intent = new Intent(IntentConstant.VIDEO_ACTIVITY_ACTION);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addCategory(IntentConstant.DEFAULT_CATEGORY);
                    intent.putExtra(UIConstants.CALL_INFO, callInfo);

                    ActivityStack.getIns().popup(ActivityStack.getIns().getCurActivity());
                    ActivityUtil.startActivity(LocContext.getContext(), intent);
                }
                break;

            case ADD_LOCAL_VIEW:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ADD_LOCAL_VIEW, obj);
                break;

            case DEL_LOCAL_VIEW:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.DEL_LOCAL_VIEW, obj);
                break;

            //远端拒绝增加视频请求
            case REMOTE_REFUSE_ADD_VIDEO_SREQUEST:
                LogUtil.i(UIConstants.DEMO_TAG, "remote refuse upgrade video!");
                mHandler.sendEmptyMessage(UPGRADE_FAILED);
                break;

            //收到远端增加视频请求
            case RECEIVED_REMOTE_ADD_VIDEO_REQUEST:
                LogUtil.i(UIConstants.DEMO_TAG, "Add video call!");
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.CALL_UPGRADE_ACTION, obj);
                break;




            case CONF_INFO_NOTIFY:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.CONF_INFO_PARAM, obj);
                break;

            case DATACONF_INFO_NOTIFY:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.VCCONF_INFO_PARAM, obj);
                break;
            case CONF_END:
                break;
            case SESSION_MODIFIED:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.SESSION_MODIFIED_RESULT, obj);
                break;


            default:
                break;
        }
    }

    private void resetData()
    {
        //mPlayHandle = -1;
        mMuteStatus = false;
    }

    public boolean isMuteStatus()
    {
        return mMuteStatus;
    }

    public void setMuteStatus(boolean mMuteStatus)
    {
        this.mMuteStatus = mMuteStatus;
    }

}
