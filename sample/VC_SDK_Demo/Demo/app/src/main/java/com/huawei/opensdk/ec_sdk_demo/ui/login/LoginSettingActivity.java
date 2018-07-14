package com.huawei.opensdk.ec_sdk_demo.ui.login;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.annotation.IdRes;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.ui.base.BaseActivity;
import com.huawei.opensdk.loginmgr.LoginConstant;

public class LoginSettingActivity extends BaseActivity implements View.OnClickListener
{
    private EditText mRegServerEditText;
    private EditText mServerPortEditText;
    private CheckBox mVpnCheckBox;
    private RadioGroup mSrtpGroup;
    private RadioGroup mSipTransportGroup;
    private String mRegServerAddress;
    private String mServerPort;
    private boolean mIsVpn;
    private int mSrtpMode = 0;
    private int mSipTransport = 0;
    private SharedPreferences mSharedPreferences;

    private RadioGroup mVcTypeGroup;
    private int mVcType = 1;

    private void initView()
    {
        mRegServerEditText = (EditText) findViewById(R.id.et_register_server_address);
        mServerPortEditText = (EditText) findViewById(R.id.et_server_port);
        mVpnCheckBox = (CheckBox) findViewById(R.id.check_vpn_connect);
        mSrtpGroup = (RadioGroup)findViewById(R.id.rg_srtp);
        mSipTransportGroup = (RadioGroup)findViewById(R.id.rg_sip_transport);

        mVcTypeGroup = (RadioGroup) findViewById(R.id.vcType);

        ImageView searchButton = (ImageView) findViewById(R.id.right_img);
        ImageView navImage = (ImageView) findViewById(R.id.nav_iv);
        searchButton.setVisibility(View.GONE);
        TextView rightButton = (TextView) findViewById(R.id.right_btn);
        rightButton.setVisibility(View.VISIBLE);
        rightButton.setText(getString(R.string.save));

        rightButton.setOnClickListener(this);
        navImage.setOnClickListener(this);

        mVpnCheckBox.setChecked(mSharedPreferences.getBoolean(LoginConstant.TUP_VPN, false));
        mRegServerEditText.setText(mSharedPreferences.getString(LoginConstant.TUP_REGSERVER, LoginConstant.BLANK_STRING));
        mServerPortEditText.setText(mSharedPreferences.getString(LoginConstant.TUP_PORT, LoginConstant.BLANK_STRING));
        mSrtpGroup.check(getSrtpGroupCheckedId(mSharedPreferences.getInt(LoginConstant.TUP_SRTP, 0)));
        mSipTransportGroup.check(getSipTransportGroupCheckedId(mSharedPreferences.getInt(LoginConstant.TUP_SIP_TRANSPORT, 0)));
        mVcTypeGroup.check(getVcTypeGroupCheckedId(mSharedPreferences.getInt(LoginConstant.VC_TYPE, 1)));
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.right_btn:
                mRegServerAddress = mRegServerEditText.getText().toString().trim();
                mServerPort = mServerPortEditText.getText().toString().trim();
                mIsVpn = mVpnCheckBox.isChecked();
                mSrtpMode = getSrtpMode(mSrtpGroup.getCheckedRadioButtonId());
                mSipTransport = getSipTransportMode(mSipTransportGroup.getCheckedRadioButtonId());
                mVcType = getVcType(mVcTypeGroup.getCheckedRadioButtonId());

                saveLoginSetting(mIsVpn, mRegServerAddress, mServerPort);
                saveSecuritySetting(mSrtpMode, mSipTransport);
                saveVcType(mVcType);
                showToast(R.string.save_success);
                finish();
                break;
            case R.id.check_vpn_connect:
                if (mVpnCheckBox.isChecked())
                {
                    mVpnCheckBox.setChecked(true);
                }
                else
                {
                    mVpnCheckBox.setChecked(false);
                }
                break;
            case R.id.nav_iv:
                mVpnCheckBox.setChecked(mSharedPreferences.getBoolean(LoginConstant.TUP_VPN, false));
                mRegServerEditText.setText(mSharedPreferences.getString(LoginConstant.TUP_REGSERVER, LoginConstant.BLANK_STRING));
                mServerPortEditText.setText(mSharedPreferences.getString(LoginConstant.TUP_PORT, LoginConstant.BLANK_STRING));
                mSrtpGroup.check(getSrtpGroupCheckedId(mSharedPreferences.getInt(LoginConstant.TUP_SRTP, 0)));
                mSipTransportGroup.check(getSipTransportGroupCheckedId(mSharedPreferences.getInt(LoginConstant.TUP_SIP_TRANSPORT, 0)));
                mVcTypeGroup.check(getVcTypeGroupCheckedId(mSharedPreferences.getInt(LoginConstant.VC_TYPE, 1)));
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void initializeComposition()
    {
        setContentView(R.layout.activity_login_setting);
        initView();
        mVpnCheckBox.setOnClickListener(this);

        mSrtpGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId)
                {
                    case R.id.rb_srtp_mandatory:
                        mSrtpMode = 2;
                        break;
                    case R.id.rb_srtp_optional:
                        mSrtpMode = 1;
                        break;
                    case R.id.rb_srtp_disable:
                        mSrtpMode = 0;
                        break;
                    default:
                        break;
                }
            }
        });

        mSipTransportGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId)
                {
                    case R.id.rb_sip_transport_udp:
                        mSipTransport = 0;
                        break;
                    case R.id.rb_sip_transport_tls:
                        mSipTransport = 1;
                        break;
                    case R.id.rb_sip_transport_tcp:
                        mSipTransport = 2;
                        break;
                    default:
                        break;
                }
            }
        });

        mVcTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId)
                {
                    case R.id.vc_hosted:
                        mVcType = 1;
                        break;
                    case R.id.vc_smc:
                        mVcType = 2;
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void initializeData()
    {
        mSharedPreferences = getSharedPreferences(LoginConstant.FILE_NAME, Activity.MODE_PRIVATE);
    }

    private void saveLoginSetting(boolean isVpn, String regServerAddress, String serverPort)
    {
        if (TextUtils.isEmpty(regServerAddress) || TextUtils.isEmpty(serverPort))
        {
            showToast(R.string.server_information_not_empty);
            return;
        }
        mSharedPreferences.edit().putBoolean(LoginConstant.TUP_VPN, isVpn)
                .putString(LoginConstant.TUP_REGSERVER, regServerAddress)
                .putString(LoginConstant.TUP_PORT, serverPort)
                .commit();
    }

    private void saveSecuritySetting(int srtpMode, int sipTransport)
    {
        mSharedPreferences.edit().putInt(LoginConstant.TUP_SRTP, srtpMode)
                .putInt(LoginConstant.TUP_SIP_TRANSPORT, sipTransport)
                .commit();
    }

    private void saveVcType(int vcType)
    {
        mSharedPreferences.edit()
                .putInt(LoginConstant.VC_TYPE, vcType)
                .commit();
    }

    private int getSrtpGroupCheckedId(int srtpMode) {
        int id = R.id.rb_srtp_disable;
        switch (srtpMode) {
            case 0:
                id = R.id.rb_srtp_disable;
                break;
            case 1:
                id = R.id.rb_srtp_optional;
                break;
            case 2:
                id = R.id.rb_srtp_mandatory;
                break;
            default:
                break;
        }
        return id;
    }

    private int getSipTransportGroupCheckedId(int sipTransport) {
        int id = R.id.rb_sip_transport_udp;
        switch (sipTransport) {
            case 0:
                id = R.id.rb_sip_transport_udp;
                break;
            case 1:
                id = R.id.rb_sip_transport_tls;
                break;
            case 2:
                id = R.id.rb_sip_transport_tcp;
                break;
            default:
                break;
        }
        return id;
    }

    private int getVcTypeGroupCheckedId(int checkedId)
    {
        int vcId = R.id.vc_smc;
        switch (checkedId)
        {
            case 1:
                vcId = R.id.vc_hosted;
                break;
            case 2:
                vcId = R.id.vc_smc;
                break;
            default:
                break;
        }
        return vcId;
    }

    private int getSrtpMode(int checkedId) {
        int srtpMode = 0;
        switch (checkedId)
        {
            case R.id.rb_srtp_mandatory:
                srtpMode = 2;
                break;
            case R.id.rb_srtp_optional:
                srtpMode = 1;
                break;
            case R.id.rb_srtp_disable:
                srtpMode = 0;
                break;
            default:
                break;
        }
        return srtpMode;
    }

    private int getSipTransportMode(int checkedId) {
        int sipTransport = 0;
        switch (checkedId)
        {
            case R.id.rb_sip_transport_udp:
                sipTransport = 0;
                break;
            case R.id.rb_sip_transport_tls:
                sipTransport = 1;
                break;
            case R.id.rb_sip_transport_tcp:
                sipTransport = 2;
                break;
            default:
                break;
        }
        return sipTransport;
    }

    private int getVcType(int checkedId)
    {
        int type = 1;
        switch (checkedId)
        {
            case R.id.vc_hosted:
                type = 1;
                break;
            case R.id.vc_smc:
                type = 2;
                break;
            default:
                break;
        }
        return type;
    }
}
