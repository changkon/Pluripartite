package se206.a03;

import javax.swing.DefaultBoundedRangeModel;

/**
 * 
 * The BoundedRangeModel used as the JSlider model for the time bar. This model extends DefaultBoundedRangeModel and also includes a boolean variable 
 * which indicates when it should be active in triggering events.
 * 
 */

@SuppressWarnings("serial")
public class TimeBoundedRangeModel extends DefaultBoundedRangeModel {

	private boolean active = true;
	
	public void setActive(boolean value) {
		active = value;
	}
	
	public boolean getActive() {
		return active;
	}
	
	@Override
	protected void fireStateChanged() {
		super.fireStateChanged();
		active = true;
	}

}
