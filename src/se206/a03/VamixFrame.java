package se206.a03;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * 
 * Currently hard coded but will update. Download Full HD (1920x1080) 60fps http link by right click save as.
 * 
 * @see http://bbb3d.renderfarming.net/download.html
 *
 */

@SuppressWarnings("serial")
public class VamixFrame extends JFrame implements ActionListener {

	private JPanel panels = new JPanel(new CardLayout());
	
	private JMenuBar menuBar = new JMenuBar();
	
	private JMenu panelMenu = new JMenu("Panel");
	private JMenuItem mediaPanelOption = new JMenuItem("Media Player"); 
	private JMenuItem filterPanelOption = new JMenuItem("Filters");
	
	private JMenu mediaMenu = new JMenu("Media");
	private JMenuItem openMenuOption = new JMenuItem("Open..");
	private JMenuItem extractAudioFromVideoOption = new JMenuItem("Extract Audio");
	
	private MediaPanel mediaPanel = MediaPanel.getInstance();
	private final String MEDIA = "Media";
	
	private FilterPanel filterPanel = FilterPanel.getInstance();
	private final String FILTER = "Filter";
	
	public VamixFrame() {
		super("VAMIX");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(new Dimension(1200, 700));
		
		setMenuBar();
		setJMenuBar(menuBar);
		
		panels.add(mediaPanel, MEDIA);
		panels.add(filterPanel, FILTER);
		
		add(panels);
		
		addListeners();
	}
	
	private void setMenuBar() {
		panelMenu.add(mediaPanelOption);
		panelMenu.add(filterPanelOption);
		
		mediaMenu.add(openMenuOption);
		mediaMenu.add(extractAudioFromVideoOption);
		
		menuBar.add(panelMenu);
		menuBar.add(mediaMenu);
	}
	
	private void addListeners() {
		openMenuOption.addActionListener(this);
		extractAudioFromVideoOption.addActionListener(this);
		mediaPanelOption.addActionListener(this);
		filterPanelOption.addActionListener(this);
		
		// Makes sure when window closes, it releases the mediaPlayer.
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				mediaPanel.getMediaPlayer().release();
			}
			
		});
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == openMenuOption) {
			mediaPanel.playFile();
		} else if (e.getSource() == extractAudioFromVideoOption) {
			try {
				mediaPanel.extractAudio();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else if (e.getSource() == mediaPanelOption) {
			CardLayout c = (CardLayout)panels.getLayout();
			c.show(panels, MEDIA);
		} else if (e.getSource() == filterPanelOption) {
			CardLayout c = (CardLayout)panels.getLayout();
			c.show(panels, FILTER);
		}
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				VamixFrame vamixFrame = new VamixFrame();
				vamixFrame.setVisible(true);
			}
			
		});
	}
}
