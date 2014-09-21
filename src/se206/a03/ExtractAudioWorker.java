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

public class ExtractAudioWorker extends SwingWorker<Void, Integer> {
	private String inputFile;
	private String outputFile;
	private int lengthOfVideo;
	private ProgressMonitor monitor;
	
	public ExtractAudioWorker(String inputFile, String outputFile, int lengthOfVideo, ProgressMonitor monitor) {
		this.inputFile = inputFile;
		this.outputFile = outputFile;
		this.lengthOfVideo = lengthOfVideo;
		this.monitor = monitor;
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "avconv -i " + inputFile + " -vn -y " + outputFile);
		builder.redirectErrorStream(true);
		
		Process process = builder.start();
		
		InputStream stdout = process.getInputStream();
		BufferedReader buffer = new BufferedReader(new InputStreamReader(stdout));
		
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
			JOptionPane.showMessageDialog(null, "Extraction complete");
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		} catch (CancellationException e) {
			JOptionPane.showMessageDialog(null, "Extraction was interrupted");
		}
	}
	
}
