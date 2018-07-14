package com.huawei.opensdk.commonservice.util;

import android.content.Context;
import android.os.Environment;
import android.os.Process;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class is about crash handler util.
 * 处理程序崩溃 util类
 */
public class CrashUtil implements Thread.UncaughtExceptionHandler
{

    private static final String TAG = CrashUtil.class.getSimpleName();

    /**
     * Instance object of CrashUtil component.
     * CrashUtil实例对象
     */
    private static CrashUtil instance;

    /**
     * The UncaughtExceptionHandler object.
     * UncaughtExceptionHandler异常对象
     */
    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

    /**
     * The context
     * 上下文
     */
    private Context mContext;

    private SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd-HH");

    /**
     * This method is used to get instance object of CrashUtil.
     * 获取一个CrashUtil对象实例
     * @return CrashUtil Return instance object of CrashUtil
     *                   返回一个CrashUtil对象实例
     */
    public static CrashUtil getInstance()
    {
        if (null == instance)
        {
            instance = new CrashUtil();
        }
        return instance;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex)
    {
        handlerException(ex);
        if (uncaughtExceptionHandler != null)
        {
            uncaughtExceptionHandler.uncaughtException(thread, ex);
        }
        else
        {
            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
                LogUtil.e(TAG, e.getMessage());
            }
            Process.killProcess(Process.myPid());
            System.exit(1);
        }
    }

    /**
     * This method is used to init.
     * 初始化
     * @param context Indicates context
     *                上下文
     */
    public void init(Context context)
    {
        mContext = context;
        uncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * This method is used to handling exception information.
     * 处理异常信息
     * @param throwable
     * @return boolean Return if no exception information returned false otherwise return true
     *                 如果没有异常信息返回false，否则返回TRUE
     */
    private boolean handlerException(Throwable throwable)
    {
        if (null == throwable)
        {
            return false;
        }
        saveCrashInfoToFile(throwable);
        return true;
    }

    /**
     * This method is used to to save the captured exception information to a file.
     * 把捕获的异常信息保存到文件中去
     * @param ex
     */
    private void saveCrashInfoToFile(Throwable ex)
    {
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);

        Throwable error = ex.getCause();
        while (null != error)
        {
            error.printStackTrace(printWriter);
            error = error.getCause();
        }
        printWriter.close();
        StringBuffer buffer = new StringBuffer();
        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS").format(new Date());
        String result = writer.toString();
        String toLogStr = currentTime + "\n" + result + "\r\n";
        buffer.append(toLogStr);

        try
        {
            String time = format.format(new Date());
            String fileName = "crash-" + time + ".txt";
            String mounted = Environment.MEDIA_MOUNTED;
            if (mounted.equals(Environment.getExternalStorageState()))
            {
                String path = Environment.getExternalStorageDirectory() + "/" + "ECSDKDemoCrash" + "/" ;
                File dir = new File(path);
                if (!dir.exists())
                {
                    dir.mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(path + fileName, true);
                fos.write(buffer.toString().getBytes("UTF-8"));
                fos.close();
            }
        }
        catch (Exception e)
        {
            LogUtil.e(TAG, e.getMessage());
        }
    }
}
