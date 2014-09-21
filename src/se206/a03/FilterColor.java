package se206.a03;

import java.awt.Color;

public enum FilterColor {
	BLACK(Color.BLACK),
	RED(Color.RED),
	BLUE(Color.BLUE),
	GREEN(Color.GREEN),
	YELLOW(Color.YELLOW),
	ORANGE(Color.ORANGE);
	
	private Color color;
	
	private FilterColor(Color color) {
		this.color = color;
	}
	
	public Color getColor() {
		return color;
	}
}
