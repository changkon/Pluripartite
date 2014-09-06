package se206.a03;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;
import com.sun.jna.Native;

/**
 * Attempts to load the vlc native library (libvlc) using JNA. <br />
 * @see http://www.capricasoftware.co.uk/projects/vlcj/tutorial1.html
 *
 */

public class NativeLibrary {
	public static void main(String[] args) {
		Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
	}
}
