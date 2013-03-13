package gov.ca.fppc.fppcgifttracker.model;

import java.io.Serializable;

public class Source implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9205192131398484971L;
	private long id;
	private String name;
	private String address;
	private String activity;
	private int lobby;
	private double limit;
	private double current;

	public long getID() {
		return id;
	}

	public void setID(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public int getLobby() {
		return lobby;
	}

	public void setLobby(int lobby) {
		this.lobby = lobby;
	}

	public double getLimit() {
		return limit;
	}

	public void setLimit(double limit) {
		this.limit = limit;
	}

	public double getCurrent() {
		return current;
	}

	public void setCurrent(double current) {
		this.current = current;
	}
	
}
