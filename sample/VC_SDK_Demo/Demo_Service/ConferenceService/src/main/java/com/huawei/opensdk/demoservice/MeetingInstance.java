package com.huawei.opensdk.demoservice;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import com.huawei.meeting.ConfDefines;
import com.huawei.meeting.ConfInfo;
import com.huawei.opensdk.sdkwrapper.login.LoginCenter;
import com.huawei.tup.confctrl.ConfctrlConfMediatypeFlag;
import com.huawei.tup.confctrl.ConfctrlConfMode;
import com.huawei.tup.confctrl.ConfctrlConfRole;
import com.huawei.tup.confctrl.ConfctrlIPVersion;
import com.huawei.tup.confctrl.ConfctrlSiteCallLanguageType;
import com.huawei.tup.confctrl.ConfctrlSiteCallTerminalType;
import com.huawei.tup.confctrl.sdk.TupConfAccessInfo;
import com.huawei.tup.confctrl.sdk.TupConfDataConfParamsGetReq;
import com.huawei.tup.confctrl.sdk.TupConfInfo;
import com.huawei.tup.confctrl.sdk.TupConfVCAttendeeInfo;
import com.huawei.tup.confctrl.sdk.TupConfVCTerminalInfo;
import com.huawei.tup.confctrl.sdk.TupConfctrlDataconfParams;
import com.huawei.tup.confctrl.sdk.TupConference;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is about meeting instance
 * 会议实例类
 */
public class MeetingInstance {
    private static final String TAG = MeetingInstance.class.getSimpleName();

    /**
     * 呼叫id
     */
    private int callID = 0;

    /**
     * 会议基础信息
     */
    private ConfBaseInfo confBaseInfo;

    /**
     * 数据会议入会参数
     */
    private TupConfctrlDataconfParams joinDataConfParams;

    /**
     * 查询加入数据会议参数信息
     */
    private QueryJoinDataConfParamInfo queryJoinDataConfParamInfo;

    /**
     * 会议信息
     */
    private TupConference tupConference;

    /**
     * 数据会议信息
     */
    private DataConference dataConference;

    /**
     * 密码
     */
    private String password;

    /**
     * token
     */
    private String token;

    /**
     * 是否需要邀请自己
     */
    private boolean needInviteYourself;

    /**
     * 是否查询数据会议参数
     */
    private boolean isQueriedDataConfParam = false;

    /**
     * 是否已在数据会议中
     */
    private boolean isInDataConf = false;

    /**
     * 与会者列表
     */
    private List<Member> memberList;
    private Member self;

    /**
     * 与会者角色
     */
    private ConfConstant.ConfRole selfRole = ConfConstant.ConfRole.ATTENDEE;


    public ConfBaseInfo getConfBaseInfo() {
        return confBaseInfo;
    }

    /**
     * This method is used to data conf param info
     * 更新数据会议信息
     * @param queryJoinDataConfParamInfo
     */
    private void updateQueryJoinDataConfParamInfo(QueryJoinDataConfParamInfo queryJoinDataConfParamInfo) {
        if ((this.queryJoinDataConfParamInfo == null) || (queryJoinDataConfParamInfo == null)) {
            return;
        }

        String confId = queryJoinDataConfParamInfo.getConfId();
        String passCode = queryJoinDataConfParamInfo.getPassCode();
        String confUrl = queryJoinDataConfParamInfo.getConfUrl();
        String random = queryJoinDataConfParamInfo.getRandom();

        if ((confId != null) && (!confId.equals(""))) {
            this.queryJoinDataConfParamInfo.setConfId(confId);
        }

        if ((passCode != null) && (!passCode.equals(""))) {
            this.queryJoinDataConfParamInfo.setPassCode(passCode);
        }

        if ((confUrl != null) && (!confUrl.equals(""))) {
            this.queryJoinDataConfParamInfo.setConfUrl(confUrl);
        }

        if ((random != null) && (!random.equals(""))) {
            this.queryJoinDataConfParamInfo.setRandom(random);
        }
    }

    public void setQueryJoinDataConfParamInfo(QueryJoinDataConfParamInfo queryJoinDataConfParamInfo) {
        if (this.queryJoinDataConfParamInfo == null)
        {
            this.queryJoinDataConfParamInfo = queryJoinDataConfParamInfo;
        }
        else
        {
            updateQueryJoinDataConfParamInfo(queryJoinDataConfParamInfo);
        }

        // 无论是否查询过，在此时均设置一次，若上一次失败，再尝试一次
        this.isQueriedDataConfParam = false;
    }

    public QueryJoinDataConfParamInfo getQueryJoinDataConfParamInfo(){
        return this.queryJoinDataConfParamInfo;
    }

    public void setJoinDataConfParams(TupConfctrlDataconfParams joinDataConfParams) {
        this.joinDataConfParams = joinDataConfParams;
    }


    public MeetingInstance()
    {
        this.tupConference = new TupConference();
        this.confBaseInfo = new ConfBaseInfo();
    }



    public boolean isNeedInviteYourself() {
        return needInviteYourself;
    }



    public int getCallID() {
        return callID;
    }

    public void setCallID(int callID) {
        this.callID = callID;
    }


    public List<Member> getMemberList() {
        return memberList;
    }

    public void setMemberList(List<Member> memberList) {
        this.memberList = memberList;
    }

    public Member getSelf() {
        return self;
    }

    public void setSelf(Member self) {
        this.self = self;
    }


    public ConfConstant.ConfRole getSelfRole() {
        return selfRole;
    }

    public void setSelfRole(ConfConstant.ConfRole selfRole) {
        this.selfRole = selfRole;
    }


    /**
     * This method is used to update conf info
     * 更新会议信息
     * @param confDetailInfo 会议查询信息
     */
    public void updateConfInfo(ConfDetailInfo confDetailInfo)
    {
        confBaseInfo.setSize(confDetailInfo.getSize());
        confBaseInfo.setConfID(confDetailInfo.getConfID());
        confBaseInfo.setSubject(confDetailInfo.getSubject());
        confBaseInfo.setAccessNumber(confDetailInfo.getAccessNumber());
        confBaseInfo.setChairmanPwd(confDetailInfo.getChairmanPwd());
        confBaseInfo.setGuestPwd(confDetailInfo.getGuestPwd());
        confBaseInfo.setStartTime(confDetailInfo.getStartTime());
        confBaseInfo.setEndTime(confDetailInfo.getEndTime());
        confBaseInfo.setSchedulerNumber(confDetailInfo.getSchedulerNumber());
        confBaseInfo.setSchedulerName(confDetailInfo.getSchedulerName());
        confBaseInfo.setGroupUri(confDetailInfo.getGroupUri());
        confBaseInfo.setMediaType(confDetailInfo.getMediaType());
        confBaseInfo.setConfState(confDetailInfo.getConfState());
    }


    /**
     * This method is used to update conf info
     * 更新会议信息
     * @param tupConfInfo 会议信息
     * @param attendeeList 与会者列表
     * @param participantUpdateType 更新类型
     */
    public void updateConfInfo(TupConfInfo tupConfInfo, List<TupConfVCAttendeeInfo> attendeeList, int participantUpdateType)
    {

        if (memberList == null) {
            memberList = new ArrayList<>();
        }

        Member temp;
        Member member;
        for (TupConfVCAttendeeInfo attendee : attendeeList) {
            String number = attendee.getMT();
            temp = getMemberByNumber(number);
            if (temp == null) {
                member = ConfConvertUtil.convertAttendeeInfo(attendee,participantUpdateType);

                if (member.getRole() == ConfConstant.ConfRole.CHAIRMAN) {
                    memberList.add(0, member);
                }else {
                    memberList.add(member);
                }
            } else {
                    member = getMemberByNumber(attendee.getMT());
                if (!"".equals(attendee.getSiteName()))
                {
                    member.setNumber(attendee.getSiteName());
                    member.setDisplayName(attendee.getSiteName());
                    member.setAccountId(attendee.getSiteName());
                    member.setMute(attendee.isMute());
                    ConfConstant.ConfRole role = ((attendee.getRole() == ConfctrlConfRole.CONFCTRL_E_CONF_ROLE_CHAIRMAN) ?
                            ConfConstant.ConfRole.CHAIRMAN : ConfConstant.ConfRole.ATTENDEE);
                    member.setRole(role);
                    if (judgeMemberIsSelf(member)) {
                        member.setSelf(true);
                        this.setSelf(member);
                    }
                }
                switch (participantUpdateType){
                    case 0:
                        member.setJoinConf(true);
                        member.setStatus(ConfConstant.ParticipantStatus.IN_CONF);
                        break;
                    case 1:
                        member.setLeaveConf(true);
                        member.setStatus(ConfConstant.ParticipantStatus.LEAVED);
                        break;
                    case 2:
                        break;
                    case 3:
                        memberList.remove(member);
                        break;
                    case 4:
                        member.setMute(attendee.isMute());
                        break;
                    case 5:
                        break;
                    case 6:
                        break;
                    case 7:
                        member.setSiteName(attendee.getSiteName());
                        break;
                    case 8:
                        break;
                    case 9:
                        break;
                    case 10:
                        break;
                    case 11:
                        break;
                }
//                temp.update(attendee);
//                if ((temp.getRole() == ConfConstant.ConfRole.CHAIRMAN) && (memberList.indexOf(temp) != 0)){
//                    memberList.remove(temp);
//                    memberList.add(0, temp);
//                }
            }
        }

        return;
    }


    /**
     * This method is used to check upgrade data conf
     * 检查是否升级数据会议
     */
    public void checkUpgradeDataConf()
    {
        if ((self != null) && (self.isInDataConference())) {
            return;
        }

        if (isQueriedDataConfParam) {
            return;
        }

        if (joinDataConfParams != null) {
            isQueriedDataConfParam = true;
            return;
        }

        if (LoginCenter.getInstance().getServerType()==LoginCenter.LOGIN_E_SERVER_TYPE_SMC)
        {
            getDataConfParamsInSMC();
        }
        else
        {
            getDataConfParamsInMEDIAX();
        }

        // 查询失败也不再尝试，因为尝试了也没用
        isQueriedDataConfParam = true;
    }


    /**
     * This method is used to get data conf params in MEDIAX
     * MEDIAX组网下获取数据会议参数
     */
    private void getDataConfParamsInMEDIAX()
    {
        int result;
        if (queryJoinDataConfParamInfo != null)
        {
            TupConfDataConfParamsGetReq dataConfParams = new TupConfDataConfParamsGetReq();
            dataConfParams.setConfUrl(queryJoinDataConfParamInfo.getConfUrl());
            dataConfParams.setConfId(queryJoinDataConfParamInfo.getConfId());
            dataConfParams.setPassword(queryJoinDataConfParamInfo.getPassCode());
            dataConfParams.setRandom(queryJoinDataConfParamInfo.getRandom());
            dataConfParams.setType(3);
            result = tupConference.getDataConfParams(dataConfParams);
            if (result != 0) {
                Log.e(TAG, "getDataConfParams result ->" + result);
            }
        }
        return;
    }

    /**
     * This method is used to get data conf params in SMC
     * SMC组网下获取数据会议参数
     */
    private void getDataConfParamsInSMC()
    {
        int result;
        if (queryJoinDataConfParamInfo != null)
        {
            TupConfDataConfParamsGetReq dataConfParams = new TupConfDataConfParamsGetReq();
            dataConfParams.setConfUrl(queryJoinDataConfParamInfo.getConfUrl());
            dataConfParams.setPasscode(MeetingMgr.getInstance().getDateConfId());
            dataConfParams.setSipNum(LoginCenter.getInstance().getSipAccountInfo().getTerminal());
            dataConfParams.setType(1);
            result = tupConference.getDataConfParams(dataConfParams);
            if (result != 0) {
                Log.e(TAG, "getDataConfParams result ->" + result);
            }
        }
        return;
    }

    /**
     * 判断自己是否已在与会者里面
     * @param member
     * @return
     */
    private boolean judgeMemberIsSelf(Member member) {
        if (member.isSelf()
                || member.getNumber().equals(MeetingMgr.getInstance().getJoinConfNumber())
                || member.getNumber().equals(LoginCenter.getInstance().getSipAccountInfo().getTerminal())
                || member.getNumber().equals(LoginCenter.getInstance().getSipAccountInfo().getSipNumber())
                || member.getNumber().equals(LoginCenter.getInstance().getSipAccountInfo().getSipImpi())) {
            return true;
        }
        return false;
    }

    private boolean judgeMemberWhetherOnline(Member member, List<TupConfVCAttendeeInfo> attendeeList) {
        for (TupConfVCAttendeeInfo attendeeInfo : attendeeList) {
            if (member.getNumber().equals(attendeeInfo.getMT())) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method is used to join conference
     * @param confId              会议ID
     * @param password            会议接入密码
     * @param token               会议接入TOKEN
     * @param needInviteYourself  通过会议列表等方式主动入会议时，需要邀请自己的号码；
     *                             被邀请或IVR主动入会等方式加入时，不需要邀请自己的号码
     * @return
     */
    public int joinConf(String confId, String password, String token, boolean needInviteYourself)
    {
        Log.i(TAG, "join conf.");

        //记录一下当前的会议密码和TOKEN，用于申请会控权限
        this.password = password;
        this.token = token;
        this.confBaseInfo.setConfID(confId);

        TupConfAccessInfo tupConfAccessInfo = new TupConfAccessInfo(Integer.valueOf(confId),0,"","");

        tupConfAccessInfo.setPasscode(password);

        int result = this.tupConference.accessConf(tupConfAccessInfo);
        if (result != 0)
        {
            Log.e(TAG, "accessConf result ->" + result);
            return result;
        }

        return 0;
    }

    /**
     * This method is used to leave conf
     * 离开会议
     * @return
     */
    public int leaveConf()
    {
        Log.i(TAG, "leave conf.");

        //如果已加入数据会议，则先先离开数据会议
        if (dataConference != null)
        {
            dataConference.leaveConf();
            dataConference = null;
        }

        int destroyResult = this.tupConference.destroyConf();
        if (destroyResult != 0)
        {
            Log.e(TAG, "destroyConf ->" + destroyResult);
        }

        return 0;
    }

    /**
     * This method is used to end conf
     * 结束会议
     * @return
     */
    public int endConf()
    {
        Log.i(TAG, "end conf.");

        //如果已加入数据会议，则先先结束数据会议
        if (dataConference != null)
        {
            dataConference.terminateConf();
            dataConference = null;
        }

        int result = this.tupConference.endConf();
        if (result != 0)
        {
            Log.e(TAG, "endConf ->" + result);
        }

        int destroyResult = this.tupConference.destroyConf();
        if (destroyResult != 0)
        {
            Log.e(TAG, "destroyConf ->" + destroyResult);
        }

        return destroyResult;
    }


    /**
     * This method is used to add attendee
     * 添加与会者
     * @param attendee 与会者信息
     * @return
     */
    public int addAttendee(Member attendee)
    {
        Log.i(TAG, "add attendee.");
		
        TupConfVCTerminalInfo tupConfVCTerminalInfo = new TupConfVCTerminalInfo();
        tupConfVCTerminalInfo.setTerminalID(attendee.getNumber());
        tupConfVCTerminalInfo.setTerminalIDLength(attendee.getNumber().length());
        tupConfVCTerminalInfo.setPucNumber(attendee.getNumber());
        tupConfVCTerminalInfo.setNumberLen(attendee.getNumber().length());
        String uri = LoginCenter.getInstance().getLoginServerAddress();

        tupConfVCTerminalInfo.setUdwSiteBandwidth(1920);
        List<String> tels = new ArrayList<>();
        tels.add(attendee.getNumber());
        tupConfVCTerminalInfo.setTelNum(tels);
        tupConfVCTerminalInfo.setTerminalType(ConfctrlSiteCallTerminalType.CC_sip);
        tupConfVCTerminalInfo.setURI(uri);
        tupConfVCTerminalInfo.setURILen(uri.length());
        tupConfVCTerminalInfo.setIpType(ConfctrlIPVersion.CC_IP_V4);
        tupConfVCTerminalInfo.setLanguageType(ConfctrlSiteCallLanguageType.CC_sitecall_simpleChineseGB2312);
        List<TupConfVCTerminalInfo> attendeeList = new ArrayList<>();
        attendeeList.add(tupConfVCTerminalInfo);

        int result = this.tupConference.addVCAttendee(attendeeList);
        if (result != 0)
        {
            Log.e(TAG, "addECAttendee ->" + result);
        }
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
        Log.i(TAG, "hangup attendee.");

        int result;
        result = this.tupConference.hangUpAttendee(attendee.getMT());
        if (result != 0)
        {
            Log.e(TAG, "endConf ->" + result);
        }

        result = this.tupConference.removeAttendee(attendee.getMT());
        if (result != 0)
        {
            Log.e(TAG, "endConf ->" + result);
        }

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
        Log.i(TAG, "hangup attendee.");

        int result = this.tupConference.hangUpAttendee(attendee.getMT());
        if (result != 0)
        {
            Log.e(TAG, "endConf ->" + result);
        }
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
        Log.i(TAG, "mute attendee.");

        int result = this.tupConference.muteAttendee(attendee.getMT(), isMute);
        if (result != 0)
        {
            Log.e(TAG, "muteAttendee ->" + result);
        }
        return result;
    }

    /**
     * This method is used to release chairman
     * 是否主席
     * @return
     */
    public int releaseChairman()
    {
        Log.i(TAG, "release chairman.");

        int result = this.tupConference.releaseChairman(self.getMT());
        if (result != 0)
        {
            Log.e(TAG, "releaseChairman ->" + result);
        }
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
        Log.i(TAG, "request chairman.");

        int result = this.tupConference.requestChairman(chairmanPassword, self.getMT());
        if (result != 0)
        {
            Log.e(TAG, "requestChairman ->" + result);
        }
        return result;
    }

    public int setPresenter(Member member)
    {
        Log.i(TAG, "set presenter .");

        if (dataConference == null)
        {
            return -1;
        }

        return dataConference.setPresenter(member.getDataUserId());
    }

    /**
     * This method is used to set host
     * 设置主席
     * @param member 与会者信息
     * @return
     */
    public int setHost(Member member)
    {
        Log.i(TAG, "set host .");

        if (dataConference == null)
        {
            return -1;
        }

        return dataConference.setHost(member.getDataUserId());
    }

    public int postpone(int time)
    {
        Log.i(TAG, "postpone conf.");

        int result = this.tupConference.rostponeConf(time);
        if (result != 0)
        {
            Log.e(TAG, "rostponeConf ->" + result);
        }
        return result;
    }

    /**
     * This method is used to set conf mode
     * 设置会议类型
     * @param confctrlConfMode
     * @return
     */
    public int setConfMode(ConfctrlConfMode confctrlConfMode)
    {
        Log.i(TAG, "set conf mode.");

        int result = this.tupConference.setConfMode(confctrlConfMode);
        if (result != 0)
        {
            Log.e(TAG, "setConfMode ->" + result);
        }
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
        Log.i(TAG, "broadcast attendee.");

        int result = this.tupConference.broadcastAttendee(attendee.getMT(), isBroadcast);
        if (result != 0)
        {
            Log.e(TAG, "broadcastAttendee ->" + result);
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
        Log.i(TAG, "watch attendee.");

        int result = this.tupConference.watchAttendee(attendee.getMT());
        if (result != 0)
        {
            Log.e(TAG, "watchAttendee ->" + result);
        }
        return result;
    }

    /**
     * This method is used to upgrade conf
     * 升级会议
     * @return
     */
    public int upgradeConf()
    {
        Log.i(TAG, "upgrade conf.");

        int mediaType = ConfConvertUtil.convertConfMediaType(confBaseInfo.getMediaType());
        mediaType = mediaType | ConfctrlConfMediatypeFlag.CONFCTRL_E_CONF_MEDIATYPE_FLAG_DATA.getIndex();

        int result = this.tupConference.upgradeConf(mediaType, confBaseInfo.getGroupUri());
        if (result != 0)
        {
            Log.e(TAG, "upgradeConf ->" + result);
        }
        return result;
    }

    /**
     * This method is used to join data conf
     * 加入数据会议
     * @param confNotification
     * @return
     */
    public int joinDataConf(IConfNotification confNotification)
    {
        Log.i(TAG, "join data conf.");
        if (joinDataConfParams == null)
        {
            return -1;
        }

        if (dataConference != null) {
            return 0;
        }
        dataConference = new DataConference(this, confNotification);

        String hostKey = joinDataConfParams.getHostKey();
        String confKey = joinDataConfParams.getCryptKey();
        String siteId = joinDataConfParams.getSiteId();
        String dataConfID = joinDataConfParams.getConfId();
        String userId = joinDataConfParams.getUserId();
        String userName = LoginCenter.getInstance().getAccount();
        if (null != self)
        {
            userName = self.getDisplayName();
        }


        String siteUrl;
        String serverIp;
        String svrinterIp;

        int option = ConfDefines.CONF_OPTION_USERLIST
                | ConfDefines.CONF_OPTION_QOS
                | ConfDefines.CONF_OPTION_HOST_NO_GRAB;

        ConfInfo confInfo = new ConfInfo();

        if (LoginCenter.getInstance().getServerType() == LoginCenter.getInstance().LOGIN_E_SERVER_TYPE_SMC)
        {
            //uc
            option |= ConfDefines.CONF_OPTION_NO_VIDEO;
            siteUrl = joinDataConfParams.getCmAddress();
            serverIp = joinDataConfParams.getServerIp();
        }
        else
        {
            //mediaX
            option |= ConfDefines.CONF_OPTION_NO_VIDEO;

            siteUrl = joinDataConfParams.getSiteUrl();
            serverIp = joinDataConfParams.getSbcServerAddress();
            svrinterIp = joinDataConfParams.getServerIp();
            confInfo.setSvrinterIp(svrinterIp);
            confInfo.setM((byte) joinDataConfParams.getM());
            confInfo.setT((byte) joinDataConfParams.getT());
        }

        confInfo.setConfId(Integer.parseInt(dataConfID));
        confInfo.setConfKey(confKey);
        confInfo.setHostKey(hostKey);
        if ((userId != null) && (!userId.equals(""))) {
            confInfo.setUserId(Long.parseLong(userId));
        }
        confInfo.setUserName(userName);
        if (selfRole == ConfConstant.ConfRole.CHAIRMAN) {
            confInfo.setUserType(ConfDefines.CONF_ROLE_HOST | ConfDefines.CONF_ROLE_PRESENTER);
        } else {
            confInfo.setUserType(ConfDefines.CONF_ROLE_GENERAL);
        }
        confInfo.setSiteId(siteId);

        confInfo.setSvrIp(serverIp);
        confInfo.setSiteUrl(siteUrl);

        if (self != null) {
            confInfo.setUserUri(self.getNumber());
        } else {
            confInfo.setUserUri(LoginCenter.getInstance().getSipAccountInfo().getTerminal());
        }

        String userInfoStr = "<UserInfo><BindNum>" + confInfo.getUserUri() + "</BindNum></UserInfo>";
        byte[] userInfo = userInfoStr.getBytes(Charset.defaultCharset());
        // 组件接口的逻辑一定要先传len再传info
        confInfo.setUserInfoLen(userInfo.length);
        confInfo.setUserInfo(userInfo);

        confInfo.setConfOption(option);
        confInfo.setConfTitle("DataConference");

        dataConference.joinConf(confInfo);

        return 0;
    }


    public Member getMemberByNumber(String number)
    {
        Member temp = new Member(number);
        int index = memberList.indexOf(temp);
        if (index == -1) {
            return null;
        } else {
            return memberList.get(index);
        }
    }

    public Member getMemberByDataUserId(long userId) {
        if (userId == 0) {
            return null;
        }

        for (Member member : memberList) {
            if (member.getDataUserId() == userId) {
                return member;
            }
        }
        return null;
    }

    public void attachSurfaceView(ViewGroup container, Context context)
    {
        if (dataConference != null)
        {
            dataConference.attachSurfaceView(container, context);
        }
    }

    public boolean switchCamera()
    {
        if (dataConference != null)
        {
            return dataConference.switchCamera();
        }
        return false;
    }

    public boolean openLocalVideo()
    {
        if (dataConference != null)
        {
            return dataConference.openLocalVideo();
        }
        return false;
    }

    public boolean closeLocalVideo()
    {
        if (dataConference != null)
        {
            return dataConference.closeLocalVideo();
        }
        return false;
    }

    public void setVideoContainer(Context context, ViewGroup localView, ViewGroup remoteView)
    {
        if (dataConference != null)
        {
            dataConference.getVideoManager().setVideoContainer(context, localView, remoteView);
        }
    }

    public boolean attachRemoteVideo(long userID, long deviceID)
    {
        if (dataConference != null)
        {
            return dataConference.attachRemoteVideo(userID, deviceID);
        }
        return false;
    }

    public boolean detachRemoteVideo(long userID, long deviceID)
    {
        if (dataConference != null)
        {
            return dataConference.detachRemoteVideo(userID, deviceID);
        }
        return false;
    }

    public boolean videoOpen(long deviceID)
    {
        if (dataConference != null)
        {
            return dataConference.videoOpen(deviceID);
        }
        return false;
    }

    public void leaveVideo()
    {
        if (dataConference != null)
        {
            dataConference.leaveVideo();
        }
    }

    public void changeLocalVideoVisible(boolean visible)
    {
        if (dataConference != null)
        {
            dataConference.changeLocalVideoVisible(visible);
        }
    }

    public boolean attachVideo(long userID, long deviceID)
    {
        if (dataConference != null)
        {
            return dataConference.attachVideo(userID, deviceID);
        }
        return false;
    }
}