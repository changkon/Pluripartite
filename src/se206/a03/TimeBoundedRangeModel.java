package se206.a03;

import javax.swing.DefaultBoundedRangeModel;

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
