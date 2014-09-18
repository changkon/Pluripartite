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

import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;

import org.java.ayatana.ApplicationMenu;
import org.java.ayatana.AyatanaDesktop;

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
public class MediaFrame extends JFrame implements ActionListener, ChangeListener {
	private MediaSetting mediaSetting = MediaSetting.getInstance();

	public static final String initialTimeDisplay = "--:--";
	
	private JPanel mediaPanel = new JPanel(new BorderLayout());
	private JPanel playbackPanel = new JPanel(new MigLayout());
	private JPanel timePanel = new JPanel(new MigLayout());
	private JPanel buttonPanel = new JPanel(new MigLayout());
	
	private JMenuBar menuBar = new JMenuBar();
	private JMenu mediaMenu = new JMenu("Media");
	private JMenuItem openMenuItem = new JMenuItem("Open..");
	private JMenuItem blah = new JMenuItem("BLAH");
	
	public JButton playButton = new JButton(MediaIcon.getIcon(DisplayIcon.PLAY));
	public JButton stopButton = new JButton(MediaIcon.getIcon(DisplayIcon.STOP));
	public JButton fastforwardButton = new JButton(MediaIcon.getIcon(DisplayIcon.FASTFORWARD));
	public JButton rewindButton = new JButton(MediaIcon.getIcon(DisplayIcon.REWIND));
	public JButton muteButton = new JButton(MediaIcon.getIcon(DisplayIcon.UNMUTE));
	public JButton maxVolumeButton = new JButton(MediaIcon.getIcon(DisplayIcon.MAXVOLUME));
	public JButton openButton = new JButton(MediaIcon.getIcon(DisplayIcon.OPEN));
	
	public JLabel startTimeLabel = new JLabel(initialTimeDisplay); // Initial labels
	public JLabel finishTimeLabel = new JLabel(initialTimeDisplay);
	
	public JSlider timeSlider = new JSlider();
	
	private final static int minVolume = 0;
	private final static int maxVolume = 200; // The max volume of VLC
	
	public JSlider volumeSlider = new JSlider(JSlider.HORIZONTAL, minVolume, maxVolume, 100); // 100 is arbitrary value.
	
	public Timer t;
	
	private EmbeddedMediaPlayerComponent mediaPlayerComponent;
	private EmbeddedMediaPlayer mediaPlayer;
	
	public MediaFrame() {
		super("VAMIX");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(new Dimension(800, 500));
		setLayout(new BorderLayout());
		
		mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
		
		mediaPlayer = mediaPlayerComponent.getMediaPlayer();
		
		mediaPanel.add(mediaPlayerComponent);
		
		// Initially set value to 0
		timeSlider.setValue(0);
		
		timePanel.add(startTimeLabel);
		timePanel.add(timeSlider, "pushx, growx");
		timePanel.add(finishTimeLabel);
		setButtonPanel();
		
		playbackPanel.add(timePanel, "north, pushx, growx, wrap 0px");
		playbackPanel.add(buttonPanel, "pushx, growx");
		
		add(mediaPlayerComponent, BorderLayout.CENTER);
		add(playbackPanel, BorderLayout.SOUTH);

		// Add button listeners
		playButton.addActionListener(this);
		stopButton.addActionListener(this);
		rewindButton.addActionListener(this);
		fastforwardButton.addActionListener(this);
		muteButton.addActionListener(this);
		
		volumeSlider.addChangeListener(this);
		timeSlider.addChangeListener(this);
		
		maxVolumeButton.addActionListener(this);
		openButton.addActionListener(this);
		
		// Should be mediaPanel but it isn't working for me.
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// Double click.
				if (e.getClickCount() == 2) {
					// Gets what "state" the JFrame is in. State is if the screen is maximised or not etc.
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
		
		// Makes sure when window closes, it releases the mediaPlayer.
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				mediaPlayer.release();
			}
			
		});
		
		mediaPlayer.addMediaPlayerEventListener(new MediaPlayerListener(this));
		
		// Initialise timer. Update nearly every third a second.
		t = new Timer(300, this);
		
		setJMenuBar(menuBar);
		menuBar.add(mediaMenu);
		mediaMenu.add(openMenuItem);
//		mediaMenu.add(blah);
	}
	
	/*
	 * Places buttons onto the button panel. Adds change listeners.
	 */
	private void setButtonPanel() {
		playButton.setToolTipText("Play/Pause media file");
		playButton.setBorderPainted(false);
		playButton.setFocusPainted(false);
		playButton.setContentAreaFilled(false);
		playButton.getModel().addChangeListener(new ChangeListener() {
	        @Override
	        public void stateChanged(ChangeEvent e) {
	            ButtonModel model = (ButtonModel) e.getSource();
	            if (model.isRollover()) {
	            	playButton.setBorderPainted(true);
	            } else {
	            	playButton.setBorderPainted(false);
	            }
	        }
	    });
		buttonPanel.add(playButton);
		
		stopButton.setToolTipText("Stop media");
		stopButton.setBorderPainted(false);
		stopButton.setFocusPainted(false);
		stopButton.setContentAreaFilled(false);
		stopButton.getModel().addChangeListener(new ChangeListener() {
	        @Override
	        public void stateChanged(ChangeEvent e) {
	            ButtonModel model = (ButtonModel) e.getSource();
	            if (model.isRollover()) {
	            	stopButton.setBorderPainted(true);
	            } else {
	            	stopButton.setBorderPainted(false);
	            }
	        }
	    });
		buttonPanel.add(stopButton);
		
		rewindButton.setToolTipText("Rewind some time");
		rewindButton.setBorderPainted(false);
		rewindButton.setFocusPainted(false);
		rewindButton.setContentAreaFilled(false);
		rewindButton.getModel().addChangeListener(new ChangeListener() {
	        @Override
	        public void stateChanged(ChangeEvent e) {
	            ButtonModel model = (ButtonModel) e.getSource();
	            if (model.isRollover()) {
	            	rewindButton.setBorderPainted(true);
	            } else {
	            	rewindButton.setBorderPainted(false);
	            }
	        }
	    });
		buttonPanel.add(rewindButton, "gapleft 15");
		
		fastforwardButton.setToolTipText("Fast forward some time");
		fastforwardButton.setBorderPainted(false);
		fastforwardButton.setFocusPainted(false);
		fastforwardButton.setContentAreaFilled(false);
		fastforwardButton.getModel().addChangeListener(new ChangeListener() {
	        @Override
	        public void stateChanged(ChangeEvent e) {
	            ButtonModel model = (ButtonModel) e.getSource();
	            if (model.isRollover()) {
	            	fastforwardButton.setBorderPainted(true);
	            } else {
	            	fastforwardButton.setBorderPainted(false);
	            }
	        }
	    });
		buttonPanel.add(fastforwardButton);
		
		muteButton.setToolTipText("Mute/unmute media file");
		muteButton.setBorderPainted(false);
		muteButton.setFocusPainted(false);
		muteButton.setContentAreaFilled(false);
		muteButton.getModel().addChangeListener(new ChangeListener() {
	        @Override
	        public void stateChanged(ChangeEvent e) {
	            ButtonModel model = (ButtonModel) e.getSource();
	            if (model.isRollover()) {
	            	muteButton.setBorderPainted(true);
	            } else {
	            	muteButton.setBorderPainted(false);
	            }
	        }
	    });
		buttonPanel.add(muteButton, "gapleft 15");
		
		volumeSlider.setToolTipText("Adjust Volume");
		buttonPanel.add(volumeSlider);
		
		maxVolumeButton.setToolTipText("Set to max volume");
		maxVolumeButton.setBorderPainted(false);
		maxVolumeButton.setFocusPainted(false);
		maxVolumeButton.setContentAreaFilled(false);
		maxVolumeButton.getModel().addChangeListener(new ChangeListener() {
	        @Override
	        public void stateChanged(ChangeEvent e) {
	            ButtonModel model = (ButtonModel) e.getSource();
	            if (model.isRollover()) {
	            	maxVolumeButton.setBorderPainted(true);
	            } else {
	            	maxVolumeButton.setBorderPainted(false);
	            }
	        }
	    });
		buttonPanel.add(maxVolumeButton);
		
		openButton.setToolTipText("Open media file");
		openButton.setBorderPainted(false);
		openButton.setFocusPainted(false);
		openButton.setContentAreaFilled(false);
		openButton.getModel().addChangeListener(new ChangeListener() {
	        @Override
	        public void stateChanged(ChangeEvent e) {
	            ButtonModel model = (ButtonModel) e.getSource();
	            if (model.isRollover()) {
	            	openButton.setBorderPainted(true);
	            } else {
	            	openButton.setBorderPainted(false);
	            }
	        }
	    });
		buttonPanel.add(openButton, "push, align right");
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == playButton) {
			// Pause if current media is playing or play if it can be played and is currently paused.
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.pause();
			} else {
				mediaPlayer.play();
			}
		} else if (e.getSource() == stopButton) {
			// Need to edit.
			mediaPlayer.stop();
		} else if (e.getSource() == fastforwardButton) {
			// time in milliseconds
			fastforwardButton.setSelected(true);
			mediaPlayer.skip(mediaSetting.getSkipTime());
		} else if (e.getSource() == rewindButton) {
			// time in milliseconds
			rewindButton.setSelected(true);
			mediaPlayer.skip(-mediaSetting.getSkipTime());
		} else if (e.getSource() == muteButton) {
			
			// Toggles mute. Cannot update in event listener because mute doesn't toggle event listener.
			if (mediaPlayer.isMute()) {
				muteButton.setIcon(MediaIcon.getIcon(DisplayIcon.UNMUTE));
				mediaPlayer.mute(false);
			} else {
				muteButton.setIcon(MediaIcon.getIcon(DisplayIcon.MUTE));
				mediaPlayer.mute(true);
			}
			
		} else if (e.getSource() == maxVolumeButton) {
			// 200 is the max volume setting.
			mediaPlayer.setVolume(200);
		} else if (e.getSource() == openButton) {
			playFile();
		} else {
			// If it is in this branch, it must have been called from timer.
			startTimeLabel.setText(MediaTimer.getTime(mediaPlayer.getTime()));
		}
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		JSlider source = (JSlider)e.getSource();
		if (e.getSource() == volumeSlider) {
			if(!source.getValueIsAdjusting()){
				int volumeTemp = source.getValue();
				mediaPlayer.setVolume(volumeTemp);
			}
		} else if (e.getSource() == timeSlider) {
			if(!source.getValueIsAdjusting()){
				int time = source.getValue();
				mediaPlayer.setTime(time);
			}
		}
	}
	
	private void playFile() {
		JFileChooser chooser = new JFileChooser();
		int selection = chooser.showOpenDialog(null);
		
		if (selection == JFileChooser.APPROVE_OPTION) {
			File selectedFile = chooser.getSelectedFile();
			mediaPlayer.playMedia(selectedFile.getPath());
		}
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				MediaFrame mediaFrame = new MediaFrame();
				mediaFrame.setVisible(true);
				
				if (AyatanaDesktop.isSupported()) {
					ApplicationMenu.tryInstall(mediaFrame);
				}
			}
			
		});
	}
}
