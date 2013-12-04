package com.xizz.scoreoflife.adapter;

import java.sql.Date;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.xizz.scoreoflife.R;
import com.xizz.scoreoflife.db.DataSource;
import com.xizz.scoreoflife.object.EventCheck;

public class ChecksAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<EventCheck> mChecks;
	private DataSource mDataSource;

	public ChecksAdapter(Context context, List<EventCheck> checks) {
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mChecks = checks;
		mDataSource = DataSource.getDataSource(context);
	}

	@Override
	public int getCount() {
		return mChecks.size();
	}

	@Override
	public Object getItem(int position) {
		return mChecks.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final View row = mInflater.inflate(R.layout.event_check, parent, false);

		final TextView nameView = (TextView) row
				.findViewById(R.id.eventItemName);
		final TextView scoreView = (TextView) row
				.findViewById(R.id.eventItemScore);
		final TextView dateView = (TextView) row
				.findViewById(R.id.eventItemDate);
		final EventCheck check = mChecks.get(position);
		nameView.setText(check.event.name);
		scoreView.setText(Integer.toString(check.event.score));
		dateView.setText(new Date(check.event.startDate).toString());

		final CheckBox checkBox = (CheckBox) row.findViewById(R.id.isDone);
		checkBox.setChecked(check.isDone);

		checkBox.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (((CheckBox) v).isChecked()) {
					check.isDone = true;
				} else {
					check.isDone = false;
				}
				mDataSource.updateCheck(check);
			}
		});
		return row;
	}

}
