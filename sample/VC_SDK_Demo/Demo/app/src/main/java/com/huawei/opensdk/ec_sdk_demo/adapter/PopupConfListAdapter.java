package com.huawei.opensdk.ec_sdk_demo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huawei.opensdk.demoservice.Member;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.tup.confctrl.ConfctrlConfRole;

import java.util.ArrayList;
import java.util.List;

/**
 * This adapter is about display conf list
 * 显示会议列表适配层
 */
public class PopupConfListAdapter extends BaseAdapter
{
    private List<Member> memberList = new ArrayList<>();
    private Context context;

    public PopupConfListAdapter(Context context)
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
        Member entity = memberList.get(position);
        VideoConfViewHolder viewHolder;
        if (convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.popup_video_conf_list_item, null);
            viewHolder = new VideoConfViewHolder();
            viewHolder.hostLogo = (ImageView) convertView.findViewById(R.id.host_logo);
            viewHolder.nameTv = (TextView) convertView.findViewById(R.id.name_tv);
            viewHolder.videoStatus = (RelativeLayout) convertView.findViewById(R.id.video_status_layout);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (VideoConfViewHolder) convertView.getTag();
        }
        String displayName = entity.getDisplayName();
        viewHolder.nameTv.setText(displayName);
        viewHolder.hostLogo.setVisibility(entity.getRole().equals(
                ConfctrlConfRole.CONFCTRL_E_CONF_ROLE_CHAIRMAN) ? View.VISIBLE : View.GONE);
        return convertView;
    }

    public void setData(List<Member> memberList)
    {
        this.memberList = memberList;
    }

    private static class VideoConfViewHolder
    {
        public TextView nameTv;
        public ImageView hostLogo;
        public RelativeLayout videoStatus;
    }
}
