package gov.ca.fppc.fppcgifttracker.controller;

import gov.ca.fppc.fppcgifttracker.R;
import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class ContributionOptionFragment extends DialogFragment {
	public interface ContributionOption {
		public void processOption(int choice);
	}
	
	private Button yes;
	private Button no;
	private ContributionOption callback;
	
	@Override
	public void onAttach(Activity a) {
		super.onAttach(a);
		try {
			callback = (ContributionOption) a;
		} catch (ClassCastException e) {
			throw new ClassCastException(a.toString()+" must implement ContributionOption interface");
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.contrib_option_frag, container, false);
		this.yes = (Button) view.findViewById(R.id.yes);
		this.no = (Button) view.findViewById(R.id.no);
		
		yes.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
				callback.processOption(Constant.DELETE);
			}
		});
		no.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		return view;
	}
}
