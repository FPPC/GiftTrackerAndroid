package gov.ca.fppc.fppcgifttracker.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import gov.ca.fppc.fppcgifttracker.R;
import gov.ca.fppc.fppcgifttracker.model.GiftDAO;
import gov.ca.fppc.fppcgifttracker.model.GiftSourceRelationDAO;
import gov.ca.fppc.fppcgifttracker.model.Source;
import gov.ca.fppc.fppcgifttracker.model.SourceDAO;
import gov.ca.fppc.fppcgifttracker.util.MiscUtil;

public class NewGift extends Activity {

	private EditText editYear;
	private EditText editMonth;
	private EditText editDay;
	private EditText description;
	private TextView dateError;
	private Button cancel;
	private Button save;
	private Button add;
	private Calendar today;
	private ListView selectedList;
	private TextWatcher tw;
	private List<Source> selected;
	private List<Double> contribution;
	private Intent i;
	private GiftDAO gdao;
	private GiftSourceRelationDAO sgdao;
	private SourceDAO sdao;
	private int mode;
	
	/*save and load*/
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putSerializable("source_list", (Serializable) this.selected);
		savedInstanceState.putSerializable("contribution", (Serializable) this.contribution);
	}
	@Override
	public void onRestoreInstanceState(Bundle saved){

	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.new_gift);

		/*init the source list */
		selected = new ArrayList<Source>();

		/* get view handles */
		dateError = (TextView)this.findViewById(R.id.date_error);
		editYear = (EditText)this.findViewById(R.id.gft_year);
		editMonth = (EditText)this.findViewById(R.id.gft_month);
		editDay = (EditText)this.findViewById(R.id.gft_day);
		description = (EditText)this.findViewById(R.id.gft_description);
		cancel = (Button)this.findViewById(R.id.new_gift_cancel);
		save = (Button)this.findViewById(R.id.new_gift_save);
		add = (Button)this.findViewById(R.id.new_gift_add_source);
		selectedList = (ListView)this.findViewById(R.id.selected_src_list);

		/* get today date*/
		today = Calendar.getInstance();

		/* fill out the form for today */
		dateError.setText("");
		editYear.setText(String.format("%d",today.get(Calendar.YEAR)));
		editMonth.setText(String.format("%d",1+today.get(Calendar.MONTH)));
		editDay.setText(String.format("%d",today.get(Calendar.DAY_OF_MONTH)));

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

		/*set the selected list adapter */
		SourceAdapter srcAdapt = new SourceAdapter(this, selected, sgdao,today.get(Calendar.YEAR),today.get(Calendar.MONTH));
		selectedList.setAdapter(srcAdapt);

		/* set the add source button */
		add.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				//showAddSourceDialog();
				startSelectingActivity();
			};
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == Constant.APPEND_SOURCE) {
			if (data.hasExtra(Constant.SRC)) {
				Source getback = (Source) data.getSerializableExtra(Constant.SRC);
				selected.add(getback);
				SourceAdapter srcAdapt = new SourceAdapter(this, selected, sgdao,today.get(Calendar.YEAR),today.get(Calendar.MONTH));
				selectedList.setAdapter(srcAdapt);
			}
		}
	}

	/*New Activity Style */
	private void startSelectingActivity() {
		Intent i = new Intent(this, SelectSource.class);
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
		//TODO
	}
	private void processEdit() {
		//TODO
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
		if (checkDate()) {
			save.setEnabled(true);
			dateError.setText("");
			return;
		} else {
			save.setEnabled(false);
			dateError.setText(R.string.please_enter_a_valid_date);
			return;
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
