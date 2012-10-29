package acs.tabbychat;

public class PrefsToggleButton extends PrefsButton {
	protected int onColor = 0x66a5e7e4;
	protected int offColor = 0x99000000;
	protected String onTitle = "On";
	protected String offTitle = "Off";
	
	public PrefsToggleButton() {
		super(9999, 0, 0, 1, 1, "");
	}
	
	public PrefsToggleButton(int _id, int _x, int _y, int _w, int _h) {
		super(_id, _x, _y, _w, _h, "Off");
		this.bgcolor = this.offColor;
		this.displayString = this.offTitle;
	}
	
	public boolean state() {
		return (this.displayString.equalsIgnoreCase(this.onTitle));
	}
	
	public void toggle() {
		if (this.displayString.equalsIgnoreCase(this.offTitle)) {
			this.displayString = onTitle;
			this.bgcolor = onColor;
		} else {
			this.displayString = offTitle;
			this.bgcolor = offColor;
		}
	}
	
	public void updateTo(boolean theVar) {
		if (theVar) {
			this.displayString = onTitle;
			this.bgcolor = onColor;
		} else {
			this.displayString = offTitle;
			this.bgcolor = offColor;
		}
	}
}
