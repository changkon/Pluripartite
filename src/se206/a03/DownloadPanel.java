package se206.a03;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/** Manages the download
 *  Pop-ups guiding the user through the process
 *  Executes DownloadWorker when everything has been verified
 *  
 *  -Can cancel the DownloadWorker
 */



public class DownloadPanel extends JPanel implements ActionListener {
	
	String URL;
	DownloadWorker dw;
	JButton download;
	JButton cancel;
	JLabel downloadLabel;
	JTextField urlInput;
	
	public DownloadPanel(){
		download = new JButton("Download");
		cancel = new JButton("Cancel");
		downloadLabel = new JLabel("Download URL: ");
		urlInput = new JTextField("", 40);
		
		urlInput.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					e.consume();
					executeDownload();
				}
				
			}

			@Override
			public void keyReleased(KeyEvent e) {}

			@Override
			public void keyTyped(KeyEvent e) {}
		});
		
		download.setBorderPainted(false);
		download.setFocusPainted(false);
		download.setContentAreaFilled(false);
		
		cancel.setBorderPainted(false);
		cancel.setFocusPainted(false);
		cancel.setContentAreaFilled(false);
		
		download.getModel().addChangeListener(new ChangeListener() {
	        @Override
	        public void stateChanged(ChangeEvent e) {
	            ButtonModel model = (ButtonModel) e.getSource();
	            if (model.isRollover()) {
	            	download.setBorderPainted(true);
	            } else {
	            	download.setBorderPainted(false);
	            }
	        }
	    });
		
		cancel.getModel().addChangeListener(new ChangeListener() {
	        @Override
	        public void stateChanged(ChangeEvent e) {
	            ButtonModel model = (ButtonModel) e.getSource();
	            if (model.isRollover()) {
	            	cancel.setBorderPainted(true);
	            } else {
	            	cancel.setBorderPainted(false);
	            }
	        }
	    });
		
		add(downloadLabel);
		add(urlInput);
		add(download);
		add(cancel);	
		
		download.addActionListener(this);
		cancel.addActionListener(this);
	}
	
	public void downloadExecute(String temp){
		URL = temp;
		
		/*Boolean validInput = false;
			URL = JOptionPane.showInputDialog("Enter the URL");
			// If they entered a valid URL
		*/
			if(URL != null && !URL.isEmpty()){	
				File basename = new File(URL);
				File fullName = new File(System.getProperty("user.dir") + "/" + basename.getName());
				int reply;
				//if the file exists
				if(fullName.exists()){
					//prompt the user if they would like to overwrite or resume download
					reply = JOptionPane.showConfirmDialog(null, "FileExists! Resume download? (Yes for resume download, no for overwrite)", "OverwriteCheck", JOptionPane.YES_NO_OPTION);
					if(reply == JOptionPane.NO_OPTION){
						//if overwrite, delete the existing file
						fullName.delete();							
					}
				}
				
				//ask the user if its open source
				reply = JOptionPane.showConfirmDialog(null, "Is this open source?", "Open Source Check", JOptionPane.YES_NO_OPTION);
				if (reply == JOptionPane.YES_OPTION){
				  	
					boolean valid = false;
					try {
						valid = validURLCheck();
					} catch (IOException e) {
						e.printStackTrace();
					}
					if(valid){
						try{
					  		ProgressMonitor monitor = new ProgressMonitor(null, "Download has started", "", 0, 100);
					  		//if open source, execute the download through DownloadWorker
							dw = new DownloadWorker(URL, monitor);
							dw.execute();
						}catch(Exception eeee){
						}
					}else{
						JOptionPane.showMessageDialog(null, "URL is not valid. Please enter a valid URL");
					}
				}
				else {
					//Ask a user to come back with another song that is open source
					JOptionPane.showMessageDialog(null, "Please choose another open source song!");
				}
			}
		
	}
	
	private boolean validURLCheck() throws IOException {
		/*
		SwingWorker<String, String> temp = new SwingWorker<String, String>(){

			@Override
			protected String doInBackground() throws Exception {
				publish("");
				return null;
			}
			
			@Override
			protected void process(List<String> chunks){
				JOptionPane.showMessageDialog(null, "Checking if URL is valid..");
			}
			
			@Override
			protected void done(){
			}
			
		}; temp.execute();
		*/
		
		ProcessBuilder builder = new ProcessBuilder("/bin/bash","-c","wget --spider -v " + URL);
		Process process = builder.start();		
		String stdOutput = null;
		String lastOutput = null;
		
		builder.redirectErrorStream(true);
		
		process = builder.start();
		InputStream out = process.getInputStream();
		BufferedReader stdout = new BufferedReader(new InputStreamReader(out));
		
		stdOutput = stdout.readLine();
		while(stdOutput != null && !stdOutput.equals("") && !(stdOutput.length() == 0)){
			lastOutput = stdOutput;
			stdOutput = stdout.readLine();
		}
		
		if(lastOutput.equals("Remote file exists.")){
			return true;
		}else{
			return false;
		}
		
	}

	/** Cancels the current DownloadWorker
	 * 
	 */
	
	public void cancelDownload(){
		dw.cancel(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == download){
			executeDownload();		
		}
		else if (e.getSource() == cancel){
			this.setVisible(false);
			urlInput.setText("");
			URL = "";
		}
	}
	
	private void executeDownload(){
		if (!(urlInput.getText().equals("")) && urlInput.getText() != null && urlInput.getText().length() != 0){
			downloadExecute(urlInput.getText());
		}
	}

}	
	

