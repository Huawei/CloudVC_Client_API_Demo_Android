package com.huawei.opensdk.ec_sdk_demo.widget.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public abstract class SettingBaseAdapter extends BaseAdapter
{   
    /**
     * 数据集合
     */
    protected List<Object> data;
    
    /**
     * 加载view
     */
    protected LayoutInflater inflater;
    
    public SettingBaseAdapter(Context context)
    {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        init();
    }
    
    public SettingBaseAdapter(Context context, List<Object> data)
    {
        this.data = data;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        init();
    }
    
    private void init()
    {
        if (null == data)
        {
            data = new ArrayList<Object>();
        }
    }

    @Override
    public int getCount()
    {
        return data.size();
    }
    
    @Override
    public Object getItem(int position)
    {
        return data.get(position);
    }
    
    @Override
    public long getItemId(int position)
    {
        return position;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        return null;
    }
}
