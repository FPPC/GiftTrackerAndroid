package gov.ca.fppc.fppcgifttracker.model;

import gov.ca.fppc.fppcgifttracker.util.ExcelOutputComparator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class ExportData {
	private SQLiteDatabase db;
	private SQLiteHelper dbhelper;
	private Context context;
	public ExportData(Context context) {
		this.context = context;
	}
		
	public List<ExcelOutput> export() {
		List<Gift> log = getListOfGift(Calendar.getInstance().get(Calendar.YEAR));
		return getAllData(log);
	}
	
	private List<Gift> getListOfGift(int year) {
		GiftDAO gdao = new GiftDAO(context);
		gdao.open();
		List<Gift> result = gdao.getAllGift(year);
		gdao.close();
		return result;
	}
	
	private List<ExcelOutput> getAllData(List<Gift> log) {
		List<ExcelOutput> result = new ArrayList<ExcelOutput>();
		GiftSourceRelationDAO sgdao = new GiftSourceRelationDAO(context);
		sgdao.open();
		Gift g;
		Source s;
		List<Source> los;
		for(int i = 0; i < log.size(); i++) {
			g = log.get(i);
			los = sgdao.listOfDonor(g.getID());
			for(int j = 0; j < los.size(); j++) {
				s = los.get(j);
				result.add(new ExcelOutput(s, g, sgdao.getValue(g.getID(), s.getID())));
			}
		}
		sgdao.close();
		Collections.sort(result, new ExcelOutputComparator());
		return result;
	}
}
