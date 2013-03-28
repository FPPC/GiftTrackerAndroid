package gov.ca.fppc.fppcgifttracker.util;

public class MiscUtil {
	private static final String [] month = {"January","February","March","April","May","June","July","August","September","October","November","December"};
	private static final int[] numberOfDays={31,28,31,30,31,30,31,31,30,31,30,31};
	public static String month_name(int i) {
		if ((i >= 1) && (i <= 12)) return month[i-1];
		return "";
	}
	
	public static boolean ValidateDate(int year, int month, int day) {
		int febadd = 0;
		int limit;
		if (isLeap(year)) {febadd=1;}
		if (month > 12 || month < 1) return false;
		limit = numberOfDays[month-1];
		if (month == 2) {limit+=febadd;}
		if (day > limit || day < 1) return false;
		return true;
	}
	
	private static boolean isLeap(int year) {
		/* not divisible by 4, not leap year */
		if ((year % 4) !=0) {
			return false;
		}
		
		/* divisible by 4, 100, but not 400, not leap year */
		if (((year % 100) ==0) && ((year % 400)!=0)) {
			return false;
		}
		return true;
	}
}
