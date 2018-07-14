package com.huawei.opensdk.commonservice.localbroadcast;

/**
 * This interface is about local broadcast callback reception.
 * 本地广播接收回调接口
 */
public interface LocBroadcastReceiver
{
    /**
     * This method is used to receive a broadcast message.
     * 接收广播消息
     * @param broadcastName Indicates registered broadcast name
     *                      注册的广播名称
     * @param obj           Indicates sent data
     *                      要接收的数据
     */
    void onReceive(String broadcastName, Object obj);
}
