package com.huawei.opensdk.ec_sdk_demo.ui.discover;

import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.logic.conference.mvp.ConfCtrlContract;
import com.huawei.opensdk.ec_sdk_demo.logic.conference.mvp.ConfCtrlPresenter;
import com.huawei.opensdk.ec_sdk_demo.logic.eaddrbook.EnterpriseAddressBookContract;
import com.huawei.opensdk.ec_sdk_demo.logic.eaddrbook.EnterpriseAddressBookPresenter;
import com.huawei.opensdk.ec_sdk_demo.ui.base.AbsFragment;
import com.huawei.opensdk.ec_sdk_demo.util.ActivityUtil;
import com.huawei.opensdk.ec_sdk_demo.widget.BottomLineLayout;

public class DiscoverFragment extends AbsFragment implements View.OnClickListener,
        ConfCtrlContract.ConfView, EnterpriseAddressBookContract.EAddrBookView
{

    private ConfCtrlContract.ConfPresenter confPresenter;
    private EnterpriseAddressBookContract.EnterprisePresenter enterprisePresenter;
    private BottomLineLayout confListItem;
    private BottomLineLayout contactListItem;

    @Override
    public int getLayoutId()
    {
        return R.layout.discover_fragment;
    }

    @Override
    public void onViewCreated()
    {
        super.onViewCreated();
        initData();
        initView();
    }

    private void initView()
    {
        confListItem = (BottomLineLayout) mView.findViewById(R.id.conference_entry_item);
        contactListItem = (BottomLineLayout) mView.findViewById(R.id.eaddr_book_entry_item);
        confListItem.setOnClickListener(this);
        contactListItem.setOnClickListener(this);
    }

    private void initData()
    {
        confPresenter = new ConfCtrlPresenter(this);
        enterprisePresenter = new EnterpriseAddressBookPresenter(this);
    }


    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.conference_entry_item:
                confPresenter.gotoConfList();
                break;
            case R.id.eaddr_book_entry_item:
                enterprisePresenter.gotoEAddrBookEntry();
                break;
            default:
                break;
        }
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
    public void showCustomToast(int resID)
    {
        Toast.makeText(getActivity(), getString(resID), Toast.LENGTH_SHORT).show();
    }


    @Override
    public void doStartActivity(Intent intent)
    {
        ActivityUtil.startActivity(getActivity(), intent);
    }
}
