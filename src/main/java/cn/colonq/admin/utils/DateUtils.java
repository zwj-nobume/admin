package cn.colonq.admin.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.springframework.stereotype.Component;

import cn.colonq.admin.config.ServiceException;

@Component
public class DateUtils {
	private final ThreadSafePool<Calendar> calendarPool;
	private final SimpleDateFormat sdf;

	public DateUtils(ThreadSafePool<Calendar> calendarPool) {
		this.calendarPool = calendarPool;
		this.sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		this.sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
	}

	public Date getNowDate() {
		final Calendar safeCal = calendarPool.getItem();
		safeCal.setTimeInMillis(System.currentTimeMillis());
		return getTime(safeCal);
	}

	public String format(final Date date) {
		return sdf.format(date);
	}

	public Date parseTime(final String format) {
		try {
			return sdf.parse(format);
		} catch (ParseException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	public Calendar getCalendar() {
		return calendarPool.getItem();
	}

	public void putCalendar(final Calendar cal) {
		calendarPool.putItem(cal);
	}

	public Date getTime(final long timeMillis) {
		final Calendar safeCal = calendarPool.getItem();
		safeCal.setTimeInMillis(timeMillis);
		return getTime(safeCal);
	}

	public long getTimeMillis(final Date dateTime) {
		return dateTime == null ? 0 : dateTime.getTime();
	}

	/**
	 * 获取多少分钟之后
	 * 
	 * @param minutes
	 * @return
	 */
	public Date getMinutesAfter(final int minutes) {
		final Calendar safeCal = calendarPool.getItem();
		safeCal.setTimeInMillis(System.currentTimeMillis());
		safeCal.add(Calendar.MINUTE, minutes);
		return getTime(safeCal);
	}

	/**
	 * 获取多少小时之后
	 * 
	 * @param minutes
	 * @return
	 */
	public Date getHourAfter(final int hour) {
		final Calendar safeCal = calendarPool.getItem();
		safeCal.setTimeInMillis(System.currentTimeMillis());
		safeCal.add(Calendar.HOUR, hour);
		return getTime(safeCal);
	}

	/**
	 * 获取多少天之后
	 * 
	 * @param minutes
	 * @return
	 */
	public Date getDaysAfter(final int days) {
		final Calendar safeCal = calendarPool.getItem();
		safeCal.setTimeInMillis(System.currentTimeMillis());
		safeCal.add(Calendar.DATE, days);
		return getTime(safeCal);
	}

	/**
	 * 获取多少周之后
	 * 
	 * @param minutes
	 * @return
	 */
	public Date getWeeksAfter(final int weeks) {
		final Calendar safeCal = calendarPool.getItem();
		safeCal.setTimeInMillis(System.currentTimeMillis());
		safeCal.add(Calendar.WEEK_OF_YEAR, weeks);
		return getTime(safeCal);
	}

	/**
	 * 获取多少月之后
	 * 
	 * @param minutes
	 * @return
	 */
	public Date getMonthAfter(final int month) {
		final Calendar safeCal = calendarPool.getItem();
		safeCal.setTimeInMillis(System.currentTimeMillis());
		safeCal.add(Calendar.MONTH, month);
		return getTime(safeCal);
	}

	/**
	 * 获取多少年之后
	 * 
	 * @param minutes
	 * @return
	 */
	public Date getYearAfter(final int years) {
		final Calendar safeCal = calendarPool.getItem();
		safeCal.setTimeInMillis(System.currentTimeMillis());
		safeCal.add(Calendar.YEAR, years);
		return getTime(safeCal);
	}

	/**
	 * 获取今天最开始
	 * 
	 * @return
	 */
	public Date getTodayBegin() {
		final Calendar safeCal = calendarPool.getItem();
		safeCal.setTimeInMillis(System.currentTimeMillis());
		return getBegin(safeCal);
	}

	/**
	 * 获取今天结束
	 * 
	 * @return
	 */
	public Date getTodayEnd() {
		final Calendar safeCal = calendarPool.getItem();
		safeCal.setTimeInMillis(System.currentTimeMillis());
		return getEnd(safeCal);
	}

	/**
	 * 获取本周开始
	 * 
	 * @return
	 */
	public Date getThisWeekBegin() {
		final Calendar safeCal = calendarPool.getItem();
		safeCal.setTimeInMillis(System.currentTimeMillis());
		if (safeCal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
			safeCal.add(Calendar.DATE, -1);
		}
		safeCal.add(Calendar.DAY_OF_WEEK, 2 - safeCal.get(Calendar.DAY_OF_WEEK));
		return getBegin(safeCal);
	}

	/**
	 * 获取本周结束
	 * 
	 * @return
	 */
	public Date getThisWeekEnd() {
		final Calendar safeCal = calendarPool.getItem();
		safeCal.setTimeInMillis(System.currentTimeMillis());
		safeCal.set(Calendar.DAY_OF_WEEK, 8);
		return getEnd(safeCal);
	}

	/**
	 * 获取本月最初
	 * 
	 * @return
	 */
	public Date getThisMonthBegin() {
		final Calendar safeCal = calendarPool.getItem();
		safeCal.setTimeInMillis(System.currentTimeMillis());
		return getMonthBegin(safeCal);
	}

	/**
	 * 获取本月结束
	 * 
	 * @return
	 */
	public Date getThisMonthEnd() {
		final Calendar safeCal = calendarPool.getItem();
		safeCal.setTimeInMillis(System.currentTimeMillis());
		return getMonthEnd(safeCal);
	}

	/**
	 * 获取本年最初
	 * 
	 * @return
	 */
	public Date getThisYearBegin() {
		final Calendar safeCal = calendarPool.getItem();
		safeCal.setTimeInMillis(System.currentTimeMillis());
		safeCal.set(Calendar.MONTH, 0);
		return getMonthBegin(safeCal);
	}

	/**
	 * 获取本年结束
	 * 
	 * @return
	 */
	public Date getThisYearEnd() {
		final Calendar safeCal = calendarPool.getItem();
		safeCal.setTimeInMillis(System.currentTimeMillis());
		safeCal.set(Calendar.MONTH, 11);
		return getMonthEnd(safeCal);
	}

	private Date getBegin(final Calendar safeCal) {
		safeCal.set(Calendar.HOUR_OF_DAY, 0);
		safeCal.set(Calendar.MINUTE, 0);
		safeCal.set(Calendar.SECOND, 0);
		safeCal.set(Calendar.MILLISECOND, 0);
		return getTime(safeCal);
	}

	private Date getEnd(final Calendar safeCal) {
		safeCal.set(Calendar.HOUR_OF_DAY, 23);
		safeCal.set(Calendar.MINUTE, 59);
		safeCal.set(Calendar.SECOND, 59);
		safeCal.set(Calendar.MILLISECOND, 999);
		return getTime(safeCal);
	}

	private Date getMonthBegin(final Calendar safeCal) {
		safeCal.set(Calendar.DAY_OF_MONTH, 1);
		return getBegin(safeCal);
	}

	private Date getMonthEnd(final Calendar safeCal) {
		safeCal.set(Calendar.DAY_OF_MONTH, safeCal.getActualMaximum(Calendar.DAY_OF_MONTH));
		return getBegin(safeCal);
	}

	private Date getTime(final Calendar safeCal) {
		final Date time = safeCal.getTime();
		calendarPool.putItem(safeCal);
		return time;
	}
}
