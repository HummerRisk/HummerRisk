package com.hummerrisk.commons.utils;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateParser;
import org.apache.commons.lang3.time.DatePrinter;

import java.lang.management.ManagementFactory;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 时间工具类
 *
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils
{
    public static String YYYY = "yyyy";

    public static String YYYY_MM = "yyyy-MM";

    public static String YYYY_MM_DD = "yyyy-MM-dd";

    public static String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    public static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    private static String[] parsePatterns = {
            "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
            "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM"};

    public static String getDateFormat(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
        return sdf.format(date);
    }
    public static String getDateFormatHan(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        return sdf.format(date);
    }
    public static String getDateFormatMonthDelYear(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日");
        return sdf.format(date);
    }
    public static String getDateFormatHoursDelYear(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(date);
    }
    public static String getDateFormatMonths(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日 HH:mm");
        return sdf.format(date);
    }
    public static String getDateFormatToDay(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        return sdf.format(date);
    }

    /**
     * 获取当前Date型日期
     *
     * @return Date() 当前日期
     */
    public static Date getNowDate()
    {
        return new Date();
    }

    /**
     * 获取当前日期, 默认格式为yyyy-MM-dd
     *
     * @return String
     */
    public static String getDate()
    {
        return dateTimeNow(YYYY_MM_DD);
    }

    public static final String getTime()
    {
        return dateTimeNow(YYYY_MM_DD_HH_MM_SS);
    }

    public static final String dateTimeNow()
    {
        return dateTimeNow(YYYYMMDDHHMMSS);
    }

    public static final String dateTimeNow(final String format)
    {
        return parseDateToStr(format, new Date());
    }

    public static final String dateTime(final Date date)
    {
        return parseDateToStr(YYYY_MM_DD, date);
    }

    public static final String parseDateToStr(final String format, final Date date)
    {
        return new SimpleDateFormat(format).format(date);
    }

    public static final Date dateTime(final String format, final String ts)
    {
        try
        {
            return new SimpleDateFormat(format).parse(ts);
        }
        catch (ParseException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * 格林尼治时间转当地时间
     * @param format
     * @param ts
     * @return
     */
    public static Date utcTimeStrToLocalDate(final String format,final String ts){
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            TimeZone utcZone = TimeZone.getTimeZone("UTC");
            simpleDateFormat.setTimeZone(utcZone);
            return simpleDateFormat.parse(ts);
        }catch (ParseException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * 当地时间转格林尼治时间
     * @param format
     * @param ts
     * @return
     */
    public static String localDateStrToUtcDateStr(String format,String ts){

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = simpleDateFormat.parse(ts);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date time = calendar.getTime();
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return simpleDateFormat.format(time);
    }

    /**
     * 当地时间转格林尼治时间
     * @param format
     * @param date
     * @return
     */
    public static String localDateToUtcDateStr(String format,Date date){
       /* SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        TimeZone utcZone = TimeZone.getTimeZone("UTC");
        simpleDateFormat.setTimeZone(utcZone);
        return simpleDateFormat.format(date);
*/
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date time = calendar.getTime();
        SimpleDateFormat outputFmt = new SimpleDateFormat(format);
        outputFmt.setTimeZone(TimeZone.getTimeZone("UTC"));
        return outputFmt.format(time);
    }

    /**
     * 日期路径 即年/月/日 如2018/08/08
     */
    public static final String datePath()
    {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyyMMdd");
    }

    /**
     * 日期路径 即年/月/日 如20180808
     */
    public static final String dateTime()
    {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyyMMdd");
    }

    /**
     * 日期型字符串转化为日期 格式
     */
    public static Date parseDate(Object str)
    {
        if (str == null)
        {
            return null;
        }
        try
        {
            return parseDate(str.toString(), parsePatterns);
        }
        catch (ParseException e)
        {
            return null;
        }
    }
    public static final String parseDateToStrSS(final Date date)
    {
        return new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS).format(date);
    }
    public static final Date dateTimeParseSS(final String ts)
    {
        try
        {
            return new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS).parse(ts);
        }
        catch (ParseException e)
        {
            throw new RuntimeException(e);
        }
    }


    /**
     * 获取服务器启动时间
     */
    public static Date getServerStartDate()
    {
        long time = ManagementFactory.getRuntimeMXBean().getStartTime();
        return new Date(time);
    }

    /**
     * 计算两个时间差
     */
    public static String getDatePoor(Date endDate, Date nowDate)
    {
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - nowDate.getTime();
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        // 计算差多少秒//输出结果
        // long sec = diff % nd % nh % nm / ns;
        return day + "天" + hour + "小时" + min + "分钟";
    }

    /**
     * 获取当前时间 加上某几天
     */
    public static Date addDate(Date date,long day) throws ParseException {
        long time = date.getTime(); // 得到指定日期的毫秒数
        day = day*24*60*60*1000; // 要加上的天数转换成毫秒数
        time+=day; // 相加得到新的毫秒数
        return new Date(time); // 将毫秒数转换成日期
    }

    public static String addDateStr(String format,String dateStr,long day) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        Date date = dateFormat.parse(dateStr);
        long time = date.getTime(); // 得到指定日期的毫秒数
        day = day*24*60*60*1000; // 要加上的天数转换成毫秒数
        time+=day; // 相加得到新的毫秒数
        return dateFormat.format(new Date(time)); // 将毫秒数转换成日期
    }

    /**
     * 自己拼接年月
     */
    public static String getDateForString(int month){
        String mont = String.valueOf(getCurMonth());
        if (mont.length()==1){
            mont = "0"+mont;
        }else{
            mont = mont;
        }
        String date = String.valueOf(getCurYear())+"-"+mont;
        //判断当前传过来的时间有几位
        String monthStr= String.valueOf(month);
        int length = monthStr.length();
        if (length==1){
            date = date+"-0"+monthStr;
        }else{
            //直接拼接
            date = date+"-"+monthStr;
        }
        return date;
    }

    /**
     * 时间减去一天或者加上一天
     * @return
     */
    public static Date getCurrentTimeDayCount(Date date,int dayCount){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, dayCount);
        return calendar.getTime();
    }

    /**
     * 时间减去一个月或者加上一个月
     * @return
     */
    public static Date getCurrentTimeMonthCount(Date date,int dayCount){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, dayCount);
        return calendar.getTime();
    }

    /**
     * 获取当前月份有多少天
     */
    public static int getCurrentMonthCount(){
        int month = getCurMonth();
        int year = getCurYear();
        int days = 0;
        if (month != 2) {
            switch (month) {
                case 1:
                case 3:
                case 5:
                case 7:
                case 8:
                case 10:
                case 12:
                    days = 31;
                    break;
                case 4:
                case 6:
                case 9:
                case 11:
                    days = 30;

            }
        } else {
            // 闰年
            if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0)
                days = 29;
            else
                days = 28;
        }
        return days;
    }

    /**
     * 获取当前月份
     * @return
     */
    public static int getCurMonth() {
        Calendar cale = null;
        cale = Calendar.getInstance();
        return cale.get(Calendar.MONTH) + 1;
    }
    /**
     * 获取当前年份
     * @return
     */
    public static int getCurYear() {
        Calendar cale = null;
        cale = Calendar.getInstance();
        return cale.get(Calendar.YEAR);
    }
    /**
     * 获取当前日
     * @return
     */
    public static String getCurDay() {
        String dd = new SimpleDateFormat("d").format(new Date());
        return dd;
    }

    /**
     * date2比date1多的天数
     * @param date1
     * @param date2
     * @return
     */
    public static int subDay(Date date1,Date date2)
    {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        int day1= cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if(year1 != year2)   //同一年
        {
            int timeDistance = 0 ;
            for(int i = year1 ; i < year2 ; i ++)
            {
                if(i%4==0 && i%100!=0 || i%400==0)    //闰年
                {
                    timeDistance += 366;
                }
                else    //不是闰年
                {
                    timeDistance += 365;
                }
            }

            return timeDistance + (day2-day1) ;
        }
        else    //不同年
        {
            System.out.println("判断day2 - day1 : " + (day2-day1));
            return day2-day1;
        }
    }
    /**
     * 将秒转为分钟和秒格式
     */
    public static String getMinutesAndSeconds(int seconds){
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        String str = minutes+"分"+remainingSeconds+"秒";
        return str;
    }
    /**
     * 计算两个日期的时间差
     * differ
     */
    public static Integer getDayDifferCount(Date startTime,Date endTime) throws ParseException {
        String startDate = DateUtils.parseDateToStr("yyyy-MM-dd", startTime);
        String endDate = DateUtils.parseDateToStr("yyyy-MM-dd", endTime);
        //算两个日期间隔多少天
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = format.parse(endDate);
        Date date2 = format.parse(startDate);
        int a = (int) ((date1.getTime() - date2.getTime()) / (1000*3600*24));
        return a;
    }

    /**
     * 获取本周的开始时间
     * @return
     */
    public static Date getBeginDayOfWeek() {
        Date date = new Date();
        if (date == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayofweek = cal.get(Calendar.DAY_OF_WEEK);
        if (dayofweek == 1) {
            dayofweek += 7;
        }
        cal.add(Calendar.DATE, 2 - dayofweek);
        return getDayStartTime(cal.getTime());
    }

    /**
     * 获取某个日期的开始时间
     * @param d
     * @return
     */
    // 获取某个日期的开始时间
    public static Timestamp getDayStartTime(Date d) {
        Calendar calendar = Calendar.getInstance();
        if (null != d)
            calendar.setTime(d);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return new Timestamp(calendar.getTimeInMillis());
    }

    public static Date stringToDate(String source) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
        Date date = null;
        try {
            date = simpleDateFormat.parse(source);
        } catch (Exception e) {
        }
        return date;
    }
    public static List<String> getBetweenDate(String startDate, String endDate,String dateFormat){
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        List<String> list = new ArrayList<String>();
        try {
            Date startTime = sdf.parse(startDate);
            Date endTime = sdf.parse(endDate);

            Calendar calendar = Calendar.getInstance();
            while (startTime.getTime()<=endTime.getTime()){
                list.add(sdf.format(startTime));
                calendar.setTime(startTime);
                calendar.add(Calendar.DATE, 1);
                startTime=calendar.getTime();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return list;
    }
}
