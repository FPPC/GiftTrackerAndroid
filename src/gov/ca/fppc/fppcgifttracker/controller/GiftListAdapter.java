package gov.ca.fppc.fppcgifttracker.controller;

import java.util.List;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import gov.ca.fppc.fppcgifttracker.R;
import gov.ca.fppc.fppcgifttracker.model.Gift;
import gov.ca.fppc.fppcgifttracker.model.GiftSourceRelationDAO;

public class GiftListAdapter extends ArrayAdapter<Gift> {
	private final Context context;
	private List<Gift> gift;
	private GiftSourceRelationDAO lookup;

	
	public GiftListAdapter(Context context, List<Gift> gift, GiftSourceRelationDAO lookup) {
		super(context, R.layout.gift_fragment_item, gift);
		this.context = context;
		this.gift = gift;
		this.lookup = lookup;
	
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		ListViewHolder vholder;
		if (rowView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.gift_fragment_item, parent, false);
			vholder = new ListViewHolder();
			vholder.description = (TextView)rowView.findViewById(R.id.gift_description);
			vholder.from = (TextView)rowView.findViewById(R.id.from);
			vholder.date = (TextView)rowView.findViewById(R.id.date);
			vholder.value= (TextView)rowView.findViewById(R.id.value);
			rowView.setTag(vholder);
		} else {
			vholder = (ListViewHolder) rowView.getTag();
		}
		Gift gf = gift.get(position);
		vholder.description.setText(gf.getDescription());
		// rendering list of contributor
		List<String> donors = lookup.listOfDonor(gf.getID());
		String fr = "From: "+donors.get(0);
		for(int i = 1; i < donors.size(); i++) {
			fr.concat(", "+donors.get(i));
		}
		Log.wtf("String", fr);
		vholder.from.setText(fr);
		//date
		vholder.value.setText(String.format("$%.2f", lookup.giftValue(gf.getID())));
		String d = String.format("%2d/%2d/%4d",gf.getMonth(),gf.getDay(),gf.getYear());
		vholder.date.setText(d);
		Log.wtf("date", d);
		return rowView;
	}

	static private class ListViewHolder {
		TextView description;
		TextView from;
		TextView date;
		TextView value;
	}
}