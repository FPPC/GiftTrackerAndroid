package gov.ca.fppc.fppcgifttracker.model;

import java.util.ArrayList;
import gov.ca.fppc.fppcgifttracker.util.MiscUtil;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class GiftDAO {
	/**
	 * 
	 */
	
	
	private SQLiteDatabase db;
	private SQLiteHelper dbhelper;
	private String[] allColumns = { SQLiteHelper.GIFT_ID, SQLiteHelper.GIFT_YEAR, SQLiteHelper.GIFT_MONTH, 
			SQLiteHelper.GIFT_DAY, SQLiteHelper.GIFT_DESCRIPTION };
	
	public GiftDAO(Context context) {
		dbhelper = new SQLiteHelper(context);
	}

	public void open() throws SQLException {
		db = dbhelper.getWritableDatabase();
	}

	public void close() {
		dbhelper.close();
	}

	public Gift createGift(long sourceID, int year, int month, int day, String description) {
		ContentValues values = new ContentValues();
		values.put(SQLiteHelper.SOURCE_ID, sourceID);
		values.put(SQLiteHelper.GIFT_YEAR, year);
		values.put(SQLiteHelper.GIFT_MONTH, month);
		values.put(SQLiteHelper.GIFT_DAY, day);
		values.put(SQLiteHelper.GIFT_DESCRIPTION, description);

		long insertID = db.insert(SQLiteHelper.TABLE_GIFT, null, values);

		/*
		 * create the index entry for search
		 */
		values.clear();
		String content = String.format("%2d/%2d/%4d", month, day, year) + " " + MiscUtil.month_name(month)+ " " + description;
		values.put(SQLiteHelper.CONTENT, content);
		values.put(SQLiteHelper.DOC_ID, insertID);

		long verifyID = db.insert(SQLiteHelper.GIFT_TABLE_FTS, null, values);

		/*
		 * DEBUG .. I just love how true-to-nature the log method name is
		 */
		if (verifyID != insertID) {
			android.util.Log.wtf("GIFT_DAO","DOC_ID != GIFT_ID : GiftDAO.java line 59");
		}

		/*
		 * Verify entry recorded
		 */
		Cursor cursor = db.query(SQLiteHelper.TABLE_GIFT, // table
				allColumns, // columns
				SQLiteHelper.GIFT_ID + " = " + insertID, // selection
				null, null, null, null);
		Gift newGift = null;
		if (cursor.moveToFirst()) {
			newGift = cursorToGift(cursor);
		}
		cursor.close();
		return newGift;
	}

	public long updateGift(Gift gift) {
		ContentValues values = new ContentValues();
		values.put(SQLiteHelper.GIFT_YEAR, gift.getYear());
		values.put(SQLiteHelper.GIFT_MONTH, gift.getMonth());
		values.put(SQLiteHelper.GIFT_DAY, gift.getDay());
		values.put(SQLiteHelper.GIFT_DESCRIPTION, gift.getDescription());
		long updateID = gift.getID();
		updateID = db.update(SQLiteHelper.TABLE_GIFT, values,
				SQLiteHelper.GIFT_ID + " = " + updateID, null);
		/*
		 * Update the index too
		 */
		values.clear();
		String content = String.format("%2d/%2d/%4d", gift.getMonth(), gift.getDay(), gift.getYear())
				+ " " + MiscUtil.month_name(gift.getMonth())+ " " + gift.getDescription();
		values.put(SQLiteHelper.CONTENT, content);
		long verifyID = db.update(SQLiteHelper.GIFT_TABLE_FTS, values, SQLiteHelper.DOC_ID+" = " + updateID,null);

		/*
		 * DEBUG .. I just love how true-to-nature the log method name is
		 */
		if (verifyID != updateID) {
			android.util.Log.wtf("GIFT_DAO","DOC_ID != GIFT_ID : GiftDAO.java line 97");
		}

		return updateID;
	}

	private Gift cursorToGift(Cursor cursor) {
		Gift gft = new Gift();
		gft.setID(cursor.getLong(0));
		gft.setYear(cursor.getInt(1));
		gft.setMonth(cursor.getInt(2));
		gft.setDay(cursor.getInt(3));
		gft.setDescription(cursor.getString(4));
		return gft;
	}

	public void deleteGift(Gift gft) {
		long id = gft.getID();
		System.out.println("Deleted entry's ID: " + id);
		db.delete(SQLiteHelper.TABLE_GIFT, SQLiteHelper.GIFT_ID + " = " + id,
				null);
	}

	public List<Gift> getAllGift(int year) {
		List<Gift> gfts = new ArrayList<Gift>();

		/* Build the query */
		String where = SQLiteHelper.GIFT_YEAR + " = ?";

		Cursor cursor = db.query(SQLiteHelper.TABLE_GIFT, null, where, new String[] {Integer.toString(year)},
				null, null, null);
		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				Gift gift = cursorToGift(cursor);
				gfts.add(gift);
				cursor.moveToNext();
			}
		} 
		cursor.close();
		return gfts;
	}
}
