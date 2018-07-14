package com.huawei.opensdk.ec_sdk_demo.logic;

public interface BaseView
{
    //声明公共的一些方法
    void showLoading();//显示加载进度条
    void dismissLoading();//关闭加载进度条

    void showCustomToast(int resID);
}
