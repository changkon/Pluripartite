package se206.a03;

/**
 * Contains the media settings for the VAMIX. Users can edit media settings through gui.
 * 
 */
public class MediaSetting {
	private static MediaSetting theInstance = null;
	private int initialVolume;
	private long skipTime;
	private boolean startMuted;
	
	public static MediaSetting getInstance() {
		if (theInstance == null) {
			theInstance = new MediaSetting();
		}
		return theInstance;
	}
	
	private MediaSetting() {
		initialVolume = 100;
		skipTime = 10000;
		startMuted = false;
	}
	
	public int getInitialVolume() {
		return initialVolume;
	}
	
	public long getSkipTime() {
		return skipTime;
	}
	
	public void setInitialVolume(int volume) {
		initialVolume = volume;
	}
	
	public void setSkipTime(long time) {
		skipTime = time;
	}
	
	public boolean getStartMuted() {
		return startMuted;
	}
	
	public void setStartMuted(boolean mute) {
		startMuted = mute;
	}
}
