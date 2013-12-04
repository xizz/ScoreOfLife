package com.xizz.scoreoflife;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Toast;

import com.xizz.scoreoflife.adapter.ChecksPagerAdapter;
import com.xizz.scoreoflife.db.DataSource;
import com.xizz.scoreoflife.util.Util;

public class MainActivity extends FragmentActivity {

	private DataSource mSource;
	private long mDisplayDate = -1;
	private ViewPager mPager;
	private ChecksPagerAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mSource = DataSource.getDataSource(this);
		mSource.open();

		mPager = (ViewPager) findViewById(R.id.pager);

	}

	@Override
	protected void onResume() {
		mAdapter = new ChecksPagerAdapter(this);
		mPager.setAdapter(mAdapter);
		// mPosition could be saved from previous state.
		// Set it to the current day be default.
		if (mDisplayDate == -1) {
			mDisplayDate = Util.getToday();
		}
		if (mDisplayDate < mAdapter.getFirstDay()) {
			mDisplayDate = mAdapter.getFirstDay();
		}
		mPager.setCurrentItem(dateToIndex(mDisplayDate, mAdapter.getFirstDay()));
		super.onResume();
	}

	@Override
	protected void onPause() {
		mDisplayDate = indexToDate(mPager.getCurrentItem(),
				mAdapter.getFirstDay());
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		mSource.close();
		super.onDestroy();
	}

	public void clicked(View view) {
		Toast.makeText(this, "clicked", Toast.LENGTH_LONG).show();
	}

	public void manageEvents(View view) {
		Intent intent = new Intent(this, EventsActivity.class);
		startActivity(intent);
	}

	public void showScore(View view) {
		Intent intent = new Intent(this, ScoreActivity.class);
		startActivity(intent);
	}

	private static int dateToIndex(long date, long startDate) {
		return (int) ((date - startDate) / Util.ONEDAY);
	}

	private static long indexToDate(int index, long startDate) {
		return startDate + index * Util.ONEDAY;
	}

	}
