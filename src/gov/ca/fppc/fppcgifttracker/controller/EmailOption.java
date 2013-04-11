package gov.ca.fppc.fppcgifttracker.controller;

import gov.ca.fppc.fppcgifttracker.R;
import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class EmailOption extends DialogFragment {
	public interface SelectEmailOption {
		public void send(String email);
	}
	
	private Button send;
	private Button cancel;
	private EditText email;
	private SelectEmailOption callback;
	
	@Override
	public void onAttach(Activity a) {
		super.onAttach(a);
		try {
			callback = (SelectEmailOption) a;
		} catch (ClassCastException e) {
			throw new ClassCastException(a.toString() + " must implement SelectEmailOption interface");
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.email_dialog, container, false);
		this.send = (Button) view.findViewById(R.id.send_btn);
		this.cancel = (Button) view.findViewById(R.id.cancel_btn);
		this.email = (EditText) view.findViewById(R.id.output_email);
		this.getDialog().setTitle("Please enter your email address");
		
		send.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
				callback.send(email.getText().toString());
			}
		});
		
		cancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		return view;
	}
}
