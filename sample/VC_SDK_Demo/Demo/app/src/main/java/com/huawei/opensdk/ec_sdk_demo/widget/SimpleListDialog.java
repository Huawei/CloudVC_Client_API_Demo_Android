package com.huawei.opensdk.ec_sdk_demo.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.huawei.opensdk.commonservice.common.LocContext;
import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.widget.adapter.ListViewDialogAdapter;

import java.util.List;

/**
 * This class is about simple list dialog.
 */
public class SimpleListDialog extends Dialog
{
    ListView listView;

    protected SimpleListDialog(Context context)
    {
        super(context, R.style.Theme_dialog);

        setContentView(R.layout.dialog_whitestyle_lv);
        listView = (ListView) findViewById(R.id.dialog_listview);

        setCanceledOnTouchOutside(true);
        setOnDismissListener(null);
    }

    /**
     * 简单的数据显示类
     */
    public SimpleListDialog(Context context, List<Object> data)
    {
        this(context);
        listView.setPadding(0, 0, 0, 0);
        listView.setDividerHeight(1);
        ViewGroup baseLayout = (ViewGroup) findViewById(R.id.base_layout);
        baseLayout.setBackgroundDrawable(LocContext.getContext().getResources().getDrawable(R.drawable.bg_popup_window));
        ListViewDialogAdapter adapter = new ListViewDialogAdapter(context, data);
        listView.setAdapter(adapter);
    }

    /**
     * 设置listview的item点击事件
     */
    public void setOnItemClickListener(AdapterView.OnItemClickListener listener)
    {
        if (listener == null)
        {
            LogUtil.i(UIConstants.DEMO_TAG, "listener is null, please check!");
            return;
        }

        listView.setOnItemClickListener(listener);
    }

}
