package gov.ca.fppc.fppcgifttracker.controller;

import java.util.List;
import gov.ca.fppc.fppcgifttracker.R;
import android.content.Context;
import android.util.Log;
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
	
	public SourceAdapter(Context context, List<Source> source, GiftSourceRelationDAO lookup, int year, int month) {
		super(context, R.layout.source_item_view,source);
		this.context = context;
		this.source = source;
		this.lookup = lookup;
		this.year = year;
		this.month = month;
	}
	
	public void updateYearMonth(int year, int month) {
		this.year = year;
		this.month = month;
		this.notifyDataSetChanged();
	}
		
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		SourceViewHolder vholder;
		if (rowView==null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.source_item_view, parent, false);
			vholder = new SourceViewHolder();
			vholder.s_name=(TextView) rowView.findViewById(R.id.gift_description);
			vholder.s_business=(TextView) rowView.findViewById(R.id.date);
			vholder.s_sum=(TextView) rowView.findViewById(R.id.value);
			rowView.setTag(vholder);
		} else {
			vholder = (SourceViewHolder) rowView.getTag();
		}
		Source sc = source.get(position);
		vholder.s_name.setText(sc.getName());
		String job;
		double monthly = 0.0;
		double yearly = 0.0;
		monthly = Constant.LOBBY_LIMIT-lookup.totalReceived(sc.getID(), this.year, this.month);
		yearly = Constant.GIFT_LIMIT-lookup.totalReceived(sc.getID(),this.year);
		double limit_left = Constant.GIFT_LIMIT;
		if (sc.getLobby() != 0) {
			job = sc.getActivity()+ " - Lobbying";
			limit_left = (monthly<yearly)?monthly:yearly;
		} else {
			job = sc.getActivity();
			limit_left = yearly;
		}
			
		vholder.s_business.setText(job);
		if (limit_left < 0.0){
			vholder.s_sum.setText(String.format("-$%.2f",-limit_left));			
			vholder.s_sum.setTextColor(context.getResources().getColor(R.color.error_red));
		} else {
			vholder.s_sum.setText(String.format("$%.2f",limit_left));			
			vholder.s_sum.setTextColor(context.getResources().getColor(R.color.dollar_green));
		}
		return rowView;
	}
	
	public Source getSource(int position) {
		return this.source.get(position);
	}
	
	static private class SourceViewHolder {
		TextView s_name;
		TextView s_business;
		TextView s_sum;
		
	}
}
