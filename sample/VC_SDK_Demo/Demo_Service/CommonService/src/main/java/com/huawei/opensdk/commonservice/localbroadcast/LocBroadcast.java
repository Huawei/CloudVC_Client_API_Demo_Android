package com.huawei.opensdk.commonservice.localbroadcast;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class is about local broadcast registration cancellation category.
 * 本地广播注册取消类
 */
public class LocBroadcast
{

    private static final String TAG = LocBroadcast.class.getSimpleName();

    /**
     * Instance object of LocBroadcast component.
     * 获取一个LocBroadcast对象
     */
    private final static LocBroadcast INS = new LocBroadcast();

    /**
     * Store a collection of broadcast objects and accepted classes
     * 存储广播对象和接受类的集合
     */
    protected final Map<String, LinkedList<LocBroadcastReceiver>> broadcasts = new HashMap<>();

    /**
     * Variable lock
     * 可变锁
     */
    protected final Object broadcastLock = new Object();

    /**
     * Register the broadcast name
     * 注册的广播名称
     */
    private String broadcastName;

    /**
     * This is a constructor of LocBroadcast class.
     * 构造方法
     */
    private LocBroadcast()
    {
    }

    /**
     * This method is used to get instance object of ImMgr.
     * 获取ImMgr对象实例
     * @return ImMgr Return instance object of ImMgr
     *               返回一个ImMgr对象实例
     */
    public static LocBroadcast getInstance()
    {
        return INS;
    }

    public String getBroadcastName()
    {
        return broadcastName;
    }

    /**
     * This method is used to registered broadcast.
     * 注册广播
     * @param receiver Indicates receiver
     *                 广播接收对象
     * @param actions  Indicates accept the action
     *                 注册的广播名称数组
     * @return boolean Return true：registered success；false：registered failed
     *                 返回TRUE表示注册成功，false表示注册失败
     */
    public boolean registerBroadcast(LocBroadcastReceiver receiver, String[] actions)
    {
        if (null == receiver || null == actions)
        {
            return false;
        }

        synchronized (broadcastLock)
        {
            LinkedList<LocBroadcastReceiver> list;

            for (String action : actions)
            {
                list = broadcasts.get(action);

                if (null == list)
                {
                    list = new LinkedList<>();
                    broadcasts.put(action, list);
                }

                if (!list.contains(receiver))
                {
                    list.add(receiver);
                }
            }
        }

        return true;
    }

    /**
     * This method is used to unregistered broadcast.
     * 去注册广播(注销广播)
     * @param receiver Indicates receiver
     *                 广播接收对象
     * @param actions  Indicates accept the action
     *                 注册的广播名称数组
     * @return boolean Return true：unregistered success；false：unregistered failed
     *                 返回TRUE表示注册成功，false表示注册失败
     */
    public boolean unRegisterBroadcast(LocBroadcastReceiver receiver, String[] actions)
    {
        if (null == receiver || null == actions)
        {
            return false;
        }

        List<LocBroadcastReceiver> list;

        synchronized (broadcastLock)
        {
            for (String action : actions)
            {
                list = broadcasts.get(action);

                if (null == list)
                {
                    return false;
                }

                list.remove(receiver);
            }
        }

        return true;
    }

    /**
     * This method is used to send a broadcast message.
     * 发送广播消息
     * @param action Indicates registered broadcast name
     *               注册的广播名称数组
     * @param data   Indicates sent data
     *               要发送的数据
     */
    public void sendBroadcast(String action, Object data)
    {
        if (null == action)
        {
            return;
        }

        this.broadcastName = action;

        synchronized (broadcastLock)
        {
            List<LocBroadcastReceiver> receivers = broadcasts.get(action);
            if (null == receivers || receivers.isEmpty())
            {
                Log.i(TAG, "no receiver for action#" + action);
                return;
            }

            for (LocBroadcastReceiver receiver : receivers)
            {
                if (Looper.getMainLooper() == Looper.myLooper())
                {
                    HANDLER.post(new OnReceiver(receiver, action, data));
                }
                else
                {
                    EXECUTOR.execute(new OnReceiver(receiver, action, data));
                }
            }
        }
    }

    private static final Handler HANDLER = new Handler(Looper.getMainLooper());
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(6);

    private static class OnReceiver implements Runnable
    {
        private final LocBroadcastReceiver receiver;

        private final String action;
        private final Object data;

        public OnReceiver(LocBroadcastReceiver receiver, String action, Object data)
        {
            this.receiver = receiver;
            this.action = action;
            this.data = data;
        }

        @Override
        public void run()
        {
            receiver.onReceive(action, data);
        }
    }

    public static void destroy()
    {
        if (!EXECUTOR.isShutdown())
        {
            EXECUTOR.shutdownNow();
        }
    }
}
