package com.huawei.opensdk.commonservice.util;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.huawei.opensdk.commonservice.common.LocContext;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;

/**
 * This class is about device manager util.
 * 设备管理 util类
 */
public final class DeviceManager
{
    private static final String TAG = DeviceManager.class.getSimpleName();

    /**
     * This is a constructor of DeviceManager class.
     * 构造方法
     */
    private DeviceManager()
    {
    }

    // save screen size
    private static final float mDensity;

    private static final int mScreenWidth;

    private static final int mScreenHeight;

    static
    {
        Resources resources = Resources.getSystem();
        DisplayMetrics metrics = resources.getDisplayMetrics();

        mDensity = metrics.density;
        mScreenWidth = metrics.widthPixels;
        mScreenHeight = metrics.heightPixels;
    }

    public static float getmDensity()
    {
        return mDensity;
    }

    public static int getmScreenWidth()
    {
        return mScreenWidth;
    }

    public static int getmScreenHeight()
    {
        return mScreenHeight;
    }

    /**
     * This method is used to is ip address boolean.
     * 是否为本地ip地址
     * @param iPAddress Indicates the p address
     *                  本地ip地址
     * @return boolean If yes return TRUE, otherwise return false
     *                 是返回TRUE，否则返回false
     */
    public static boolean isIPAddress(String iPAddress)
    {
        Pattern p = null;
        if (iPAddress.contains(":"))
        {
            p = Pattern.compile("^((25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\\.){3}"
                    + "(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9]):[0-9]{2,5}$");
        }
        else
        {
            p = Pattern.compile("^((25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\\.){3}"
                    + "(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])$");
        }

        return p.matcher(iPAddress).matches();
    }

    /**
     * This method is used to get vpn local ip.
     * 获取本地VPN ip地址
     * @return String  Return the vpn local ip
     *                 返回本地VPN ip地址
     */
    public static String getVpnLocalIp()
    {
        String ip = "";
        try
        {
            List<NetworkInterface> networkInterfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface networkInterface : networkInterfaces)
            {
                String displayName = networkInterface.getDisplayName();
                List<InterfaceAddress> addresses = networkInterface.getInterfaceAddresses();
                if (displayName.equals("ppp0") && addresses.size() > 0)
                {
                    ip = addresses.get(0).getAddress().getHostAddress();
                    Log.d(TAG, "ip = " + ip);
                    if (!TextUtils.isEmpty(ip))
                    {
                        break;
                    }
                }
            }
            return ip;
        }
        catch (Exception e)
        {
            LogUtil.e(TAG, e.getMessage());
        }
        return null;
    }

    /**
     * This method is used to get ip address.
     * 获取本地ip地址
     * @return String Return ip address
     *                返回本地ip地址
     */
    public static String getLocalIp()
    {
        String ip = "";
        try
        {
            Enumeration<NetworkInterface> networkInfo = NetworkInterface
                    .getNetworkInterfaces();
            NetworkInterface intf = null;
            Enumeration<InetAddress> intfAddress = null;
            InetAddress inetAddress = null;
            if (networkInfo == null)
            {
                LogUtil.d("getLocalIp",
                        "get LocalIp address Error , return null value ");
                return "";
            }
            for (Enumeration<NetworkInterface> en = networkInfo; en
                    .hasMoreElements(); )
            {
                intf = en.nextElement();
                intfAddress = intf.getInetAddresses();
                for (Enumeration<InetAddress> enumIpAddr = intfAddress; enumIpAddr
                        .hasMoreElements(); )
                {
                    inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress())
                    {
                        ip = inetAddress.getHostAddress();
                        if (isIPV4Addr(ip))
                        {
                            LogUtil.i("getLocalIp", "ip is " + ip);
                            return ip;
                        }
                    }
                }
            }
        }
        catch (SocketException e)
        {
            LogUtil.e(TAG, e.getMessage());
        }
        return ip;
    }

    /**
     * This method is used to is ipv4 address.
     * 是否为ipv4 地址
     * @param ipAddr Indicates ip address
     *               ip地址
     * @return boolean If yes return TRUE, otherwise return false
     *                 是返回TRUE，否则返回false
     */
    private static boolean isIPV4Addr(String ipAddr)
    {
        Pattern p = Pattern.compile("^((25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\\.){3}"
                + "(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])$");
        return p.matcher(ipAddr).matches();
    }

    /**
     * This method is used to is network available boolean.
     * 网络是否可用
     * @param context Indicates the context
     *                上下文
     * @return boolean If yes return TRUE, otherwise return false
     *                 是返回TRUE，否则返回false
     */
    public static boolean isNetworkAvailable(Context context)
    {
        ConnectivityManager cm = (ConnectivityManager) context.
                getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null)
        {
            NetworkInfo[] info = cm.getAllNetworkInfo();
            if (info != null)
            {
                for (int i = 0; i < info.length; ++i)
                {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * This method is used to get the ip address.
     * 获取本地ip地址
     */
    public static String getLocalIpAddress(boolean isVPN)
    {
        return !isVPN ? DeviceManager.getLocalIp() : DeviceManager.getVpnLocalIp();
    }


    /**
     * This method is used to is PadScreen
     *
     * @return boolean
     */
    public static boolean isPhone()
    {
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager wm = (WindowManager) LocContext.getContext().
                getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;
        int height = metric.heightPixels;
        float xdpi = metric.xdpi;
        float ydpi = metric.ydpi;
        float density = metric.density;
        double size = Math.sqrt(Math.pow(width / xdpi, 2) + Math.pow(height / ydpi, 2));
        LogUtil.i(TAG, "screen size: " + size);
        if (size > 6.600000 && Math.max(width, height) / density > 900)
        {
            return false;
        }
        return true;
    }
}
