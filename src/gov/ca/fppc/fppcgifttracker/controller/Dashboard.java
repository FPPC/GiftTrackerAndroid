package gov.ca.fppc.fppcgifttracker.controller;

import gov.ca.fppc.fppcgifttracker.model.GiftDAO;
import gov.ca.fppc.fppcgifttracker.model.GiftSourceRelationDAO;
import gov.ca.fppc.fppcgifttracker.model.Source;
import gov.ca.fppc.fppcgifttracker.model.SourceDAO;
import gov.ca.fppc.fppcgifttracker.R;
//import java.util.Calendar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Dashboard extends Activity {
	private SourceDAO sdao;
	private GiftDAO gdao;
	private GiftSourceRelationDAO sgdao;

	//private int month;
	//private int year;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dashboard);
		/*
		 * Current month and year:
		 */
		//this.year = Calendar.getInstance().get(Calendar.YEAR);
		//this.month = Calendar.getInstance().get(Calendar.MONTH);
		/*
		 * Connect DAOs (Data access objects)
		 */
		sdao = new SourceDAO(this);
		sdao.open();
		gdao = new GiftDAO(this);
		gdao.open();
		sgdao = new GiftSourceRelationDAO(this);
		sgdao.open();
		/*setup fragment*/
		SourceListFragment sourceListFrag = (SourceListFragment) getFragmentManager()
				.findFragmentById(R.id.source_list_fragment);
	
		/*
		 * Set on click for add source button
		 */
		Button add_btn = (Button) this.findViewById(R.id.dashboard_add_btn);
		add_btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				addButton(v);
			}
		});
	}

	private void addButton (View Button) {
		Intent intent = new Intent(this, NewSource.class);
		intent.putExtra(Constant.SRC, (Source)null);
		intent.putExtra(Constant.MODE, Constant.NEW);

		this.startActivity(intent);
	}
	
	@Override
	public void onDestroy() {
		sdao.close();
		gdao.close();
		sgdao.close();
		super.onDestroy();
	}
}
