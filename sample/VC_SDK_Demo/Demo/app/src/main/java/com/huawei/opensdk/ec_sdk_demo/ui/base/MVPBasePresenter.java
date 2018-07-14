package com.huawei.opensdk.ec_sdk_demo.ui.base;


/**
 * This abstract class is about MVP base presenter .
 */
public abstract class MVPBasePresenter<V>
{

    /**
     * View interface weak reference
     */
    protected V mView;

    public void attachView(V view)
    {
        mView = view;
    }

    protected V getView()
    {
        return mView;
    }

    public boolean isViewAtteached()
    {
        return mView != null;
    }

    public void detachView()
    {
        if (null != mView)
        {
            mView = null;
        }
    }
}
