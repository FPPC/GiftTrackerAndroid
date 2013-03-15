package gov.ca.fppc.fppcgifttracker.controller;

import gov.ca.fppc.fppcgifttracker.model.GiftDAO;
import gov.ca.fppc.fppcgifttracker.model.GiftSourceRelationDAO;
import gov.ca.fppc.fppcgifttracker.model.Source;
import gov.ca.fppc.fppcgifttracker.model.SourceDAO;
import gov.ca.fppc.fppcgifttracker.util.SourceComparator;
import gov.ca.fppc.fppcgifttracker.R;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class Dashboard extends Activity {
	private SourceDAO sdao;
	private List<Source> source;
	private GiftDAO gdao;
	private GiftSourceRelationDAO sgdao;
	private EditText searchtext;
	private ListView sourceList;
	private final Context c  = this;	
	private int month;
	private int year;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dashboard);

		/*
		 * Current month and year:
		 */
		this.year = Calendar.getInstance().get(Calendar.YEAR);
		this.month = Calendar.getInstance().get(Calendar.MONTH);
		/*
		 * Connect DAOs (Data access objects)
		 */
		sdao = new SourceDAO(this);
		sdao.open();
		gdao = new GiftDAO(this);
		gdao.open();
		sgdao = new GiftSourceRelationDAO(this);
		sgdao.open();

		/*
		 * Set up the source List
		 */
		source = sdao.getAllSource();
		/* sort it first */
		Collections.sort(source, new SourceComparator(sgdao));
		
		final ArrayAdapter<Source> adapter = new SourceAdapter(this,source,sgdao, this.year, this.month);
		sourceList = (ListView)this.findViewById(R.id.src_list);
		sourceList.setAdapter(adapter);

		/*
		 * Set up the search bar
		 */
		searchtext = (EditText) this.findViewById(R.id.search_bar);
		searchtext.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {
				List<Source> temp;
				ArrayAdapter<Source> a;
				if (s.toString().length() > 0) {
					temp = sdao.filterSource(s.toString());
					android.util.Log.wtf("Yolo","oloy");
				} else {
					temp = sdao.getAllSource();
					android.util.Log.wtf("Happens","hahaha");
				}
				a = new SourceAdapter(c, temp,sgdao, year, month);
				sourceList.setAdapter(a);

			}
			public void beforeTextChanged(CharSequence s, int start, int before, int count) {}
			public void onTextChanged(CharSequence s, int start, int before, int count) {}			
		});


		/*
		 * Set onclick for add source button
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
	}
}
