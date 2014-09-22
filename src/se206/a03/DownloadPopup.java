package se206.a03;

import java.io.File;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.ProgressMonitor;

/** Manages the download
 *  Pop-ups guiding the user through the process
 *  Executes DownloadWorker when everything has been verified
 *  
 *  -Can cancel the DownloadWorker
 */



public class DownloadPopup {
	
	String URL;
	DownloadWorker dw;
	
	public DownloadPopup(){}
	
	public void downloadExecute(){
		URL = "";
		
		Boolean validInput = false;
			URL = JOptionPane.showInputDialog("Enter the URL");
			// If they entered a valid URL
			if(URL != null && !URL.isEmpty()){	
				validInput = true;
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
				  	try{
				  		ProgressMonitor monitor = new ProgressMonitor(null, "Download has started", "", 0, 100);
				  		//if open source, execute the download through DownloadWorker
						dw = new DownloadWorker(URL, monitor);
						dw.execute();
					}catch(Exception eeee){
					}
				}
				else {
					//Ask a user to come back with another song that is open source
					JOptionPane.showMessageDialog(null, "Please choose another open source song!");
				}
			}
		
	}
	
	/** Cancels the current DownloadWorker
	 * 
	 */
	
	public void cancelDownload(){
		dw.cancel(true);
	}
}	
	

