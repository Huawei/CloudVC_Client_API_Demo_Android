package com.huawei.opensdk.commonservice.util;

import android.content.ContentValues;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

/**
 * This class is about XML parse.
 * XML文件解析类
 */
public final class Xml
{
    private Xml()
    {

    }

    /**
     * This method is used to resolving nodes in XML
     * 解析xml中的节点
     * @param xml Indicates XML content to parse
     *            需要解析的xml内容
     * @param key Indicates nodes to parse
     *            需要解析的节点
     * @return ContentValues Returns the result of ContentValues type correspondence resolution
     *                       返回ContentValues类型对应解析的结果
     */
    public static ContentValues parseStringInXml(String xml, List<String> key)
    {
        XmlPullParser parser;

        try
        {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            parser = factory.newPullParser();
        }
        catch (XmlPullParserException e)
        {
            return null;
        }

        ByteArrayInputStream inputStream = null;
        ContentValues values = new ContentValues();

        try
        {
            byte[] body = null;

            if (xml != null)
            {
                body = xml.getBytes("UTF-8");
            }

            if (null == body)
            {
                return null;
            }

            inputStream = new ByteArrayInputStream(body);
            parser.setInput(inputStream, "UTF-8");
            handleXmlContent(parser, key, values);
        }
        catch (IOException e)
        {
            return null;
        }
        catch (XmlPullParserException e)
        {
            return null;
        }
        finally
        {
            //
        }

        return values;
    }


    private static void handleXmlContent(XmlPullParser parser, List<String> key, ContentValues values) throws XmlPullParserException, IOException
    {
        int eventType = parser.getEventType();
        String name;

        while (eventType != XmlPullParser.END_DOCUMENT)
        {
            if (eventType == XmlPullParser.START_TAG)
            {
                name = parser.getName();
                if (key.contains(name))
                {
                    values.put(name, parser.nextText());
                }
            }

            eventType = parser.next();
        }
    }
}