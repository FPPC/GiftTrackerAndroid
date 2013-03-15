package gov.ca.fppc.fppcgifttracker.controller;

import gov.ca.fppc.fppcgifttracker.R;
import gov.ca.fppc.fppcgifttracker.model.*;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class NewSource extends Activity {
	private EditText src_name;
	private EditText src_address;
	private EditText src_business;
	private CheckBox lobbyist;

	private SourceDAO sdao;
	private Source source;
	private int mode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_source);

		/*pulling extras*/
		Intent i = getIntent();

		sdao = new SourceDAO(this);
		sdao.open();
		source = (Source) i.getSerializableExtra(Constant.SRC);
		mode = i.getIntExtra(Constant.MODE, Constant.NEW);

		/*set object to view*/
		src_name = (EditText)this.findViewById(R.id.source_name_input);
		src_address = (EditText)this.findViewById(R.id.source_address_input);
		src_business = (EditText)this.findViewById(R.id.source_business_input);
		lobbyist = (CheckBox)this.findViewById(R.id.lobbyist);

		Button cancel_edit = (Button)this.findViewById(R.id.source_input_cancel_delete_btn);
		Button save = (Button)this.findViewById(R.id.source_input_save_btn);

		cancel_edit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				removeForm(v);
			}
		});
		
		save.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				saveForm(v);
			}
		});
		
		// TODO Auto-generated method stub

		/*fill out the form if it's an edit, not a new*/
		if (source != null || mode == Constant.EDIT) {
			src_name.setText(source.getName());
			src_address.setText(source.getAddress());
			src_business.setText(source.getActivity());
			lobbyist.setChecked((source.getLobby()!=0));

			cancel_edit.setText(R.string.delete);


		}
	}

	public void saveForm(View button) {
		String name = src_name.getText().toString();
		String addr = src_address.getText().toString();
		String acti = src_business.getText().toString();
		
		int lobby = (lobbyist.isChecked()?1:0);
		Intent i;
		if (mode == Constant.EDIT) {
			source.setName(name);
			source.setAddress(addr);
			source.setActivity(acti);
			source.setLobby(lobby);

			sdao.updateSource(source);
			/*
			 * quick patch for test,
			 * should direct back to the page before the edit, not to dashboard
			 */
			i = new Intent(this,Dashboard.class);
			
		} else {
			/*new source case*/
			Source log_id = sdao.createSource(name, addr, acti, lobby);
			android.util.Log.wtf("ADD ENTRY", ""+log_id.getID()+" "+log_id.getName());
			i = new Intent(this,Dashboard.class);
		}
		this.startActivity(i);
	}

	public void removeForm(View button) {
		Intent i;
		if (mode == Constant.EDIT) {
			sdao.deleteSource(source);
			/*
			 * quick patching for test, will need fix later TODO
			 */
			i = new Intent(this,Dashboard.class);
		} else {
			i = new Intent(this,Dashboard.class);
		}
		this.startActivity(i);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		sdao.close();
	}
}
