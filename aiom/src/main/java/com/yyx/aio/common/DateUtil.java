package com.yyx.aio.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/*
 * @version 1.0
 * 
 * @author yangyong
 * @since JDK1.7
 */
public class DateUtil {

	/*
	 * yyyy-MM-dd hh:mm:ss
	 */
	public static String DEFAULT_FORMATSTR = "yyyy-MM-dd HH:mm:ss";

	/*
	 * yyyy-MM-dd
	 */
	public static String DEFAULT_FORMATSHORT = "yyyy-MM-dd";

	public static SimpleDateFormat lFormat = new SimpleDateFormat(DEFAULT_FORMATSHORT);
	public static SimpleDateFormat lFormat_time = new SimpleDateFormat(DEFAULT_FORMATSTR);

	/*
	 * 日志
	 */
	private static Log log = LogFactory.getLog(DateUtil.class);

	/*
	 * @description: 获取当前(utcMs)时间毫秒
	 * @date: 2011-10-8下午05:50:21
	 * @author: yangyong
	 */
	public static Long getCurrentUtcMsTime() {
		return System.currentTimeMillis();
	}

	/*
	 * @description: 获得当前日期
	 * @date: 2011-10-8下午06:04:21
	 * @author: yangyong
	 */
	public static String getTodayDate() {
		Calendar today = Calendar.getInstance();
		return lFormat.format(today.getTime());
	}

	/*
	 * @description: 获得当前时间
	 * @date: 2011-10-8下午06:04:21
	 * @author: yangyong
	 */
	public static String getTodayTime() {
		Calendar today = Calendar.getInstance();
		return lFormat_time.format(today.getTime());
	}

	/*
	 * @description:获得上周是*年的第？周
	 * @date: 2011-10-8下午06:04:34
	 * @author: yangyong
	 */
	public static int getPreWeek() {
		Calendar sysdate = Calendar.getInstance();
		int lastWeekNum = sysdate.get(Calendar.WEEK_OF_YEAR) - 1;
		return lastWeekNum;
	}

	/*
	 * @description:获得上周的属于？年
	 * @date: 2011-10-8下午06:04:59
	 * @author: yangyong
	 */
	public static int getPreWeekOfYear() {
		Calendar sysdate = Calendar.getInstance();
		sysdate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		sysdate.add(Calendar.WEEK_OF_MONTH, -1);
		return Integer.valueOf(lFormat.format(sysdate.getTime()).substring(0, 4));
	}

	/*
	 * 给时间增加秒数
	 *
	 * @param date
	 * @param second
	 * @return
	 */
	public static Date addSecond(Date date, int second) {

		if (date != null) {

			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.setTimeZone(TimeZone.getDefault());
			long Time = (cal.getTimeInMillis() / 1000) + second;
			cal.setTimeInMillis(Time * 1000);

			return cal.getTime();

		} else {
			return null;
		}

	}

	/*
	 * 给时间减去秒数
	 *
	 * @param date
	 * @param second
	 * @return
	 */
	public static Date subtractSecond(Date date, int second) {

		if (date != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.setTimeZone(TimeZone.getDefault());
			long Time = (cal.getTimeInMillis() / 1000) - second;
			cal.setTimeInMillis(Time * 1000);

			return cal.getTime();

		} else {
			return null;
		}

	}

	/*
	 * @description: 根据时间获取(utcMs)时间毫秒
	 */
	public static Long getUtcMsTime(Date date) {
		if (date != null) {

			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.setTimeZone(TimeZone.getDefault());

			return cal.getTimeInMillis();
		} else {
			return null;
		}
	}

	/*
	 * description:获得上月是？月 author: yangyong Time: 2011-9-28下午03:55:04
	 */
	public static int getPreMonth() {
		GregorianCalendar gcLast = (GregorianCalendar) Calendar.getInstance();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.MONTH, -1);
		Date theDate = calendar.getTime();
		gcLast.setTime(theDate);
		gcLast.set(Calendar.DAY_OF_MONTH, 1);
		StringBuffer str = new StringBuffer().append(lFormat.format(gcLast.getTime()));
		return Integer.valueOf(str.toString().replace("-", "").substring(4, 6));
	}

	/*
	 * description:获得上月是？年 author: yangyong Time: 2011-9-28下午03:55:04
	 */
	public static int getPreMonthOfYear() {
		GregorianCalendar gcLast = (GregorianCalendar) Calendar.getInstance();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.MONTH, -1);
		Date theDate = calendar.getTime();
		gcLast.setTime(theDate);
		gcLast.set(Calendar.DAY_OF_MONTH, 1);
		StringBuffer str = new StringBuffer().append(lFormat.format(gcLast.getTime()));
		return Integer.valueOf(str.toString().substring(0, 4));
	}

	/*
	 * description:获得上周开始时间(2011-10-3) author: yangyong Time: 2011-9-28下午03:55:04
	 */
	public static String getPreWeekStartDay() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		cal.add(Calendar.WEEK_OF_MONTH, -1);
		return lFormat.format(cal.getTime());
	}

	/*
	 * description:获得上周结束时间(2011-10-9) author: yangyong Time: 2011-9-28下午03:55:04
	 */
	public static String getPreWeekEndDay() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		cal.add(Calendar.WEEK_OF_MONTH, 0);
		return lFormat.format(cal.getTime());
	}

	/*
	 * description:获得上月开始时间 author: yangyong Time: 2011-9-28下午03:55:04
	 */
	public static String getPreMonthStartDay() {
		GregorianCalendar gcLast = (GregorianCalendar) Calendar.getInstance();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.MONTH, -1);
		Date theDate = calendar.getTime();
		gcLast.setTime(theDate);
		gcLast.set(Calendar.DAY_OF_MONTH, 1);
		return lFormat.format(gcLast.getTime());
	}

	/*
	 * description:获得上月结束时间 author: yangyong Time: 2011-9-28下午03:55:04
	 */
	@SuppressWarnings("static-access")
	public static String getPreMonthEndDay() {
		Calendar cal = Calendar.getInstance();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.MONTH, -1);
		calendar.add(cal.MONTH, 1);
		calendar.set(cal.DATE, 1);
		calendar.add(cal.DATE, -1);
		return lFormat.format(calendar.getTime());
	}

	/*
	 * description:获得上年是？年 author: yangyong Time: 2011-1-28下午04:14:51
	 */
	public static int getPreYear() {
		Calendar today = Calendar.getInstance();
		int preYearNum = Integer.valueOf(lFormat.format(today.getTime()).substring(0, 4));
		return preYearNum - 1;
	}

	/*
	 * 将utc时间数转换为date时间类型
	 *
	 * @param utcTime
	 *            utc时间数(s)
	 * @return date时间
	 */
	public static Date utcTimeToDate(Long utcTime) {

		Date resultDate = null;
		if (utcTime != null) {

			log.debug("[utcTimeToDate] utcTime:" + utcTime);

			// 将UTC国际标准时间的毫秒形式转换成本地的时间
			Calendar calendar = Calendar.getInstance();
			// int offset = calendar.get(Calendar.ZONE_OFFSET);
			// int dst = calendar.get(Calendar.DST_OFFSET);
			calendar.setTimeInMillis(utcTime);
			// calendar.add(Calendar.MILLISECOND, -(offset + dst));
			resultDate = calendar.getTime();
			log.debug("[utcTimeToDate] date:" + resultDate);
		}
		return resultDate;
	}

	/*
	 * @description:将utc时间(毫秒)数转换为date时间类型
	 * @date: 2011-10-11下午03:52:45
	 * @author: yangyong
	 */
	public static Date utcMsTimeToDate(Long utcTime) {
		Date resultDate = null;
		if (utcTime != null) {
			utcTime = utcTime.longValue();
			log.debug("[utcTimeToDate] utcTime:" + utcTime);
			Calendar calendar = Calendar.getInstance();
			// int offset = calendar.get(Calendar.ZONE_OFFSET);
			// int dst = calendar.get(Calendar.DST_OFFSET);
			calendar.setTimeInMillis(utcTime);
			// calendar.add(Calendar.MILLISECOND, -(offset + dst));
			resultDate = calendar.getTime();
			log.debug("[utcTimeToDate] date:" + resultDate);
		}
		return resultDate;
	}

	/*
	 * @description:字符型日期("2011/09/10"、"2011-09-10")转换为Utc日期
	 * @date: 2016-01-11下午16:43:11
	 * @author: yangyong
	 * @throws ParseException
	 */
	public static Long stringDateToUtcDate(String stringDate) throws ParseException {

		// 将本地当前时间转换成UTC国际标准时间的毫秒形式
		if (stringDate != null) {
			log.debug("[dateToUtcTime] date:" + stringDate);
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date date = format.parse(stringDate.replace("/", "-"));

			Calendar calendar = Calendar.getInstance();
			// int offset = calendar.get(Calendar.ZONE_OFFSET);
			// int dst = calendar.get(Calendar.DST_OFFSET);
			calendar.setTime(date);
			// calendar.add(Calendar.MILLISECOND, -(offset + dst));
			return calendar.getTimeInMillis();

			// timecode=date.UTC(date.getYear(), date.getMonth(), date.getDay(), 0, 0, 0);

		} else {

			return null;
		}

	}

	/*
	 * @description:字符型时间("2011/09/10 12:45:12"、"2011-09-10 12:45:12")转换为Utc时间
	 * @date: 2016-01-11下午16:43:11
	 * @author: yangyong
	 * @throws ParseException
	 */
	public static Long stringTimeToUtcTime(String stringTime) throws ParseException {

		// 将本地当前时间转换成UTC国际标准时间的毫秒形式
		if (stringTime != null) {
			log.debug("[dateToUtcTime] date:" + stringTime);
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = format.parse(stringTime.replace("/", "-"));

			Calendar calendar = Calendar.getInstance();
			// int offset = calendar.get(Calendar.ZONE_OFFSET);
			// int dst = calendar.get(Calendar.DST_OFFSET);
			calendar.setTime(date);
			// calendar.add(Calendar.MILLISECOND, -(offset + dst));
			return calendar.getTimeInMillis();
		} else {
			return null;
		}

	}

	/*
	 * @description:字符型时间("2011/09/10 "、"2011-09-10 ")转换为Utc时间
	 * @date: 2016-01-11下午16:43:11
	 * @author: yangyong
	 * @throws ParseException
	 */
	public static Long stringToUtcTime(String stringTime) throws ParseException {

		// 将本地当前时间转换成UTC国际标准时间的毫秒形式
		if (stringTime != null) {
			log.debug("[dateToUtcTime] date:" + stringTime);
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date date = format.parse(stringTime.replace("/", "-"));
			Calendar calendar = Calendar.getInstance();
			// int offset = calendar.get(Calendar.ZONE_OFFSET);
			// int dst = calendar.get(Calendar.DST_OFFSET);
			calendar.setTime(date);
			// calendar.add(Calendar.MILLISECOND, -(offset + dst));
			return calendar.getTimeInMillis();
		} else {
			return null;
		}

	}

	/*
	 * 将date类型转换为utc时间数
	 *
	 * @param date
	 *            时间
	 * @return utc时间数
	 */
	public static Long dateToUtcTime(Date date) {

		// 将本地当前时间转换成UTC国际标准时间的毫秒形式
		if (date != null) {
			Calendar calendar = Calendar.getInstance();
			// int offset = calendar.get(Calendar.ZONE_OFFSET);
			// int dst = calendar.get(Calendar.DST_OFFSET);
			calendar.setTime(date);
			// calendar.add(Calendar.MILLISECOND, -(offset + dst));
			return calendar.getTimeInMillis();
		} else {
			return null;
		}

	}

	/*
	 *
	 * @param forMatStr
	 *            输入的时间日期类型字符串格式
	 * @param dateStr
	 *            输入的时间日期类型字符串
	 * @return 生成的时间日期
	 */
	public static Date getDate(String forMatStr, String dateStr) throws Exception {

		SimpleDateFormat sdf = new SimpleDateFormat(forMatStr);

		Date checkDate = null;

		checkDate = sdf.parse(dateStr);

		return checkDate;
	}

	/*
	 * @info 默认时间日期类型字符串格式 "yyyy-MM-dd hh:mm:ss"
	 * @param dateStr
	 *            输入的时间日期类型字符串
	 * @return 生成的时间日期
	 */
	public static Date getDate(String dateStr) throws Exception {

		SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_FORMATSTR, java.util.Locale.US);

		Date checkDate = null;

		checkDate = sdf.parse(dateStr);

		return checkDate;
	}

	/*
	 * 日期转化日期字符串
	 *
	 * @param format
	 *            格式
	 * @param date
	 *            日期
	 * @return
	 */
	public static String getDateStr(String format, Date date) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String dateStr = null;

		dateStr = sdf.format(date);

		return dateStr;
	}

	/*
	 * 两个日期之间的分钟数
	 *
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static double getBetweenMinutes(Date date1, Date date2) {
		long ss = (date1.getTime() - date2.getTime());
		double minutes = ss / 1000 / 60;
		return Math.abs(minutes);
	}

	/*
	 * 转换为timestamp类型
	 *
	 * @param timeStr
	 *            yyyy-MM-dd hh:mm:ss.fff
	 * @return
	 */
	public static Timestamp getTimestamp(String timeStr) throws Exception {

		Timestamp ts = null;

		ts = Timestamp.valueOf(timeStr);
		//System.out.println(ts);

		return ts;
	}

	/*
	 * 获取UTC时间
	 *
	 * @param dayNum
	 *            天数,以当天零点为基数，指定天数UTC时间(如传递-1获取前一天零点UTC时间)
	 * @return
	 */
	public static long getZeroUtc(int dayNum) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, dayNum);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTimeInMillis();
	}

	public static long getLastDayOfLastMonth() {
		Calendar calendar3 = Calendar.getInstance();
		calendar3.set(Calendar.DAY_OF_MONTH, 1);
		calendar3.add(Calendar.DAY_OF_MONTH, -1);
		calendar3.set(Calendar.AM_PM, Calendar.PM);
		calendar3.set(Calendar.HOUR, 11);
		calendar3.set(Calendar.MINUTE, 59);
		calendar3.set(Calendar.SECOND, 59);
		calendar3.set(Calendar.MILLISECOND, 999);
		return calendar3.getTimeInMillis();
	}
	/*
	 * 获取某月份的第一天
	 * @param flag 0当前月 1下月 -1上月
	 * @return 格式：yyyy-MM-01 00:00:00
	 */
	public static String getMoth(int flag){
		Calendar c = Calendar.getInstance();
		int moth = 0;
		c.add(Calendar.MONTH, flag);
		moth = c.get(Calendar.MONTH)+1;
		if(moth==12){
			c.add(Calendar.DAY_OF_YEAR, +1);
		}
		c.set(Calendar.DAY_OF_MONTH,1);
		String s = lFormat.format(c.getTime());
		s+=" 00:00:00";
		return s;
	}
	/*
	 * 通过标准时间格式获取对应的long值
	 * @param dateTime 标准时间
	 * @param formatStr	时间格式
	 * @return
	 */
	public static Long getDateToLongByStr(String dateTime,String formatStr){
		SimpleDateFormat format = new SimpleDateFormat(formatStr);
		Date date = null;
		try {
			date = format.parse(dateTime);
		} catch (ParseException e) {

		}
		return date.getTime();
	}
	/*
	 *
	 * @return
	 */
	public static String getLastDayOfLastMonthStr() {
		return lFormat.format(new Date(getLastDayOfLastMonth()));
	}



	public static void main(String[] arg) throws Exception {
		System.out.println(getLastDayOfLastMonthStr());
	}
	/*
	 * 通过毫秒获取 标准时间 （yyyy-MM-dd HH:mm:ss）
	 * @param millis
	 * @return
	 */
	public static String getDateStrByLong(Long millis){
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(millis);
		Date date = c.getTime();
		return lFormat_time.format(date);
	}
	/*
	 * 通过毫秒获取 标准时间 （yyyy-MM-dd）
	 * @param millis
	 * @return
	 */
	public static String getDateShortStrByLong(Long millis){
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(millis);
		Date date = c.getTime();
		return lFormat.format(date);
	}
	/*
	 * 设置超时时间
	 * @param startTime 生效时间	(毫秒为单位)
	 * @param timeoutDate 失效时间 (小时为单位)
	 * @return
	 */
	public static Long getTimeoutDate(Long startTime,Integer timeoutDate){
		return startTime+(1000*60*60*timeoutDate);
	}


	/*
	 * @info 默认时间日期类型字符串格式 "yyyy-MM-dd"
	 * @param dateStr
	 *            输入的时间日期类型字符串
	 * @return 生成的时间日期
	 */
	public static Date stringToDate(String dateStr) throws Exception {

		SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_FORMATSHORT, java.util.Locale.US);

		Date checkDate = null;

		checkDate = sdf.parse(dateStr);

		return checkDate;
	}
}
