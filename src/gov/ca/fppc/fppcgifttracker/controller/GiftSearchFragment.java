package gov.ca.fppc.fppcgifttracker.controller;

import gov.ca.fppc.fppcgifttracker.R;
import gov.ca.fppc.fppcgifttracker.model.Gift;
import gov.ca.fppc.fppcgifttracker.model.GiftDAO;
import gov.ca.fppc.fppcgifttracker.model.GiftSourceRelationDAO;
import gov.ca.fppc.fppcgifttracker.model.Source;
import gov.ca.fppc.fppcgifttracker.model.SourceDAO;
import gov.ca.fppc.fppcgifttracker.util.GiftComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
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

public class GiftSearchFragment extends Fragment {
	private SourceDAO sdao;
	private GiftDAO gdao;
	private GiftSourceRelationDAO sgdao;
	private Source source;
	private int year;
	private List<Gift> gift;
	private EditText searchtext;
	private ListView giftList;
	private Activity parent;
	private GiftItemClick callback;

	public interface GiftItemClick {
		public void processChosen(Gift gft);
	}

	@Override
	public void onDestroy() {
		sdao.close();
		gdao.close();
		sgdao.close();
		super.onDestroy();
	}

	public void updateSource(Source source) {
		this.source = source;
		GiftListAdapter adapt = (GiftListAdapter) giftList.getAdapter();
		adapt.updateSource(source);
		filtering(searchtext.getText());
	}

	@Override
	public void onAttach(Activity a) {
		super.onAttach(a);
		try {
			callback = (GiftItemClick)a;
		} catch (ClassCastException e) {
			throw new ClassCastException(a.toString()+" must implement GiftItemList interface");
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
		this.parent = getActivity();
		sdao = new SourceDAO(parent);
		sdao.open();
		gdao = new GiftDAO(parent);
		gdao.open();
		sgdao = new GiftSourceRelationDAO(parent);
		sgdao.open();

		source = null;

		this.year = Constant.currentYear;

		View view = inflater.inflate(R.layout.gift_list_fragment, container, false);
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
				callback.processChosen(a.getItem(position));
			}
		});
		return view;
	}

	public void filtering(Editable s) {
		if (source == null) {
			if ((s.toString()).length() > 0) {
				gift.clear();
				gift.addAll(gdao.filterGift(s.toString()));

			} else {
				gift.clear();
				gift.addAll(gdao.getAllGift(year));
				Collections.sort(gift, new GiftComparator());
			
			}
			GiftListAdapter adapt = (GiftListAdapter) giftList.getAdapter();
			adapt.notifyDataSetChanged();
		} else {
			if ((s.toString()).length() > 0) {
				gift.clear();
				gift.addAll(sgdao.filterGiftFrom(source.getID(), year, s.toString()));
				
			} else {
				gift.clear();
				gift.addAll(sgdao.allGiftFrom(source.getID(), year));
				Collections.sort(gift,new GiftComparator());
			}
		}
		GiftListAdapter adapt = (GiftListAdapter) giftList.getAdapter();
		adapt.notifyDataSetChanged();
	}

	public void setup() {
		gift = new ArrayList<Gift>();
		ArrayAdapter<Gift> adapter = new GiftListAdapter(parent, gift, sgdao);
		giftList.setAdapter(adapter);
	}

}
