package gov.ca.fppc.fppcgifttracker.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import gov.ca.fppc.fppcgifttracker.R;
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
	private Calendar today;
	private TextWatcher tw;
	private AutoCompleteTextView sourceSelectList;
	private List<Source> selected;
	private List<Source> candidate;
	private List<Double> contribution;


	private SourceDAO sdao;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.new_gift);
		/* get view handles */
		dateError = (TextView)this.findViewById(R.id.date_error);
		editYear = (EditText)this.findViewById(R.id.gft_year);
		editMonth = (EditText)this.findViewById(R.id.gft_month);
		editDay = (EditText)this.findViewById(R.id.gft_day);
		description = (EditText)this.findViewById(R.id.gft_description);
		cancel = (Button)this.findViewById(R.id.new_gift_cancel);
		save = (Button)this.findViewById(R.id.new_gift_save);
		sourceSelectList = (AutoCompleteTextView)this.findViewById(R.id.source_select);

		/* get today date*/
		today = Calendar.getInstance();
		/* fill out the form for today */
		dateError.setText("");
		editYear.setText(String.format("%d",today.get(Calendar.YEAR)));
		editMonth.setText(String.format("%d",1+today.get(Calendar.MONTH)));
		editDay.setText(String.format("%d",today.get(Calendar.DAY_OF_MONTH)));

		/* make the text watcher */
		tw = new TextWatcher() {
			public void afterTextChanged(Editable s) {
				checkDate();
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
		
		setAutoComplete();

	}

	private void setAutoComplete() {
		/* get the candidate list <all sources> */
		candidate = sdao.getAllSource();
		/* setup the selected list <empty at first> */
		selected = new ArrayList<Source>();
		contribution = new ArrayList<Double>();

		/*ArrayAdapter for the suggestion list*/
		ArrayAdapter<Source> suggestion;
		sourceSelectList.addTextChangedListener(new TextWatcher () {

			public void afterTextChanged(Editable s) {
				if (s.toString().length() > 0) {
					candidate = sdao.filterSource(s.toString());
				} else {

				}
			}
			public void beforeTextChanged(CharSequence s, int start, int before, int count) {}
			public void onTextChanged(CharSequence s, int start, int before, int count) {}			
		});
		
	}

	/* Check the date */
	private void checkDate() {
		int y=0,m=0,d=0;
		try {
			y = Integer.parseInt(editYear.getText().toString());
			m = Integer.parseInt(editMonth.getText().toString());
			d = Integer.parseInt(editDay.getText().toString());
		} catch (NumberFormatException e){
			/*do nothing*/
		}
		if (MiscUtil.ValidateDate(y, m, d)) {
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
	}
}
