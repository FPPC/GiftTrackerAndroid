package gov.ca.fppc.fppcgifttracker.controller;

public class Constant {
	public static final String SRC = "SOURCE";
	public static final String GFT = "GIFT";	
	public static final String SRC_DAO = "SRC_DAO";
	public static final String GFT_DAO = "GFT_DAO";
	public static final String MODE = "mode";
	public static final int EDIT = 1;
	public static final int NEW = 2;
	public static final int DELETE = 3;
	public static final int OTHER = 4;
	public static final int SEND = 5;
	public static final int CANCEL = 6;
	public static final int APPEND_SOURCE = 3;
	public static double GIFT_LIMIT = 440.0;
	public static double LOBBY_LIMIT = 10.0;
	public static final String SUBJECT = "Excel file exported from Gift Tracking App";
	public static final String MESSAGE = "This is an automated email sent from Android FPPC Gift Tracking App." +"\n"
			+"Attached is the filled out Schedule D of form 700.";
	public static final String FILE_LOC = "/sdcard/download/ScheduleD.xls";
	
	public static int currentYear = 2013;
	
	public static final int [] years = {2012, 2013, 2014};
	public static final double [] normalLimit  = {420.0, 440.0, 440.0};
	public static final double [] lobbyLimit = {10.0, 10.0, 10.0};
}
