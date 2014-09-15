package se206.a03;

import javax.swing.SwingWorker;

public class SkipWorker extends SwingWorker<Void, Void> {
	private Playback mode;
	
	public SkipWorker(Playback mode) {
		this.mode = mode;
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		MediaPanel mediaPanel = MediaPanel.getInstance();
		MediaSetting mediaSetting = MediaSetting.getInstance();
		
		while(!isCancelled()) {
			switch(mode) {
				case FASTFORWARD:
					mediaPanel.mediaSkipTime(mediaSetting.getSkipTime());
					break;
				case REWIND:
					mediaPanel.mediaSkipTime(-mediaSetting.getSkipTime());
					break;
				default:
					break;
			}
			Thread.sleep(725); // arbitrary value. tested and thought this value is good.
		}
		return null;
	}
	
}