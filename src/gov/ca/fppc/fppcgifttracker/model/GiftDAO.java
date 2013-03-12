package gov.ca.fppc.fppcgifttracker.model;

import java.util.ArrayList;
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
	private String[] allColumns = { SQLiteHelper.GIFT_ID,
			SQLiteHelper.SOURCE_ID, SQLiteHelper.GIFT_DATE,
			SQLiteHelper.GIFT_VALUE, SQLiteHelper.GIFT_DESCRIPTION };

	public GiftDAO(Context context) {
		dbhelper = new SQLiteHelper(context);
	}

	public void open() throws SQLException {
		db = dbhelper.getWritableDatabase();
	}

	public void close() {
		dbhelper.close();
	}

	public Gift createGift(long sourceID, String date, double value,
			String description) {
		ContentValues values = new ContentValues();
		values.put(SQLiteHelper.SOURCE_ID, sourceID);
		values.put(SQLiteHelper.GIFT_DATE, date);
		values.put(SQLiteHelper.GIFT_VALUE, value);
		values.put(SQLiteHelper.GIFT_DESCRIPTION, description);
		long insertID = db.insert(SQLiteHelper.TABLE_GIFT, null, values);
		Cursor cursor = db.query(SQLiteHelper.TABLE_GIFT, // table
				allColumns, // columns
				SQLiteHelper.GIFT_ID + " = " + insertID, // selection
				null, null, null, null);
		cursor.moveToFirst();
		Gift newGift = new Gift();// cursorToGift(cursor);
		cursor.close();
		return newGift;
	}

	public long updateGift(Gift gift) {
		ContentValues values = new ContentValues();
		values.put(SQLiteHelper.SOURCE_ID, gift.getID());
		values.put(SQLiteHelper.GIFT_DATE, gift.getDate());
		values.put(SQLiteHelper.GIFT_VALUE, gift.getValue());
		values.put(SQLiteHelper.GIFT_DESCRIPTION, gift.getDescription());
		long updateID = gift.getID();
		updateID = db.update(SQLiteHelper.TABLE_GIFT, values,
				SQLiteHelper.GIFT_ID + " = " + updateID, null);
		return updateID;
	}

	private Gift cursorToGift(Cursor cursor) {
		Gift gft = new Gift();
		gft.setID(cursor.getLong(0));
		gft.setSourceId(cursor.getLong(2));
		gft.setDate(cursor.getLong(3));
		gft.setValue(cursor.getDouble(4));
		gft.setDescription(cursor.getString(5));
		return gft;
	}

	public void deleteGift(Gift gft) {
		long id = gft.getID();
		System.out.println("Deleted entry's ID: " + id);
		db.delete(SQLiteHelper.TABLE_GIFT, SQLiteHelper.GIFT_ID + " = " + id,
				null);
	}

	public List<Gift> getAllGift(long sourceID) {
		List<Gift> gfts = new ArrayList<Gift>();

		/* Build the query */
		String where = SQLiteHelper.SOURCE_ID + " = ?";
		String[] value = new String[] { Long.toString(sourceID) };

		Cursor cursor = db.query(SQLiteHelper.TABLE_GIFT, null, where, value,
				null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Gift gift = cursorToGift(cursor);
			gfts.add(gift);
			cursor.moveToNext();
		}
		cursor.close();
		return gfts;
	}
}
