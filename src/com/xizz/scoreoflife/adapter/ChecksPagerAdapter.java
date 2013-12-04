package com.xizz.scoreoflife.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.xizz.scoreoflife.R;
import com.xizz.scoreoflife.db.DataSource;
import com.xizz.scoreoflife.object.Event;
import com.xizz.scoreoflife.object.EventCheck;
import com.xizz.scoreoflife.util.Util;

public class ChecksPagerAdapter extends PagerAdapter {

	private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd", Locale.ENGLISH);
	private final static long TODAY = getToday();
	private final static long DAY_MILLISECS = 86400000;

	private long mFirstDay;
	private DataSource mSource;
	private Context mContext;
	private LayoutInflater mInflater;
	private List<Event> mEvents;

	public ChecksPagerAdapter(Context context) {
		mSource = DataSource.getDataSource(context);
		mContext = context;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mEvents = mSource.getAllEvents();
		mFirstDay = getEarliestDate(mEvents);
	}

	@Override
	public int getCount() {
		return (int) ((TODAY - mFirstDay) / DAY_MILLISECS) + 1;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View view = mInflater.inflate(R.layout.checklist, container, false);

		TextView dateView = (TextView) view.findViewById(R.id.dateText);
		ListView checksList = (ListView) view.findViewById(R.id.checklist);
		TextView emptyText = (TextView) view.findViewById(android.R.id.empty);

		checksList.setEmptyView(emptyText);

		long date = mFirstDay + DAY_MILLISECS * position;
		dateView.setText(new java.sql.Date(date).toString());

		createChecksIfNotExist(date);
		List<EventCheck> checks = mSource.getChecks(date, date
				+ (DAY_MILLISECS - 1));
		Util.removeLegacyChecks(mEvents, checks);
		Util.linkEventChecks(mEvents, checks);
		checksList.setAdapter(new ChecksAdapter(mContext, checks));
		container.addView(view);
		return view;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	public long getFirstDay() {
		return mFirstDay;
	}
	
	private void createChecksIfNotExist(long date) {
		List<EventCheck> checks = mSource.getChecks(date, date + DAY_MILLISECS
				- 1);
		for (Event e : mEvents) {
			if (!eventExist(e, checks)) {
				EventCheck check = new EventCheck(e.id, date);
				mSource.insertCheck(check);
			}
		}
	}

	private static long getToday() {
		try {
			return DATE_FORMAT.parse(DATE_FORMAT.format(new Date())).getTime();
		} catch (ParseException e) {
			return 0;
		}
	}

	private static long getEarliestDate(List<Event> events) {
		if (events == null || events.isEmpty())
			return TODAY;
		Event ealiestEvent = events.get(0);
		for (Event e : events) {
			if (e.startDate < ealiestEvent.startDate) {
				ealiestEvent = e;
			}
		}

		return ealiestEvent.startDate;
	}

	private static boolean eventExist(Event event, List<EventCheck> checks) {
		for (EventCheck c : checks) {
			if (event.id == c.eventId) {
				return true;
			}
		}
		return false;
	}

}
