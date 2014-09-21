package se206.a03;

/**
 * Contains the media settings for the VAMIX. Users can edit media settings through gui.
 * 
 */
public class MediaSetting {
	private static MediaSetting theInstance = null;
	private long skipTime;
	
	public static MediaSetting getInstance() {
		if (theInstance == null) {
			theInstance = new MediaSetting();
		}
		return theInstance;
	}
	
	private MediaSetting() {
		skipTime = 5000;
	}
	
	public long getSkipTime() {
		return skipTime;
	}
	
	public void setSkipTime(long time) {
		skipTime = time;
	}
}
