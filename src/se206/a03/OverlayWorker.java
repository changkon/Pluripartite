package se206.a03;

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

		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "avconv -i \"" + videoFileInput + "\" -i \"" + audioFileInput + "\" -map 0:v -map 0:a -map 1:a" +
				"-c:v copy -c:a copy -y \"" + videoFileOutput + "\"");
		builder.redirectErrorStream(true);

		Process process = builder.start();

		process.waitFor();

		return null;
	}

}
