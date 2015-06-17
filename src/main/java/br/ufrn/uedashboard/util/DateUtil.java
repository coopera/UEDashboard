package br.ufrn.uedashboard.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	
	public SimpleDateFormat getDateFormatter() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		return formatter;
	}
	
	public boolean isWeekend(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		
	    return (Calendar.SUNDAY == dayOfWeek || Calendar.SATURDAY == dayOfWeek);
	}
	
	public boolean isFriday(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		
		return dayOfWeek == Calendar.FRIDAY;
	}
	
	public Date getZeroTimeDate(Date date) {
	    Date res = date;
	    Calendar calendar = Calendar.getInstance();

	    calendar.setTime(date);
	    calendar.set(Calendar.HOUR_OF_DAY, 0);
	    calendar.set(Calendar.MINUTE, 0);
	    calendar.set(Calendar.SECOND, 0);
	    calendar.set(Calendar.MILLISECOND, 0);

	    res = calendar.getTime();

	    return res;
	}
	
	public int daysBetween(Date d1, Date d2){
        return (int)( (d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
	}
	
	public Date getNextDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, 1);
		return calendar.getTime();
	}
	
	public int getMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		
		return month;
	}
	
	public String getDayOfWeek(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		
		if (dayOfWeek == Calendar.SATURDAY) {
			return "Saturday";
		} else if (dayOfWeek == Calendar.SUNDAY) {
			return "Sunday";
		} else if (dayOfWeek == Calendar.MONDAY) {
			return "Monday";
		} else if (dayOfWeek == Calendar.TUESDAY) {
			return "Tuesday";
		} else if (dayOfWeek == Calendar.WEDNESDAY) {
			return "Wednesday";
		} else if (dayOfWeek == Calendar.THURSDAY) {
			return "Thursday";
		} else {
			return "Friday";
		}
	}

}
