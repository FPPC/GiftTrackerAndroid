package gov.ca.fppc.fppcgifttracker.controller;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import gov.ca.fppc.fppcgifttracker.R;
import gov.ca.fppc.fppcgifttracker.controller.GiftSearchFragment.GiftItemClick;
import gov.ca.fppc.fppcgifttracker.controller.GiftListOption.GiftSelectOption;
import gov.ca.fppc.fppcgifttracker.model.Gift;
import gov.ca.fppc.fppcgifttracker.model.GiftDAO;
import gov.ca.fppc.fppcgifttracker.model.GiftSourceRelationDAO;
import gov.ca.fppc.fppcgifttracker.model.SourceDAO;
import gov.ca.fppc.fppcgifttracker.model.Source;

public class GiftList extends Activity implements GiftItemClick, GiftSelectOption {
	private SourceDAO sdao;
	private GiftDAO gdao;
	private GiftSourceRelationDAO sgdao;
	private TextView name;
	private TextView job;
	private TextView address;
	private TextView totalReceived;
	private GiftSearchFragment gsFrag;
	private Source src;
	private LinearLayout wrapper;
	private LinearLayout buttonWrap;
	private Button sms;
	private Button email;
	private Button call;

	private Gift chosenGift;

	@Override
	public void processChosen(Gift gft) {
		this.chosenGift = gft;
		FragmentManager fm = getFragmentManager();
		GiftListOption option = new GiftListOption();
		option.show(fm,"option");
	}

	@Override
	public void processOption(int option) {
		switch (option) {
		case Constant.EDIT:
			editGift();
			this.chosenGift = null;
			break;
		case Constant.DELETE:
			deleteGift();
			this.chosenGift = null;
			break;
		}
	}

	private void editGift() {
		Intent intent = new Intent(this, NewGift.class); 
		intent.putExtra(Constant.GFT, chosenGift);
		intent.putExtra(Constant.MODE, Constant.EDIT);
		this.startActivity(intent);
	}

	private void deleteGift() {
		if (chosenGift !=null) {
			gdao.deleteGift(chosenGift);
			gsFrag.updateSource(src);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle saved) {
		super.onSaveInstanceState(saved);
		EditText search = (EditText)findViewById(R.id.gift_search_bar);
		saved.putString("search", search.getText().toString());
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gift_dashboard);
		sdao = new SourceDAO(this);
		sdao.open();
		gdao = new GiftDAO(this);
		gdao.open();
		sgdao = new GiftSourceRelationDAO(this);
		sgdao.open();

		/*init chosenGift*/
		this.chosenGift = null;

		/*get view handles */
		name = (TextView)this.findViewById(R.id.source_name_in_dash);
		job  = (TextView)this.findViewById(R.id.job);
		address = (TextView)this.findViewById(R.id.address);
		totalReceived = (TextView)this.findViewById(R.id.total_received);
		wrapper = (LinearLayout)this.findViewById(R.id.gift_info_wrap);
		buttonWrap = (LinearLayout) this.findViewById(R.id.contact_wrapper);
		sms = (Button) this.findViewById(R.id.SMS);
		email = (Button) this.findViewById(R.id.email_src);
		call = (Button) this.findViewById(R.id.call);

		/*process intent*/
		Intent i = this.getIntent();
		src = (Source) i.getSerializableExtra(Constant.SRC);

		/*setup fragment*/
		gsFrag = (GiftSearchFragment)this.getFragmentManager()
				.findFragmentById(R.id.gift_list_fragment);
		gsFrag.updateSource(src);

		/*process dashboard*/
		if (src == null) {
			wrapper.setVisibility(View.GONE);
			buttonWrap.setVisibility(View.GONE);
		} else {
			name.setText(this.src.getName());
			job.setText(this.src.getActivity()+ ((this.src.getLobby()==0)?"":" - Lobbyist"));
			address.setText(this.src.getAddress());
			updateReceived();
		}
		
		sms.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				sendSMS(src.getPhone());
			}
		});
		
		email.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				emailSource(src.getEmail());
			}
		});
		
		call.setOnClickListener(new View.OnClickListener() {
		
			@Override
			public void onClick(View v) {
				makeCall(src.getPhone());
			}
		});
	}
	
	private void makeCall(String phone) {
		Intent callIntent = new Intent(Intent.ACTION_CALL);
		callIntent.setData(Uri.parse("tel:"+phone));
		this.startActivity(Intent.createChooser(callIntent, "Call your source with:"));
	}
	private void sendSMS(String number) {
		Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
		smsIntent.addCategory(Intent.CATEGORY_DEFAULT);
		smsIntent.setType("vnd.android-dir/mms-sms");
		smsIntent.setData(Uri.parse("sms:" + number));
		this.startActivity(Intent.createChooser(smsIntent, "Choose your SMS client"));
	}
	
	private void emailSource(String emailAddress) {
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.setType("plain/text");
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] {emailAddress}); 
		this.startActivity(Intent.createChooser(emailIntent, "Choose your email client"));
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateReceived();
	}

	private void updateReceived()
	{
		if (src !=null ){
			totalReceived.setText(String.format("$%.2f",sgdao.totalReceived(src.getID(), Constant.currentYear)));
		}
	}
}
