package com.huawei.opensdk.ec_sdk_demo.ui.base;

import android.os.Bundle;

/**
 * This abstract class is about MVP base activity.
 */
public abstract class MVPBaseActivity<V, P extends MVPBasePresenter<V>> extends BaseActivity
{
    protected P mPresenter;
    protected V mView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        mPresenter = createPresenter();
        mView = createView();
        mPresenter.attachView(mView);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mPresenter.detachView();
    }

    protected abstract V createView();

    protected abstract P createPresenter();
}
