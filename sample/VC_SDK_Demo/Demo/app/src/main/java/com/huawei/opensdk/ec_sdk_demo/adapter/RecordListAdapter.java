package com.huawei.opensdk.ec_sdk_demo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huawei.common.CallRecordInfo;
import com.huawei.opensdk.contactmgr.RecordsInfo;
import com.huawei.opensdk.ec_sdk_demo.R;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * This adapter is about call record list
 * 通话记录列表适配层
 */
public class RecordListAdapter extends BaseAdapter {

    List<RecordsInfo> list;
    Context context;
    RecordsInfo recordsInfo;

    public RecordListAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<RecordsInfo> data) {
        this.list = data;
        notifyDataSetChanged();
    }


    static class CallRecordView
    {
        private ImageView ivCallType;
        private TextView tvName;
        private TextView tvPhone;
        private TextView tvIsAudio;
        private TextView tvTime;
        private TextView tvId;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CallRecordView callRecordView;
        if (null == convertView)
        {
            callRecordView = new CallRecordView();
            convertView = LayoutInflater.from(context).inflate(R.layout.call_record_list,null);
            callRecordView.ivCallType = (ImageView) convertView.findViewById(R.id.call_record_list_image);
            callRecordView.tvName = (TextView) convertView.findViewById(R.id.call_record_list_name);
            callRecordView.tvPhone = (TextView) convertView.findViewById(R.id.call_record_list_number);
            callRecordView.tvIsAudio = (TextView) convertView.findViewById(R.id.call_record_list_audio_video);
            callRecordView.tvTime = (TextView) convertView.findViewById(R.id.call_record_list_time);
            callRecordView.tvId = (TextView) convertView.findViewById(R.id.call_record_list_id);
            convertView.setTag(callRecordView);
        }
        else
        {
            callRecordView = (CallRecordView) convertView.getTag();
        }

        recordsInfo = list.get(position);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm");
        String dateStart = dateFormat.format(recordsInfo.getCallStartTime());
        callRecordView.tvName.setText(dateStart);
        callRecordView.tvPhone.setText(recordsInfo.getNumber());
        if (recordsInfo.getCallType() == CallRecordInfo.DialType.AUDIO)
        {
            callRecordView.tvIsAudio.setText("AUDIO");
        }
        else if (recordsInfo.getCallType() == CallRecordInfo.DialType.VIDEO)
        {
            callRecordView.tvIsAudio.setText("VIDEO");
        }
        if (recordsInfo.getRecordType() == CallRecordInfo.RecordType.CALL_RECORD_IN)
        {
            callRecordView.ivCallType.setBackgroundResource(R.drawable.tree_in_coming_call);
        }
        else if (recordsInfo.getRecordType() == CallRecordInfo.RecordType.CALL_RECORD_OUT)
        {
            callRecordView.ivCallType.setBackgroundResource(R.drawable.tree_out_going_call);
        }
        else if (recordsInfo.getRecordType() == CallRecordInfo.RecordType.CALL_RECORD_MISS)
        {
            callRecordView.ivCallType.setBackgroundResource(R.drawable.tree_miss_call);
        }
        callRecordView.tvTime.setText(recordsInfo.getInterval());
        callRecordView.tvId.setText(String.valueOf(recordsInfo.getRecordId()));
        return convertView;
    }
}
