package com.huawei.opensdk.ec_sdk_demo.ui.servicesetting;

import android.view.View;

import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.ui.base.BaseActivity;

public class ServiceSettingActivity extends BaseActivity implements View.OnClickListener {

    @Override
    public void initializeComposition() {
        setContentView(R.layout.activity_service_setting);
    }

    @Override
    public void initializeData() {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id)
        {
        }
    }
}
