package com.huawei.opensdk.ec_sdk_demo.ui.conference;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.huawei.opensdk.demoservice.ConfBaseInfo;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.adapter.ConfListAdapter;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.logic.conference.mvp.ConfListPresenter;
import com.huawei.opensdk.ec_sdk_demo.logic.conference.mvp.IConfListContract;
import com.huawei.opensdk.ec_sdk_demo.ui.IntentConstant;
import com.huawei.opensdk.ec_sdk_demo.ui.base.MVPBaseActivity;
import com.huawei.opensdk.ec_sdk_demo.util.ActivityUtil;
import com.huawei.opensdk.ec_sdk_demo.widget.ThreeInputDialog;

import java.util.List;

public class ConfListActivity extends MVPBaseActivity<IConfListContract.ConfListView, ConfListPresenter> implements IConfListContract.ConfListView, View.OnClickListener
{
    private ConfListPresenter mPresenter;
    private ConfListAdapter adapter;
    private ListView listView;
    private ImageView rightIV;
    private ImageView directJoinConfIV;

    @Override
    protected IConfListContract.ConfListView createView()
    {
        return this;
    }

    @Override
    protected ConfListPresenter createPresenter()
    {
        mPresenter = new ConfListPresenter();
        return mPresenter;
    }

    @Override
    public void initializeComposition()
    {
        setContentView(R.layout.conference_list_layout);
        listView = (ListView) findViewById(R.id.conference_list);
        rightIV = (ImageView) findViewById(R.id.right_img);
        directJoinConfIV = (ImageView) findViewById(R.id.join_conf_iv);

        //TODO
        directJoinConfIV.setVisibility(View.VISIBLE);
        directJoinConfIV.setImageResource(R.drawable.join_conf_by_number_icon);
        directJoinConfIV.setOnClickListener(this);

        initRightIV();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                mPresenter.onItemClick(position);
            }
        });
    }

    private void initRightIV()
    {
        rightIV.setImageResource(R.drawable.icon_create);
        rightIV.setOnClickListener(this);
        rightIV.setVisibility(View.VISIBLE);
    }

    @Override
    public void initializeData()
    {
        adapter = new ConfListAdapter(this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public void showLoading()
    {

    }

    @Override
    public void dismissLoading()
    {

    }

    @Override
    public void showCustomToast(final int resID)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                showToast(resID);
            }
        });
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.right_img:
                Intent intent = new Intent(IntentConstant.CREATE_CONF_ACTIVITY_ACTION);
                ActivityUtil.startActivity(this, intent);
                break;
            case R.id.join_conf_iv:
                showJoinConfDialog();
                break;
            default:
                break;
        }
    }

    private void showJoinConfDialog()
    {
        final ThreeInputDialog editDialog = new ThreeInputDialog(this);
        editDialog.setTitle(R.string.join_conf);
        editDialog.setRightButtonListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mPresenter.joinReserveConf(editDialog.getInput1(), editDialog.getInput2(), editDialog.getInput3());
            }
        });
        editDialog.setHint1(R.string.conf_id_input);
        editDialog.setHint2(R.string.access_code_input);
        editDialog.setHint3(R.string.password_code_input);
        editDialog.show();
    }

    @Override
    public void refreshConfList(final List<ConfBaseInfo> confBaseInfoList)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                adapter.setData(confBaseInfoList);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void gotoConfDetailActivity(String confID)
    {
        Intent intent = new Intent(IntentConstant.CONF_DETAIL_ACTIVITY_ACTION);
        intent.putExtra(UIConstants.CONF_ID, confID);
        ActivityUtil.startActivity(this, intent);
    }
}
