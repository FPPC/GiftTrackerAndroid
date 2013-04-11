package gov.ca.fppc.fppcgifttracker.model;

import gov.ca.fppc.fppcgifttracker.controller.Constant;
import java.io.Serializable;

import android.util.Log;

public class Source implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9205192131398484971L;
	private long id;
	private String name;
	private String address1;
	private String address2;
	private String city;
	private String state;
	private String zip;
	private String email;
	private String phone;
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
		String a = address1;
		String a2 = address2;
		String c = city;
		String s = state;
		a=(a.isEmpty()?"":a+", ");
		a2=a2.isEmpty()?"":a2+", ";
		c=c.isEmpty()?"":c+", ";
		s=s.isEmpty()?"":s+", ";
		return a+a2+c+s+zip;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}
	
	public void setAddress2(String address2) {
		this.address2 = address2;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public void setState(String state) {
		this.state = state;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	public String getAddress1() {
		return address1;
	}
	public String getAddress2() {
		return address2;
	}
	public String getCity() {
		return city;
	}
	public String getState() {
		return state;
	}
	public String getZip() {
		return zip;
	}
	
	public String getEmail() {
		return email;
	}
	
	public String getPhone() {
		return phone;
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
		return Constant.GIFT_LIMIT;
	}
	
	public double getLobbyLimit() {
		return Constant.LOBBY_LIMIT;
	}

	public double getLimitLeft(GiftSourceRelationDAO r, int year, int month) {
		double limit = Constant.GIFT_LIMIT;
		if (this.lobby == 0) {
			return limit-r.totalReceived(this.id, year);
		}
		
		return limit-r.totalReceived(this.id, year, month);
	}
	
	@Override
	public String toString() {
		String result = ""+getID()+" "+getName()+" "+getAddress()+" "+getActivity()+" "+getLobby();
		return result;
	}
	
	@Override
	public boolean equals(Object o) {
		Source obj;
		try {
			obj = (Source) o;
		} catch (ClassCastException e) {
			Log.wtf("Compare", "exception catched");			
			return false;
		}
		boolean result = toString().equals(obj.toString());
		return result;
	}
}
