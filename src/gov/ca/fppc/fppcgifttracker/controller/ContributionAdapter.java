package gov.ca.fppc.fppcgifttracker.controller;

import java.util.List;
import gov.ca.fppc.fppcgifttracker.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import gov.ca.fppc.fppcgifttracker.model.*;

public class ContributionAdapter extends ArrayAdapter<Source>{
	private final Context context;
	private List<Source> source;
	private GiftSourceRelationDAO lookup;
	private Long gid;
	private View.OnFocusChangeListener valueUpdater;
	/*
	 * Make comparator for sort
	 * Sort by allowance left
	 */
	
	public ContributionAdapter(Context context, List<Source> source, 
			GiftSourceRelationDAO lookup, long gid, View.OnFocusChangeListener valueUpdater) {
		super(context, R.layout.contribution_list,source);
		this.context = context;
		this.source = source;
		this.lookup = lookup;
		this.gid=gid;
		this.valueUpdater = valueUpdater;
	}
		
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		ContributionViewHolder vholder;
		/*optimized for speed*/
		if (rowView==null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.contribution_list, parent, false);
			vholder = new ContributionViewHolder();
			vholder.s_name=(TextView) rowView.findViewById(R.id.s_name);
			vholder.s_business=(TextView) rowView.findViewById(R.id.s_business);
			vholder.s_contribution = (EditText) rowView.findViewById(R.id.contribution);
			rowView.setTag(vholder);
		} else {
			vholder = (ContributionViewHolder) rowView.getTag();
		}
		Source sc = source.get(position);
		vholder.s_name.setText(sc.getName());
		//android.util.Log.wtf("work here",sc.getName());
		String job;
		if (sc.getLobby() != 0) {
			job = sc.getActivity()+ " - Lobbyist";
		} else {
			job = sc.getActivity();
		}
			
		vholder.s_business.setText(job);
		
		if (gid != -1) {
			vholder.s_contribution.setText(String.format("%.2f",lookup.getValue(gid, sc.getID())));
		}
		/*else {
			vholder.s_contribution.setText("");
		}*/
		vholder.s_contribution.setOnFocusChangeListener(valueUpdater);
		return rowView;
	}
	
	public Source getSource(int position) {
		return this.source.get(position);
	}
	
	static private class ContributionViewHolder {
		TextView s_name;
		TextView s_business;
		EditText s_contribution;
		
	}
}
