package com.xizz.scoreoflife.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.xizz.scoreoflife.object.Event;
import com.xizz.scoreoflife.object.EventCheck;

public class DataSource {

	private SQLiteOpenHelper mOpenHelper;
	private SQLiteDatabase mDatabase;

	private static DataSource sDataSource;

	public static DataSource getDataSource(Context context) {
		if (sDataSource == null) {
			sDataSource = new DataSource(context.getApplicationContext());
		}
		return sDataSource;
	}

	private DataSource(Context context) {
		mOpenHelper = new DBOpenHelper(context);
	}

	public void open() throws SQLException {
		mDatabase = mOpenHelper.getWritableDatabase();
	}

	public void close() {
		mOpenHelper.close();
	}

	public void insertEvent(Event event) {
		ContentValues values = new ContentValues();
		values.put(DBOpenHelper.NAME, event.name);
		values.put(DBOpenHelper.SCORE, event.score);
		values.put(DBOpenHelper.START_DATE, event.startDate);
		values.put(DBOpenHelper.ORDER_INDEX, event.index);
		event.id = mDatabase.insert(DBOpenHelper.TABLE_EVENTS, null, values);
		orderEventIndex();
	}

	public void insertCheck(EventCheck check) {
		ContentValues values = new ContentValues();
		values.put(DBOpenHelper.DATE, check.date);
		values.put(DBOpenHelper.IS_DONE, check.isDone);
		values.put(DBOpenHelper.EVENT_ID, check.eventId);

		mDatabase.insert(DBOpenHelper.TABLE_CHECKS, null, values);
	}

	public void deleteEvent(Event event) {
		String[] ids = { Long.toString(event.id) };
		int result = mDatabase.delete(DBOpenHelper.TABLE_CHECKS,
				DBOpenHelper.EVENT_ID + "=?", ids);
		Log.d("xi", Integer.toString(result));
		result = mDatabase.delete(DBOpenHelper.TABLE_EVENTS, DBOpenHelper.NAME
				+ "=?", new String[] { event.name });
		Log.d("xi", Integer.toString(result));
		orderEventIndex();
	}

	public void updateEvent(Event event) {
		ContentValues values = new ContentValues();
		values.put(DBOpenHelper.ID, event.id);
		values.put(DBOpenHelper.NAME, event.name);
		values.put(DBOpenHelper.SCORE, event.score);
		values.put(DBOpenHelper.START_DATE, event.startDate);
		values.put(DBOpenHelper.ORDER_INDEX, event.index);
		String[] whereArgs = { Long.toString(event.id) };

		mDatabase.update(DBOpenHelper.TABLE_EVENTS, values, DBOpenHelper.ID
				+ "=?", whereArgs);
	}

	public void updateCheck(EventCheck check) {
		ContentValues values = new ContentValues();
		values.put(DBOpenHelper.DATE, check.date);
		values.put(DBOpenHelper.IS_DONE, check.isDone);
		values.put(DBOpenHelper.EVENT_ID, check.eventId);
		String[] whereArgs = { Long.toString(check.date),
				Long.toString(check.eventId) };

		mDatabase.update(DBOpenHelper.TABLE_CHECKS, values, DBOpenHelper.DATE
				+ "=? AND " + DBOpenHelper.EVENT_ID + "=?", whereArgs);
	}

	public void deleteCheck(long eventId) {
		String[] whereArgs = { Long.toString(eventId) };
		mDatabase.delete(DBOpenHelper.TABLE_CHECKS, DBOpenHelper.EVENT_ID
				+ "=?", whereArgs);
	}

	public void deleteChecks(long beforeThisDate) {
		String[] whereArgs = { Long.toString(beforeThisDate) };
		mDatabase.delete(DBOpenHelper.TABLE_CHECKS, DBOpenHelper.DATE + "<=?",
				whereArgs);
	}

	public List<Event> getAllEvents() {
		List<Event> events = new ArrayList<Event>();
		Cursor cursor = mDatabase.query(DBOpenHelper.TABLE_EVENTS, null, null,
				null, null, null, DBOpenHelper.ORDER_INDEX);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			events.add(cursorToEvent(cursor));
			cursor.moveToNext();
		}
		cursor.close();
		return events;
	}

	// Need join query
	// public List<EventCheck> getAllChecks() {
	// List<EventCheck> checks = new ArrayList<EventCheck>();
	// Cursor cursor = mDatabase.query(DBOpenHelper.TABLE_CHECKS, null, null,
	// null, null, null, null);
	// cursor.moveToFirst();
	// while (!cursor.isAfterLast()) {
	// checks.add(cursorToCheck(cursor));
	// cursor.moveToNext();
	// }
	// cursor.close();
	// return checks;
	// }

	public List<EventCheck> getChecks(long startDate, long endDate) {
		List<EventCheck> checks = new ArrayList<EventCheck>();
		String[] dates = { Long.toString(startDate), Long.toString(endDate) };

		final String query = "SELECT * FROM event_checks a INNER JOIN events b "
				+ "ON a.event_id=b._id WHERE a.date BETWEEN ? AND ? "
				+ "ORDER BY b.order_index";
		Cursor cursor = mDatabase.rawQuery(query, dates);

		// Cursor cursor = mDatabase
		// .query(DBOpenHelper.TABLE_CHECKS, null, DBOpenHelper.DATE
		// + " BETWEEN ? AND ?", dates, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			checks.add(cursorToCheck(cursor));
			cursor.moveToNext();
		}
		cursor.close();
		return checks;
	}

	private void orderEventIndex() {
		List<Event> events = getAllEvents();
		for (int i = 0; i < events.size(); ++i) {
			Event e = events.get(i);
			e.index = i + 1;
			updateEvent(e);
		}
	}

	private Event cursorToEvent(Cursor c) {
		Event event = new Event();

		event.id = c.getLong(c.getColumnIndex(DBOpenHelper.ID));
		event.name = c.getString(c.getColumnIndex(DBOpenHelper.NAME));
		event.score = c.getInt(c.getColumnIndex(DBOpenHelper.SCORE));
		event.startDate = c.getLong(c.getColumnIndex(DBOpenHelper.START_DATE));
		event.index = c.getInt(c.getColumnIndex(DBOpenHelper.ORDER_INDEX));

		return event;
	}

	private EventCheck cursorToCheck(Cursor c) {
		EventCheck check = new EventCheck();

		check.eventId = c.getLong(c.getColumnIndex(DBOpenHelper.EVENT_ID));
		check.date = c.getLong(c.getColumnIndex(DBOpenHelper.DATE));
		check.isDone = c.getInt(c.getColumnIndex(DBOpenHelper.IS_DONE)) > 0;

		return check;
	}

}