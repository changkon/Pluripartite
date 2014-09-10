package se206.a03;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Manages the icons used in the media player.
 * 
 * @see http://www.flaticon.com/packs/computer-and-media-1
 *
 */

public class MediaIcon {
	public enum DisplayIcon {
		PLAY(path + "/res/play.png"),
		PAUSE(path + "/res/pause.png"),
		STOP(path + "/res/stop.png"),
		FASTFORWARD(path + "/res/fastforward.png"),
		REWIND(path + "/res/rewind.png"),
		MUTE(path + "/res/mute.png"),
		UNMUTE(path + "/res/unmute.png"),
		MAXVOLUME(path + "/res/maxvolume.png"),
		OPEN(path + "/res/open.png");
		
		private ImageIcon i;
		
		private DisplayIcon(String file) {
			i = new ImageIcon(file);
		}
		
		public ImageIcon getValue() {
			return i;
		}
	}
	
	private static final int pixel = 20;
	private static String path = System.getProperty("user.dir");
	
	public static Icon getIcon(DisplayIcon i) {
		ImageIcon icon = i.getValue();
		BufferedImage bi = resizeImage(icon);
		icon.setImage(bi);
		return icon;
	}
	
	/**
	 * Resizes a given image to desired size
	 * 
	 * @param icon
	 * @param width
	 * @param height
	 * @return BufferedImage
	 */
	private static BufferedImage resizeImage(ImageIcon icon) {
		BufferedImage bi = new BufferedImage(pixel, pixel, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi.createGraphics();
		
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g.drawImage(icon.getImage(), 0, 0, pixel, pixel, null);
	    g.dispose();
	    
		return bi;
	}
}
