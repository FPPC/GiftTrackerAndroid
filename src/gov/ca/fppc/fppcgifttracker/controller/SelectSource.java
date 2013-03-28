package gov.ca.fppc.fppcgifttracker.controller;

import java.util.Calendar;

import gov.ca.fppc.fppcgifttracker.R;
import gov.ca.fppc.fppcgifttracker.controller.SourceListFragment.ListItemClick;
import gov.ca.fppc.fppcgifttracker.model.Source;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SelectSource extends Activity implements ListItemClick{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_source);
		SourceListFragment frag = (SourceListFragment) getFragmentManager().findFragmentById(R.id.source_select_fragment);
		Intent i = getIntent();
		if (i.hasExtra("month")) {
			frag.updateMonthYear(i.getIntExtra("month",Calendar.getInstance().get(Calendar.MONTH)), Calendar.getInstance().get(Calendar.YEAR));
		}
	}
	
	@Override
	public void processChosenSource(Source src) {
		Intent i = new Intent();
		i.putExtra(Constant.SRC, src);
		setResult(RESULT_OK, i);
		super.finish();
	}
}
