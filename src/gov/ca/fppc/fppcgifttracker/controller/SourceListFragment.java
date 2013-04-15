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
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class SourceListFragment extends DialogFragment{

	private SourceDAO sdao;
	private GiftDAO gdao;
	private GiftSourceRelationDAO sgdao;
	private int month;
	private int year;
	private List<Source> source;
	private EditText searchtext;
	private ListView sourceList;
	private Activity parent;
	private ListItemClick callback;

	public interface ListItemClick {
		public void processChosenSource(Source src);
	}

	@Override
	public void onDestroy() {
		sdao.close();
		gdao.close();
		sgdao.close();
		super.onDestroy();
	}
	
	public void updateMonthYear(int month, int year) {
		this.month = month;
		this.year = year;
		SourceAdapter adapt = (SourceAdapter)this.sourceList.getAdapter();
		adapt.updateYearMonth(year, month);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			callback = (ListItemClick) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement ListItemClick interface");
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		filtering(searchtext.getText());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		/*
		 * Connect DAOs (Data access objects)
		 */
		this.parent = getActivity();
		sdao = new SourceDAO(parent);
		sdao.open();
		gdao = new GiftDAO(parent);
		gdao.open();
		sgdao = new GiftSourceRelationDAO(parent);
		sgdao.open();
		
		/*month and year, default to current*/
		this.month = Calendar.getInstance().get(Calendar.MONTH);
		this.year = Constant.currentYear;
		View view = inflater.inflate(R.layout.source_search_fragment, 
				container, false);
		/* view handle should be done here */
		this.sourceList = (ListView) view.findViewById(R.id.src_list);
		this.searchtext = (EditText) view.findViewById(R.id.search_bar);

		/* populate the list*/
		setup();

		searchtext.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {
				filtering(s);
			}
			public void beforeTextChanged(CharSequence s, int start, int before, int count) {}
			public void onTextChanged(CharSequence s, int start, int before, int count) {}			
		});
		/* on item click listener*/
		sourceList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				SourceAdapter a = (SourceAdapter) sourceList.getAdapter();
				callback.processChosenSource(a.getSource(position));
			}
		});
		return view;
	}
	public void filtering(Editable s) {
		if ((s != null) && (s.toString().length() > 0)) {
			source = sdao.filterSource(s.toString());
		} else {
			source = sdao.getAllSource();;
			Collections.sort(source, new SourceComparator(sgdao,year,month));
		}
		SourceAdapter adapt = (SourceAdapter)sourceList.getAdapter();
		adapt.notifyDataSetChanged();
	}
	public void setup() {
		/*
		 * Set up the source List
		 */
		source = this.sdao.getAllSource();
		/* sort it first */
		Collections.sort(source, new SourceComparator(sgdao,year,month));
		ArrayAdapter<Source> adapter = new SourceAdapter(parent,source,sgdao, 
				this.year, this.month);		
		sourceList.setAdapter(adapter);
	}

}
