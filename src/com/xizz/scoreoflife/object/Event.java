package com.xizz.scoreoflife.object;

import java.sql.Date;

public class Event {
	private String NEWLINE = System.getProperty("line.separator");
	public long id;
	public String name;
	public int score;
	public long startDate;
	public int index;

	public Event() {
		name = "";
		score = 0;
		startDate = 0;
		index = 0;
	}

	public Event(String n, int s, long d, int i) {
		name = n;
		score = s;
		startDate = d;
		index = i;
	}

	@Override
	public String toString() {
		return name + NEWLINE + "Score: " + score + NEWLINE + "Start Date: "
				+ new Date(startDate);
	}
}
