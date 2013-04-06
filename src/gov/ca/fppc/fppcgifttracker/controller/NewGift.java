package gov.ca.fppc.fppcgifttracker.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import gov.ca.fppc.fppcgifttracker.R;
import gov.ca.fppc.fppcgifttracker.controller.ContributionOptionFragment.ContributionOption;
import gov.ca.fppc.fppcgifttracker.model.Gift;
import gov.ca.fppc.fppcgifttracker.model.GiftDAO;
import gov.ca.fppc.fppcgifttracker.model.GiftSourceRelationDAO;
import gov.ca.fppc.fppcgifttracker.model.Source;
import gov.ca.fppc.fppcgifttracker.model.SourceDAO;
import gov.ca.fppc.fppcgifttracker.util.MiscUtil;

public class NewGift extends Activity implements ContributionOption {

	private EditText editYear;
	private EditText editMonth;
	private EditText editDay;
	private EditText description;
	private TextView sumDisplay;
	private TextView dateError;
	private Button cancel;
	private Button save;
	private Button add;
	private Button edit;
	private Calendar today;
	private ListView selectedList;
	private TextWatcher tw;
	private List<Source> selected;
	private List<Source> removal;
	private Intent i;
	private GiftDAO gdao;
	private GiftSourceRelationDAO sgdao;
	private SourceDAO sdao;
	private int mode;
	private long gid;
	private View.OnFocusChangeListener valueUpdater;
	private boolean hasItem;
	private boolean correctDate;

	private Source chosenSource;

	/*save and load*/
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putSerializable("source_list", (Serializable) this.selected);
		savedInstanceState.putSerializable("remove_list", (Serializable) this.removal);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.new_gift);
		chosenSource = null;
		/* init the source list */
		if (savedInstanceState ==null) {
			selected = new ArrayList<Source>();
			removal = new ArrayList<Source>();
		} else {
			selected = (List<Source>) savedInstanceState.getSerializable("source_list");
			removal = (List<Source>) savedInstanceState.getSerializable("remove_list");
		}

		/* get view handles */
		dateError = (TextView)this.findViewById(R.id.date_error);
		editYear = (EditText)this.findViewById(R.id.gft_year);
		editMonth = (EditText)this.findViewById(R.id.gft_month);
		editDay = (EditText)this.findViewById(R.id.gft_day);
		description = (EditText)this.findViewById(R.id.gft_description);
		cancel = (Button)this.findViewById(R.id.new_gift_cancel);
		save = (Button)this.findViewById(R.id.new_gift_save);
		add = (Button)this.findViewById(R.id.new_gift_add_source);
		edit = (Button)this.findViewById(R.id.edit_contribution);
		
		selectedList = (ListView)this.findViewById(R.id.selected_src_list);
		sumDisplay = (TextView)this.findViewById(R.id.totalvalue);
		/* get today date*/
		today = Calendar.getInstance();

		/* fill out the form for today */
		dateError.setText("");
		editYear.setText(String.format("%d",today.get(Calendar.YEAR)));
		editMonth.setText(String.format("%d",1+today.get(Calendar.MONTH)));
		editDay.setText(String.format("%d",today.get(Calendar.DAY_OF_MONTH)));
		correctDate = true;

		/* make the text watcher for verifying date*/
		tw = new TextWatcher() {
			public void afterTextChanged(Editable s) {
				fixDate();
			}
			public void beforeTextChanged(CharSequence s, int start, int before, int count) {}
			public void onTextChanged(CharSequence s, int start, int before, int count) {}		
		};

		/* set the watcher */
		editYear.addTextChangedListener(tw);
		editMonth.addTextChangedListener(tw);
		editDay.addTextChangedListener(tw);

		/* get the DAOs */
		sdao = new SourceDAO(this);
		sdao.open();
		gdao = new GiftDAO(this);
		sgdao = new GiftSourceRelationDAO(this);
		gdao.open();
		sgdao.open();

		/*get the intent and work with the mode */
		i = getIntent();
		mode = i.getIntExtra(Constant.MODE,Constant.NEW);
		processMode(mode);

		/* set the display of total sum */
		valueUpdater = new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					updateSum();
				}
			}
		};

		/* setup the selected list */
		setupAdapter();

		/* set the add source button */
		add.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				//showAddSourceDialog();
				startSelectingActivity();
			}
		});
		/*set the save gift button */
		save.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				saveGift();
				finish();
			}
		});
		cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		edit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setEdit();
			}
		});
		
	}
	
	private void updateSum() {
		double sum = 0.0;
		for(int i = 0; i < selectedList.getCount();i++) {
			try {
				EditText contribution = (EditText) selectedList.getChildAt(i).findViewById(R.id.contribution);
				double value = Double.parseDouble(contribution.getText().toString());
				sum+= value;
			} catch (NullPointerException E) {
				sum+=0.0;
			} catch (NumberFormatException n) {
				sum+=0.0;
			}
		}
		sumDisplay.setText(String.format("  $%.2f", sum));
	}

	private void setEdit() {
		ContributionAdapter srcAdapt = (ContributionAdapter) selectedList.getAdapter();
		srcAdapt.toggleEdit();
		if (edit.getText().toString().equals("Edit Value")) {
			edit.setText("Finish Edit");
		} else {
			edit.setText("Edit Value");
			updateSum();
		}
	}
	private void setupAdapter() {
		ContributionAdapter srcAdapt = new ContributionAdapter(this, selected, sgdao, gid,valueUpdater);
		selectedList.setAdapter(srcAdapt);
		selectedList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position,
					long id) {
				ContributionAdapter a = (ContributionAdapter) selectedList.getAdapter();
				processChosenSource(a.getItem(position));
				Log.wtf("item clicked", ""+position);
			}
		});
		Log.wtf("Ran", "it should have been set");
		hasItem = !(selectedList.getCount()==0);
		fixDate();
	}

	private void processChosenSource(Source src) {
		this.chosenSource = src;
		FragmentManager fm = getFragmentManager();
		ContributionOptionFragment popup = new ContributionOptionFragment();
		popup.show(fm,"confirm");
	}

	public void processOption(int option) {
		switch (option) {
		case Constant.DELETE:
			deleteContrib();
			this.chosenSource = null;
			break;
		}
	}

	private void deleteContrib() {
		ContributionAdapter a = (ContributionAdapter) selectedList.getAdapter();
		selected.remove(chosenSource);
		removal.add(chosenSource);
		a.notifyDataSetChanged();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == Constant.APPEND_SOURCE) {
			if (data.hasExtra(Constant.SRC)) {
				Source getback = (Source) data.getSerializableExtra(Constant.SRC);
				if (!selected.contains(getback)) {
					selected.add(getback);
				}
				ContributionAdapter adapt = (ContributionAdapter) selectedList.getAdapter();
				adapt.notifyDataSetChanged();
				hasItem = !(selectedList.getCount()==0);
			}
		}
	}

	/*New Activity Style */
	private void startSelectingActivity() {
		Intent i = new Intent(this, SelectSource.class);
		if (correctDate) {
			i.putExtra("month",Integer.parseInt(editMonth.getText().toString()));
		}
		startActivityForResult(i,Constant.APPEND_SOURCE);
	}

	/* deal with the type of intent */
	private void processMode(int mode) {
		switch (mode) {
		case Constant.NEW:
			processNew();
			break;
		case Constant.EDIT:
			processEdit();
			break;
		}
	}

	private void processNew() {
		this.gid = -1;
		if (i.hasExtra(Constant.SRC)) {
			Source src = (Source)i.getSerializableExtra(Constant.SRC);
			if (!selected.contains(src)) {
				selected.add(src);
			}
		}
	}
	private void processEdit() {
		Gift g = (Gift) i.getSerializableExtra(Constant.GFT);
		// populate the text view items
		this.gid = g.getID();
		this.editYear.setText(""+g.getYear());
		this.editMonth.setText(""+g.getMonth());
		this.editDay.setText(""+g.getDay());
		this.description.setText(g.getDescription());
		fixDate();
		//selected and contribution
		selected.clear();
		selected.addAll(sgdao.listOfDonor(gid));

		double sum = 0.0;
		double value = 0.0;
		for(int i =0; i < selected.size();i++) {
			value = sgdao.getValue(gid, selected.get(i).getID());
			sum+=value;
		}
		sumDisplay.setText(String.format("$%.2f",sum));
	}

	/* Check the date */
	private boolean checkDate() {
		int y=0,m=0,d=0;
		try {
			y = Integer.parseInt(editYear.getText().toString());
			m = Integer.parseInt(editMonth.getText().toString());
			d = Integer.parseInt(editDay.getText().toString());
		} catch (NumberFormatException e){
			/*do nothing*/
		}
		return (MiscUtil.ValidateDate(y, m, d));
	}

	private void fixDate() {
		correctDate = checkDate();
		if (correctDate) {
			save.setEnabled(correctDate&&hasItem);
			dateError.setText("");
			return;
		} else {
			save.setEnabled(correctDate&&hasItem);
			dateError.setText(R.string.please_enter_a_valid_date);
			return;
		}
	}

	private void saveGift() {
		/*save to table gift*/
		if (correctDate && hasItem) {
			int year = Integer.parseInt(editYear.getText().toString());
			int month = Integer.parseInt(editMonth.getText().toString());
			int day = Integer.parseInt(editDay.getText().toString());
			String des = description.getText().toString();
			Gift g;
			if (mode == Constant.NEW) {
				g = gdao.createGift(year, month, day, des);

			} else {
				g = new Gift(gid, year, month, day, des);
				gdao.updateGift(g);
			}	
			/*now the value*/
			for(int i = 0; i < selectedList.getCount();i++) {
				EditText contribution = (EditText) selectedList.getChildAt(i).findViewById(R.id.contribution);
				double value = Double.parseDouble(contribution.getText().toString());
				long sid = selected.get(i).getID();
				sgdao.createRelationIfNotExist(sid, g.getID(), value);
			}
			
			for(int i = 0; i < removal.size();i++) {
				sgdao.deleteRelation(removal.get(i).getID(), gid);
			}
			removal.clear();
			updateSum();
		}
	}

	@Override
	public void onDestroy() {
		sdao.close();
		sgdao.close();
		gdao.close();
		super.onDestroy();
	}
}
