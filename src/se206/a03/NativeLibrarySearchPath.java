package se206.a03;
import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

/**
 * 
 * Path to vlc native library. Currently hard coded.
 * 
 * @see http://www.capricasoftware.co.uk/projects/vlcj/tutorial1.html
 *
 */

public class NativeLibrarySearchPath {
	public static void main(String[] args) {
		System.out.println(System.getProperty("os.name"));
		NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), "/home/linux/vlc/install/lib");
		Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
	}
	
//	public static String findPath() {
//		String os = System.getProperty("os.name");
//		File file;
//		if (os.equals("Linux")) {
//			file = new File("libvlc.so");
//		} else if (os.contains("Windows")) {
//			file = new File("vlc.dll");
//		}
//		return "";
//	}
}
