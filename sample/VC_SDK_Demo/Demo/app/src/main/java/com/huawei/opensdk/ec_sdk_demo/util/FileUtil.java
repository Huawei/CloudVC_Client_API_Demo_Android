package com.huawei.opensdk.ec_sdk_demo.util;

import android.os.Environment;
import android.text.TextUtils;

import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

/**
 * This class is about file Tool class.
 */
public final class FileUtil
{
    private FileUtil()
    {
    }


    /**
     * copy file
     * @param input FileInputStream
     * @param trgPath The path to the target file to be copied
     */
    public static boolean copyFile(InputStream input, String trgPath)
    {
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);
        if (!sdCardExist)
        {
            LogUtil.e(UIConstants.DEMO_TAG, "sdcard is not exist");
            return false;
        }

        if (null == input || TextUtils.isEmpty(trgPath))
        {
            LogUtil.e(UIConstants.DEMO_TAG, "stream or The target path is null!");
            return false;
        }

        BufferedInputStream inBuffStream = null;
        FileOutputStream output = null;
        BufferedOutputStream outBuffStream = null;

        File trgFile = new File(trgPath);
        try
        {
            if (!trgFile.exists())
            {
                boolean isCreateSuccess = trgFile.createNewFile();
                if (!isCreateSuccess)
                {
                    return false;
                }
            }
            inBuffStream = new BufferedInputStream(input);
            output = new FileOutputStream(trgFile);
            outBuffStream = new BufferedOutputStream(output);
            byte[] buffer = new byte[2 * 1024 * 1024];
            while (true)
            {
                int inBuflen = inBuffStream.read(buffer);
                if (-1 == inBuflen)
                {
                    outBuffStream.flush();
                    break;
                }
                else
                {
                    outBuffStream.write(buffer, 0, inBuflen);
                }
            }

            return true;
        }
        catch (FileNotFoundException e)
        {
            LogUtil.e(UIConstants.DEMO_TAG, e.getMessage());
            return false;
        }
        catch (IOException e)
        {
            LogUtil.e(UIConstants.DEMO_TAG, e.getMessage());
            return false;
        }
        finally
        {
            Closeables.closeCloseable(outBuffStream);
            Closeables.closeCloseable(output);
            Closeables.closeCloseable(inBuffStream);
            Closeables.closeCloseable(input);
        }
    }

    /**
     * is sdcard exist
     * @return boolean
     */
    public static boolean isSdCardExist()
    {
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);
        if (sdCardExist)
        {
            return true;
        }
        LogUtil.e(UIConstants.DEMO_TAG, "sdcard is not exist");
        return false;
    }

    public static void deleteFile(File file)
    {
        deleteFile(file, null);
    }

    public static void deleteFile(File file, File[] exceptFiles)
    {
        if (file == null || !file.exists())
        {
            return;
        }

        if (file.isFile() && !file.isHidden())
        {
            boolean success = file.delete();

            if (!success)
            {
                LogUtil.e(UIConstants.DEMO_TAG,  "delete file error ");
            }

            return;
        }

        if (file.isDirectory() && !isContainFile(file, exceptFiles))
        {
            File[] files = file.listFiles();
            if (null != files && 0 != files.length)
            {
                for (File f : files)
                {
                    deleteFile(f, exceptFiles);
                }
            }

            if (!file.delete())
            {
                LogUtil.e(UIConstants.DEMO_TAG,  "delete file error ");
            }
        }
    }

    /**
     * Unit conversion
     * @param size
     * @return String
     */
    public static String makeUpSizeShow(double size)
    {
        double unit = 1024.0;
        String sizeUnit = "B";
        // to KB
        if (unit < size)
        {
            sizeUnit = "KB";
            size = size / unit;
        }
        // to M
        if (unit < size)
        {
            sizeUnit = "M";
            size = size / unit;
        }
        // to .00
        DecimalFormat df = new DecimalFormat(".00");
        return df.format(size) + sizeUnit;
    }

    private static boolean isContainFile(File file, File[] files)
    {
        if (file == null || files == null || files.length == 0)
        {
            return false;
        }

        for (File f : files)
        {
            if (file.equals(f))
            {
                return true;
            }
        }

        return false;
    }

    private interface OpenResult
    {
        int OPEN_SUCCESS = 0;
        int OPEN_BY_THIRDPARTY_FAIL = 1;
    }
}
