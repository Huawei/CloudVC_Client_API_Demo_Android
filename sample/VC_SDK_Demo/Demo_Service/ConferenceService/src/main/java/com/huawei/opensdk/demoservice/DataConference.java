package com.huawei.opensdk.demoservice;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.huawei.meeting.ConfDefines;
import com.huawei.meeting.ConfExtendChatMsg;
import com.huawei.meeting.ConfExtendMsg;
import com.huawei.meeting.ConfExtendUserInfoMsg;
import com.huawei.meeting.ConfExtendVideoDeviceInfoMsg;
import com.huawei.meeting.ConfExtendVideoParamMsg;
import com.huawei.meeting.ConfGLView;
import com.huawei.meeting.ConfInfo;
import com.huawei.meeting.ConfInstance;
import com.huawei.meeting.ConfMsg;
import com.huawei.meeting.ConfPrew;
import com.huawei.meeting.ConfResult;
import com.huawei.meeting.IConferenceUI;
import com.huawei.opensdk.commonservice.common.LocContext;
import com.huawei.opensdk.demoservice.data.CameraEntity;
import com.huawei.opensdk.demoservice.data.callBackWapper.VideoSwitchInfoWapper;
import com.huawei.opensdk.sdkwrapper.manager.TupMgr;
import com.huawei.videoengine.ViERenderer;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * This class is about date conf Function management.
 * 数据会议回调管理类
 */
public class DataConference implements IConferenceUI {
    private static final String TAG = MeetingInstance.class.getSimpleName();

    /**
     * conf ID
     * 会议id
     */
    private String confID;

    /**
     * Meeting instance
     * 会议实体
     */
    private MeetingInstance meetingInstance;

    /**
     * UI call back
     * UI回调
     */
    private IConfNotification confNotification;

    /**
     * conference heart beat
     * 会议心跳
     */
    private Handler heartBeatHandler;

    /**
     * Heart beat timer
     * 心跳定时器
     */
    private Timer heartBeatTimer;

    /**
     * Data conf work thread
     * 数据会议工作线程
     */
    private DataConference.WorkThread dataConfWorkThread;

    /**
     * Conf instance
     * 会议实例
     */
    private ConfInstance mConfIns;

    /**
     * 数据会议共享页面帧数
     */
    private ConfPrew mConfPrew;
    private ConfGLView surfaceView;

    /**
     * 视频管理
     */
    private VideoManager videoManager;

    private int currentShareType = ConfDefines.IID_COMPONENT_AS;
    private int currentPage1;
    private int currentPage2;

    private boolean isRelease;

    /** Conference component switch */
    private int componentVal = ConfDefines.IID_COMPONENT_BASE
            | ConfDefines.IID_COMPONENT_DS
            | ConfDefines.IID_COMPONENT_AS
            | ConfDefines.IID_COMPONENT_CHAT
            | ConfDefines.IID_COMPONENT_WB;

    public static final String DATA_CONF_RES_PATH = LocContext.
            getContext().getFilesDir() + "/AnnoRes";


    public DataConference(MeetingInstance meetingInstance, IConfNotification notification) {
        this.meetingInstance = meetingInstance;
        this.confNotification = notification;
        this.confID = meetingInstance.getConfBaseInfo().getConfID();
    }


    /**
     * This method is used to join conference
     * 加入会议
     * @param confInfo  会议信息
     * @return
     */
    public int joinConf(ConfInfo confInfo) {
        mConfIns = new ConfInstance();
        mConfIns.setConfUI(this);
        mConfPrew = ConfPrew.getInstance();

        dataConfWorkThread = new WorkThread(confInfo);
        dataConfWorkThread.start();

        return 0;
    }

    /**
     * This method is used to leave conf
     * 离开会议
     * @return
     */
    public int leaveConf()
    {
        Log.i(TAG, "leave conference");
        if (mConfIns == null)
        {
            return 0;
        }

        int result = mConfIns.confLeave();
        if (result != 0)
        {
            Log.e(TAG, "leaveConf->" + result);
            return result;
        }

        isRelease = true;
        stopWorkThreadAndBeatTimer();

        return 0;
    }

    /**
     * This method is used to terminate conf
     * @return
     */
    public int terminateConf()
    {
        Log.i(TAG, "terminate conference");
        if (mConfIns == null)
        {
            return 0;
        }

        int result = mConfIns.confTerminate();
        if (result != 0)
        {
            Log.e(TAG, "confTerminate->" + result);
            return result;
        }

        return 0;
    }


    /**
     * This method is used to set up presenter
     * 设置主讲人
     * @param userId 用户id
     * @return
     */
    public int setPresenter(long userId) {
        if (mConfIns == null) {
            Log.e(TAG, "mConfIns is null");
            return -1;
        }

        int result = mConfIns.confUserSetRole(userId, ConfDefines.CONF_ROLE_PRESENTER);
        if (result != 0) {
            Log.i(TAG, "confUserSetRole->" + result);
        }
        return result;
    }

    /**
     * This method is used to setting up the moderator
     * 设置主持人
     * @param userId
     * @return
     */
    public int setHost(long userId) {
        if (mConfIns == null) {
            Log.e(TAG, "mConfIns is null");
            return -1;
        }

        int result = mConfIns.confUserSetRole(userId, ConfDefines.CONF_ROLE_HOST);
        if (result != 0) {
            Log.i(TAG, "confUserSetRole->" + result);
        }
        return result;
    }


    public void attachSurfaceView(ViewGroup container, Context context)
    {
        surfaceView = new ConfGLView(context);

        surfaceView.setConf(mConfIns);
        surfaceView.setViewType(currentShareType);

        container.addView(surfaceView);
        surfaceView.onResume();
        surfaceView.setVisibility(View.VISIBLE);

        if (currentShareType == ConfDefines.IID_COMPONENT_DS)
        {
            mConfIns.dsSetCurrentPage(currentPage1, currentPage2);
        }
        else if (currentShareType == ConfDefines.IID_COMPONENT_WB)
        {
            mConfIns.wbSetCurrentPage(currentPage1, currentPage2);
        }
    }

    public boolean attachVideo(long userID, long deviceID)
    {
        Member member = meetingInstance.getMemberByDataUserId(userID);
        if (member == null)
        {
            return false;
        }

        if (member.isSelf())
        {
            return attachLocalVideo(userID, deviceID);
        }
        else
        {
            return attachRemoteVideo(userID, deviceID);
        }
    }


    /**
     *This method is used to add remote video
     * 添加远端视频
     * @param userID
     * @param deviceID
     * @return
     */
    public boolean attachRemoteVideo(long userID, long deviceID)
    {
        boolean result1 = videoAttach(userID, deviceID, videoManager.getRemoteIndexOfSurface(), 1, 0);
        Log.i(TAG, "remote video attach result------: " + result1);
        if (result1)
        {
            videoManager.addViewToContainer();
        }
        return result1;
    }

    /**
     * This method is used to add local video
     * 添加本地视频
     * @param userID
     * @param deviceID
     * @return
     */
    public boolean attachLocalVideo(long userID, long deviceID)
    {
        boolean result1 = videoAttach(userID, deviceID, videoManager.getLocalIndexOfSurface(), 0, 0);
        Log.i(TAG, "local video attach result------: " + result1);
        if (result1)
        {
            videoManager.addViewToContainer();
        }
        return result1;
    }

    /**
     * This method is used to delete remote video
     * 删除远端视频
     * @param userID 用户id
     * @param deviceID 设备id
     * @return
     */
    public boolean detachRemoteVideo(long userID, long deviceID)
    {
        boolean result1 = videoDetach(userID, deviceID, videoManager.getRemoteIndexOfSurface(), true);
        Log.i(TAG, "remote video detach result------: " + result1);
        if (result1)
        {
            videoManager.addViewToContainer();
        }
        return result1;
    }

    /**
     * This method is used to delete local video
     * 删除本地视频
     * @param userID
     * @param deviceID
     * @return
     */
    public boolean detachLocalVideo(long userID, long deviceID)
    {
        boolean result1 = videoDetach(userID, deviceID, videoManager.getLocalIndexOfSurface(), true);
        Log.i(TAG, "local video detach result------: " + result1);
        if (result1)
        {
            videoManager.addViewToContainer();
        }
        return result1;
    }

    /**
     * This method is used to set local window visible
     * 设置本地窗口是否可见
     * @param visible
     */
    public void changeLocalVideoVisible(boolean visible)
    {
        Log.i(TAG, "change local video visible: " + visible);
        videoManager.svLocalSurfaceView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    /****************************************视频控制*****************************************/
    /**
     * This method is used to set related parameters for video
     * 设置视频的相关参数
     * @param deviceID id 设备ID
     * @param xResolution 视频宽
     * @param yResolution 视频高
     * @param nFrame 视频帧率
     * @param rotate 视频旋转角度
     * @return 返回TC_OK表示成功，其它表示失败
     */
    public boolean videoSetParam(long deviceID, int xResolution, int yResolution, int nFrame, int rotate)
    {
        Log.i(TAG, "videoParam :xRes=" + xResolution + ", yRes=" + yResolution
                + ", nFrame" + nFrame + ", rotate" + rotate);
        int nRet = mConfIns.videoSetParam(deviceID, xResolution, yResolution, nFrame, 0, rotate);
        Log.i(TAG, "videoSetParam result:" + nRet);
        return (nRet == 0);
    }

    /**
     * This method is used to open your own camera.
     * 打开自己的摄像头
     * @param deviceID the device id
     * @return the boolean
     */
    public boolean videoOpen(long deviceID)
    {
        videoSetParam(deviceID, 128*4, 96*4, 20, 0);
        Log.i(TAG, "video open deviceID: " + deviceID);
        int nRet = mConfIns.videoOpen(deviceID);
        return (nRet == 0);
    }

    /**
     * This method is used to video and window bindings
     * 指定用户的视频与窗口进行绑定
     * @param indexWnd the index wnd
     * @param userId the userid
     * @param deviceId the deviceid
     * @param indexWnd 窗口句柄
     * @param bHigh 加入低流或是高流(默认高流)
     * @param showMode 视频在窗口显示模式，取值：0：表示布满窗口;1：表示按视频的比例进行显示，其余部分以黑色填充;2：表示按窗口大小进行裁剪
     * @return 返回TC_OK表示成功，其它表示失败
     */
    public boolean videoAttach(long userId, long deviceId, int indexWnd, int bHigh, int showMode)
    {
        int result = mConfIns.videoAttach(userId, deviceId, indexWnd,
                bHigh, showMode);
        Log.i(TAG, "video attach result -----: " + result);
        return (result == 0);
    }

    /**
     * This method is used to close your own camera.
     * 关闭自己的摄像头
     * @param deviceID the device id
     * @param closeAll 是否关闭所有
     * @return 返回TC_OK表示成功，其它表示失败
     */
    public boolean videoClose(long deviceID, boolean closeAll)
    {
        int result = mConfIns.videoClose(deviceID, closeAll);
        return (result == 0);
    }

    /**
     * This method is used to video and window solution binding
     * 指定用户视频与窗口进行解绑
     * @param userId 用户ID，范围(0，2147483647]
     * @param deviceId the device id
     * @param indexWnd 窗口句柄
     * @param leaveChannel Detach窗口的时候，是否需要离开channel (默认是不离开channel)
     * @return 返回TC_OK表示成功，其它表示失败
     */
    public boolean videoDetach(long userId, long deviceId, int indexWnd, boolean leaveChannel)
    {
        int result = mConfIns.videoDetach(userId, deviceId, indexWnd, leaveChannel);
        return (result == 0);
    }

    /**
     * This method is used to notify other attendees to open the device
     * 通知其它与会者打开设备
     * @param userId the userId
     * @param deviceId the userid
     * @param xResolution 视频宽
     * @param yResolution 视频高
     * @param nFrameRate 帧率
     * @return 返回TC_OK表示成功，其它表示失败
     */
    public boolean videoNotifyOpen(long userId, long deviceId, int xResolution, int yResolution, int nFrameRate)
    {
        int nRet = mConfIns.videoNotifyOpen(userId, deviceId, xResolution, yResolution, nFrameRate);
        return (nRet == 0);
    }

    /**
     * This method is used to notify other attendees to close the device
     * 通知其它与会者关闭设备
     * @param userId 被通知的用户ID,范围(0，2147483647)
     * @param deviceId the device id
     * @return 返回TC_OK表示成功，其它表示失败
     */
    public boolean videoNotifyClose(int userId, int deviceId)
    {
        int nRet = mConfIns.videoNotifyClose(userId, deviceId);
        return (nRet == 0);
    }

    /**
     * This method is used to switch to high or low flow for receiving
     * 对于接收端来说，切换到高流或是低流
     * @param userId 用户ID，范围(0，2147483647]
     * @param deviceId device id
     * @param bHighChannel 切换到高流或是低流，取值:true为高流，false为低流
     * @return the boolean
     */
    public boolean videoSwitchChannel(long userId, long deviceId, boolean bHighChannel)
    {
        int nRet = mConfIns.videoSwitchChannel(userId, deviceId, bHighChannel);
        return (nRet == 0);
    }

    /**
     * resizeSharedView
     */
    private void resizeSharedView()
    {
        if (surfaceView != null)
        {
            surfaceView.resize();
        }
    }

    /**
     * updateDesktopSharedView
     */
    private void updateSharedView()
    {
        if (surfaceView != null)
        {
            surfaceView.update();
        }
    }

    /**
     * This method is used to pause video
     * 暂停视频
     * @param nUserID the n user id
     * @param deviceID the device id
     * @return the boolean
     */
    private boolean videoPause(int nUserID, String deviceID)
    {
        int result = mConfIns.videoPause(nUserID, Long.parseLong(deviceID));

        return (result == 0);
    }

    /**
     * This method is used to restore local video
     * 恢复本地视频
     * @param userID the user id
     * @param deviceID the device id
     * @return the boolean
     */
    public boolean videoResume(int userID, String deviceID)
    {
        int result = mConfIns.videoResume(userID, Long.parseLong(deviceID));
        return (result == 0);
    }

    /**
     * This method is used to open the local preview and start the video wizard
     * 打开本地预览,启动视频向导
     * @param devicdID the devicd id
     * @param iWndIndex the wnd index
     * @return the boolean
     */
    private boolean videoWizStartcapture(int devicdID, int iWndIndex)
    {
        int result = mConfPrew.videoWizStartcapture(devicdID, 176, 144, 10, iWndIndex);
        return (result == 0);
    }

    /**
     * This method is used to set video parameters
     * 设置视频参数
     * @param nDeviceID the n device id
     * @param rotate the rotate
     * @return the boolean
     */
    private boolean videoWizSetCaptureParam(int nDeviceID, int rotate)
    {
        int result = mConfPrew.videoWizSetCaptureParam(nDeviceID, 640, 480, 15, rotate);
        return (result == 0);
    }

    /**
     * This method is used to close the local Preview or wizard and destroy the preview window
     * 关闭本地预览或向导，销毁预览窗口
     * @param devicdID the devicd id
     * @return the boolean
     */
    private boolean videoWizCloseCapture(int devicdID)
    {
        int result = mConfPrew.videoWizCloseCapture(devicdID);
        return (result == 0);
    }

    /**
     * This method is used to close video
     * 关闭视频
     */
    public void leaveVideo()
    {
        long openedCameraID = getOpenedCameraID();
        int userID =  Integer.valueOf(this.meetingInstance.getSelf().getNumber());
        if (openedCameraID > 0)
        {
            detachLocalVideo(userID, openedCameraID);
            videoClose(openedCameraID, true);
        }
        getVideoManager().clearData();
    }

    /**
     * This method is used to open local video
     * @return
     */
    public boolean openLocalVideo()
    {
        long deviceID = getFirstClosedCameraID();
        if (deviceID > 0)
        {
            return videoOpen(deviceID);
        }
        return false;
    }

    /**
     * This method is used to close local video
     * @return
     */
    public boolean closeLocalVideo()
    {
        long openedCameraID = getOpenedCameraID();
        if (openedCameraID > 0)
        {
            return videoClose(openedCameraID, false);
        }
        return false;
    }

    /**
     * This method is used to camera switch
     * 摄像头切换
     * @return
     */
    public boolean switchCamera()
    {
        long deviceID = getFirstClosedCameraID();
        if (deviceID > 0)
        {
            long openedCameraID = getOpenedCameraID();
            if (openedCameraID > 0)
            {
                videoClose(openedCameraID, false);
            }
            return videoOpen(deviceID);
        }
        else
        {
            Log.i(TAG,  "can't switch camera: device is null ");
            return false;
        }
    }

    private long getFirstClosedCameraID()
    {
        List<CameraEntity> cameraEntityList = this.meetingInstance.getSelf().getCameraEntityList();
        if (cameraEntityList != null && !cameraEntityList.isEmpty())
        {
            for (CameraEntity cameraEntity : cameraEntityList)
            {
                if (cameraEntity.getCameraStatus() == CameraEntity.CAMERA_STATUS_CLOSED)
                {
                    return cameraEntity.getDeviceID();
                }
            }
        }
        return 0;
    }

    /**
     * This method is used to get open camera ID
     * 获取已打开摄像头id
     * @return
     */
    private long getOpenedCameraID()
    {
        List<CameraEntity> cameraEntityList = this.meetingInstance.getSelf().getCameraEntityList();
        if (cameraEntityList != null && !cameraEntityList.isEmpty())
        {
            for (CameraEntity cameraEntity : cameraEntityList)
            {
                if (cameraEntity.getCameraStatus() == CameraEntity.CAMERA_STATUS_OPENED)
                {
                    return cameraEntity.getDeviceID();
                }
            }
        }
        return 0;
    }

    /**
     * This method is used to join data conf
     * 加入数据会议
     * @param confInfo 会议信息
     * @return
     */
    private int joinDataConf(ConfInfo confInfo) {
        int result;

        // 初始化SDK
        TupMgr.getInstance().initDataConfSDK();

        // 设置会议服务器地址
        result = mConfIns.setServerIpList(confInfo.getSvrIp());
        if (result != 0) {
            Log.e(TAG, "setServerIpList failed, return -> " + result);
            //return result;
        }

        // 创建会议对象
        boolean ret = mConfIns.confNew(confInfo);
        if (ret != true) {
            Log.e(TAG, "confNew failed, return -> " + ret);
            return -1;
        }

        // 加入会议
        result = mConfIns.confJoin();
        if (result != 0) {
            Log.e(TAG, "confJoin failed, return -> " + result);
            return result;
        }

        isRelease = false;

        return 0;
    }

    /**
     * This method is used to schedule heart beat
     * 开启心跳
     */
    private void scheduleHeartBeat() {
        heartBeatTimer = new Timer("ConferenceHeartBeat");
        heartBeatTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message m = new Message();
                m.what = 0;
                heartBeatHandler.sendMessage(m);
            }
        }, 200, 100);
    }

    private void heartBeat() {
        if (mConfIns == null)
        {
            return;
        }
        mConfIns.confHeartBeat();
    }

    /**
     * This method is used to load the specified component
     * 加载指定组件
     */
    private int loadComponent()
    {
        componentVal |= ConfDefines.IID_COMPONENT_VIDEO;
        int ret = mConfIns.confLoadComponent(componentVal);
        if (ret != 0) {
            Log.e(TAG, "confLoadComponent->" + ret);
            return ret;
        }
        Log.i(TAG, "confLoadComponent success");
        return 0;
    }

    /**
     * This method is used to get the number of video devices available
     * 获取可用的视频设备数量
     * @return video device num
     */
    private boolean getVideoDeviceNum()
    {
        int nRet = mConfIns.videoGetDeviceCount();
        return (nRet == 0);
    }

    /**
     * This method is used to get available Video Device details
     * 获取可用的视频设备具体信息
     * @return the boolean
     */
    private boolean getVideoDeviceInfo()
    {
        int result = mConfIns.videoGetDeviceInfo();
        return (result == 0);
    }

    private String getNumberFromUserInfo(byte[] info)
    {
        if (info == null)
        {
            return null;
        }

        String infoStr = new String(info, Charset.defaultCharset());
        String statusKey = "Status";
        String uriKey = "BindNum";
        String nameKey = "UserName";

        List<String> key = new ArrayList<>();
        key.add(statusKey);
        key.add(uriKey);
        key.add(nameKey);

        ContentValues values = com.huawei.opensdk.commonservice.util.Xml.parseStringInXml(infoStr, key);

        if (values == null)
        {
            return null;
        }

        if (values.containsKey(uriKey))
        {
            if (!TextUtils.isEmpty((String) values.get(uriKey)))
            {
                return ((String) values.get(uriKey));
            }
        }
        return null;
    }

    /**
     * This method is used to go to the meeting success
     * 进入会议成功
     * @param msg
     * @param extendMsg 携带新加入的与会者信息
     */
    private void onUserEnter(ConfMsg msg, ConfExtendUserInfoMsg extendMsg)
    {
        String enterUserId = extendMsg.getUserid() + "";
        String enterUserName = extendMsg.getUserName();
        int userRole = extendMsg.getUserRole();

        Log.i(TAG, "user enter:enterUserId->" + enterUserId + "|enterUserName->" + enterUserName + "|userRole->" + userRole);

        String userNumber = getNumberFromUserInfo(extendMsg.getUserInfo());
        if (TextUtils.isEmpty(userNumber))
        {
            userNumber = extendMsg.getUserUri();
        }

        //SMC下不能通过号码判断
//        Member member = meetingInstance.getMemberByNumber(userNumber);
        Member member = meetingInstance.checkSelfInDataConf(userNumber);
        if (member == null)
        {
            return;
        }

        member.setInDataConference(true);
        member.setHost(((userRole & ConfDefines.CONF_ROLE_HOST) == ConfDefines.CONF_ROLE_HOST));
        member.setPresent(((userRole & ConfDefines.CONF_ROLE_PRESENTER) == ConfDefines.CONF_ROLE_PRESENTER));
        member.setDataUserId(extendMsg.getUserid());

        confNotification.onConfEventNotify(ConfConstant.CONF_EVENT.STATE_UPDATE, confID);
    }

    /**
     * This method is used to refreshing the presenter role information
     * 刷新主讲人角色信息
     * @param oldSpeaker 旧发言者
     * @param newSpeaker 新发言者
     */
    private void onPresenterChange(long oldSpeaker, long newSpeaker)
    {
        Member oldPresenter = meetingInstance.getMemberByDataUserId(oldSpeaker);
        if (oldPresenter != null) {
            oldPresenter.setPresent(false);
        }

        Member newPresenter = meetingInstance.getMemberByDataUserId(newSpeaker);
        if (newPresenter != null) {
            newPresenter.setPresent(true);
        }

        confNotification.onConfEventNotify(ConfConstant.CONF_EVENT.STATE_UPDATE, confID);
    }

    /**
     * This method is used to refreshing the moderator role information
     * 刷新主持人角色信息
     * @param oldHost 旧主席
     * @param newHost 新主席
     */
    private void onHostChange(long oldHost, long newHost)
    {
        Member oldHostMember = meetingInstance.getMemberByDataUserId(oldHost);
        if (oldHostMember != null) {
            oldHostMember.setHost(false);
        }

        Member newHostMember = meetingInstance.getMemberByDataUserId(newHost);
        if (newHostMember != null) {
            newHostMember.setHost(true);
        }

        confNotification.onConfEventNotify(ConfConstant.CONF_EVENT.STATE_UPDATE, confID);
    }

    private void onUserLeave(ConfExtendUserInfoMsg extendMsg)
    {
        Log.i(TAG, "onUserLeave");

        Member member = meetingInstance.getMemberByDataUserId(extendMsg.getUserid());
        if (member == null)
        {
            return;
        }
        member.setInDataConference(false);

        confNotification.onConfEventNotify(ConfConstant.CONF_EVENT.STATE_UPDATE, confID);
    }

    /**
     * This method is used to destroy a Meeting object
     * 销毁一个会议对象
     */
    private void onConfLeave()
    {
        Log.i(TAG, "onConfLeave");

        mConfIns.confRelease();
        stopWorkThreadAndBeatTimer();

        confNotification.onConfEventNotify(ConfConstant.CONF_EVENT.JOIN_DATA_CONF_LEAVE, confID);
    }

    private void onConfTerminate()
    {
        Log.i(TAG, "onConfTerminate");

        //mConfIns.confRelease();
        isRelease = true;
        stopWorkThreadAndBeatTimer();

        confNotification.onConfEventNotify(ConfConstant.CONF_EVENT.JOIN_DATA_CONF_TERMINATE, confID);
    }

    /**
     * This method is used to load component
     * 加载组件
     * @param value2
     */
    private void onLoadComponent(int value2)
    {
        String bmpPath = DATA_CONF_RES_PATH + File.separator;
        switch (value2)
        {
            case ConfDefines.IID_COMPONENT_VIDEO://视频
                Log.i(TAG, "loadComponent video");
                mConfIns.videoSetEncodeMaxResolution(640, 480);
                //获取本地视频设备和能力
                getVideoDeviceNum();
                getVideoDeviceInfo();
                break;
            case ConfDefines.IID_COMPONENT_DS://文档共享
                Log.i(TAG, "loadComponent DS");
                mConfIns.annotRegCustomerType(ConfDefines.IID_COMPONENT_DS);
                mConfIns.annotInitResource(bmpPath, ConfDefines.IID_COMPONENT_DS);
                break;
            case ConfDefines.IID_COMPONENT_AS://屏幕共享
                Log.i(TAG, "loadComponent AS");
                mConfIns.annotRegCustomerType(ConfDefines.IID_COMPONENT_AS);
                mConfIns.annotInitResource(bmpPath, ConfDefines.IID_COMPONENT_AS);
                break;
            case ConfDefines.IID_COMPONENT_WB://白板
                Log.i(TAG, "loadComponent WB");
                mConfIns.annotRegCustomerType(ConfDefines.IID_COMPONENT_WB);
                mConfIns.annotInitResource(bmpPath, ConfDefines.IID_COMPONENT_WB);
                break;
            default:
                break;
        }
    }

    /**
     * This method is used to get device information
     * 获取设备信息
     * @param extendMsg
     */
    private void onGetDeviceInfo(ConfExtendVideoDeviceInfoMsg extendMsg)
    {
        Member member = meetingInstance.getMemberByDataUserId(extendMsg.getUserId());
        if (member == null) {
            return;
        }

        CameraEntity cameraEntity = new CameraEntity();
        cameraEntity.setDeviceID(extendMsg.getDeviceId());
        cameraEntity.setDeviceName(extendMsg.getDeviceName());
        cameraEntity.setIndex(extendMsg.getDeviceIndex());
        cameraEntity.setUserID(extendMsg.getUserId());
        cameraEntity.setDeviceStatus(extendMsg.getDeviceStatus());

        member.replaceCamera(cameraEntity);

    }

    /**
     * This method is used to update camera info
     * 更新摄像头信息
     * @param msg
     * @param extendMsg
     */
    private void updateCameraInfo(ConfMsg msg, ConfExtendVideoParamMsg extendMsg)
    {
        int status = msg.getnValue1();
        long userID = msg.getnValue2();
        long deviceID = extendMsg.getDeviceId();

        Member member = meetingInstance.getMemberByDataUserId(userID);
        if (member == null) {
            return;
        }

        member.updateCamera(deviceID, status);
    }

    private void onChatMessage(ConfExtendChatMsg extendMsg)
    {

    }

    /**
     * This method is used to stop data conferencing worker thread heartbeat
     * 停止数据会议工作线程心跳
     */
    public void stopWorkThreadAndBeatTimer()
    {
        surfaceView = null;
        if (heartBeatTimer != null)
        {
            heartBeatTimer.cancel();
        }
        if (dataConfWorkThread != null)
        {
            heartBeatHandler.getLooper().quit();
            dataConfWorkThread.interrupt();
            dataConfWorkThread = null;
        }
    }

    /**
     * This method is used to start a data conferencing worker thread
     * 启动数据会议工作线程
     */
    private class WorkThread extends Thread {
        private ConfInfo confInfo;

        public WorkThread(ConfInfo confInfo) {
            this.confInfo = confInfo;
        }

        public void run() {
            Looper.prepare();
            // 加入数据会议
            joinDataConf(confInfo);

            // 启动心跳
            scheduleHeartBeat();
            heartBeatHandler = new Handler() {
                public void handleMessage(Message msg) {

                    heartBeat();
                }
            };

            Looper.loop();

            if(isRelease)
            {
                mConfIns.confRelease();
                TupMgr.getInstance().uninitDataConfSDK();
            }
        }
    }

    @Override
    public void confMsgNotify(ConfMsg msg, ConfExtendMsg extendMsg) {
        Log.i(TAG, "msgType->" + msg.getMsgType() + " , nValue1->" + msg.getnValue1()
                + " , nValue2->" + msg.getnValue2());

        switch (msg.getMsgType())
        {
            //会议状态、与会者状态
            case ConfMsg.CONF_MSG_ON_CONFERENCE_JOIN:
            case ConfMsg.CONF_MSG_USER_ON_ENTER_IND:
            case ConfMsg.CONF_MSG_USER_ON_LEAVE_IND:
            case ConfMsg.CONF_MSG_ON_COMPONENT_LOAD:
            case ConfMsg.CONF_MSG_ON_CONFERENCE_TERMINATE:
            case ConfMsg.CONF_MSG_ON_CONFERENCE_LEAVE:
            case ConfMsg.CONF_MSG_ON_DISCONNECT:
            case ConfMsg.CONF_MSG_ON_RECONNECT:
            case ConfMsg.CONF_MSG_USER_ON_PRESENTER_CHANGE_IND:
            case ConfMsg.CONF_MSG_USER_ON_HOST_CHANGE_IND:
            case ConfMsg.CONF_MSG_USER_ON_HOST_GIVE_CFM:
            case ConfMsg.CONF_MSG_USER_ON_HOST_CHANGE_CFM:
            case ConfMsg.CONF_MSG_USER_ON_PRESENTER_CHANGE_CFM:
            case ConfMsg.CONF_MSG_USER_ON_HOST_GIVE_IND:
            case ConfMsg.CONF_MSG_USER_ON_PRESENTER_GIVE_IND:
            case ConfMsg.CONF_MSG_USER_ON_PRESENTER_GIVE_CFM:
                handleConfStatusMsgNotify(msg, extendMsg);
                break;

            //桌面共享
            case ConfMsg.COMPT_MSG_AS_ON_SCREEN_DATA:
            case ConfMsg.COMPT_MSG_AS_ON_SHARING_STATE:
            case ConfMsg.COMPT_MSG_AS_ON_SHARING_SESSION:
            case ConfMsg.COMPT_MSG_AS_ON_SCREEN_SIZE:
                handleAsMsgNotify(msg);
                break;

            //文档共享
            case ConfMsg.COMPT_MSG_DS_ON_DOC_NEW:
            case ConfMsg.COMPT_MSG_DS_ON_DOC_DEL:
            case ConfMsg.COMPT_MSG_DS_ON_PAGE_NEW:
            case ConfMsg.COMPT_MSG_DS_ON_PAGE_DEL:
            case ConfMsg.COMPT_MSG_DS_ON_CURRENT_PAGE:
            case ConfMsg.COMPT_MSG_DS_ON_CURRENT_PAGE_IND:
            case ConfMsg.COMPT_MSG_DS_ANDROID_DOC_COUNT:
            case ConfMsg.COMPT_MSG_DS_ON_DRAW_DATA_NOTIFY:
            case ConfMsg.COMPT_MSG_DS_PAGE_DATA_DOWNLOAD:
                handleDsMsgNotify(msg, extendMsg);
                break;

            //白板共享
            case ConfMsg.COMPT_MSG_WB_ON_DOC_NEW:
            case ConfMsg.COMPT_MSG_WB_ON_DOC_DEL:
            case ConfMsg.COMPT_MSG_WB_XML_ON_NEW_DOC:
            case ConfMsg.COMPT_MSG_WB_ON_PAGE_NEW:
            case ConfMsg.COMPT_MSG_WB_ON_PAGE_DEL:
            case ConfMsg.COMPT_MSG_WB_ON_CURRENT_PAGE:
            case ConfMsg.COMPT_MSG_WB_ON_CURRENT_PAGE_IND:
            case ConfMsg.COMPT_MSG_WB_ON_DRAW_DATA_NOTIFY:
                handleWbMsgNotify(msg, extendMsg);
                break;

            // 视频
            case ConfMsg.COMPT_MSG_VIDEO_ON_GETDEVICE_NUM:
            case ConfMsg.COMPT_MSG_VIDEO_ON_GETDEVICE_INFO:
            case ConfMsg.COMPT_MSG_VIDEO_ON_DEVICE_INFO:
            case ConfMsg.COMPT_MSG_VIDEO_ON_SWITCH:
            case ConfMsg.COMPT_MSG_VIDEO_ON_MAX_OPENVIDEO:
            case ConfMsg.COMPT_MSG_VIDEO_ON_NOTIFY:
                handleVideoMsgNotify(msg, extendMsg);
                break;

            // 聊天
            case ConfMsg.COMPT_MSG_CHAT_ON_RECV_MSG:
                handleChatMsgNotify(msg, extendMsg);
                break;

            default:
                return;
        }

        return;
    }


    private void handleConfStatusMsgNotify(ConfMsg msg, ConfExtendMsg extendMsg)
    {
        int nValue1 = msg.getnValue1();
        long nValue2 = msg.getnValue2();

        switch (msg.getMsgType())
        {
            //加入会议结果
            case ConfMsg.CONF_MSG_ON_CONFERENCE_JOIN:
                if (nValue1 == ConfResult.TC_OK) {
//                    nValue1 = loadComponent();
                }
                confNotification.onConfEventNotify(ConfConstant.CONF_EVENT.JOIN_DATA_CONF_RESULT, nValue1);
                break;

            //加入会议成功
            case ConfMsg.CONF_MSG_USER_ON_ENTER_IND:
                onUserEnter(msg, (ConfExtendUserInfoMsg) extendMsg);
                break;

            //向其他与会者通知用户离开会议
            case ConfMsg.CONF_MSG_USER_ON_LEAVE_IND:
                onUserLeave((ConfExtendUserInfoMsg) extendMsg);
                break;

            //组件加载成功
            case ConfMsg.CONF_MSG_ON_COMPONENT_LOAD:
                onLoadComponent((int) nValue2);
                break;

            // 通知会议结束
            case ConfMsg.CONF_MSG_ON_CONFERENCE_TERMINATE:
                onConfTerminate();
                break;

            //被踢除侧离开通知消息
            case ConfMsg.CONF_MSG_ON_CONFERENCE_LEAVE:
                onConfLeave();
                break;

            case ConfMsg.CONF_MSG_ON_DISCONNECT:
            case ConfMsg.CONF_MSG_ON_RECONNECT:
                Log.i(TAG, "data conf disconnect or reconnect");
                break;

            //会议主持人变更消息
            case ConfMsg.CONF_MSG_USER_ON_HOST_CHANGE_IND:
            case ConfMsg.CONF_MSG_USER_ON_HOST_GIVE_CFM:
            case ConfMsg.CONF_MSG_USER_ON_HOST_CHANGE_CFM:
            case ConfMsg.CONF_MSG_USER_ON_HOST_GIVE_IND:
                onHostChange(nValue1, nValue2);
                break;

            //会议主讲人变更消息
            case ConfMsg.CONF_MSG_USER_ON_PRESENTER_CHANGE_IND:
            case ConfMsg.CONF_MSG_USER_ON_PRESENTER_CHANGE_CFM:
            case ConfMsg.CONF_MSG_USER_ON_PRESENTER_GIVE_IND:
            case ConfMsg.CONF_MSG_USER_ON_PRESENTER_GIVE_CFM:
                onPresenterChange(nValue1, nValue2);
                break;

            default:
                return;
        }
        return;
    }

    private void handleAsMsgNotify(ConfMsg msg)
    {
        switch (msg.getMsgType())
        {
            case ConfMsg.COMPT_MSG_AS_ON_SCREEN_DATA:
                Log.i(TAG, "AS on screen data");
                break;
            case ConfMsg.COMPT_MSG_AS_ON_SHARING_STATE:
                Log.i(TAG, "AS on sharing state");
                break;
            case ConfMsg.COMPT_MSG_AS_ON_SHARING_SESSION:
                Log.i(TAG, "AS on sharing session");
                break;
            case ConfMsg.COMPT_MSG_AS_ON_SCREEN_SIZE:
                Log.i(TAG, "AS on screen size");
                break;
            default:
                return;
        }

        if (surfaceView == null)
        {
            return;
        }
        currentShareType = ConfDefines.IID_COMPONENT_AS;
        surfaceView.setViewType(ConfDefines.IID_COMPONENT_AS);
        resizeSharedView();
        updateSharedView();
    }

    private void handleDsMsgNotify(ConfMsg msg, ConfExtendMsg extendMsg)
    {
        switch (msg.getMsgType())
        {
            case ConfMsg.COMPT_MSG_DS_ON_DOC_NEW:
                Log.i(TAG,  "msg --> COMPT_MSG_DS_ON_DOC_NEW");
                mConfIns.dsSetCurrentPage(msg.getnValue1(), msg.getnValue2());
                resizeSharedView();
                updateSharedView();
                break;

            case ConfMsg.COMPT_MSG_DS_ON_DOC_DEL:
                break;
            case ConfMsg.COMPT_MSG_DS_ON_PAGE_NEW:
                break;
            case ConfMsg.COMPT_MSG_DS_ON_PAGE_DEL:
                break;
            case ConfMsg.COMPT_MSG_DS_ON_CURRENT_PAGE:
                break;
            case ConfMsg.COMPT_MSG_DS_ON_CURRENT_PAGE_IND:
                Log.i(TAG,  "msg --> COMPT_MSG_DS_ON_CURRENT_PAGE_IND");
                mConfIns.dsSetCurrentPage(msg.getnValue1(), msg.getnValue2());
                break;

            case ConfMsg.COMPT_MSG_DS_ANDROID_DOC_COUNT:
            case ConfMsg.COMPT_MSG_DS_ON_DRAW_DATA_NOTIFY:
                updateSharedView();
                break;
            case ConfMsg.COMPT_MSG_DS_PAGE_DATA_DOWNLOAD:
                break;
            default:
                return;
        }
        if (surfaceView == null)
        {
            return;
        }
        currentShareType = ConfDefines.IID_COMPONENT_DS;
        surfaceView.setViewType(ConfDefines.IID_COMPONENT_DS);
        return;
    }

    private void handleWbMsgNotify(ConfMsg msg, ConfExtendMsg extendMsg)
    {
        if (surfaceView == null)
        {
            return;
        }

        switch (msg.getMsgType())
        {
            //新建一个白板文档
            case ConfMsg.COMPT_MSG_WB_ON_DOC_NEW:
                Log.i(TAG,  "msg --> COMPT_MSG_WB_ON_DOC_NEW");
                mConfIns.wbSetCurrentPage(msg.getnValue1(), (int) msg.getnValue2());
                surfaceView.setViewType(ConfDefines.IID_COMPONENT_WB);
                resizeSharedView();
                updateSharedView();
                break;

            //共享白板关闭
            case ConfMsg.COMPT_MSG_WB_ON_DOC_DEL:
                break;

            case ConfMsg.COMPT_MSG_WB_XML_ON_NEW_DOC:
                break;

            case ConfMsg.COMPT_MSG_WB_ON_PAGE_NEW:
                break;

            case ConfMsg.COMPT_MSG_WB_ON_PAGE_DEL:
                break;

            //当前文档或当前页发生变化
            case ConfMsg.COMPT_MSG_WB_ON_CURRENT_PAGE:
                break;

            //翻页前预先通知
            case ConfMsg.COMPT_MSG_WB_ON_CURRENT_PAGE_IND:
                Log.i(TAG,  "msg --> COMPT_MSG_WB_ON_CURRENT_PAGE_IND");
                mConfIns.wbSetCurrentPage(msg.getnValue1(), (int) msg.getnValue2());
                break;

            //白板界面数据更新
            case ConfMsg.COMPT_MSG_WB_ON_DRAW_DATA_NOTIFY:
                updateSharedView();
                break;

            default:
                return;
        }

        currentShareType = ConfDefines.IID_COMPONENT_WB;
        return;
    }

    private void handleVideoMsgNotify(ConfMsg msg, ConfExtendMsg extendMsg)
    {
        switch (msg.getMsgType())
        {
            //获取视频数量
            case ConfMsg.COMPT_MSG_VIDEO_ON_GETDEVICE_NUM:
                Log.i(TAG, "video --> on get device number:" + msg.getnValue1() + "|" + msg.getnValue2());
                break;

            //获取视频设备信息
            case ConfMsg.COMPT_MSG_VIDEO_ON_GETDEVICE_INFO:
                Log.i(TAG, "video --> on get device info:"+ msg.getnValue1() + "|" + msg.getnValue2());
                onGetDeviceInfo((ConfExtendVideoDeviceInfoMsg)extendMsg);
                break;

            // 设备添加或是删除:(包括自己和别人)
            case ConfMsg.COMPT_MSG_VIDEO_ON_DEVICE_INFO:
                Log.i(TAG, "video ------ add/remove device:"+ msg.getnValue1() + "|" + msg.getnValue2());
                onGetDeviceInfo((ConfExtendVideoDeviceInfoMsg)extendMsg);
                break;

            // 视频状态相关: 0:关闭  1:打开  2.Resume 4.Pause
            case ConfMsg.COMPT_MSG_VIDEO_ON_SWITCH:
                updateCameraInfo(msg, (ConfExtendVideoParamMsg)extendMsg);

                ConfExtendVideoParamMsg fromMsg = (ConfExtendVideoParamMsg) extendMsg;
                VideoSwitchInfoWapper videoSwitchInfoWapper = new VideoSwitchInfoWapper(msg.getnValue1(), fromMsg.getDeviceId(), msg.getnValue2());
                confNotification.onConfEventNotify(ConfConstant.CONF_EVENT.CAMERA_STATUS_UPDATE, videoSwitchInfoWapper);

                break;

            // 最大打开视频数
            case ConfMsg.COMPT_MSG_VIDEO_ON_MAX_OPENVIDEO:
                Log.i(TAG, "video ------ max open video:" + msg.getnValue1() + "|" + msg.getnValue2());
                break;

            //被邀请侧邀请打开/关闭视频的通知消息
            case ConfMsg.COMPT_MSG_VIDEO_ON_NOTIFY:
                Log.i(TAG, "video ------ video notify:" + msg.getnValue1() + "|" + msg.getnValue2());
                break;

            default:
                return;
        }
        return;
    }

    private void handleChatMsgNotify(ConfMsg msg, ConfExtendMsg extendMsg)
    {
        switch (msg.getMsgType())
        {
            case ConfMsg.COMPT_MSG_CHAT_ON_RECV_MSG:
                Log.i(TAG, "chat on receive message");
                onChatMessage((ConfExtendChatMsg) extendMsg);
                break;

            default:
                return;
        }
        return;
    }

    public VideoManager getVideoManager()
    {
        if (videoManager == null)
        {
            videoManager = new VideoManager();
        }
        return videoManager;
    }

    public class VideoManager
    {

        private int ori = 0; //Screen direction
        /**
         * loading local video ViewGroup
         **/
        private ViewGroup mLocalContainer;

        /**
         * local video SurfaceView
         **/
        private SurfaceView svLocalSurfaceView;
        private SurfaceView localSurfaceView;
        /**
         * loading remote video ViewGroup
         **/
        private ViewGroup mRemoteContainer;
        /**
         * remote video SurfaceView
         **/
        private SurfaceView remoteSurfaceView;


        public void setVideoContainer(Context context, ViewGroup localView,
                                      ViewGroup remoteView)
        {
            if (ori != 1)
            {
                Configuration cf = context.getResources().getConfiguration();
                ori = cf.orientation;
            }
            if (null == mLocalContainer)
            {
                mLocalContainer = localView;
            }
            if (null == svLocalSurfaceView)
            {
                svLocalSurfaceView = ViERenderer.createLocalRenderer(context);
                svLocalSurfaceView.setZOrderMediaOverlay(true);
                svLocalSurfaceView.setVisibility(View.VISIBLE);
            }
            /*if (null == localSurfaceView)
            {
                localSurfaceView = ViERenderer.createRenderer(context, true);
                localSurfaceView.setVisibility(View.VISIBLE);
            }*/

            if (null == mRemoteContainer)
            {
                mRemoteContainer = remoteView;
            }

            if (null == remoteSurfaceView)
            {
                remoteSurfaceView = ViERenderer.createRenderer(context, true);
                remoteSurfaceView.setZOrderMediaOverlay(false);
                remoteSurfaceView.setVisibility(View.VISIBLE);
            }
            mRemoteContainer.removeView(remoteSurfaceView);
            mRemoteContainer.addView(remoteSurfaceView);
            mLocalContainer.removeView(svLocalSurfaceView);
            mLocalContainer.addView(svLocalSurfaceView);
        }

        public void addViewToContainer()
        {
            mRemoteContainer.removeView(remoteSurfaceView);
            mRemoteContainer.addView(remoteSurfaceView);
            mLocalContainer.removeView(svLocalSurfaceView);
            mLocalContainer.addView(svLocalSurfaceView);
        }

        public int getRemoteIndexOfSurface()
        {
            if (remoteSurfaceView != null)
            {
                return ViERenderer.getIndexOfSurface(remoteSurfaceView);
            }
            return -1;
        }

        public int getLocalIndexOfSurface()
        {
            if (svLocalSurfaceView != null)
            {
                return ViERenderer.getIndexOfSurface(svLocalSurfaceView);
            }
            return -1;
        }

        public void clearData()
        {
            svLocalSurfaceView = null;
            remoteSurfaceView = null;
            mLocalContainer = null;
            mRemoteContainer = null;
        }
    }

}
