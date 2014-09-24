package se206.a03;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

public class OverlayWorker extends SwingWorker<Void, Void> {
	private String videoFileInput;
	private String audioFileInput;
	private String videoFileOutput;


	public OverlayWorker(String videoFileInput, String audioFileInput, String videoFileOutput) {
		this.videoFileInput = videoFileInput;
		this.audioFileInput = audioFileInput;
		this.videoFileOutput = videoFileOutput;
	}


	@Override
	protected Void doInBackground() throws Exception {

		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "avconv -i \"" + videoFileInput + "\" -i \"" + audioFileInput + "\" -filter_complex" +
				" amix=inputs=2 -c:v copy -strict experimental -y \"" + videoFileOutput + "\"");
		builder.redirectErrorStream(true);

		Process process = builder.start();

		process.waitFor();

		return null;
	}


	@Override
	protected void done() {
		JOptionPane.showMessageDialog(null, "Overlay complete");
	}
}
