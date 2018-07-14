package com.huawei.opensdk.ec_sdk_demo.logic.login;

/**
 * This interface is about the contract between the view and the presenter
 */
public interface ILoginContract
{

    interface LoginBaseView
    {

        void dismissLoginDialog();

        void setEditText(String account, String password);

        void showToast(int resId);

    }

    interface LoginBaserPresenter
    {
        void onLoginParams();

        void doLogin(String userName, String password);

        void initServerData();
    }

}
