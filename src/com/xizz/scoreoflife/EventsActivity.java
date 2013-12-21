package com.xizz.scoreoflife;

import java.sql.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.xizz.scoreoflife.adapter.EventsAdapter;
import com.xizz.scoreoflife.db.DataSource;
import com.xizz.scoreoflife.object.Event;
import com.xizz.scoreoflife.util.Util;

public class EventsActivity extends Activity implements
		OnItemLongClickListener, OnItemClickListener {

	private DataSource mSource;
	private ListView mEventsView;
	private EventsAdapter mAdapter;
	private Event mEventClicked;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_events);
		
		mSource = DataSource.getDataSource(this);

		mEventsView = (ListView) findViewById(R.id.eventsList);
		mEventsView.setOnItemLongClickListener(this);
		mEventsView.setOnItemClickListener(this);
	}

	@Override
	protected void onResume() {
		List<Event> events = mSource.getAllEvents();

		mAdapter = new EventsAdapter(this, events);
		mEventsView.setAdapter(mAdapter);
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.events, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add_event:
			startActivityForResult(new Intent(this, EventInputActivity.class),
					Util.REQUEST_ADD);
			break;
		}
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		final Event event = (Event) mAdapter.getItem(position);
		Intent intent = new Intent(this, EventDetailActivity.class);
		intent.putExtra(Util.ID, event.id);
		intent.putExtra(Util.NAME, event.name);
		intent.putExtra(Util.SCORE, event.score);
		intent.putExtra(Util.DATE, event.startDate);
		startActivity(intent);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		mEventClicked = (Event) mAdapter.getItem(position);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// The first item cannot move up and the last item cannot move down,
		// so the menu items needs to be handled differently.
		if (position == 0) {
			builder.setItems(new String[] { "Edit", "Delete", "Move Down" },
					getFirstMenu(position));
		} else if (position == mAdapter.getCount() - 1) {
			builder.setItems(new String[] { "Move Up", "Edit", "Delete" },
					getLastMenu(position));
		} else {
			builder.setItems(new String[] { "Move Up", "Edit", "Delete",
					"Move Down" }, getMiddleMenu(position));
		}
		builder.show();
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode != RESULT_OK || data == null) {
			return;
		}

		switch (requestCode) {
		case Util.REQUEST_ADD:
			Event event = new Event();
			event.name = data.getStringExtra(Util.NAME);
			event.score = data.getIntExtra(Util.SCORE, 0);
			event.startDate = data.getLongExtra(Util.DATE, 0);
			mSource.insertEvent(event);
			mAdapter.add(event);
			mAdapter.notifyDataSetChanged();
			break;
		case Util.REQUEST_EDIT:
			mEventClicked.name = data.getStringExtra(Util.NAME);
			mEventClicked.score = data.getIntExtra(Util.SCORE, 0);
			mEventClicked.startDate = data.getLongExtra(Util.DATE, 0);
			mSource.updateEvent(mEventClicked);
			break;
		}
	}

	private void editEvent(Event event) {
		Intent inputIntent = new Intent(EventsActivity.this,
				EventInputActivity.class);
		inputIntent.putExtra(Util.NAME, event.name);
		inputIntent.putExtra(Util.SCORE, event.score);
		inputIntent.putExtra(Util.DATE, new Date(event.startDate).toString());
		startActivityForResult(inputIntent, Util.REQUEST_EDIT);
	}

	private void askForDelete(final Event event) {
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
		alertBuilder.setMessage(event + Util.NEWLINE + Util.NEWLINE
				+ "Delete this item?");
		alertBuilder.setPositiveButton("Delete",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mSource.deleteEvent(event);
						mAdapter.remove(event);
						mAdapter.notifyDataSetChanged();
					}
				});
		alertBuilder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

		alertBuilder.show();
	}

	private DialogInterface.OnClickListener getFirstMenu(final int position) {
		return new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int index) {
				switch (index) {
				case 0:
					editEvent(mEventClicked);
					break;
				case 1:
					askForDelete(mEventClicked);
					break;
				case 2:
					swapIndex((Event) mAdapter.getItem(position + 1),
							mEventClicked);
					break;
				}
			}
		};
	}

	private DialogInterface.OnClickListener getLastMenu(final int position) {
		return new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int index) {
				switch (index) {
				case 0:
					swapIndex((Event) mAdapter.getItem(position - 1),
							mEventClicked);
					break;
				case 1:
					editEvent(mEventClicked);
					break;
				case 2:
					askForDelete(mEventClicked);
					break;
				}
			}
		};
	}

	private DialogInterface.OnClickListener getMiddleMenu(final int position) {
		return new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int index) {
				switch (index) {
				case 0:
					swapIndex((Event) mAdapter.getItem(position - 1),
							mEventClicked);
					break;
				case 1:
					editEvent(mEventClicked);
					break;
				case 2:
					askForDelete(mEventClicked);
					break;
				case 3:
					swapIndex((Event) mAdapter.getItem(position + 1),
							mEventClicked);
					break;
				}
			}
		};
	}

	private void swapIndex(Event e1, Event e2) {
		int tempIndex = e1.index;
		e1.index = e2.index;
		e2.index = tempIndex;
		mSource.updateEvent(e1);
		mSource.updateEvent(e2);
		List<Event> events = mSource.getAllEvents();
		mAdapter = new EventsAdapter(this, events);
		mEventsView.setAdapter(mAdapter);
	}
}
