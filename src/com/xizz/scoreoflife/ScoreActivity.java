package com.xizz.scoreoflife;

import java.text.MessageFormat;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.xizz.scoreoflife.db.DataSource;
import com.xizz.scoreoflife.object.Event;
import com.xizz.scoreoflife.object.EventCheck;
import com.xizz.scoreoflife.util.Util;

public class ScoreActivity extends Activity {

	private final static long TODAY = Util.getToday();
	private final static String NEWLINE = System.getProperty("line.separator");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_score);

		final DataSource data = DataSource.getDataSource(this);

		final List<EventCheck> checks = data.getChecks(TODAY - Util.ONEDAY * 30,
				System.currentTimeMillis());
		final List<Event> events = data.getAllEvents();
		Util.linkEventChecks(events, checks);

		// calculate the scores of week and month
		final int weekTotal = getTotalScore(events, 7);
		final int monthTotal = getTotalScore(events, 30);
		final int weekScore = getScore(checks, 7);
		final int monthScore = getScore(checks, 30);

		StringBuilder str = new StringBuilder();
		str.append("Past 7 days: " + weekScore + "/" + weekTotal + NEWLINE);
		str.append("Completion: "
				+ MessageFormat.format("{0,number,#.##%}", weekScore * 1.0
						/ weekTotal) + NEWLINE + NEWLINE);

		str.append("Past 30 days: " + monthScore + "/" + monthTotal + NEWLINE);
		str.append("Completion: "
				+ MessageFormat.format("{0,number,#.##%}", monthScore * 1.0
						/ monthTotal));

		TextView textView = (TextView) findViewById(R.id.scoreText);
		textView.setText(str.toString());
	}

	private int getTotalScore(List<Event> events, int days) {
		int total = 0;
		for (int i = 0; i < days; ++i) {
			for (Event e : events) {
				if (e.startDate <= (TODAY - Util.ONEDAY * i)) {
					total += e.score;
				}
			}
		}
		return total;
	}

	private static int getScore(List<EventCheck> checks, int days) {
		int score = 0;
		for (EventCheck c : checks) {
			// The event start date might be modified, so we should make sure
			// the check date is after the event start date
			if (c.date >= c.event.startDate && c.isDone
					&& c.date >= (TODAY - Util.ONEDAY * (days - 1))) {
				score += c.event.score;
			}
		}
		return score;
	}
}