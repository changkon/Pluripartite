package se206.a03;

import javax.swing.SwingWorker;

import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class FilterPreviewWorker extends SwingWorker<Void, Void> {
	private String inputFilename;
	private String openingText;
	private String closingText;
	private String openingX;
	private String openingY;
	private String closingX;
	private String closingY;
	private FilterFont openingFont;
	private FilterFont closingFont;
	private int openingFontSize;
	private int closingFontSize;
	private FilterColor openingFontColor;
	private FilterColor closingFontColor;
	private int lengthOfVideo;
	
	public FilterPreviewWorker(String inputFilename, String openingText, String closingText, String openingX, String closingX, String openingY, String closingY, 
			FilterFont openingFont, FilterFont closingFont, int openingFontSize, int closingFontSize, FilterColor openingFontColor, FilterColor closingFontColor, int lengthOfVideo) {
		
		this.inputFilename = inputFilename;
		this.openingText = openingText;
		this.closingText = closingText;
		
		// Set openingX/openingY and closingX/closingY. If the value is empty, give it determinded values.
		if (openingX.equals("")) {
			this.openingX = "(W/2)-(w/2)"; // Sets to middle of screen.
		} else {
			this.openingX = openingX;
		}
		
		if (closingX.equals("")) {
			this.closingX = "(W/2)-(w/2)";
		} else {
			this.closingX = closingX;
		}
		
		if (openingY.equals("")) {
			this.openingY = "(H/1.1)"; // Sets near the bottom of the screen.
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
		StringBuilder command = new StringBuilder("avplay -i " + inputFilename + " -vf ");
		
		int lastTenSeconds = lengthOfVideo - 10;
		
		boolean hasOpeningText = !openingText.equals("");
		boolean hasClosingText = !closingText.equals("");
		
		if (hasOpeningText && hasClosingText) {
			command.append("drawtext=\"fontfile=" + openingFont.getPath() + ": fontsize=" + openingFontSize + ": fontcolor=" + openingFontColor.toString() + ": x=" + openingX + ": y=" 
					+ openingY + ": text=\'" + openingText + "\': draw=\'lt(t,10)\':,drawtext=fontfile=" + closingFont.getPath() + ": fontsize=" + closingFontSize + 
					": fontcolor=" + closingFontColor.toString() + ": x=" +	closingX + ": y=" + closingY + ": text=\'" + closingText + "\': draw=\'gt(t," + lastTenSeconds + ")\'\"");
		} else if (hasOpeningText) {
			command.append("drawtext=\"fontfile=" + openingFont.getPath() + ": fontsize=" + openingFontSize + ": fontcolor=" + openingFontColor.toString() + ": x=" + openingX + 
					": y=" + openingY + ": text=\'" + openingText + "\': draw=\'lt(t,10)\'\"");
		} else {
			command.append("drawtext=\"fontfile=" + closingFont.getPath() + ": fontsize=" + closingFontSize + ": fontcolor=" + closingFontColor.toString() + ": x=" + closingX + 
					": y=" + closingY + ": text=\'" + closingText + "\': draw=\'gt(t," + lastTenSeconds + ")\'\"");
		}
		
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", command.toString());
		
		Process process = builder.start();
		process.waitFor();
		
		return null;
	}

}