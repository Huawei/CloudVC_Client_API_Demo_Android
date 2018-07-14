package com.huawei.opensdk.ec_sdk_demo.adapter;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huawei.opensdk.demoservice.ConfConstant;
import com.huawei.opensdk.demoservice.Member;
import com.huawei.opensdk.ec_sdk_demo.R;

import java.util.List;

/**
 * This adapter is about conf manager
 * 会议管理适配层
 */
public class ConfManagerAdapter extends BaseAdapter
{

    private Context context;
    private List<Member> conferenceMemberEntityList;

    public ConfManagerAdapter(Context context)
    {
        this.context = context;
    }

    public void setData(List<Member> conferenceMemberEntityList)
    {
        this.conferenceMemberEntityList = conferenceMemberEntityList;
    }

    @Override
    public int getCount()
    {
        return conferenceMemberEntityList == null ? 0 : conferenceMemberEntityList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return conferenceMemberEntityList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder;
        if (convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.conf_manager_item, null);
            viewHolder = new ViewHolder();
            viewHolder.chairmanPicIV = (ImageView) convertView.findViewById(R.id.chairman_pic);
            viewHolder.memberNameTV = (TextView) convertView.findViewById(R.id.member_name_tv);
            viewHolder.memberNumberTV = (TextView) convertView.findViewById(R.id.member_number_tv);
            viewHolder.callingStateIV = (ImageView) convertView.findViewById(R.id.calling_state_iv);
            viewHolder.handUpIV = (ImageView) convertView.findViewById(R.id.hand_up_iv);
            viewHolder.presenterIV = (ImageView) convertView.findViewById(R.id.presenter_pic);
            viewHolder.dataConfIV = (ImageView) convertView.findViewById(R.id.data_conf_iv);
            convertView.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) convertView.getTag();

        if (null == conferenceMemberEntityList || 0 == conferenceMemberEntityList.size())
        {
            return convertView;
        }
        Member conferenceMemberEntity = conferenceMemberEntityList.get(position);
        viewHolder.memberNameTV.setText(conferenceMemberEntity.getDisplayName());
        viewHolder.memberNumberTV.setText(conferenceMemberEntity.getNumber());
        boolean isChairMan = (conferenceMemberEntity.getRole() == ConfConstant.ConfRole.CHAIRMAN);
        viewHolder.chairmanPicIV.setVisibility(isChairMan ? View.VISIBLE : View.INVISIBLE);
        viewHolder.handUpIV.setVisibility(conferenceMemberEntity.isHandUp() ? View.VISIBLE : View.GONE);

        updateMemberStatus(viewHolder, conferenceMemberEntity);

        return convertView;

    }

    private void updateMemberStatus(ViewHolder viewHolder, Member conferenceMemberEntity)
    {
        ImageView stateIV = viewHolder.callingStateIV;
        viewHolder.callingStateIV.setBackgroundResource(R.anim.conf_calling_animation);
        AnimationDrawable animationDrawable;

        switch (conferenceMemberEntity.getStatus())
        {
            case CALLING:
                animationDrawable = (AnimationDrawable) stateIV.getBackground();
                stateIV.post(new MyRunnable(animationDrawable));
                break;
            case IN_CONF:
                if (conferenceMemberEntity.isMute())
                {
                    stateIV.setBackgroundResource(R.drawable.conf_calling_mute);
                    stateIV.setContentDescription("conf_calling_mute");
                }
                else
                {
                    animationDrawable = (AnimationDrawable) stateIV.getBackground();
                    stateIV.setBackgroundResource(R.drawable.conf_calling_success);
                    stateIV.setContentDescription("conf_calling_success");
                    animationDrawable.stop();
                }
                break;
            case LEAVED:
                stateIV.setBackgroundResource(R.drawable.conf_calling_leave);
                stateIV.setContentDescription("conf_calling_leave");
                break;
            default:
                stateIV.setBackgroundResource(R.drawable.conf_calling_leave);
                stateIV.setContentDescription("conf_calling_leave");
                break;

        }

        if (conferenceMemberEntity.isPresent())
        {
            viewHolder.presenterIV.setVisibility(View.VISIBLE);
        }
        else
        {
            viewHolder.presenterIV.setVisibility(View.GONE);
        }

        if (conferenceMemberEntity.isInDataConference())
        {
            viewHolder.dataConfIV.setVisibility(View.VISIBLE);
        }
        else
        {
            viewHolder.dataConfIV.setVisibility(View.GONE);
        }
    }

    private static class MyRunnable implements Runnable
    {
        private AnimationDrawable animationDrawable;

        public MyRunnable(AnimationDrawable drawable)
        {
            this.animationDrawable = drawable;
        }

        @Override
        public void run()
        {
            animationDrawable.start();
        }
    }

    private static class ViewHolder
    {
        public ImageView chairmanPicIV;
        public TextView memberNameTV;
        public TextView memberNumberTV;
        public ImageView callingStateIV;
        public ImageView handUpIV;
        public ImageView presenterIV;
        public ImageView dataConfIV;
    }
}
