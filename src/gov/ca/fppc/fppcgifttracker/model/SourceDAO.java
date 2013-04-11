package gov.ca.fppc.fppcgifttracker.model;

import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SourceDAO {

	/**
	 * 
	 */
	private SQLiteDatabase db;
	private SQLiteHelper dbhelper;
	private String[] allColumns = { SQLiteHelper.SOURCE_ID,
			SQLiteHelper.SOURCE_NAME, SQLiteHelper.SOURCE_ADDR1,
			SQLiteHelper.SOURCE_ADDR2, SQLiteHelper.SOURCE_CITY,
			SQLiteHelper.SOURCE_STATE, SQLiteHelper.SOURCE_ZIP,
			SQLiteHelper.SOURCE_ACTI, SQLiteHelper.SOURCE_LOBBY,
			SQLiteHelper.SOURCE_EMAIL, SQLiteHelper.SOURCE_PHONE};
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

	public Source createSource(String name, String addr1, String addr2, String city, String state, String zip, 
			String acti, int lobby, String email, String phone) {
		ContentValues values = new ContentValues();
		values.put(SQLiteHelper.SOURCE_NAME, name);
		values.put(SQLiteHelper.SOURCE_ADDR1, addr1);
		values.put(SQLiteHelper.SOURCE_ADDR2, addr2);
		values.put(SQLiteHelper.SOURCE_CITY, city);
		values.put(SQLiteHelper.SOURCE_STATE, state);
		values.put(SQLiteHelper.SOURCE_ZIP, zip);
		values.put(SQLiteHelper.SOURCE_ACTI, acti);
		values.put(SQLiteHelper.SOURCE_LOBBY, lobby);
		values.put(SQLiteHelper.SOURCE_EMAIL, email);
		values.put(SQLiteHelper.SOURCE_PHONE, phone);
		long insertID = db.insert(SQLiteHelper.TABLE_SOURCE, null, values);

		/*
		 * put the same thing into FTS table
		 */
		ContentValues search_index = new ContentValues();
		String index_content = name + " " + addr1+" "+addr2+" "+city+" "+state+" "+zip 
				+ " " + acti + (lobby==0?"":" lobbyist") + email + " " + phone; /*lobbyist gets a tag for search purpose*/
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

	public static Source cursorToSource(Cursor cursor) {
		Source src = new Source();
		src.setID(cursor.getLong(0));
		src.setName(cursor.getString(1));
		src.setAddress1(cursor.getString(2));
		src.setAddress2(cursor.getString(3));
		src.setCity(cursor.getString(4));
		src.setState(cursor.getString(5));
		src.setZip(cursor.getString(6));
		src.setActivity(cursor.getString(7));
		src.setLobby(cursor.getInt(8));
		src.setEmail(cursor.getString(9));
		src.setPhone(cursor.getString(10));
		return src;
	}

	public long updateSource(Source source) {
		ContentValues values = new ContentValues();
		values.put(SQLiteHelper.SOURCE_NAME, source.getName());
		values.put(SQLiteHelper.SOURCE_ADDR1, source.getAddress1());
		values.put(SQLiteHelper.SOURCE_ADDR2, source.getAddress2());
		values.put(SQLiteHelper.SOURCE_CITY, source.getCity());
		values.put(SQLiteHelper.SOURCE_STATE, source.getState());
		values.put(SQLiteHelper.SOURCE_ZIP, source.getZip());	
		values.put(SQLiteHelper.SOURCE_ACTI, source.getActivity());
		values.put(SQLiteHelper.SOURCE_LOBBY, source.getLobby());
		values.put(SQLiteHelper.SOURCE_EMAIL, source.getEmail());
		values.put(SQLiteHelper.SOURCE_PHONE, source.getPhone());
		
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
		//delete all gift valued associated with the source
		String where = SQLiteHelper.SOURCE_ID + " = ?";
		String [] args = {""+id};
		db.delete(SQLiteHelper.TABLE_GIVING,where,args); 
		
		//delete the source
		db.delete(SQLiteHelper.TABLE_SOURCE,where,args);
		//mirror move on the index table
		where = SQLiteHelper.DOC_ID + " = ?";
		db.delete(SQLiteHelper.SOURCE_TABLE_FTS, where, args);
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
		 * SELECT D.* FROM indexed_sources i JOIN sources d ON i.docid = d.src_id WHERE i.content MATCH <search string>;
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
