package se206.a03;

import java.util.concurrent.TimeUnit;

public class MediaTimer {
	public static String getTime(long time) {
		int hours = (int)TimeUnit.MILLISECONDS.toHours(time) % 24;
		int minutes = (int)TimeUnit.MILLISECONDS.toMinutes(time) % 60;
		int seconds = (int)TimeUnit.MILLISECONDS.toSeconds(time) % 60;
		if (hours == 0) {
			return String.format("%02d:%02d", minutes, seconds);
		}
		return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	}
}
