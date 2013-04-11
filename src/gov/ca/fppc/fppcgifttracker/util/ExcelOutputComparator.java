package gov.ca.fppc.fppcgifttracker.util;

import java.util.Comparator;

import gov.ca.fppc.fppcgifttracker.model.ExcelOutput;

public class ExcelOutputComparator implements Comparator<ExcelOutput>{
	@Override
	public int compare(ExcelOutput e1, ExcelOutput e2) {
		String s1 = e1.getSource().getName();
		String s2 = e2.getSource().getName();
		return s1.compareTo(s2);
	}
}
