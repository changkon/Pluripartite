package se206.a03;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.BoundedRangeModel;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.miginfocom.swing.MigLayout;
import uk.co.caprica.vlcj.binding.internal.libvlc_audio_output_channel_t;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

@SuppressWarnings("serial")
public class MediaPanel extends JPanel implements ActionListener, ChangeListener {
	private static MediaPanel theInstance = null;
	
	public static final String initialTimeDisplay = "--:--";
	
	private JPanel playbackPanel = new JPanel(new MigLayout());
	private JPanel timePanel = new JPanel(new MigLayout());
	private JPanel buttonPanel = new JPanel(new MigLayout());
	
	public JButton playButton = new JButton(MediaIcon.getIcon(Playback.PLAY));
	public JButton stopButton = new JButton(MediaIcon.getIcon(Playback.STOP));
	public JButton fastforwardButton = new JButton(MediaIcon.getIcon(Playback.FASTFORWARD));
	public JButton rewindButton = new JButton(MediaIcon.getIcon(Playback.REWIND));
	public JButton muteButton = new JButton(MediaIcon.getIcon(Playback.UNMUTE));
	public JButton maxVolumeButton = new JButton(MediaIcon.getIcon(Playback.MAXVOLUME));
	public JButton openButton = new JButton(MediaIcon.getIcon(Playback.OPEN));
	
	public JLabel startTimeLabel = new JLabel(initialTimeDisplay); // Initial labels
	public JLabel finishTimeLabel = new JLabel(initialTimeDisplay);
	
	public JSlider timeSlider = new JSlider(new TimeBoundedRangeModel());
	
	private final static int minVolume = 0;
	private final static int maxVolume = 200; // The max volume of VLC
	
	public JSlider volumeSlider = new JSlider(JSlider.HORIZONTAL, minVolume, maxVolume, 100); // 100 is arbitrary value.
	
	private EmbeddedMediaPlayerComponent mediaPlayerComponent;
	private EmbeddedMediaPlayer mediaPlayer;
	
	private SkipWorker skipWorker = new SkipWorker(Playback.FASTFORWARD); // arbitrary. value doesn't matter.
	
	public static MediaPanel getInstance() {
		if (theInstance == null) {
			theInstance = new MediaPanel();
		}
		return theInstance;
	}
	
	private MediaPanel() {
		setLayout(new BorderLayout());
		
		mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
		mediaPlayer = mediaPlayerComponent.getMediaPlayer();
		
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
		
		addListeners();
	}
	
	/*
	 * Places buttons onto the button panel. Adds change listeners.
	 */
	private void setButtonPanel() {
		playButton.setToolTipText("Play/Pause media file");
		playButton.setBorderPainted(false);
		playButton.setFocusPainted(false);
		playButton.setContentAreaFilled(false);

		buttonPanel.add(playButton);
		
		stopButton.setToolTipText("Stop media");
		stopButton.setBorderPainted(false);
		stopButton.setFocusPainted(false);
		stopButton.setContentAreaFilled(false);

		buttonPanel.add(stopButton);
		
		rewindButton.setToolTipText("Rewind some time");
		rewindButton.setBorderPainted(false);
		rewindButton.setFocusPainted(false);
		rewindButton.setContentAreaFilled(false);

		buttonPanel.add(rewindButton, "gapleft 15");
		
		fastforwardButton.setToolTipText("Fast forward some time");
		fastforwardButton.setBorderPainted(false);
		fastforwardButton.setFocusPainted(false);
		fastforwardButton.setContentAreaFilled(false);

		buttonPanel.add(fastforwardButton);
		
		muteButton.setToolTipText("Mute/unmute media file");
		muteButton.setBorderPainted(false);
		muteButton.setFocusPainted(false);
		muteButton.setContentAreaFilled(false);

		buttonPanel.add(muteButton, "gapleft 15");
		
		volumeSlider.setToolTipText("Adjust Volume");
		buttonPanel.add(volumeSlider);
		
		maxVolumeButton.setToolTipText("Set to max volume");
		maxVolumeButton.setBorderPainted(false);
		maxVolumeButton.setFocusPainted(false);
		maxVolumeButton.setContentAreaFilled(false);

		buttonPanel.add(maxVolumeButton);
		
		openButton.setToolTipText("Open media file");
		openButton.setBorderPainted(false);
		openButton.setFocusPainted(false);
		openButton.setContentAreaFilled(false);

		buttonPanel.add(openButton, "push, align right");
	}
	
	private void addListeners() {
		// Add button listeners
		playButton.addActionListener(this);
		stopButton.addActionListener(this);
		rewindButton.addActionListener(this);
		fastforwardButton.addActionListener(this);
		muteButton.addActionListener(this);
		maxVolumeButton.addActionListener(this);
		openButton.addActionListener(this);
		
		// Volume slider change listeners
		volumeSlider.addChangeListener(this);
		timeSlider.addChangeListener(this);
		
		// Add change listeners.
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
		
		// video surface responds when double click.
		mediaPlayerComponent.getVideoSurface().addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// Double click.
				if (e.getClickCount() == 2) {
					// Gets what "state" the JFrame is in. State is if the screen is maximised or not etc.
					Component component = SwingUtilities.getWindowAncestor(MediaPanel.getInstance());
					if (component != null && component instanceof JFrame) {
						JFrame jframe = (JFrame)component;
						
						int state = jframe.getExtendedState();
						switch(state) {
							case JFrame.MAXIMIZED_BOTH:
								jframe.setExtendedState(JFrame.NORMAL);
								break;
							default:
								jframe.setExtendedState(JFrame.MAXIMIZED_BOTH);
								break;
						}
					}
				}
			}
		});
		
		mediaPlayer.addMediaPlayerEventListener(new MediaPlayerListener(this));
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == playButton) {
			switch(skipWorker.getState()) {
				case STARTED:
					skipWorker.cancel(true);
					break;
				default:
					break;
			}
			
			// Pause if current media is playing or play if it can be played and is currently paused.
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.pause();
			} else {
				mediaPlayer.play();
			}
		} else if (e.getSource() == stopButton) {
			mediaPlayer.stop();
		} else if (e.getSource() == fastforwardButton) {
			// time in milliseconds
			fastforwardButton.setSelected(true);
			
			switch(skipWorker.getState()) {
				case PENDING:
					skipWorker = new SkipWorker(Playback.FASTFORWARD);
					skipWorker.execute();
					break;
				default:
					skipWorker.cancel(true);
					skipWorker = new SkipWorker(Playback.FASTFORWARD);
					skipWorker.execute();
					break;
			}
			

		} else if (e.getSource() == rewindButton) {
			// time in milliseconds
			rewindButton.setSelected(true);
			
			switch(skipWorker.getState()) {
				case PENDING:
					skipWorker = new SkipWorker(Playback.REWIND);
					skipWorker.execute();
					break;
				default:
					skipWorker.cancel(true);
					skipWorker = new SkipWorker(Playback.REWIND);
					skipWorker.execute();
					break;
			}
		} else if (e.getSource() == muteButton) {
			
			// Toggles mute. Cannot update in event listener because mute doesn't toggle event listener.
			if (mediaPlayer.isMute()) {
				muteButton.setIcon(MediaIcon.getIcon(Playback.UNMUTE));
				mediaPlayer.mute(false);
			} else {
				muteButton.setIcon(MediaIcon.getIcon(Playback.MUTE));
				mediaPlayer.mute(true);
			}
			
		} else if (e.getSource() == maxVolumeButton) {
			// 200 is the max volume setting.
			mediaPlayer.setVolume(200);
		} else if (e.getSource() == openButton) {
			playFile();
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
		} else if (e.getSource() == timeSlider  && ((TimeBoundedRangeModel)timeSlider.getModel()).getActive()) {
			if(!source.getValueIsAdjusting()){
				System.out.println("received");
				int time = source.getValue();
				mediaPlayer.setTime(time);
			}
		}
	}
	
	public void playFile() {
		JFileChooser chooser = new JFileChooser();
		int selection = chooser.showOpenDialog(null);
		
		if (selection == JFileChooser.APPROVE_OPTION) {
			File selectedFile = chooser.getSelectedFile();
			mediaPlayer.playMedia(selectedFile.getPath());
		}
	}
	
	public EmbeddedMediaPlayer getMediaPlayer() {
		return mediaPlayer;
	}
	
	public void extractAudio() throws IOException {
		if (mediaPlayer.isPlayable()) {
			String inputFilename = MRLFilename.getFilename(mediaPlayer.mrl());
			
			if (inputFilename == null) {
				JOptionPane.showMessageDialog(null, "Incorrect file directory");
				return;
			}
			
			String type = Files.probeContentType(Paths.get(inputFilename));
			
			if (type.contains("video")) {

				if (mediaPlayer.getAudioTrackCount() == 0) {
					JOptionPane.showMessageDialog(null, "No audio track exists in video");
					return;
				}
				
				JFileChooser chooser = new JFileChooser();
				
				// Removes the accept all filter.
				chooser.setAcceptAllFileFilterUsed(false);
				// Adds mp3 as filter.
				chooser.addChoosableFileFilter(new FileNameExtensionFilter("MPEG/mp3", "mp3"));
				
				int selection = chooser.showSaveDialog(this);
				
				if (selection == JFileChooser.APPROVE_OPTION) {
					File saveFile = chooser.getSelectedFile();
					
					String extensionType = chooser.getFileFilter().getDescription();
					String outputFilename = saveFile.getPath();
					
					/*
					 * Even though the extension type is listed below, sometimes users still add .mp3 to the end of the file so this
					 * makes sure that when I add extension type to the filename, I only add .mp3 if it's not already there.
					 * 
					 * At the moment, I only have .mp3 has possible extension.
					 */
					
					if (extensionType.contains("MPEG/mp3") && !saveFile.getPath().contains(".mp3")) {
						outputFilename = outputFilename + ".mp3";
					}
					
					// Checks to see if the filename the user wants to save already exists so it asks if it wants to overwrite or not.
					if (Files.exists(Paths.get(outputFilename))) {
						int overwriteSelection = JOptionPane.showConfirmDialog(null, "File already exists, do you want to overwrite?",
								"Select an option", JOptionPane.YES_NO_OPTION);
						
						// Overwrite if yes.
						if (overwriteSelection == JOptionPane.OK_OPTION) {
							executeExtract(inputFilename, outputFilename);
						}
					} else {
						executeExtract(inputFilename, outputFilename);
					}
				}
			} else {
				JOptionPane.showMessageDialog(null, "Media is not video file");
			}
		} else {
			JOptionPane.showMessageDialog(null, "No media recognized");
		}
	}
	
	private void executeExtract(String inputFilename, String outputFilename) {
		int lengthOfVideo = (int)(mediaPlayer.getLength() / 1000);
		
		ProgressMonitor monitor = new ProgressMonitor(null, "Extraction has started", "", 0, lengthOfVideo);
		
		ExtractAudioWorker worker = new ExtractAudioWorker(inputFilename, outputFilename, lengthOfVideo, monitor);
		worker.execute();
	}
}