package gov.ca.fppc.fppcgifttracker.model;

import gov.ca.fppc.fppcgifttracker.controller.Constant;

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
		return (lobby == 0)?Constant.GIFT_LIMIT:Constant.LOBBY_LIMIT;
	}

	public double getLimitLeft(GiftSourceRelationDAO r, int year, int month) {
		double limit = Constant.GIFT_LIMIT;
		if (this.lobby == 0) {
			return limit-r.totalReceived(this.id, year);
		}
		
		return limit-r.totalReceived(this.id, year, month);
	}
}
