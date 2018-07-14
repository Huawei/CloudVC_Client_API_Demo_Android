package com.huawei.opensdk.ec_sdk_demo.logic.conference.mvp;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.huawei.opensdk.callmgr.CallMgr;
import com.huawei.opensdk.demoservice.ConfBaseInfo;
import com.huawei.opensdk.demoservice.ConfConstant;
import com.huawei.opensdk.demoservice.MeetingMgr;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.ui.base.MVPBasePresenter;
import com.huawei.opensdk.ec_sdk_demo.util.DateUtil;
import com.huawei.opensdk.sdkwrapper.login.LoginCenter;

import java.util.Comparator;
import java.util.List;


public class ConfListPresenter extends MVPBasePresenter<IConfListContract.ConfListView> implements IConfListContract.IConfListPresenter
{
    private List<ConfBaseInfo> confBaseInfoList;

    @Override
    public void receiveBroadcast(String broadcastName, Object obj)
    {
        switch (broadcastName)
        {
//            case CustomBroadcastConstants.GET_CONF_LIST_RESULT:
//                if (obj != null)
//                {
//                    confBaseInfoList = (List<ConfBaseInfo>) obj;
//                    Collections.sort(confBaseInfoList, getComparator());
//                    getView().refreshConfList(confBaseInfoList);
//                }
//                break;
//            default:
//                break;
        }
    }

//    @Override
//    public void queryConfList()
//    {
//        MeetingMgr.getInstance().queryMyConfList(ConfConstant.ConfRight.MY_CREATE_AND_JOIN);
//    }

    @Override
    public void onItemClick(int position)
    {
        getView().gotoConfDetailActivity(confBaseInfoList.get(position).getConfID());
    }

    @Override
    public void joinReserveConf(String confID, String accessCode, String password)
    {
        if (TextUtils.isEmpty(confID) || TextUtils.isEmpty(accessCode) || TextUtils.isEmpty(password))
        {
            getView().showCustomToast(R.string.empty_input);
            return;
        }

        //Model 1: use call to join the meeting
        joinReserveConf(confID, accessCode, password, true);

        //Model 2: use ctc to join the meeting
        //joinReserveConf(confID, password, ConfConstant.ConfRole.CHAIRMAN);

    }

    @NonNull
    private Comparator<ConfBaseInfo> getComparator()
    {
        return new Comparator<ConfBaseInfo>()
        {
            @Override
            public int compare(ConfBaseInfo lhs, ConfBaseInfo rhs)
            {
                long time1 = DateUtil.getInstance().parseDateStr(lhs.getStartTime(), DateUtil.UTC, DateUtil.FMT_YMDHM).getTime();
                long time2 = DateUtil.getInstance().parseDateStr(rhs.getStartTime(), DateUtil.UTC, DateUtil.FMT_YMDHM).getTime();
                if (time1 < time2)
                {
                    return 1;
                }
                return -1;
            }
        };
    }

    /**
     * This method is used to access reserved conf
     * @param confID  conference ID
     * @param accessCode reservation conference access code
     * @param password enrollment password
     * @param isVideo whether video access
     * @return
     */
    private void joinReserveConf(String confID, String accessCode, String password, boolean isVideo)
    {
        int callId = CallMgr.getInstance().accessReservedConf(confID, accessCode, password, isVideo);
        if (callId == 0)
        {
            getView().showCustomToast(R.string.join_conf_fail);
        }
    }


    /**
     * This method is used to access reserved conf
     * CTC模式加入预约会议，需要用户指定自己的角色
     * @param confID
     * @param password
     * @param role
     */
    private void joinReserveConf(String confID, String password, ConfConstant.ConfRole role)
    {
        // 指定自己的入会号码
        MeetingMgr.getInstance().setJoinConfNumber(LoginCenter.getInstance().getSipAccountInfo().getTerminal());

        // 先接入会议控制，再邀请自己的号码入会
        int result = MeetingMgr.getInstance().joinConf(confID, password, role);
        if (result != 0)
        {
            getView().showCustomToast(R.string.join_conf_fail);
        }
    }

}
