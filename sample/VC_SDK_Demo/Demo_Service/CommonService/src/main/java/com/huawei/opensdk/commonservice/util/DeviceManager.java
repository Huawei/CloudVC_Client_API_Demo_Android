package com.huawei.opensdk.commonservice.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.huawei.opensdk.commonservice.common.LocContext;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
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
     * 判断是否是ipv4地址；解决UADP包循环依赖
     * @param ipAddress 匹配的ip地址，不能为空，否则会空指针
     * @return true ipv4地址，否则false
     */
    public static boolean isIPV4Address(String ipAddress)
    {
        Pattern p = Pattern.compile("^((25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\\.)"
                + "{3}(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])$");
        return p.matcher(ipAddress).matches();
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
                if (null == displayName || displayName.equals(""))
                {
                    Log.e(TAG, "the displayName is null");
                    return null;
                }
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
//                            LogUtil.i("getLocalIp", "ip is " + ip);
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
     * This method is used to get the ip address.
     * 获取本地ip地址
     */
    public static String getLocalIpAddress(boolean isVPN)
    {
        return DeviceManager.getIpAddress(isVPN);
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
	
	/**
     * 获取当前的IP地址，该接口不涉及Svn相关的Ip；
     * @return 当前IP
     * @see
     */
    public static String getIpAddress(boolean enableVpn)
    {
        LogUtil.i(TAG, "Local Address : VPN enable is " + enableVpn);

        // 检查是否有vpn连接，如果是vpn连接的话返回vpn的ip地址
        String vpnIp = getVpnIp();
        if (enableVpn && !TextUtils.isEmpty(vpnIp))
        {
            LogUtil.i(TAG, "Local Address : return VPN " + ipToStars(vpnIp));
            return vpnIp;
        }

        // 检查是否有wifi连接，如果是wifi连接的话返回wifi的ip地址
        String wifiIp = getWifiIp();
        if (!TextUtils.isEmpty(wifiIp))
        {
            if (enableVpn || !wifiIp.equals(vpnIp))
            {
                LogUtil.i(TAG, "Local Address : return WIFI " + ipToStars(wifiIp));
                return wifiIp;
            }
        }

        // 检查是否有网络连接，如果有则返回对应IP。
        List<String> netIps = getNetAddress();
        for (String tempNetIp : netIps)
        {
            if (!enableVpn && vpnIp.equals(tempNetIp))
            {
                continue;
            }

            LogUtil.i(TAG, "Local Address : return NetIp " + ipToStars(tempNetIp));
            return tempNetIp;
        }

        if (!TextUtils.isEmpty(vpnIp))
        {
            LogUtil.i(TAG, "Local Address : return VPN " + ipToStars(vpnIp));
            return vpnIp;
        }
        else if (!TextUtils.isEmpty(wifiIp))
        {
            LogUtil.i(TAG, "Local Address : return WIFI " + ipToStars(wifiIp));
            return wifiIp;
        }
        else
        {
            return "";
        }
    }

    /**
     * 在VPN连接的情况下，返回VPN的IP
     * @return VPN IP
     */
    private static String getVpnIp()
    {
        LogUtil.i(TAG, "Local Address : VPN begin");
        // 在VPN连接的情况下，返回VPN的IP
        NetworkInterface vpn = null;
        String vpnIp;

        try
        {
            vpn = NetworkInterface.getByName("tun0");

            if (vpn == null)
            {
                vpn = NetworkInterface.getByName("ppp0");
            }
        }
        catch (SocketException e)
        {
            LogUtil.e(TAG, e.getMessage());
        }

        vpnIp = getValidIpAddress(vpn);

        if (null == vpnIp)
        {
            vpnIp = "";
        }

        LogUtil.i(TAG, "Local Address : VPN is " + ipToStars(vpnIp));
        return vpnIp;
    }

    /**
     * 将int类型转换为String类型的字符串
     * @param i ip地址
     * @return
     */
    public static String intToIp(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + ((i >> 24) & 0xFF);
    }

    /**
     * 检查是否有wifi连接，如果是wifi连接的话返回wifi的ip地址
     * @return wifi ip
     */
    private static String getWifiIp()
    {
        @SuppressLint("WifiManagerLeak") Object obj = LocContext.getContext()
                .getSystemService(Context.WIFI_SERVICE);
        if (!(obj instanceof WifiManager))
        {
            return "";
        }

        LogUtil.i(TAG, "Local Address : WIFI begin");
        WifiInfo wifiInfo = ((WifiManager) obj).getConnectionInfo();

        String wifiIp;
        if (wifiInfo != null)
        {
            int ipAddress = wifiInfo.getIpAddress();
            wifiIp = intToIp(ipAddress);

            if (0 != ipAddress)
            {
                LogUtil.i(TAG, "Local Address : WIFI is " + ipToStars(wifiIp));
                return wifiIp;
            }
        }

        LogUtil.i(TAG, "Local Address : WIFI is ");
        return "";
    }

    private static List<String> getNetAddress()
    {
        LogUtil.i(TAG, "Local Address : NetAddress begin");
        List<String> allIpList = new ArrayList<>();

        Enumeration<NetworkInterface> networkInfo = null;

        try
        {
            networkInfo = NetworkInterface.getNetworkInterfaces();
        }
        catch (SocketException e)
        {
            LogUtil.e(TAG, e.getMessage());
        }

        if (networkInfo == null)
        {
            LogUtil.e(TAG, "Local Address : NetAddress address Error");
            return allIpList;
        }

        NetworkInterface intf;
        List<String> tempIp;
        for (Enumeration<NetworkInterface> en = networkInfo; en.hasMoreElements(); )
        {
            intf = en.nextElement();
            tempIp = getValidIpAddressList(intf);
            if (!tempIp.isEmpty())
            {
                allIpList.addAll(tempIp);
            }
        }
        List<String> ipStarList = new ArrayList<>();
        for (String ip : allIpList)
        {
            ipStarList.add(ipToStars(ip));
        }
        LogUtil.i(TAG, "Local Address : NetAddress is " + ipStarList);
        return allIpList;
    }
	
	/**
     * 本地ip地址匿名化格式 ipv4星化中间2个字节 ipv6星化中间11个字节
     * @param ip 原始ip
     * @return 格式化后的ip
     */
    public static String ipToStars(String ip)
    {
        if (TextUtils.isEmpty(ip))
        {
            return ip;
        }
        int index;
        if (isIPV4Address(ip))
        {
            index = ip.indexOf(".");
            return index == -1 ? ip: ip.substring(0, ip.indexOf(".")) + "******" + ip.substring(ip.lastIndexOf(".") + 1);
        }
        else
        {
            index = ip.indexOf(":");
            return index == -1 ? ip: ip.substring(0, ip.indexOf(":")) + "******************" + ip.substring(ip.lastIndexOf(":") + 1);
        }
    }
	
	public static String getValidIpAddress(NetworkInterface networkInterface)
    {
        if (networkInterface == null)
        {
            return "";
        }

        Enumeration<InetAddress> addressEnum;
        InetAddress inetAddress;

        String ip;

        try
        {
            // up表示网卡已经启用
            if (!networkInterface.isUp())
            {
                return "";
            }

            addressEnum = networkInterface.getInetAddresses();

            for (Enumeration<InetAddress> value = addressEnum; value.hasMoreElements(); )
            {
                inetAddress = value.nextElement();

                if (inetAddress.isLoopbackAddress())
                {
                    continue;
                }

                ip = inetAddress.getHostAddress();

                if (!isIPV4Address(ip))
                {
                    continue;
                }

                if ("10.0.2.15".equals(ip))
                {
                    LogUtil.i(TAG, "Local Address : error ip#" + ipToStars(ip));
                }
                else
                {
                    LogUtil.i(TAG, "Local Address : name#" + inetAddress.getHostName() + ",ip#" + ipToStars(ip));
                    return ip;
                }
            }
        }
        catch (SocketException e)
        {
            LogUtil.e(TAG, e.getMessage());
        }

        return "";
    }
	
	private static List<String> getValidIpAddressList(NetworkInterface networkInterface)
    {
        List<String> ipList = new ArrayList<>();
        if (networkInterface == null)
        {
            return ipList;
        }

        Enumeration<InetAddress> addressEnum;
        InetAddress inetAddress;

        try
        {
            // up表示网卡已经启用
            if (!networkInterface.isUp())
            {
                return ipList;
            }

            addressEnum = networkInterface.getInetAddresses();

            String tempIp;
            for (Enumeration<InetAddress> value = addressEnum; value.hasMoreElements(); )
            {
                inetAddress = value.nextElement();

                if (inetAddress.isLoopbackAddress())
                {
                    continue;
                }

                tempIp = inetAddress.getHostAddress();

                if (!isIPV4Address(tempIp))
                {
                    continue;
                }

                if ("10.0.2.15".equals(tempIp))
                {
                    LogUtil.d(TAG, "Local Address : getValidIp error ip#" + ipToStars(tempIp));
                }
                else
                {
                    LogUtil.d(TAG, "Local Address : getValidIp name#" + networkInterface.getDisplayName() + ",ip#" + ipToStars(tempIp));
                    ipList.add(tempIp);
                }
            }
        }
        catch (SocketException e)
        {
            LogUtil.e(TAG, e.getMessage());
        }

        return ipList;
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

}
