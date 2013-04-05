package gov.ca.fppc.fppcgifttracker.controller;

import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import gov.ca.fppc.fppcgifttracker.R;
import gov.ca.fppc.fppcgifttracker.controller.GiftSearchFragment.GiftItemClick;
import gov.ca.fppc.fppcgifttracker.model.Gift;
import gov.ca.fppc.fppcgifttracker.model.GiftDAO;
import gov.ca.fppc.fppcgifttracker.model.GiftSourceRelationDAO;
import gov.ca.fppc.fppcgifttracker.model.SourceDAO;
import gov.ca.fppc.fppcgifttracker.model.Source;

public class GiftList extends Activity implements GiftItemClick {
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
	
	@Override
	public void processChosen(Gift gft) {
		// TODO Auto-generated method stub
		
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
		
		/*get view handles */
		name = (TextView)this.findViewById(R.id.source_name_in_dash);
		job  = (TextView)this.findViewById(R.id.job);
		address = (TextView)this.findViewById(R.id.address);
		totalReceived = (TextView)this.findViewById(R.id.total_received);
		wrapper = (LinearLayout)this.findViewById(R.id.gift_info_wrap);
		
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
		} else {
			name.setText(this.src.getName());
			job.setText(this.src.getActivity()+ ((this.src.getLobby()==0)?"":" - Lobbyist"));
			address.setText(this.src.getAddress());
			totalReceived.setText(String.format("$%.2f",sgdao.totalReceived(src.getID(), Calendar.getInstance().get(Calendar.YEAR))));
		}
	}
}
