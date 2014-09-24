package se206.a03;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ProgressMonitor;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.miginfocom.swing.MigLayout;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

@SuppressWarnings("serial")
public class AudioPanel extends JPanel implements ActionListener {
	private static AudioPanel theInstance = null;
	private EmbeddedMediaPlayer mediaPlayer = MediaPanel.getInstance().getMediaPlayer();
	private TitledBorder title;

	private final String defaultAudioReplaceText = "No file chosen";

	private JPanel audioExtractionPanel = new JPanel(new MigLayout());
	private JPanel audioReplacePanel = new JPanel(new MigLayout());

	private JLabel extractionLabel = new JLabel("Extraction");

	private JLabel timeLabel = new JLabel("Please input times in hh:mm:ss");
	private JLabel startTimeLabel = new JLabel("Start Time:");
	private JTextField startTimeInput = new JTextField(10);
	private JLabel lengthLabel = new JLabel("Length Time:");
	private JTextField lengthInput = new JTextField(10);
	private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
	private JButton extractButton = new JButton("Extract");

	private JLabel orLabel = new JLabel("OR");

	private JButton extractFullButton = new JButton("Extract entire Video");

	private JLabel replaceAudioLabel = new JLabel("Replace Audio");

	private JButton selectAudioReplaceFileButton = new JButton("Choose File");
	private JLabel selectedAudioReplaceFileLabel = new JLabel(defaultAudioReplaceText);
	private JButton audioReplaceButton = new JButton("Replace");

	public static AudioPanel getInstance() {
		if (theInstance == null) {
			theInstance = new AudioPanel();
		}
		return theInstance;
	}

	private AudioPanel() {
		setLayout(new MigLayout());

		setPreferredSize(new Dimension(812, 300));
		title = BorderFactory.createTitledBorder("Audio");
		setBorder(title);

		setAudioExtractionPanel();
		setAudioReplacePanel();

		addListeners();

		add(audioExtractionPanel, "wrap, pushx, growx");
		add(audioReplacePanel, "wrap, pushx, growx");
	}

	private void setAudioExtractionPanel() {
		Font font = extractionLabel.getFont().deriveFont(Font.ITALIC + Font.BOLD, 16f); // Default is 12.

		extractionLabel.setFont(font);

		audioExtractionPanel.add(extractionLabel, "wrap");
		audioExtractionPanel.add(timeLabel, "wrap");
		audioExtractionPanel.add(startTimeLabel, "split 4");
		audioExtractionPanel.add(startTimeInput);
		audioExtractionPanel.add(lengthLabel);
		audioExtractionPanel.add(lengthInput);
		audioExtractionPanel.add(extractButton);

		audioExtractionPanel.add(orLabel, "gapleft 30");

		audioExtractionPanel.add(extractFullButton, "gapleft 30, wrap");
	}

	private void setAudioReplacePanel() {
		Font font = replaceAudioLabel.getFont().deriveFont(Font.ITALIC + Font.BOLD, 16f); // Default is 12.

		replaceAudioLabel.setFont(font);

		audioReplacePanel.add(replaceAudioLabel, "wrap");
		audioReplacePanel.add(selectAudioReplaceFileButton);
		audioReplacePanel.add(selectedAudioReplaceFileLabel, "wrap");
		audioReplacePanel.add(audioReplaceButton);
	}

	private void addListeners() {
		extractButton.addActionListener(this);
		extractFullButton.addActionListener(this);

		selectAudioReplaceFileButton.addActionListener(this);
		audioReplaceButton.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == extractButton) {
			try {
				if (validateTime(startTimeInput.getText()) && validateTime(lengthInput.getText()) && validateMedia()) {
					String outputFilename = getOutputFilename();

					if (outputFilename != null) {
						executeExtract(MRLFilename.getFilename(mediaPlayer.mrl()), outputFilename, startTimeInput.getText(), lengthInput.getText());
					}

				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else if (e.getSource() == extractFullButton) {
			try {
				if (validateMedia()) {
					String outputFilename = getOutputFilename();

					if (outputFilename != null) {

						String length = MediaTimer.getFormattedTime(mediaPlayer.getLength());

						// if length comes back in the format, mm:ss. Have to add 00: so that it is in correct format.
						if (length.length() == 5) {
							length = "00:" + length;
						}

						executeExtract(MRLFilename.getFilename(mediaPlayer.mrl()), outputFilename, "00:00:00", length);

					}
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else if (e.getSource() == selectAudioReplaceFileButton) {
			String filename = getInputFilename();

			if (filename != null) {
				selectedAudioReplaceFileLabel.setText(filename);
			}

		} else if (e.getSource() == audioReplaceButton) {

			if (!selectedAudioReplaceFileLabel.getText().equals(defaultAudioReplaceText)) {
				try {
					if (validateMedia()) {
						// This assumes that the audio selected is valid. No checking is done.
						// http://stackoverflow.com/questions/3140992/read-out-time-length-duration-of-an-mp3-song-in-java
						if (!selectedAudioReplaceFileLabel.getText().equals(defaultAudioReplaceText)) {
							File audioFile = new File(selectedAudioReplaceFileLabel.getText());
							
							String audioPath = audioFile.getPath();
							
							JFileChooser chooser = new JFileChooser();
							
							// Removes the accept all filter.
							chooser.setAcceptAllFileFilterUsed(false);
							// Adds mp4 as filter.
							chooser.addChoosableFileFilter(new FileNameExtensionFilter("MPEG-4", "mp4"));
							
							int selection = chooser.showSaveDialog(null);
							
							if (selection == JOptionPane.OK_OPTION) {
								File saveFile = chooser.getSelectedFile();
								String outputFilename = saveFile.getPath();
								
								String extensionType = chooser.getFileFilter().getDescription();
								
								/*
								 * Even though the extension type is listed below, sometimes users still add .mp3 to the end of the file so this
								 * makes sure that when I add extension type to the filename, I only add .mp3 if it's not already there.
								 * 
								 * At the moment, I only have .mp4 has possible extension.
								 */

								if (extensionType.contains("MPEG-4") && !saveFile.getPath().contains(".mp4")) {
									outputFilename = outputFilename + ".mp4";
								}
								
								// Checks to see if the filename the user wants to save already exists so it asks if it wants to overwrite or not.
								if (Files.exists(Paths.get(outputFilename))) {
									int overwriteSelection = JOptionPane.showConfirmDialog(null, "File already exists, do you want to overwrite?",
											"Select an option", JOptionPane.YES_NO_OPTION);

									// Overwrite if yes.
									if (overwriteSelection == JOptionPane.OK_OPTION) {
										executeReplace(MRLFilename.getFilename(mediaPlayer.mrl()), audioPath, outputFilename);
									}
								} else {
									executeReplace(MRLFilename.getFilename(mediaPlayer.mrl()), audioPath, outputFilename);
								}
							}
						}
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} else {
				JOptionPane.showMessageDialog(null, "Please select audio file to replace");
			}
		}
	}

	private void executeReplace(String videoInput, String audioInput, String videoOutput) {
		
		AudioReplaceWorker worker = new AudioReplaceWorker(videoInput, audioInput, videoOutput);
		worker.execute();
		JOptionPane.showMessageDialog(null, "Replacing audio has started");
	}
	
	/**
	 * Returns the selected mp3 file from JFileChooser.
	 * @return
	 */

	private String getInputFilename() {
		JFileChooser chooser = new JFileChooser();

		// Removes the accept all filter.
		chooser.setAcceptAllFileFilterUsed(false);
		// Adds mp3 as filter.
		chooser.addChoosableFileFilter(new FileNameExtensionFilter("MPEG/mp3", "mp3"));

		int selection = chooser.showOpenDialog(this);

		if (selection == JFileChooser.APPROVE_OPTION) {
			File saveFile = chooser.getSelectedFile();

			String inputFilename = saveFile.getPath();

			return inputFilename;
		}
		return null;
	}

	/**
	 * Returns the output filename of the mp3 audio. Asks user if overwrite is desired if same file exists.
	 * @return
	 * @throws IOException
	 */

	private String getOutputFilename() throws IOException {
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
					return outputFilename;
				}
			} else {
				return outputFilename;
			}
		}

		return null;
	}

	/**
	 * {@link se206.a03.ExtractAudioWorker }
	 * 
	 * @param inputFilename
	 * @param outputFilename
	 * @param startTime
	 * @param lengthTime
	 */

	private void executeExtract(String inputFilename, String outputFilename, String startTime, String lengthTime) {
		int lengthOfAudio = MediaTimer.getSeconds(lengthTime);

		ProgressMonitor monitor = new ProgressMonitor(null, "Extraction has started", "", 0, lengthOfAudio);

		ExtractAudioWorker worker = new ExtractAudioWorker(inputFilename, outputFilename, startTime, lengthTime, lengthOfAudio, monitor);
		worker.execute();
	}

	/**
	 * Determines if the user input a valid time in the specified format. hh:mm:ss <br />
	 * 
	 * @param time
	 * @return
	 */

	private boolean validateTime(String time) {
		Date inputTime = null;
		try {
			// Checks that you can formulate date from given input.
			inputTime = timeFormat.parse(time);
		} catch (ParseException e) {
			// The time that was input does not match our given time format.
			JOptionPane.showMessageDialog(null, time + " is in the wrong time format");
			return false;
		}

		// Time can be rounded so ensure input time is correct. eg 61 seconds automatically becomes 1min 1sec.
		if (!timeFormat.format(inputTime).equals(time)) {
			JOptionPane.showMessageDialog(null, "Invalid time");
			return false;
		}

		// If we reach this statement, time has been validated.
		return true;
	}

	/**
	 * Determines if there is a media loaded onto player which is a video and contains an audio track.
	 * @return
	 * @throws IOException 
	 */

	private boolean validateMedia() throws IOException {
		if (mediaPlayer.isPlayable()) {
			String inputFilename = MRLFilename.getFilename(mediaPlayer.mrl());

			if (inputFilename == null) {
				JOptionPane.showMessageDialog(null, "Incorrect file directory");
				return false;
			}

			String type = Files.probeContentType(Paths.get(inputFilename));

			if (type.contains("video")) {

				if (mediaPlayer.getAudioTrackCount() == 0) {
					JOptionPane.showMessageDialog(null, "No audio track exists in video");
					return false;
				}
			} else {
				JOptionPane.showMessageDialog(null, "Media is not video file");
				return false;
			}
		} else {
			JOptionPane.showMessageDialog(null, "No media recognized");
			return false;
		}

		return true;
	}

}
