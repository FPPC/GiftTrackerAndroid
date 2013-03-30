package gov.ca.fppc.fppcgifttracker.util;

import gov.ca.fppc.fppcgifttracker.model.Gift;

import java.util.Comparator;

public class GiftComparator implements Comparator<Gift> {
	@Override
	public int compare(Gift g1, Gift g2) {
		/* if different year, compare the year
		 * else if different month, compare month
		 * else compare day
		 */
		
		return (g1.getYear()!=g2.getYear())?g2.getYear()-g1.getYear():
			(g1.getMonth()!=g2.getMonth())?(g2.getMonth()-g1.getMonth()):
				g2.getDay()-g1.getDay();
	}
}
