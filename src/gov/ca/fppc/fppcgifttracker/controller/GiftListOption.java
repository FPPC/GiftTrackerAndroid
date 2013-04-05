package gov.ca.fppc.fppcgifttracker.controller;

import gov.ca.fppc.fppcgifttracker.R;
import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class GiftListOption extends DialogFragment {
	public interface GiftSelectOption {
		public void processOption(int choise);
	}
	
	private Button delete;
	private Button edit;
	private GiftSelectOption callback;
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			callback = (GiftSelectOption) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()+" must implement GiftSelectOption interface");
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceSate) {
		super.onCreateView(inflater, container, savedInstanceSate);
		View view = inflater.inflate(R.layout.gift_select_option_frag, container, false);
		this.delete = (Button) view.findViewById(R.id.delete_gift_btn);
		this.edit = (Button) view.findViewById(R.id.edit_gift_btn);
		this.getDialog().setTitle("What would you like to do?");
		
		edit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
				callback.processOption(Constant.EDIT);
			}
		});
		
		delete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
				callback.processOption(Constant.DELETE);
			}
		});
		return view;
	}
}