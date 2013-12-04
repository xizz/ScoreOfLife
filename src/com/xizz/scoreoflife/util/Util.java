package com.xizz.scoreoflife.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.xizz.scoreoflife.object.Event;
import com.xizz.scoreoflife.object.EventCheck;

public class Util {

	public final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd", Locale.ENGLISH);
	public final static long ONEDAY = 86400000;

	public final static String NEWLINE = System.getProperty("line.separator");
	
	public final static String NAME = "name";
	public final static String SCORE = "score";
	public final static String DATE = "date";
	public final static String ID = "id";
	

	public final static int REQUEST_ADD = 111;
	public final static int REQUEST_EDIT = 222;
	
	public static void removeLegacyChecks(List<Event> events,
			List<EventCheck> checks) {
		List<EventCheck> removeList = new ArrayList<EventCheck>();
		for (Event e : events) {
			for (EventCheck c : checks) {
				if (c.eventId == e.id && c.date < e.startDate) {
					removeList.add(c);
				}
			}
		}
		for (EventCheck c : removeList) {
			checks.remove(c);
		}
	}

	public static void linkEventChecks(List<Event> events,
			List<EventCheck> checks) {
		for (Event e : events) {
			for (EventCheck c : checks) {
				if (c.eventId == e.id) {
					c.event = e;
				}
			}
		}
	}

	public static long getToday() {
		try {
			return DATE_FORMAT.parse(DATE_FORMAT.format(new Date())).getTime();
		} catch (ParseException e) {
			return 0;
		}
	}

}
