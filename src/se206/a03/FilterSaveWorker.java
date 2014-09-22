package se206.a03;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;

/**
 * 
 * Encodes filter options to file. Progress is shown on progress monitor.
 *
 */

public class FilterSaveWorker extends SwingWorker<Void, Integer> {
	private String inputFilename;
	private String outputFilename;
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
	private ProgressMonitor monitor;
	private int lengthOfVideo;
	
	public FilterSaveWorker(String inputFilename, String outputFilename, String openingText, String closingText, String openingX, String closingX, String openingY, String closingY, FilterFont openingFont, 
			FilterFont closingFont, int openingFontSize, int closingFontSize, FilterColor openingFontColor, FilterColor closingFontColor, ProgressMonitor monitor, int lengthOfVideo) {
		
		this.inputFilename = inputFilename;
		this.outputFilename = outputFilename;
		this.openingText = openingText;
		this.closingText = closingText;
		
		// Set openingX/openingY and closingX/closingY. If the value is empty, give it determined values.
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
		this.monitor = monitor;
		this.lengthOfVideo = lengthOfVideo;
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		StringBuilder command = new StringBuilder("avconv -i " + inputFilename + " -c:a copy -vf ");
		int filterLength = MediaSetting.getInstance().getOpeningClosingFilterLength();
		int lastSeconds = lengthOfVideo - filterLength;
		
		boolean hasOpeningText = !openingText.equals("");
		boolean hasClosingText = !closingText.equals("");
		
		if (hasOpeningText && hasClosingText) {
			command.append("drawtext=\"fontfile=" + openingFont.getPath() + ": fontsize=" + openingFontSize + ": fontcolor=" + openingFontColor.toString() + ": x=" + openingX + ": y=" 
					+ openingY + ": text=\'" + openingText + "\': draw=\'lt(t," + filterLength + ")\':,drawtext=fontfile=" + closingFont.getPath() + ": fontsize=" + closingFontSize + 
					": fontcolor=" + closingFontColor.toString() + ": x=" +	closingX + ": y=" + closingY + ": text=\'" + closingText + "\': draw=\'gt(t," + lastSeconds + ")\'\" "
					+ outputFilename);
		} else if (hasOpeningText) {
			command.append("drawtext=\"fontfile=" + openingFont.getPath() + ": fontsize=" + openingFontSize + ": fontcolor=" + openingFontColor.toString() + ": x=" + openingX + 
					": y=" + openingY + ": text=\'" + openingText + "\': draw=\'lt(t," + filterLength + ")\'\" " + outputFilename);
		} else {
			command.append("drawtext=\"fontfile=" + closingFont.getPath() + ": fontsize=" + closingFontSize + ": fontcolor=" + closingFontColor.toString() + ": x=" + closingX + 
					": y=" + closingY + ": text=\'" + closingText + "\': draw=\'gt(t," + lastSeconds + ")\'\" " + outputFilename);
		}
		
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", command.toString());
		builder.redirectErrorStream(true);
		Process process = builder.start();
		
		InputStream stdout = process.getInputStream();
		BufferedReader buffer = new BufferedReader(new InputStreamReader(stdout));
		
		System.out.println(command.toString());
		
		Pattern p = Pattern.compile("\\btime=\\b\\d+.\\d+");
		Matcher m;
		String line = "";
		
		while ((line = buffer.readLine()) != null) {
			
			if (monitor.isCanceled()) {
				process.destroy();
				this.cancel(true);
				break;
			}
			
			m = p.matcher(line);
			
			if (m.find()) {
				// greedy solution. We know if a string matches pattern, it must start with time=
				publish((int)Double.parseDouble(m.group().substring(5)));
			}
		}
		
		if (!isCancelled()) {
			process.waitFor();
		}
		
		return null;
	}

	@Override
	protected void process(List<Integer> chunks) {
		if (!isDone()) {
			for (Integer element : chunks) {
				String format = String.format("Completed : %2d%%", (int)(((double)element / lengthOfVideo) * 100));
				monitor.setNote(format);
				monitor.setProgress(element);
			}
		}
	}
	
	@Override
	protected void done() {
		try {
			monitor.close();
			get();
			JOptionPane.showMessageDialog(null, "Filtering has completed");
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		} catch (CancellationException e) {
			JOptionPane.showMessageDialog(null, "Filtering was interrupted");
		}
	}
	
}
