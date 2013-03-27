package gov.ca.fppc.fppcgifttracker.controller;

import gov.ca.fppc.fppcgifttracker.controller.DashboardOption.SelectOption;
import gov.ca.fppc.fppcgifttracker.controller.SourceListFragment.ListItemClick;
import gov.ca.fppc.fppcgifttracker.model.GiftDAO;
import gov.ca.fppc.fppcgifttracker.model.GiftSourceRelationDAO;
import gov.ca.fppc.fppcgifttracker.model.Source;
import gov.ca.fppc.fppcgifttracker.model.SourceDAO;
import gov.ca.fppc.fppcgifttracker.R;
//import java.util.Calendar;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Dashboard extends Activity implements SelectOption, ListItemClick {
	private SourceDAO sdao;
	private GiftDAO gdao;
	private GiftSourceRelationDAO sgdao;
	private Source src;

	//private int month;
	//private int year;
	@Override
	protected void onSaveInstanceState(Bundle saved) {
		super.onSaveInstanceState(saved);
		EditText search = (EditText)findViewById(R.id.search_bar);
		saved.putString("search", search.getText().toString());
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dashboard);
		sdao = new SourceDAO(this);
		sdao.open();
		gdao = new GiftDAO(this);
		gdao.open();
		sgdao = new GiftSourceRelationDAO(this);
		sgdao.open();

		/*
		 * Set on click for add source button
		 */
		Button add_btn = (Button) this.findViewById(R.id.dashboard_add_btn);
		add_btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				addButton(v);
			}
		});
		if (savedInstanceState == null) {
			/*setup fragment*/
			SourceListFragment sourceListFrag = (SourceListFragment) getFragmentManager()
					.findFragmentById(R.id.source_list_fragment);
		} else {
			((EditText)findViewById(R.id.search_bar)).setText(savedInstanceState.getString("search"));
		}
	}

	private void addButton (View button) {
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

	@Override
	public void onResume() {
		super.onResume();
		//TODO
	}

	/*Interface ListItemClick */
	public void processChosenSource(Source src) {
		this.src = src;
		FragmentManager fm = getFragmentManager();
		DashboardOption option = new DashboardOption();
		option.show(fm,"option");
	}

	/* interface SelectOption */
	public void processOption(int option) {
		switch (option) {
		case Constant.EDIT:
			editSource();
			this.src = null;
			break;
		case Constant.NEW:
			newGift();
			this.src = null;
			break;
		}
	}

	private void editSource() {
		Intent intent = new Intent(this, NewSource.class);
		intent.putExtra(Constant.SRC, this.src);
		intent.putExtra(Constant.MODE, Constant.EDIT);
		this.startActivity(intent);
	}

	private void newGift() {
		Intent intent = new Intent(this, NewGift.class);
		intent.putExtra(Constant.SRC, this.src);
		intent.putExtra(Constant.MODE,Constant.NEW);
		this.startActivity(intent);
	}

}
