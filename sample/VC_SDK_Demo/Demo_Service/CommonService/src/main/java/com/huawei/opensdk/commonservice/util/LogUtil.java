package com.huawei.opensdk.commonservice.util;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class is about The type Tup log util.
 * Record tup log,Written in the SD card file.
 * LogUtil日志类
 * 记录tup日志, 写入 SD 卡文件
 */
public final class LogUtil
{

    public static final String CHARSET_UTF_8 = "UTF-8";
    public static final String DEMO_LOG = "VCSDKDemo";
    public static final String DEMO_LOG_FILE_NAME = "VCSDKDemo.log";
    private static final String TAG = "VCSDKDemo";
    private static String format = "yyyy-MM-dd HH:mm:ss.SSS";
    private static double logFileSize = 1024.00 * 1024.00 * 5;
    private static boolean isLog = true;
    private static String logPath = DEMO_LOG;

    private static final LogTag defaultInstance = new LogTag();

    private LogUtil()
    {
    }

    private static LogTag getInstance()
    {
        return defaultInstance;
    }


    public static void setLogPath(String path)
    {
        logPath = path;
    }

    /**
     * This method is used to log debug.
     * debug日志级别
     *
     * @param msg the msg
     *            消息内容
     */
    public static void d(String msg)
    {
        if (isLog)
        {
            writeLog("debug" + "-" + msg);
            Log.d(TAG, " " + msg);
        }
    }

    /**
     * This method is used to log debug.
     * debug日志级别
     *
     * @param tag the tag
     *            日志标签
     * @param msg the msg
     *            消息内容
     */
    public static void d(String tag, String msg)
    {
        if (isLog)
        {
            Log.d(TAG, tag + " " + msg);
            writeLog("debug" + "-" + getTagName(tag) + " : " + msg);
        }
    }

    /**
     * This method is used to log info.
     * info日志级别
     *
     * @param tag the tag
     *            日志标签
     * @param msg the msg
     *            消息内容
     */
    public static void i(String tag, String msg)
    {
        if (isLog)
        {
            Log.i(TAG, tag + ":" + getInstance().getExtraInfo() + " " + msg);
            writeLog("info" + "-" + getTagName(tag) + ":" + getInstance().getExtraInfo() + " : " + msg);

        }
    }

    /**
     * This method is used to log error.
     * error日志级别
     *
     * @param tag the tag
     *            日志标签
     * @param msg the msg
     *            消息内容
     */
    public static void e(String tag, String msg)
    {
        if (isLog)
        {
            Log.e(TAG, tag + " " + msg);
            writeLog("error" + "-" + getTagName(tag) + " : " + msg);
        }

    }

    private static String getTagName(String tag)
    {
        return tag == null ? DEMO_LOG : tag;
    }

    private static void writeLog(String logText)
    {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            return;
        }

        String nowTimeStr = String.format("[%s]", new SimpleDateFormat(format).format(new Date()));
        String toLogStr = nowTimeStr + " " + logText;
        toLogStr += "\r\n";

        FileOutputStream fileOutputStream = null;
        String logFile = Environment.getExternalStorageDirectory() + "/" + logPath;
        String filename = DEMO_LOG_FILE_NAME;
        try
        {

            File fileOld = new File(logFile + "/" + filename);
            if ((float) ((fileOld.length() + logText.length()) / 1024.00) > logFileSize)
            {
                File bakFile = new File(fileOld.getPath() + ".bak");
                if (bakFile.exists())
                {
                    if (bakFile.delete())
                    {
                        Log.d("Write Log", "delete " + bakFile.getName());
                    }
                }
                if (fileOld.renameTo(bakFile))
                {
                    Log.d("Write Log", fileOld.getName() + " rename to " + bakFile.getName());
                }
            }

            File file = new File(logFile);
            if (!file.exists())
            {
                if (file.mkdir())
                {
                    Log.d("Write Log", "create " + file.getName());
                }
            }

            File filepath = new File(logFile + "/" + filename);
            if (!filepath.exists())
            {
                if (filepath.createNewFile())
                {
                    Log.d("Write Log", "create " + filepath.getName());
                }
            }
            fileOutputStream = new FileOutputStream(filepath, true);

            byte[] buffer = toLogStr.getBytes(CHARSET_UTF_8);

            fileOutputStream.write(buffer);
        }
        catch (FileNotFoundException e)
        {
            LogUtil.e(TAG, e.getMessage());
        }
        catch (IOException e)
        {
            LogUtil.e(TAG, e.getMessage());
        }
        finally
        {
            if (fileOutputStream != null)
            {
                try
                {
                    fileOutputStream.close();
                }
                catch (IOException e)
                {
                    LogUtil.e(TAG, e.getMessage());
                }
            }
        }
    }
}
