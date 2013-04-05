package gov.ca.fppc.fppcgifttracker.model;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

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

	public List<String> nameOfDonor(long gid) {
		ArrayList<String> result = new ArrayList<String>();
		/*raw query builder */
		/*
		 * SELECT S.SOURCE_NAME 
		 * FROM TABLE_SOURCE S JOIN TABLE_GIVING G 
		 * ON G.SOURCE_ID = S.SOURCE_ID
		 * AND G.GIFT_ID = ?
		 */
		String query = "SELECT S."+SQLiteHelper.SOURCE_NAME+" "
				+"FROM "+SQLiteHelper.TABLE_SOURCE+" S JOIN "+SQLiteHelper.TABLE_GIVING+" G "
				+"ON G."+SQLiteHelper.SOURCE_ID+" = S."+SQLiteHelper.SOURCE_ID+" "
				+"AND G."+SQLiteHelper.GIFT_ID+" = ?";
		String [] args = {""+gid};
		Cursor cursor = db.rawQuery(query, args);
		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				result.add(cursor.getString(0));
				cursor.moveToNext();
			}
		}
		return result;
	}

	public List<Source> listOfDonor(long gid) {
		ArrayList<Source> result = new ArrayList<Source>();
		String query = "SELECT S.* FROM "
				+SQLiteHelper.TABLE_SOURCE+" S JOIN "+SQLiteHelper.TABLE_GIVING+" G "
				+"ON G."+SQLiteHelper.SOURCE_ID+" = S."+SQLiteHelper.SOURCE_ID+" "
				+"AND G."+SQLiteHelper.GIFT_ID+" = ?";
		String [] args = {""+gid};
		Cursor cursor = db.rawQuery(query, args);
		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				result.add(SourceDAO.cursorToSource(cursor));
				cursor.moveToNext();
			}
		}
		return result;
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
	}	
	public long createRelationIfNotExist(long sourceID, long giftID, double value) {
		ContentValues values = new ContentValues();
		values.put(SQLiteHelper.SOURCE_ID, sourceID);
		values.put(SQLiteHelper.GIFT_ID, giftID);
		values.put(SQLiteHelper.VALUE, value);

		String[] column = new String[] {SQLiteHelper.VALUE};
		String where = SQLiteHelper.GIFT_ID + " = ? AND "+ SQLiteHelper.SOURCE_ID + " = ?";
		String[] v = new String[] {Long.toString(giftID),Long.toString(sourceID)};
		Cursor c = db.query(SQLiteHelper.TABLE_GIVING, column, where, v,null,null,null);
		if (c.moveToFirst()) {
			return db.update(SQLiteHelper.TABLE_GIVING, values, where, v);
		}		
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

	public List<Gift> allGiftFrom(long sid, int year) {
		ArrayList<Gift> result = new ArrayList<Gift>();
		/* raw query
		 * SELECT G.* FROM TABLE_GIVING V 
		 * JOIN TABLE_GIFT G ON G.GIFT_ID = V.GIFT_ID
		 * JOIN TABLE_SOURCE S ON S.SOURCE_ID = V.SOURCE_ID
		 * WHERE S.SOURCE_ID = ? AND G.GIFT_YEAR = ?;
		 */
		String query = "SELECT G.* FROM " + SQLiteHelper.TABLE_GIVING +" V "
				+"JOIN "+SQLiteHelper.TABLE_GIFT+" G ON G."+SQLiteHelper.GIFT_ID+" = V."+SQLiteHelper.GIFT_ID+" "
				+"JOIN "+SQLiteHelper.TABLE_SOURCE+" S ON S."+SQLiteHelper.SOURCE_ID+" = V."+SQLiteHelper.SOURCE_ID+" "
				+"WHERE S."+SQLiteHelper.SOURCE_ID+" = ? AND G."+SQLiteHelper.GIFT_YEAR+" = ?";
		String [] args = {""+sid,""+year};
		Cursor cursor = db.rawQuery(query, args);
		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				Gift gift = GiftDAO.cursorToGift(cursor);
				result.add(gift);
				cursor.moveToNext();
			}
		}
		cursor.close();
		return result;
	}

	public List<Gift> filterGiftFrom(long sid, int year, String s) {
		ArrayList<Gift> result = new ArrayList<Gift>();
		/*raw query
		 * SELECT G.* FROM TABLE_GIVING V
		 * JOIN TABLE_GIFT G ON G.GIFT_ID = V.GIFT_ID
		 * JOIN TABLE_SOURCE S ON S.SOURCE_ID = V.SOURCE_ID
		 * JOIN INDEXED_GIFT I ON V.GIFT_ID = I.DOC_ID
		 * WHERE S.SOURCE_ID = ? AND I.CONTENT MATCH ? AND G.GIFT_YEAR = ?"
		 */
		String query = "SELECT G.* FROM " + SQLiteHelper.TABLE_GIVING +" V "
				+"JOIN "+SQLiteHelper.TABLE_GIFT+" G ON G."+SQLiteHelper.GIFT_ID+" = V."+SQLiteHelper.GIFT_ID+" "
				+"JOIN "+SQLiteHelper.TABLE_SOURCE+" S ON S."+SQLiteHelper.SOURCE_ID+" = V."+SQLiteHelper.SOURCE_ID+" "
				+"JOIN "+SQLiteHelper.GIFT_TABLE_FTS+" I ON V."+SQLiteHelper.GIFT_ID+" = I."+SQLiteHelper.DOC_ID+" "
				+"WHERE S."+SQLiteHelper.SOURCE_ID+" = ? AND I."+SQLiteHelper.CONTENT+" MATCH ? AND G."+SQLiteHelper.GIFT_YEAR+" = ?";
		String [] args = {""+sid,s+"*",""+year};
		Cursor cursor = db.rawQuery(query, args);
		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				Gift gift = GiftDAO.cursorToGift(cursor);
				result.add(gift);
				cursor.moveToNext();
			}
		}
		cursor.close();
		return result;
	}

}
