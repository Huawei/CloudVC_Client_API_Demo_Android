package com.huawei.opensdk.ec_sdk_demo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huawei.opensdk.contactmgr.ContactsInfo;
import com.huawei.opensdk.ec_sdk_demo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * This adapter is about contacts list
 * 联系人列表适配层
 */
public class ContactListAdapter extends BaseAdapter {

    private Context context;
    private ContactsInfo contactInfo;
    private List<ContactsInfo> contactsList = new ArrayList<>();

    public ContactListAdapter(Context context) {
        this.context = context;
    }

    public void setDate(List<ContactsInfo> list)
    {
        this.contactsList = list;
        notifyDataSetChanged();
    }

    static final class ContactView
    {
        private ImageView ivHead;
        private TextView tvName;
        private TextView tvPhone;
        private TextView tvId;
    }

    @Override
    public int getCount() {
        return contactsList.size();
    }

    @Override
    public Object getItem(int position) {
        return contactsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ContactView contactView;

        if (null == convertView)
        {
            contactView = new ContactView();
            convertView = LayoutInflater.from(context).inflate(R.layout.contact_list,null);
            contactView.ivHead = (ImageView) convertView.findViewById(R.id.contact_list_head);
            contactView.tvName = (TextView) convertView.findViewById(R.id.contact_list_name);
            contactView.tvPhone = (TextView) convertView.findViewById(R.id.contact_list_phone);
            contactView.tvId = (TextView) convertView.findViewById(R.id.contact_list_id);
            convertView.setTag(contactView);
        }
        else
        {
            contactView = (ContactView) convertView.getTag();
        }

        contactInfo = contactsList.get(position);
        contactView.ivHead.setBackgroundResource(R.drawable.tree_contact_head);
        contactView.tvName.setText(contactInfo.getName());
        contactView.tvPhone.setText(contactInfo.getPhone());
        contactView.tvId.setText(contactInfo.getId());

        return convertView;
    }
}
