package com.huawei.opensdk.ec_sdk_demo.logic.conference;


import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.huawei.opensdk.callmgr.CallInfo;
import com.huawei.opensdk.commonservice.common.LocContext;
import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcastReceiver;
import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.demoservice.ConfConstant;
import com.huawei.opensdk.demoservice.IConfNotification;
import com.huawei.opensdk.demoservice.MeetingMgr;
import com.huawei.opensdk.demoservice.QueryJoinDataConfParamInfo;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.ui.IntentConstant;
import com.huawei.opensdk.ec_sdk_demo.util.ActivityUtil;
import com.huawei.opensdk.sdkwrapper.login.LoginCenter;

import object.Conf;
import object.DataConfParam;

public class ConfFunc implements IConfNotification
{
    private static final int BOOK_CONF_SUCCESS = 100;
    private static final int BOOK_CONF_FAILED = 101;
    private static final int JOIN_CONF_SUCCESS = 110;
    private static final int JOIN_CONF_FAILED = 111;

    private static ConfFunc mInstance = new ConfFunc();
    private String[] broadcastNames = new String[]{CustomBroadcastConstants.CONF_INFO_PARAM,
            CustomBroadcastConstants.VCCONF_INFO_PARAM,
            CustomBroadcastConstants.CONF_CALL_CONNECTED};

    private ConfFunc()
    {
        LocBroadcast.getInstance().registerBroadcast(receiver,broadcastNames);
    }

    private Handler mHandler = new Handler(Looper.getMainLooper())
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case BOOK_CONF_SUCCESS:
                    Toast.makeText(LocContext.getContext(), "book conf success.",
                            Toast.LENGTH_SHORT).show();
                    break;

                case BOOK_CONF_FAILED:
                    Toast.makeText(LocContext.getContext(), "book conf failed.",
                            Toast.LENGTH_SHORT).show();
                    break;

                case JOIN_CONF_SUCCESS:
                    if (msg.obj instanceof String)
                    {
                        String confID = (String)msg.obj;
                        Intent intent = new Intent(IntentConstant.CONF_MANAGER_ACTIVITY_ACTION);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(UIConstants.CONF_ID, confID);
                        ActivityUtil.startActivity(LocContext.getContext(), intent);
                    }
                    break;

                case JOIN_CONF_FAILED:
                    Toast.makeText(LocContext.getContext(), "join conf failed.",
                            Toast.LENGTH_SHORT).show();
                    break;

                default:
                    break;
            }
        }
    };


    private LocBroadcastReceiver receiver = new LocBroadcastReceiver()
    {
        @Override
        public void onReceive(String broadcastName, Object obj)
        {
            switch (broadcastName)
            {
                case CustomBroadcastConstants.CONF_INFO_PARAM:

                    if (MeetingMgr.getInstance().getVcMediaType()==ConfConstant.ConfMediaType.VOICE_AND_DATA_CONF ||
                            MeetingMgr.getInstance().getVcMediaType()==ConfConstant.ConfMediaType.VIDEO_AND_DATA_CONF)
                    {
                        return;
                    }
                    else if(MeetingMgr.getInstance().getVcMediaType()==ConfConstant.ConfMediaType.VIDEO_CONF)
                    {
                        Conf confParam = (Conf)obj;

                        // 当前不在会议中，则加入会议
                        if (!MeetingMgr.getInstance().isInConference())
                        {
                            String mediaxToken = "";
                            if (LoginCenter.getInstance().getServerType() == LoginCenter.getInstance().LOGIN_E_SERVER_TYPE_MEDIAX){
                                mediaxToken = LoginCenter.getInstance().getLoginStatus().getAuthResult().getAuthToken();
                            }
                            int result = MeetingMgr.getInstance().joinConfByToken(String.valueOf(confParam.getCallId()),mediaxToken , ConfConstant.ConfRole.ATTENDEE);
//                            MeetingMgr.getInstance().getCurrentConferenceBaseInfo().setMediaType();

                            if (result != 0)
                            {
                                LogUtil.e(UIConstants.DEMO_TAG,  "join conf failed, return -> " + result);
                                mHandler.sendEmptyMessage(JOIN_CONF_FAILED);
                                return;
                            }
                            MeetingMgr.getInstance().setCurrentConferenceCallID(confParam.getCallId());
                        }
                    }

                    break;
                case CustomBroadcastConstants.VCCONF_INFO_PARAM:

                    DataConfParam confParam = (DataConfParam)obj;

                    // 当前不在会议中，则加入会议
                    if (!MeetingMgr.getInstance().isInConference())
                    {
                        String mediaxToken = "";
                        if (LoginCenter.getInstance().getServerType() == LoginCenter.getInstance().LOGIN_E_SERVER_TYPE_MEDIAX){
                            mediaxToken = LoginCenter.getInstance().getLoginStatus().getAuthResult().getAuthToken();
                        }
                        int result = MeetingMgr.getInstance().joinConfByToken(confParam.getCallId()+"",mediaxToken , ConfConstant.ConfRole.ATTENDEE);

                        if (result != 0)
                        {
                            LogUtil.e(UIConstants.DEMO_TAG,  "join conf failed, return -> " + result);
                            mHandler.sendEmptyMessage(JOIN_CONF_FAILED);
                            return;
                        }
                        MeetingMgr.getInstance().setCurrentConferenceCallID(confParam.getCallId());
                    }

                    //保存当前会议参数信息，用于加入会议后，获取数据会议入会参数
                    QueryJoinDataConfParamInfo info = new QueryJoinDataConfParamInfo();

                    info.setGetDataParam(true);

                    if (LoginCenter.getInstance().getServerType() == LoginCenter.getInstance().LOGIN_E_SERVER_TYPE_SMC){
                        info.setCallId(confParam.getCallId());
                        info.setConfId(confParam.getDataConfId());
//                        info.setPassCode(confParam.getDataConfId());
                        info.setRandom(confParam.getDataRandom());
                        String Url = LoginCenter.getInstance().getConfConfigInfo().getServerUri();
                        int port = LoginCenter.getInstance().getConfConfigInfo().getServerPort();
                        info.setConfUrl("https://"+Url+":"+port);
                    }else if(LoginCenter.getInstance().getServerType() == LoginCenter.getInstance().LOGIN_E_SERVER_TYPE_MEDIAX) {
                        info.setCallId(confParam.getCallId());
                        info.setConfId(confParam.getDataConfId());
                        info.setPassCode(confParam.getPassCode());
                        info.setRandom(confParam.getDataRandom());
                        info.setConfUrl(confParam.getDataConfUrl());
                    }

                    MeetingMgr.getInstance().saveCurrentConferenceGetDataConfParamInfo(info);
                    break;

                case CustomBroadcastConstants.CONF_CALL_CONNECTED:
                    if (obj instanceof CallInfo)
                    {
                        CallInfo callInfo = (CallInfo)obj;
                        MeetingMgr.getInstance().setDateConfId(callInfo.getConfID());
                    }
                    break;

                default:
                    break;
            }
        }
    };


    public static ConfFunc getInstance()
    {
        return mInstance;
    }

    @Override
    public void onConfEventNotify(ConfConstant.CONF_EVENT confEvent, Object params) {
        switch (confEvent)
        {
            case BOOK_CONF_SUCCESS:
                mHandler.sendEmptyMessage(BOOK_CONF_SUCCESS);
                break;

            case BOOK_CONF_FAILED:
                mHandler.sendEmptyMessage(BOOK_CONF_FAILED);
                break;

            case JOIN_CONF_SUCCESS:
                mHandler.sendMessage(mHandler.obtainMessage(JOIN_CONF_SUCCESS, params));

                break;

            case JOIN_CONF_FAILED:
                mHandler.sendEmptyMessage(JOIN_CONF_FAILED);
                break;

            case STATE_UPDATE:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.CONF_STATE_UPDATE, params);
                break;

            case CHAIRMAN_INFO:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.CONF_CHAIRMAN_INFO, params);
                break;

            case ADD_YOURSELF_FAILED:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ADD_SELF_RESULT, params);
                break;

            case ADD_ATTENDEE_RESULT:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ADD_ATTENDEE_RESULT, params);
                break;

            case DEL_ATTENDEE_RESULT:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.DEL_ATTENDEE_RESULT, params);
                break;

            case MUTE_ATTENDEE_RESULT:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.MUTE_ATTENDEE_RESULT, params);
                break;

            case UN_MUTE_ATTENDEE_RESULT:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.UN_MUTE_ATTENDEE_RESULT, params);
                break;

            case REQUEST_CHAIRMAN_RESULT:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.REQUEST_CHAIRMAN_RESULT, params);
                break;

            case RELEASE_CHAIRMAN_RESULT:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.RELEASE_CHAIRMAN_RESULT, params);
                break;

            case CHAIRMAN_RELEASE_IND:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.RELEASE_CHAIRMAN_IND, params);
                break;

            case WILL_TIMEOUT:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.WILL_TIMEOUT, params);
                break;

            case POSTPONE_CONF_RESULT:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.POSTPONE_CONF_RESULT, params);
                break;

            case SPEAKER_LIST_IND:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.SPEAKER_LIST_IND, params);
                break;

            case SET_CONF_MODE_RESULT:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.SET_CONF_MODE_RESULT, params);
                break;

            case GET_DATA_CONF_PARAM_RESULT:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.GET_DATA_CONF_PARAM_RESULT, params);
                break;

            case UPGRADE_CONF_RESULT:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.UPGRADE_CONF_RESULT, params);

            case JOIN_DATA_CONF_RESULT:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.DATA_CONFERENCE_JOIN_RESULT, params);
                break;

            case JOIN_DATA_CONF_TERMINATE:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.CONF_MSG_ON_CONFERENCE_TERMINATE, params);
                break;

            case CAMERA_STATUS_UPDATE:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.DATA_CONFERENCE_CAMERA_STATUS_UPDATE, params);
                break;

            case WATCH_ATTENDEE_RESULT:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.WATCH_ATTENDEE_CONF_RESULT, params);
                break;

            case BROADCAST_ATTENDEE_RESULT:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.BROADCAST_ATTENDEE_CONF_RESULT, params);
                break;
            case CANCEL_BROADCAST_ATTENDEE_RESULT:
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.CANCEL_BROADCAST_ATTENDEE_CONF_RESULT, params);
                break;


            default:
                break;
        }
    }
}
