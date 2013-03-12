package gov.ca.fppc.fppcgifttracker.model;

import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class SourceDAO {

	/**
	 * 
	 */
	private SQLiteDatabase db;
	private SQLiteHelper dbhelper;
	private String[] allColumns = { SQLiteHelper.SOURCE_ID,
			SQLiteHelper.SOURCE_NAME, SQLiteHelper.SOURCE_ADDR,
			SQLiteHelper.SOURCE_ACTI, SQLiteHelper.SOURCE_LOBBY,
			SQLiteHelper.SOURCE_LIMIT };

	public SourceDAO(Context context) {
		dbhelper = new SQLiteHelper(context);
	}

	public void open() throws SQLException {
		db = dbhelper.getWritableDatabase();
	}

	public void close() {
		dbhelper.close();
	}

	public Source createSource(String name, String addr, String acti,
			int lobby, double limit) {
		ContentValues values = new ContentValues();
		values.put(SQLiteHelper.SOURCE_NAME, name);
		values.put(SQLiteHelper.SOURCE_ADDR, addr);
		values.put(SQLiteHelper.SOURCE_ACTI, acti);
		values.put(SQLiteHelper.SOURCE_LOBBY, lobby);
		values.put(SQLiteHelper.SOURCE_LIMIT, limit);
		long insertID = db.insert(SQLiteHelper.TABLE_SOURCE, null, values);

		Cursor cursor = db.query(SQLiteHelper.TABLE_SOURCE, allColumns,
				SQLiteHelper.SOURCE_ID + " = " + insertID, null, null, null,
				null);
		cursor.moveToFirst();
		Source newSource = cursorToSource(cursor);
		cursor.close();
		return newSource;
	}

	private Source cursorToSource(Cursor cursor) {
		Source src = new Source();
		src.setID(cursor.getLong(0));
		src.setName(cursor.getString(1));
		src.setAddress(cursor.getString(2));
		src.setActivity(cursor.getString(3));
		src.setLobby(cursor.getInt(4));
		src.setLimit(cursor.getDouble(5));
		return src;
	}

	public long updateSource(Source source) {
		ContentValues values = new ContentValues();
		values.put(SQLiteHelper.SOURCE_NAME, source.getName());
		values.put(SQLiteHelper.SOURCE_ADDR, source.getAddress());
		values.put(SQLiteHelper.SOURCE_ACTI, source.getActivity());
		values.put(SQLiteHelper.SOURCE_LOBBY, source.getLobby());
		values.put(SQLiteHelper.SOURCE_LIMIT, source.getLimit());
		long updateID = source.getID();
		updateID = db.update(SQLiteHelper.TABLE_GIFT, values,
				SQLiteHelper.SOURCE_ID + " = " + updateID, null);
		return updateID;
	}

	public double sum(long source_id) {
		/* build the query */
		String[] column = new String[] { SQLiteHelper.GIFT_VALUE };
		String where = SQLiteHelper.GIFT_ID + " = ?";
		String[] value = new String[] { Long.toString(source_id) };

		Cursor cursor = db.query(SQLiteHelper.TABLE_GIFT, column, where, value,
				null, null, null);
		double result = 0.0;
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			result += cursor.getDouble(0);
			cursor.moveToNext();
		}
		return result;
	}

	public void deleteSource(Source src) {
		long id = src.getID();
		System.out.println("Deleted source's ID: " + id);
		db.delete(SQLiteHelper.TABLE_SOURCE, SQLiteHelper.SOURCE_ID + " = "
				+ id, null);
	}

	public List<Source> getAllSource() {
		List<Source> srcs = new ArrayList<Source>();
		Cursor cursor = db.query(SQLiteHelper.TABLE_SOURCE, allColumns, null,
				null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Source source = cursorToSource(cursor);
			source.setCurrent(this.sum(source.getID()));
			srcs.add(source);
			cursor.moveToNext();
		}
		cursor.close();
		return srcs;
	}
}
