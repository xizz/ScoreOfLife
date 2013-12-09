package com.xizz.scoreoflife.adapter;

import java.sql.Date;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xizz.scoreoflife.R;
import com.xizz.scoreoflife.object.Event;

public class EventsAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<Event> mEvents;

	public EventsAdapter(Context context, List<Event> events) {
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mEvents = events;
	}

	@Override
	public int getCount() {
		return mEvents.size();
	}

	@Override
	public Object getItem(int position) {
		return mEvents.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final View row = mInflater.inflate(R.layout.event, parent, false);

		final TextView nameView = (TextView) row
				.findViewById(R.id.eventItemName);
		final TextView scoreView = (TextView) row
				.findViewById(R.id.eventItemScore);
		final TextView dateView = (TextView) row
				.findViewById(R.id.eventItemDate);
		final Event event = mEvents.get(position);
		nameView.setText(event.name);
		scoreView.setText(Integer.toString(event.score));
		dateView.setText(new Date(event.startDate).toString());

		return (row);
	}
	
	public void remove(Event event) {
		mEvents.remove(event);
	}

	public void add(Event event) {
		mEvents.add(event);
	}

}
