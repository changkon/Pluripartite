package se206.a03;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;

/**
 * Updates media frame when media frame state has changed. Because this is not in the EDT, it must call on invokeAndWait
 * to get GUI to be updated thread safe.
 * 
 */

public class MediaPlayerListener extends MediaPlayerEventAdapter {
	private MediaPanel mediaPanel;
	
	public MediaPlayerListener(MediaPanel mediaPanel) {
		this.mediaPanel = mediaPanel;
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
					mediaPanel.finishTimeLabel.setText(MediaTimer.getTime(mediaPlayer.getLength()));
					mediaPanel.timeSlider.setMinimum(0);
					mediaPanel.timeSlider.setMaximum((int)mediaPlayer.getLength());
					mediaPanel.startTimeLabel.setText(MediaTimer.getTime(mediaPlayer.getTime()));
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
					mediaPanel.playButton.setIcon(MediaIcon.getIcon(Playback.PLAY));
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
					mediaPanel.playButton.setIcon(MediaIcon.getIcon(Playback.PAUSE));
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
					mediaPanel.playButton.setIcon(MediaIcon.getIcon(Playback.PLAY));
					mediaPanel.startTimeLabel.setText(MediaPanel.initialTimeDisplay);
					mediaPanel.finishTimeLabel.setText(MediaPanel.initialTimeDisplay);
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
					mediaPanel.playButton.setIcon(MediaIcon.getIcon(Playback.PLAY));
					mediaPanel.startTimeLabel.setText(MediaPanel.initialTimeDisplay);
					mediaPanel.finishTimeLabel.setText(MediaPanel.initialTimeDisplay);
				}
				
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void timeChanged(MediaPlayer mediaPlayer, final long newTime) {
		super.timeChanged(mediaPlayer, newTime);
		
		try {
			SwingUtilities.invokeAndWait(new Runnable() {

				@Override
				public void run() {
					mediaPanel.startTimeLabel.setText(MediaTimer.getTime(newTime));
					((TimeBoundedRangeModel)mediaPanel.timeSlider.getModel()).setActive(false);
					mediaPanel.timeSlider.setValue((int)newTime);
				}
				
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
