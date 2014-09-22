package se206.a03;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
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
	
	private JLabel openingXLabel = new JLabel("x");
	private JLabel openingYLabel = new JLabel("y");
	
	private JTextField openingXTextField = new JTextField(5);
	private JTextField openingYTextField = new JTextField(5);
	
	private JComboBox<FilterFont> closingFontCombo = new JComboBox<FilterFont>(FilterFont.values());
	private JComboBox<Integer> closingFontSizeCombo = new JComboBox<Integer>(fontSizeSelection);
	private JComboBox<FilterColor> closingFontColorCombo = new JComboBox<FilterColor>(FilterColor.values());
	
	private JLabel closingXLabel = new JLabel("x");
	private JLabel closingYLabel = new JLabel("y");
	
	private JTextField closingXTextField = new JTextField(5);
	private JTextField closingYTextField = new JTextField(5);
	
	private JButton saveButton = new JButton("Save");
	private JButton previewButton = new JButton("Preview");
	
	public static FilterPanel getInstance() {
		if (theInstance == null) {
			theInstance = new FilterPanel();
		}
		return theInstance;
	}
	
	private FilterPanel() {
		setLayout(new MigLayout());
		
		setOpeningTextPanel();
		setClosingTextPanel();
		addListeners();

		add(textLabel, "wrap");
		add(openingTextPanel, "wrap");
		add(closingTextPanel, "wrap");
		add(saveButton, "split 2");
		add(previewButton);
	}

	private void setOpeningTextPanel() {
		// Sets filter for textfields.
		((AbstractDocument)openingXTextField.getDocument()).setDocumentFilter(new MyTextFieldFilter());
		((AbstractDocument)openingYTextField.getDocument()).setDocumentFilter(new MyTextFieldFilter());
		
		openingOptionPanel.add(openingFontCombo, "split 3"); // split the cell in 3. this so 3 components go into same cell
		openingOptionPanel.add(openingFontSizeCombo);
		openingOptionPanel.add(openingFontColorCombo, "wrap");
		openingOptionPanel.add(openingXLabel, "split 4"); // split the cell in 4. this is so 4 components go into same cell
		openingOptionPanel.add(openingXTextField);
		openingOptionPanel.add(openingYLabel);
		openingOptionPanel.add(openingYTextField);
		
		openingTextScroll.setPreferredSize(new Dimension(400, 200)); // arbitrary value.
		
		openingTextArea.setLineWrap(true);
		openingTextArea.setWrapStyleWord(true);
		
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
		closingOptionPanel.add(closingXLabel, "split 4"); // split the cell in 4. this is so 4 components go into same cell
		closingOptionPanel.add(closingXTextField);
		closingOptionPanel.add(closingYLabel);
		closingOptionPanel.add(closingYTextField);
		
		closingTextScroll.setPreferredSize(new Dimension(400, 200));
		
		closingTextArea.setLineWrap(true);
		closingTextArea.setWrapStyleWord(true);

		closingTextPanel.add(closingOptionPanel, "wrap");
		closingTextPanel.add(closingTextScroll);
	}
	
	private void addListeners() {
		openingFontCombo.addActionListener(this);
		openingFontSizeCombo.addActionListener(this);
		openingFontColorCombo.addActionListener(this);
		
		closingFontCombo.addActionListener(this);
		closingFontSizeCombo.addActionListener(this);
		closingFontColorCombo.addActionListener(this);
		
		// Sets the preferred index of font size. It also calls event listener which is important for displaying correct font.
		openingFontSizeCombo.setSelectedIndex(3);
		closingFontSizeCombo.setSelectedIndex(3);
		
		saveButton.addActionListener(this);
		previewButton.addActionListener(this);
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
