package gov.ca.fppc.fppcgifttracker.controller;

import java.util.Collections;
import java.util.Comparator;
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
	private final List<Source> everything;
	
	/*
	 * Make comparator for sort
	 * Sort by allowance left
	 */
	private static Comparator<Source> C = new Comparator<Source>() {
		public int compare(Source s1, Source s2) {
			return (int)((s1.getLimit()-s1.getCurrent()) - (s2.getLimit()-s2.getCurrent()));
		}
	};
	
	public SourceAdapter(Context context, List<Source> source) {
		super(context, R.layout.source_item_view,source);
		this.context=context;
		this.source= source;
		this.everything = source;
		Collections.sort(this.source,C);
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
		vholder.s_name.setText(source.get(position).getName());
		vholder.s_business.setText(source.get(position).getActivity());
		//TODO: color according to %
		vholder.s_sum.setText(String.format("$%.2f",source.get(position).getCurrent()));
		
		return rowView;
	}
	static class SourceViewHolder {
		TextView s_name;
		TextView s_business;
		TextView s_sum;
	}
}
