package se206.a03;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.java.ayatana.ApplicationMenu;
import org.java.ayatana.AyatanaDesktop;

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
	
	private JMenu mediaMenu = new JMenu("Media");
	private JMenuItem openMenuOption = new JMenuItem("Open..");
	
	private MediaPanel mediaPanel = MediaPanel.getInstance();
	
	
	public VamixFrame() {
		super("VAMIX");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(new Dimension(800, 500));
		
		setMenuBar();
		setJMenuBar(menuBar);
		
		panels.add(mediaPanel, "Media");

		add(panels);
		
		addListeners();
		
		// Makes sure when window closes, it releases the mediaPlayer.
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				mediaPanel.releaseMediaPlayer();
			}
			
		});
	}
	
	private void setMenuBar() {
		panelMenu.add(mediaPanelOption);
		mediaMenu.add(openMenuOption);
		
		menuBar.add(panelMenu);
		menuBar.add(mediaMenu);
	}
	
	private void addListeners() {
		openMenuOption.addActionListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == openMenuOption) {
			mediaPanel.playFile();
		}
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				VamixFrame vamixFrame = new VamixFrame();
				vamixFrame.setVisible(true);
				
				if(AyatanaDesktop.isSupported()) {
					ApplicationMenu.tryInstall(vamixFrame);
				}
			}
			
		});
	}
}
