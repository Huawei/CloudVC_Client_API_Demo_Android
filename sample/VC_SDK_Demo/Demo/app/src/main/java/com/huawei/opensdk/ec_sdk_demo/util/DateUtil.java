package com.huawei.opensdk.ec_sdk_demo.util;

import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil
{
    private static final DateUtil INSTANCE = new DateUtil();

    /**
     * time patten(yyyy-MM-dd HH:mm)
     */
    public static final String FMT_YMDHM = "yyyy-MM-dd HH:mm";

    public static final String UTC = "UTC";

    private DateUtil()
    {
    }

    public static DateUtil getInstance()
    {
        return INSTANCE;
    }

    public static String localTimeUtc(String srcTime)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat dspFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String utcTime;
        Date resultDate;
        long resultTime = 0;

        TimeZone timeZone = TimeZone.getDefault();

        if ((srcTime == null) || srcTime.equals(""))
        {
            return null;
        }
        else
        {
            try
            {
                sdf.setTimeZone(timeZone);
                resultDate = sdf.parse(srcTime);

                resultTime = resultDate.getTime();
            }
            catch (Exception e)
            {
                resultTime = System.currentTimeMillis();
                dspFmt.setTimeZone(TimeZone.getDefault());
                utcTime = dspFmt.format(resultTime);
                return utcTime;
            }
        }

        dspFmt.setTimeZone(TimeZone.getTimeZone("GMT00:00"));
        utcTime = dspFmt.format(resultTime);

        return utcTime;
    }

    /**
     * Convert utc time to local time
     * @param utcTime
     * @param utcPatten
     * @param localPatten
     * @return Local time
     */
    public static String utcToLocalDate(String utcTime, String utcPatten, String localPatten)
    {
        Date utcDate = parseDateStr(utcTime, UTC, utcPatten);
        return generateFormat(utcDate, localPatten);
    }

    /**
     * parseDateStr
     * @param time
     * @param dateId
     * @param patten
     * @return
     */
    public static Date parseDateStr(String time,String dateId, String patten){
        SimpleDateFormat utcFormat = new SimpleDateFormat(patten);
        utcFormat.setTimeZone(TimeZone.getTimeZone(dateId));
        Date utcDate;
        try
        {
            utcDate = utcFormat.parse(time);
        }
        catch (ParseException e)
        {
            LogUtil.e(UIConstants.DEMO_TAG, e.getMessage());
            return new Date();
        }
        return utcDate;
    }

    private static String generateFormat(Date date, String patten){
        SimpleDateFormat localFormat = new SimpleDateFormat(patten);
        localFormat.setTimeZone(TimeZone.getDefault());

        return localFormat.format(date.getTime());
    }
}
