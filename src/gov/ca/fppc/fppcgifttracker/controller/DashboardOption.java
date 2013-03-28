package gov.ca.fppc.fppcgifttracker.controller;

import gov.ca.fppc.fppcgifttracker.R;
import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class DashboardOption extends DialogFragment {
	public interface SelectOption {
		public void processOption(int choice);
	}

	private Button edit;
	private Button add;
	private SelectOption callback;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			callback = (SelectOption) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()+" must implement SelectOption interface");
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.edit_or_new_gift_fragment,container,false);
		this.edit = (Button) view.findViewById(R.id.edit_source_btn);
		this.add = (Button) view.findViewById(R.id.add_gift_btn);
		this.getDialog().setTitle("What would you like to do?");
		
		edit.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dismiss();
				callback.processOption(Constant.EDIT);
			}
		});
		add.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dismiss();
				callback.processOption(Constant.NEW);
			}
		});
		
		return view;
	}
}
