/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.observer;

public class ToggleObjectCoordinates extends ObserverEvalObject implements
		Cloneable {

	private String bcontrol;
	private String x;
	private String y;

	// private Boolean animate;

	public ToggleObjectCoordinates() {
		super();
		this.x = "";
		this.y = "";
		// this.animate = false;
	}

	public ToggleObjectCoordinates(String type, String bcontrol, String x,
			String y, String eval) {
		super(type, eval);
		this.bcontrol = bcontrol;
		this.x = x;
		this.y = y;
		// this.animate = animate;
	}

	public ToggleObjectCoordinates(String type, String x, String y,
 String eval) {
		this(type, null, x, y, eval);
	}

	public String getBcontrol() {
		return bcontrol;
	}

	public String getX() {
		return x;
	}

	public String getY() {
		return y;
	}

	// public Boolean getAnimate() {
	// return animate;
	// }

	public void setBcontrol(String bcontrol) {
		Object oldValue = this.bcontrol;
		this.bcontrol = bcontrol;
		firePropertyChange("bcontrol", oldValue, this.bcontrol);
	}

	public void setX(String x) {
		Object oldValue = this.x;
		this.x = x;
		firePropertyChange("x", oldValue, this.x);
	}

	public void setY(String y) {
		Object oldValue = this.y;
		this.y = y;
		firePropertyChange("y", oldValue, this.y);
	}

	// public void setAnimate(Boolean animate) {
	// Object oldValue = this.animate;
	// this.animate = animate;
	// firePropertyChange("animate", oldValue, this.animate);
	// }

	@Override
	public ToggleObjectCoordinates clone() throws CloneNotSupportedException {
		return (ToggleObjectCoordinates) super.clone();
	}

}
