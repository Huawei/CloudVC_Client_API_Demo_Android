package com.huawei.opensdk.ec_sdk_demo.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class FragmentAdapter extends FragmentPagerAdapter
{
    public void setData(List<Fragment> fragments)
    {
        this.fragments = fragments;
    }

    private List<Fragment> fragments;

    public FragmentAdapter(FragmentManager fm)
    {
        super(fm);
        fragments = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int i)
    {
        return fragments.get(i);
    }

    @Override
    public int getCount()
    {
        return fragments.size();
    }
}
