package se206.a03;

import java.lang.reflect.InvocationTargetException;
import javax.swing.SwingUtilities;
import se206.a03.MediaIcon.DisplayIcon;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;

/**
 * Updates media frame when media frame state has changed. Because this is not in the EDT, it must call on invokeAndWait
 * to get GUI to be updated thread safe.
 * 
 */

public class MediaPlayerListener extends MediaPlayerEventAdapter {
	private MediaFrame mediaFrame;
	
	public MediaPlayerListener(MediaFrame mediaFrame) {
		this.mediaFrame = mediaFrame;
	}
	
	// executed synchronously on the AWT event dispatching thread. Call is blocked until all processing AWT events have been
	// processed.
	
	@Override
	public void mediaParsedChanged(final MediaPlayer mediaPlayer, int newStatus) {
		super.mediaParsedChanged(mediaPlayer, newStatus);

		try {
			SwingUtilities.invokeAndWait(new Runnable() {

				@Override
				public void run() {
					mediaFrame.finishTimeLabel.setText(MediaTimer.getTime(mediaPlayer.getLength()));
					mediaFrame.timeSlider.setMinimum(0);
					mediaFrame.timeSlider.setMaximum((int)mediaPlayer.getLength());
					mediaFrame.startTimeLabel.setText(MediaTimer.getTime(mediaPlayer.getTime()));
				}
				
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void paused(MediaPlayer mediaPlayer) {
		super.paused(mediaPlayer);
		
		try {
			SwingUtilities.invokeAndWait(new Runnable() {

				@Override
				public void run() {
					mediaFrame.playButton.setIcon(MediaIcon.getIcon(DisplayIcon.PLAY));
					mediaFrame.t.stop();
				}
				
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void playing(MediaPlayer mediaPlayer) {
		super.playing(mediaPlayer);
		
		try {
			SwingUtilities.invokeAndWait(new Runnable() {

				@Override
				public void run() {
					mediaFrame.t.start();
					mediaFrame.playButton.setIcon(MediaIcon.getIcon(DisplayIcon.PAUSE));
				}
				
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stopped(MediaPlayer mediaPlayer) {
		super.stopped(mediaPlayer);
		// Prepares media to be played when play button is pressed.
		mediaPlayer.prepareMedia(mediaPlayer.mrl());
		try {
			SwingUtilities.invokeAndWait(new Runnable() {

				@Override
				public void run() {
					mediaFrame.t.stop();
					mediaFrame.playButton.setIcon(MediaIcon.getIcon(DisplayIcon.PLAY));
					mediaFrame.startTimeLabel.setText(MediaFrame.initialTimeDisplay);
					mediaFrame.finishTimeLabel.setText(MediaFrame.initialTimeDisplay);
				}
				
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void finished(MediaPlayer mediaPlayer) {
		super.finished(mediaPlayer);
		
		mediaPlayer.prepareMedia(mediaPlayer.mrl());
		try {
			SwingUtilities.invokeAndWait(new Runnable() {

				@Override
				public void run() {
					mediaFrame.t.stop();
					mediaFrame.playButton.setIcon(MediaIcon.getIcon(DisplayIcon.PLAY));
					mediaFrame.startTimeLabel.setText(MediaFrame.initialTimeDisplay);
					mediaFrame.finishTimeLabel.setText(MediaFrame.initialTimeDisplay);
				}
				
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
