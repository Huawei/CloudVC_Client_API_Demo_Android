package com.huawei.opensdk.demoservice;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import com.huawei.opensdk.commonservice.util.DeviceManager;
import com.huawei.opensdk.sdkwrapper.login.LoginCenter;
import com.huawei.opensdk.sdkwrapper.manager.TupMgr;
import com.huawei.tup.confctrl.CCAddAttendeeInfo;
import com.huawei.tup.confctrl.CCIPAddr;
import com.huawei.tup.confctrl.ConfctrlAttendeeMediax;
import com.huawei.tup.confctrl.ConfctrlConfMode;
import com.huawei.tup.confctrl.ConfctrlConfType;
import com.huawei.tup.confctrl.ConfctrlConfWarningTone;
import com.huawei.tup.confctrl.ConfctrlEncryptMode;
import com.huawei.tup.confctrl.ConfctrlIPVersion;
import com.huawei.tup.confctrl.ConfctrlLanguage;
import com.huawei.tup.confctrl.ConfctrlReminderType;
import com.huawei.tup.confctrl.ConfctrlSiteCallTerminalType;
import com.huawei.tup.confctrl.ConfctrlTimezone;
import com.huawei.tup.confctrl.ConfctrlUserType;
import com.huawei.tup.confctrl.ConfctrlVideoProtocol;
import com.huawei.tup.confctrl.sdk.TupConfAttendeeOptResult;
import com.huawei.tup.confctrl.sdk.TupConfBaseAttendeeInfo;
import com.huawei.tup.confctrl.sdk.TupConfBookVcHostedConfInfo;
import com.huawei.tup.confctrl.sdk.TupConfBookVcOnPremiseConfInfo;
import com.huawei.tup.confctrl.sdk.TupConfECAttendeeInfo;
import com.huawei.tup.confctrl.sdk.TupConfInfo;
import com.huawei.tup.confctrl.sdk.TupConfNotifyBase;
import com.huawei.tup.confctrl.sdk.TupConfOptResult;
import com.huawei.tup.confctrl.sdk.TupConfParam;
import com.huawei.tup.confctrl.sdk.TupConfSpeakerInfo;
import com.huawei.tup.confctrl.sdk.TupConfVCAttendeeInfo;
import com.huawei.tup.confctrl.sdk.TupConfctrlDataconfParams;
import com.huawei.tup.confctrl.sdk.TupConference;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is about meeting function management.
 * 会议服务管理类
 */
public class MeetingMgr extends TupConfNotifyBase implements IMeetingMgr{

    private static final String TAG = MeetingMgr.class.getSimpleName();

    private static MeetingMgr mInstance;

    /**
     * UI回调
     */
    private IConfNotification mConfNotification;

    /**
     * 预约会议状态
     */
    private ConfConstant.BookConfStatus currentBookConfStatus;

    /**
     * 当前正在召开的会议
     */
    private MeetingInstance currentConference;

    /**
     * 自己加入会议的号码
     */
    private String joinConfNumber;

    public String getDateConfId() {
        return dateConfId;
    }

    public void setDateConfId(String dateConfId) {
        this.dateConfId = dateConfId;
    }

    /**
     * 获取数据会议大参数的会议id
     */
    private String dateConfId;

    /**
     * EC组网媒体类型会通过回调返回，VC组网只能在这里保存下媒体类型
     */
    private ConfConstant.ConfMediaType vcMediaType = ConfConstant.ConfMediaType.VIDEO_CONF;

    public ConfConstant.ConfMediaType getVcMediaType() {
        return vcMediaType;
    }

    private MeetingMgr()
    {
    }

    public static MeetingMgr getInstance()
    {
        if (mInstance == null)
        {
            mInstance = new MeetingMgr();
        }
        return mInstance;
    }

    public void regConfServiceNotification(IConfNotification confNotification)
    {
        this.mConfNotification = confNotification;
    }


    public String getJoinConfNumber() {
        return joinConfNumber;
    }

    public void setJoinConfNumber(String joinConfNumber) {
        this.joinConfNumber = joinConfNumber;
    }


    public boolean isInConference() {
        if (null == currentConference)
        {
            return false;
        }
        return true;
    }

    public int getCurrentConferenceCallID() {
        if (null == currentConference)
        {
            return 0;
        }
        return currentConference.getCallID();
    }

    public void setCurrentConferenceCallID(int callID) {
        if (null == currentConference)
        {
            return;
        }
        currentConference.setCallID(callID);
    }

    public List<Member> getCurrentConferenceMemberList() {
        if (null == currentConference)
        {
            return null;
        }
        return currentConference.getMemberList();
    }

    public Member getCurrentConferenceSelf() {
        if (null == currentConference)
        {
            return null;
        }
        return currentConference.getSelf();
    }

    public ConfBaseInfo getCurrentConferenceBaseInfo() {
        if (null == currentConference)
        {
            return null;
        }
        return currentConference.getConfBaseInfo();
    }

    public void saveCurrentConferenceGetDataConfParamInfo(QueryJoinDataConfParamInfo info) {
        if (null == currentConference)
        {
            return;
        }

        //此模式为VC模式下，因accessConf时传入的为呼叫id，因此  用呼叫id判断
        if (getCurrentConferenceBaseInfo().getConfID().equals(info.getCallId()+""))
        {
            currentConference.setQueryJoinDataConfParamInfo(info);
        }
    }

    public void updateCurrentConferenceBaseInfo(ConfDetailInfo confDetailInfo) {
        if (null == currentConference)
        {
            return;
        }

        if (getCurrentConferenceBaseInfo().getConfID().equals(confDetailInfo.getConfID()))
        {
            currentConference.updateConfInfo(confDetailInfo);
        }
    }

    /**
     * This method is used to book instant conference or reserved conference
     * @param bookConferenceInfo 创会信息
     * @return
     */
    public int bookConference(BookConferenceInfo bookConferenceInfo)
    {
        Log.i(TAG, "bookConference.");
        int result = 0;
        if (bookConferenceInfo == null)
        {
            Log.e(TAG, "booKConferenceInfo obj is null");
            return -1;
        }

       this.vcMediaType = bookConferenceInfo.getMediaType();

        if (LoginCenter.getInstance().getServerType() == LoginCenter.getInstance().LOGIN_E_SERVER_TYPE_SMC)
        {
            List<CCAddAttendeeInfo> vcAttendeeList = ConfConvertUtil.smcMemberListToAttendeeList(bookConferenceInfo.getMemberList());


            String number = LoginCenter.getInstance().getAccount();
            TupConfBookVcOnPremiseConfInfo confInfo = new TupConfBookVcOnPremiseConfInfo();
            confInfo.setSitecallType(0);

            confInfo.setServerAddr(new CCIPAddr(LoginCenter.getInstance().getLoginServerAddress(), ConfctrlIPVersion.CC_IP_V4));
            confInfo.setLocalAddr(new CCIPAddr(DeviceManager.getLocalIp(), ConfctrlIPVersion.CC_IP_V4));
            CCAddAttendeeInfo attendeeInfo = new CCAddAttendeeInfo();
            CCIPAddr ccipAddr = new CCIPAddr();
            ccipAddr.setIp(DeviceManager.getLocalIp());
            ccipAddr.setIpVer(ConfctrlIPVersion.CC_IP_V4);
            attendeeInfo.setTerminalIpAddr(ccipAddr);
            attendeeInfo.setTerminalType(ConfctrlSiteCallTerminalType.CC_sip);
            attendeeInfo.setSiteBandwidth(1920);
            attendeeInfo.setNumber(number);
            attendeeInfo.setNumberLen(number.length());
            attendeeInfo.setTerminalId(number);
            attendeeInfo.setTerminalIdLength(number.length());
            attendeeInfo.setUri(number + "@" + LoginCenter.getInstance().getLoginServerAddress());
            confInfo.setCcAddterminalInfo(vcAttendeeList);

            confInfo.setSitenumber(vcAttendeeList.size());
            confInfo.setConfName(bookConferenceInfo.getSubject());
            confInfo.setConfNameLen(bookConferenceInfo.getSubject().length());
            confInfo.setPwdLen(bookConferenceInfo.getPassWord().length()); //max length is 6
            confInfo.setPucPwd(bookConferenceInfo.getPassWord());
            confInfo.setSitecallMode(0); //CC_SITE_CALL_MODE_REPORT
            if (bookConferenceInfo.getMediaType()==ConfConstant.ConfMediaType.VOICE_CONF)
            {
                confInfo.setTerminalDataRate(640);
                confInfo.setHasDataConf(3);
            }else if (bookConferenceInfo.getMediaType()==ConfConstant.ConfMediaType.VIDEO_CONF){
                confInfo.setTerminalDataRate(19200);
                confInfo.setHasDataConf(3);
            }else if (bookConferenceInfo.getMediaType()==ConfConstant.ConfMediaType.VOICE_AND_DATA_CONF){
                confInfo.setTerminalDataRate(640);
                confInfo.setHasDataConf(2);
            }else if (bookConferenceInfo.getMediaType()==ConfConstant.ConfMediaType.VIDEO_AND_DATA_CONF){
                confInfo.setTerminalDataRate(19200);
                confInfo.setHasDataConf(2);
            }
            confInfo.setLanguage("zh-CN");
            confInfo.setVideoProto(ConfctrlVideoProtocol.CC_VIDEO_PROTO_BUTT);

            //其他参数可选，使用默认值即可

            result = TupMgr.getInstance().getConfManagerIns().bookReservedConf(confInfo);
            if (result != 0)
            {
                Log.e(TAG, "bookReservedConf result ->" + result);
                return  result;
            }

        }
        else if (LoginCenter.getInstance().getServerType() == LoginCenter.getInstance().LOGIN_E_SERVER_TYPE_MEDIAX)
        {
            List<ConfctrlAttendeeMediax> attendeeList = ConfConvertUtil.mediaxMemberListToAttendeeList(bookConferenceInfo.getMemberList());
            TupConfBookVcHostedConfInfo hostedConfInfo = new TupConfBookVcHostedConfInfo();
            hostedConfInfo.setConfType(ConfctrlConfType.CONFCTRL_E_CONF_TYPE_NORMAL);
            hostedConfInfo.setSubject(bookConferenceInfo.getSubject());
            hostedConfInfo.setMediaType(ConfConvertUtil.convertConfMediaType(bookConferenceInfo.getMediaType()));
            hostedConfInfo.setAllowInvite(1);
            hostedConfInfo.setAutoInvite(1);
            hostedConfInfo.setAllowVideoControl(1);
            hostedConfInfo.setTimezone(ConfctrlTimezone.CONFCTRL_E_TIMEZONE_BEIJING);
            hostedConfInfo.setConfLen(2 * 60 * 60);
            hostedConfInfo.setWelcomeVoiceEnable(ConfctrlConfWarningTone.CONFCTRL_E_CONF_WARNING_TONE_DEFAULT);
            hostedConfInfo.setEnterPrompt(ConfctrlConfWarningTone.CONFCTRL_E_CONF_WARNING_TONE_DEFAULT);
            hostedConfInfo.setLeavePrompt(ConfctrlConfWarningTone.CONFCTRL_E_CONF_WARNING_TONE_DEFAULT);
            hostedConfInfo.setConfFilter(1);
            hostedConfInfo.setRecordFlag(0);
            hostedConfInfo.setAutoProlong(0);
            hostedConfInfo.setMultiStreamFlag(0);
            hostedConfInfo.setReminder(ConfctrlReminderType.CONFCTRL_E_REMINDER_TYPE_NONE);
            hostedConfInfo.setLanguage(ConfctrlLanguage.CONFCTRL_E_LANGUAGE_EN_US);
            hostedConfInfo.setConfEncryptMode(ConfctrlEncryptMode.CONFCTRL_E_ENCRYPT_MODE_NONE);
            hostedConfInfo.setUserType(ConfctrlUserType.CONFCTRL_E_USER_TYPE_MOBILE);
            hostedConfInfo.setAttendee(attendeeList);
            hostedConfInfo.setNumOfAttendee(attendeeList.size());
            if (3 > attendeeList.size())
            {
                hostedConfInfo.setSize(3);
            }
            else
            {
                hostedConfInfo.setSize(attendeeList.size());
            }
            hostedConfInfo.setStartTime(0);

            result = TupMgr.getInstance().getConfManagerIns().bookReservedConf(hostedConfInfo);
        }

        if (result != 0)
        {
            Log.e(TAG, "bookReservedConf result ->" + result);
        }

        return result;
    }

    /**
     * This method is used to join conference
     * 用于通过会议列表等方式主动入会议，入会后需要邀请自己的号码
     * @param confId              会议ID
     * @param password            会议接入密码
     * @return
     */
    public int joinConf(String confId, String password, ConfConstant.ConfRole role)
    {
        Log.i(TAG,  "join conf.");

        currentConference = new MeetingInstance();
        int result = currentConference.joinConf(confId, password, "", true);
        if (result != 0)
        {
            Log.e(TAG, "joinConf result ->" + result);
            currentConference = null;
            return result;
        }

        // 记录当前会议中自己的角色
        currentConference.setSelfRole(role);
        return 0;
    }

    /**
     * This method is used to join conference
     * 用于被邀请或IVR主动入会等方式加入时，入会后不需要邀请自己的号码
     * @param confId              会议ID
     * @param token               会议接入TOKEN
     * @return
     */
    public int joinConfByToken(String confId, String token, ConfConstant.ConfRole role)
    {
        Log.i(TAG,  "join conf.");

        currentConference = new MeetingInstance();
        int result = currentConference.joinConf(confId, "", token, false);
        if (result != 0)
        {
            Log.e(TAG, "joinConf result ->" + result);
            currentConference = null;
            return result;
        }

        // 记录当前会议中自己的角色
        currentConference.setSelfRole(role);
        return 0;
    }

    /**
     * This method is used to auto join conf
     *  自动加入会议
     *  创建立即会议成功后调用
     * @param confInfo
     * @return
     */
    private int autoJoinConf(TupConfInfo confInfo)
    {
        Log.i(TAG,  "auto join conf.");

        String confId = confInfo.getConfID();

        ConfConstant.ConfRole role = ConfConstant.ConfRole.CHAIRMAN;
        //自动入会时，有主席密码则用主席密码入会，无主席则用普通与会密码入会
        String password = confInfo.getChairmanPwd();
        if ((password == null) || (password.equals("")))
        {
            role = ConfConstant.ConfRole.ATTENDEE;
            password = confInfo.getGuestPwd();
        }

        //自动加入会议时，需要自动邀请自己
        return joinConf(confId, password, role);
    }

    /**
     * This method is used to add yourself
     * 添加自己
     * @return
     */
    private int addYourself()
    {
        Member attendeeInfo = new Member();

        attendeeInfo.setNumber(getJoinConfNumber());
        ConfConstant.ConfRole role = currentConference.getSelfRole();
        attendeeInfo.setRole(role);

        return currentConference.addAttendee(attendeeInfo);
    }

    /**
     * This method is used to leave conf
     * 离会
     * @return
     */
    public int leaveConf()
    {
        if (null == currentConference)
        {
            Log.i(TAG,  "leave conf, currentConference is null ");
            return 0;
        }

//        currentConference.removeAttendee(getCurrentConferenceSelf());

        int result = currentConference.leaveConf();
        if (result == 0) {
            currentConference = null;
        }

        return result;
    }

    /**
     * This method is used to end conf
     * 结束会议
     * @return
     */
    public int endConf()
    {
        if (null == currentConference)
        {
            Log.i(TAG,  "end conf, currentConference is null ");
            return 0;
        }

        int result =  currentConference.endConf();
        if (result == 0) {
            currentConference = null;
        }

        return result;
    }

    /**
     * This method is used to add attendee
     * 添加与会者
     * @param attendee 与会者信息
     * @return
     */
    public int addAttendee(Member attendee)
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "add attendee failed, currentConference is null ");
            return -1;
        }

        int result =  currentConference.addAttendee(attendee);

        //TODO
        return result;
    }

    /**
     * This method is used to remove attendee
     * 移除与会者
     * @param attendee 与会者信息
     * @return
     */
    public int removeAttendee(Member attendee)
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "remove attendee failed, currentConference is null ");
            return -1;
        }

        int result =  currentConference.removeAttendee(attendee);

        //TODO
        return result;
    }

    /**
     * This method is used to hang up attendee
     * 挂断与会者
     * @param attendee 与会者信息
     * @return
     */
    public int hangupAttendee(Member attendee)
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "hangup attendee failed, currentConference is null ");
            return -1;
        }

        int result =  currentConference.hangupAttendee(attendee);

        //TODO
        return result;
    }

    /**
     * This method is used to mute attendee
     * 静音与会者
     * @param attendee 与会者信息
     * @param isMute 是否静音
     * @return
     */
    public int muteAttendee(Member attendee, boolean isMute)
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "mute attendee failed, currentConference is null ");
            return -1;
        }

        int result = currentConference.muteAttendee(attendee, isMute);

        //TODO
        return result;
    }

    /**
     * This method is used to release chairman
     * 是否主席
     * @return
     */
    public int releaseChairman()
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "release chairman failed, currentConference is null ");
            return -1;
        }

        int result =  currentConference.releaseChairman();

        //TODO
        return result;
    }

    /**
     * This method is used to request chairman
     * 请求主席
     * @param chairmanPassword 主席密码
     * @return
     */
    public int requestChairman(String chairmanPassword)
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "request chairman failed, currentConference is null ");
            return -1;
        }

        int result =  currentConference.requestChairman(chairmanPassword);

        //TODO
        return result;
    }

    public int setPresenter(Member attendee)
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "set presenter failed, currentConference is null ");
            return -1;
        }

        int result =  currentConference.setPresenter(attendee);

        //TODO
        return result;
    }

    /**
     * This method is used to set host
     * 设置主席
     * @param attendee 与会者信息
     * @return
     */
    public int setHost(Member attendee)
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "set presenter failed, currentConference is null ");
            return -1;
        }

        int result =  currentConference.setHost(attendee);

        //TODO
        return result;
    }

    /**
     * This method is used to postpone conf
     * 延长会议
     * @param time 延长时长
     * @return
     */
    public int postpone(int time)
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "postpone conf failed, currentConference is null ");
            return -1;
        }

        int result =  currentConference.postpone(time);

        //TODO
        return result;
    }

    /**
     * This method is used to set conf mode
     * 设置会议类型
     * @param confctrlConfMode 会议类型
     * @return
     */
    public int setConfMode(ConfctrlConfMode confctrlConfMode)
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "set conf mode failed, currentConference is null ");
            return -1;
        }

        int result =  currentConference.setConfMode(confctrlConfMode);

        //TODO
        return result;
    }

    /**
     * This method is used to broadcast attendee
     * 广播与会者
     * @param attendee 与会者信息
     * @param isBroadcast 是否广播
     * @return
     */
    public int broadcastAttendee(Member attendee, boolean isBroadcast)
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "broadcast attendee failed, currentConference is null ");
            return -1;
        }

        int result = currentConference.broadcastAttendee(attendee, isBroadcast);

        if (0 != result)
        {
            Log.e(TAG,  "broadcast attendee failed.");
        }
        return result;
    }

    /**
     * This method is used to watch attendee
     * 观看与会者
     * @param attendee 与会者信息
     * @return
     */
    public int watchAttendee(Member attendee)
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "broadcast attendee failed, currentConference is null ");
            return -1;
        }

        int result =  currentConference.watchAttendee(attendee);

        if (0 != result)
        {
            Log.e(TAG,  "watch attendee failed.");
        }
        return result;
    }

    /**
     * This method is used to upgrade conf
     * 会议升级
     * @return
     */
    public int upgradeConf()
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "upgrade conf failed, currentConference is null ");
            return -1;
        }

        int result =  currentConference.upgradeConf();

        //TODO
        return result;
    }


    /**
     * This method is used to join data conf
     * 加入数据会议
     * @return
     */
    public int joinDataConf()
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "join data conf failed, currentConference is null ");
            return -1;
        }

        int result =  currentConference.joinDataConf(mConfNotification);

        //TODO
        return result;
    }

    /**
     * 升级为数据会议前检查
     */
    public void checkUpgradeDataConf()
    {
        if (null == currentConference)
        {
            return;
        }

        currentConference.checkUpgradeDataConf();
    }

    public boolean judgeInviteFormMySelf(String confID)
    {
        if ((confID == null) || (confID.equals("")))
        {
            return false;
        }

        //TODO
        if (currentConference != null && getCurrentConferenceBaseInfo().getConfID().equals(confID))
        {
            return true;
        }

        return false;
    }

    private Member getMemberByNumber(String number)
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "upgrade conf failed, currentConference is null ");
            return null;
        }
        return currentConference.getMemberByNumber(number);
    }

    public void attachSurfaceView(ViewGroup container, Context context)
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "attach surface view failed, currentConference is null ");
            return;
        }
        currentConference.attachSurfaceView(container, context);
    }

    public boolean switchCamera()
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "switch camera failed, currentConference is null ");
            return false;
        }
        return currentConference.switchCamera();
    }

    public boolean openLocalVideo()
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "open local video failed, currentConference is null ");
            return false;
        }
        return currentConference.openLocalVideo();
    }

    public boolean closeLocalVideo()
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "close local video failed, currentConference is null ");
            return false;
        }
        return currentConference.closeLocalVideo();
    }

    public void setVideoContainer(Context context, ViewGroup localView, ViewGroup remoteView)
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "set video container failed, currentConference is null ");
            return;
        }
        currentConference.setVideoContainer(context, localView, remoteView);
    }

    public boolean attachRemoteVideo(long userID, long deviceID)
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "attach remote video failed, currentConference is null ");
            return false;
        }
        return currentConference.attachRemoteVideo(userID, deviceID);
    }

    public boolean detachRemoteVideo(long userID, long deviceID)
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "detach remote video failed, currentConference is null ");
            return false;
        }
        return currentConference.detachRemoteVideo(userID, deviceID);
    }

    public boolean videoOpen(long deviceID)
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "open video failed, currentConference is null ");
            return false;
        }
        return currentConference.videoOpen(deviceID);
    }

    public void leaveVideo()
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "leave video failed, currentConference is null ");
            return;
        }
        currentConference.leaveVideo();
    }

    public void changeLocalVideoVisible(boolean visible)
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "change local video visible failed, currentConference is null ");
            return;
        }
        currentConference.changeLocalVideoVisible(visible);
    }

    public boolean attachVideo(long userID, long deviceID)
    {
        if (null == currentConference)
        {
            Log.e(TAG,  "attach video failed, currentConference is null ");
            return false;
        }
        return currentConference.attachVideo(userID, deviceID);
    }

    @Override
    public void onBookReservedConfResult(TupConfOptResult result, TupConfInfo confInfo)
    {
        Log.i(TAG, "onBookReservedConfResult");
    }

    @Override
    public void onGetConfListResult(List<TupConfInfo> list, TupConfOptResult tupConfOptResult)
    {
        Log.i(TAG, "onGetConfListResult");
    }

    @Override
    public void onGetConfInfoResult(TupConfInfo confInfo, List<TupConfECAttendeeInfo> list,
                                    TupConfOptResult tupConfOptResult)
    {
        Log.i(TAG, "onGetConfInfoResult");
    }

    @Override
    public void onSubscribeConfResult(TupConfOptResult tupConfOptResult)
    {
        Log.i(TAG, "onSubscribeConfResult");
    }


    @Override
    public void onRequestConfRightResult(TupConfOptResult tupConfOptResult, TupConference tupConference) {
        Log.i(TAG, "onRequestConfRightResult");
    }


    @Override
    public void onConfStatusUpdateInd(TupConfInfo tupConfInfo, List<TupConfECAttendeeInfo> list, int participantUpdateType) {
        Log.i(TAG, "onConfStatusUpdateInd");
    }

    @Override
    public void onAddAttendeeResult(TupConfAttendeeOptResult tupConfAttendeeOptResult) {
        Log.i(TAG, "onAddAttendeeResult");
        if ((currentConference == null) || (tupConfAttendeeOptResult == null)) {
            return;
        }

        mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.ADD_ATTENDEE_RESULT, tupConfAttendeeOptResult.getOptResult());
    }

    @Override
    public void onDelAttendeeResult(TupConfAttendeeOptResult tupConfAttendeeOptResult) {
        Log.i(TAG, "onDelAttendeeResult");
        if ((currentConference == null) || (tupConfAttendeeOptResult == null)) {
            return;
        }

        mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.DEL_ATTENDEE_RESULT, tupConfAttendeeOptResult.getOptResult());
    }

    @Override
    public void onMuteAttendeeResult(TupConfAttendeeOptResult tupConfAttendeeOptResult, boolean isMute)
    {
        Log.i(TAG, "onMuteAttendeeResult");
        if ((currentConference == null) || (tupConfAttendeeOptResult == null)) {
            return;
        }

        if (isMute)
        {
            mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.MUTE_ATTENDEE_RESULT, tupConfAttendeeOptResult.getOptResult());
        }
        else
        {
            mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.UN_MUTE_ATTENDEE_RESULT, tupConfAttendeeOptResult.getOptResult());
        }
    }

    @Override
    public void onMuteConfResult(TupConfOptResult tupConfOptResult, boolean isMute) {
        Log.i(TAG, "onMuteConfResult");
    }

    @Override
    public void onLockConfResult(TupConfOptResult tupConfOptResult, boolean isLock) {
        Log.i(TAG, "onLockConfResult");
    }

    @Override
    public void onHandupResult(TupConfOptResult tupConfOptResult, boolean isHandup) {
        Log.i(TAG, "onHandupResult");
    }


    @Override
    public void onHanddownAttendeeResult(TupConfAttendeeOptResult tupConfAttendeeOptResult) {
        Log.i(TAG, "onHanddownAttendeeResult");
    }


    @Override
    public void onConfWillTimeOutInd(TupConference conf) {
        Log.i(TAG, "onConfWillTimeOutInd");
        if ((currentConference == null) || (conf == null)) {
            return;
        }

        int time = conf.getRemainingTime();
        mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.WILL_TIMEOUT, time);
    }

    @Override
    public void onConfPostponeResult(TupConfOptResult tupConfOptResult) {
        Log.i(TAG, "onConfPostponeResult");
        if ((currentConference == null) || (tupConfOptResult == null)) {
            return;
        }

        mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.POSTPONE_CONF_RESULT, tupConfOptResult.getOptResult());
    }

    @Override
    public void onSpeakerListInd(TupConference tupConference, TupConfSpeakerInfo tupConfSpeakerInfo) {
        Log.i(TAG, "onSpeakerListInd");
        if ((currentConference == null) || (tupConference == null) || (tupConfSpeakerInfo == null)) {
            return;
        }

        List<Member> speakers = new ArrayList<>();
        List<TupConfBaseAttendeeInfo> speakerList = tupConfSpeakerInfo.getSpeakerList();
        for (TupConfBaseAttendeeInfo speaker : speakerList)
        {
            Member member = new Member();
            member.setNumber(speaker.getNumber());

            //根据号码在与会者列表中找到与会者，获取此与会者的名字
//            Member temp = getMemberByNumber(speaker.getNumber());
            //SMC下通过MT号判断
            Member temp = getMemberByNumber(speaker.getMT());
            if (temp == null) {
                continue;
            }

            member.setDisplayName(temp.getDisplayName());
        }

        if (speakers.size() > 0)
        {
            mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.SPEAKER_LIST_IND, speakers);
        }
    }

    @Override
    public void onReqChairmanResult(TupConfOptResult tupConfOptResult) {
        Log.i(TAG, "onReqChairmanResult");
        if ((currentConference == null) || (tupConfOptResult == null)) {
            return;
        }
        mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.REQUEST_CHAIRMAN_RESULT, tupConfOptResult.getOptResult());
    }

    @Override
    public void onRealseChairmanResult(TupConfOptResult tupConfOptResult) {
        Log.i(TAG, "onRealseChairmanResult");
        if ((currentConference == null) || (tupConfOptResult == null)) {
            return;
        }
        mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.RELEASE_CHAIRMAN_RESULT, tupConfOptResult.getOptResult());
    }

    @Override
    public void onSetConfModeResult(TupConfOptResult tupConfOptResult) {
        Log.i(TAG, "onSetConfModeResult");
        if ((currentConference == null) || (tupConfOptResult == null)) {
            return;
        }

        mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.SET_CONF_MODE_RESULT, tupConfOptResult.getOptResult());
    }

    @Override
    public void onBroadcastAttendeeResult(TupConfOptResult tupConfOptResult) {
        Log.i(TAG, "onBroadcastAttendeeResult");
        if ((currentConference == null) || (tupConfOptResult == null)) {
            return;
        }
        mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.BROADCAST_ATTENDEE_RESULT, tupConfOptResult.getOptResult());
    }

    @Override
    public void onCancelBroadcastAttendeeResult(TupConfOptResult tupConfOptResult) {
        Log.i(TAG, "onCancelBroadcastAttendeeResult");
        if ((currentConference == null) || (tupConfOptResult == null)) {
            return;
        }
        mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.CANCEL_BROADCAST_ATTENDEE_RESULT, tupConfOptResult.getOptResult());
    }

    @Override
    public void onBroadcastAttendeeInd(TupConference tupConference, TupConfBaseAttendeeInfo tupConfBaseAttendeeInfo) {
        Log.i(TAG, "onBroadcastAttendeeInd");
    }

    @Override
    public void onCancelBroadcastAttendeeInd(TupConference tupConference, TupConfBaseAttendeeInfo tupConfBaseAttendeeInfo) {
        Log.i(TAG, "onCancelBroadcastAttendeeInd");
    }

    @Override
    public void onWatchAttendeeResult(TupConfOptResult tupConfOptResult) {
        Log.i(TAG, "onWatchAttendeeResult");
        if ((currentConference == null) || (tupConfOptResult == null)) {
            return;
        }
        mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.WATCH_ATTENDEE_RESULT, tupConfOptResult.getOptResult());
    }

    @Override
    public void onMultiPicResult(TupConfOptResult tupConfOptResult) {
        Log.i(TAG, "onMultiPicResult");
    }

    @Override
    public void onAttendeeBroadcastedInd(TupConference tupConference, TupConfBaseAttendeeInfo tupConfBaseAttendeeInfo) {
        Log.i(TAG, "onAttendeeBroadcastedInd");
    }

    @Override
    public void onLocalBroadcastStatusInd(TupConference tupConference, boolean b) {
        Log.i(TAG, "onLocalBroadcastStatusInd");
    }

    @Override
    public void onConfInfoInd(TupConference tupConference) {
        Log.i(TAG, "onConfInfoInd");
    }

    @Override
    public void onEndConfInd(TupConference tupConference) {
        Log.i(TAG, "onEndConfInd");
    }

    @Override
    public void onBeTransToConfInd(TupConference tupConference, int i) {
        Log.i(TAG, "onBeTransToConfInd");
    }

    @Override
    public void onEndConfResult(TupConfOptResult tupConfOptResult) {
        Log.i(TAG, "onEndConfResult");
    }

    @Override
    public void onCallAttendeeResult(TupConfAttendeeOptResult tupConfAttendeeOptResult) {
        Log.i(TAG, "onCallAttendeeResult");
    }

    @Override
    public void onHangupAttendeeResult(TupConfAttendeeOptResult tupConfAttendeeOptResult) {
        Log.i(TAG, "onHangupAttendeeResult");
    }

    @Override
    public void onTransToConfResult(TupConfOptResult tupConfOptResult, int i) {
        Log.i(TAG, "onTransToConfResult");
    }

    @Override
    public void onUpgradeConfResult(TupConfOptResult tupConfOptResult) {
        Log.i(TAG, "onUpgradeConfResult");

        if ((currentConference == null) || (tupConfOptResult == null)) {
            return;
        }

        // 升级会议成功，直接更新当前会议类型，防止服务器未及时推送会议状态更新消息
        if (tupConfOptResult.getOptResult() == 0) {
            ConfConstant.ConfMediaType confMediaType = currentConference.getConfBaseInfo().getMediaType();
            if (confMediaType == ConfConstant.ConfMediaType.VIDEO_CONF){
                currentConference.getConfBaseInfo().setMediaType(ConfConstant.ConfMediaType.VIDEO_AND_DATA_CONF);
                //meidiax升级会议在此处先加入一次数据会议，如果不成功 与会者列表更新会进行一次补救
                if(null != currentConference.getQueryJoinDataConfParamInfo()){
                    if (currentConference.getQueryJoinDataConfParamInfo().isGetDataParam()){
                        checkUpgradeDataConf();
                        currentConference.getQueryJoinDataConfParamInfo().setGetDataParam(false);
                    }

                }
            }
        }

        mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.UPGRADE_CONF_RESULT, tupConfOptResult.getOptResult());
    }

    @Override
    public void onAuxtokenOwnerInd(TupConference tupConference, TupConfBaseAttendeeInfo tupConfBaseAttendeeInfo) {
        Log.i(TAG, "onAuxtokenOwnerInd");
    }

    @Override
    public void onAuxsendCmd(TupConference tupConference, boolean b) {
        Log.i(TAG, "onAuxsendCmd");
    }

    @Override
    public void onGetDataConfParamsResult(TupConfOptResult tupConfOptResult, TupConfctrlDataconfParams tupConfctrlDataconfParams) {
        Log.i(TAG, "onGetDataConfParamsResult");
        if ((currentConference == null) || (tupConfOptResult == null)) {
            return;
        }

        if (tupConfOptResult.getOptResult() == 0) {
            currentConference.setJoinDataConfParams(tupConfctrlDataconfParams);
        }

        mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.GET_DATA_CONF_PARAM_RESULT, tupConfOptResult.getOptResult());
    }

    @Override
    public void onAddDataConfInd(TupConfOptResult tupConfOptResult, TupConfInfo tupConfInfo) {
        Log.i(TAG, "onAddDataConfInd");
    }

    @Override
    public void onAttendeeListUpdateInd(TupConference tupConference, List<TupConfVCAttendeeInfo> list, int participantUpdateType) {
        Log.i(TAG, "onAttendeeListUpdateInd");

        if ((currentConference == null) || (tupConference == null))
        {
            return;
        }

        if(null != currentConference.getQueryJoinDataConfParamInfo()){
            if (currentConference.getQueryJoinDataConfParamInfo().isGetDataParam()){
                checkUpgradeDataConf();
                currentConference.getQueryJoinDataConfParamInfo().setGetDataParam(false);
            }

        }

        //更新保存会议状态信息
        currentConference.updateConfInfo(null, list, participantUpdateType);

        mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.STATE_UPDATE, String.valueOf(tupConference.getCallID()));
    }

    @Override
    public void onBookReservedConfResult(TupConfOptResult tupConfOptResult) {
        Log.i(TAG, "onBookReservedConfResult");
        if ((tupConfOptResult == null))
        {
            Log.e(TAG, "book conference is failed, unknown error.");
            mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.BOOK_CONF_FAILED, -1);

            this.currentBookConfStatus = ConfConstant.BookConfStatus.IDLE;
            return;
        }

        if (tupConfOptResult.getOptResult() != TupConfParam.CONF_RESULT.TUP_SUCCESS)
        {
            Log.e(TAG, "book conference is failed, return ->" + tupConfOptResult.getOptResult());
            mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.BOOK_CONF_FAILED, tupConfOptResult.getOptResult());
        }
        else
        {
            Log.i(TAG, "book conference is success.");
            mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.BOOK_CONF_SUCCESS, tupConfOptResult.getOptResult());
        }

        //无论成功失败，均清除状态
        this.currentBookConfStatus = ConfConstant.BookConfStatus.IDLE;
        super.onBookReservedConfResult(tupConfOptResult);
    }

    @Override
    public void onChairmanInfoInd(TupConference tupConference) {
        Log.i(TAG, "onChairmanInfoInd");
        if(null == tupConference){
            return;
        }

        mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.CHAIRMAN_INFO, tupConference);
    }

    @Override
    public void onChairmanReleasedInd(TupConference tupConference, int i) {
        Log.i(TAG, "onChairmanInfoInd");
        if(null == tupConference){
            return;
        }

        mConfNotification.onConfEventNotify(ConfConstant.CONF_EVENT.CHAIRMAN_RELEASE_IND, tupConference);
    }
}
