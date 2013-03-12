package gov.ca.fppc.fppcgifttracker.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteHelper extends SQLiteOpenHelper {
	// constant for column name, table name, and database name
	public static final String TABLE_SOURCE = "sources";
	public static final String SOURCE_NAME = "src_name";
	public static final String SOURCE_ID = "src_id";
	public static final String SOURCE_ADDR = "src_address";
	public static final String SOURCE_ACTI = "src_activity";
	public static final String SOURCE_LOBBY = "src_lobby";
	public static final String SOURCE_LIMIT = "src_limit";

	public static final String TABLE_GIFT = "gifts";
	public static final String GIFT_DATE = "gft_date";
	public static final String GIFT_ID = "gft_id";
	public static final String GIFT_VALUE = "gft_value";
	public static final String GIFT_DESCRIPTION = "gft_description";

	private static final String DATABASE_NAME = "FORM700D";
	private static final int DATABASE_VERSION = 1;

	// build create table query

	private static final String SOURCE_CREATE = "create table " + TABLE_SOURCE
			+ "(" + SOURCE_ID + " integer primary key autoincrement, "
			+ SOURCE_NAME + " text not null, " + SOURCE_ADDR + " text, "
			+ SOURCE_ACTI + " text, " + SOURCE_LOBBY + " integer, "
			+ SOURCE_LIMIT + " real);";

	private static final String GIFT_CREATE = "create table " + TABLE_GIFT
			+ "(" + GIFT_ID + " integer primary key autoincrement, "
			+ SOURCE_ID + " integer, " + GIFT_DATE + " integer, " + GIFT_VALUE
			+ " real, " + GIFT_DESCRIPTION + " text);";

	public SQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(SOURCE_CREATE);
		database.execSQL(GIFT_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(SQLiteHelper.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", all old data shall now be destroyed");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SOURCE);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_GIFT);
		onCreate(db);
	}
}
