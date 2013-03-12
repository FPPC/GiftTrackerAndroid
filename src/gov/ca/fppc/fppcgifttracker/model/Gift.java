package gov.ca.fppc.fppcgifttracker.model;

import java.io.Serializable;

public class Gift implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1736938716919278842L;
	private long id;
	private long sourceId;
	private long date;
	private double value;
	private String description;

	public long getID() {
		return id;
	}

	public void setID(long id) {
		this.id = id;
	}

	public long getSourceId() {
		return sourceId;
	}

	public void setSourceId(long sourceId) {
		this.sourceId = sourceId;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
