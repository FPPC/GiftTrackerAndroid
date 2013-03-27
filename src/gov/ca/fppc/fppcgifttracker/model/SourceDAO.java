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
			SQLiteHelper.SOURCE_ACTI, SQLiteHelper.SOURCE_LOBBY};
	private List<Source> srcs;

	public SourceDAO(Context context) {
		dbhelper = new SQLiteHelper(context);
		srcs = new ArrayList<Source>();
	}

	public void open() throws SQLException {
		db = dbhelper.getWritableDatabase();
	}

	public void close() {
		dbhelper.close();
	}

	public Source createSource(String name, String addr, String acti, int lobby) {
		ContentValues values = new ContentValues();
		values.put(SQLiteHelper.SOURCE_NAME, name);
		values.put(SQLiteHelper.SOURCE_ADDR, addr);
		values.put(SQLiteHelper.SOURCE_ACTI, acti);
		values.put(SQLiteHelper.SOURCE_LOBBY, lobby);
		long insertID = db.insert(SQLiteHelper.TABLE_SOURCE, null, values);

		/*
		 * put the same thing into FTS table
		 */
		ContentValues search_index = new ContentValues();
		String index_content = name + " " + addr + " " + acti + (lobby==0?"":" lobbyist"); /*lobbyist gets a tag for search purpose*/
		search_index.put(SQLiteHelper.CONTENT, index_content);
		search_index.put(SQLiteHelper.DOC_ID,insertID);
		long secondID = db.insert(SQLiteHelper.SOURCE_TABLE_FTS, null, search_index);

		/*
		 * Keep this DEBUG here just in case I miss a bug
		 */
		if (secondID != insertID) {
			android.util.Log.wtf("SOURCE_DAO","DOC_ID != SOURCE_ID :SourceDAO.java line 60");
		}
		
		/*
		 * Verify that entry is recorded in table
		 */
		Cursor cursor = db.query(SQLiteHelper.TABLE_SOURCE, allColumns,
				SQLiteHelper.SOURCE_ID + " = " + insertID, null, null, null,
				null);
		Source newSource = null;
		if (cursor.moveToFirst()) {
			newSource = cursorToSource(cursor);
		}
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
		return src;
	}

	public long updateSource(Source source) {
		ContentValues values = new ContentValues();
		values.put(SQLiteHelper.SOURCE_NAME, source.getName());
		values.put(SQLiteHelper.SOURCE_ADDR, source.getAddress());
		values.put(SQLiteHelper.SOURCE_ACTI, source.getActivity());
		values.put(SQLiteHelper.SOURCE_LOBBY, source.getLobby());
		long updateID = source.getID();
		db.update(SQLiteHelper.TABLE_SOURCE, values,
				SQLiteHelper.SOURCE_ID + " = " + updateID, null);
		
		//update the index too
		ContentValues search_index = new ContentValues();
		String index_content = source.getName()+" "+ source.getAddress()+ " " + source.getActivity() + (source.getLobby()==0?"":" lobbyist");
		search_index.put(SQLiteHelper.CONTENT, index_content);
		search_index.put(SQLiteHelper.DOC_ID,updateID);
		db.update(SQLiteHelper.SOURCE_TABLE_FTS, search_index, SQLiteHelper.DOC_ID + " = " + updateID,null);
		return updateID;
	}

	public void deleteSource(Source src) {
		long id = src.getID();
		System.out.println("Deleted source's ID: " + id);
		db.delete(SQLiteHelper.TABLE_SOURCE, SQLiteHelper.SOURCE_ID + " = "
				+ id, null);
		//mirror move on the index table
		db.delete(SQLiteHelper.SOURCE_TABLE_FTS, SQLiteHelper.DOC_ID + " = " + id, null);
	}

	public List<Source> getAllSource() {
		srcs.clear();
		Cursor cursor = db.query(SQLiteHelper.TABLE_SOURCE, allColumns, null,
				null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Source source = cursorToSource(cursor);
			srcs.add(source);
			cursor.moveToNext();
		}
		cursor.close();
		
		
		return srcs;
	}

	public List<Source> filterSource(String s) {
		srcs.clear();
		/*
		 * quick search for the ID
		 * SELECT I.* FROM indexed_sources i JOIN sources d ON i.docid = d.src_id WHERE i.content MATCH <search string>;
		 */
		String queryline = "SELECT d.* FROM " 
				+ SQLiteHelper.SOURCE_TABLE_FTS + " i JOIN " 
				+ SQLiteHelper.TABLE_SOURCE+ " d ON i."
				+ SQLiteHelper.DOC_ID + " = d."
				+ SQLiteHelper.SOURCE_ID + " WHERE i."
				+SQLiteHelper.CONTENT + " MATCH ?;";
		String [] argument = {s+"*"};
		Cursor cursor = db.rawQuery(queryline, argument);
		
		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				Source source = cursorToSource(cursor);
				srcs.add(source);
				cursor.moveToNext();
			}
		}
		cursor.close();
		return srcs;
	}
}
