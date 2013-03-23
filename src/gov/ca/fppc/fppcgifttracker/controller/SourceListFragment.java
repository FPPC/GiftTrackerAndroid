package gov.ca.fppc.fppcgifttracker.controller;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import gov.ca.fppc.fppcgifttracker.R;
import gov.ca.fppc.fppcgifttracker.model.GiftDAO;
import gov.ca.fppc.fppcgifttracker.model.GiftSourceRelationDAO;
import gov.ca.fppc.fppcgifttracker.model.Source;
import gov.ca.fppc.fppcgifttracker.model.SourceDAO;
import gov.ca.fppc.fppcgifttracker.util.SourceComparator;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class SourceListFragment extends Fragment{

	private SourceDAO sdao;
	private GiftDAO gdao;
	private GiftSourceRelationDAO sgdao;
	private int month;
	private int year;
	private List<Source> source;
	private EditText searchtext;
	private ListView sourceList;
	private Activity parent;

	@Override
	public void onAttach(Activity parent) {
		super.onAttach(parent);
		this.parent = parent;
	}
	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);
		/*
		 * Connect DAOs (Data access objects)
		 */
		sdao = new SourceDAO(parent);
		sdao.open();
		gdao = new GiftDAO(parent);
		gdao.open();
		sgdao = new GiftSourceRelationDAO(parent);
		sgdao.open();
	}
	@Override
	public void onDestroy() {
		sdao.close();
		gdao.close();
		sgdao.close();
		super.onDestroy();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.source_search_fragment, 
				container, false);
		return view;
	}

	public void setup() {
		/*
		 * Current month and year:
		 */
		this.year = Calendar.getInstance().get(Calendar.YEAR);
		this.month = Calendar.getInstance().get(Calendar.MONTH);
		
		/*
		 * Set up the source List
		 */
		source = sdao.getAllSource();
		/* sort it first */
		Collections.sort(source, new SourceComparator(sgdao));

		ArrayAdapter<Source> adapter = new SourceAdapter(parent,source,sgdao, 
				this.year, this.month);
		sourceList = (ListView)parent.findViewById(R.id.src_list);
		sourceList.setAdapter(adapter);

		/*
		 * Set up the search bar
		 */
		searchtext = (EditText) parent.findViewById(R.id.search_bar);
		searchtext.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {
				List<Source> temp;
				ArrayAdapter<Source> a;
				if (s.toString().length() > 0) {
					temp = sdao.filterSource(s.toString());
					/* DEBUG android.util.Log.wtf("Yolo","oloy");*/
				} else {
					temp = sdao.getAllSource();;
					Collections.sort(source, new SourceComparator(sgdao));
					/* DEBUG android.util.Log.wtf("Happens","hahaha");*/
				}
				a = new SourceAdapter(parent, temp,sgdao, year, month);
				sourceList.setAdapter(a);

			}
			public void beforeTextChanged(CharSequence s, int start, int before, int count) {}
			public void onTextChanged(CharSequence s, int start, int before, int count) {}			
		});
	}

}
