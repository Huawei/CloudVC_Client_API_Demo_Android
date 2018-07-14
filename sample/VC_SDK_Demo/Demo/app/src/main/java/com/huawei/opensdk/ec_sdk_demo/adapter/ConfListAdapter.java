package com.huawei.opensdk.ec_sdk_demo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huawei.opensdk.demoservice.ConfBaseInfo;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * This adapter is about conf list
 * 会议列表适配层
 */
public class ConfListAdapter extends BaseAdapter
{
    private List<ConfBaseInfo> meetingList = new ArrayList<>();
    private Context context;

    public ConfListAdapter(Context context)
    {
        this.context = context;
    }

    @Override
    public int getCount()
    {
        return meetingList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return meetingList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ConfBaseInfo confBaseInfo = meetingList.get(position);
        ConfViewHolder viewHolder;
        if (convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.main_conference_item, null);
            viewHolder = new ConfViewHolder();
            viewHolder.confEmcee = (TextView) convertView.findViewById(R.id.conf_emcee);
            viewHolder.confItem = (RelativeLayout) convertView.findViewById(R.id.conf_item);
            viewHolder.confSubject = (TextView) convertView.findViewById(R.id.conf_name);
            viewHolder.confTime = (TextView) convertView.findViewById(R.id.conf_time);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ConfViewHolder) convertView.getTag();
        }
        viewHolder.confSubject.setText(confBaseInfo.getSubject());
        viewHolder.confTime.setText(DateUtil.getInstance().utcToLocalDate(confBaseInfo.getStartTime(),
                DateUtil.FMT_YMDHM, DateUtil.FMT_YMDHM));
        viewHolder.confEmcee.setText("chairman:" + confBaseInfo.getSchedulerName());
        return convertView;
    }

    public void setData(List<ConfBaseInfo> memberList)
    {
        this.meetingList = memberList;
    }

    private static class ConfViewHolder
    {
        /**
         * 整个item区域
         */
        public RelativeLayout confItem;
        /**
         * 会议名称
         */
        public TextView confSubject;
        /**
         * 会议时间
         */
        public TextView confTime;
        /**
         * 主持人
         */
        public TextView confEmcee;


        public ImageView meetingBack;
    }
}
