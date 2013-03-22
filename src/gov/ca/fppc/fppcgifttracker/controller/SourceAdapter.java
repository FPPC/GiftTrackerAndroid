package gov.ca.fppc.fppcgifttracker.controller;

import java.util.List;
import gov.ca.fppc.fppcgifttracker.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import gov.ca.fppc.fppcgifttracker.model.*;

public class SourceAdapter extends ArrayAdapter<Source>{
	private final Context context;
	private List<Source> source;
	private GiftSourceRelationDAO lookup;
	private int year;
	private int month;
	/*
	 * Make comparator for sort
	 * Sort by allowance left
	 */
	
	public SourceAdapter(Context context, List<Source> source, GiftSourceRelationDAO lookup, int year, int month) {
		super(context, R.layout.source_item_view,source);
		this.context = context;
		this.source = source;
		this.lookup = lookup;
		this.year = year;
		this.month = month;
	}
		
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		SourceViewHolder vholder;
		if (rowView==null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.source_item_view, parent, false);
			vholder = new SourceViewHolder();
			vholder.s_name=(TextView) rowView.findViewById(R.id.s_name);
			vholder.s_business=(TextView) rowView.findViewById(R.id.s_business);
			vholder.s_sum=(TextView) rowView.findViewById(R.id.s_sum);
			rowView.setTag(vholder);
		} else {
			vholder = (SourceViewHolder) rowView.getTag();
		}
		Source sc = source.get(position);
		vholder.s_name.setText(sc.getName());
		android.util.Log.wtf("work here",sc.getName());
		String job;
		double limit_left = sc.getLimit();
		if (sc.getLobby() != 0) {
			job = sc.getActivity()+ " - Lobbyist";
			limit_left -= lookup.totalReceived(sc.getID(), this.year, this.month);
		} else {
			job = sc.getActivity();
			limit_left -= lookup.totalReceived(sc.getID(),this.year);
		}
			
		vholder.s_business.setText(job);
		//TODO: color according to %
		vholder.s_sum.setText(String.format("$%.2f",limit_left));
		return rowView;
	}
	
	static private class SourceViewHolder {
		TextView s_name;
		TextView s_business;
		TextView s_sum;
		
	}
}
