package se206.a03;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ProgressMonitor;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.DocumentFilter;

import net.miginfocom.swing.MigLayout;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

/**
 * Singleton design pattern. Panel contains anything related to filter editing of video.
 * 
 */

@SuppressWarnings("serial")
public class FilterPanel extends JPanel implements ActionListener {
	private static FilterPanel theInstance = null;
	private EmbeddedMediaPlayer mediaPlayer = MediaPanel.getInstance().getMediaPlayer();
	
	private static final int MAXWORDS = 20;
	
	private JLabel textLabel = new JLabel("Text (" + MAXWORDS + " words max for each selection). X and Y are co ordinates. This is optional");
	
	private JPanel openingTextPanel = new JPanel(new MigLayout());
	private JPanel closingTextPanel = new JPanel(new MigLayout());
	
	private JPanel openingOptionPanel = new JPanel(new MigLayout());
	private JPanel closingOptionPanel = new JPanel(new MigLayout());
	
	private JTextArea openingTextArea = new JTextArea(new MyStyledDocument(MAXWORDS));
	private JScrollPane openingTextScroll = new JScrollPane(openingTextArea);
	
	private JTextArea closingTextArea = new JTextArea(new MyStyledDocument(MAXWORDS));
	private JScrollPane closingTextScroll = new JScrollPane(closingTextArea);
	
	private Integer[] fontSizeSelection = {10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 32, 34, 36, 38, 40};
	
	private JComboBox<FilterFont> openingFontCombo = new JComboBox<FilterFont>(FilterFont.values());
	private JComboBox<Integer> openingFontSizeCombo = new JComboBox<Integer>(fontSizeSelection);
	private JComboBox<FilterColor> openingFontColorCombo = new JComboBox<FilterColor>(FilterColor.values());
	
	private JLabel openingXLabel = new JLabel("x:");
	private JLabel openingYLabel = new JLabel("y:");
	
	private String[] timeLengthSelection = {"1 second","2 seconds","3 seconds","4 seconds","5 seconds",
											"6 seconds", "7 seconds", "8 seconds", "9 seconds", "10 seconds"};
	private JComboBox<String> openingTimeLength = new JComboBox<String>(timeLengthSelection);
	private JComboBox<String> closingTimeLength = new JComboBox<String>(timeLengthSelection);
	
	private JTextField openingXTextField = new JTextField(5);
	private JTextField openingYTextField = new JTextField(5);
	
	private JComboBox<FilterFont> closingFontCombo = new JComboBox<FilterFont>(FilterFont.values());
	private JComboBox<Integer> closingFontSizeCombo = new JComboBox<Integer>(fontSizeSelection);
	private JComboBox<FilterColor> closingFontColorCombo = new JComboBox<FilterColor>(FilterColor.values());
	
	private JLabel closingXLabel = new JLabel("x:");
	private JLabel closingYLabel = new JLabel("y:");
	
	private JTextField closingXTextField = new JTextField(5);
	private JTextField closingYTextField = new JTextField(5);
	
	private JButton saveButton = new JButton("Save Video");
	private JButton previewButton = new JButton("Preview");
	private JButton saveWorkButton = new JButton("Save current work");
	
	//private MediaPanel mp = MediaPanel.getInstance();
	//private EmbeddedMediaPlayerComponent mpc = mp.mediaPlayerComponent;
	
	private String currentFileName = "";
	
	public static FilterPanel getInstance() {
		if (theInstance == null) {
			theInstance = new FilterPanel();
		}
		return theInstance;
	}
	
	private FilterPanel() {
		setLayout(new MigLayout("gap rel 0" , "grow"));
		setOpeningTextPanel();
		setClosingTextPanel();
		addListeners();		
		
		add(textLabel, "wrap");
			
		add(openingTextPanel, "left");
		add(closingTextPanel, "right");

		add(saveButton);
		add(previewButton);
		add(saveWorkButton);
	}

	public void checkLog(String fileName) {
		//set up the folder if it doesnt exist
		currentFileName = fileName;
		File home = new File(System.getProperty("user.home"));
		File vamixdir = new File(home + "/.vamix");
				if (!vamixdir.exists()) {
					try{
						vamixdir.mkdir();
					}catch(SecurityException se){
					
					}
				}
		//set up the file if it doesnt exist
		File log = new File(home + "/.vamix/log.txt");
				try{
					if(!log.exists()){
						log.createNewFile();
					}
					}catch(IOException e){
				}
		try{
			BufferedReader in = new BufferedReader(new FileReader(log));
					String line = in.readLine();
					while(line != null && line.length() != 0){
					  String[] words = line.split(",");
					  if(words[0].equals(fileName)){							
							openingTextArea.setText(words[1]);
							closingTextArea.setText(words[2]);
							openingXTextField.setText(words[3]);
							closingXTextField.setText(words[4]);
							openingYTextField.setText(words[5]);
							closingYTextField.setText(words[6]);
							openingFontCombo.setSelectedItem(FilterFont.toFilterFont(words[7]));
							closingFontCombo.setSelectedItem(FilterFont.toFilterFont(words[8]));
							openingFontSizeCombo.setSelectedItem(Integer.parseInt(words[9]));
							closingFontSizeCombo.setSelectedItem(Integer.parseInt(words[10]));
							openingFontColorCombo.setSelectedItem(FilterColor.toFilterColor(words[11]));
							closingFontColorCombo.setSelectedItem(FilterColor.toFilterColor(words[12]));
							openingTimeLength.setSelectedItem(words[13]);
							closingTimeLength.setSelectedItem(words[14]);
							
							/*System.out.println(openingTextArea.getText());
							System.out.println(closingTextArea.getText());
							System.out.println(openingXTextField.getText());
							System.out.println(closingXTextField.getText());
							System.out.println(openingYTextField.getText());
							System.out.println(closingYTextField.getText());
							System.out.println(openingFontCombo.getSelectedItem());
							System.out.println(closingFontCombo.getSelectedItem());
							System.out.println(openingFontSizeCombo.getSelectedItem());
							System.out.println(closingFontSizeCombo.getSelectedItem());
							System.out.println(openingFontColorCombo.getSelectedItem());
							System.out.println(closingFontColorCombo.getSelectedItem());
							*/
					  }
					  line = in.readLine();
					}
				}catch(Exception eeeee){
					eeeee.printStackTrace();
				}
	}

	private void setOpeningTextPanel() {
		// Sets filter for textfields.
		((AbstractDocument)openingXTextField.getDocument()).setDocumentFilter(new MyTextFieldFilter());
		((AbstractDocument)openingYTextField.getDocument()).setDocumentFilter(new MyTextFieldFilter());

		openingOptionPanel.add(openingFontCombo, "split 3"); // split the cell in 3. this so 3 components go into same cell
		openingOptionPanel.add(openingFontSizeCombo);
		openingOptionPanel.add(openingFontColorCombo, "wrap");
		openingOptionPanel.add(openingXLabel, "split 5"); // split the cell in 4. this is so 4 components go into same cell
		openingOptionPanel.add(openingXTextField);
		openingOptionPanel.add(openingYLabel);
		openingOptionPanel.add(openingYTextField);
		openingOptionPanel.add(openingTimeLength);
		
		openingTextScroll.setPreferredSize(new Dimension(400, 200)); // arbitrary value.
		
		openingTextArea.setLineWrap(true);
		openingTextArea.setWrapStyleWord(true);
		openingTextArea.setText("Opening Scene Text");
		
		openingTextPanel.add(openingOptionPanel, "wrap");
		openingTextPanel.add(openingTextScroll);
	}
	
	private void setClosingTextPanel() {
		// Sets filter for textfields.
		((AbstractDocument)closingXTextField.getDocument()).setDocumentFilter(new MyTextFieldFilter());
		((AbstractDocument)closingYTextField.getDocument()).setDocumentFilter(new MyTextFieldFilter());
		
		closingOptionPanel.add(closingFontCombo, "split 3"); // split the cell in 3. this so 3 components go into same cell
		closingOptionPanel.add(closingFontSizeCombo);
		closingOptionPanel.add(closingFontColorCombo, "wrap");
		closingOptionPanel.add(closingXLabel, "split 5"); // split the cell in 4. this is so 4 components go into same cell
		closingOptionPanel.add(closingXTextField);
		closingOptionPanel.add(closingYLabel);
		closingOptionPanel.add(closingYTextField);
		closingOptionPanel.add(closingTimeLength);
		
		closingTextScroll.setPreferredSize(new Dimension(400, 200));
		
		closingTextArea.setLineWrap(true);
		closingTextArea.setWrapStyleWord(true);
		closingTextArea.setText("Closing Scene Text");
		
		closingTextPanel.add(closingOptionPanel, "wrap");
		closingTextPanel.add(closingTextScroll);
	}
	
	private void addListeners() {
		openingFontCombo.addActionListener(this);
		openingFontSizeCombo.addActionListener(this);
		openingFontColorCombo.addActionListener(this);
		openingTimeLength.addActionListener(this);
		
		closingFontCombo.addActionListener(this);
		closingFontSizeCombo.addActionListener(this);
		closingFontColorCombo.addActionListener(this);
		closingTimeLength.addActionListener(this);
		
		
		// Sets the preferred index of font size. It also calls event listener which is important for displaying correct font.
		openingFontSizeCombo.setSelectedIndex(3);
		closingFontSizeCombo.setSelectedIndex(3);
		
		saveButton.addActionListener(this);
		previewButton.addActionListener(this);
		saveWorkButton.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == openingFontCombo || e.getSource() == openingFontSizeCombo || e.getSource() == openingFontColorCombo) {
			Font font = ((FilterFont)openingFontCombo.getSelectedItem()).getFont();
			font = font.deriveFont((float)((Integer)openingFontSizeCombo.getSelectedItem()));
			openingTextArea.setFont(font);
			openingTextArea.setForeground(((FilterColor)openingFontColorCombo.getSelectedItem()).getColor());
		} else if (e.getSource() == closingFontCombo || e.getSource() == closingFontSizeCombo || e.getSource() == closingFontColorCombo) {
			Font font = ((FilterFont)closingFontCombo.getSelectedItem()).getFont();
			font = font.deriveFont((float)((Integer)closingFontSizeCombo.getSelectedItem()));
			closingTextArea.setFont(font);
			closingTextArea.setForeground(((FilterColor)closingFontColorCombo.getSelectedItem()).getColor());
		} else if (e.getSource() == saveButton) {
			if (verifyInput()) {
				
				JFileChooser chooser = new JFileChooser();
				
				// Removes the accept all filter.
				chooser.setAcceptAllFileFilterUsed(false);
				// Adds mp3 as filter.
				chooser.addChoosableFileFilter(new FileNameExtensionFilter("MPEG-4", "mp4"));
				
				int selection = chooser.showSaveDialog(null);
				
				switch(selection) {
					case JFileChooser.APPROVE_OPTION:
						
						File saveFile = chooser.getSelectedFile();
						
						String extensionType = chooser.getFileFilter().getDescription();
						String outputFilename = saveFile.getPath();
						
						if (extensionType.contains("MPEG-4") && !saveFile.getPath().contains(".mp4")) {
							outputFilename = outputFilename + ".mp4";
						}
						
						if (Files.exists(Paths.get(outputFilename))) {
							int overwriteSelection = JOptionPane.showConfirmDialog(null, "File already exists, do you want to overwrite?",
									"Select an option", JOptionPane.YES_NO_OPTION);
							
							// Overwrite if yes.
							if (overwriteSelection == JOptionPane.OK_OPTION) {
								executeFilterSave(outputFilename);
							}
						} else {
							executeFilterSave(outputFilename);
						}
						
						break;
					default:
						break;
				}
				
			}
		} else if (e.getSource() == previewButton) {
			if (verifyInput()) {
				int lengthOfVideo = (int)(mediaPlayer.getLength() / 1000);
				
				FilterPreviewWorker worker = new FilterPreviewWorker(
						MRLFilename.getFilename(mediaPlayer.mrl()),
						openingTextArea.getText(),
						closingTextArea.getText(),
						openingXTextField.getText(),
						closingXTextField.getText(),
						openingYTextField.getText(),
						closingYTextField.getText(),
						(FilterFont)openingFontCombo.getSelectedItem(),
						(FilterFont)closingFontCombo.getSelectedItem(),
						(Integer)openingFontSizeCombo.getSelectedItem(),
						(Integer)closingFontSizeCombo.getSelectedItem(), 
						(FilterColor)openingFontColorCombo.getSelectedItem(),
						(FilterColor)closingFontColorCombo.getSelectedItem(),
						lengthOfVideo
						);
				
				
				worker.execute();
			}
		} else if (e.getSource() == saveWorkButton){
			if(verifyInput()){
				PrintWriter writer = null;
				try {
					File home = new File(System.getProperty("user.home"));
					File logFile = new File(home + "/.vamix/log.txt");
					writer = new PrintWriter(new FileWriter(logFile, true));
					String txtline = currentFileName + "," + openingTextArea.getText() + "," +closingTextArea.getText() + "," +
									 openingXTextField.getText() + "," + closingXTextField.getText()+ "," + openingYTextField.getText() + ","
									 + closingYTextField.getText() + ","+ openingFontCombo.getSelectedItem() + "," + closingFontCombo.getSelectedItem() + ","
									 + openingFontSizeCombo.getSelectedItem()+ "," + closingFontSizeCombo.getSelectedItem() + "," +
									 openingFontColorCombo.getSelectedItem()+ "," + closingFontColorCombo.getSelectedItem() + "," + 
									 openingTimeLength.getSelectedItem() + "," + closingTimeLength.getSelectedItem();
					writer.println(txtline);
					JOptionPane.showMessageDialog(null, "Saved session for this video. Press okay!");
				} catch (Exception eee) {
					eee.printStackTrace();
				} finally {
					try {
						writer.close();
					} catch (Exception ssse) {
					}
				}
			}
		} else if (e.getSource() == openingTimeLength ){
			MediaSetting.getInstance().setOpeningFilterLength((String)openingTimeLength.getSelectedItem());
		} else if (e.getSource() == closingTimeLength ){
			MediaSetting.getInstance().setClosingFilterLength((String)closingTimeLength.getSelectedItem());
		}
	}
	
	private void executeFilterSave(String outputFilename) {
		
		int lengthOfVideo = (int)(mediaPlayer.getLength() / 1000);
		
		ProgressMonitor monitor = new ProgressMonitor(null, "Filtering has started", "", 0, lengthOfVideo);
		
		FilterSaveWorker worker = new FilterSaveWorker(
				MRLFilename.getFilename(mediaPlayer.mrl()),
				outputFilename,
				openingTextArea.getText(),
				closingTextArea.getText(),
				openingXTextField.getText(),
				closingXTextField.getText(),
				openingYTextField.getText(),
				closingYTextField.getText(),
				(FilterFont)openingFontCombo.getSelectedItem(),
				(FilterFont)closingFontCombo.getSelectedItem(),
				(Integer)openingFontSizeCombo.getSelectedItem(),
				(Integer)closingFontSizeCombo.getSelectedItem(), 
				(FilterColor)openingFontColorCombo.getSelectedItem(),
				(FilterColor)closingFontColorCombo.getSelectedItem(),
				monitor,
				lengthOfVideo
				);
		
		worker.execute();
	}
	
	private boolean verifyInput() {
		
		// If media is not parsed, return false;
		if (!mediaPlayer.isPlayable()) {
			JOptionPane.showMessageDialog(null, "Please parse media");
			return false;
		}
		
		String inputFilename = MRLFilename.getFilename(mediaPlayer.mrl());
		
		try {
			String type = Files.probeContentType(Paths.get(inputFilename));
			
			if (!type.contains("video")) {
				JOptionPane.showMessageDialog(null, "This is not a video file");
				return false;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		String openingText = openingTextArea.getText();
		String closingText = closingTextArea.getText();
		
		// Return false if both textArea are empty
		if (openingText.equals("") && closingText.equals("")) {
			JOptionPane.showMessageDialog(null, "Please input text in opening text or closing text");
			return false;
		}
		
		
		return true;
	}
	
	/**
	 * 
	 * Document which limits amount the amount of words in document. Words are recognised when they are separated by space
	 *
	 */
	
	private class MyStyledDocument extends DefaultStyledDocument {
		private int maxWords;
		
		public MyStyledDocument(int maxWords) {
			this.maxWords = maxWords;
		}
		
		// Override insertString method. Only add strings if less than 20 words. Words are counted if they are separated by space.
		@Override
		public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
			String text = getText(0, getLength());
			int count = 0;
			
			for (char c : text.toCharArray()) {
				if (c == ' ') {
					count++;
				}
			}
			
			if (count >= maxWords - 1 && str.equals(" ")) {
				JOptionPane.showMessageDialog(null, "Exceeded word limit!");
				return;
			}
			
			super.insertString(offs, str, a);
		}
		
	}
	
	/**
	 * 
	 * Filtering only numbers
	 * @see http://stackoverflow.com/questions/9477354/how-to-allow-introducing-only-digits-in-jtextfield
	 *
	 */
	
	private class MyTextFieldFilter extends DocumentFilter {

		// Called when insertString method is called on document. eg textField.getDocument().insertString(..);
		@Override
		public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {

			boolean isDigits = true;
			
			for(char c : string.toCharArray()) {
				if (!Character.isDigit(c)) {
					isDigits = false;
					break;
				}
			}

			if (isDigits) {
				super.insertString(fb, offset, string, attr);
			}
		}

		// Invoked whenever text is input into textfield
		@Override
		public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
			
			boolean isDigits = true;
			
			for(char c : text.toCharArray()) {
				if (!Character.isDigit(c)) {
					isDigits = false;
					break;
				}
			}

			if (isDigits) {
				super.replace(fb, offset, length, text, attrs);
			}
		}
		
	}
}
