package com.xizz.scoreoflife;

import java.sql.Date;
import java.text.ParseException;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import com.xizz.scoreoflife.util.Util;

public class EventInputActivity extends Activity implements OnDateSetListener {

	private DatePickerDialog mDatePicker;
	private EditText mNameView;
	private EditText mScoreView;
	private EditText mDateView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_input);

		mNameView = (EditText) findViewById(R.id.editTextName);
		mScoreView = (EditText) findViewById(R.id.editTextScore);
		mDateView = (EditText) findViewById(R.id.editTextDate);

		Intent intent = getIntent();
		String date = intent.getStringExtra(Util.DATE);
		// Find out if this is call from new or edit.
		if (date != null) {
			mNameView.setText(intent.getStringExtra(Util.NAME));
			mScoreView.setText(Integer.toString(intent.getIntExtra(Util.SCORE,
					0)));
			mDateView.setText(intent.getStringExtra(Util.DATE));
			setTitle("Edit Event");
		} else {
			mDateView.setText(new Date(System.currentTimeMillis()).toString());
		}

		setDatePickerByDateView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.event_input, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.done_input:
			doneInput();
			break;
		}
		return true;
	}
	
	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		mDateView.setText("" + year + "-"
				+ String.format("%02d", (monthOfYear + 1)) + "-"
				+ String.format("%02d", dayOfMonth));
	}

	private void doneInput() {
		String name = mNameView.getText().toString();
		String score = mScoreView.getText().toString();
		String date = mDateView.getText().toString();
		boolean error = false;
		if (name.length() == 0) {
			mNameView.setError("Missing event name.");
			error = true;
		}
		if (score.length() == 0) {
			mScoreView.setError("Missing event score.");
			error = true;
		}
		if (error)
			return;
		Intent output = new Intent();
		output.putExtra(Util.NAME, name);
		output.putExtra(Util.SCORE, Integer.parseInt(score));
		try {
			output.putExtra(Util.DATE, Util.DATE_FORMAT.parse(date).getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		setResult(RESULT_OK, output);
		finish();
	}

	public void pickDate(View view) {
		mDatePicker.show();
	}

	private void setDatePickerByDateView() {
		String date = mDateView.getText().toString();
		int year = Integer.parseInt(date.substring(0, 4));
		int month = Integer.parseInt(date.substring(5, 7)) - 1;
		int day = Integer.parseInt(date.substring(8, 10));

		mDatePicker = new DatePickerDialog(this, this, year, month, day);
	}
}
