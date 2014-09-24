package se206.a03;

import javax.swing.SwingWorker;

/**
 * 
 * Shows preview of filter options. Opens a JFrame and shows the video with filters.
 *
 */

public class FilterPreviewWorker extends SwingWorker<Void, Void> {
	private String inputFilename;
	private String openingText;
	private String closingText;
	private String openingX;
	private String openingY;
	private String closingX;
	private String closingY;
	private String openingORclosing;
	
	private FilterFont openingFont;
	private FilterFont closingFont;
	private int openingFontSize;
	private int closingFontSize;
	private FilterColor openingFontColor;
	private FilterColor closingFontColor;
	private int lengthOfVideo;

	
	public FilterPreviewWorker(String openingORclosing, String inputFilename, String openingText, String closingText, String openingX, String closingX, String openingY, String closingY, 
			FilterFont openingFont, FilterFont closingFont, int openingFontSize, int closingFontSize, FilterColor openingFontColor, FilterColor closingFontColor, int lengthOfVideo) {
		this.openingORclosing = openingORclosing;
		this.inputFilename = inputFilename;
		this.openingText = openingText;
		this.closingText = closingText;
		
		// Set openingX/openingY and closingX/closingY. If the value is empty, give it determinded values.
		if (openingX.equals("")) {
			this.openingX = "(W/2)-(w/2)"; // Sets to middle of screen. W = main input width. w = text width.
		} else {
			this.openingX = openingX;
		}
		
		if (closingX.equals("")) {
			this.closingX = "(W/2)-(w/2)";
		} else {
			this.closingX = closingX;
		}
		
		if (openingY.equals("")) {
			this.openingY = "(H/1.1)"; // Sets near the bottom of the screen. H = main input height.
		} else {
			this.openingY = openingY;
		}
		
		if (closingY.equals("")) {
			this.closingY = "(H/1.1)";
		} else {
			this.closingY = closingY;
		}		
		
		
		this.openingFont = openingFont;
		this.closingFont = closingFont;
		this.openingFontSize = openingFontSize;
		this.closingFontSize = closingFontSize;
		this.openingFontColor = openingFontColor;
		this.closingFontColor = closingFontColor;
		this.lengthOfVideo = lengthOfVideo;
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		
		int filterOpeningLength = MediaSetting.getInstance().getOpeningFilterLength();
		int filterClosingLength = MediaSetting.getInstance().getClosingFilterLength();
		int lastSeconds = lengthOfVideo - filterClosingLength;
				
		StringBuilder command = null;
		if(openingORclosing.equals("Opening")){
			int filterOpeningSceneLen = filterOpeningLength + 2;
			command = new StringBuilder("avplay -i " + inputFilename + " -t "+ filterOpeningSceneLen +" -vf ");
		}else if(openingORclosing.equals("Closing")){
			
			int filterClosingSceneLen = (int) (MediaPanel.getInstance().mediaPlayer.getLength() - 1000*filterClosingLength - 1000*2);
			
			String filterClosingSceneString = MediaTimer.getFormattedTime(filterClosingSceneLen);
			if(filterClosingSceneString.length() == 5){
				filterClosingSceneString = "00:" + filterClosingSceneString;
			}

			command = new StringBuilder("avplay -i " + inputFilename + " -ss "+ filterClosingSceneString + " -vf ");
		}
		
		
		boolean hasOpeningText = !openingText.equals("");
		boolean hasClosingText = !closingText.equals("");
		
		if (hasOpeningText && hasClosingText) {
			command.append("drawtext=\"fontfile=" + openingFont.getPath() + ": fontsize=" + openingFontSize + ": fontcolor=" + openingFontColor.toString() + ": x=" + openingX + ": y=" 
					+ openingY + ": text=\'" + openingText + "\': draw=\'lt(t," + filterOpeningLength + ")\':,drawtext=fontfile=" + closingFont.getPath() + ": fontsize=" + closingFontSize + 
					": fontcolor=" + closingFontColor.toString() + ": x=" +	closingX + ": y=" + closingY + ": text=\'" + closingText + "\': draw=\'gt(t," + lastSeconds + ")\'\"");
		} else if (hasOpeningText) {
			command.append("drawtext=\"fontfile=" + openingFont.getPath() + ": fontsize=" + openingFontSize + ": fontcolor=" + openingFontColor.toString() + ": x=" + openingX + 
					": y=" + openingY + ": text=\'" + openingText + "\': draw=\'lt(t," + filterOpeningLength + ")\'\"");
		} else {
			command.append("drawtext=\"fontfile=" + closingFont.getPath() + ": fontsize=" + closingFontSize + ": fontcolor=" + closingFontColor.toString() + ": x=" + closingX + 
					": y=" + closingY + ": text=\'" + closingText + "\': draw=\'gt(t," + lastSeconds + ")\'\"");
		}
		
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", command.toString());
		
		System.out.println(command.toString());
		
		Process process = builder.start();
		//process.waitFor();
		

		if(openingORclosing.equals("Opening")){
			Thread.sleep(filterOpeningLength * 1000);
		}else if(openingORclosing.equals("Closing")){
			if(filterClosingLength < 10){
				Thread.sleep(10000);
			}else{
				Thread.sleep(filterClosingLength * 10000 + 2000);
			}
		}
		Thread.sleep(1500);
		process.destroy();
		
		return null;
	}

}
