package com.huawei.opensdk.ec_sdk_demo.ui.conference;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.huawei.opensdk.demoservice.ConfDetailInfo;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.logic.conference.mvp.ConfDetailPresenter;
import com.huawei.opensdk.ec_sdk_demo.logic.conference.mvp.ICreateConfContract;
import com.huawei.opensdk.ec_sdk_demo.ui.base.MVPBaseActivity;
import com.huawei.opensdk.ec_sdk_demo.util.CommonUtil;
import com.huawei.opensdk.ec_sdk_demo.util.DateUtil;
import com.huawei.opensdk.ec_sdk_demo.widget.EditDialog;
import com.huawei.opensdk.sdkwrapper.login.LoginCenter;


public class ConfDetailActivity extends MVPBaseActivity<ICreateConfContract.ConfDetailView, ConfDetailPresenter> implements View.OnClickListener, ICreateConfContract.ConfDetailView
{

    private ConfDetailPresenter mPresenter;
    TextView participantNumberTV;
    String confID;

    @Override
    public void initializeComposition()
    {
        setContentView(R.layout.conference_detail_layout);
        participantNumberTV = (TextView) findViewById(R.id.participant_number_tv);
        participantNumberTV.setText(LoginCenter.getInstance().getSipAccountInfo().getTerminal());
        TextView titleTV = (TextView) findViewById(R.id.title_text);
        titleTV.setVisibility(View.VISIBLE);
        findViewById(R.id.participant_number_area).setOnClickListener(this);
        findViewById(R.id.reject_btn).setOnClickListener(this);
        findViewById(R.id.reject_btn).setVisibility(View.GONE);
        findViewById(R.id.join_conf_btn).setOnClickListener(this);
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
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.join_conf_btn:
                mPresenter.joinConf(participantNumberTV.getText().toString());
                finish();
                break;
            case R.id.reject_btn:

                break;
            case R.id.participant_number_area:
                showAccessNumberDialog();
                break;
            default:
                break;
        }
    }

    private void showAccessNumberDialog()
    {
        final EditDialog dialog = new EditDialog(this, R.string.input_access_number);
        dialog.setRightButtonListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                CommonUtil.hideSoftInput(ConfDetailActivity.this);
                if (TextUtils.isEmpty(dialog.getText()))
                {
                    showToast(R.string.invalid_number);
                    return;
                }
                mPresenter.updateAccessNumber(dialog.getText());
                participantNumberTV.setText(dialog.getText());
            }
        });
        dialog.show();
    }

    @Override
    protected ICreateConfContract.ConfDetailView createView()
    {
        return this;
    }

    @Override
    protected ConfDetailPresenter createPresenter()
    {
        mPresenter = new ConfDetailPresenter();
        return mPresenter;
    }

    @Override
    public void showLoading()
    {

    }

    @Override
    public void dismissLoading()
    {

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
    public void refreshView(final ConfDetailInfo confDetailInfo)
    {
        final String startTime = DateUtil.getInstance().utcToLocalDate(confDetailInfo.getStartTime(),
                DateUtil.FMT_YMDHM, DateUtil.FMT_YMDHM);
        final String endTime = DateUtil.getInstance().utcToLocalDate(confDetailInfo.getEndTime(),
                DateUtil.FMT_YMDHM, DateUtil.FMT_YMDHM);
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                ((TextView) findViewById(R.id.title_text)).setText(confDetailInfo.getSubject());
                ((TextView) findViewById(R.id.conference_start_time_tv)).setText(startTime);
                ((TextView) findViewById(R.id.conference_end_time_tv)).setText(endTime);
                ((TextView) findViewById(R.id.conference_id_content)).setText(confDetailInfo.getConfID());
                ((TextView) findViewById(R.id.conference_access_code_content)).setText(confDetailInfo.getAccessNumber());
                ((TextView) findViewById(R.id.conference_host_password_content)).setText(confDetailInfo.getChairmanPwd());
                ((TextView) findViewById(R.id.conference_member_password_content)).setText(confDetailInfo.getGuestPwd());
            }
        });

    }

}
