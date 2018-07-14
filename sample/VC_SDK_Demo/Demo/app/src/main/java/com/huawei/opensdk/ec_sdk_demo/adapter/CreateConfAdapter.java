package com.huawei.opensdk.ec_sdk_demo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huawei.opensdk.demoservice.Member;
import com.huawei.opensdk.ec_sdk_demo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * This adapter is about create conf
 * 创建会议适配层
 */
public class CreateConfAdapter extends BaseAdapter
{
    private TextView numberTV;
    private TextView AccountTV;
    private TextView nameTV;

    private List<Member> memberList = new ArrayList<>();
    private Context context;

    public CreateConfAdapter(Context context)
    {
        this.context = context;
    }

    @Override
    public int getCount()
    {
        return memberList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return memberList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.simple_item, null);
        }
        nameTV = (TextView) convertView.findViewById(R.id.name_tv);
        numberTV = (TextView) convertView.findViewById(R.id.number_tv);
        AccountTV = (TextView) convertView.findViewById(R.id.account_tv);
        nameTV.setText("Name:" + memberList.get(position).getDisplayName());
        numberTV.setText("Number:" + memberList.get(position).getNumber());
        AccountTV.setText("Account:" + memberList.get(position).getAccountId());
        return convertView;
    }

    public void setData(List<Member> memberList)
    {
        this.memberList = memberList;
    }
}
