package com.huawei.opensdk.ec_sdk_demo.ui;

import android.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huawei.opensdk.contactmgr.ContactMgr;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.ui.base.BaseActivity;
import com.huawei.opensdk.ec_sdk_demo.ui.call.CallFragment;
import com.huawei.opensdk.ec_sdk_demo.ui.contact.ContactFragment;
import com.huawei.opensdk.ec_sdk_demo.ui.discover.DiscoverFragment;
import com.huawei.opensdk.ec_sdk_demo.util.ActivityUtil;
import com.huawei.opensdk.ec_sdk_demo.widget.BaseDialog;
import com.huawei.opensdk.ec_sdk_demo.widget.ConfirmDialog;
import com.huawei.opensdk.ec_sdk_demo.widget.ThreeInputDialog;
import com.huawei.opensdk.loginmgr.LoginMgr;
import com.huawei.opensdk.sdkwrapper.login.LoginCenter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener
{
    private ImageView mCallTab;
    private ImageView mContactTab;
    private ImageView mDiscoverTab;
    private ViewPager mViewPager;
    private List<ImageView> mMainTabs = new ArrayList<>();
    private int mCurrentPosition;
    private CallFragment mCallFragment;
    private ContactFragment mContactFragment;
    private DiscoverFragment mDiscoverFragment;
    private final List<Fragment> fragments = new ArrayList<>();
    private ImageView mDrawerBtn;
    private DrawerLayout mDrawerLayout;

    private TextView displayName;
    private TextView sipNumber;
    private BaseDialog mLogoutDialog;
    private ImageView mSearchBtn;
    private ImageView mHeadIv;
    private String mMyAccount;

    @Override
    public void initializeComposition()
    {
        setContentView(R.layout.activity_main);
        mHeadIv = (ImageView) findViewById(R.id.blog_head_iv);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mCallTab = (ImageView) findViewById(R.id.call_tab);
        mContactTab = (ImageView) findViewById(R.id.contact_tab);
        mDiscoverTab = (ImageView) findViewById(R.id.discover_tab);
        mDrawerBtn = (ImageView) findViewById(R.id.nav_iv);
//        mStatusIv = (ImageView) findViewById(R.id.blog_state_iv);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mSearchBtn = (ImageView) findViewById(R.id.right_img);
        LinearLayout logoutButton = (LinearLayout) findViewById(R.id.logout_btn);

        LinearLayout settingButton = (LinearLayout) findViewById(R.id.iv_setting);

        //暂时只在SMC下支持修改密码
        if (LoginCenter.getInstance().getServerType() == LoginCenter.getInstance().LOGIN_E_SERVER_TYPE_SMC){
            LinearLayout changePwdButton = (LinearLayout) findViewById(R.id.iv_change_pwd);
            changePwdButton.setOnClickListener(this);
        }

        displayName = (TextView) findViewById(R.id.blog_name_tv);
        sipNumber = (TextView) findViewById(R.id.blog_number_tv);
        sipNumber.setSelected(true);

        mSearchBtn.setVisibility(View.GONE);

        initIndicator();
        initViewPager();
        initDrawerShow();


        settingButton.setOnClickListener(this);
        logoutButton.setOnClickListener(this);
        mDrawerBtn.setOnClickListener(this);
        mHeadIv.setOnClickListener(this);
        mSearchBtn.setOnClickListener(this);
    }

    private void initDrawerShow()
    {

        String name = LoginMgr.getInstance().getAccount();
        String number = LoginMgr.getInstance().getSipNumber();

        displayName.setText(name);
        sipNumber.setText(number);
    }

    private void initViewPager()
    {
        if (mCallFragment == null)
        {
            mCallFragment = new CallFragment();
        }

        if (mContactFragment == null)
        {
            mContactFragment = new ContactFragment();
        }

        if (mDiscoverFragment == null)
        {
            mDiscoverFragment = new DiscoverFragment();
        }

        fragments.clear();
        fragments.add(mCallFragment);
        fragments.add(mContactFragment);
        fragments.add(mDiscoverFragment);

        FragmentAdapter adapter = new FragmentAdapter(getFragmentManager());
        adapter.setData(fragments);
        mViewPager.setAdapter(adapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float offset, int offsetPixels)
            {
                mCurrentPosition = position;
            }

            @Override
            public void onPageSelected(int position)
            {
                setTabSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int i)
            {
            }
        });
    }

    private void setTabSelected(int position)
    {
        for (int i = 0; i < mMainTabs.size(); i++)
        {
            mMainTabs.get(i).setSelected(position == i);
        }
    }

    @Override
    public void initializeData()
    {
        mMyAccount = LoginCenter.getInstance().getAccount();
    }

    private void initIndicator()
    {
        mMainTabs.add(mCallTab);
        mMainTabs.add(mContactTab);
        mMainTabs.add(mDiscoverTab);

        mCallTab.setSelected(true);

        for (int i = 0; i < mMainTabs.size(); i++)
        {
            final ImageView tab = mMainTabs.get(i);

            tab.setOnTouchListener(new View.OnTouchListener()
            {
                @Override
                public boolean onTouch(View v, MotionEvent event)
                {
                    if (event.getAction() == MotionEvent.ACTION_UP)
                    {
                        mCurrentPosition = Integer.parseInt((String) v.getTag());
                        mViewPager.setCurrentItem(mCurrentPosition);
                    }
                    return true;
                }
            });
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.logout_btn:
                showLogoutDialog();
                break;
            case R.id.nav_iv:
                mDrawerLayout.openDrawer(Gravity.LEFT);
                break;
            case R.id.iv_setting:
                ActivityUtil.startActivity(MainActivity.this, IntentConstant.SERVICE_SETTING_ACTIVITY_ACTION);
                break;
            case R.id.right_img:
                ActivityUtil.startActivity(MainActivity.this, IntentConstant.IM_SEARCH_ACTIVITY_ACTION);
                break;
            case R.id.iv_change_pwd:
                showAddMemberDialog();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    public void onBackPressed()
    {
        Log.i(UIConstants.DEMO_TAG, "on back pressed , logout!");
        showLogoutDialog();
    }

    private void showLogoutDialog()
    {
        if (null == mLogoutDialog)
        {
            mLogoutDialog = new ConfirmDialog(this,R.string.sure_logout);
            mLogoutDialog.setRightButtonListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Log.i(UIConstants.DEMO_TAG, "logout");
                    ContactMgr.getInstance().stopLdapContactsServer();
                    LoginMgr.getInstance().logout();
                }
            });
        }
        mLogoutDialog.show();
    }

    private void showAddMemberDialog()
    {
        final ThreeInputDialog addMemberDialog = new ThreeInputDialog(this);
        addMemberDialog.setRightButtonListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (TextUtils.isEmpty(addMemberDialog.getInput1())
                        || TextUtils.isEmpty(addMemberDialog.getInput2()))
                {
                    showToast(R.string.invalid_password);
                    return;
                }

                LoginMgr.getInstance().changePassword(
                        addMemberDialog.getInput1(),
                        addMemberDialog.getInput2(),
                        addMemberDialog.getInput3());
            }
        });
        addMemberDialog.setHint1(R.string.new_password);
        addMemberDialog.setHint2(R.string.old_password);
        addMemberDialog.setHint3(R.string.input_account);
        addMemberDialog.show();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (null != mLogoutDialog)
        {
            mLogoutDialog.dismiss();
        }
    }

}
