package com.huawei.opensdk.ec_sdk_demo.util;

import android.net.Uri;
import android.text.TextUtils;

import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * This class is about compression and decompression
 * 压缩与解压缩
 * 对文件进行解压缩处理
 */
public final class ZipUtil
{
    /**
     * Constructors
     */
    private ZipUtil()
    {
    }


    /**
     * 方法名称：zip
     * 作者：
     * 方法描述：压缩文件
     * 输入参数：@param byteSource
     * 输入参数：@return
     * 返回类型：byte[]：
     * 备注：
     */
    @Deprecated
    public static byte[] zip(byte[] byteSource)
    {
        if ((byteSource == null) || (byteSource.length == 0))
        {
            return new byte[0];
        }

        byte[] byteZipped = null;
        ByteArrayOutputStream bos = null;
        ZipOutputStream zos = null;
        try
        {
            bos = new ByteArrayOutputStream();
            zos = new ZipOutputStream(bos);
            zos.putNextEntry(new ZipEntry("0"));
            zos.setMethod(ZipOutputStream.DEFLATED);
            zos.write(byteSource);
            zos.finish();
            byteZipped = bos.toByteArray();
            Closeables.closeCloseable(zos);
            Closeables.closeCloseable(bos);
        }
        catch (IOException e)
        {
            LogUtil.i(UIConstants.DEMO_TAG,  e.toString());
        }
        finally
        {
            Closeables.closeCloseable(zos);
            Closeables.closeCloseable(bos);
        }
        return byteZipped;
    }

    /**
     * 压缩指定路径的文件到指定路径下
     *
     * @param sourcePaths
     * @param targetPath
     */
    public static void zipMultiFile(Object[] sourcePaths, String targetPath)
    {
        // 如果为null，则直接返回
        int length = null == sourcePaths ? 0 : sourcePaths.length;
        if (0 == length || TextUtils.isEmpty(targetPath))
        {
            return;
        }

        FileOutputStream fos = null;
        CheckedOutputStream cos = null;
        ZipOutputStream zos = null;
        BufferedOutputStream out = null;

        FileInputStream fis = null;
        Reader reader = null;
        BufferedReader bin = null;

        try
        {
            // 初始化压缩包输出流
            fos = new FileOutputStream(targetPath);
            // 输出校验流,采用Adler32更快
            cos = new CheckedOutputStream(fos, new Adler32());
            // 创建压缩输出流
            zos = new ZipOutputStream(cos);
            out = new BufferedOutputStream(zos);
            String filePath;
            int countOfWrite;
            for (Object path : sourcePaths)
            {
                if (!(path instanceof String))
                {
                    continue;
                }

                filePath = (String) path;
                LogUtil.d(UIConstants.DEMO_TAG,  filePath);
                // 针对单个文件建立读取流
                File file = new File(filePath);
                if (!file.isDirectory())
                {
                    fis = new FileInputStream(file);
                    reader = new InputStreamReader(fis, "UTF-8");
                    bin = new BufferedReader(reader);

                    //ZipEntry ZIP 文件条目; putNextEntry 写入新条目，并定位到新条目开始处
                    zos.putNextEntry(new ZipEntry(Uri.parse(filePath).getLastPathSegment()));
                    while ((countOfWrite = bin.read()) != -1)
                    {
                        out.write(countOfWrite);
                    }

                    Closeables.closeCloseable(bin);
                    Closeables.closeCloseable(reader);
                    Closeables.closeCloseable(fis);
                    out.flush();
                }
                else
                {
                    // 如果是目录，则创建目录，遍历其下的所有文件，写入zip包中
                    File[] filesArray = file.listFiles();
                    if (filesArray == null)
                    {
                        continue;
                    }

                    String dirName = Uri.parse(filePath).getLastPathSegment() + File.separator;
                    if (filesArray.length != 0)
                    {
                        for (File fi : filesArray)
                        {
                            fis = new FileInputStream(fi);
                            reader = new InputStreamReader(fis, "UTF-8");
                            bin = new BufferedReader(reader);

                            // ZipEntry ZIP 文件条目; putNextEntry 写入新条目，并定位到新条目开始处
                            zos.putNextEntry(new ZipEntry(dirName + Uri.parse(fi.getPath()).getLastPathSegment()));
                            while ((countOfWrite = bin.read()) != -1)
                            {
                                out.write(countOfWrite);
                            }

                            Closeables.closeCloseable(bin);
                            Closeables.closeCloseable(reader);
                            Closeables.closeCloseable(fis);
                        }
                    }
                    out.flush();
                }
            }
        }
        catch (FileNotFoundException e)
        {
            LogUtil.i(UIConstants.DEMO_TAG,  e.toString());
        }
        catch (UnsupportedEncodingException e)
        {
            LogUtil.i(UIConstants.DEMO_TAG,  e.toString());
        }
        catch (IOException e)
        {
            LogUtil.i(UIConstants.DEMO_TAG,  e.toString());
        }
        finally
        {
            Closeables.closeCloseable(bin);
            Closeables.closeCloseable(reader);
            Closeables.closeCloseable(fis);

            Closeables.closeCloseable(out);
            Closeables.closeCloseable(zos);
            Closeables.closeCloseable(cos);
            Closeables.closeCloseable(fos);
        }
    }

    /**
     * 描述：将ZIP文件后解压到指定目录下
     *
     * @param is     ZIP数据流 使用后会关闭inputStream
     * @param outputDirectory 指定目录
     */
    public static void unZipFile(InputStream is, String outputDirectory)
    {
        // 创建解压目标目录
        File file = new File(outputDirectory);
        // 如果目标目录不存在，则创建
        if (!file.exists())
        {
            if (!file.mkdirs())
            {
                return;
            }
        }

        ZipInputStream zis = null;
        FileOutputStream fos = null;
        // ZipEntry entry = null;

        try
        {
            // 打开压缩文件
            zis = new ZipInputStream(is);
            // 读取一个进入点
            ZipEntry entry = zis.getNextEntry();
            // 使用1Mbuffer
            byte[] buffer = new byte[1024 * 1024];
            String head = outputDirectory + File.separator;
            // 解压时字节计数
            int count = 0;
            // 如果进入点为空说明已经遍历完所有压缩包中文件和目录
            while (entry != null)
            {
                // 如果是一个目录
                if (entry.isDirectory())
                {
                    file = new File(head + entry.getName());
                    if (!file.mkdirs())
                    {
                        break;
                    }
                }
                else
                {
                    // 如果是文件
                    file = new File(head + entry.getName());
                    // 创建该文件
                    boolean isCreate = file.createNewFile();
                    fos = new FileOutputStream(file);
                    while ((count = zis.read(buffer)) > 0)
                    {
                        fos.write(buffer, 0, count);
                    }

                    Closeables.closeCloseable(fos);
                }
                // 定位到下一个文件入口
                entry = zis.getNextEntry();
            }

            Closeables.closeCloseable(zis);
        }
        catch (IOException e)
        {
            LogUtil.e(UIConstants.DEMO_TAG,  "close...Exception->e" + e.toString());
        }
        finally
        {
            Closeables.closeCloseable(fos);
            Closeables.closeCloseable(zis);
            Closeables.closeCloseable(is);
        }
    }
}
