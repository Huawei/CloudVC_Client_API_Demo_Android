package com.huawei.opensdk.ec_sdk_demo.ui.call;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.opensdk.callmgr.CallMgr;
import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcastReceiver;
import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.contactmgr.ContactMgr;
import com.huawei.opensdk.contactmgr.RecordsInfo;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.adapter.RecordListAdapter;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.ui.base.AbsFragment;
import com.huawei.opensdk.loginmgr.LoginMgr;

import java.util.ArrayList;
import java.util.List;

public class CallFragment extends AbsFragment implements View.OnClickListener, AdapterView.OnItemLongClickListener, LocBroadcastReceiver
{
    private ImageView mBtnCall;
    private ImageView mBtnVideo;
    private String mSipNumber = "";
    private ImageView mHideDial;

    private LinearLayout mDialLayout;

    private DialPlateControl mDialPlateControl;

    private ImageView mShowArea;
    private ImageView mDeleteNumberArea;

    private static String number;
    private int mCallID = 0;

    private ListView mRecordView;
    private RecordListAdapter mRecordListAdapter;
    private List<RecordsInfo> mRecordsInfoList = new ArrayList<>();
    private String[] action = new String[]{CustomBroadcastConstants.ACTION_CALL_RECORD};

    public static String getNumber() {
        return number;
    }

    public static void setNumber(String number) {
        CallFragment.number = number;
    }

    /**
     * display call view
     * @param callNumber the call number
     * @param isVideoCall the is video call
     */
    public void showCallingLayout(final String callNumber, final boolean isVideoCall)
    {
        if (TextUtils.isEmpty(callNumber))
        {
            LogUtil.i(UIConstants.DEMO_TAG, "empty CallNumber return!!!");
            return;
        }

        mCallID = CallMgr.getInstance().startCall(callNumber, isVideoCall);
        if (mCallID == 0)
        {
            return;
        }
    }

    @Override
    public int getLayoutId()
    {
        return R.layout.fragment_call;
    }

    @Override
    public void onViewLoad()
    {
        super.onViewLoad();
    }

    @Override
    public void onDataLoad()
    {
        super.onDataLoad();
    }

    @Override
    public void onViewCreated()
    {
        LocBroadcast.getInstance().registerBroadcast(this, action);
        super.onViewCreated();
        mBtnCall = (ImageView) mView.findViewById(R.id.call_audio_btn);
        mBtnVideo = (ImageView) mView.findViewById(R.id.call_video_btn);
        mHideDial = (ImageView) mView.findViewById(R.id.hide_dial_btn);
        mDialLayout = (LinearLayout) mView.findViewById(R.id.dial_plate_area);
        mShowArea = (ImageView) mView.findViewById(R.id.show_dial_btn);
        mDeleteNumberArea = (ImageView) mView.findViewById(R.id.delete_panel_btn);
        mRecordView = (ListView) mView.findViewById(R.id.call_record_list);

        mRecordsInfoList = ContactMgr.getInstance().getLocalRecords();
        if (mRecordsInfoList.size() == 0 || null == mRecordsInfoList)
        {
            //可以添加一些无记录的界面，暂未实现
        }
        mRecordListAdapter = new RecordListAdapter(getActivity());
        mRecordListAdapter.setData(mRecordsInfoList);
        mRecordView.setAdapter(mRecordListAdapter);

        mDialPlateControl = new DialPlateControl(mDialLayout);
//        mDialPlateControl.showDialPlate();
        mDialPlateControl.hideDialPlate();

        mBtnCall.setOnClickListener(this);
        mBtnVideo.setOnClickListener(this);
        mHideDial.setOnClickListener(this);
        mShowArea.setOnClickListener(this);
        mDeleteNumberArea.setOnClickListener(this);
        mRecordView.setOnItemLongClickListener(this);
        mSipNumber = LoginMgr.getInstance().getSipNumber();
    }

    private void hideSoftKeyboard(View v)
    {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private String checkCallNumber(String callNumber)
    {
        if (TextUtils.isEmpty(callNumber))
        {
            showToast(R.string.call_number_is_null);
            return null;
        }

        if (mSipNumber.equals(callNumber))
        {
            showToast(R.string.can_not_call_self);
            return null;
        }
        return callNumber;
    }

    private void showToast(int resId)
    {
        Toast.makeText(context, getString(resId), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v)
    {
        String toNumber = mDialPlateControl.getCallNumber();
        switch (v.getId())
        {
            case R.id.call_audio_btn:
                if (TextUtils.isEmpty(toNumber))
                {
                    LogUtil.i(UIConstants.DEMO_TAG, "call number is empty!");
                    return;
                }
                hideSoftKeyboard(v);
                checkCallNumber(toNumber);
                showCallingLayout(toNumber, false);
                break;
            case R.id.call_video_btn:
                if (TextUtils.isEmpty(toNumber))
                {
                    LogUtil.i(UIConstants.DEMO_TAG, "call number is empty!");
                    return;
                }
                hideSoftKeyboard(v);
                checkCallNumber(toNumber);
                showCallingLayout(toNumber, true);
                break;
            case R.id.hide_dial_btn:
                mDialPlateControl.hideDialPlate();
                break;
            case R.id.show_dial_btn:
                mDialPlateControl.showDialPlate();
                break;
            case R.id.delete_panel_btn:
                if (TextUtils.isEmpty(toNumber))
                {
                    LogUtil.i(UIConstants.DEMO_TAG, context.getString(R.string.number_empty));
                    return;
                }
                toNumber = toNumber.substring(0, toNumber.length() - 1);
                mDialPlateControl.setCallNumber(toNumber);
                break;
            default:
                break;
        }
    }

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case UIConstants.REFRESH_CALL_RECORDS_LIST:
                    mRecordListAdapter.setData(ContactMgr.getInstance().getLocalRecords());
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onReceive(String broadcastName, Object obj) {
        switch (broadcastName)
        {
            case CustomBroadcastConstants.ACTION_CALL_RECORD:
                handler.sendEmptyMessage(UIConstants.REFRESH_CALL_RECORDS_LIST);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        TextView tvId = (TextView) view.findViewById(R.id.call_record_list_id);
        final int recordId = Integer.parseInt(tvId.getText().toString());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.delete);
        builder.setMessage(R.string.delete_callrecord);
        builder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int delResult = ContactMgr.getInstance().deleteCallRecordByRecordId(recordId);
                mRecordsInfoList = ContactMgr.getInstance().getLocalRecords();
                if (delResult == 0)
                {
                    Toast.makeText(getActivity(),"Delete success",Toast.LENGTH_SHORT).show();
                    mRecordListAdapter.setData(mRecordsInfoList);
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, null);
        builder.create();
        builder.show();
        return true;
    }
}
