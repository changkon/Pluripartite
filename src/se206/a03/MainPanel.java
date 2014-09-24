package se206.a03;

import java.awt.Color;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class MainPanel extends JPanel {
	private static MainPanel theInstance = null;
	
	private MediaPanel mediaPanel = MediaPanel.getInstance();
	private AudioPanel audioPanel = AudioPanel.getInstance();
	
	public static MainPanel getInstance() {
		if (theInstance == null) {
			theInstance = new MainPanel();
		}
		return theInstance;
	}
	
	private MainPanel() {
		setLayout(new MigLayout());
		
//		mediaPanel.setBackground(Color.blue);
//		audioPanel.setBackground(Color.RED);
		
		add(mediaPanel, "wrap");
		add(audioPanel);
	}
}
