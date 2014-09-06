package se206.a03;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;
import se206.a03.MediaIcon.DisplayIcon;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

/**
 * 
 * Currently hard coded but will update. Download Full HD (1920x1080) 60fps http link by right click save as.
 * 
 * @see http://bbb3d.renderfarming.net/download.html
 *
 */

@SuppressWarnings("serial")
public class MediaFrame extends JFrame implements ActionListener {
	private JPanel mediaPanel = new JPanel(new BorderLayout());
	private JPanel buttonPanel = new JPanel(new MigLayout());
	
	private JButton playButton = new JButton(MediaIcon.getIcon(DisplayIcon.PLAY));
	private JButton stopButton = new JButton(MediaIcon.getIcon(DisplayIcon.STOP));
	private JButton fastforwardButton = new JButton(MediaIcon.getIcon(DisplayIcon.FASTFORWARD));
	private JButton rewindButton = new JButton(MediaIcon.getIcon(DisplayIcon.REWIND));
	private JButton muteButton = new JButton(MediaIcon.getIcon(DisplayIcon.UNMUTE));
	private JButton toggleVolumeButton = new JButton(MediaIcon.getIcon(DisplayIcon.TOGGLEVOLUME));
	private JButton maxVolumeButton = new JButton(MediaIcon.getIcon(DisplayIcon.MAXVOLUME));
	private JButton openButton = new JButton(MediaIcon.getIcon(DisplayIcon.OPEN));
	
	private EmbeddedMediaPlayerComponent mediaPlayerComponent;
	private EmbeddedMediaPlayer mediaPlayer;
	
	private MediaSetting mediaSetting = MediaSetting.getInstance();
	
	public MediaFrame() {
		super("VAMIX");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(new Dimension(800, 500));
		setLayout(new BorderLayout());
		
		mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
		mediaPlayer = mediaPlayerComponent.getMediaPlayer();
		
		mediaPanel.add(mediaPlayerComponent);
		
		setButtonPanel();
		
		add(mediaPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);

		// Add button listeners
		playButton.addActionListener(this);
		stopButton.addActionListener(this);
		rewindButton.addActionListener(this);
		fastforwardButton.addActionListener(this);
		muteButton.addActionListener(this);
		toggleVolumeButton.addActionListener(this);
		maxVolumeButton.addActionListener(this);
		openButton.addActionListener(this);
		
		// Should be mediaPanel but it isn't working for me.
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// Double click.
				if (e.getClickCount() == 2) {
					int state = getExtendedState();
					switch(state) {
						case JFrame.MAXIMIZED_BOTH:
							setExtendedState(JFrame.NORMAL);
							break;
						default:
							setExtendedState(JFrame.MAXIMIZED_BOTH);
							break;
					}
				}
			}
		});
		
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				mediaPlayer.release();
			}
			
		});
	}
	
	// Places buttons on buttonPanel and sets its tooltip.
	private void setButtonPanel() {
		playButton.setToolTipText("Play/Pause media file");
		buttonPanel.add(playButton);
		
		stopButton.setToolTipText("Stop media");
		buttonPanel.add(stopButton);
		
		rewindButton.setToolTipText("Rewind some time");
		buttonPanel.add(rewindButton, "gapleft 15");
		
		fastforwardButton.setToolTipText("Fast forward some time");
		buttonPanel.add(fastforwardButton);
		
		muteButton.setToolTipText("Mute/unmute media file");
		buttonPanel.add(muteButton, "gapleft 15");
		
		toggleVolumeButton.setToolTipText("Toggle volume");
		buttonPanel.add(toggleVolumeButton);
		
		maxVolumeButton.setToolTipText("Set to max volume");
		buttonPanel.add(maxVolumeButton);
		
		openButton.setToolTipText("Open media file");
		buttonPanel.add(openButton, "push, align right");
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == playButton) {
			if (mediaPlayer.isPlaying()) {
				playButton.setIcon(MediaIcon.getIcon(DisplayIcon.PLAY));
			} else {
				playButton.setIcon(MediaIcon.getIcon(DisplayIcon.PAUSE));
			}
			// Automatically pauses and plays when pressed.
			mediaPlayer.pause();
		} else if (e.getSource() == stopButton) {
			mediaPlayer.stop();
			playButton.setIcon(MediaIcon.getIcon(DisplayIcon.PLAY));
		} else if (e.getSource() == fastforwardButton) {
			// time in milliseconds
			mediaPlayer.skip(mediaSetting.getSkipTime());
		} else if (e.getSource() == rewindButton) {
			// time in milliseconds
			mediaPlayer.skip(-mediaSetting.getSkipTime());
		} else if (e.getSource() == muteButton) {
			if (mediaPlayer.isMute()) {
				muteButton.setIcon(MediaIcon.getIcon(DisplayIcon.UNMUTE));
			} else {
				muteButton.setIcon(MediaIcon.getIcon(DisplayIcon.MUTE));
			}
			// Toggles mute
			mediaPlayer.mute();
		} else if (e.getSource() == toggleVolumeButton){
			System.out.println(mediaPlayer.getVolume());
		} else if (e.getSource() == maxVolumeButton) {
			// 200 is the max volume setting.
			mediaPlayer.setVolume(200);
		} else if (e.getSource() == openButton) {
			playFile();
		}
	}
	
	private void playFile() {
		JFileChooser chooser = new JFileChooser();
		int selection = chooser.showOpenDialog(null);
		String inputFilename = "";
		
		if (selection == JFileChooser.APPROVE_OPTION) {
			File selectedFile = chooser.getSelectedFile();
			inputFilename = selectedFile.getPath();

			mediaPlayer.playMedia(inputFilename);
			setInitialSettings();
			playButton.setIcon(MediaIcon.getIcon(DisplayIcon.PAUSE));
		}
	}
	
	private void setInitialSettings() {
		mediaPlayer.setVolume(mediaSetting.getInitialVolume());
		mediaPlayer.mute(mediaSetting.getStartMuted());
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				MediaFrame mediaFrame = new MediaFrame();
				mediaFrame.setVisible(true);
			}
			
		});
	}
}
