package gov.ca.fppc.fppcgifttracker.controller;

import gov.ca.fppc.fppcgifttracker.R;
import gov.ca.fppc.fppcgifttracker.model.*;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class NewSource extends Activity {
	private EditText src_name;
	private EditText src_address1;
	private EditText src_address2;
	private EditText src_city;
	private EditText src_state;
	private EditText src_zip;
	private EditText src_email;
	private EditText src_phone;
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
		src_address1 = (EditText)this.findViewById(R.id.source_address_input1);
		src_address2 = (EditText)this.findViewById(R.id.source_addres_input2);
		src_city = (EditText)this.findViewById(R.id.source_city);
		src_state = (EditText)this.findViewById(R.id.source_state);
		src_zip = (EditText)this.findViewById(R.id.source_zip);
		src_email = (EditText)this.findViewById(R.id.email);
		src_phone = (EditText)this.findViewById(R.id.phone);
		src_business = (EditText) this.findViewById(R.id.source_business_input);
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

		/*fill out the form if it's an edit, not a new*/
		if (source != null || mode == Constant.EDIT) {
			src_name.setText(source.getName());
			src_address1.setText(source.getAddress1());
			src_address2.setText(source.getAddress2());
			src_city.setText(source.getCity());
			src_state.setText(source.getState());
			src_zip.setText(source.getZip());
			src_email.setText(source.getEmail());
			src_phone.setText(source.getPhone());
			src_business.setText(source.getActivity());
			lobbyist.setChecked((source.getLobby()!=0));

			cancel_edit.setText(R.string.delete);


		}
	}

	public void saveForm(View button) {
		String name = src_name.getText().toString();
		String addr1 = src_address1.getText().toString();
		String addr2 = src_address2.getText().toString();
		String city = src_city.getText().toString();
		String state = src_state.getText().toString();
		String zip = src_zip.getText().toString();
		String email = src_email.getText().toString();
		String phone = src_phone.getText().toString();

		String acti = src_business.getText().toString();

		int lobby = (lobbyist.isChecked()?1:0);
		if (mode == Constant.EDIT) {
			source.setName(name);
			source.setAddress1(addr1);
			source.setAddress2(addr2);
			source.setCity(city);
			source.setState(state);
			source.setZip(zip);
			source.setEmail(email);
			source.setPhone(phone);
						
			source.setActivity(acti);
			source.setLobby(lobby);
			Log.wtf(source.getEmail(), source.getPhone());
			sdao.updateSource(source);
			finish();

		} else {
			/*new source case*/
			sdao.createSource(name, addr1, addr2, city, state, zip, acti, lobby, email, phone);
			finish();
		}
	}

	public void removeForm(View button) {
		if (mode == Constant.EDIT) {
			sdao.deleteSource(source);
		}
		finish();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		sdao.close();
	}
}
