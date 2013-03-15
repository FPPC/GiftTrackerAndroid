package gov.ca.fppc.fppcgifttracker.util;

public class MiscUtil {
	private static String [] month = {"January","February","March","April","May","June","July","August","September","October","November","December"};
	public static String month_name(int i) {
		if ((i>0) && (i<12)) return month[i-1];
		return "";
	}
}
