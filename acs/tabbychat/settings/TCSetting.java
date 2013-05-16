package acs.tabbychat.settings;

import org.lwjgl.opengl.GL11;
import net.minecraft.client.Minecraft;
import net.minecraft.src.FontRenderer;
import net.minecraft.src.GuiButton;

public class TCSetting extends GuiButton {
	public int buttonColor = 0xbba5e7e4;
	private int buttonOffColor = 0x99000000;
	protected int labelX;
	public String description;
	public String type;
	protected volatile Object value;
	protected Object tempValue;
	private Object theDefault;
	protected static Minecraft mc;
		
	public TCSetting(int theID, int theX, int theY, String theLabel) {
		super(theID, theX, theY, theLabel);
	}
	
	public TCSetting(String theLabel, int theID) {
		super(theID, 0, 0, "");
	}
		
	public void actionPerformed() { }
	
	public void clear() {
		this.value = this.theDefault;
		this.tempValue = this.theDefault;
	}
	
	public void disable() {
		this.enabled = false;
	}
	
	public void drawButton(Minecraft mc, int cursorX, int cursorY) { }
	
	public void enable() {
		this.enabled = true;
	}
		
	protected Object getTempValue() {
		return this.tempValue;
	}
		
	protected Object getValue() {
		return this.value;
	}
		
	public Boolean hovered(int cursorX, int cursorY) {
		return cursorX >= this.xPosition && cursorY >= this.yPosition && cursorX < this.xPosition + this.width && cursorY < this.yPosition + this.height;
	}
		
	public void mouseClicked(int par1, int par2, int par3) { }
		
	public void reset() {
	}
		
	public void save() {
	}
		
	public void setButtonDims(int wide, int tall) {
		this.width = wide;
		this.height = tall;
	}
		
	public void setButtonLoc(int bx, int by) {
		this.xPosition = bx;
		this.yPosition = by;
	}
	
	public void setLabelLoc(int _x) {
		this.labelX = _x;
	}
			
	protected void setTempValue(Object updateVal) {
		this.tempValue = updateVal;
	}
	
	protected void setCleanValue(Object updateVal) {}
	
	protected void setValue(Object updateVal) {
		this.value = updateVal;
	}

}
