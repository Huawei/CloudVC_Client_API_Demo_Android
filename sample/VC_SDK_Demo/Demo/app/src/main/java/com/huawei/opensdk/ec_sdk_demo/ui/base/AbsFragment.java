package com.huawei.opensdk.ec_sdk_demo.ui.base;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

public abstract class AbsFragment extends Fragment
{
    protected View mView;
    protected Context context;

    public abstract int getLayoutId();

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        this.context = activity;
    }

    @Override
    public final View onCreateView(LayoutInflater inflater
            , ViewGroup container, Bundle savedInstanceState)
    {
        if (mView != null)
        {
            return mView;
        }
        return inflater.inflate(getLayoutId(), container, false);
    }

    @Override
    public final void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        if (mView == null)
        {
            mView = view;
            onViewLoad();
            onDataLoad();
        }
        onViewCreated();
    }

    /**
     * 只在初始化时调用一次
     * before {@link #onDataLoad()}
     */
    public void onViewLoad()
    {
    }

    /**
     * 只在初始化时调用一次
     * after {@link #onViewLoad()}
     * before {@link #onViewCreated()}
     */
    public void onDataLoad()
    {
    }

    /**
     * Fragment生命周期Created后调用
     * {@link #onViewDestroyed()}
     */
    public void onViewCreated()
    {
    }

    @Override
    public final void onDestroyView()
    {
        super.onDestroyView();

        onViewDestroyed();
        ViewParent vp = mView.getParent();
        if (vp instanceof ViewGroup)
        {
            ((ViewGroup) vp).removeView(mView);
        }
    }

    /**
     * 视图已经被销毁了
     * {@link #onViewCreated()}
     */
    void onViewDestroyed()
    {
    }
}
