package com.huawei.opensdk.ec_sdk_demo.ui.login;

import android.app.ProgressDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcastReceiver;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.ui.IntentConstant;
import com.huawei.opensdk.ec_sdk_demo.ui.base.MVPBaseActivity;
import com.huawei.opensdk.ec_sdk_demo.util.ActivityUtil;
import com.huawei.opensdk.ec_sdk_demo.logic.login.ILoginContract;
import com.huawei.opensdk.ec_sdk_demo.logic.login.LoginPresenter;
import com.huawei.opensdk.commonservice.util.LogUtil;

/**
 * This class is about login ui logic
 */
public class LoginActivity extends MVPBaseActivity<ILoginContract.LoginBaseView, LoginPresenter>
        implements ILoginContract.LoginBaseView, LocBroadcastReceiver
{

    /**
     * user name
      */
    private String mUserName;

    /**
     * password
     */
    private String mPassword;

    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private Button mLoginButton;

    private ImageView mLoginSettingBtn;

    private ProgressDialog mDialog;
    private String[] mActions = new String[]{CustomBroadcastConstants.LOGIN_SUCCESS, CustomBroadcastConstants.LOGIN_FAILED,
            CustomBroadcastConstants.LOGOUT};

    @Override
    public void initializeComposition()
    {
        setContentView(R.layout.activity_login);

        mUsernameEditText = (EditText) findViewById(R.id.et_account);
        mPasswordEditText = (EditText) findViewById(R.id.et_password);
        mLoginButton = (Button) findViewById(R.id.btn_login);
        mLoginSettingBtn = (ImageView) findViewById(R.id.iv_login_setting);

        mPresenter.onLoginParams();

        mLoginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                showLoginDialog(getString(R.string.logining_msg));
                mUserName = mUsernameEditText.getText().toString().trim();
                mPassword = mPasswordEditText.getText().toString();
                new Thread(runnable).start();
            }
        });

        mLoginSettingBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dismissLoginDialog();
                ActivityUtil.startActivity(LoginActivity.this, IntentConstant.LOGIN_SETTING_ACTIVITY_ACTION,
                        new String[]{IntentConstant.DEFAULT_CATEGORY});
            }
        });
    }
	
	/*最新的API要求需要在子线程里面判断网络信息，和一些耗时操作*/
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            mPresenter.doLogin(mUserName, mPassword);
        }
    };

    @Override
    public void initializeData()
    {
        mPresenter.initServerData();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        LocBroadcast.getInstance().registerBroadcast(this, mActions);
    }

    @Override
    public void dismissLoginDialog()
    {
        if (mDialog != null && mDialog.isShowing())
        {
            mDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        dismissLoginDialog();
        LocBroadcast.getInstance().unRegisterBroadcast(this, mActions);
    }

    public void showLoginDialog(String msg)
    {
        if (null == mDialog)
        {
            mDialog = new ProgressDialog(this);
        }

        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setTitle(msg);
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.show();
    }

    @Override
    public void setEditText(String account, String password)
    {
        mUsernameEditText.setText(account);
        mPasswordEditText.setText(password);
    }

    @Override
    public void showToast(int resId)
    {
        super.showToast(resId);
    }

    @Override
    protected ILoginContract.LoginBaseView createView()
    {
        return this;
    }

    @Override
    protected LoginPresenter createPresenter()
    {
        return new LoginPresenter(this);
    }

    @Override
    public void onReceive(String broadcastName, Object obj)
    {
        switch (broadcastName)
        {
            case CustomBroadcastConstants.LOGIN_SUCCESS:
                LogUtil.i(UIConstants.DEMO_TAG, "login success");
                dismissLoginDialog();
                break;
            case CustomBroadcastConstants.LOGIN_FAILED:
                LogUtil.i(UIConstants.DEMO_TAG, "login fail");
                dismissLoginDialog();
                break;
            case CustomBroadcastConstants.LOGOUT:
                break;
            default:
                break;
        }
    }
}
