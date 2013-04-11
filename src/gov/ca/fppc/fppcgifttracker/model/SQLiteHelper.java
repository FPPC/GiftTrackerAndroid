package gov.ca.fppc.fppcgifttracker.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteHelper extends SQLiteOpenHelper {
	// constant for column name, table name, and database name
	public static final String TABLE_SOURCE = "sources";
	
	/*
	 * Source table
	 */
	public static final String SOURCE_NAME = "src_name";
	public static final String SOURCE_ID = "src_id";
	public static final String SOURCE_ADDR1 = "src_address1";
	public static final String SOURCE_ADDR2 = "src_address2";
	public static final String SOURCE_CITY = "src_city";
	public static final String SOURCE_STATE = "src_state";
	public static final String SOURCE_ZIP = "src_zip";
	public static final String SOURCE_ACTI = "src_activity";
	public static final String SOURCE_LOBBY = "src_lobby";
	public static final String SOURCE_EMAIL = "src_email";
	public static final String SOURCE_PHONE = "src_phone";
	

	/*
	 * Gift table
	 */
	public static final String TABLE_GIFT = "gifts";
	public static final String GIFT_DAY = "gft_day";
	public static final String GIFT_MONTH = "gft_month";
	public static final String GIFT_YEAR = "gft_year";
	public static final String GIFT_ID = "gft_id";
	public static final String GIFT_DESCRIPTION = "gft_description";
	
	/*
	 * Giving table - relationship between source and gift
	 */
	public static final String TABLE_GIVING ="giving";
	public static final String GIVING_ID = "giving_id";
	public static final String VALUE = "value";
	
	/*
	 * FTS tables
	 */
	public static final String SOURCE_TABLE_FTS = "indexed_sources";
	public static final String GIFT_TABLE_FTS = "indexed_gifts";
	public static final String DOC_ID = "docid";
	public static final String CONTENT = "content";

	private static final String DATABASE_NAME = "FORM700D";
	private static final int DATABASE_VERSION = 2;


	
	
	/*
	 * // build create table query
	 * Essentially:
	 * CREATE TABLE TABLE_SOURCE (
	 * 		SOURCE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
	 * 		SOURCE_NAME TEXT NOT NULL,
	 * 		SOURCE_ADDR TEXT,
	 * 		SOURCE_ACTI TEXT,
	 * 		SOURCE_LOBBY INTEGER);
	 */
	private static final String SOURCE_CREATE = "CREATE TABLE " + TABLE_SOURCE
			+ "(" + SOURCE_ID + " integer primary key autoincrement, "
			+ SOURCE_NAME + " text not null, " + SOURCE_ADDR1 + " text, "
			+ SOURCE_ADDR2 + " text, " + SOURCE_CITY + " text, " + SOURCE_STATE + " text, "
			+ SOURCE_ZIP + " text, "
			+ SOURCE_ACTI + " text, " + SOURCE_LOBBY + " integer, " 
			+ SOURCE_EMAIL + " text, " + SOURCE_PHONE + " text);";
	/*
	 * CREATE TABLE TABLE_GIFT(
	 * 		GIFT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
	 * 		GIFT_YEAR INTEGER,
	 * 		GIFT_MONTH INTEGER,
	 * 		GIFT_DAY INTEGER,
	 * 		GIFT_DESCRIPTION TEXT);
	 */
	private static final String GIFT_CREATE = "create table " + TABLE_GIFT
			+ "(" + GIFT_ID + " integer primary key autoincrement, "
			+ GIFT_YEAR + " integer, " 
			+ GIFT_MONTH + " integer, " 
			+ GIFT_DAY + " integer, " 
			+  GIFT_DESCRIPTION + " text);";
	
	/*
	 * CREATE TABLE TABLE_GIVING (
	 * 		GIVING_ID INTEGER PRIMARY KEY AUTOINCREMENT,
	 * 		VALUE REAL,
	 * 		SOURCE_ID INTEGER,
	 * 		GIFT_ID INTEGER,
	 * 		FOREIGN KEY(SOURCE_ID) REFERENCES TABLE_SOURCE(SOURCE_ID),
	 * 		FOREIGN KEY(GIFT_ID) REFERENCES TABLE_GIFT(GIFT_ID));
	 */
	private static final String GIVING_CREATE = "CREATE TABLE " + TABLE_GIVING
			+ "(" + GIVING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ VALUE + " REAL, "
			+ SOURCE_ID + " INTEGER, "
			+ GIFT_ID + " INTEGER, "
			+ "FOREIGN KEY("+SOURCE_ID+") REFERENCES "+TABLE_SOURCE+"("+SOURCE_ID+"), "
			+ "FOREIGN KEY("+GIFT_ID+") REFERENCES "+TABLE_GIFT+"("+GIFT_ID+"));";

	/*
	 * Build create index table query for fast source search
	 * CREATE VIRTUAL TABLE SOURCE_TABLE_FTS USING FTS4(CONTENT);
	 */
	private static final String SOURCE_SEARCH_CREATE = "CREATE VIRTUAL TABLE " + SOURCE_TABLE_FTS
			+ " USING fts4(" + CONTENT+ ");";
	
	/*
	 * CREATE VIRTUAL TABLE GIFT_TABLE_FTS USING FTS4(CONTENT);
	 */
	private static final String GIFT_SEARCH_CREATE = "CREATE VIRTUAL TABLE " + GIFT_TABLE_FTS + " USING FTS4("+CONTENT+");";
	
	public SQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(SOURCE_CREATE);
		database.execSQL(GIFT_CREATE);
		database.execSQL(GIVING_CREATE);
		database.execSQL(SOURCE_SEARCH_CREATE);
		database.execSQL(GIFT_SEARCH_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(SQLiteHelper.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", all old data shall now be destroyed");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SOURCE);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_GIFT);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_GIVING);
		db.execSQL("DROP TABLE IF EXISTS " + SOURCE_TABLE_FTS);
		db.execSQL("DROP TABLE IF EXISTS " + GIFT_TABLE_FTS);
		onCreate(db);
	}
}
