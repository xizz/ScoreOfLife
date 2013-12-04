package com.xizz.scoreoflife.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {

	public final static String TABLE_EVENTS = "events";
	public final static String TABLE_CHECKS = "event_checks";

	public final static String ID = "_id";
	public final static String NAME = "name";
	public final static String SCORE = "score";
	public final static String START_DATA = "start_date";

	public final static String IS_DONE = "is_done";
	public final static String EVENT_ID = "event_id";
	public final static String DATE = "date";

	public DBOpenHelper(Context context) {
		super(context, "scoremylife.db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + TABLE_EVENTS + "(" + ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + NAME + " TEXT, "
				+ SCORE + " INTEGER, " + START_DATA + " INTEGER)");
		db.execSQL("CREATE TABLE " + TABLE_CHECKS + "(" + DATE + " INTEGER, "
				+ IS_DONE + " INTEGER, " + EVENT_ID + " INTEGER, FOREIGN KEY("
				+ EVENT_ID + ") REFERENCES " + TABLE_EVENTS + "(" + ID + "))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHECKS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
		onCreate(db);
	}
}
