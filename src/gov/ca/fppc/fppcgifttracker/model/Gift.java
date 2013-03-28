package gov.ca.fppc.fppcgifttracker.model;

import java.io.Serializable;

public class Gift implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1736938716919278842L;
	private long id;
	private int year;
	private int month;
	private int day;
	private String description;

	public long getID() {
		return id;
	}

	public void setID(long id) {
		this.id = id;
	}

	public int getYear() {
		return this.year;
	}
	
	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return this.month;
	}
	
	public void setMonth(int month) {
		this.month = month;
	}
	
	public int getDay() {
		return this.day;
	}
	
	public void setDay(int day) {
		this.day = day;
	}

	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public String toString() {
		return ""+id+ " "+ year+"/"+month+"/"+day+" "+description;
	}

}
