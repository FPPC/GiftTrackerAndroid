package gov.ca.fppc.fppcgifttracker.controller;

import java.util.Calendar;

import gov.ca.fppc.fppcgifttracker.controller.DashboardOption.SelectOption;
import gov.ca.fppc.fppcgifttracker.controller.SourceListFragment.ListItemClick;
import gov.ca.fppc.fppcgifttracker.model.GiftDAO;
import gov.ca.fppc.fppcgifttracker.model.GiftSourceRelationDAO;
import gov.ca.fppc.fppcgifttracker.model.Source;
import gov.ca.fppc.fppcgifttracker.model.SourceDAO;
import gov.ca.fppc.fppcgifttracker.util.MiscUtil;
import gov.ca.fppc.fppcgifttracker.R;
//import java.util.Calendar;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class Dashboard extends Activity implements SelectOption, ListItemClick {
	private SourceDAO sdao;
	private GiftDAO gdao;
	private GiftSourceRelationDAO sgdao;
	private TextView monthDisplay;
	private TextView yearDisplay;
	private TextView monthCount;
	private TextView yearCount;
	private TextView monthSum;
	private TextView yearSum;
	private Spinner monthSpinner;
	private Source src;
	private int month;
	private int year;
	
	@Override
	protected void onSaveInstanceState(Bundle saved) {
		super.onSaveInstanceState(saved);
		EditText search = (EditText)findViewById(R.id.search_bar);
		saved.putString("search", search.getText().toString());
		saved.putInt("month", month);
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
		
		/*get current month/year */
		year = Calendar.getInstance().get(Calendar.YEAR);
		if (savedInstanceState == null) {
			month = Calendar.getInstance().get(Calendar.MONTH)+1;
		} else {
			month = savedInstanceState.getInt("month");
		}
		
		/* get view handles */
		monthDisplay = (TextView)this.findViewById(R.id.month_display);
		yearDisplay = (TextView)this.findViewById(R.id.year_display);
		monthCount = (TextView)this.findViewById(R.id.month_gift_count);
		yearCount = (TextView)this.findViewById(R.id.year_gift_count);
		monthSum = (TextView)this.findViewById(R.id.month_value_sum);
		yearSum = (TextView)this.findViewById(R.id.year_value_sum);
		monthSpinner = (Spinner)this.findViewById(R.id.month_spinner);
		/*update the summary display*/
		updateDashboard();

		/*
		 * Set on click for add source button
		 */
		Button add_btn = (Button) this.findViewById(R.id.dashboard_add_btn);
		add_btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				addButton(v);
			}
		});
		
		/*setup fragment*/
			SourceListFragment sourceListFrag = (SourceListFragment) getFragmentManager()
					.findFragmentById(R.id.source_list_fragment);
			sourceListFrag.updateMonthYear(month, year);
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
		updateDashboard();
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

	private void updateDashboard() {
		double monthValue = sgdao.totalReceived(year, this.month);
		double yearValue = sgdao.totalReceived(year);
		//String monthDisplayText = "Gifts received in " + MiscUtil.month_name(month)+":";
		String yearDisplayText = "Gift received in "+ year+":";
		//monthDisplay.setText(monthDisplayText);
		//TODO using spinner now
		monthDisplay.setText("Gift received in:");
		ArrayAdapter<CharSequence> spinnerAdapter = 
				ArrayAdapter.createFromResource(this, R.array.months, R.layout.spinner_item);
		spinnerAdapter.setDropDownViewResource(R.layout.spinner_list);
		monthSpinner.setAdapter(spinnerAdapter);
		
		yearDisplay.setText(yearDisplayText);
		int mCount = gdao.getGiftCount(month, year);
		int yCount = gdao.getGiftCount(year);
		monthSum.setText(String.format("$%.2f", monthValue));
		yearSum.setText(String.format("$%.2f", yearValue));
		monthCount.setText(String.format("%d gift(s)",mCount));
		yearCount.setText(String.format("%d gift(s)",yCount));
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
