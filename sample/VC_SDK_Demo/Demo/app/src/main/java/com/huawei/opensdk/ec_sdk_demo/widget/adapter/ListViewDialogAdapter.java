package com.huawei.opensdk.ec_sdk_demo.widget.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.huawei.opensdk.ec_sdk_demo.R;

import java.util.List;

/**
 * This class is about create simple ListView Style dialog box
 * 简单listView风格对话框
 */
public class ListViewDialogAdapter extends SettingBaseAdapter
{   
    /**
     * 是否含有对勾
     */
    private boolean isHaveGou;
    
    /**
     * 初始默认索引
     */
    private int index = -1;
    
    public ListViewDialogAdapter(Context context, List<Object> data)
    {
        super(context, data);
    }

    @Override
    public View getView(int position, View convertView1, ViewGroup parent)
    {
        View view = null;
        if (isHaveGou)
        {
            view = inflater.inflate(R.layout.dialog_whitestyle_lv_item2, parent, false);
            TextView displayItem = (TextView) view.findViewById(R.id.dialog_item_textview);
            ImageView imageView = (ImageView) view.findViewById(R.id.dialog_item_imageview);

            displayItem.setText((String) data.get(position));
            if (position == index)
            {
                imageView.setImageResource(R.drawable.btn_gou_white);
            }
            else
            {
                imageView.setImageDrawable(null);
            }
        }
        else
        {
            view = inflater.inflate(R.layout.whitestyle_lv_item, parent, false);
            TextView displayItem = (TextView) view.findViewById(R.id.list_item_tv);
            displayItem.setText((String) data.get(position));
        }
        // 设置点击效果
        int background = R.drawable.bg_dialog_selector;
        view.setBackgroundResource(background);
        return view;
    }
    
    public void setHaveGou(boolean isHaveGou)
    {
        this.isHaveGou = isHaveGou;
    }
    
    public void setIndex(int index)
    {
        this.index = index;
    }
    
}
