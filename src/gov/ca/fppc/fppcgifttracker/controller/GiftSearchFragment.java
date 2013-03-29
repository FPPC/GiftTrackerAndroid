package gov.ca.fppc.fppcgifttracker.controller;

import gov.ca.fppc.fppcgifttracker.R;
import gov.ca.fppc.fppcgifttracker.controller.SourceListFragment.ListItemClick;
import gov.ca.fppc.fppcgifttracker.model.Gift;
import gov.ca.fppc.fppcgifttracker.model.GiftDAO;
import gov.ca.fppc.fppcgifttracker.model.GiftSourceRelationDAO;
import gov.ca.fppc.fppcgifttracker.model.Source;
import gov.ca.fppc.fppcgifttracker.model.SourceDAO;

import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

public class GiftSearchFragment extends Fragment {
	private SourceDAO sdao;
	private GiftDAO gdao;
	private GiftSourceRelationDAO sgdao;
	private int month;
	private int year;
	private List<Gift> gift;
	private EditText searchtext;
	private ListView giftList;
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
		//TODO
	}
	
	@Override
	public void onAttach(Activity a) {
		super.onAttach(a);
		try {
			callback = (ListItemClick)a;
		} catch (ClassCastException e) {
			throw new ClassCastException(a.toString()+" must implement ListItemClick interface");
		}
	}
	
	@Override
	public void onResume() {
		filtering(searchtext.getText());
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, Viewgroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		this.parent = getActivity();
		sdao = new SourceDAO(parent);
		sdao.open();
		gdao = new GiftDAO(parent);
		gdao.open();
		sgdao = new GiftSourceRelationDAO(parent);
		sgdao.open();
		
		this.month = Calendar.getInstance().get(Calendar.MONTH);
		this.year = Calendar.getInstance().get(Calendar.YEAR);
		View view = inflater.inflate(R.layout.source_search_fragment, container, false);
		this.giftList = (ListView) view.findViewById(R.id.gift_list_in_fragment);
		this.searchtext = (EditText) view.findViewById(R.id.gift_search_bar);
		
		setup();
		
		searchtext.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				filtering(s);
			}
			public void beforeTextChanged(CharSequence s, int start, int before, int count){}
			public void onTextChanged(CharSequence s, int start, int before, int count){}
		});
		
		giftList.setOnItemClickListener(new OnItemClickListener () {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				GiftListAdapter a = (GiftListAdapter) giftList.getAdapter();
				callback.processChosenGift
			}
		}
	}
}
