package se206.a03;

import java.util.List;

import javax.swing.SwingWorker;

import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

/**
 * 
 * Skips the media player some time depending on the playback mode it is given. <br />
 * 
 * Also check {@link se206.a03.Playback }
 *
 */

public class SkipWorker extends SwingWorker<Void, Integer> {
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
			publish((int)mediaPlayer.getTime());
			Thread.sleep(725); // arbitrary value. tested and thought this value is good.
		}
		return null;
	}

	@Override
	protected void process(List<Integer> chunks) {
		for (Integer i : chunks) {
			MediaPanel.getInstance().timeSlider.setValue(i);
		}
	}	
	
	
	
}