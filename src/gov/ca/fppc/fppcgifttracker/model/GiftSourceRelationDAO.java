package gov.ca.fppc.fppcgifttracker.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class GiftSourceRelationDAO {
	/**
	 * 
	 */


	private SQLiteDatabase db;
	private SQLiteHelper dbhelper;
	/*	private String[] allColumns = { SQLiteHelper.GIVING_ID, SQLiteHelper.VALUE, SQLiteHelper.SOURCE_ID, SQLiteHelper.GIFT_ID};
	 */
	public GiftSourceRelationDAO(Context context) {
		dbhelper = new SQLiteHelper(context);
	}

	public void open() throws SQLException {
		db = dbhelper.getWritableDatabase();
	}

	public void close() {
		dbhelper.close();
	}

	public double getValue(long gid, long sid) {
		String[] column = new String[] {SQLiteHelper.VALUE};
		String where = SQLiteHelper.GIFT_ID + " = ? AND "+ SQLiteHelper.SOURCE_ID + " = ?";
		String[] value = new String[] {Long.toString(gid),Long.toString(sid)};
		Cursor c = db.query(SQLiteHelper.TABLE_GIVING, column, where, value,null,null,null);
		if (c.moveToFirst()) {
			return c.getDouble(0);
		}
		return 0.0;
	}
	
	public double giftValue(long gift_id) {
		/* build the query*/ 
		String[] column = new String[] { SQLiteHelper.VALUE };
		String where = SQLiteHelper.GIFT_ID + " = ?";
		String[] value = new String[] { Long.toString(gift_id) };

		Cursor cursor = db.query(SQLiteHelper.TABLE_GIVING, column, where, value,
				null, null, null);
		double result = 0.0;
		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				result += cursor.getDouble(0);
				cursor.moveToNext();
			}
		}
		return result;
	}
	
	public double totalReceived(long sid, int year, int m) {
		/*
		 * Build raw query
		 * SELECT R.VALUE FROM 
		 * 		TABLE_GIVING R JOIN TABLE_GIFT G ON R.GIFT_ID = G.GIFT_ID 
		 * 		WHERE G.YEAR = YEAR AND G.MONTH = MONTH AND R.SOURCE_ID = SID;
		 */
		String query = "SELECT R."+SQLiteHelper.VALUE+" FROM "
				+SQLiteHelper.TABLE_GIVING+" R JOIN "+SQLiteHelper.TABLE_GIFT+" G ON R."+SQLiteHelper.GIFT_ID+" = G."+SQLiteHelper.GIFT_ID
				+" WHERE G."+SQLiteHelper.GIFT_YEAR+" = ? AND G."+SQLiteHelper.GIFT_MONTH+" = ? AND R."+SQLiteHelper.SOURCE_ID+" = ?;";
		String [] args = {""+year,""+m,""+sid};
		
		Cursor cursor = db.rawQuery(query,args);
		double sum = 0.0;
		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				sum+= cursor.getDouble(0);
				cursor.moveToNext();
			}
		}
		cursor.close();
		return sum;
	}
	
	public double totalReceived(long sid, int year) {
		/*
		 * Build raw query
		 * SELECT R.VALUE FROM 
		 * 		TABLE_GIVING R JOIN TABLE_GIFT G ON R.GIFT_ID = G.GIFT_ID 
		 * 		WHERE G.YEAR = YEAR;
		 */
		
		String query = "SELECT R."+SQLiteHelper.VALUE+" FROM "
				+SQLiteHelper.TABLE_GIVING+" R JOIN "+SQLiteHelper.TABLE_GIFT+" G ON R."+SQLiteHelper.GIFT_ID+" = G."+SQLiteHelper.GIFT_ID
				+" WHERE G."+SQLiteHelper.GIFT_YEAR+" = ? AND R."+SQLiteHelper.SOURCE_ID+" =?;";
		String [] args = {""+year,""+sid};
		Cursor cursor = db.rawQuery(query,args);
		double sum = 0.0;
		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				sum+= cursor.getDouble(0);
				cursor.moveToNext();
			}
		}
		cursor.close();
		return sum;
	}

	public double totalReceived(int year, int month) {
		/*
		 * Build raw query
		 * SELECT VALUE FROM 
		 * 		TABLE_GIVING R JOIN TABLE_GIFT G ON R.GIFT_ID = G.GIFT_ID 
		 * 		WHERE G.YEAR = YEAR AND G.MONTH = MONTH;
		 */
		
		String query = "SELECT R."+SQLiteHelper.VALUE+" FROM "
				+SQLiteHelper.TABLE_GIVING+" R JOIN "+SQLiteHelper.TABLE_GIFT+" G ON R."+SQLiteHelper.GIFT_ID+" = G."+SQLiteHelper.GIFT_ID
				+" WHERE G."+SQLiteHelper.GIFT_YEAR+" = ? AND G."+SQLiteHelper.GIFT_MONTH+" = ?;";
		String [] args = {""+year,""+month};
		Cursor cursor = db.rawQuery(query,args);
		double sum = 0.0;
		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				sum+= cursor.getDouble(0);
				cursor.moveToNext();
			}
		}
		cursor.close();
		return sum;
	}	
	
	public double totalReceived(int year) {
		/*
		 * Build raw query
		 * SELECT VALUE FROM 
		 * 		TABLE_GIVING R JOIN TABLE_GIFT G ON R.GIFT_ID = G.GIFT_ID 
		 * 		WHERE G.YEAR = YEAR;
		 */
		
		String query = "SELECT R."+SQLiteHelper.VALUE+" FROM "
				+SQLiteHelper.TABLE_GIVING+" R JOIN "+SQLiteHelper.TABLE_GIFT+" G ON R."+SQLiteHelper.GIFT_ID+" = G."+SQLiteHelper.GIFT_ID
				+" WHERE G."+SQLiteHelper.GIFT_YEAR+" = ?;";
		String [] args = {""+year};
		Cursor cursor = db.rawQuery(query,args);
		double sum = 0.0;
		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				sum+= cursor.getDouble(0);
				cursor.moveToNext();
			}
		}
		cursor.close();
		return sum;
	}	public long createRelation(long sourceID, long giftID, double value) {
		ContentValues values = new ContentValues();
		values.put(SQLiteHelper.SOURCE_ID, sourceID);
		values.put(SQLiteHelper.GIFT_ID, giftID);
		values.put(SQLiteHelper.VALUE, value);

		return db.insert(SQLiteHelper.TABLE_GIVING, null, values);
	}

	public void deleteRelation(long sourceID, long giftID) {
		db.delete(SQLiteHelper.TABLE_GIVING,SQLiteHelper.SOURCE_ID + " = " + sourceID 
				+ " AND " + SQLiteHelper.GIFT_ID + " = " + giftID,null);
	}

	public void deleteByGiftID(long giftID) {
		db.delete(SQLiteHelper.TABLE_GIVING,SQLiteHelper.GIFT_ID+" = "+giftID,null);
	}

	public void deleteBySourceID(long sourceID) {
		db.delete(SQLiteHelper.TABLE_GIVING,SQLiteHelper.SOURCE_ID+" = "+sourceID,null);		
	}

	public long updateValue(long sid, long gid, double newValue) {
		ContentValues v = new ContentValues();
		v.put(SQLiteHelper.VALUE, newValue);
		return db.update(SQLiteHelper.TABLE_GIVING, v, SQLiteHelper.SOURCE_ID + " = " + sid 
				+ " AND " + SQLiteHelper.GIFT_ID + " = " + gid, null);

	}

}
