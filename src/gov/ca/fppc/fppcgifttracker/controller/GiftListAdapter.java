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
import gov.ca.fppc.fppcgifttracker.model.Source;

public class GiftListAdapter extends ArrayAdapter<Gift> {
	private final Context context;
	private List<Gift> gift;
	private GiftSourceRelationDAO lookup;
	private Source source;

	public GiftListAdapter(Context context, List<Gift> gift, GiftSourceRelationDAO lookup) {
		super(context, R.layout.gift_fragment_item, gift);
		this.context = context;
		this.gift = gift;
		this.lookup = lookup;
		this.source = null;
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

		List<String> donors = lookup.nameOfDonor(gf.getID());
		String fr = "";
		if (!donors.isEmpty()) {
			fr=fr+"From: "+donors.get(0);
			for(int i = 1; i < donors.size(); i++) {
				fr=fr+", "+donors.get(i);
			}
		}

		vholder.from.setText(fr);

		if (this.source == null) {
			vholder.value.setText(String.format("$%.2f", lookup.giftValue(gf.getID())));
		} else {
			vholder.value.setText(String.format("$%.2f",lookup.getValue(gf.getID(), source.getID())));
		}
		
		String d = String.format("%2d/%2d/%4d",gf.getMonth(),gf.getDay(),gf.getYear());
		vholder.date.setText(d);
		return rowView;
	}
		
	public void updateSource(Source source) {
		this.source = source;
	}

	static private class ListViewHolder {
		TextView description;
		TextView from;
		TextView date;
		TextView value;
	}
}
