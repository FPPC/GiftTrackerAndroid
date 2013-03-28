package gov.ca.fppc.fppcgifttracker.util;

import java.util.Comparator;

import gov.ca.fppc.fppcgifttracker.controller.Constant;
import gov.ca.fppc.fppcgifttracker.model.GiftSourceRelationDAO;
import gov.ca.fppc.fppcgifttracker.model.Source;

public class SourceComparator implements Comparator<Source> {

	private GiftSourceRelationDAO lookup;
	private int year;
	private int month;
	public SourceComparator(GiftSourceRelationDAO lookup, int year, int month) {
		this.lookup = lookup;
		this.year = year;
		this.month = month;
	}

	@Override
	public int compare(Source s1, Source s2) {
		double s1value = (s1.getLobby()==0)?Constant.GIFT_LIMIT-lookup.totalReceived(s1.getID(), year)
				:Constant.LOBBY_LIMIT-lookup.totalReceived(s1.getID(),year,month);
		double s2value = (s2.getLobby()==0)?Constant.GIFT_LIMIT-lookup.totalReceived(s2.getID(), year)
				:Constant.LOBBY_LIMIT-lookup.totalReceived(s2.getID(),year,month);
		return (int)s1value-(int)s2value;
	}
}
