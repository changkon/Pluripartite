package se206.a03;

import javax.swing.SwingWorker;

import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class SkipWorker extends SwingWorker<Void, Void> {
	private Playback mode;
	
	public SkipWorker(Playback mode) {
		this.mode = mode;
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		EmbeddedMediaPlayer mediaPlayer = MediaPanel.getInstance().getMediaPlayer();
		MediaSetting mediaSetting = MediaSetting.getInstance();
		
		while(!isCancelled()) {
			switch(mode) {
				case FASTFORWARD:
					mediaPlayer.skip(mediaSetting.getSkipTime());
					break;
				case REWIND:
					mediaPlayer.skip(-mediaSetting.getSkipTime());
					break;
				default:
					break;
			}
			Thread.sleep(725); // arbitrary value. tested and thought this value is good.
		}
		return null;
	}
	
}